package cloud.bangover.actors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SingleThreadDispatcherTest {
  @Test
  public void shouldTestSingleThreadDispatcher() {
    DispatcherTestCase.of(new SingleThreadDispatcher()).run();
  }
}
