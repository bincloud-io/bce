package cloud.bangover.generators;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;

/**
 * This class is a stub {@link Generator} implementation. The {@link StubGenerator} should be used
 * only for a testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The generating value
 */
public class StubGenerator<T> implements Generator<T> {
  private final StubbingQueue<T> stubbingQueue;

  /**
   * Create a {@link StubGenerator} by default.
   */
  public StubGenerator() {
    this(null);
  }

  /**
   * Create a {@link StubGenerator} with a default generating value.
   *
   * @param value The default value
   */
  public StubGenerator(T value) {
    super();
    this.stubbingQueue = new StubbingQueue<T>(value);
  }

  /**
   * Configure stubbing.
   *
   * @return The {@link StubbingQueueConfigurer} for a stub object.
   */
  public StubbingQueueConfigurer<T> configure() {
    return stubbingQueue.configure();
  }

  @Override
  public T generateNext() {
    return stubbingQueue.peek();
  }
}
