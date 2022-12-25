package cloud.bangover.validation.global;

import cloud.bangover.validation.ErrorMessage;
import cloud.bangover.validation.Rule;
import cloud.bangover.validation.RuleExecutor;
import cloud.bangover.validation.RuleExecutor.RuleExecutionReport;
import java.util.Arrays;
import cloud.bangover.validation.Rules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GlobalValidationsTest {
  private static final ErrorMessage GREATER_THAN_ERROR_MESSAGE = ErrorMessage.createFor("GREATER THAN FAILED");
  private static final String UNKNOWN_ALIAS = "UNKNOWN";
  private static final String GREATER_THAN_ALIAS = "GREATER_THAN";
  private static final Rule<Integer> GREATER_THAN_REGISTERED_RULE =
      Rules.greaterThan(Integer.class, 100, GREATER_THAN_ERROR_MESSAGE);

  @Before
  public void setUp() {
    GlobalValidations.registerRule(GREATER_THAN_ALIAS, GREATER_THAN_REGISTERED_RULE);
  }

  @Test
  public void shouldUnknownRuleBeSkipped() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(new Object(), GlobalValidations.getRule(UNKNOWN_ALIAS));
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertFalse(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
  }
  
  @Test
  public void shouldRuleForNotAllowableTypeBePassed() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(new Object(), GlobalValidations.getRule(GREATER_THAN_ALIAS));
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertFalse(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
  }
  
  @Test
  public void shouldRuleBePassed() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(1000, GlobalValidations.getRule(GREATER_THAN_ALIAS));
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertTrue(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
    Assert.assertTrue(report.contains(Arrays.asList()));
  }
  
  @Test
  public void shouldRuleBeFailed() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(1, GlobalValidations.getRule(GREATER_THAN_ALIAS));
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertTrue(report.isAcceptable());
    Assert.assertFalse(report.ruleIsPassed());
    Assert.assertTrue(report.contains(Arrays.asList(
        GREATER_THAN_ERROR_MESSAGE
          .withParameter(Rules.MIN_PARAMETER_VALUE, 100)
          .withParameter(Rules.VALIDATED_ELEMENT_PARAMETER_NAME, 1))
      )
    );
  }
  
  @Test
  public void shouldInvertedRuleBePassedForNotValidValue() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(1, GlobalValidations.getRule(GREATER_THAN_ALIAS).invert());
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertTrue(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
    Assert.assertTrue(report.contains(Arrays.asList()));       
  }
  
  @After
  public void tearDown() {
    GlobalValidations.clear();
  }
}
