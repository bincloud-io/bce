package cloud.bangover.interactions.streaming.binary;

import cloud.bangover.MockHistory;
import cloud.bangover.async.promises.MockErrorHandler;
import cloud.bangover.async.promises.MockResponseHandler;
import cloud.bangover.async.promises.WaitingPromise;
import cloud.bangover.interactions.streaming.DirectStreamer;
import cloud.bangover.interactions.streaming.Stream;
import cloud.bangover.interactions.streaming.Stream.Stat;
import cloud.bangover.interactions.streaming.Streamer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BinaryStreamingTest {
  private BinaryChunk FIRST_CHUNK = new BinaryChunk(new byte[] {1, 2, 3});
  private BinaryChunk SECOND_CHUNK = new BinaryChunk(new byte[] {4, 5, 6});
  
  @Test
  public void shouldTransferDataFromSourceToDestination() {
    // Given
    Streamer streamer = new DirectStreamer();
    MockBinarySource binarySource = new MockBinarySource();
    MockBinaryDestination binaryDestination = new MockBinaryDestination();
    MockResponseHandler<Stat> mockResponseHandler = new MockResponseHandler<Stat>();
    MockErrorHandler<Throwable> mockErrorHandler = new MockErrorHandler<Throwable>();
    Stream<BinaryChunk> stream = streamer.createStream(binarySource, binaryDestination);
    binarySource.configureChunksQueue()
      .withNextEntry(FIRST_CHUNK)
      .withNextEntry(SECOND_CHUNK);
    // When
    WaitingPromise.of(stream.start())
      .then(mockResponseHandler)
      .error(mockErrorHandler)
      .await();
    // Then
    Assert.assertTrue(binarySource.isReleased());
    Assert.assertTrue(binaryDestination.isReleased());
    MockHistory<BinaryChunk> chunksHistory = binaryDestination.getHistory();
    Assert.assertTrue(chunksHistory.hasEntry(0, FIRST_CHUNK));
    Assert.assertTrue(chunksHistory.hasEntry(1, SECOND_CHUNK));
    Assert.assertFalse(mockErrorHandler.getHistory().hasEntries());
    Stat stat = mockResponseHandler.getHistory().getEntry(0);
    Assert.assertEquals((Long) 6L, stat.getSize());
  }
}
