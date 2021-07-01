package io.bincloud.resources.port.adapter.endpoint.files.download;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import io.bincloud.common.domain.model.error.AsyncErrorsHandler;
import io.bincloud.common.domain.model.message.MessageProcessor;
import io.bincloud.resources.domain.model.Constants;
import io.bincloud.resources.domain.model.contracts.download.DownloadListener;
import io.bincloud.resources.domain.model.contracts.download.FileDownloader.FileDownloadRequest;
import io.bincloud.resources.domain.model.errors.ResourceDoesNotExistException;
import io.bincloud.resources.domain.model.errors.ResourceDoesNotHaveUploadsException;
import io.bincloud.resources.domain.model.errors.UnsatisfiableRangeFormatException;
import io.bincloud.resources.domain.model.errors.UnspecifiedResourceException;
import io.bincloud.resources.domain.model.errors.UploadedFileDescriptorHasNotBeenFoundException;
import io.bincloud.resources.port.adapter.endpoint.files.ServletResponseHandler;
import io.bincloud.resources.domain.model.contracts.download.FileRevisionDescriptor;
import io.bincloud.resources.domain.model.contracts.download.Fragment;
import io.bincloud.resources.domain.model.contracts.download.MultiRangeDownloadListener;

public class HttpServletFileRangesDownloadListener implements MultiRangeDownloadListener {
	private final AsyncContext asyncContext;
	private final ServletResponseHandler responseHandler;
	private final AsyncErrorsHandler<AsyncContext> errorsHandler;
	private final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGE";

	public HttpServletFileRangesDownloadListener(AsyncContext asyncContext, MessageProcessor messageProcessor) {
		super();
		this.asyncContext = asyncContext;
		this.responseHandler = new ServletResponseHandler(messageProcessor);
		this.errorsHandler = createErrorsHandler();
	}

	
	@Override
	public void onRequestError(FileDownloadRequest request, Exception error) {
		errorsHandler.handleError(asyncContext, error);
		
	}

	@Override
	public void onDownloadStart(FileRevisionDescriptor revisionDescriptor) {
		
	}

	@Override
	public void onDownloadError(FileRevisionDescriptor revisionDescriptor, Exception error) {
		errorsHandler.handleError(asyncContext, error);
	}

	@Override
	public void onDownloadComplete(FileRevisionDescriptor revisionDescriptor) {
		
	}

	@Override
	public void onFragmentDownloadComplete(FileRevisionDescriptor revisionDescriptor, Fragment fragment) {
		
	}
	
	private AsyncErrorsHandler<AsyncContext> createErrorsHandler() {
		return AsyncErrorsHandler.createFor(AsyncContext.class)
			.registerHandler(UnspecifiedResourceException.class, responseHandler.createErrorHandler(HttpServletResponse.SC_BAD_REQUEST))
			.registerHandler(ResourceDoesNotExistException.class, responseHandler.createErrorHandler(HttpServletResponse.SC_BAD_REQUEST))
			.registerHandler(ResourceDoesNotHaveUploadsException.class, responseHandler.createErrorHandler(HttpServletResponse.SC_NO_CONTENT))
			.registerHandler(UnsatisfiableRangeFormatException.class, responseHandler.createErrorHandler(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE))
			.registerHandler(UploadedFileDescriptorHasNotBeenFoundException.class, responseHandler.createErrorHandler(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
			.registerDefaultHandler(responseHandler.createDefaultErrorHandler(Constants.CONTEXT));
	}
}
