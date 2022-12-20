package cloud.bangover.text.transformers;

import cloud.bangover.text.TextTemplate;
import cloud.bangover.text.TextTemplates;
import cloud.bangover.text.transformers.TemplateCompilingTransformer.TemplateCompiler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TemplateCompilingTransformerTest {
  @Test
  public void shouldTemplateBeSuccessfullyCompiled() {
    // Given
    TextTemplate template = TextTemplates.createBy("TEMPLATE.TEXT")
        .withParameter("PARAM_1", "VALUE_1")
        .withParameter("PARAM_2", "VALUE_2");
    TemplateCompiler compiler = new StubParamsConcatinationTemplateCompiler();

    // When
    template = template.transformBy(new TemplateCompilingTransformer(compiler));
    // Then
    Assert.assertEquals("TEMPLATE.TEXT[PARAM_1=VALUE_1,PARAM_2=VALUE_2]", template.getTemplateText());
    Assert.assertEquals("VALUE_1", template.getParameters().get("PARAM_1"));
    Assert.assertEquals("VALUE_2", template.getParameters().get("PARAM_2"));
  }

  @Test
  public void shouldTemplateCompilationBeFailed() {
    // Given
    TextTemplate template = TextTemplates.createBy("TEMPLATE.TEXT")
        .withParameter("PARAM_1", "VALUE_1")
        .withParameter("PARAM_2", "VALUE_2");
    TemplateCompiler compiler = new StubFailedCompilationTemplateCompiler();
    // When
    template = template.transformBy(new TemplateCompilingTransformer(compiler));
    // Then
    Assert.assertEquals("", template.getTemplateText());
    Assert.assertEquals("VALUE_1", template.getParameters().get("PARAM_1"));
    Assert.assertEquals("VALUE_2", template.getParameters().get("PARAM_2"));
  }
  
  private static class StubParamsConcatinationTemplateCompiler implements TemplateCompiler {
    @Override
    public String compile(String template, Map<String, String> parameters) {
      List<String> tokens = new ArrayList<String>();
      for (Entry<String, String> entry : parameters.entrySet()) {
        tokens.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
      }
      return String.format("%s[%s]", template, String.join(",", tokens));
    }
  }
  
  private static class StubFailedCompilationTemplateCompiler implements TemplateCompiler {
    @Override
    public String compile(String template, Map<String, String> parameters) {
      return null;
    }
    
  }
}
