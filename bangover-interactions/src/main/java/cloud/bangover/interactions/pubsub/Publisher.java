package cloud.bangover.interactions.pubsub;

/**
 * This interface describes the contract for event publishing.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <M> The message type name
 */
public interface Publisher<M> {
  /**
   * Publish the message to the topic.
   *
   * @param message The message object
   */
  public void publish(M message);
}