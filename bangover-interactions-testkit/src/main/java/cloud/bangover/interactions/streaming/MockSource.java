package cloud.bangover.interactions.streaming;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import lombok.Getter;

public class MockSource<T> implements Source<T> {
  private StubbingQueue<SubmitIteation<T>> stubbingQueue = new StubbingQueue<SubmitIteation<T>>();
  @Getter
  private boolean released = false;  
  
  public StubbingQueueConfigurer<SubmitIteation<T>> configureReadingIterations() {
    return stubbingQueue.configure();
  }

  @Override
  public void read(DestinationConnection<T> connection) {
    if (!stubbingQueue.isEmpty()) {
      SubmitIteation<T> item = stubbingQueue.peek();
      connection.submit(item.getData(), item.getSize());
    } else {      
      connection.complete();
    }
  }

  @Override
  public void release() {
    this.released = true;
  }  
}
