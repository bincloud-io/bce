package cloud.bangover.text.compilers;

import cloud.bangover.text.transformers.TemplateCompilingTransformer.TemplateCompiler;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HandlebarsTemplateCompilerTest {
  private static final String MUSTACHE_TEMPLATE = "{{greetingWord}}, {{objectWord}}!!!";
  private static final String GREETING_WORD = "Hello";
  private static final String OBJECT_WORD = "World";
  
  @Test
  public void shouldCompileTemplate() {
    // When
    Map<String, String> parameters = new HashMap<>();
    parameters.put("greetingWord", GREETING_WORD);
    parameters.put("objectWord", OBJECT_WORD);
    TemplateCompiler templateCompiler = new HandlebarsTemplateCompiler();
    // Then
    Assert.assertEquals("Hello, World!!!", templateCompiler.compile(MUSTACHE_TEMPLATE, parameters));
  }
  
}
