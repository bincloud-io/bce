package io.bcs.port.adapters.file;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.bce.domain.errors.ApplicationException;
import io.bce.domain.errors.ErrorDescriptor.ErrorCode;
import io.bce.domain.errors.UnexpectedErrorException;
import io.bce.interaction.streaming.Streamer;
import io.bce.promises.Promise.ErrorHandler;
import io.bce.promises.Promise.ResponseHandler;
import io.bce.promises.Promises;
import io.bcs.application.ContentService;
import io.bcs.domain.model.Constants;
import io.bcs.domain.model.file.ContentReceiver;
import io.bcs.domain.model.file.ContentUploader;
import io.bcs.domain.model.file.FileNotExistsException;
import io.bcs.domain.model.file.FileNotSpecifiedException;
import io.bcs.domain.model.file.Lifecycle.FileUploadStatistic;
import io.bcs.domain.model.file.UnsatisfiableRangeFormatException;
import io.bcs.domain.model.file.states.ContentNotUploadedException;
import io.bcs.domain.model.file.states.ContentUploadedException;
import io.bcs.domain.model.file.states.FileDisposedException;
import io.bcs.port.adapters.ContentLoadingProperties;

public class ContentLoadingServlet extends HttpServlet {
    private static final long serialVersionUID = 2026798739467262029L;
    private static final String HTTP_RANGES_HEADER = "Ranges";
    private static final String FILE_STORAGE_NAME_PARAMETER = "fileStorageName";
    private static final String BOUNDED_CONTEXT_HEADER = "X-BC-CONTEXT";
    private static final String ERROR_CODE_HEADER = "X-BC-ERR-CODE";
    private static final String ERROR_SEVERITY_HEADER = "X-BC-ERR-SEVERITY";
    private static final String UPLOADED_SIZE_HEADER = "X-BC-UPLOADED-SIZE";

    @Inject
    private Streamer streamer;

    @Inject
    private ContentService contentService;

    @Inject
    private ContentLoadingProperties contentLoadingProperties;

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        executeAsynchronously(request, response, asyncContext -> {
            downloadContent(asyncContext, request, response, () -> createHeadersOnlyContentReceiver(response));
        });
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        executeAsynchronously(request, response, asyncContext -> {
            downloadContent(asyncContext, request, response, () -> createFileDataContentReceiver(response));
        });
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        executeAsynchronously(request, response, asyncContext -> {
            uploadContent(asyncContext, request, response);

        });
    }

    private void uploadContent(AsyncContext asyncContext, HttpServletRequest request, HttpServletResponse response) {
        Promises.<FileUploadStatistic>of(deferred -> {
            ContentUploader contentUploader = createFileContentUploader(request);
            contentService.upload(contentUploader).execute(getStorageFileNameParam(request)).delegate(deferred);
        }).then(uploadSuccessHandler(response))
                .error(FileNotSpecifiedException.class, applicationError(response, HttpServletResponse.SC_BAD_REQUEST))
                .error(FileNotExistsException.class, applicationError(response, HttpServletResponse.SC_NOT_FOUND))
                .error(ContentUploadedException.class, applicationError(response, HttpServletResponse.SC_CONFLICT))
                .error(FileDisposedException.class, applicationError(response, HttpServletResponse.SC_NOT_FOUND))
                .error(unrecognizedErrorHandler(response)).finalize(() -> asyncContext.complete());
    }

    private void downloadContent(AsyncContext asyncContext, HttpServletRequest request, HttpServletResponse response,
            Supplier<ContentReceiver> receiverProvider) {
        Promises.<Void>of(deferred -> {
            ContentReceiver receiver = receiverProvider.get();
            contentService.download(receiver).execute(new HttpServletDownloadCommand(request)).delegate(deferred);
        }).error(FileNotSpecifiedException.class, applicationError(response, HttpServletResponse.SC_BAD_REQUEST))
                .error(FileNotExistsException.class, applicationError(response, HttpServletResponse.SC_NOT_FOUND))
                .error(UnsatisfiableRangeFormatException.class,
                        applicationError(response, HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE))
                .error(ContentNotUploadedException.class, applicationError(response, HttpServletResponse.SC_NOT_FOUND))
                .error(FileDisposedException.class, applicationError(response, HttpServletResponse.SC_NOT_FOUND))
                .error(unrecognizedErrorHandler(response)).finalize(() -> asyncContext.complete());
    }

    private ResponseHandler<FileUploadStatistic> uploadSuccessHandler(HttpServletResponse response) {
        return result -> {
            response.setHeader(BOUNDED_CONTEXT_HEADER, Constants.CONTEXT.toString());
            response.setHeader(ERROR_CODE_HEADER, ErrorCode.SUCCESSFUL_COMPLETED_CODE.extract().toString());
            response.setHeader(UPLOADED_SIZE_HEADER, result.getContentLength().toString());
            response.setStatus(HttpServletResponse.SC_OK);
        };
    }

    private ErrorHandler<Throwable> unrecognizedErrorHandler(HttpServletResponse response) {
        ErrorHandler<UnexpectedErrorException> unexpectedErrorHandler = applicationError(response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return error -> unexpectedErrorHandler.onError(new UnexpectedErrorException(error));
    }

    private <E extends ApplicationException> ErrorHandler<E> applicationError(HttpServletResponse response,
            int statusCode) {
        return error -> {
            response.setHeader(BOUNDED_CONTEXT_HEADER, error.getContextId().toString());
            response.setHeader(ERROR_CODE_HEADER, error.getErrorCode().extract().toString());
            response.setHeader(ERROR_SEVERITY_HEADER, error.getErrorSeverity().toString());
            response.setStatus(statusCode);
        };
    }

    private <T> void executeAsynchronously(HttpServletRequest servletRequest, HttpServletResponse response,
            Consumer<AsyncContext> methodExecutor) {
        methodExecutor.accept(servletRequest.startAsync(servletRequest, response));
    }

    private ContentReceiver createHeadersOnlyContentReceiver(HttpServletResponse response) {
        return new HttpHeadersReceiver(response);
    }

    private ContentReceiver createFileDataContentReceiver(HttpServletResponse response) {
        try {
            return new HttpFileDataReceiver(streamer, response);
        } catch (IOException error) {
            throw new UnexpectedErrorException(error);
        }
    }

    private ContentUploader createFileContentUploader(HttpServletRequest request) {
        try {
            return new HttpFileContentUploader(streamer, request, contentLoadingProperties.getBufferSize());
        } catch (IOException error) {
            throw new UnexpectedErrorException(error);
        }
    }

    private static Optional<String> getStorageFileNameParam(HttpServletRequest request) {
        return normalizeParameterValue(Optional.ofNullable(request.getParameter(FILE_STORAGE_NAME_PARAMETER)));
    }

    private static HttpRanges getHttpRanges(HttpServletRequest request) {
        return new HttpRanges(normalizeParameterValue(Optional.ofNullable(request.getHeader(HTTP_RANGES_HEADER))));
    }

    private static Optional<String> normalizeParameterValue(Optional<String> notNormalizedValue) {
        return notNormalizedValue.map(String::trim).filter(value -> !value.isEmpty());
    }

    private static class HttpServletDownloadCommand extends HttpDownloadCommand {
        public HttpServletDownloadCommand(HttpServletRequest request) {
            super(getStorageFileNameParam(request), getHttpRanges(request));
        }
    }
}
