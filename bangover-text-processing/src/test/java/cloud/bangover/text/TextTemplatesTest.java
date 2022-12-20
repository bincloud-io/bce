package cloud.bangover.text;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TextTemplatesTest {
  private static final Object RANDOM_OBJECT = new Object();
  private static final String RANDOM_STRING = "THE RANDOM STRING";
  private static final String CUSTOMIZED_NULL_PATTERN = "NULL";
  private static final String CONVERTED_STRING = String.format("CONVERTED(%s);", RANDOM_STRING);

  private final TextTemplate textTemplate;
  private final String expectedTextTemplate;
  private final Map<String, Object> expectedParameters;
  private final String expectedStringified;

  public TextTemplatesTest(TextTemplate textTemplate, String expectedTextTemplate,
      Map<String, Object> expectedParameters, String expectedStringified) {
    super();
    this.textTemplate = textTemplate;
    this.expectedTextTemplate = expectedTextTemplate;
    this.expectedParameters = expectedParameters;
    this.expectedStringified = expectedStringified;
  }

  @Parameters
  public static Collection<Object[]> data() {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("key", "value");
    return Arrays
        .asList(new Object[][] { { TextTemplates.emptyTemplate(), "", Collections.emptyMap(), "" },
            { TextTemplates.createBy(RANDOM_OBJECT), RANDOM_OBJECT.toString(),
                Collections.emptyMap(), RANDOM_OBJECT.toString() },
            { TextTemplates.createBy(RANDOM_STRING), RANDOM_STRING, Collections.emptyMap(),
                RANDOM_STRING },
            { TextTemplates.createBy(RANDOM_STRING, parameters), RANDOM_STRING, parameters,
                RANDOM_STRING },
            { TextTemplates.wrap(TextTemplates.createBy(RANDOM_STRING, parameters)), RANDOM_STRING,
                parameters, RANDOM_STRING },
            { TextTemplates.createBy(RANDOM_STRING, parameters)
                .transformBy(new StubFormatterBasedTransformer("CONVERTED(%s);")), CONVERTED_STRING,
                parameters, CONVERTED_STRING },
            { TextTemplates.createBy(null, null), "", Collections.emptyMap(), "" },
            { TextTemplates.createBy(null, null).withNullPattern(CUSTOMIZED_NULL_PATTERN),
                CUSTOMIZED_NULL_PATTERN, Collections.emptyMap(), CUSTOMIZED_NULL_PATTERN },
            { TextTemplates.createBy(RANDOM_STRING).withParameter("key", "value"), RANDOM_STRING,
                parameters, RANDOM_STRING }, });
  }

  @Test
  public void shouldTemplateBeCreatedCorrectly() {
    // Then
    Assert.assertEquals(expectedTextTemplate, textTemplate.getTemplateText());
    Assert.assertEquals(expectedParameters, textTemplate.getParameters());
    Assert.assertEquals(expectedStringified, textTemplate.toString());
  }
}
