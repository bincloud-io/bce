package cloud.bangover;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class MockHistory<T> {
  private final List<T> history = new LinkedList<T>();

  public List<T> getAllHistoryEntries() {
    return Collections.unmodifiableList(history);
  }
  
  public boolean isNotEmpty() {
    return getLength() != 0;
  }

  public int getLength() {
    return this.history.size();
  }

  public boolean hasEntry(int queuePosition, T resolution) {
    if (queuePosition < getLength()) {
      return resolution.equals(getEntry(queuePosition));
    }
    return false;
  }

  public T getEntry(int queuePosition) {
    return this.history.get(queuePosition);
  }
  
  public void put(T value) {
    this.history.add(value); 
  }
  
  public void clear() {
    this.history.clear();
  }
}