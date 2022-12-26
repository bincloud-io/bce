package cloud.bangover.interactions.polling;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;

/**
 * This class implements the object which poll data from some data source. This class implements
 * {@link Iterable} interface so data polling could be implemented through the for-each loop.
 *
 * @author Dmitry Mikhaylenko
 *
 * @param <D> The polling data type name
 */
@RequiredArgsConstructor
public class DataPoller<D> implements Iterable<PolledElement<D>> {
  private final BatchPoller<D> dataPoller;

  @Override
  public Iterator<PolledElement<D>> iterator() {
    return new PollIterator();
  }

  private class PollIterator implements Iterator<PolledElement<D>> {
    private Iterator<D> polledDataIterator = Collections.emptyIterator();
    private AtomicLong index = new AtomicLong(0L);

    @Override
    public boolean hasNext() {
      runPollingIterationStep();
      return polledDataIterator.hasNext();
    }

    @Override
    public PolledElement<D> next() {
      runPollingIterationStep();
      return new PollingDataHolder<D>(index.getAndIncrement(), polledDataIterator.next());
    }

    private void runPollingIterationStep() {
      if (isRequirePollingIteration()) {
        pollNext();
      }
    }

    private boolean isRequirePollingIteration() {
      return !polledDataIterator.hasNext();
    }

    private void pollNext() {
      Collection<D> polledData = dataPoller.poll();
      switchPolledDataIterator(polledData.iterator());
    }

    private void switchPolledDataIterator(Iterator<D> polledDataIterator) {
      this.polledDataIterator = polledDataIterator;
    }
  }
}
