package cloud.bangover.validation.jsr

import cloud.bangover.validation.ErrorMessage
import cloud.bangover.validation.ValidationService
import cloud.bangover.validation.ValidationState
import cloud.bangover.validation.ValidationState.ErrorState
import cloud.bangover.validation.ValidationState.GroupedError
import cloud.bangover.validation.context.ValidationContextSpec.RootEntity
import cloud.bangover.validation.jsr.JsrValidationService
import java.util.stream.Collectors
import javax.validation.Valid
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import spock.lang.Specification

class JsrValidationServiceSpec extends Specification {
  def "Scenario: validate object using bean validation"() {
    ValidatorFactory validatorFactory;

    given:
    validatorFactory = Validation.byDefaultProvider().configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory()
    ValidationService validationService = new JsrValidationService(validatorFactory.getValidator());

    and: "The validatable object"
    RootObject validatable = new RootObject();

    when: "The validation is executed"
    ValidationState validationState = validationService.validate(validatable)
    ErrorState errorState = validationState.getErrorState();
    List<ErrorMessage> ungroupedErrors = new ArrayList();
    Map<String, GroupedError> errors = errorState.getGroupedErrors().stream()
        .collect(Collectors.toMap({it.getGroupName()}, {it}));

    then: "The object should be invalid"
    validationState.isValid() == false

    and: "The validation state should contain ungrouped error message should contain message with passed property"
    ErrorMessage ungroupedErrorMessage = errorState.getUngroupedErrors().iterator().next();
    ungroupedErrorMessage.getMessage() == "Ungrouped error"
    ungroupedErrorMessage.getParameters().get("passedProperty") == "Ungrouped value"

    and: "The validation state should contain errors with groups: " +
    "setInnerObjects[].value, listInnerObjects[0].value, listInnerObjects[1].value and mapInnerObjects[object].value"
    errors.keySet() == new HashSet([
      "setInnerObjects[].value",
      "listInnerObjects[0].value",
      "listInnerObjects[1].value",
      "mapInnerObjects[object].value",
    ])

    and: "The grouped error \"setInnerObjects[].value\" should contain two messages with passed properties (because of set members errors concatination)"
    ErrorMessage firstSetInnerObjectsGroupedErrorMessage = errors.get("setInnerObjects[].value").getMessages().get(0)
    firstSetInnerObjectsGroupedErrorMessage.getMessage() == "Grouped error"
    firstSetInnerObjectsGroupedErrorMessage.getParameters().get("passedProperty") == "Grouped value"
    
    ErrorMessage secondSetInnerObjectsGroupedErrorMessage = errors.get("setInnerObjects[].value").getMessages().get(0)
    secondSetInnerObjectsGroupedErrorMessage.getMessage() == "Grouped error"
    secondSetInnerObjectsGroupedErrorMessage.getParameters().get("passedProperty") == "Grouped value"

    and: "The grouped error \"listInnerObjects[0].value\" should contain message with passed property"
    ErrorMessage firstListGroupErrorMessage = errors.get("listInnerObjects[0].value").getMessages().get(0)
    firstListGroupErrorMessage.getMessage() == "Grouped error"
    firstListGroupErrorMessage.getParameters().get("passedProperty") == "Grouped value"

    and: "The grouped error \"listInnerObjects[1].value\" should contain message with passed property"
    ErrorMessage secondListGroupErrorMessage = errors.get("listInnerObjects[1].value").getMessages().get(0)
    secondListGroupErrorMessage.getMessage() == "Grouped error"
    secondListGroupErrorMessage.getParameters().get("passedProperty") == "Grouped value"

    and: "The grouped error \"mapInnerObjects[object].value\" should contain message with passed property"
    ErrorMessage mapGroupedErrorMessage = errors.get("setInnerObjects[].value").getMessages().get(0)
    mapGroupedErrorMessage.getMessage() == "Grouped error"
    mapGroupedErrorMessage.getParameters().get("passedProperty") == "Grouped value"

    cleanup:
    validatorFactory.close();
  }

  @AlwaysFailed(passedProperty="Ungrouped value", message="Ungrouped error")
  private static class RootObject {
    @Valid
    private Set<InnerObject> setInnerObjects = new HashSet([
      new InnerObject(),
    ]);

    @Valid
    private List<InnerObject> listInnerObjects = [
      new InnerObject(),
      new InnerObject()
    ];

    @Valid
    private Map<String, InnerObject> mapInnerObjects = new HashMap([
      "object": new InnerObject()
    ]);
  }

  private static class InnerObject {
    @AlwaysFailed(passedProperty="Grouped value", message="Grouped error")
    private String value = "value";
  }
}
