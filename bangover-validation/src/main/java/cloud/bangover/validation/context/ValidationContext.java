package cloud.bangover.validation.context;

import cloud.bangover.validation.ErrorMessage;
import cloud.bangover.validation.Rule;
import cloud.bangover.validation.ValidationGroup;
import cloud.bangover.validation.ValidationService;
import cloud.bangover.validation.ValidationState;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This interface declares the contract for validation flow imperative handling. It contains the set
 * of operations, allows affect to the validation process and aggregate the validation result into
 * the assigned context.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationContext {
  private final ValidationGroup group;
  private final DerivationPolicy derivation;
  private final ValidationState validationState;

  public ValidationContext() {
    this(ValidationGroup.UNGROUPED, DerivationPolicy.DERIVE_GROUPES, new ValidationState());
  }

  /**
   * Create validation service for the validation context.
   *
   * @return The validation service
   */
  public static ValidationService createValidationService() {
    return new ValidationService() {
      @Override
      public <V> ValidationState validate(V validatable) {
        if (validatable instanceof Validatable) {
          return validate((Validatable) validatable);
        }
        return new ValidationState();
      }

      private ValidationState validate(Validatable validatable) {
        return validatable.validate(new ValidationContext()).getState();
      }
    };
  }

  /**
   * Get the result validation state.
   *
   * @return The validation state
   */
  public ValidationState getState() {
    return this.derivation.deriveState(group, validationState);
  }

  /**
   * Validate an object, implementing the {@link Validatable} interface.
   *
   * @param validatable The validatable object.
   * @return The derived context
   */
  public ValidationContext validate(Validatable validatable) {
    return validate(ValidationGroup.UNGROUPED, validatable);
  }

  /**
   * Validate an object, implementing the {@link Validatable} interface using specified derivation
   * policy.
   *
   * @param validatable      The validatable object
   * @param derivationPolicy The derivation policy
   * @return The derived context
   */
  public ValidationContext validate(Validatable validatable, DerivationPolicy derivationPolicy) {
    return validate(ValidationGroup.UNGROUPED, validatable, derivationPolicy);
  }

  /**
   * Validate an object, implementing the {@link Validatable} interface, grouped by a specified
   * group.
   *
   * @param groupName   The validation group
   * @param validatable The validatable object
   * @return The derived context
   */
  public ValidationContext validate(String groupName, Validatable validatable) {
    return validate(ValidationGroup.createFor(groupName), validatable);
  }

  /**
   * Validate an object, implementing the {@link Validatable} interface, grouped by a specified
   * group, using specified derivation policy.
   *
   * @param groupName        The validation group
   * @param validatable      The validatable object
   * @param derivationPolicy The derivation policy
   * @return The derived context
   */
  public ValidationContext validate(String groupName, Validatable validatable,
      DerivationPolicy derivationPolicy) {
    return validate(ValidationGroup.createFor(groupName), validatable, derivationPolicy);
  }

  /**
   * Validate all objects inside a collection, implementing the {@link Validatable} interface,
   * grouped by a specified group.
   *
   * @param <T>        The collection item type name
   * @param groupName  The validation group
   * @param collection The validatable collection
   * @return The derived context
   */
  public <T> ValidationContext validate(String groupName, Collection<T> collection) {
    Long index = 0L;
    ValidationContext context = this;
    for (T value : collection) {
      if (value instanceof Validatable) {
        context = context.validate(createIndexedGroup(groupName, index), (Validatable) value);
      }
      index++;
    }
    return context;
  }
  
  private ValidationContext validate(ValidationGroup group, Validatable validatable) {
    return validate(group, validatable, DerivationPolicy.DERIVE_STATE);
  }

  private ValidationContext validate(ValidationGroup group, Validatable validatable,
      DerivationPolicy derivationPolicy) {
    ValidationContext subContext =
        validatable.validate(new ValidationContext(group, derivationPolicy, new ValidationState()));
    return merge(subContext);
  }

  /**
   * Append ungrouped rule checking for the value, obtained by the provider.
   *
   * @param <T>           The value type name
   * @param valueProvider The under validation value provider
   * @param rule          The rule under validation
   * @return The validation context
   */
  public <T> ValidationContext withRule(ValueProvider<T> valueProvider, Rule<T> rule) {
    return withRule(valueProvider, rule, (context, errors) -> withErrors(errors));
  }

  /**
   * Append ungrouped rule checking for the value.
   *
   * @param <T>   The value type name
   * @param value The under validation value
   * @param rule  The rule under validation
   * @return The validation context
   */
  public <T> ValidationContext withRule(T value, Rule<T> rule) {
    return this.withRule((ValueProvider<T>) () -> value, rule);
  }

  private <T> ValidationContext withRule(ValueProvider<T> valueProvider, Rule<T> rule,
      ContextErrorAppender errorAppender) {
    T value = valueProvider.getValue();
    if (rule.isAcceptableFor(value)) {
      Collection<ErrorMessage> errors = rule.check(value);
      if (!errors.isEmpty()) {
        return errorAppender.withError(this, errors.toArray(new ErrorMessage[errors.size()]));
      }
    }
    return this;
  }
  
  /**
   * Append grouped rule checking for the value, obtained by the provider.
   *
   * @param <T>           The value type name
   * @param groupName     The validation group name
   * @param valueProvider The under validation value provider
   * @param rule          The rule under validation
   * @return The validation context
   */
  public <T> ValidationContext withRule(String groupName, ValueProvider<T> valueProvider,
      Rule<T> rule) {
    return withRule(valueProvider, rule, (context, errors) -> withErrors(groupName, errors));
  }
  
  /**
   * Append grouped rule checking for the value.
   *
   * @param <T>       The value type name
   * @param groupName The validation group name
   * @param value     The under validation value
   * @param rule      The rule under validation
   * @return The validation context
   */
  public <T> ValidationContext withRule(String groupName, T value, Rule<T> rule) {
    return this.withRule(groupName, (ValueProvider<T>) () -> value, rule);
  }

  /**
   * Append the ungrouped error to the validation state.
   *
   * @param errors The error messages
   * @return The validation context
   */
  public ValidationContext withErrors(ErrorMessage... errors) {
    return withErrors(errors, (state, error) -> state.withUngrouped(error));
  }

  /**
   * Append the grouped error to the validation state.
   *
   * @param groupName The group name
   * @param errors    The error messages
   * @return The validation context
   */
  public ValidationContext withErrors(String groupName, ErrorMessage... errors) {
    ValidationGroup group = ValidationGroup.createFor(groupName);
    return withErrors(errors, (state, error) -> state.withGrouped(group, error));
  }

  private ValidationContext withErrors(ErrorMessage[] errors, StateErrorAppender errorAppender) {
    ValidationState resultState = this.validationState;
    for (ErrorMessage error : errors) {
      resultState = errorAppender.withError(resultState, error);
    }
    return new ValidationContext(this.group, this.derivation, resultState);
  }

  private ValidationContext merge(ValidationContext subContext) {
    return new ValidationContext(this.group, this.derivation,
        this.validationState.merge(subContext.getState()));
  }

  private ValidationGroup createIndexedGroup(String groupName, Long index) {
    ValidationGroup indexGroup = ValidationGroup.createFor(String.format("[%s]", index));
    return ValidationGroup.createFor(groupName).deriveWith(indexGroup);
  }

  private interface ContextErrorAppender {
    public ValidationContext withError(ValidationContext currentState, ErrorMessage... errors);
  }

  private interface StateErrorAppender {
    public ValidationState withError(ValidationState currentState, ErrorMessage error);
  }

  /**
   * This class enumerates the variants of derivation policies.
   *
   * @author Dmitry Mikhaylenko
   *
   */
  public static enum DerivationPolicy {
    /**
     * Derive only groups of the grouped messages excluding ungrouped.
     */
    DERIVE_GROUPES {
      @Override
      protected ValidationState deriveState(ValidationGroup group, ValidationState state) {
        return state.asDerivedFrom(group);
      }
    },
    /**
     * Derive all state including ungrouped(it will be represented as a grouped messages of the base
     * group).
     */
    DERIVE_STATE {
      @Override
      protected ValidationState deriveState(ValidationGroup group, ValidationState state) {
        return state.asSubgroup(group);
      }
    };

    protected abstract ValidationState deriveState(ValidationGroup group, ValidationState state);
  }
}