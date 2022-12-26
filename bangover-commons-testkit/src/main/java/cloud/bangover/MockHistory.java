package cloud.bangover;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a history object, keeping mocks historical data. For example invocations arguments.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The history items objects type name
 */
public final class MockHistory<T> {
  private final List<T> history = new LinkedList<T>();

  /**
   * Get all history entries.
   *
   * @return The history entries
   */
  public List<T> getAllHistoryEntries() {
    return Collections.unmodifiableList(history);
  }

  /**
   * Check that the history has entries.
   *
   * @return True if it has and false otherwise
   */
  public boolean hasEntries() {
    return getLength() != 0;
  }

  /**
   * Get history length.
   *
   * @return The history length
   */
  public int getLength() {
    return this.history.size();
  }

  /**
   * Check that the history contain entry on a specified position. Equivalence will be determined by
   * {@link Object#equals(Object)} method.
   *
   * @param position   The position
   * @param entryValue The entry value
   * @return True if it has and false otherwise
   */
  public boolean hasEntry(int position, T entryValue) {
    if (position < getLength()) {
      return entryValue.equals(getEntry(position));
    }
    return false;
  }

  /**
   * Get entry value on a specified position.
   *
   * @param position The position
   * @return The entry value
   */
  public T getEntry(int position) {
    return this.history.get(position);
  }

  /**
   * Put entry to the history.
   *
   * @param entryValue The entry value
   */
  public void put(T entryValue) {
    this.history.add(entryValue);
  }

  /**
   * Clear history.
   */
  public void clear() {
    this.history.clear();
  }
}
