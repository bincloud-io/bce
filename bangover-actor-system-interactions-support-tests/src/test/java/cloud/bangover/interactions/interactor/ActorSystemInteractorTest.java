package cloud.bangover.interactions.interactor;

import cloud.bangover.actors.Actor;
import cloud.bangover.actors.Actor.Context;
import cloud.bangover.actors.ActorAddress;
import cloud.bangover.actors.ActorName;
import cloud.bangover.actors.ActorSystem;
import cloud.bangover.actors.Actors;
import cloud.bangover.actors.Actors.SystemConfiguration;
import cloud.bangover.actors.Actors.SystemConfigurer;
import cloud.bangover.actors.Actors.SystemInitializer;
import cloud.bangover.actors.CorrelationKey;
import cloud.bangover.actors.EventLoop.Dispatcher;
import cloud.bangover.actors.FixedMessagesWaitingDispatcher;
import cloud.bangover.actors.Message;
import cloud.bangover.async.promises.MockErrorHandler;
import cloud.bangover.async.promises.MockResponseHandler;
import cloud.bangover.async.promises.WaitingPromise;
import cloud.bangover.async.timer.Timeout;
import cloud.bangover.async.timer.TimeoutException;
import cloud.bangover.async.timer.Timer;
import cloud.bangover.generators.StubGenerator;
import cloud.bangover.interactions.interactor.Interactor.Factory;
import cloud.bangover.interactions.interactor.Interactor.WrongRequestTypeException;
import cloud.bangover.interactions.interactor.Interactor.WrongResponseTypeException;
import lombok.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ActorSystemInteractorTest {
  private static final Timeout LONG_TIMEOUT = Timeout.ofSeconds(10L);
  private static final Timeout SHORT_TIMEOUT = Timeout.ofMilliseconds(10L);
  private static final ActorName PARSER_ACTOR_NAME = ActorName.wrap("LONG_PARSER");
  private static final CorrelationKey GENERATED_KEY = CorrelationKey.wrap("CORRELATED_KEY");

  @Test
  public void shouldIterationBeCompletedSuccessfully() {
    // Given
    FixedMessagesWaitingDispatcher dispatcher =
        FixedMessagesWaitingDispatcher.singleThreadDispatcher(3);
    ActorSystem actorSystem = createActorSystem(dispatcher);
    actorSystem.start();
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, new Actor.Factory<String>() {
      @Override
      public Actor<String> createActor(Context context) {
        return new LongParserActor(context);
      }
    });
    Interactor<String, Long> interactor =
        createInteractor(actorSystem, actorAddress, String.class, Long.class, LONG_TIMEOUT);
    MockResponseHandler<Long> resultListener = new MockResponseHandler<Long>();

    // When
    WaitingPromise.of(interactor.invoke("100")).then(resultListener).await();
    actorSystem.shutdown();

    // Then
    resultListener.getHistory().hasEntry(0, 100L);
  }

  @Test
  public void shouldCompleteInteractionWithTimeoutError() {
    // Given
    FixedMessagesWaitingDispatcher dispatcher =
        FixedMessagesWaitingDispatcher.singleThreadDispatcher(2);
    ActorSystem actorSystem = createActorSystem(dispatcher);
    actorSystem.start();
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, new Actor.Factory<String>() {
      @Override
      public Actor<String> createActor(Context context) {
        return new LongParserActor(context);
      }
    });
    Interactor<String, Long> interactor =
        createInteractor(actorSystem, actorAddress, String.class, Long.class, SHORT_TIMEOUT);
    MockErrorHandler<Throwable> errorListener = new MockErrorHandler<Throwable>();
    // When
    WaitingPromise.of(interactor.invoke("100")).error(errorListener).await();
    actorSystem.shutdown();
    // Then
    Assert.assertTrue(errorListener.getHistory().getEntry(0) instanceof TimeoutException);
  }

  @Test
  public void shouldCompleteIterationWithWrongRequestTypeError() {
    // Given
    FixedMessagesWaitingDispatcher dispatcher =
        FixedMessagesWaitingDispatcher.singleThreadDispatcher(1);
    ActorSystem actorSystem = createActorSystem(dispatcher);
    actorSystem.start();
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, new Actor.Factory<String>() {
      @Override
      public Actor<String> createActor(Context context) {
        return new LongParserActor(context);
      }
    });
    @SuppressWarnings("unchecked")
    Interactor<Long, Long> interactor =
        (Interactor<Long, Long>) ((Object) createInteractor(actorSystem, actorAddress, String.class,
            Long.class, LONG_TIMEOUT));
    MockErrorHandler<Throwable> errorListener = new MockErrorHandler<Throwable>();

    // When
    WaitingPromise.of(interactor.invoke(100L)).error(errorListener).await();
    actorSystem.shutdown();

    // Then
    Assert.assertTrue(errorListener.getHistory().getEntry(0) instanceof WrongRequestTypeException);
  }

  @Test
  public void shouldCompleteIterationWithWrongResponseTypeError() {
    // Given
    FixedMessagesWaitingDispatcher dispatcher =
        FixedMessagesWaitingDispatcher.singleThreadDispatcher(1);
    ActorSystem actorSystem = createActorSystem(dispatcher);
    actorSystem.start();
    ActorAddress actorAddress = actorSystem.actorOf(PARSER_ACTOR_NAME, new Actor.Factory<String>() {
      @Override
      public Actor<String> createActor(Context context) {
        return new LongParserActor(context);
      }
    });
    Interactor<String, String> interactor =
        createInteractor(actorSystem, actorAddress, String.class, String.class, LONG_TIMEOUT);
    MockErrorHandler<Throwable> errorListener = new MockErrorHandler<Throwable>();

    // When
    WaitingPromise.of(interactor.invoke("100")).error(errorListener).await();
    actorSystem.shutdown();

    // Then
    Assert.assertTrue(errorListener.getHistory().getEntry(0) instanceof WrongResponseTypeException);
  }

  private ActorSystem createActorSystem(Dispatcher dispatcher) {
    return Actors.create(new SystemInitializer() {
      @Override
      public SystemConfiguration initializeSystem(SystemConfigurer configurer) {
        return configurer.withDispatcher(dispatcher)
            .withCorrelationKeyGenerator(new StubGenerator<CorrelationKey>(GENERATED_KEY))
            .configure();
      }
    });
  }

  private <Q, S> Interactor<Q, S> createInteractor(ActorSystem actorSystem,
      ActorAddress targetActor, Class<Q> requestType, Class<S> responseType, Timeout timeout) {
    Factory interactorFactory = ActorSystemInteractor.factory(actorSystem);
    TargetAddress targetAddress = TargetAddress.ofUrn(targetActor.toString());
    return interactorFactory.createInteractor(targetAddress, requestType, responseType, timeout);
  }

  private static class LongParserActor extends Actor<String> {
    public LongParserActor(@NonNull Context context) {
      super(context);
    }

    @Override
    protected void receive(Message<String> message) {
      message.whenIsMatchedTo(String.class, new Message.MessageHandleFunction<String>() {
        @Override
        public void receive(String value) {
          Timer.sleep(100L);
          tell(message.replyWith(Long.parseLong(value)));
        }
      });
    }
  }
}
