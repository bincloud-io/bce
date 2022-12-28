package cloud.bangover.interactions.streaming.binary;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class BinaryChunkTest {
  @DataPoints("binaryChunks")
  public static Runnable[] binaryChunks() {
    return new Runnable[] { 
        new BinaryChunkTestingTask(new byte[] {}, 0, true),
        new BinaryChunkTestingTask(new byte[] { 1 }, 1, false),
        new BinaryChunkTestingTask(new byte[] { 1, 2 }, 2, false),
        new BinaryChunkTestingTask(new byte[] { 1, 2, 3 }, 3, false),

    };
  }

  @Theory
  public void shouldCreateBinaryChunk(@FromDataPoints("binaryChunks") Runnable testingTask) {
    testingTask.run();
  }

  @Test
  public void shouldEmptyChunkHaveZeroSizeAndEmptyBody() {
    Assert.assertEquals(new BinaryChunk(new byte[0]), BinaryChunk.EMPTY);
  }

  private static class BinaryChunkTestingTask implements Runnable {
    private final byte[] body;
    private final int expectedSize;
    private final boolean expectedShouldBeEmpty;

    public BinaryChunkTestingTask(byte[] body, int expectedSize, boolean expectedShouldBeEmpty) {
      super();
      this.body = body;
      this.expectedSize = expectedSize;
      this.expectedShouldBeEmpty = expectedShouldBeEmpty;
    }

    @Override
    public void run() {
      BinaryChunk chunk = new BinaryChunk(body);
      Assert.assertEquals(body, chunk.getBody());
      Assert.assertEquals(expectedSize, chunk.getSize());
      Assert.assertEquals(expectedShouldBeEmpty, chunk.isEmpty());
    }

  }
}
