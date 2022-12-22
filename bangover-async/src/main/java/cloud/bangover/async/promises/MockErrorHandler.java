package cloud.bangover.async.promises;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import cloud.bangover.async.promises.Promise.ErrorHandler;

public final class MockErrorHandler<E extends Throwable> implements ErrorHandler<E>, HistoricalMock<E> {
  private final MockHistory<E> errorsAcceptionsHistory = new MockHistory<E>();
  
  @Override
  public MockHistory<E> getHistory() {
    return this.errorsAcceptionsHistory;
  }

  @Override
  public void onError(E error) {
    this.errorsAcceptionsHistory.put(error);
  }
}
