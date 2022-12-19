package cloud.bangover.errors;

import cloud.bangover.BoundedContextId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StubErrorDescriptor implements ErrorDescriptor {
  private final BoundedContextId contextId;
  private final ErrorCode errorCode;
  private final ErrorSeverity errorSeverity;
  private final Map<String, Object> errorDetails;

  public StubErrorDescriptor(BoundedContextId contextId, ErrorCode errorCode,
      ErrorSeverity errorSeverity) {
    this(contextId, errorCode, errorSeverity, Collections.emptyMap());
  }

  public StubErrorDescriptor(BoundedContextId contextId, ErrorCode errorCode,
      ErrorSeverity errorSeverity, Map<String, Object> errorDetails) {
    super();
    this.contextId = contextId;
    this.errorCode = errorCode;
    this.errorSeverity = errorSeverity;
    this.errorDetails = errorDetails;
  }

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
