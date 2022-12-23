package cloud.bangover.interactions.pubsub;

import cloud.bangover.MockHistory;

public class MockSubscriber<M> implements Subscriber<M> {
  private MockHistory<M> history = new MockHistory<M>();
  
  public boolean hasReceivedMessages() {
    return history.isNotEmpty();
  }
  
  public boolean hasReceovedMessage(int queuePosition, M message) {
    return history.hasEntry(queuePosition, message);
  }
  
  @Override
  public void onMessage(M message) {
    this.history.put(message);
  }
}
