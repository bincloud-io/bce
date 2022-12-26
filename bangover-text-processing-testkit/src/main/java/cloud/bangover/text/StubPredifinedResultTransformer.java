package cloud.bangover.text;

import cloud.bangover.text.TextTemplate.Transformer;
import lombok.RequiredArgsConstructor;

/**
 * This class is a stub {@link Transformer} implementation. This class should be used only in the
 * testing goals.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@RequiredArgsConstructor
public class StubPredifinedResultTransformer implements Transformer {
  private final TextTemplate transformedText;

  @Override
  public TextTemplate transform(TextTemplate sourceTemplate) {
    return transformedText;
  }
}
