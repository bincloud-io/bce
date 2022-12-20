package cloud.bangover.text.resolvers;

import cloud.bangover.locale.LocaleProvider;
import cloud.bangover.locale.StubLocaleProvider;
import cloud.bangover.text.transformers.BundleResolvingTransformer.BundleResolver;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ResourceBundleResolverTest {
  private static final String TEST_BUNDLE_NAME = "bundles/simple-bundle";
  private final LocaleProvider localeProvider = new StubLocaleProvider(new Locale("ru", "RU"));
  private final String templateId;
  private final Optional<String> resolvedText;
  
  public ResourceBundleResolverTest(String templateId, Optional<String> resolvedText) {
    super();
    this.templateId = templateId;
    this.resolvedText = resolvedText;
  }
  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {"unknown", Optional.empty()},
      {"message.empty", Optional.of("")},
      {"message.default", Optional.of("DEFAULT MESSAGE")},
      {"message.localized" , Optional.of("СООБЩЕНИЕ С ЛОКАЛИЗАЦИЕЙ")},
    });
  }
  
  @Test
  public void shouldResolveBundle() {
    // When
    BundleResolver bundleResolver = new ResourceBundleResolver(localeProvider)
        .withResourceBundle(TEST_BUNDLE_NAME);
    // Then
    Assert.assertEquals(resolvedText, bundleResolver.resolve(templateId));
  }

}
