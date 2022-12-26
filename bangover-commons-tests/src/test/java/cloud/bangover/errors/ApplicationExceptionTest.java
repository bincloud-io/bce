package cloud.bangover.errors;

import cloud.bangover.BoundedContextId;
import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import cloud.bangover.errors.ErrorDescriptor.ErrorSeverity;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ApplicationExceptionTest {
  private static final BoundedContextId CONTEXT_ID = BoundedContextId.createFor("CONTEXT");
  private static final ErrorCode ERROR_CODE = ErrorCode.createFor(100L);

  private final ErrorSeverity severity;

  public ApplicationExceptionTest(ErrorSeverity severity) {
    super();
    this.severity = severity;
  }

  @Test
  public void shouldCreateApplicationError() {
    // When
    ApplicationException exception = new SimpleApplicationException(severity);
    // Then
    Assert.assertEquals(CONTEXT_ID, exception.getContextId());
    Assert.assertEquals(ERROR_CODE, exception.getErrorCode());
    Assert.assertEquals(severity, exception.getErrorSeverity());

    Map<String, Object> errorDetails = exception.getErrorDetails();
    Assert.assertEquals(exception.getMessage(), errorDetails.get(ApplicationException.ERROR_MESSAGE_DETAIL_NAME));
    Assert.assertEquals(new ErrorStackTrace(exception), errorDetails.get(ApplicationException.ERROR_STACKTRACE_DETAIL_NAME));
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { ErrorSeverity.BUSINESS }, { ErrorSeverity.INCIDENT } });
  }

  private class SimpleApplicationException extends ApplicationException {
    private static final long serialVersionUID = 9049305114980229010L;

    public SimpleApplicationException(ErrorSeverity severity) {
      super(CONTEXT_ID, severity, ERROR_CODE);
    }
  }
}
