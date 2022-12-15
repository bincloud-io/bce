package cloud.bangover.validation.context;

/**
 * This interface declares the contract for object validation.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public interface Validatable {
  /**
   * Validate object state inside the context.
   *
   * @param context The validation context
   * @return The derived context with applied validations
   */
  public ValidationContext validate(ValidationContext context);
}