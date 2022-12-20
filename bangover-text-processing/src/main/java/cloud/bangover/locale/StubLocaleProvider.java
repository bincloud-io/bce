package cloud.bangover.locale;

import java.util.Locale;

public class StubLocaleProvider implements LocaleProvider {
  private final Locale locale;
  
  public StubLocaleProvider(Locale locale) {
    super();
    this.locale = locale;
  }
  
  @Override
  public Locale getLocale() {
    return this.locale;
  }

}
