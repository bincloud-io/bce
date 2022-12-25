package cloud.bangover.validation.context;

import cloud.bangover.validation.ErrorMessage;
import cloud.bangover.validation.GroupedErrors;
import cloud.bangover.validation.StubRule;
import cloud.bangover.validation.ValidationState;
import cloud.bangover.validation.ValidationState.ErrorState;
import cloud.bangover.validation.context.ValidationContext.DerivationPolicy;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ValidationContextTest {
  private static final String FIRST_LEVEL_GROUP_NAME = "gr-first";
  private static final String SECOND_LEVEL_GROUP_NAME = "gr-second";
  private static final String THIRD_LEVEL_GROUP_NAME = "gr-third";
  private static final String FOURTH_LEVEL_GROUP_NAME = "gr-fourth";
  private static final String ELEMENTS_COLLECTION_GROUP = "collection";

  private static final String DERIVED_FROM_FIRST_AND_SECOND_LEVEL_GROUP_NAME =
      String.format("%s.%s", FIRST_LEVEL_GROUP_NAME, SECOND_LEVEL_GROUP_NAME);
  private static final String DERIVED_FROM_FIRST_AND_SECOND_AND_THIRD_LEVEL_GROUP_NAME = String
      .format("%s.%s", DERIVED_FROM_FIRST_AND_SECOND_LEVEL_GROUP_NAME, THIRD_LEVEL_GROUP_NAME);
  private static final String DERIVED_COLLECTION_ITEM_UNGROUPED_ERRORS_GROUP_NAME = String.format(
      "%s.%s.[1]", DERIVED_FROM_FIRST_AND_SECOND_LEVEL_GROUP_NAME, ELEMENTS_COLLECTION_GROUP);
  private static final String DERIVED_COLLECTION_ITEM_GROUPED_ERRORS_GROUP_NAME = String.format(
      "%s.%s", DERIVED_COLLECTION_ITEM_UNGROUPED_ERRORS_GROUP_NAME, FOURTH_LEVEL_GROUP_NAME);

  private static final String MESSAGE_TEXT_1 = "Message text 1";
  private static final String MESSAGE_TEXT_2 = "Message text 2";
  private static final String MESSAGE_TEXT_3 = "Message text 3";
  private static final String MESSAGE_TEXT_4 = "Message text 4";
  private static final String MESSAGE_TEXT_5 = "Message text 5";
  private static final String MESSAGE_TEXT_6 = "Message text 6";
  private static final String MESSAGE_TEXT_7 = "Message text 7";
  private static final String MESSAGE_TEXT_8 = "Message text 8";
  private static final String MESSAGE_TEXT_9 = "Message text 9";
  private static final String MESSAGE_TEXT_10 = "Message text 10";

  private static final ErrorMessage MESSAGE_TEMPLATE_1 = ErrorMessage.createFor(MESSAGE_TEXT_1);
  private static final ErrorMessage MESSAGE_TEMPLATE_2 = ErrorMessage.createFor(MESSAGE_TEXT_2);
  private static final ErrorMessage MESSAGE_TEMPLATE_3 = ErrorMessage.createFor(MESSAGE_TEXT_3);
  private static final ErrorMessage MESSAGE_TEMPLATE_4 = ErrorMessage.createFor(MESSAGE_TEXT_4);
  private static final ErrorMessage MESSAGE_TEMPLATE_5 = ErrorMessage.createFor(MESSAGE_TEXT_5);
  private static final ErrorMessage MESSAGE_TEMPLATE_6 = ErrorMessage.createFor(MESSAGE_TEXT_6);
  private static final ErrorMessage MESSAGE_TEMPLATE_7 = ErrorMessage.createFor(MESSAGE_TEXT_7);
  private static final ErrorMessage MESSAGE_TEMPLATE_8 = ErrorMessage.createFor(MESSAGE_TEXT_8);
  private static final ErrorMessage MESSAGE_TEMPLATE_9 = ErrorMessage.createFor(MESSAGE_TEXT_9);
  private static final ErrorMessage MESSAGE_TEMPLATE_10 = ErrorMessage.createFor(MESSAGE_TEXT_10);

  private static final Object VALIDATABLE_VALUE_1 = new Object();
  private static final Object VALIDATABLE_VALUE_2 = new Object();

  @DataPoints("validationContextTasks")
  public static Runnable[] validationContextTasks() {
    return new Runnable[] {
        new ValidationContextTask(false, Arrays.asList(), Arrays.asList(), true),
        new ValidationContextTask(true, Arrays.asList(), Arrays.asList(), true),
        new ValidationContextTask(true, Arrays.asList(MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2),
            Arrays.asList(), false),
        new ValidationContextTask(true, Arrays.asList(),
            Arrays.asList(MESSAGE_TEMPLATE_3, MESSAGE_TEMPLATE_4), false),
        new ValidationContextTask(true, Arrays.asList(MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2),
            Arrays.asList(MESSAGE_TEMPLATE_3, MESSAGE_TEMPLATE_4), false), };
  }

  @Test
  public void shouldErrorsBeAppendedToContextManually() {
    // Given
    ValidationContext context = new ValidationContext();
    ErrorMessage[] ungroupedErrorTextTemplates =
        new ErrorMessage[] { MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2, MESSAGE_TEMPLATE_3 };

    ErrorMessage[] groupedErrorTextTemplates =
        new ErrorMessage[] { MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2 };

    // When
    context = context.withErrors(ungroupedErrorTextTemplates);
    context = context.withErrors(FIRST_LEVEL_GROUP_NAME, groupedErrorTextTemplates);
    ValidationState validationState = context.getState();
    ErrorState errorState = validationState.getErrorState();

    // Then
    Assert.assertTrue(errorState.getUngroupedErrors()
        .containsAll(Arrays.asList(MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2, MESSAGE_TEMPLATE_3)));

    Assert.assertTrue(GroupedErrors.errorsOf(FIRST_LEVEL_GROUP_NAME, errorState)
        .containsAll(Arrays.asList(MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_2)));
  }

  @Theory
  public void shouldValidationRulesBeChecked(
      @FromDataPoints("validationContextTasks") Runnable validationTask) {
    validationTask.run();
  }

  @Test
  public void shouldInternalObjectsBeValidated() {
    // Given
    ValidationContext context = new ValidationContext();
    Validatable validatable = new RootEntity();

    // When
    context = validatable.validate(context);
    ValidationState validationState = context.getState();
    ErrorState errorState = validationState.getErrorState();

    // Then
    Assert.assertFalse(validationState.isValid());

    Assert.assertTrue(errorState.getUngroupedErrors()
        .containsAll(Arrays.asList(MESSAGE_TEMPLATE_1, MESSAGE_TEMPLATE_8)));

    Assert.assertTrue(GroupedErrors.errorsOf(FIRST_LEVEL_GROUP_NAME, errorState)
        .containsAll(Arrays.asList(MESSAGE_TEMPLATE_2, MESSAGE_TEMPLATE_3)));

    Assert.assertTrue(
        GroupedErrors.errorsOf(DERIVED_FROM_FIRST_AND_SECOND_LEVEL_GROUP_NAME, errorState)
            .containsAll(Arrays.asList(MESSAGE_TEMPLATE_4, MESSAGE_TEMPLATE_5)));
    Assert.assertTrue(
        GroupedErrors.errorsOf(DERIVED_FROM_FIRST_AND_SECOND_AND_THIRD_LEVEL_GROUP_NAME, errorState)
            .containsAll(Arrays.asList(MESSAGE_TEMPLATE_6, MESSAGE_TEMPLATE_7)));

    Assert.assertTrue(
        GroupedErrors.errorsOf(DERIVED_COLLECTION_ITEM_UNGROUPED_ERRORS_GROUP_NAME, errorState)
            .containsAll(Arrays.asList(MESSAGE_TEMPLATE_9)));

    Assert.assertTrue(
        GroupedErrors.errorsOf(DERIVED_COLLECTION_ITEM_GROUPED_ERRORS_GROUP_NAME, errorState)
            .containsAll(Arrays.asList(MESSAGE_TEMPLATE_10)));
  }

  @Test
  public void shouldIsntHavingValidatableInterfaceObjectsBeValidated() {
    Assert.assertTrue(ValidationContext.createValidationService().validate(new Object()).isValid());
  }

  @Test
  public void shouldHavingValidatableInterfaceObjectsBeValidated() {
    Assert.assertFalse(
        ValidationContext.createValidationService().validate(new RootEntity()).isValid());
  }

  private static class ValidationContextTask implements Runnable {
    private final boolean acceptable;
    private final Collection<ErrorMessage> ungroupedErrors;
    private final Collection<ErrorMessage> groupedErrors;
    private final boolean valid;

    public ValidationContextTask(boolean acceptable, Collection<ErrorMessage> ungroupedErrors,
        Collection<ErrorMessage> groupedErrors, boolean valid) {
      super();
      this.acceptable = acceptable;
      this.ungroupedErrors = ungroupedErrors;
      this.groupedErrors = groupedErrors;
      this.valid = valid;
    }

    @Override
    public void run() {
      // Given
      ValidationContext context = new ValidationContext();
      
      StubRule<Object> rule =
          new StubRule<Object>(Object.class)
            .stubFor(VALIDATABLE_VALUE_1, acceptable, ungroupedErrors)
            .stubFor(VALIDATABLE_VALUE_2, acceptable, groupedErrors);
      // When
      context = context.withRule(VALIDATABLE_VALUE_1, rule);
      context = context.withRule(FIRST_LEVEL_GROUP_NAME, VALIDATABLE_VALUE_2, rule);
      ValidationState validationState = context.getState();
      ErrorState errorState = validationState.getErrorState();
      // Then
      Assert.assertEquals(valid, validationState.isValid());
      Assert.assertTrue(errorState.getUngroupedErrors().containsAll(ungroupedErrors));
      Assert.assertTrue(
          GroupedErrors.errorsOf(FIRST_LEVEL_GROUP_NAME, errorState).containsAll(groupedErrors));
      ;
    }
  }

  private static class RootEntity implements Validatable {
    private FirstEntity firstEntity = new FirstEntity();
    private SecondEntity secondEntity = new SecondEntity();

    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.validate(FIRST_LEVEL_GROUP_NAME, firstEntity).validate(FIRST_LEVEL_GROUP_NAME,
          secondEntity, DerivationPolicy.DERIVE_GROUPES);
    }
  }

  private static class FirstEntity implements Validatable {
    private ThirdEntity thirdEntity = new ThirdEntity();

    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.withErrors(MESSAGE_TEMPLATE_2).validate(thirdEntity);
    }
  }

  private static class ThirdEntity implements Validatable {
    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.withErrors(MESSAGE_TEMPLATE_3).withErrors(SECOND_LEVEL_GROUP_NAME,
          MESSAGE_TEMPLATE_4, MESSAGE_TEMPLATE_5);
    }
  }

  private static class SecondEntity implements Validatable {
    private FourthEntity fourthEntity = new FourthEntity();

    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.withErrors(MESSAGE_TEMPLATE_1).validate(fourthEntity,
          DerivationPolicy.DERIVE_GROUPES);
    }
  }

  private static class FourthEntity implements Validatable {
    private FifthEntity fifthEntity = new FifthEntity();

    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.validate(SECOND_LEVEL_GROUP_NAME, fifthEntity,
          DerivationPolicy.DERIVE_GROUPES);
    }
  }

  private static class FifthEntity implements Validatable {
    private Collection<Object> firstCollection = Arrays.asList(new Object(), new SixthEntity());

    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.withErrors(MESSAGE_TEMPLATE_8)
          .withErrors(THIRD_LEVEL_GROUP_NAME, MESSAGE_TEMPLATE_6, MESSAGE_TEMPLATE_7)
          .validate(ELEMENTS_COLLECTION_GROUP, firstCollection);
    }
  }

  private static class SixthEntity implements Validatable {
    @Override
    public ValidationContext validate(ValidationContext context) {
      return context.withErrors(MESSAGE_TEMPLATE_9).withErrors(FOURTH_LEVEL_GROUP_NAME,
          MESSAGE_TEMPLATE_10);
    }
  }
}
