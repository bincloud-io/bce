package io.bce.actor;

import io.bce.actor.Actor.Factory;

public interface ActorSystem {

  /**
   * Tell about message to the actor system
   * 
   * @param <T> The message body type
   * @param message The message
   * @return The actual message correlation key
   */
  public <T> CorrelationKey tell(Message<T> message);

  /**
   * Create the actor instance in the actor system
   * 
   * @param <T> The actor message body type name
   * @param actorName The actor name
   * @param factory The actor factory
   * @return The actor reference
   */
  public <T> ActorAddress actorOf(ActorName actorName, Factory<T> factory);

  /**
   * Start the actor system
   */
  public void start();

  /**
   * Shutdown the actor system
   */
  public void shutdown();
}
