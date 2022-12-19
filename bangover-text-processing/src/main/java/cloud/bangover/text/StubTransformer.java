package cloud.bangover.text;

import cloud.bangover.text.TextTemplate.Transformer;

public class StubTransformer implements Transformer {
  private final TextTemplate transformedText;

  public StubTransformer(TextTemplate transformedText) {
    super();
    this.transformedText = transformedText;
  }

  @Override
  public TextTemplate transform(TextTemplate sourceTemplate) {
    return transformedText;
  }
}
