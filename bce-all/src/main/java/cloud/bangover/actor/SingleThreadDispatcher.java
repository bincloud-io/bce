package cloud.bangover.actor;

import cloud.bangover.actor.EventLoop.Dispatcher;
import cloud.bangover.actor.EventLoop.Worker;

/**
 * This class is the dispatcher implementation, working in the single thread.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class SingleThreadDispatcher implements Dispatcher {
  @Override
  public final void dispatch(Worker worker) {
    worker.execute();
  }
}
