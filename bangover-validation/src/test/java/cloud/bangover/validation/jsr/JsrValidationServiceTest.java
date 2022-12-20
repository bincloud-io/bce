package cloud.bangover.validation.jsr;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import cloud.bangover.validation.ErrorMessage;
import cloud.bangover.validation.GroupedErrors;
import cloud.bangover.validation.ValidationService;
import cloud.bangover.validation.ValidationState;
import cloud.bangover.validation.ValidationState.ErrorState;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsrValidationServiceTest {
  private ValidatorFactory validatorFactory;

  @Before
  public void setUp() {
    validatorFactory = Validation.byDefaultProvider().configure()
        .messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory();
  }

  @Test
  public void shouldValidateObjectUsingBeanValidation() {
    // Given
    ValidationService validationService = new JsrValidationService(validatorFactory.getValidator());
    RootObject validatable = new RootObject();
    // When
    ValidationState validationState = validationService.validate(validatable);
    ErrorState errorState = validationState.getErrorState();
    // Then
    Assert.assertFalse(validationState.isValid());
    ErrorMessage ungroupedMessage = errorState.getUngroupedErrors().iterator().next();
    Assert.assertEquals("Ungrouped error", ungroupedMessage.getMessage());
    Assert.assertEquals("Ungrouped value", ungroupedMessage.getParameters().get("passedProperty"));
    ErrorMessage setObjectsErrorMessage =
        GroupedErrors.errorsOf("setInnerObjects[].value", errorState).iterator().next();
    ErrorMessage firstListInnerObjects =
        GroupedErrors.errorsOf("listInnerObjects[0].value", errorState).iterator().next();
    ErrorMessage secondListInnerObjects =
        GroupedErrors.errorsOf("listInnerObjects[1].value", errorState).iterator().next();
    ErrorMessage mapObjectsErrorMessage =
        GroupedErrors.errorsOf("mapInnerObjects[object].value", errorState).iterator().next();
    Assert.assertEquals("Grouped error", setObjectsErrorMessage.getMessage());
    Assert.assertEquals("Grouped value",
        setObjectsErrorMessage.getParameters().get("passedProperty"));
    Assert.assertEquals("Grouped error", firstListInnerObjects.getMessage());
    Assert.assertEquals("Grouped value",
        firstListInnerObjects.getParameters().get("passedProperty"));
    Assert.assertEquals("Grouped error", secondListInnerObjects.getMessage());
    Assert.assertEquals("Grouped value",
        secondListInnerObjects.getParameters().get("passedProperty"));
    Assert.assertEquals("Grouped error", mapObjectsErrorMessage.getMessage());
    Assert.assertEquals("Grouped value",
        mapObjectsErrorMessage.getParameters().get("passedProperty"));
  }

  @After
  public void tearDown() {
    validatorFactory.close();
  }

  @Documented
  @Retention(RUNTIME)
  @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD })
  @Constraint(validatedBy = { AlwaysFailedValidator.class })
  private static @interface AlwaysFailed {
    String passedProperty();

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

  public static class AlwaysFailedValidator implements ConstraintValidator<AlwaysFailed, Object> {
    @Override
    public void initialize(AlwaysFailed constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      return false;
    }
  }

  @AlwaysFailed(passedProperty = "Ungrouped value", message = "Ungrouped error")
  private static class RootObject {
    @Valid
    private Set<InnerObject> setInnerObjects;

    @Valid
    private List<InnerObject> listInnerObjects;

    @Valid
    private Map<String, InnerObject> mapInnerObjects;

    public RootObject() {
      super();
      this.setInnerObjects = new HashSet<InnerObject>(Arrays.asList(new InnerObject()));
      this.listInnerObjects = Arrays.asList(new InnerObject(), new InnerObject());

      this.mapInnerObjects = new HashMap<String, InnerObject>();
      this.mapInnerObjects.put("object", new InnerObject());
    }
  }

  private static class InnerObject {
    @AlwaysFailed(passedProperty = "Grouped value", message = "Grouped error")
    private String value = "value";
  }
}
