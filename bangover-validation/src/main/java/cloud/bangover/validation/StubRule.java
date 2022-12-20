package cloud.bangover.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StubRule<T> implements Rule<T> {
  private final Map<T, Rule<T>> validationDefinitions;
  private final Class<T> validatableType;
  private final boolean inverted;

  public StubRule(Class<T> validatableType) {
    this(validatableType, false, new HashMap<T, Rule<T>>());
  }

  private StubRule(Class<T> validatableType, boolean inverted, Map<T, Rule<T>> definitions) {
    super();
    this.validationDefinitions = definitions;
    this.validatableType = validatableType;
    this.inverted = inverted;
  }

  public StubRule<T> stubFor(T value, boolean acceptable, Collection<ErrorMessage> errorMessages) {
    this.validationDefinitions.put(value, new RuleDefinition(acceptable, errorMessages));
    return this;
  }

  @Override
  public boolean isAcceptableFor(T value) {
    return definitionOf(value).isAcceptableFor(value);
  }

  @Override
  public Collection<ErrorMessage> check(T value) {
    return definitionOf(value).check(value);
  }

  @Override
  public Rule<T> invert() {
    return new StubRule<T>(this.validatableType, !this.inverted, this.validationDefinitions);
  }

  private Rule<T> definitionOf(T value) {
    return validationDefinitions.get(value);
  }

  private class RuleDefinition implements Rule<T> {
    private final boolean acceptable;
    private final Collection<ErrorMessage> errorMessages;
    private final boolean inverted;

    public RuleDefinition(boolean acceptable, Collection<ErrorMessage> errorMessages) {
      this(acceptable, errorMessages, false);
    }

    public RuleDefinition(boolean acceptable, Collection<ErrorMessage> errorMessages,
        boolean inverted) {
      super();
      this.acceptable = acceptable;
      this.errorMessages = errorMessages;
      this.inverted = inverted;
    }

    @Override
    public boolean isAcceptableFor(T value) {
      return acceptable;
    }

    @Override
    public Collection<ErrorMessage> check(T value) {
      if (StubRule.this.inverted || this.inverted) {
        return Collections.emptyList();
      }
      return errorMessages;
    }

    @Override
    public Rule<T> invert() {
      return new RuleDefinition(inverted, errorMessages, !this.inverted);
    }
  }
}
