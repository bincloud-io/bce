package cloud.bangover;

import java.util.LinkedList;

public class StubbingQueue<T> {
  private LinkedList<T> entriesQueue;
  private T defaultValue;

  public StubbingQueue() {
    this(null);
  }

  public StubbingQueue(T defaultValue) {
    this(new LinkedList<T>(), defaultValue);
  }

  private StubbingQueue(LinkedList<T> entriesQueue, T defaultValue) {
    super();
    this.entriesQueue = entriesQueue;
    this.defaultValue = defaultValue;
  }
  
  public StubbingQueueConfigurer<T> configure() {
    return new StubbingQueueConfigurer<T>() {
      @Override
      public StubbingQueueConfigurer<T> withNextEntry(T entry) {
        StubbingQueue.this.entriesQueue.add(entry);
        return this;
      }

      @Override
      public StubbingQueueConfigurer<T> withDefault(T defaultEntry) {
        StubbingQueue.this.defaultValue = defaultEntry;
        return this;
      }
    };
  }
  
  public boolean isEmpty() {
    return entriesQueue.isEmpty();
  }

  public T peek() {
    if (!entriesQueue.isEmpty()) {
      return entriesQueue.poll();
    }
    return defaultValue;
  }

  public interface StubbingQueueConfigurer<T> {
    public StubbingQueueConfigurer<T> withNextEntry(T entry);

    public StubbingQueueConfigurer<T> withDefault(T defaultEntry);
  }
}
