package cloud.bangover.async.promises;

import cloud.bangover.async.promises.Promise.ResponseHandler;

public final class MockResponseHandler<T> implements ResponseHandler<T> {
  private MockHistory<T> resolutions = new MockHistory<T>();

  public boolean hasResolutions() {
    return this.resolutions.isNotEmpty();
  }

  public boolean hasResolution(int queuePosition, T resolution) {
    return this.resolutions.hasEntry(queuePosition, resolution);
  }

  @Override
  public void onResponse(T response) {
    this.resolutions.put(response);
  }
}
