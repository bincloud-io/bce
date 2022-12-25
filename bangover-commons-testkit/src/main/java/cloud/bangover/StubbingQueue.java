package cloud.bangover;

import java.util.LinkedList;

/**
 * This object keeps the stubbing results. It may be used in cases if we want to stub returning
 * results depend on invocation times. For example if we want to the stubbed method would return
 * predefined values by specified order on each invocation we could configure stubbing queue of
 * results for every call and peek new entry. It should be used only for testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The stubbing queue element type name
 */
public class StubbingQueue<T> {
  private LinkedList<T> entriesQueue;
  private T defaultValue;

  /**
   * Create the stubbing queue by default.
   */
  public StubbingQueue() {
    this(null);
  }

  /**
   * Create the stubbing queue with predefined default value (if the {@link StubbingQueue} is empty
   * the predefined value will be returned.
   *
   * @param defaultValue The default value
   */
  public StubbingQueue(T defaultValue) {
    this(new LinkedList<T>(), defaultValue);
  }

  private StubbingQueue(LinkedList<T> entriesQueue, T defaultValue) {
    super();
    this.entriesQueue = entriesQueue;
    this.defaultValue = defaultValue;
  }

  /**
   * Configure stubbing queue.
   *
   * @return The {@link StubbingQueueConfigurer}
   */
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

  /**
   * Check that the {@link StubbingQueue} is empty.
   *
   * @return True if is empty and false otherwise
   */
  public boolean isEmpty() {
    return entriesQueue.isEmpty();
  }

  /**
   * Peek value from queue head or return default value if queue is empty.
   *
   * @return The value from head of queue or default value.
   */
  public T peek() {
    if (!entriesQueue.isEmpty()) {
      return entriesQueue.poll();
    }
    return defaultValue;
  }

  /**
   * This interface describes the {@link StubbingQueue} configurer.
   *
   * @author Dmitry Mikhaylenko
   *
   * @param <T> The stubbing queue element type name
   */
  public interface StubbingQueueConfigurer<T> {
    /**
     * Append entry to the {@link StubbingQueue} tail.
     *
     * @param entry The entry
     * @return The {@link StubbingQueueConfigurer}
     */
    public StubbingQueueConfigurer<T> withNextEntry(T entry);

    /**
     * Predefine the default value.
     *
     * @param defaultValue The default value
     * @return The {@link StubbingQueueConfigurer}
     */
    public StubbingQueueConfigurer<T> withDefault(T defaultValue);
  }
}
