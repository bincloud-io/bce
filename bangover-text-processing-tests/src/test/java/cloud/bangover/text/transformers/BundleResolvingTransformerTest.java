package cloud.bangover.text.transformers;

import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplates;
import cloud.bangover.text.transformers.BundleResolvingTransformer.BundleResolver;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BundleResolvingTransformerTest {
  @Test
  public void shouldBundleBeResolved() {
    // Given
    TextTemplate template = TextTemplates.createBy("TEMPLATE.ID");
    BundleResolver resolver = new StubPredefinedResultBundleResolver(Optional.of("RESOLVED_TEXT"));
    // When
    template = template.transformBy(new BundleResolvingTransformer(resolver));
    // Then
    Assert.assertEquals("RESOLVED_TEXT", template.getTemplateText());
  }
  
  @Test
  public void shouldBundleNotToBeResolved() {
    // Given
    TextTemplate template = TextTemplates.createBy("TEMPLATE.ID");
    BundleResolver resolver = new StubPredefinedResultBundleResolver(Optional.empty());
    // When
    template = template.transformBy(new BundleResolvingTransformer(resolver));
    // Then
    Assert.assertEquals("TEMPLATE.ID", template.getTemplateText());    
  }
  
  @Test
  public void shouldBindleResolverProtectBeProtectedOfThrownError() {
    // Given
    TextTemplate template = TextTemplates.createBy("TEMPLATE.ID");
    BundleResolver resolver = new ThrowErrorBundleResolver(new RuntimeException("SOMETHING WENT WRONG"));
    // When
    template = template.transformBy(new BundleResolvingTransformer(resolver));
    // Then
    Assert.assertEquals("TEMPLATE.ID", template.getTemplateText());
  }
  
  @RequiredArgsConstructor
  private static class StubPredefinedResultBundleResolver implements BundleResolver {
    private final Optional<String> predefinedResult;
    
    @Override
    public Optional<String> resolve(String bundleKey) {
      return predefinedResult;
    }
  }
  
  private static class ThrowErrorBundleResolver implements BundleResolver {
    private final RuntimeException error;
    
    public ThrowErrorBundleResolver(RuntimeException error) {
      super();
      this.error = error;
    }
    
    @Override
    public Optional<String> resolve(String bundleKey) {
      throw error;
    }
  }
}
