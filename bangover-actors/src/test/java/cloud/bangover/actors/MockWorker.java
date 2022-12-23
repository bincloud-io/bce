package cloud.bangover.actors;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import cloud.bangover.actors.EventLoop.Worker;
import java.time.Instant;
import lombok.Getter;

@Getter
public class MockWorker implements Worker, HistoricalMock<Instant> {
  private final MockHistory<Instant> history = new MockHistory<Instant>();

  @Override
  public void execute() {
    this.history.put(Instant.now());
  }
}
