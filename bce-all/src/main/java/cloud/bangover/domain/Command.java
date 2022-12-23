package cloud.bangover.domain;

import cloud.bangover.async.promises.Promise;

public interface Command<T> {
  public Promise<T> execute();
}
