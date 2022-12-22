package cloud.bangover.actor

import cloud.bangover.actor.SingleThreadDispatcher
import cloud.bangover.actor.EventLoop.Dispatcher

class SingleThreadDispatcherSpec extends DispatcherSpecification {

  @Override
  protected Dispatcher getDispatcher() {
    return new SingleThreadDispatcher()
  }
}
