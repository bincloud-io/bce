package cloud.bangover.interactions.polling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DataPollerTest {
  @Test
  public void shouldPollData() {
    // Given
    StubBatchPoller<Integer> pollingFunction = new StubBatchPoller<Integer>();
    DataPoller<Integer> dataPoller = new DataPoller<Integer>(pollingFunction);
    pollingFunction.configureIterations()
      .withNextEntry(Arrays.asList(1, 2, 3))
      .withNextEntry(Arrays.asList(4, 5, 6))
      .withNextEntry(Arrays.asList(7, 8, 9));
    // When
    List<Long> indexes = new ArrayList<Long>();
    List<Integer> dataItems = new ArrayList<Integer>();
    for (PolledElement<Integer> element : dataPoller) {
      indexes.add(element.getIndex());
      dataItems.add(element.getData());
    }

    // Then
    Assert.assertEquals(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L), indexes);
    Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), dataItems);
  }
}
