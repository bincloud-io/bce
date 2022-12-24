package cloud.bangover.interactions.streaming;

import cloud.bangover.MockHistory;
import cloud.bangover.interactions.streaming.Stream.Stat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RechargeableSourceTest {
  @Test
  public void shouldTransferDataFromEverySource() throws Throwable {
    // Given
    DirectStreamer streamer = new DirectStreamer();
    MockSource<Integer> firstSource = new MockSource<Integer>();
    MockSource<Integer> secondSource = new MockSource<Integer>();
    MockDestination<Integer> destination = new MockDestination<Integer>();
    firstSource.configureReadingIterations().withNextEntry(new SubmitIteation<Integer>(1, 1));
    secondSource.configureReadingIterations().withNextEntry(new SubmitIteation<Integer>(1, 1));
    Queue<Source<Integer>> sourcesQueue = new LinkedList<Source<Integer>>(Arrays.asList(firstSource, secondSource));
    RechargeableSource<Integer> source = new RechargeableSource<Integer>(sourcesQueue) {
      @Override
      public void release() {
      }
    };
    Stream<Integer> stream = streamer.createStream(source, destination);
    // When
    Stat status = stream.start().get(10L);
    // Then
    MockHistory<SubmitIteation<Integer>> submitIterations = destination.getHistory();
    Assert.assertTrue(destination.isReleased());
    Assert.assertTrue(submitIterations.hasEntry(0, new SubmitIteation<Integer>(1, 1)));
    Assert.assertTrue(submitIterations.hasEntry(1, new SubmitIteation<Integer>(1, 1)));
    Assert.assertEquals((Long) 2L, status.getSize());
  }
}
