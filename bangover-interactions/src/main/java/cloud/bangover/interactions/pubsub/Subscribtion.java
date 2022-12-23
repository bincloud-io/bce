package cloud.bangover.interactions.pubsub;

/**
 * This interface describes the contract of subscribtion handling.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public interface Subscribtion {
  /**
   * Unsubscribe from the subscription.
   */
  public void unsubscribe();
}