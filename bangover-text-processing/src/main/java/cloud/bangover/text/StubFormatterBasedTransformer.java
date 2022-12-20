package cloud.bangover.text;

import cloud.bangover.text.TextTemplate.Transformer;

public class StubFormatterBasedTransformer implements Transformer {
  private final String format;

  public StubFormatterBasedTransformer(String format) {
    super();
    this.format = format;
  }

  @Override
  public TextTemplate transform(TextTemplate sourceTemplate) {
    return TextTemplates.createBy(String.format(format, sourceTemplate.getTemplateText()),
        sourceTemplate.getParameters());
  }
}
