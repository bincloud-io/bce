package cloud.bangover.validation;

import cloud.bangover.BoundedContextId;
import cloud.bangover.errors.ApplicationException;
import cloud.bangover.errors.ErrorDescriptor.ErrorCode;
import cloud.bangover.errors.ErrorDescriptor.ErrorSeverity;
import cloud.bangover.text.TextProcessor;
import cloud.bangover.text.TextTemplates;
import cloud.bangover.text.transformers.StubTextTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ErrorDescriptionGeneratorTest {
  private static final String PROCESSED_MESSAGE = "Processed message";
  private static final String DEFAULT_MESSAGE = "Something went wrong!!!";

  @Test
  public void shouldGenerateMessageTemplateUsingProcessor() {
    // Given
    StubTextTransformer transformer = new StubTextTransformer();
    transformer.configure().withDefault(TextTemplates.createBy(PROCESSED_MESSAGE));
    // When
    TextProcessor textProcessor = TextProcessor.create().withTransformer(transformer);
    ApplicationException error = new ApplicationException( BoundedContextId.createFor("CTX"), ErrorSeverity.BUSINESS, ErrorCode.createFor(1L), DEFAULT_MESSAGE);

    // Then
    Assert.assertEquals(PROCESSED_MESSAGE, ErrorDescriptionGenerator.of(textProcessor, error).generateDescription());
  }

  @Test
  public void shouldGenerateDefaultDescription() {
    // When
    TextProcessor textProcessor = TextProcessor.create();
    ApplicationException error = new ApplicationException( BoundedContextId.createFor("CTX"), ErrorSeverity.BUSINESS, ErrorCode.createFor(1L), DEFAULT_MESSAGE);

    // Then
    Assert.assertEquals(error.getMessage(), ErrorDescriptionGenerator.of(textProcessor, error).generateDescription());
  }
}
