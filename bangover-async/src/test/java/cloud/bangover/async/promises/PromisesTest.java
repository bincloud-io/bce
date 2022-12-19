package cloud.bangover.async.promises;

import cloud.bangover.async.promises.Promises.PromiseRejectionDuplicateException;
import cloud.bangover.async.promises.Promises.PromiseResolutionDuplicateException;
import cloud.bangover.async.timer.Timer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PromisesTest {
  private static final String RESPONSE = "HELLO WORLD!";
  private static final RuntimeException TYPED_EXCEPTION = new RuntimeException("THE TYPED ERROR!");
  private static final Exception UNTYPED_EXCEPTION = new Exception("THE UNTYPED ERROR!");

  @Test
  public void shouldResolvePromisesWithoutErrors() {
    // Given
    MockResponseHandler<String> firstResponseHandler = new MockResponseHandler<String>();
    MockResponseHandler<String> secondResponseHandler = new MockResponseHandler<String>();
    MockErrorHandler<RuntimeException> firstErrorHandler = new MockErrorHandler<RuntimeException>();
    MockErrorHandler<Throwable> secondErrorHandler = new MockErrorHandler<Throwable>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        Timer.sleep(1000L);
        deferred.resolve(RESPONSE);
      }
    })).then(firstResponseHandler).then(secondResponseHandler)
        .error(RuntimeException.class, firstErrorHandler).error(secondErrorHandler).await();
    // Then
    Assert.assertTrue(firstResponseHandler.hasResolution(0, RESPONSE));
    Assert.assertTrue(secondResponseHandler.hasResolution(0, RESPONSE));
    Assert.assertFalse(firstErrorHandler.hasAcceptedErrors());
    Assert.assertFalse(secondErrorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldTypedErrorHandlerBeResolved() {
    // Given
    MockResponseHandler<String> responseHandler = new MockResponseHandler<String>();
    MockErrorHandler<RuntimeException> firstErrorHandler = new MockErrorHandler<RuntimeException>();
    MockErrorHandler<Throwable> secondErrorHandler = new MockErrorHandler<Throwable>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        Timer.sleep(1000L);
        deferred.reject(TYPED_EXCEPTION);
      }
    })).then(responseHandler).error(RuntimeException.class, firstErrorHandler)
        .error(secondErrorHandler).await();
    // Then
    Assert.assertFalse(responseHandler.hasResolutions());
    Assert.assertTrue(firstErrorHandler.hasAcceptedError(0, TYPED_EXCEPTION));
    Assert.assertFalse(secondErrorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldUntypedErrorHandlerBeResolved() {
    // Given
    MockResponseHandler<String> responseHandler = new MockResponseHandler<String>();
    MockErrorHandler<RuntimeException> firstErrorHandler = new MockErrorHandler<RuntimeException>();
    MockErrorHandler<Throwable> secondErrorHandler = new MockErrorHandler<Throwable>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        Timer.sleep(1000L);
        deferred.reject(UNTYPED_EXCEPTION);
      }
    })).then(responseHandler).error(RuntimeException.class, firstErrorHandler)
        .error(secondErrorHandler).await();
    // Then
    Assert.assertFalse(responseHandler.hasResolutions());
    Assert.assertFalse(firstErrorHandler.hasAcceptedErrors());
    Assert.assertTrue(secondErrorHandler.hasAcceptedError(0, UNTYPED_EXCEPTION));
  }

  @Test
  public void shouldPromiseBeRejectedIfExceptionIsThrownFromDeferredFunction() {
    // Given
    MockResponseHandler<String> responseHandler = new MockResponseHandler<String>();
    MockErrorHandler<Throwable> errorHandler = new MockErrorHandler<Throwable>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        Timer.sleep(1000L);
        throw TYPED_EXCEPTION;
      }
    })).then(responseHandler).error(errorHandler).await();
    // Then
    Assert.assertFalse(responseHandler.hasResolutions());
    Assert.assertTrue(errorHandler.hasAcceptedError(0, TYPED_EXCEPTION));
  }

  @Test
  public void shouldPromiseBeProtectedOfDoubleResolution() {
    // Given
    MockErrorHandler<PromiseResolutionDuplicateException> manualHandler =
        new MockErrorHandler<PromiseResolutionDuplicateException>();
    MockErrorHandler<PromiseResolutionDuplicateException> errorHandler =
        new MockErrorHandler<PromiseResolutionDuplicateException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        try {
          deferred.resolve(RESPONSE);
          deferred.resolve(RESPONSE);
        } catch (PromiseResolutionDuplicateException error) {
          manualHandler.onError(error);
        }
      }
    })).error(PromiseResolutionDuplicateException.class, errorHandler).await();
    // Then
    Assert.assertTrue(manualHandler.hasAcceptedErrors());
    Assert.assertFalse(errorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldPromiseBeProtectedOfDoubleRejection() {
    // Given
    MockErrorHandler<PromiseRejectionDuplicateException> manualHandler =
        new MockErrorHandler<PromiseRejectionDuplicateException>();
    MockErrorHandler<PromiseRejectionDuplicateException> errorHandler =
        new MockErrorHandler<PromiseRejectionDuplicateException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        try {
          deferred.reject(TYPED_EXCEPTION);
          deferred.reject(TYPED_EXCEPTION);
        } catch (PromiseRejectionDuplicateException error) {
          manualHandler.onError(error);
        }
      }
    })).error(PromiseRejectionDuplicateException.class, errorHandler).await();
    // Then
    Assert.assertTrue(manualHandler.hasAcceptedErrors());
    Assert.assertFalse(errorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldPromiseBeProtectedOfRejectionAfterResolution() {
    // Given
    MockErrorHandler<PromiseResolutionDuplicateException> manualHandler =
        new MockErrorHandler<PromiseResolutionDuplicateException>();
    MockErrorHandler<PromiseResolutionDuplicateException> errorHandler =
        new MockErrorHandler<PromiseResolutionDuplicateException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        try {
          deferred.resolve(RESPONSE);
          deferred.reject(TYPED_EXCEPTION);
        } catch (PromiseResolutionDuplicateException error) {
          manualHandler.onError(error);
        }
      }
    })).error(PromiseResolutionDuplicateException.class, errorHandler).await();
    // Then
    Assert.assertTrue(manualHandler.hasAcceptedErrors());
    Assert.assertFalse(errorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldPromiseBeProtectedOfResolutionAfterRejection() {
    // Given
    MockErrorHandler<PromiseRejectionDuplicateException> manualHandler =
        new MockErrorHandler<PromiseRejectionDuplicateException>();
    MockErrorHandler<PromiseRejectionDuplicateException> errorHandler =
        new MockErrorHandler<PromiseRejectionDuplicateException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<String>() {
      @Override
      public void execute(Deferred<String> deferred) {
        try {
          deferred.reject(TYPED_EXCEPTION);
          deferred.resolve(RESPONSE);
        } catch (PromiseRejectionDuplicateException error) {
          manualHandler.onError(error);
        }
      }
    })).error(PromiseRejectionDuplicateException.class, errorHandler).await();
    // Then
    Assert.assertTrue(manualHandler.hasAcceptedErrors());
    Assert.assertFalse(errorHandler.hasAcceptedErrors());
  }

  @Test
  public void shouldResolvePromisesChain() {
    // Given
    MockResponseHandler<Integer> initialResponseHandler = new MockResponseHandler<Integer>();
    MockResponseHandler<String> chainedResponseHandler = new MockResponseHandler<String>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<Integer>() {
      @Override
      public void execute(Deferred<Integer> deferred) {
        deferred.resolve(100);
      }
    })).then(initialResponseHandler).chain(new Promise.ChainingDeferredFunction<Integer, String>() {
      @Override
      public void execute(Integer previousResult, Deferred<String> deferred) {
        deferred.resolve(previousResult.toString());
      }
    }).then(chainedResponseHandler).await();
    // Then
    Assert.assertTrue(initialResponseHandler.hasResolution(0, 100));
    Assert.assertTrue(chainedResponseHandler.hasResolution(0, "100"));
  }

  @Test
  public void shouldRejectChainedPromiseInCaseWhenInitialResolvedButChainedRejected() {
    // Given
    MockResponseHandler<Integer> initialResponseHandler = new MockResponseHandler<Integer>();
    MockResponseHandler<String> chainedResponseHandler = new MockResponseHandler<String>();
    MockErrorHandler<RuntimeException> chainedErrorHandler =
        new MockErrorHandler<RuntimeException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<Integer>() {
      @Override
      public void execute(Deferred<Integer> deferred) {
        deferred.resolve(100);
      }
    })).then(initialResponseHandler).chain(new Promise.ChainingDeferredFunction<Integer, String>() {
      @Override
      public void execute(Integer previousResult, Deferred<String> deferred) {
        deferred.reject(TYPED_EXCEPTION);
      }
    }).then(chainedResponseHandler).error(RuntimeException.class, chainedErrorHandler).await();
    // Then
    Assert.assertTrue(initialResponseHandler.hasResolution(0, 100));
    Assert.assertFalse(chainedResponseHandler.hasResolutions());
    Assert.assertTrue(chainedErrorHandler.hasAcceptedError(0, TYPED_EXCEPTION));
  }

  @Test
  public void shouldRejectChainedPromiseInCaseWhenInitialResolvedButExceptionThrownInChainedDeferredFunction() {
    // Given
    MockResponseHandler<Integer> initialResponseHandler = new MockResponseHandler<Integer>();
    MockResponseHandler<String> chainedResponseHandler = new MockResponseHandler<String>();
    MockErrorHandler<RuntimeException> chainedErrorHandler =
        new MockErrorHandler<RuntimeException>();
    // When
    WaitingPromise.of(Promises.of(new Deferred.DeferredFunction<Integer>() {
      @Override
      public void execute(Deferred<Integer> deferred) {
        deferred.resolve(100);
      }
    })).then(initialResponseHandler).chain(new Promise.ChainingDeferredFunction<Integer, String>() {
      @Override
      public void execute(Integer previousResult, Deferred<String> deferred) {
        throw TYPED_EXCEPTION;
      }
    }).then(chainedResponseHandler).error(RuntimeException.class, chainedErrorHandler).await();
    // Then
    Assert.assertTrue(initialResponseHandler.hasResolution(0, 100));
    Assert.assertFalse(chainedResponseHandler.hasResolutions());
    Assert.assertTrue(chainedErrorHandler.hasAcceptedError(0, TYPED_EXCEPTION));
  }

  @Test
  public void shouldResolvedPromiseBeCreatedByValue() {
    // Given
    MockResponseHandler<String> responseHandler = new MockResponseHandler<String>();
    // When
    WaitingPromise.of(Promises.resolvedBy(RESPONSE)).then(responseHandler).await();
    // Then
    Assert.assertTrue(responseHandler.hasResolution(0, RESPONSE));
  }
  
  @Test
  public void shouldRejectedPromiseBeCreatedByError() {
    // Given
    MockErrorHandler<RuntimeException> errorHandler =
        new MockErrorHandler<RuntimeException>();
    // When
    WaitingPromise.of(Promises.rejectedBy(TYPED_EXCEPTION)).error(RuntimeException.class, errorHandler).await();
    // Then
    Assert.assertTrue(errorHandler.hasAcceptedError(0, TYPED_EXCEPTION));    
  }
  
  @Test
  public void shouldPromiseBeFinalizedOnResolve() {
    // Given
    MockFinalizingHandler finalizingHandler = new MockFinalizingHandler();
    // When
    WaitingPromise.of(Promises.resolvedBy(RESPONSE)).finalize(finalizingHandler).await();
    // Then
    Assert.assertTrue(finalizingHandler.isFinalized());
  }
  
  @Test
  public void shouldPromiseBeFinalizedOnReject() {
    // Given
    MockFinalizingHandler finalizingHandler = new MockFinalizingHandler();
    // When
    WaitingPromise.of(Promises.rejectedBy(TYPED_EXCEPTION)).finalize(finalizingHandler).await();
    // Then
    Assert.assertTrue(finalizingHandler.isFinalized());
  }
  
  @Test
  public void shouldResultBeReturnedSynchronously() throws Throwable {
    Assert.assertEquals(RESPONSE, Promises.resolvedBy(RESPONSE).get(1L));
  }
  
  @Test(expected = RuntimeException.class)
  public void shouldErrorBeRethrownOnRejection() throws Throwable {
    Promises.rejectedBy(TYPED_EXCEPTION).get(1L);
  }
}
