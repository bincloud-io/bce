package cloud.bangover.actor;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import cloud.bangover.actor.Message.MessageHandleFunction;
import lombok.Getter;

@Getter
public class MockMessageHandleFunction<B> implements MessageHandleFunction<B>, HistoricalMock<B> {
  private final MockHistory<B> history = new MockHistory<B>();
  
  @Override
  public void receive(B body) {
    history.put(body);
  }
}
