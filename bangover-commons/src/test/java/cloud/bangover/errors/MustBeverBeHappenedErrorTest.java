package cloud.bangover.errors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MustBeverBeHappenedErrorTest {
  @Test
  public void shouldErrorBeInitializedByMessageText() {
    // When
    MustNeverBeHappenedError error = new MustNeverBeHappenedError("MESSAGE TEXT");
    // Then
    Assert.assertNull(error.getCause());
    Assert.assertEquals("MESSAGE TEXT", error.getMessage());
  }

  @Test
  public void shouldErrorBeInitializedByThrowableObject() {
    // Given
    Throwable initialCause = new Throwable("ERROR_TEXT");
    // When
    MustNeverBeHappenedError error = new MustNeverBeHappenedError(initialCause);
    // Then
    Assert.assertEquals("Error class java.lang.Throwable must never be happened for this case.",
        error.getMessage());
    Assert.assertSame(initialCause, error.getCause());
  }
}
