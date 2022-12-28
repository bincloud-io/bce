package cloud.bangover.interactions.streaming.binary;

import cloud.bangover.StubbingQueue;
import cloud.bangover.StubbingQueue.StubbingQueueConfigurer;
import cloud.bangover.interactions.streaming.binary.BinaryChunk.BinaryChunkReader;
import lombok.NonNull;

public class MockBinarySource extends BinarySource {
  private StubBinaryChunkWriter stubReader;
  private boolean released = false;

  public MockBinarySource() {
    this(new StubBinaryChunkWriter());
  }  
  
  private MockBinarySource(@NonNull StubBinaryChunkWriter reader) {
    super(reader);
    this.stubReader = reader;
    
  }
  
  public StubbingQueueConfigurer<BinaryChunk> configureChunksQueue() {
    return stubReader.configureChunks();
  }
  
  public boolean isReleased() {
    return released;
  }
  
  @Override
  public void release() {
    this.released = true;
  }

  public static class StubBinaryChunkWriter implements BinaryChunkReader {
    private StubbingQueue<BinaryChunk> providingQueue =
        new StubbingQueue<BinaryChunk>(BinaryChunk.EMPTY);

    public StubbingQueueConfigurer<BinaryChunk> configureChunks() {
      return providingQueue.configure();
    }

    @Override
    public BinaryChunk readChunk() {
      return providingQueue.peek();
    }
  }
}