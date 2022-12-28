package cloud.bangover.interactions.pubsub;

/**
 * This interface describes the contract for event listening.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <M> The message type name
 */
public interface Subscriber<M> {
  /**
   * React on the message.
   *
   * @param message The message object
   */
  public void onMessage(M message);
}