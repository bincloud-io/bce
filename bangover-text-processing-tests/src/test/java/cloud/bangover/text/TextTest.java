package cloud.bangover.text;

import cloud.bangover.text.TextTemplate.Transformer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TextTest {
  @Before
  public void setUp() {
    Transformer transformer = new Transformer() {
      @Override
      public TextTemplate transform(TextTemplate sourceTemplate) {
        return TextTemplates.createBy(String.format("%s world!", sourceTemplate.getTemplateText()));
      }
    };
    
    TextProcessor textProcessor = TextProcessor.create()
        .withTransformer(transformer);
    Text.configureProcessor(textProcessor);
  }
  
  @Test
  public void shouldInterpolateText() {
    // Given
    TextTemplate template = TextTemplates.createBy("Hello");
    // When
    String interpolated = Text.interpolate(template);
    // Then
    Assert.assertEquals("Hello world!", interpolated);
  }
  
  @After
  public void tearDown() {
    Text.reset();
  }
}
