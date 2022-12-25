package cloud.bangover.actors;

import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExecutorServiceDispatcherTest {
  @Test
  public void shouldTestExecutorServiceDispatcher() {
    DispatcherTestCase.of(ExecutorServiceDispatcher.createFor(Executors.newFixedThreadPool(4))).run();
  }
}
