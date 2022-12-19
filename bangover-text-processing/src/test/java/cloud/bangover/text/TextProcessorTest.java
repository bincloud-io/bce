package cloud.bangover.text;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TextProcessorTest {
  @Test
  public void shouldMessageBeInterpolated() {
    // When
    TextTemplate template = TextTemplates.createBy("Hello");
    TextProcessor textProcessor = TextProcessor.create()
        .withTransformer(new StubPredifinedResultTransformer(TextTemplates.createBy("Hello world!")));
    // Then
    Assert.assertEquals("Hello world!", textProcessor.interpolate(template));
  }
}
