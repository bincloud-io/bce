package cloud.bangover.actors;

import cloud.bangover.actors.EventLoop.Dispatcher;
import cloud.bangover.actors.EventLoop.Worker;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;

@RequiredArgsConstructor(staticName = "of")
public class DispatcherTestCase implements Runnable {
  private final Dispatcher dispatcher;

  @Override
  public void run() {
    // Given
    MockWorker firstWorker = new MockWorker();
    MockWorker secondWorker = new MockWorker();
    MockWorker thirdWorker = new MockWorker();
    MockWorker fourthWorker = new MockWorker();
    CountDownLatch dispatchersAwaitLatch = new CountDownLatch(4);
    WorkersWaiter waiter = new WorkersWaiter(dispatchersAwaitLatch);
    Collection<Worker> workers = Arrays.asList(
      firstWorker,
      secondWorker,
      thirdWorker,
      fourthWorker
    );

    // When
    dispatchAllWorkers(dispatchersAwaitLatch, dispatcher, workers);
    waiter.await();

    // Then
    Assert.assertTrue(firstWorker.getHistory().hasEntries());
    Assert.assertTrue(secondWorker.getHistory().hasEntries());
    Assert.assertTrue(thirdWorker.getHistory().hasEntries());
    Assert.assertTrue(fourthWorker.getHistory().hasEntries());
  }

  private void dispatchAllWorkers(CountDownLatch dispatcherAwaitLatch, Dispatcher dispatcher,
      Collection<Worker> workers) {
    for (Worker worker : workers) {
      dispatcher.dispatch(new WaitableWorker(worker, dispatcherAwaitLatch));
    }
  }
}
