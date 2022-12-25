package cloud.bangover.logging;

import cloud.bangover.errors.ErrorStackTrace;
import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplates;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AbstractLoggerTest {
  private static final String LOG_MESSAGE = "Hello world";
  private static final RuntimeException THROWABLE = new RuntimeException("Something went wrong!!!");
  private static final String THROWABLE_STACKTRACE = new ErrorStackTrace(THROWABLE).toString();
  private static final TextTemplate LOG_MESSAGE_TEMPLATE = TextTemplates.createBy(LOG_MESSAGE);

  private final AbstractTask task;
  private final Level expectedLevel;
  private final String expectedMessageText;

  public AbstractLoggerTest(AbstractTask task, Level expectedLevel, String expectedMessageText) {
    super();
    this.task = task;
    this.expectedLevel = expectedLevel;
    this.expectedMessageText = expectedMessageText;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        // Log string
        { new TraceWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.TRACE, LOG_MESSAGE },
        { new DebugWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.DEBUG, LOG_MESSAGE },
        { new InfoWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.INFO, LOG_MESSAGE },
        { new WarnWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.WARN, LOG_MESSAGE },
        { new ErrorWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.ERROR, LOG_MESSAGE },
        { new CriticWriteStringTask(new StubLogger(), LOG_MESSAGE), Level.CRITIC, LOG_MESSAGE },
        // Log throwable
        { new TraceWriteThrowableTask(new StubLogger(), THROWABLE), Level.TRACE,
            THROWABLE_STACKTRACE },
        { new DebugWriteThrowableTask(new StubLogger(), THROWABLE), Level.DEBUG,
            THROWABLE_STACKTRACE },
        { new InfoWriteThrowableTask(new StubLogger(), THROWABLE), Level.INFO,
            THROWABLE_STACKTRACE },
        { new WarnWriteThrowableTask(new StubLogger(), THROWABLE), Level.WARN,
            THROWABLE_STACKTRACE },
        { new ErrorWriteThrowableTask(new StubLogger(), THROWABLE), Level.ERROR,
            THROWABLE_STACKTRACE },
        { new CriticWriteThrowableTask(new StubLogger(), THROWABLE), Level.CRITIC,
            THROWABLE_STACKTRACE },
        // Log text record
        { new TraceWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.TRACE,
            LOG_MESSAGE },
        { new DebugWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.DEBUG,
            LOG_MESSAGE },
        { new InfoWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.INFO,
            LOG_MESSAGE },
        { new WarnWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.WARN,
            LOG_MESSAGE },
        { new ErrorWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.ERROR,
            LOG_MESSAGE },
        { new CriticWriteTextTemplateTask(new StubLogger(), LOG_MESSAGE_TEMPLATE), Level.CRITIC,
            LOG_MESSAGE }, });
  }

  @Test
  public void shouldLogMessage() {
    // When
    task.run();
    // Then
    LogRecord loggedRecord = task.getLoggedRecord();
    Assert.assertEquals(expectedLevel, loggedRecord.getLevel());
    Assert.assertEquals(expectedMessageText, loggedRecord.getMessageText());
  }

  private static class StubLogger extends AbstractLogger {
    private LogRecord lastLogged;

    public LogRecord getLastLogged() {
      return lastLogged;
    }

    @Override
    public void log(LogRecord logRecord) {
      this.lastLogged = logRecord;
    }

    @Override
    public ApplicationLogger named(String loggerName) {
      throw new UnsupportedOperationException();
    }
  }

  private static abstract class AbstractTask implements Runnable {
    protected final StubLogger logger;

    public AbstractTask(StubLogger logger) {
      super();
      this.logger = logger;
    }

    public LogRecord getLoggedRecord() {
      return this.logger.getLastLogged();
    }
  }

  private static abstract class AbstractWriteStringTask extends AbstractTask {
    protected final String message;

    public AbstractWriteStringTask(StubLogger logger, String message) {
      super(logger);
      this.message = message;
    }
  }

  private static class TraceWriteStringTask extends AbstractWriteStringTask {
    public TraceWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.trace(message);
    }
  }

  private static class DebugWriteStringTask extends AbstractWriteStringTask {
    public DebugWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.debug(message);
    }
  }

  private static class InfoWriteStringTask extends AbstractWriteStringTask {
    public InfoWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.info(message);
    }
  }

  private static class WarnWriteStringTask extends AbstractWriteStringTask {
    public WarnWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.warn(message);
    }
  }

  private static class ErrorWriteStringTask extends AbstractWriteStringTask {
    public ErrorWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.error(message);
    }
  }

  private static class CriticWriteStringTask extends AbstractWriteStringTask {
    public CriticWriteStringTask(StubLogger logger, String message) {
      super(logger, message);
    }

    @Override
    public void run() {
      logger.critic(message);
    }
  }

  private static abstract class AbstractWriteThrowableTask extends AbstractTask {
    protected final Throwable error;

    public AbstractWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger);
      this.error = error;
    }
  }

  private static class TraceWriteThrowableTask extends AbstractWriteThrowableTask {
    public TraceWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.trace(error);
    }
  }

  private static class DebugWriteThrowableTask extends AbstractWriteThrowableTask {
    public DebugWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.debug(error);
    }
  }

  private static class InfoWriteThrowableTask extends AbstractWriteThrowableTask {
    public InfoWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.info(error);
    }
  }

  private static class WarnWriteThrowableTask extends AbstractWriteThrowableTask {
    public WarnWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.warn(error);
    }
  }

  private static class ErrorWriteThrowableTask extends AbstractWriteThrowableTask {
    public ErrorWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.error(error);
    }
  }

  private static class CriticWriteThrowableTask extends AbstractWriteThrowableTask {
    public CriticWriteThrowableTask(StubLogger logger, Throwable error) {
      super(logger, error);
    }

    @Override
    public void run() {
      logger.critic(error);
    }
  }

  private static abstract class AbstractWriteTextTemplateTask extends AbstractTask {
    protected final TextTemplate textTemplate;

    public AbstractWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger);
      this.textTemplate = textTemplate;
    }
  }

  private static class TraceWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public TraceWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.trace(textTemplate);
    }
  }

  private static class DebugWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public DebugWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.debug(textTemplate);
    }
  }

  private static class InfoWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public InfoWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.info(textTemplate);
    }
  }

  private static class WarnWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public WarnWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.warn(textTemplate);
    }

  }

  private static class ErrorWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public ErrorWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.error(textTemplate);
    }
  }

  private static class CriticWriteTextTemplateTask extends AbstractWriteTextTemplateTask {
    public CriticWriteTextTemplateTask(StubLogger logger, TextTemplate textTemplate) {
      super(logger, textTemplate);
    }

    @Override
    public void run() {
      logger.critic(textTemplate);
    }
  }
}
