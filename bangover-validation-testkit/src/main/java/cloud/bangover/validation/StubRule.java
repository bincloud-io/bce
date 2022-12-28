package cloud.bangover.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * This class implements the {@link Rule} is usable for rules stubbing.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The testable object type name
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StubRule<T> implements Rule<T> {
  private final Class<T> validatableType;
  private final boolean inverted;
  private final Map<T, Rule<T>> validationDefinitions;
  
  /**
   * Create the {@link StubRule} by validatable object type.
   *
   * @param validatableType The validatable object type
   */
  public StubRule(Class<T> validatableType) {
    this(validatableType, false, new HashMap<T, Rule<T>>());
  }
  
  /**
   * Create stub rule for specified value.
   *
   * @param value The validatable value
   * @param acceptable The acceptable flag, returning by {@link Rule#isAcceptableFor(Object)}
   * @param errorMessages The error messages
   * @return The {@link StubRule} instance
   */
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
