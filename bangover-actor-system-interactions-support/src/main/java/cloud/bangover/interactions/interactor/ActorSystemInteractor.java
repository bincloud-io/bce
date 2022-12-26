package cloud.bangover.interactions.interactor;

import cloud.bangover.actors.Actor;
import cloud.bangover.actors.ActorAddress;
import cloud.bangover.actors.ActorName;
import cloud.bangover.actors.ActorSystem;
import cloud.bangover.actors.CorrelationKey;
import cloud.bangover.actors.Message;
import cloud.bangover.async.promise.AsyncResolverProxy;
import cloud.bangover.async.promises.Deferred;
import cloud.bangover.async.promises.Promise;
import cloud.bangover.async.promises.Promises;
import cloud.bangover.async.timer.Timeout;
import cloud.bangover.async.timer.TimeoutException;
import cloud.bangover.async.timer.TimeoutSupervisor;
import cloud.bangover.async.timer.Timer;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class is the intractor implementation for communicating with the random actor, registered in
 * the actor system. It will register intermediate actor into the actor system and communicate with
 * another actors via messages {@link Message}. Requests and responses is correlated using
 * correlation key.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <Q> The request type name
 * @param <S> The response type name
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActorSystemInteractor<Q, S> implements Interactor<Q, S> {
  private final ActorSystem actorSystem;
  private final Class<Q> requestType;
  private final Class<S> repsponseType;
  private final TargetAddress target;
  private final Timeout timeout;

  /**
   * Get the actor system interactor factory.
   *
   * @param actorSystem The actor system
   * @return The interactor factory
   */
  public static final Factory factory(@NonNull ActorSystem actorSystem) {
    return new ActorInteractorFactory(actorSystem);
  }

  @Override
  public final Promise<S> invoke(Q request) {
    return Promises.of(resolver -> {
      ActorAddress interactorActor =
          actorSystem.actorOf(generateActorName(), context -> new InteractorActor(context,
              createTargetAddress(target), timeout, new AsyncResolverProxy<>(resolver)));
      actorSystem.tell(Message.createFor(interactorActor, request));
    });
  }

  private ActorAddress createTargetAddress(TargetAddress target) {
    return ActorAddress.ofUrn(target.toString());
  }

  private ActorName generateActorName() {
    return ActorName.wrap(String.format("INTERACTION--%s", UUID.randomUUID()));
  }

  @RequiredArgsConstructor
  private static class ActorInteractorFactory implements Factory {
    @NonNull
    private final ActorSystem actorSystem;

    @Override
    public <Q, S> Interactor<Q, S> createInteractor(TargetAddress target, Class<Q> requestType,
        Class<S> responseType, Timeout timeout) {
      return new ActorSystemInteractor<Q, S>(actorSystem, requestType, responseType, target,
          timeout);
    }
  }

  private class InteractorActor extends Actor<Object> {
    private final TimeoutSupervisor timeoutSupervisor;
    private final ActorAddress targetAddress;
    private CorrelationKey correlationKey;
    private final Deferred<S> resolver;
    private Stage stage;

    public InteractorActor(@NonNull Context context, ActorAddress targetAddress, Timeout timeout,
        Deferred<S> resolver) {
      super(context);
      this.timeoutSupervisor = Timer.supervisor(timeout,
          () -> tell(Message.createFor(self(), self(), new TimeoutException(timeout))));
      this.correlationKey = CorrelationKey.UNCORRELATED;
      this.targetAddress = targetAddress;
      this.stage = Stage.REQUEST_WAITING;
      this.resolver = resolver;
    }

    @Override
    protected void receive(Message<Object> message) throws Throwable {
      rethrowIfErrorReceived(message.getBody());
      message.whenIsMatchedTo(body -> stage.isRequestWaitingStage(),
          body -> processRequest(message), v -> {
            message.whenIsMatchedTo(body -> stage.isResponseWaitingStage(),
                body -> processResponse(message));
          });
    }

    @Override
    protected FaultResolver<Object> getFaultResover() {
      return new FaultResolver<Object>() {
        @Override
        public void resolveError(LifecycleController lifecycle, Message<Object> message,
            Throwable error) {
          resolver.reject(error);
          lifecycle.stop();
        }
      };
    }

    private void rethrowIfErrorReceived(Object body) throws Throwable {
      if (body instanceof Throwable) {
        throw (Throwable) body;
      }
    }

    private void processRequest(Message<Object> requestMessage) {
      requestMessage.whenIsMatchedTo(requestType, requestBody -> {
        handleRequest(requestMessage.map(v -> requestBody));
      }, requestBody -> {
        throw new WrongRequestTypeException(repsponseType, requestBody);
      });
    }

    private void processResponse(Message<Object> responseMessage) {
      responseMessage.whenIsMatchedTo(repsponseType, responseBody -> {
        handleResponse(responseMessage.map(v -> responseBody));
        completeInteraction();
      }, responseBody -> {
        throw new WrongResponseTypeException(repsponseType, responseBody);
      });
    }

    private void handleRequest(Message<Q> requestMessage) {
      this.stage = Stage.RESPONSE_WAITING;
      this.correlationKey = requestMessage.getCorrelationKey();
      timeoutSupervisor.startSupervision();
      tell(requestMessage.withDestination(targetAddress).withSender(self()));
    }

    private void handleResponse(Message<S> responseMessage) {
      responseMessage.whenIsMatchedTo(v -> isResponseCorrelatedToRequest(responseMessage),
          responseBody -> {
            timeoutSupervisor.stopSupervision();
            resolver.resolve(responseBody);
          });
    }

    private void completeInteraction() {
      stop(self());
    }

    private boolean isResponseCorrelatedToRequest(Message<S> responseMessage) {
      return responseMessage.getCorrelationKey().equals(correlationKey);
    }
  }

  private enum Stage {
    REQUEST_WAITING,
    RESPONSE_WAITING;

    public boolean isRequestWaitingStage() {
      return this == REQUEST_WAITING;
    }

    public boolean isResponseWaitingStage() {
      return this == RESPONSE_WAITING;
    }
  }
}
