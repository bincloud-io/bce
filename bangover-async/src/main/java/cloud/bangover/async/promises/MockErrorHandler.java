package cloud.bangover.async.promises;

import cloud.bangover.async.promises.Promise.ErrorHandler;

public final class MockErrorHandler<E extends Throwable> implements ErrorHandler<E> {
  private final MockHistory<E> errorsAcceptionsHistory = new MockHistory<E>();

  public boolean hasAcceptedErrors() {
    return this.errorsAcceptionsHistory.isNotEmpty();
  }

  public boolean hasAcceptedError(int queuePosition, E error) {
    return this.errorsAcceptionsHistory.hasEntry(queuePosition, error);
  }

  @Override
  public void onError(E error) {
    this.errorsAcceptionsHistory.put(error);
  }
}
