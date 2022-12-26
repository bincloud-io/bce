package cloud.bangover.interactions.polling;

import java.util.Iterator;
import lombok.experimental.UtilityClass;

/**
 * This class is responsible for creating different standard data pollers configurations. s
 *
 * @author Dmitry Mikhaylenko
 *
 */
@UtilityClass
public class Polling {
  /**
   * Create data poller without data indexing. We will have only polled data flow without polling
   * order indexes.
   *
   * @param <D>             The polling data type name
   * @param pollingFunction The data polling function
   * @return The {@link Iterable} data poller
   */
  public <D> Iterable<D> createPoller(BatchPoller<D> pollingFunction) {
    Iterator<PolledElement<D>> indexedIterator = createIndexedPoller(pollingFunction).iterator();
    return new Iterable<D>() {
      @Override
      public Iterator<D> iterator() {
        return new Iterator<D>() {
          @Override
          public boolean hasNext() {
            return indexedIterator.hasNext();
          }

          @Override
          public D next() {
            return indexedIterator.next().getData();
          }

          @Override
          public void remove() {
            indexedIterator.remove();
          }
        };
      }
    };
  }

  /**
   * Create data poller with data indexing. So we will have flow of {@link PolledElement} objects,
   * containing as polled data item, as polling order position.
   *
   * @param <D>             The polling data type name
   * @param pollingFunction The data polling function
   * @return The {@link Iterable} data poller
   */
  public <D> Iterable<PolledElement<D>> createIndexedPoller(BatchPoller<D> pollingFunction) {
    return new DataPoller<D>(pollingFunction);
  }
}
