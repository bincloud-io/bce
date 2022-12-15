package cloud.bangover.validation;

import java.util.Collection;

/**
 * This interface declares the contract for single validation rule check.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The validatable value type
 */
public interface Rule<T> {
  /**
   * Check that the value is acceptable for checking by the rule.
   *
   * @param value The validatable value
   * @return True if is acceptable and false otherwise
   */
  public boolean isAcceptableFor(T value);

  /**
   * Perform rule check.
   *
   * @param value The validatable value
   * @return Collection of validation error messages
   */
  public Collection<ErrorMessage> check(T value);

  /**
   * Invert the rule.
   *
   * @return The inverted validation rule
   */
  public Rule<T> invert();
}