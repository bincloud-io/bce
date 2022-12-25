package cloud.bangover.errors;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ErrorStackTraceTest {
  @Test
  public void shouldStringifyStackTrace() {
    // Given
    Throwable throwableObject = new Throwable("RANDOM THROWABLE");
    ErrorStackTrace errorStace = new ErrorStackTrace(throwableObject);
    // Then
    Assert.assertEquals(stringifyStackTrace(throwableObject), errorStace.toString());
  }

  private String stringifyStackTrace(Throwable error) {
    StringWriter writer = new StringWriter();
    error.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
