package io.bce.validation;

import java.util.Optional;

import io.bce.validation.ValidationContext.Rule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TypeSafeRule<T> implements Rule<T> {
  protected final Class<T> type;

  @Override
  public boolean isAcceptableFor(T value) {
    return Optional.ofNullable(value).map(v -> type.isInstance(v)).orElse(true);
  }
}
