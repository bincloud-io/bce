package cloud.bangover.errors;

import cloud.bangover.BoundedContextId;
import lombok.NonNull;

/**
 * This class represents the unrecognized application exception.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public final class UnexpectedErrorException extends ApplicationException {
  private static final long serialVersionUID = -263012476173962439L;

  /**
   * Create {@link UnexpectedErrorException} by a random throwable object
   * 
   * @param unexpectedError The happened error
   */
  public UnexpectedErrorException(@NonNull Throwable unexpectedError) {
    this(recognizeContext(unexpectedError), unexpectedError);
  }

  /**
   * Create {@link UnexpectedErrorException} by a random throwable object with specified
   * {@link BoundedContextId}
   * 
   * @param contextId       The context id
   * @param unexpectedError The happened error
   */
  public UnexpectedErrorException(@NonNull BoundedContextId contextId,
      @NonNull Throwable unexpectedError) {
    super(contextId, ErrorSeverity.INCIDENT, ErrorCode.UNRECOGNIZED_ERROR_CODE);
    initCause(unexpectedError);
  }

  private static BoundedContextId recognizeContext(Throwable unexpectedError) {
    if (unexpectedError instanceof ErrorDescriptor) {
      return ((ErrorDescriptor) unexpectedError).getContextId();
    }
    return BoundedContextId.PLATFORM_CONTEXT;
  }
}
