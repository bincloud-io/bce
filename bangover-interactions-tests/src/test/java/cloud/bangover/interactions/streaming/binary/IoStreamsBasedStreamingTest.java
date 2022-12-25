package cloud.bangover.interactions.streaming.binary;

import cloud.bangover.async.promises.WaitingPromise;
import cloud.bangover.errors.UnexpectedErrorException;
import cloud.bangover.interactions.streaming.DirectStreamer;
import cloud.bangover.interactions.streaming.Stream;
import cloud.bangover.interactions.streaming.Streamer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

  @Test
  public void shouldCompleteDataTransferringIfBufferIsEmpty() throws Throwable {
    // Given
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BinarySource source = new InputStreamSource(new ByteArrayInputStream(new byte[0]), 5);
    BinaryDestination destination = new OutputStreamDestination(outputStream);
    Streamer streamer = new DirectStreamer();
    Stream<BinaryChunk> stream = streamer.createStream(source, destination);
    // When
    WaitingPromise.of(stream.start()).await();
    // Then
    Assert.assertEquals("", new String(outputStream.toByteArray()));
  }

  @Test(expected = UnexpectedErrorException.class)
  public void shouldBeCompletedWithErrorIfInputStreamThrewException() throws Throwable {
    InputStream inputStream = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException();
      }
    };
    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BinarySource source = new InputStreamSource(inputStream, 5);
    BinaryDestination destination = new OutputStreamDestination(outputStream);
    Streamer streamer = new DirectStreamer();
    Stream<BinaryChunk> stream = streamer.createStream(source, destination);
    stream.start().get(10L);
  }

  @Test(expected = UnexpectedErrorException.class)
  public void shouldBeCompletedWithErrorIfOutputStreamThrewException() throws Throwable {
    OutputStream outputStream = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        throw new IOException();        
      }
    };    
    BinarySource source = new InputStreamSource(new ByteArrayInputStream(new byte[] {1, 2, 3}), 5);
    BinaryDestination destination = new OutputStreamDestination(outputStream);
    Streamer streamer = new DirectStreamer();
    Stream<BinaryChunk> stream = streamer.createStream(source, destination);
    stream.start().get(10L);
  }
}
