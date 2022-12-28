package cloud.bangover.validation.context;

/**
 * This interface declares the rule of the value providing.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The type of value
 */
public interface ValueProvider<T> {
  /**
   * Get the value.
   *
   * @return The value
   */
  public T getValue();
}