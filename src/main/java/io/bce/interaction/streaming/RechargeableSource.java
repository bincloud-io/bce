package io.bce.interaction.streaming;

import java.util.Optional;
import java.util.Queue;

import lombok.RequiredArgsConstructor;

public class RechargeableSource<T> implements Source<T> {
  private final Queue<Source<T>> sourcesQueue;
  private Optional<Source<T>> current = Optional.empty();

  public RechargeableSource(Queue<Source<T>> sourcesQueue) {
    super();
    this.sourcesQueue = sourcesQueue;
    recharge();
  }

  @Override
  public void read(DestinationConnection<T> connection) {
    current.get().read(new RechargeableConnection(connection));
  }

  @Override
  public void release() {
  }

  private void recharge() {
    this.current = Optional.ofNullable(sourcesQueue.poll());
  }

  private boolean sourcesQueueIsNotEmpty() {
    return !sourcesQueue.isEmpty();
  }

  @RequiredArgsConstructor
  private class RechargeableConnection implements DestinationConnection<T> {
    private final DestinationConnection<T> original;

    @Override
    public void submit(T data, Integer size) {
      original.submit(data, size);
    }

    @Override
    public void complete() {
      if (sourcesQueueIsNotEmpty()) {
        readNextSourceFromQueue();
      } else {
        original.complete();
      }
    }

    private void readNextSourceFromQueue() {
      recharge();
      read(this);
    }
  }
}
