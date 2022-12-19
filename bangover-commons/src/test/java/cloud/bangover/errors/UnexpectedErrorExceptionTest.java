package cloud.bangover.errors;

import cloud.bangover.BoundedContextId;
import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import cloud.bangover.errors.ErrorDescriptor.ErrorSeverity;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UnexpectedErrorExceptionTest {
  private static final BoundedContextId CONTEXT_ID = BoundedContextId.createFor("CONTEXT");
  private static final BoundedContextId ANOTHER_CONTEXT_ID = BoundedContextId.createFor("ANOTHER_CONTEXT");
  private static final ErrorCode ERROR_CODE = ErrorCode.createFor(100L);
  private static final Exception NON_APPLICATION_ERROR = new Exception("ERROR");
  private static final Exception APPLICATION_ERROR = new SimpleApplicationException();

  private final UnexpectedErrorException error;
  private final BoundedContextId expectedBoundedContext;
  
  public UnexpectedErrorExceptionTest(UnexpectedErrorException wrappedError, BoundedContextId expectedBoundedContext) {
    super();
    this.error = wrappedError;
    this.expectedBoundedContext = expectedBoundedContext;
  }
  
  @Test
  public void shouldWrapObjectWithRecognizedContextId() {
    // Then
    Assert.assertEquals(expectedBoundedContext, error.getContextId());
    Assert.assertEquals(ErrorCode.UNRECOGNIZED_ERROR_CODE, error.getErrorCode());
    Assert.assertEquals(ErrorSeverity.INCIDENT, error.getErrorSeverity());
  }
  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {new UnexpectedErrorException(ANOTHER_CONTEXT_ID, APPLICATION_ERROR), ANOTHER_CONTEXT_ID},
      {new UnexpectedErrorException(ANOTHER_CONTEXT_ID, NON_APPLICATION_ERROR), ANOTHER_CONTEXT_ID},
      {new UnexpectedErrorException(APPLICATION_ERROR), CONTEXT_ID},
      {new UnexpectedErrorException(NON_APPLICATION_ERROR), BoundedContextId.PLATFORM_CONTEXT},
    });
  }
  
  private static class SimpleApplicationException extends ApplicationException {
    private static final long serialVersionUID = -4883740567075925951L;

    public SimpleApplicationException() {
      super(CONTEXT_ID, ErrorSeverity.BUSINESS, ERROR_CODE);
    }
  }
}
