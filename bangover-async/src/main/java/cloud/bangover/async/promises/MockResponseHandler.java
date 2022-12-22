package cloud.bangover.async.promises;

import cloud.bangover.HistoricalMock;
import cloud.bangover.MockHistory;
import cloud.bangover.async.promises.Promise.ResponseHandler;

public final class MockResponseHandler<T> implements ResponseHandler<T>, HistoricalMock<T> {
  private MockHistory<T> resolutions = new MockHistory<T>();
  
  @Override
  public MockHistory<T> getHistory() {
    return resolutions;
  }

  @Override
  public void onResponse(T response) {
    this.resolutions.put(response);
  }
}
