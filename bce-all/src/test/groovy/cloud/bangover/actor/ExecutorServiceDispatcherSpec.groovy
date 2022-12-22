package cloud.bangover.actor

import cloud.bangover.actor.ExecutorServiceDispatcher
import cloud.bangover.actor.EventLoop.Dispatcher
import java.util.concurrent.Executors

class ExecutorServiceDispatcherSpec extends DispatcherSpecification {
  @Override
  protected Dispatcher getDispatcher() {
    return ExecutorServiceDispatcher.createFor(Executors.newFixedThreadPool(4))
  }
}
