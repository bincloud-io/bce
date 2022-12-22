package io.bce.interaction.streaming.binary;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import io.bce.interaction.streaming.binary.BinaryChunk.BinaryChunkWriter;
import lombok.NonNull;

public class MockBinaryDestination extends BinaryDestination
    implements HistoricalMock<BinaryChunk> {
  private MockBinaryDestination.MockBinaryChunkWriter chunkWriter;
  private boolean released = false;

  public MockBinaryDestination() {
    this(new MockBinaryChunkWriter());
  }

  private MockBinaryDestination(@NonNull MockBinaryDestination.MockBinaryChunkWriter writer) {
    super(writer);
    this.chunkWriter = writer;
  }

  public boolean isReleased() {
    return released;
  }

  @Override
  public MockHistory<BinaryChunk> getHistory() {
    return chunkWriter.getHistory();
  }

  @Override
  public void release() {
    this.released = true;
  }

  private static class MockBinaryChunkWriter
      implements BinaryChunkWriter, HistoricalMock<BinaryChunk> {
    private final MockHistory<BinaryChunk> chunkHistory = new MockHistory<BinaryChunk>();

    @Override
    public MockHistory<BinaryChunk> getHistory() {
      return chunkHistory;
    }

    @Override
    public void writeChunk(BinaryChunk chunk) {
      this.chunkHistory.put(chunk);
    }
  }
}