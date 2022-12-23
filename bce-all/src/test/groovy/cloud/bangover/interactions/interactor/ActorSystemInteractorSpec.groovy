package cloud.bangover.interactions.interactor

import cloud.bangover.actor.Actor
import cloud.bangover.actor.ActorAddress
import cloud.bangover.actor.ActorName
import cloud.bangover.actor.ActorSystem
import cloud.bangover.actor.Actors
import cloud.bangover.actor.CorrelationKey
import cloud.bangover.actor.FixedMessagesWaitingDispatcher
import cloud.bangover.actor.Message
import cloud.bangover.actor.Actors.SystemConfigurer
import cloud.bangover.actor.EventLoop.Dispatcher
import cloud.bangover.async.promises.Promise.ErrorHandler
import cloud.bangover.async.promises.Promise.ResponseHandler
import cloud.bangover.async.timer.Timeout
import cloud.bangover.async.timer.TimeoutException
import cloud.bangover.interactions.interactor.ActorSystemInteractor
import cloud.bangover.interactions.interactor.Interactor
import cloud.bangover.interactions.interactor.TargetAddress
import cloud.bangover.interactions.interactor.Interactor.Factory
import cloud.bangover.interactions.interactor.Interactor.WrongRequestTypeException
import cloud.bangover.interactions.interactor.Interactor.WrongResponseTypeException
import com.github.jknack.handlebars.Context
import java.util.concurrent.CountDownLatch
import lombok.NonNull
import spock.lang.Specification

class ActorSystemInteractorSpec extends Specification {
  private static final Timeout LONG_TIMEOUT = Timeout.ofSeconds(10L)
  private static final Timeout SHORT_TIMEOUT = Timeout.ofMilliseconds(10L)
  private static final ActorName PARSER_ACTOR_NAME = ActorName.wrap("LONG_PARSER")
  private static final CorrelationKey GENERATED_KEY = CorrelationKey.wrap("CORRELATED_KEY")

  def "Scenario: complete iteraction successfully"() {
    given: "The actor system"
    CountDownLatch latch = new CountDownLatch(1)
    FixedMessagesWaitingDispatcher dispatcher = FixedMessagesWaitingDispatcher.singleThreadDispatcher(3)
    ActorSystem actorSystem = createActorSystem(dispatcher)
    actorSystem.start()

    and: "The existing target actor"
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, {Context context -> new LongParserActor(context)})

    and: "The actors interactor initialized correctly"
    Interactor<String, Long> interactor = createInteractor(actorSystem, actorAddress, String, Long, LONG_TIMEOUT)

    and: "The result listener"
    ResponseHandler<Long> resultListener = Mock(ResponseHandler)

    when: "The correct argument is received"
    interactor.invoke("100")
        .then({response ->
          resultListener.onResponse(response)
          latch.countDown()
        })
    dispatcher.getWaiter().await()
    latch.await()


    then: "The promise should be resolved with expected respone"
    1 * resultListener.onResponse(100L)

    cleanup:
    actorSystem.shutdown()
  }

  def "Scenario: complete interaction with timeout error"() {
    Throwable rejectError
    given: "The actor system"
    CountDownLatch latch = new CountDownLatch(1)
    FixedMessagesWaitingDispatcher dispatcher = FixedMessagesWaitingDispatcher.singleThreadDispatcher(2)
    ActorSystem actorSystem = createActorSystem(dispatcher)
    actorSystem.start()

    and: "The existing target actor"
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, {Context context -> new LongParserActor(context)})

    and: "The actors interactor initialized by too short waiting timeout"
    Interactor<String, Long> interactor = createInteractor(actorSystem, actorAddress, String, Long, SHORT_TIMEOUT)

    and: "The error listener"
    ErrorHandler<Throwable> errorListener = Mock(ErrorHandler)

    when: "The correct argument is received"
    interactor.invoke("100")
        .error({ error ->
          errorListener.onError(error)
          latch.countDown()
        })
    dispatcher.getWaiter().await()
    latch.await()

    then: "The promise should be rejected with timeout exception should be received"
    1 * errorListener.onError(_) >> {rejectError = it[0]}
    rejectError instanceof TimeoutException

    cleanup:
    actorSystem.shutdown()
  }

  def "Scenario: complete interaction with wrong request type error"() {
    Throwable rejectError
    given: "The actor system"
    CountDownLatch latch = new CountDownLatch(1)
    FixedMessagesWaitingDispatcher dispatcher = FixedMessagesWaitingDispatcher.singleThreadDispatcher(1)
    ActorSystem actorSystem = createActorSystem(dispatcher)
    actorSystem.start()

    and: "The existing target actor"
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, {Context context -> new LongParserActor(context)})

    and: "The actors interactor initialized correctly"
    Interactor<String, Long> interactor = createInteractor(actorSystem, actorAddress, String, Long, LONG_TIMEOUT)

    and: "The error listener"
    ErrorHandler<Throwable> errorListener = Mock(ErrorHandler)

    when: "The wrong request is passed"
    interactor.invoke(100L)
        .error({ error ->
          errorListener.onError(error)
          latch.countDown()
        })
    dispatcher.getWaiter().await()
    latch.await()

    then: "The promise should be rejected with timeout exception should be received"
    1 * errorListener.onError(_) >> {rejectError = it[0]}
    rejectError instanceof WrongRequestTypeException

    cleanup:
    actorSystem.shutdown()
  }


  def "Scenario: complete interaction with wrong response type error"() {
    Throwable rejectError
    given: "The actor system"
    CountDownLatch latch = new CountDownLatch(1)
    FixedMessagesWaitingDispatcher dispatcher = FixedMessagesWaitingDispatcher.singleThreadDispatcher(1)
    ActorSystem actorSystem = createActorSystem(dispatcher)
    actorSystem.start()

    and: "The existing target actor"
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, {Context context -> new LongParserActor(context)})

    and: "The actors interactor initialized by wrong response type"
    Interactor<String, String> interactor = createInteractor(actorSystem, actorAddress, String, String, LONG_TIMEOUT)

    and: "The error listener"
    ErrorHandler<Throwable> errorListener = Mock(ErrorHandler)

    when: "The correct argument is received"
    interactor.invoke("100")
        .error({ error ->
          errorListener.onError(error)
          latch.countDown()
        })
    dispatcher.getWaiter().await()
    latch.await()

    then: "The promise should be rejected with timeout exception should be received"
    1 * errorListener.onError(_) >> {rejectError = it[0]}
    rejectError instanceof WrongResponseTypeException

    cleanup:
    actorSystem.shutdown()
  }

  private ActorSystem createActorSystem(Dispatcher dispatcher) {
    return Actors.create({SystemConfigurer configurer ->
      return configurer
          .withDispatcher(dispatcher)
          .withCorrelationKeyGenerator({GENERATED_KEY})
          .configure()
    })
  }

  private <Q, S>Interactor<Q, S> createInteractor(
      ActorSystem actorSystem, ActorAddress targetActor, Class<Q> requestType, Class<S> responseType, Timeout timeout) {
    Factory interactorFactory = ActorSystemInteractor.factory(actorSystem)
    TargetAddress targetAddress = TargetAddress.ofUrn(targetActor.toString())
    return interactorFactory.createInteractor(targetAddress, requestType, responseType, timeout)
  }

  private static class LongParserActor extends Actor<String> {
    public LongParserActor(@NonNull Context context) {
      super(context);
    }

    @Override
    protected void receive(Message<String> message) {
      message.whenIsMatchedTo(String, { value ->
        System.sleep(100)
        tell(message.replyWith(Long.parseLong(value)))
      })
    }
  }
}
