package cloud.bangover.actor;

import cloud.bangover.actor.EventLoop.Worker;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class WaitableWorker implements Worker {
  private final Worker originalWorker;
  private final CountDownLatch workersExecutionWaiterLatch;

  @Override
  public void execute() {
    originalWorker.execute();
    workersExecutionWaiterLatch.countDown();
  }
}
