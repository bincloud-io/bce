package cloud.bangover.text;

import cloud.bangover.text.TextTemplate.Transformer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TextTransformersTest {
  private static final String TEMPLATE_TEXT_WITH_WHITESPACE = "  TEMPLATE            ";
  private static final String TEMPLATE_TEXT_WITHOUT_WHITESPACE = "TEMPLATE";

  @Test
  public void shouldTrimTransformerBeApplied() {
    // Given
    TextTemplate textTemplate = TextTemplates.createBy(TEMPLATE_TEXT_WITH_WHITESPACE);
    // When
    textTemplate = textTemplate.transformBy(TextTransformers.trimming());
    // Then
    Assert.assertEquals(TEMPLATE_TEXT_WITHOUT_WHITESPACE, textTemplate.getTemplateText());
  }

  @Test
  public void shouldChainingTransformerBeApplied() {
    // Given
    TextTemplate textTemplate = TextTemplates.createBy("TEMPLATE_TEXT");
    Transformer firstTransformation = new StubFormatterBasedTransformer("%s.FIRST_TRANSFORMATION");
    Transformer secondTransformation = new StubFormatterBasedTransformer("%s.NEXT_TRANSFORMATION");
    // When
    textTemplate = textTemplate.transformBy(TextTransformers.chain(firstTransformation, secondTransformation));
    // Then
    Assert.assertEquals("TEMPLATE_TEXT.FIRST_TRANSFORMATION.NEXT_TRANSFORMATION", textTemplate.getTemplateText());
  }

  @Test
  public void shouldDeepDiveTransformerBeApplied() {
    // Given
    TextTemplate textTemplate = TextTemplates.createBy("ROOT")
        .withParameter("SIMPLE_PARAM", "SIMPLE_PARAM_VALUE")
        .withParameter("TEMPLATE_PARAM", TextTemplates.createBy("PARAM_TEMPLATE")
        .withParameter("PARAM_TEMPLATE_KEY", "PARAM_TEMPLATE_VALUE"));
    Transformer transformer = new StubParametersConcatinationTransformer();
    // When
    textTemplate = textTemplate.transformBy(TextTransformers.deepDive(transformer));
    // Then
    Assert.assertEquals("ROOT[TEMPLATE_PARAM=PARAM_TEMPLATE[PARAM_TEMPLATE_KEY=PARAM_TEMPLATE_VALUE],SIMPLE_PARAM=SIMPLE_PARAM_VALUE]", textTemplate.getTemplateText());
    Assert.assertEquals("SIMPLE_PARAM_VALUE", textTemplate.getParameters().get("SIMPLE_PARAM"));
    Assert.assertEquals("PARAM_TEMPLATE[PARAM_TEMPLATE_KEY=PARAM_TEMPLATE_VALUE]", textTemplate.getParameters().get("TEMPLATE_PARAM"));
  }
  
  private static class StubParametersConcatinationTransformer implements Transformer {
    @Override
    public TextTemplate transform(TextTemplate sourceTemplate) {
      List<String> tokens = new ArrayList<String>();
      for (Entry<String, Object> entry : sourceTemplate.getParameters().entrySet()) {
        tokens.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
      }
      return TextTemplates.createBy(String.format("%s[%s]", sourceTemplate.getTemplateText(), String.join(",", tokens)), sourceTemplate.getParameters());
    }
  }
}
