package cloud.bangover.text.transformers;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplate.Transformer;

public class StubTextTransformer implements Transformer {
  private final StubbingQueue<TextTemplate> stubbingQueue = new StubbingQueue<TextTemplate>();
  
  public StubbingQueueConfigurer<TextTemplate> configure() {
    return stubbingQueue.configure();
  }

  @Override
  public TextTemplate transform(TextTemplate sourceTemplate) {
    return stubbingQueue.peek();
  }
}
