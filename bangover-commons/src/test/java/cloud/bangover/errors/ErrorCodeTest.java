package cloud.bangover.errors;

import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ErrorCodeTest {
  private static final Long ERROR_CODE_NUMBER = 12345L;
  private static final String STRINGIFIED_ERROR_CODE = "12345";

  @Test
  public void shouldSuccessfullyWrapErrorCode() {
    // When
    ErrorCode errorCode = ErrorCode.createFor(ERROR_CODE_NUMBER);
    // Then
    Assert.assertEquals(STRINGIFIED_ERROR_CODE, errorCode.toString());
    Assert.assertEquals(ERROR_CODE_NUMBER, errorCode.extract());
  }
}
