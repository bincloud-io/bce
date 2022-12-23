package cloud.bangover.interactions.streaming.binary;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import cloud.bangover.interactions.streaming.binary.BinaryChunk.BinaryChunkWriter;
import lombok.Getter;
import lombok.NonNull;

public class MockBinaryDestination extends BinaryDestination
    implements HistoricalMock<BinaryChunk> {
  private MockBinaryDestination.MockBinaryChunkWriter chunkWriter;
  @Getter
  private boolean released = false;

  public MockBinaryDestination() {
    this(new MockBinaryChunkWriter());
  }

  private MockBinaryDestination(@NonNull MockBinaryDestination.MockBinaryChunkWriter writer) {
    super(writer);
    this.chunkWriter = writer;
  }

  @Override
  public MockHistory<BinaryChunk> getHistory() {
    return chunkWriter.getHistory();
  }

  @Override
  public void release() {
    this.released = true;
  }

  @Getter
  private static class MockBinaryChunkWriter
      implements BinaryChunkWriter, HistoricalMock<BinaryChunk> {
    private final MockHistory<BinaryChunk> history = new MockHistory<BinaryChunk>();

    @Override
    public void writeChunk(BinaryChunk chunk) {
      this.history.put(chunk);
    }
  }
}