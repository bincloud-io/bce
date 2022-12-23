package cloud.bangover.domain;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import lombok.Getter;

@Getter
public class MockEventListener<T> implements EventListener<T>, HistoricalMock<T> {
  private final MockHistory<T> history = new MockHistory<T>();
  
  @Override
  public void onEvent(T event) {
    history.put(event);
  }
}