package cloud.bangover.validation;

import cloud.bangover.validation.ValidationContext.Validatable;
import cloud.bangover.validation.ValidationExecutor.ValidationReport;
import java.util.Collection;

/**
 * This class executes validation case for grouped validations.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class GroupedValidationCase extends ValidationCase {
  private final String group;

  public GroupedValidationCase(Validatable validatable, String group, ExpectedResult expectedResult,
      Collection<String> expectedMessages) {
    super(validatable, expectedResult, expectedMessages);
    this.group = group;
  }

  @Override
  public boolean containsExpectedErrorMessages(ValidationReport validationReport) {
    return validationReport.containsGroupedErrors(group, getExpectedErrorMessages());
  }
}
