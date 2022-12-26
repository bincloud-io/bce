package cloud.bangover.validation;

import cloud.bangover.validation.ValidationGroup.WrongValidationGroupFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ValidationGroupTest {
  @DataPoints("wrongGroupNames")
  public static String[] wrongGroupNames() {
    return new String[] { "", "Group Name", " GroupName", "GroupName " };
  }

  @Test
  public void shouldValidationGroupBeCreatedSuccessfully() {
    // When
    ValidationGroup group = ValidationGroup.createFor("GROUP_NAME");
    // Then
    Assert.assertEquals("GROUP_NAME", group.toString());
  }

  @Theory
  public void shouldNotBeCreated(@FromDataPoints("wrongGroupNames") String wrongName) {
    Assert.assertThrows(WrongValidationGroupFormatException.class, new ThrowingRunnable() {
      @Override
      public void run() throws Throwable {
        ValidationGroup.createFor(wrongName);
      }
    });
  }

  @Test
  public void shouldUngroupedValidationGroupNameBeReserved() {
    // Expected
    ValidationGroup ungroupedValue = ValidationGroup.UNGROUPED;
    Assert.assertEquals("$$__UNGROUPED_MESSAGES__$$", ungroupedValue.toString());
  }

  @Test
  public void shouldNonReservedGroupBeDerivedForSubgroup() {
    // Given
    ValidationGroup baseGroup = ValidationGroup.createFor("base");
    ValidationGroup subGroup = ValidationGroup.createFor("derived");
    // When
    ValidationGroup resultGroup = baseGroup.deriveWith(subGroup);
    // Then
    Assert.assertEquals("base.derived", resultGroup.toString());
  }
  
  @Test
  public void shouldUngroupedReservedGroupBeDerivedForSubgroup() {
    // Given
    ValidationGroup rootGroup = ValidationGroup.UNGROUPED;
    ValidationGroup subGroup = ValidationGroup.createFor("derived");
    // When
    ValidationGroup resultGroup = rootGroup.deriveWith(subGroup);
    // Then
    Assert.assertEquals("derived", resultGroup.toString());
  }
  
  @Test
  public void shouldNonReservedGroupBeDerivedForUngroupedReservedGroup() {
    // Given
    ValidationGroup rootGroup = ValidationGroup.createFor("base");
    ValidationGroup subGroup = ValidationGroup.UNGROUPED;
    // When
    ValidationGroup resultGroup = rootGroup.deriveWith(subGroup);
    // Then
    Assert.assertEquals("base", resultGroup.toString());
  }
}
