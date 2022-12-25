package cloud.bangover.interactions.pubsub;

import cloud.bangover.MockHistory;
import lombok.Getter;

@Getter
public class MockSubscriber<M> implements Subscriber<M> {
  private MockHistory<M> history = new MockHistory<M>();
  
  @Override
  public void onMessage(M message) {
    this.history.put(message);
  }
}
