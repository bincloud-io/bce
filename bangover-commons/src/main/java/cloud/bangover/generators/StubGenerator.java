package cloud.bangover.generators;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;

public class StubGenerator<T> implements Generator<T> {
  private final StubbingQueue<T> stubbingQueue;

  public StubGenerator(T value) {
    super();
    this.stubbingQueue = new StubbingQueue<T>(value);
  }
  
  public StubbingQueueConfigurer<T> configure() {
    return stubbingQueue.configure();
  }

  @Override
  public T generateNext() {
    return stubbingQueue.peek();
  }
}
