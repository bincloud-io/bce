package cloud.bangover.interactions.streaming;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class SubmitIteation<T> {
  private final T data;
  private final Integer size;
}
