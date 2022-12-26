package cloud.bangover.validation;

import cloud.bangover.validation.ValidationExecutor.ValidationReport;
import java.util.Collection;

/**
 * This class executes validation case for ungrouped validations.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class UngroupedValidationCase extends ValidationCase {
  public UngroupedValidationCase(Object validatable, ExpectedResult expectedResult,
      Collection<String> expectedMessages) {
    super(validatable, expectedResult, expectedMessages);
  }

  @Override
  protected boolean containsExpectedErrorMessages(ValidationReport validationReport) {
    return validationReport.containsUngroupedErrors(getExpectedErrorMessages());
  }
}
