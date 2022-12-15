package cloud.bangover.text.resolvers

import cloud.bangover.text.resolvers.ResourceBundleResolver.LocaleProvider
import cloud.bangover.text.transformers.BundleResolvingTransformer.BundleResolver
import spock.lang.Specification

class ResourceBundleResolverSpec extends Specification {
  private static final String TEST_BUNDLE_NAME = "bundles/simple-bundle"

  private LocaleProvider localeProvider;

  def setup() {
    this.localeProvider = Stub(LocaleProvider)
    this.localeProvider.getLocale() >> new Locale("ru", "RU")
  }

  def "Scenario: resolve message template"() {
    given: "The resource bundle resolver"
    BundleResolver bundleResolver = new ResourceBundleResolver(localeProvider)
        .withResourceBundle(TEST_BUNDLE_NAME)

    expect: "The bundle id will resolved using resource bundle"
    bundleResolver.resolve(templateId) == resolvedText

    where:
    templateId           | resolvedText
    "unknown"            | Optional.empty()
    "message.empty"      | Optional.of("")
    "message.default"    | Optional.of("DEFAULT MESSAGE")
    "message.localized"  | Optional.of("СООБЩЕНИЕ С ЛОКАЛИЗАЦИЕЙ")
  }
}
