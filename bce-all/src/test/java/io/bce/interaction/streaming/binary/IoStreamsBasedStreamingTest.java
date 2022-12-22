package io.bce.interaction.streaming.binary;

import cloud.bangover.async.promises.WaitingPromise;
import cloud.bangover.interactions.streaming.Stream;
import cloud.bangover.interactions.streaming.Streamer;
import io.bce.streaming.DirectStreamer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IoStreamsBasedStreamingTest {
  private static final String DATA = "Hello World!";

  @Test
  public void shouldTranmitData() throws Throwable {
    // Given
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BinarySource source = new InputStreamSource(new ByteArrayInputStream(DATA.getBytes()), 5);
    BinaryDestination destination = new OutputStreamDestination(outputStream);
    Streamer streamer = new DirectStreamer();
    Stream<BinaryChunk> stream = streamer.createStream(source, destination);
    // When
    WaitingPromise.of(stream.start()).await();
    // Then
    Assert.assertEquals(DATA, new String(outputStream.toByteArray()));
  }

  @Test
  public void shouldTranmitDataWithoutErrorsIfBufferSizeGreaterThenTransferringDataVolume() {
    // Given
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BinarySource source = new InputStreamSource(new ByteArrayInputStream(DATA.getBytes()), 5000);
    BinaryDestination destination = new OutputStreamDestination(outputStream);
    Streamer streamer = new DirectStreamer();
    Stream<BinaryChunk> stream = streamer.createStream(source, destination);
    // When
    WaitingPromise.of(stream.start()).await();
    // Then
    Assert.assertEquals(DATA, new String(outputStream.toByteArray()));
  }
}
