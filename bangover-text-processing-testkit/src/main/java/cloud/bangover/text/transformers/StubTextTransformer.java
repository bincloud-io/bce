package cloud.bangover.text.transformers;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplate.Transformer;

/**
 * This class is a stub {@link Transformer} implementation. This class should be used only in the
 * testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class StubTextTransformer implements Transformer {
  private final StubbingQueue<TextTemplate> stubbingQueue = new StubbingQueue<TextTemplate>();

  /**
   * Configure a {@linkplain StubTextTransformer} stubbing.
   *
   * @return The {@link StubbingQueueConfigurer}
   */
  public StubbingQueueConfigurer<TextTemplate> configure() {
    return stubbingQueue.configure();
  }

  @Override
  public TextTemplate transform(TextTemplate sourceTemplate) {
    return stubbingQueue.peek();
  }
}
