package cloud.bangover.validation;

import cloud.bangover.validation.Range.ThresholdsAmountsException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class RangeTest {
  @DataPoints("rangePoints")
  public static RangePoint[] rangePoints() {
    return new RangePoint[] {
        new RangePoint(-10L, false),
        new RangePoint(11L, false),
        new RangePoint(0L, true),
        new RangePoint(5L, true),
        new RangePoint(10L, true),
    };
  }

  @Test(expected = ThresholdsAmountsException.class)
  public void shouldFailIfRangeMinGreaterThenMax() {
    Range.createFor(10L, 0L);
  }

  @Theory
  public void shouldCheckIfValueIsntOutOfRange(
      @FromDataPoints("rangePoints") RangePoint rangePoint) {
    // When
    Range<Long> range = Range.createFor(0L, 10L);
    // Then
    Assert.assertTrue(rangePoint.isContainedIn(range));
  }

  private static class RangePoint {
    private Long value;
    private boolean contained;

    public RangePoint(Long value, boolean contained) {
      super();
      this.value = value;
      this.contained = contained;
    }

    public boolean isContainedIn(Range<Long> range) {
      return range.contains(value) == contained;
    }
  }
}
