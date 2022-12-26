package cloud.bangover.interactions.polling;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import java.util.Collection;
import java.util.Collections;

public class StubBatchPoller<D> implements BatchPoller<D> {
  private final StubbingQueue<Collection<D>> pollingIterations =
      new StubbingQueue<Collection<D>>(Collections.emptyList());

  public StubbingQueueConfigurer<Collection<D>> configureIterations() {
    return pollingIterations.configure();
  }

  @Override
  public Collection<D> poll() {
    return pollingIterations.peek();
  }
}
