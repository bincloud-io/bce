package cloud.bangover;

import cloud.bangover.BoundedContextId.WrongBoundedContextIdFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BoundedContextIdTest {
  private static final String CONTEXT_ID_STRING = "CONTEXT";
  private static final String PLATFORM_CONTEXT_ID = "PLATFORM";
  private static final String BAD_FORMATTED_NAME = "^&^&%^%:::&*&*)*(_*()";

  @Test
  public void shouldCreateSuccessfullyContextFromWellFormattedContextName() {
    // When
    BoundedContextId contextId = BoundedContextId.createFor(CONTEXT_ID_STRING);
    // Then
    Assert.assertEquals(CONTEXT_ID_STRING, contextId.toString());
  }

  @Test
  public void shouldPlatformContextIdHaveCorrectName() {
    // Then
    Assert.assertEquals(PLATFORM_CONTEXT_ID, BoundedContextId.PLATFORM_CONTEXT.toString());
  }

  @Test(expected = WrongBoundedContextIdFormatException.class)
  public void shouldCreateWithErrorFromBadFormattedContextName() {
    // Then
    BoundedContextId.createFor(BAD_FORMATTED_NAME);
  }

  @Test(expected = WrongBoundedContextIdFormatException.class)
  public void shouldCreateWithErrorFromDefaultPlatformContextIdName() {
    // Then
    BoundedContextId.createFor(PLATFORM_CONTEXT_ID);
  }
}
