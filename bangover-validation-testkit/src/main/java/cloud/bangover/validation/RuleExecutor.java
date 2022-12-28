package cloud.bangover.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This class executes rule and returns report about rule execution completing.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <V> The validatable value type
 */
@RequiredArgsConstructor
public class RuleExecutor<V> {
  private final V validatable;
  private final RuleProvider<V> validationRule;

  /**
   * Create rule executor for specified rule and validation object.
   *
   * @param <V>         The validation value
   * @param validatable The validation object
   * @param rule        The validation rule
   * @return The rule executor
   */
  public static <V> RuleExecutor<V> of(V validatable, Rule<V> rule) {
    return new RuleExecutor<V>(validatable, new RuleProvider<V>() {
      @Override
      public Rule<V> provideRule() {
        return rule;
      }
    });
  }

  /**
   * Execute rule.
   *
   * @return The rule execution report
   */
  public RuleExecutionReport execute() {
    try {
      Rule<V> rule = validationRule.provideRule();
      if (rule.isAcceptableFor(validatable)) {
        return new RuleExecutionReport(true, rule.check(validatable));
      } else {
        return new RuleExecutionReport(false, Collections.emptyList());
      }
    } catch (Throwable error) {
      return new RuleExecutionReport(error);
    }
  }

  /**
   * This interface describes the component, providing the rule.
   *
   * @author Dmitry Mikhaylenko
   *
   * @param <V> The validatable value type
   */
  public interface RuleProvider<V> {
    /**
     * Provide rule.
     *
     * @return The created rule
     */
    public Rule<V> provideRule();
  }

  /**
   * This class keeps report information about the rule execution.
   *
   * @author Dmitry Mikhaylenko
   *
   */
  public static final class RuleExecutionReport {
    @Getter
    private boolean acceptable;
    @Getter
    private Optional<Throwable> thrownError;
    @Getter
    private Collection<ErrorMessage> ruleResult = new ArrayList<>();

    private RuleExecutionReport(boolean acceptable, Collection<ErrorMessage> errorMessages) {
      super();
      this.acceptable = acceptable;
      this.ruleResult.addAll(errorMessages);
      this.thrownError = Optional.empty();
    }

    private RuleExecutionReport(Throwable thrownError) {
      super();
      this.thrownError = Optional.of(thrownError);
    }

    public boolean completedWithError() {
      return thrownError.isPresent();
    }

    public boolean completedWithoutError() {
      return !completedWithError();
    }

    public <E extends Throwable> boolean completedWith(Class<E> errorType) {
      return thrownError.map(error -> errorType.isInstance(error)).orElse(false);
    }

    public boolean ruleIsPassed() {
      return completedWithoutError() && ruleResult.isEmpty();
    }

    public boolean ruleIsFailed() {
      return completedWithoutError() && !ruleResult.isEmpty();
    }

    public Collection<String> getErrorTexts() {
      return this.ruleResult.stream().map(ErrorMessage::toString).collect(Collectors.toList());
    }

    public boolean contains(ErrorMessage textMessage) {
      return this.contains(Arrays.asList(textMessage));
    }

    public boolean contains(Collection<ErrorMessage> textMessage) {
      return this.ruleResult.containsAll(textMessage);
    }
  }
}
