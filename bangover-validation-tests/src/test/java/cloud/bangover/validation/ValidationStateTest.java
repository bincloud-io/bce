package cloud.bangover.validation;

import cloud.bangover.validation.ValidationState.ErrorState;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidationStateTest {

  private static final String GROUP_NAME = "group";
  private static final ValidationGroup GROUP = ValidationGroup.createFor(GROUP_NAME);

  private static final String GROUP_1_NAME = "group.1";
  private static final ValidationGroup GROUP_1 = ValidationGroup.createFor(GROUP_1_NAME);

  private static final String GROUP_2_NAME = "group.2";
  private static final ValidationGroup GROUP_2 = ValidationGroup.createFor(GROUP_2_NAME);

  private static final String BASE_GROUP_NAME = "base";
  private static final ValidationGroup BASE_GROUP = ValidationGroup.createFor(BASE_GROUP_NAME);
  private static final String DERIVED_BASE_GROUP_NAME = "base.group";

  private static final ErrorMessage SIMPLE_MESSAGE = ErrorMessage.createFor("simple-message");
  private static final ErrorMessage GROUPED_MESSAGE = ErrorMessage.createFor("grouped_message");
  private static final ErrorMessage UNGROUPED_MESSAGE_1 =
      ErrorMessage.createFor("ungrouped_message_1");
  private static final ErrorMessage UNGROUPED_MESSAGE_2 =
      ErrorMessage.createFor("ungrouped_message_2");
  private static final ErrorMessage MESSAGE_1 = ErrorMessage.createFor("message_1");
  private static final ErrorMessage MESSAGE_2 = ErrorMessage.createFor("message_2");

  @Test
  public void shouldEmptyValidationStateBeCreated() {
    // When
    ValidationState validationState = new ValidationState();
    ErrorState errorState = validationState.getErrorState();
    // Then
    Assert.assertTrue(validationState.isValid());
    Assert.assertTrue(errorState.getGroupedErrors().isEmpty());
    Assert.assertTrue(errorState.getUngroupedErrors().isEmpty());
  }

  @Test
  public void shouldValidationStateCreatedWithGroupedErrors() {
    // When
    ValidationState validationState = new ValidationState().withGrouped(GROUP, SIMPLE_MESSAGE);
    ErrorState errorState = validationState.getErrorState();
    // Then
    Assert.assertFalse(validationState.isValid());
    Assert.assertTrue(
        GroupedErrors.errorsOf(GROUP_NAME, errorState).containsAll(Arrays.asList(SIMPLE_MESSAGE)));
    Assert.assertTrue(errorState.getUngroupedErrors().isEmpty());
  }

  @Test
  public void shouldValidationStateCreatedWithUnroupedErrors() {
    // When
    ValidationState validationState = new ValidationState().withUngrouped(SIMPLE_MESSAGE);
    ErrorState errorState = validationState.getErrorState();

    // Then
    Assert.assertFalse(validationState.isValid());
    Assert.assertTrue(errorState.getGroupedErrors().isEmpty());
    Assert.assertFalse(errorState.getUngroupedErrors().isEmpty());
    Assert.assertTrue(errorState.getUngroupedErrors().contains(SIMPLE_MESSAGE));
  }

  @Test
  public void shouldValidationStateBeCreatedWithBothTypesOfErrors() {
    // When
    ValidationState validationState = new ValidationState().withGrouped(GROUP, GROUPED_MESSAGE)
        .withUngrouped(UNGROUPED_MESSAGE_1).withUngrouped(UNGROUPED_MESSAGE_2);
    ErrorState errorState = validationState.getErrorState();
    // Then
    Assert.assertFalse(validationState.isValid());
    Assert.assertTrue(
        GroupedErrors.errorsOf(GROUP_NAME, errorState).containsAll(Arrays.asList(GROUPED_MESSAGE)));
    Assert.assertTrue(errorState.getUngroupedErrors()
        .containsAll(Arrays.asList(UNGROUPED_MESSAGE_1, UNGROUPED_MESSAGE_2)));
  }

  @Test
  public void shouldValidationStatesBeMerged() {
    // Given
    ValidationState sourceState =
        new ValidationState().withGrouped(GROUP_1, MESSAGE_1).withGrouped(GROUP_1, MESSAGE_2);
    ValidationState stateToMerge = new ValidationState().withUngrouped(UNGROUPED_MESSAGE_1)
        .withGrouped(GROUP_1, MESSAGE_1).withGrouped(GROUP_2, MESSAGE_1);
    // When
    ValidationState result = sourceState.merge(stateToMerge);
    // Then
    ErrorState errorState = result.getErrorState();
    Assert.assertTrue(
        errorState.getUngroupedErrors().containsAll(Arrays.asList(UNGROUPED_MESSAGE_1)));
    Assert.assertTrue(GroupedErrors.errorsOf(GROUP_1_NAME, errorState)
        .containsAll(Arrays.asList(MESSAGE_1, MESSAGE_2)));
    Assert.assertTrue(
        GroupedErrors.errorsOf(GROUP_2_NAME, errorState).containsAll(Arrays.asList(MESSAGE_1)));
  }

  @Test
  public void shouldAllBeDerivedFromSpecifiedBaseGroup() {
    // Given
    ValidationState sourceState = new ValidationState().withUngrouped(UNGROUPED_MESSAGE_1)
        .withGrouped(GROUP, GROUPED_MESSAGE);
    // When
    ErrorState errorState = sourceState.asSubgroup(BASE_GROUP).getErrorState();
    // Then
    Assert.assertTrue(GroupedErrors.errorsOf(DERIVED_BASE_GROUP_NAME, errorState)
        .containsAll(Arrays.asList(GROUPED_MESSAGE)));
    Assert.assertTrue(GroupedErrors.errorsOf(BASE_GROUP_NAME, errorState)
        .containsAll(Arrays.asList(UNGROUPED_MESSAGE_1)));
    Assert.assertTrue(errorState.getUngroupedErrors().isEmpty());
  }

  @Test
  public void shouldGroupedMessagesOnlyBeDerivedFromSpecifiedBaseGroup() {
    // Given
    ValidationState sourceState = new ValidationState().withUngrouped(UNGROUPED_MESSAGE_1)
        .withGrouped(GROUP, GROUPED_MESSAGE);
    // When
    ErrorState errorState = sourceState.asDerivedFrom(BASE_GROUP).getErrorState();
    // Then
    Assert.assertTrue(GroupedErrors.errorsOf(DERIVED_BASE_GROUP_NAME, errorState)
        .containsAll(Arrays.asList(GROUPED_MESSAGE)));
    Assert.assertTrue(
        errorState.getUngroupedErrors().containsAll(Arrays.asList(UNGROUPED_MESSAGE_1)));
  }
}
