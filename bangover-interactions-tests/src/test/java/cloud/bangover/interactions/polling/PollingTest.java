package cloud.bangover.interactions.polling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PollingTest {
  @Test
  public void shouldPollDataWithoutIndexing() {
    // Given
    BatchPoller<Integer> pollingFunction = createPollingFunction();
    Iterable<Integer> poller = Polling.createPoller(pollingFunction);
    // When
    List<Integer> polledData = new ArrayList<Integer>();
    for (Integer item : poller) {
      polledData.add(item);
    }
    // Then
    Assert.assertEquals(Arrays.asList(1, 1, 1), polledData);
  }

  @Test
  public void shouldPollDataWithIndexing() {
    // Given
    BatchPoller<Integer> pollingFunction = createPollingFunction();
    Iterable<PolledElement<Integer>> poller = Polling.createIndexedPoller(pollingFunction);
    // When
    List<Long> indexes = new ArrayList<Long>();
    List<Integer> polledData = new ArrayList<Integer>();
    for (PolledElement<Integer> item : poller) {
      indexes.add(item.getIndex());
      polledData.add(item.getData());
    }
    // Then
    Assert.assertEquals(Arrays.asList(0L, 1L, 2L), indexes);
    Assert.assertEquals(Arrays.asList(1, 1, 1), polledData);
  }

  private BatchPoller<Integer> createPollingFunction() {
    StubBatchPoller<Integer> poller = new StubBatchPoller<Integer>();
    poller.configureIterations()
      .withNextEntry(Arrays.asList(1))
      .withNextEntry(Arrays.asList(1))
      .withNextEntry(Arrays.asList(1));
    return poller;
  }
}
