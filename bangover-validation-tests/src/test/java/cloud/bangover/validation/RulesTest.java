package cloud.bangover.validation;

import static cloud.bangover.validation.Rules.EXPECTED_LENGTH_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.EXPECTED_SIZE_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.EXPECTED_VALUE_PARAMETER;
import static cloud.bangover.validation.Rules.MAX_LENGTH_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.MAX_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.MAX_SIZE_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.MIN_LENGTH_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.MIN_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.MIN_SIZE_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.REGEXP_PARAMETER_VALUE;
import static cloud.bangover.validation.Rules.VALIDATED_ELEMENT_PARAMETER_NAME;

import cloud.bangover.validation.Range.ThresholdsAmountsException;
import cloud.bangover.validation.RuleExecutor.RuleExecutionReport;
import cloud.bangover.validation.RuleExecutor.RuleProvider;
import cloud.bangover.validation.Rules.RulePredicate;
import cloud.bangover.validation.Rules.SizeMustNotBeNegativeValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class RulesTest {
  private static final Object SIMPLE_OBJECT = new Object();
  private static final ErrorMessage FAIL_MESSAGE_TEMPLATE = ErrorMessage.createFor("FAIL");
  private static final RulePredicate<Object> PASSED_PREDICATE = createStaticPredicate(true);
  private static final RulePredicate<Object> FAILED_PREDICATE = createStaticPredicate(false);
  private static final Pattern PATTERN = Pattern.compile("([A-Z])*");
  
  
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  @DataPoints("rulesExecutionCases")
  public static Runnable[] rulesExecutionCases() {
    return new Runnable[] {
      new PassedCase(
        RuleExecutor.of(new Object(), Rules.notNull(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(null, Rules.isNull(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Optional.of(new Object()), Rules.isPresent(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Optional.empty(), Rules.isMissing(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(null, Rules.match(Object.class, FAIL_MESSAGE_TEMPLATE, FAILED_PREDICATE))
      ),
      new PassedCase(
        RuleExecutor.of(10, Rules.equalTo(10, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(10, Rules.notEqualTo(100, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(true, Rules.assertTrue(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(false, Rules.assertFalse(FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(100L, Rules.greaterThan(Long.class, 10L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(100L, Rules.greaterThanOrEqual(Long.class, 10L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(10L, Rules.greaterThanOrEqual(Long.class, 10L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(100L, Rules.lessThan(Long.class, 1000L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(100L, Rules.lessThanOrEqual(Long.class, 1000L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(1000L, Rules.lessThanOrEqual(Long.class, 1000L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(100L, Rules.between(Long.class, 100L, 200L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(150L, Rules.between(Long.class, 100L, 200L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(200L, Rules.between(Long.class, 100L, 200L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(99L, Rules.outside(Long.class, 100L, 200L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(201L, Rules.outside(Long.class, 100L, 200L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.hasLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.doesNotHaveLength(String.class, 15L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("", Rules.empty(String.class, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.notEmpty(String.class, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.minLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO!", Rules.minLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO!", Rules.maxLength(String.class, 6L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.maxLength(String.class, 6L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.limitedLength(String.class, 5L, 6L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.limitedLength(String.class, 4L, 5L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.limitedLength(String.class, 2L, 10L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of("HELLO", Rules.pattern(String.class, PATTERN, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.collectionHasSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.collectionDoesNotHaveSize(List.class, 6L, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Arrays.asList(), Rules.emptyCollection(List.class, FAIL_MESSAGE_TEMPLATE))
      ),
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.notEmptyCollection(List.class, FAIL_MESSAGE_TEMPLATE))
      ),         
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.minCollectionSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),      
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5, 6), Rules.minCollectionSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE))
      ),    
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5, 6), Rules.maxCollectionSize(List.class, 6L, FAIL_MESSAGE_TEMPLATE))
      ),     
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.maxCollectionSize(List.class, 6L, FAIL_MESSAGE_TEMPLATE))
      ),        
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.limitedCollectionSize(List.class, 5L, 6L, FAIL_MESSAGE_TEMPLATE))
      ), 
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.limitedCollectionSize(List.class, 4L, 5L, FAIL_MESSAGE_TEMPLATE))
      ),  
      new PassedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.limitedCollectionSize(List.class, 2L, 10L, FAIL_MESSAGE_TEMPLATE))
      ),  
      new PassedCase(
        RuleExecutor.of(SIMPLE_OBJECT, Rules.match(Object.class, FAIL_MESSAGE_TEMPLATE, PASSED_PREDICATE))
      ),
      // Failed cases
      new FailedCase(
        RuleExecutor.of(SIMPLE_OBJECT, Rules.isNull(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Optional.of(SIMPLE_OBJECT))
        )
      ),
      new FailedCase(
        RuleExecutor.of(null, Rules.notNull(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Optional.empty())
        )
      ),
      new FailedCase(
        RuleExecutor.of(Optional.empty(), Rules.isPresent(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Optional.empty())
        )
      ),
      new FailedCase(
        RuleExecutor.of(Optional.of(SIMPLE_OBJECT), Rules.isMissing(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Optional.of(SIMPLE_OBJECT))
        )
      ),
      new FailedCase(
        RuleExecutor.of(11, Rules.equalTo(10, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_VALUE_PARAMETER, 10)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 11)
        )
      ),
      new FailedCase(
        RuleExecutor.of(10, Rules.notEqualTo(10, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_VALUE_PARAMETER, 10)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 10)
        )
      ),
      new FailedCase(
        RuleExecutor.of(false, Rules.assertTrue(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_VALUE_PARAMETER, true)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, false)
        )
      ),
      new FailedCase(
        RuleExecutor.of(true, Rules.assertFalse(FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_VALUE_PARAMETER, false)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, true)
        )
      ),
      new FailedCase(
        RuleExecutor.of(9L, Rules.greaterThan(Long.class, 10L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_PARAMETER_VALUE, 10L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 9L)
        )
      ),
      new FailedCase(
        RuleExecutor.of(9L, Rules.greaterThanOrEqual(Long.class, 10L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_PARAMETER_VALUE, 10L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 9L)
        )
      ),
      new FailedCase(
        RuleExecutor.of(101L, Rules.lessThan(Long.class, 100L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MAX_PARAMETER_VALUE, 100L).withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 101L)
        )
      ),
      new FailedCase(
        RuleExecutor.of(101L, Rules.lessThanOrEqual(Long.class, 100L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MAX_PARAMETER_VALUE, 100L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 101L)
        )
      ),
      new FailedCase(
        RuleExecutor.of(3L, Rules.between(Long.class, 5L, 7L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_PARAMETER_VALUE, 5L)
            .withParameter(MAX_PARAMETER_VALUE, 7L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 3L)
          )
      ),
      new FailedCase(
        RuleExecutor.of(10L, Rules.between(Long.class, 5L, 7L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_PARAMETER_VALUE, 5L)
            .withParameter(MAX_PARAMETER_VALUE, 7L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 10L)
        )
      ),
      new FailedCase(
        RuleExecutor.of(6L, Rules.outside(Long.class, 5L, 7L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_PARAMETER_VALUE, 5L).withParameter(MAX_PARAMETER_VALUE, 7L).withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, 6L)
        )
      ),
      new FailedCase(
        RuleExecutor.of("HELL", Rules.hasLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_LENGTH_PARAMETER_VALUE, 5L).withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELL")
        )
      ),
      new FailedCase(
        RuleExecutor.of("HELLO", Rules.doesNotHaveLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_LENGTH_PARAMETER_VALUE, 5L).withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELLO")
        )
      ),
      new FailedCase(
        RuleExecutor.of("HELLO", Rules.empty(String.class, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELLO")
        )
      ),
      new FailedCase(
        RuleExecutor.of("", Rules.notEmpty(String.class, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "")
        )
      ),
      new FailedCase(
        RuleExecutor.of("HELL", Rules.minLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_LENGTH_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELL")
          )
      ),
      new FailedCase(
        RuleExecutor.of("HELLO!", Rules.maxLength(String.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MAX_LENGTH_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELLO!")
        )
      ),
      new FailedCase(
        RuleExecutor.of("HELLO!!", Rules.limitedLength(String.class, 5L, 6L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_LENGTH_PARAMETER_VALUE, 5L)
            .withParameter(MAX_LENGTH_PARAMETER_VALUE, 6L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "HELLO!!")
        )
      ),
      new FailedCase(
          RuleExecutor.of("12345", Rules.pattern(String.class, PATTERN, FAIL_MESSAGE_TEMPLATE)),
          Arrays.asList(
            FAIL_MESSAGE_TEMPLATE
              .withParameter(REGEXP_PARAMETER_VALUE, PATTERN)
              .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, "12345")
          )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5, 6), Rules.collectionHasSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_SIZE_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4, 5, 6))
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5), Rules.collectionDoesNotHaveSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(EXPECTED_SIZE_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4, 5))
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5),Rules.emptyCollection(List.class, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4, 5))
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(),Rules.notEmptyCollection(List.class, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList())
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4), Rules.minCollectionSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_SIZE_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4))
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5, 6),Rules.maxCollectionSize(List.class, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MAX_SIZE_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4, 5, 6))
        )
      ),
      new FailedCase(
        RuleExecutor.of(Arrays.asList(1, 2, 3, 4, 5, 6),Rules.limitedCollectionSize(List.class, 4L, 5L, FAIL_MESSAGE_TEMPLATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(MIN_SIZE_PARAMETER_VALUE, 4L)
            .withParameter(MAX_SIZE_PARAMETER_VALUE, 5L)
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, Arrays.asList(1, 2, 3, 4, 5, 6))
        )
      ),
      new FailedCase(
        RuleExecutor.of(SIMPLE_OBJECT,Rules.match(Object.class, FAIL_MESSAGE_TEMPLATE, FAILED_PREDICATE)),
        Arrays.asList(
          FAIL_MESSAGE_TEMPLATE
            .withParameter(VALIDATED_ELEMENT_PARAMETER_NAME, SIMPLE_OBJECT)
        )
      ),
      // Failed rules creations
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.hasLength(String.class, -5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.doesNotHaveLength(String.class, -15L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.minLength(String.class, -5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.maxLength(String.class, -6L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.limitedLength(String.class, -4L, 5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.limitedLength(String.class, 2L, -10L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<String>("HELLO", new RuleProvider<String>() {
        @Override
        public Rule<String> provideRule() {
          return Rules.limitedLength(String.class, 10L, 2L, FAIL_MESSAGE_TEMPLATE);
        }
      }, ThresholdsAmountsException.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.collectionHasSize(List.class, -5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.collectionDoesNotHaveSize(List.class, -6L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.minCollectionSize(List.class, -5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5, 6), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.maxCollectionSize(List.class, -6L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.limitedCollectionSize(List.class, -5L, 6L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {
        @Override
        public Rule<List> provideRule() {
          return Rules.limitedCollectionSize(List.class, 5L, -6L, FAIL_MESSAGE_TEMPLATE);
        }
      }, SizeMustNotBeNegativeValue.class),
      new RuleCreationFailedCase<List>(Arrays.asList(1, 2, 3, 4, 5), new RuleProvider<List>() {

        @Override
        public Rule<List> provideRule() {
          return Rules.limitedCollectionSize(List.class, 6L, 5L, FAIL_MESSAGE_TEMPLATE);
        }
      }, ThresholdsAmountsException.class)
    };
  }
  
  private static final RulePredicate<Object> createStaticPredicate(boolean result) {
    return new RulePredicate<Object>() {
      @Override
      public boolean isSatisfiedBy(Object value) {
        return result;
      }
    };
  }
  
  @Theory
  public void executeRules(
      @FromDataPoints("rulesExecutionCases") Runnable ruleExecutionCase) {
    ruleExecutionCase.run();
  }
  
  private static class PassedCase implements Runnable {
    private final RuleExecutor<?> ruleExecutor;

    public PassedCase(RuleExecutor<?> ruleExecutor) {
      super();
      this.ruleExecutor = ruleExecutor;
    }

    @Override
    public void run() {
      Assert.assertTrue(ruleExecutor.execute().ruleIsPassed());      
    }
  }
  
  private static class FailedCase implements Runnable {
    private final RuleExecutor<?> ruleExecutor;
    private final Collection<ErrorMessage> errorMessages;
    
    public FailedCase(RuleExecutor<?> ruleExecutor, Collection<ErrorMessage> errorMessages) {
      super();
      this.ruleExecutor = ruleExecutor;
      this.errorMessages = errorMessages;
    }

    @Override
    public void run() {
      RuleExecutionReport report = ruleExecutor.execute();
      Assert.assertTrue(report.ruleIsFailed());
      Assert.assertTrue(report.contains(errorMessages));      
    }
  }
  
  private static class RuleCreationFailedCase<V> implements Runnable {
    private final V validatable;
    private final RuleProvider<V> ruleProvider;
    private final Class<? extends Exception> thrownError;
    
    public RuleCreationFailedCase(V validatable, RuleProvider<V> ruleProvider,
        Class<? extends Exception> thrownError) {
      super();
      this.validatable = validatable;
      this.ruleProvider = ruleProvider;
      this.thrownError = thrownError;
    }
    
    @Override
    public void run() {
      RuleExecutor<V> ruleExecutor = new RuleExecutor<V>(validatable, ruleProvider);
      RuleExecutionReport report = ruleExecutor.execute();
      Assert.assertTrue(report.completedWithError());
      Assert.assertTrue(report.completedWith(thrownError));
    }    
  }
}
