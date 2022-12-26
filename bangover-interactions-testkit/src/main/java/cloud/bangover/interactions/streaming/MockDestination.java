package cloud.bangover.interactions.streaming;

import cloud.bangover.MockHistory;
import lombok.Getter;

@Getter
public class MockDestination<T> implements Destination<T> {
  private final MockHistory<SubmitIteation<T>> history = new MockHistory<SubmitIteation<T>>();
  private boolean released = false;

  @Override
  public void write(SourceConnection connection, T data, Integer size) {
    this.history.put(new SubmitIteation<T>(data, size));
    connection.receive();
  }

  @Override
  public void release() {
    this.released = true;
  }
}
