package cloud.bangover.interactions.streaming.binary;

import cloud.bangover.interactions.streaming.Destination;
import cloud.bangover.interactions.streaming.binary.BinaryChunk.BinaryChunkWriter;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class is the binary chunks destination.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BinaryDestination implements Destination<BinaryChunk> {
  @NonNull
  private final BinaryChunkWriter writer;

  @Override
  public void write(SourceConnection connection, BinaryChunk data, Integer size) {
    writer.writeChunk(data);
    connection.receive();
  }
}
