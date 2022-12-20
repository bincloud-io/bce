package cloud.bangover.locale;

import java.util.Locale;

/**
 * This interface describes the contract for the locale obtaining. This component should
 * guaranteed return the locale which should be used for the locale resolving.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public interface LocaleProvider {
  /**
   * Get the locale.
   *
   * @return The locale
   */
  public Locale getLocale();
}