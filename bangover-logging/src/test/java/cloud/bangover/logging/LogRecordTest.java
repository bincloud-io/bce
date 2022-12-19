package cloud.bangover.logging;

import cloud.bangover.BoundedContextId;
import cloud.bangover.errors.ErrorDescriptor;
import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import cloud.bangover.errors.ErrorDescriptor.ErrorSeverity;
import cloud.bangover.errors.StubErrorDescriptor;
import cloud.bangover.text.ErrorDescriptorTemplate;
import cloud.bangover.text.StubTransformer;
import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplates;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class LogRecordTest {
  private static final Object RANDOM_OBJECT = new Object();
  private static final String RANDOM_TEXT = "RANDOM TEXT";
  private static final String PARAM_KEY = "KEY";
  private static final String PARAM_VALUE = "VALUE";
  private static final BoundedContextId ERROR_CONTEXT = BoundedContextId.createFor("ERRCTX");
  private static final ErrorCode ERROR_CODE = ErrorCode.createFor(100L);

  @DataPoints("levelsData")
  public static Level[] levelsData() {
    return new Level[] { Level.CRITIC, Level.DEBUG, Level.ERROR, Level.INFO, Level.TRACE,
        Level.WARN };
  }

  @Theory
  public void shouldBeInitializedFromRandomObject(@FromDataPoints("levelsData") Level logLevel) {
    // When
    LogRecord logRecord = new LogRecord(logLevel, RANDOM_OBJECT);

    // Then
    Assert.assertNotNull(logRecord.getTimestamp());
    Assert.assertEquals(logLevel, logRecord.getLevel());
    Assert.assertEquals(RANDOM_OBJECT.toString(), logRecord.getMessageText());
    Assert.assertTrue(logRecord.getMessageParameters().isEmpty());
  }

  @Theory
  public void shouldBeInitializedFromRandomMessageText(
      @FromDataPoints("levelsData") Level logLevel) {
    // When
    LogRecord logRecord = new LogRecord(logLevel, RANDOM_TEXT);

    // Then
    Assert.assertNotNull(logRecord.getTimestamp());
    Assert.assertEquals(logLevel, logRecord.getLevel());
    Assert.assertEquals(RANDOM_TEXT.toString(), logRecord.getMessageText());
    Assert.assertTrue(logRecord.getMessageParameters().isEmpty());
  }

  @Theory
  public void shouldBeInitializedFromErrorDescriptor(@FromDataPoints("levelsData") Level logLevel) {
    // When
    ErrorDescriptor errorDescriptor = createStubErrorDescriptor();
    LogRecord logRecord = new LogRecord(logLevel, errorDescriptor);

    // Then
    Assert.assertNotNull(logRecord.getTimestamp());
    Assert.assertEquals(logLevel, logRecord.getLevel());
    TextTemplate expectedTemplate = ErrorDescriptorTemplate.createFor(errorDescriptor);
    Assert.assertEquals(expectedTemplate.getTemplateText(), logRecord.getMessageText());
    Assert.assertEquals(expectedTemplate.getParameters(), logRecord.getMessageParameters());
  }

  @Theory
  public void shouldBeInitializedFromMessageTemplate(@FromDataPoints("levelsData") Level logLevel) {
    // When
    TextTemplate errorTextTemplate = createStubTextTemplate();
    LogRecord logRecord = new LogRecord(logLevel, errorTextTemplate);

    // Then
    Assert.assertNotNull(logRecord.getTimestamp());
    Assert.assertEquals(logLevel, logRecord.getLevel());
    Assert.assertEquals(errorTextTemplate.getTemplateText(), logRecord.getMessageText());
    Assert.assertEquals(errorTextTemplate.getParameters(), logRecord.getMessageParameters());
  }
  
  @Theory
  public void shouldMessageBeTransformed(@FromDataPoints("levelsData") Level logLevel) {
    // When
    LogRecord logRecord = new LogRecord(logLevel, RANDOM_OBJECT);
    LogRecord transformedRecord = logRecord.transformMessage(new StubTransformer(TextTemplates.createBy(RANDOM_TEXT)));
    
    // Then
    Assert.assertNotNull(logRecord.getTimestamp());
    Assert.assertEquals(logLevel, logRecord.getLevel());
    Assert.assertFalse(logRecord == transformedRecord);
    Assert.assertEquals(RANDOM_TEXT, transformedRecord.getMessageText());
  }
  
  private TextTemplate createStubTextTemplate() {
    return TextTemplates.createBy(RANDOM_TEXT).withParameter(PARAM_KEY, PARAM_VALUE);
  }

  private ErrorDescriptor createStubErrorDescriptor() {
    return new StubErrorDescriptor(ERROR_CONTEXT, ERROR_CODE, ErrorSeverity.INCIDENT)
        .withDetailsParameter(PARAM_KEY, PARAM_VALUE);
  }
}
