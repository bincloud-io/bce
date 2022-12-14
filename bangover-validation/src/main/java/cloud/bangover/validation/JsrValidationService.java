package cloud.bangover.validation;

import java.util.Optional;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsrValidationService implements ValidationService {
  private final Validator validator;

  @Override
  public <V> ValidationState validate(V validatable) {
    ValidationState result = new ValidationState();
    for (ConstraintViolation<V> violation : validator.validate(validatable)) {
      result = Violation.from(violation.getPropertyPath(), violation.getConstraintDescriptor())
          .expand(result);
    }
    return result;
  }

  @RequiredArgsConstructor(staticName = "from")
  private static class Violation {
    private final Path propertyPath;
    private final ConstraintDescriptor<?> constraintDescriptor;

    public ValidationState expand(ValidationState validationState) {
      return this.getPath().map(
          path -> validationState.withGrouped(ValidationGroup.createFor(path), getErrorMessage()))
          .orElseGet(() -> validationState.withUngrouped(getErrorMessage()));
    }

    private Optional<String> getPath() {
      return Optional.ofNullable(propertyPath).map(Path::toString)
          .filter(token -> !token.isEmpty());
    }

    private ErrorMessage getErrorMessage() {
      return ErrorMessage.createFor(constraintDescriptor.getMessageTemplate(),
          constraintDescriptor.getAttributes());
    }
  }
}
