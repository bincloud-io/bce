package cloud.bangover.locale;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import java.util.Locale;

/**
 * This class is a stub {@link LocaleProvider} implementation. This class should be used only in the
 * testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class StubLocaleProvider implements LocaleProvider {
  private final StubbingQueue<Locale> stubbingQueue;

  public StubLocaleProvider(Locale locale) {
    super();
    this.stubbingQueue = new StubbingQueue<Locale>(locale);
  }

  /**
   * Configure {@link StubLocaleProvider} stubbing.
   *
   * @return The {@link StubbingQueueConfigurer}
   */
  public StubbingQueueConfigurer<Locale> configure() {
    return this.stubbingQueue.configure();
  }

  @Override
  public Locale getLocale() {
    return this.stubbingQueue.peek();
  }

}
