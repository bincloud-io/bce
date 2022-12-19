package cloud.bangover.logging.audit;

import cloud.bangover.BoundedContextId;
import cloud.bangover.errors.ErrorDescriptor;
import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import cloud.bangover.errors.ErrorDescriptor.ErrorSeverity;
import cloud.bangover.errors.StubErrorDescriptor;
import cloud.bangover.logging.Level;
import cloud.bangover.logging.LogRecord;
import cloud.bangover.text.ErrorDescriptorTemplate;
import cloud.bangover.text.StubTransformer;
import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplates;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ServiceAuditEventTest {
  private static final String RANDOM_TEXT = "RANDOM TEXT";
  private static final String TRANSFORMED_TEXT = "TRANSFORMED TEXT";
  private static final String PARAM_KEY_1 = "KEY_1";
  private static final String PARAM_VALUE_1 = "VALUE_1";
  private static final String PARAM_KEY_2 = "KEY_2";
  private static final String PARAM_VALUE_2 = "VALUE_2";
  private static final BoundedContextId EVENT_CONTEXT =
      BoundedContextId.createFor("BOUNDED_CONTEXT");
  private static final ErrorCode ERROR_CODE = ErrorCode.createFor(100L);
  private static final Collection<String> AUDIT_DETAILS_PARAMETERS = Arrays.asList(PARAM_KEY_2);

  @DataPoints("levelsData")
  public static Level[] levelsData() {
    return new Level[] { Level.CRITIC, Level.DEBUG, Level.ERROR, Level.INFO, Level.TRACE,
        Level.WARN };
  }

  @Theory
  public void shouldBeInitializedFromEventCodeAndMessageTemplate(
      @FromDataPoints("levelsData") Level logLevel) {
    // When
    ServiceAuditEvent auditEvent = new ServiceAuditEvent(EVENT_CONTEXT, logLevel,
        createStubTextTemplate(), AUDIT_DETAILS_PARAMETERS);
    // Then
    Assert.assertNotNull(auditEvent.getAuditLogTimestamp());
    Assert.assertEquals(logLevel, auditEvent.getAuditLogLevel());
    Assert.assertEquals(RANDOM_TEXT, auditEvent.getAuditLogMessageText());
    Assert.assertEquals(PARAM_VALUE_2, auditEvent.getAuditDetailsParameters().get(PARAM_KEY_2));
    Assert.assertFalse(auditEvent.getAuditDetailsParameters().containsKey(PARAM_KEY_1));
    Assert.assertEquals(EVENT_CONTEXT, auditEvent.getContextId());
    Assert.assertEquals(ErrorCode.SUCCESSFUL_COMPLETED_CODE, auditEvent.getErrorCode());
  }

  @Theory
  public void shouldBeInitializedFromErrorDescriptor(@FromDataPoints("levelsData") Level logLevel) {
    // When
    ErrorDescriptor errorDescriptor = createStubErrorDescriptor();
    ServiceAuditEvent auditEvent =
        new ServiceAuditEvent(logLevel, errorDescriptor, AUDIT_DETAILS_PARAMETERS);
    // Then
    Assert.assertNotNull(auditEvent.getAuditLogTimestamp());
    Assert.assertEquals(logLevel, auditEvent.getAuditLogLevel());
    Assert.assertEquals(ErrorDescriptorTemplate.createFor(errorDescriptor).getTemplateText(),
        auditEvent.getAuditLogMessageText());
    Assert.assertEquals(PARAM_VALUE_2, auditEvent.getAuditDetailsParameters().get(PARAM_KEY_2));
    Assert.assertFalse(auditEvent.getAuditDetailsParameters().containsKey(PARAM_KEY_1));
    Assert.assertEquals(EVENT_CONTEXT, auditEvent.getContextId());
    Assert.assertEquals(ERROR_CODE, auditEvent.getErrorCode());
  }

  @Theory
  public void shouldAuditEventBeTransformed(@FromDataPoints("levelsData") Level logLevel) {
    // When
    ServiceAuditEvent sourceEvent = new ServiceAuditEvent(EVENT_CONTEXT, logLevel,
        createStubTextTemplate(), AUDIT_DETAILS_PARAMETERS);
    ServiceAuditEvent transformedEvent =
        sourceEvent.transformMessage(new StubTransformer(TextTemplates.createBy(TRANSFORMED_TEXT)));
    // Then
    Assert.assertFalse(sourceEvent == transformedEvent);
    Assert.assertEquals(sourceEvent.getAuditLogLevel(), transformedEvent.getAuditLogLevel());
    Assert.assertEquals(sourceEvent.getAuditLogTimestamp(),
        transformedEvent.getAuditLogTimestamp());
    Assert.assertEquals(sourceEvent.getContextId(), transformedEvent.getContextId());
    Assert.assertEquals(sourceEvent.getErrorCode(), transformedEvent.getErrorCode());
    Assert.assertEquals(TRANSFORMED_TEXT, transformedEvent.getAuditLogMessageText());
  }

  @Theory
  public void shouldLogEventBeExtracted(@FromDataPoints("levelsData") Level logLevel) {
    // Given
    ServiceAuditEvent sourceEvent = new ServiceAuditEvent(EVENT_CONTEXT, logLevel,
        createStubTextTemplate(), AUDIT_DETAILS_PARAMETERS);
    // When
    LogRecord auditLogRecord = sourceEvent.getAuditLogRecord();
    // Then
    Assert.assertEquals(sourceEvent.getAuditLogTimestamp(), auditLogRecord.getTimestamp());
    Assert.assertEquals(sourceEvent.getAuditLogLevel(), auditLogRecord.getLevel());
    Assert.assertEquals(sourceEvent.getAuditLogMessageText(), auditLogRecord.getMessageText());
    Assert.assertEquals(PARAM_VALUE_1, auditLogRecord.getMessageParameters().get(PARAM_KEY_1));
    Assert.assertEquals(PARAM_VALUE_2, auditLogRecord.getMessageParameters().get(PARAM_KEY_2));
  }

  private TextTemplate createStubTextTemplate() {
    return TextTemplates.createBy(RANDOM_TEXT).withParameter(PARAM_KEY_1, PARAM_VALUE_1)
        .withParameter(PARAM_KEY_2, PARAM_VALUE_2);
  }

  private ErrorDescriptor createStubErrorDescriptor() {
    return new StubErrorDescriptor(EVENT_CONTEXT, ERROR_CODE, ErrorSeverity.INCIDENT)
        .withDetailsParameter(PARAM_KEY_1, PARAM_VALUE_1)
        .withDetailsParameter(PARAM_KEY_2, PARAM_VALUE_2);
  }
}
