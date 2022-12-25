package cloud.bangover.errors;

import cloud.bangover.BoundedContextId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * This class is the {@link ErrorDescriptor} stub implementation. It must be used only for testing
 * goals.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@RequiredArgsConstructor
public class StubErrorDescriptor implements ErrorDescriptor {
  private final BoundedContextId contextId;
  private final ErrorCode errorCode;
  private final ErrorSeverity errorSeverity;
  private final Map<String, Object> errorDetails;

  /**
   * Create {@link StubErrorDescriptor} by context id, error code and error severity.
   *
   * @param contextId     The context id
   * @param errorCode     The error code
   * @param errorSeverity The error severity
   */
  public StubErrorDescriptor(BoundedContextId contextId, ErrorCode errorCode,
      ErrorSeverity errorSeverity) {
    this(contextId, errorCode, errorSeverity, Collections.<String, Object>emptyMap());
  }

  /**
   * Append details parameter.
   *
   * @param key   The details key
   * @param value The details value
   * @return The derived {@link StubErrorDescriptor} with appended parameter
   */
  public StubErrorDescriptor withDetailsParameter(String key, Object value) {
    Map<String, Object> parameters = new HashMap<String, Object>(errorDetails);
    StubErrorDescriptor result =
        new StubErrorDescriptor(contextId, errorCode, errorSeverity, parameters);
    parameters.put(key, value);
    return result;
  }

  @Override
  public BoundedContextId getContextId() {
    return contextId;
  }

  @Override
  public ErrorCode getErrorCode() {
    return errorCode;
  }

  @Override
  public ErrorSeverity getErrorSeverity() {
    return errorSeverity;
  }

  @Override
  public Map<String, Object> getErrorDetails() {
    return errorDetails;
  }

}
