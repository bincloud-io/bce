package io.bcs.storage.application.upload

import static java.util.concurrent.TimeUnit.SECONDS

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch

import io.bce.domain.errors.ApplicationException
import io.bce.domain.errors.ErrorDescriptor.ErrorSeverity
import io.bce.interaction.streaming.Stream
import io.bce.interaction.streaming.Streamer
import io.bce.interaction.streaming.Stream.Stat
import io.bce.interaction.streaming.binary.BinaryDestination
import io.bce.interaction.streaming.binary.BinarySource
import io.bce.promises.Promises
import io.bcs.common.domain.model.io.transfer.CompletionCallback
import io.bcs.common.domain.model.io.transfer.Transmitter
import io.bcs.storage.domain.model.FileId
import io.bcs.storage.domain.model.FileRevision
import io.bcs.storage.domain.model.FileRevisionRepository
import io.bcs.storage.domain.model.FilesystemAccessor
import io.bcs.storage.domain.model.FileRevision.FileRevisionState
import io.bcs.storage.domain.model.contracts.FileDescriptor
import io.bcs.storage.domain.model.contracts.FilePointer
import io.bcs.storage.domain.model.contracts.upload.FileUploadListener
import io.bcs.storage.domain.model.contracts.upload.FileUploader
import io.bcs.storage.domain.model.contracts.upload.FileUploader.UploadFileCommand
import io.bcs.storage.domain.model.errors.UnspecifiedRevisionNameException
import io.bcs.storage.domain.model.states.CreatedFileRevisionState
import io.bcs.storage.domain.model.states.DisposedFileRevisionState
import io.bcs.storage.domain.model.states.DistributionFileRevisionState
import io.bcs.storage.domain.model.states.FileDoesNotExistException
import io.bcs.storage.domain.model.states.FileHasAlreadyBeenDisposedException
import io.bcs.storage.domain.model.states.FileHasAlreadyBeenUploadedException
import spock.lang.Specification

class UploadFileToResourceFeature extends Specification {
	private static final String FILE_NAME = "file.txt"
	private static final String FILE_MEDIA_TYPE = "application/media"
	private static final String FILE_DISPOSITION = "inline"
	private static final FileId FILE_REVISION_NAME = new FileId("12345")
	private static final Instant CREATION_MOMENT = Instant.now()
	private static final Instant LAST_MODIFICATION = CREATION_MOMENT.plus(1, ChronoUnit.MINUTES)
	private static final Long FILE_SIZE = 100L

	private BinarySource source
	private BinaryDestination destination
	private FileRevisionRepository fileRepository
	private FilesystemAccessor filesystemAccessor

	private Streamer dataStreamer
	private FileUploadListener uploadListener
	private FileUploader fileUploader

	def setup() {
		this.source = Stub(BinarySource)
		this.destination = Stub(BinaryDestination)
		this.filesystemAccessor = Stub(FilesystemAccessor)
		this.fileRepository = Mock(FileRevisionRepository)
		this.uploadListener = Mock(FileUploadListener)
		this.dataStreamer = Mock(Streamer)
		this.fileUploader = new FileUploadService(fileRepository, filesystemAccessor, dataStreamer)
	}
	
	def "Scenario: upload content to the file with unspecified fliesystem name"() {
		ApplicationException thrownError;
		CountDownLatch latch = new CountDownLatch(1)

		given: "The upload file command"
		UploadFileCommand command = createUploadCommandWithEmptyRevisonName()

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).error(ApplicationException, {error ->
			thrownError = error
			latch.countDown()
		})
		
		await(latch)
		
		then: "The wrong file pointer format error should be passed to the upload listener"
		thrownError.getErrorSeverity() == ErrorSeverity.BUSINESS
		thrownError.getContextId() == UnspecifiedRevisionNameException.CONTEXT
		thrownError.getErrorCode() == UnspecifiedRevisionNameException.ERROR_CODE
	}

	def "Scenario: upload content to the unknown resource id"() {
		ApplicationException thrownError;
		CountDownLatch latch = new CountDownLatch(1)

		given: "The upload file command"
		UploadFileCommand command = createValidUploadCommand()

		and: "The file with this id doesn't exist in the repository"
		fileRepository.findById(FILE_REVISION_NAME) >> Optional.empty()

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).error(ApplicationException, {error ->
			thrownError = error
			latch.countDown()
		})

		await(latch)

		then: "The wrong file pointer format error should be passed to the upload listener"
		thrownError.getErrorSeverity() == ErrorSeverity.BUSINESS
		thrownError.getContextId() == FileDoesNotExistException.CONTEXT
		thrownError.getErrorCode() == FileDoesNotExistException.ERROR_CODE
	}

	def "Scenario: file is successfuly uploaded to the existing resource"() {
		FileDescriptor storedFileDescriptor
		FileDescriptor fileDescriptor
		CountDownLatch latch = new CountDownLatch(1)

		given: "The upload file command"
		UploadFileCommand command = createValidUploadCommand()

		and: "The file, containing into repository, has the created state"
		fileRepository.findById(FILE_REVISION_NAME) >> Optional.of(createFile(new CreatedFileRevisionState(), FILE_SIZE))

		and: "There is access on write to the filesystem"
		filesystemAccessor.getAccessOnWrite(FILE_REVISION_NAME.getFilesystemName()) >> destination

		and: "The file content transferring is completed successfully"
		initSuccessfulTransferring();

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).then({response ->
			fileDescriptor = response
			latch.countDown()
		})
		
		await(latch)

		then: "Upload listener should be successfully completed with received file descriptor"
		and: "File should be stored to the repository"
		1 * fileRepository.save(_) >> {FileRevision revision ->
			storedFileDescriptor = revision.getDescriptor()
		}

		and: "The file size should be updated to the requested size"
		storedFileDescriptor.getFileSize() == FILE_SIZE

		and: "The file descriptor state should be corresponded to file state"
		fileDescriptor.getRevisionName() == storedFileDescriptor.getRevisionName()
		fileDescriptor.getStatus() == storedFileDescriptor.getStatus()
		fileDescriptor.getFileName() == storedFileDescriptor.getFileName()
		fileDescriptor.getMediaType() == storedFileDescriptor.getMediaType()
		fileDescriptor.getContentDisposition() == storedFileDescriptor.getContentDisposition()
		fileDescriptor.getCreationMoment() == storedFileDescriptor.getCreationMoment()
		fileDescriptor.getLastModification() == storedFileDescriptor.getLastModification()
		fileDescriptor.getFileSize() == storedFileDescriptor.getFileSize()
	}


	
	def "Scenario: upload content to the not acceptable file state"() {
		ApplicationException thrownError;
		CountDownLatch latch = new CountDownLatch(1)

		given: "The upload file command"
		UploadFileCommand command = createValidUploadCommand() 

		and: "The file, containing into repository, has the wrong state"
		fileRepository.findById(FILE_REVISION_NAME) >> Optional.of(createFile(fileState, FILE_SIZE))

		and: "There is access on write to the filesystem"
		filesystemAccessor.getAccessOnWrite(FILE_REVISION_NAME.getFilesystemName()) >> destination

		and: "The file content transferring is completed successfully"
		initSuccessfulTransferring();

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).error(ApplicationException, { error ->
			thrownError = error;
			latch.countDown()
		})

		await(latch)

		then: "Upload listener should be completed with error"
		thrownError.getErrorSeverity() == errorSeverity
		thrownError.getContextId() == errorContext
		thrownError.getErrorCode() == errorCode

		and: "File shouldn't be stored to the repository"
		0 * fileRepository.save(_)

		where:
		fileState                             | errorSeverity           | errorContext                                | errorCode
		new DistributionFileRevisionState()   | ErrorSeverity.BUSINESS  | FileHasAlreadyBeenUploadedException.CONTEXT | FileHasAlreadyBeenUploadedException.ERROR_CODE
		new DisposedFileRevisionState()       | ErrorSeverity.BUSINESS  | FileHasAlreadyBeenDisposedException.CONTEXT | FileHasAlreadyBeenDisposedException.ERROR_CODE

	}

	def "Scenario: error during getting access to the filesystem"() {
		Exception error = new RuntimeException("ERROR");
		Exception thrownError
		CountDownLatch latch = new CountDownLatch(1)
		
		given: "The upload file command"
		UploadFileCommand command = createValidUploadCommand()

		and: "The file, containing into repository, has the created state"
		fileRepository.findById(FILE_REVISION_NAME) >> Optional.of(createFile(new CreatedFileRevisionState(), FILE_SIZE))

		and: "There isn't access on write to the filesystem"
		filesystemAccessor.getAccessOnWrite(FILE_REVISION_NAME.getFilesystemName()) >> {throw error}
		

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).error(Exception, {err -> 
			thrownError = err
			latch.countDown()
		})
		
		await(latch)

		then: "Upload listener should be completed with error"
		thrownError.is(error)
	}

	def "Scenario: error during file content transferring"() {
		Exception error = new Exception("ERROR");
		Exception thrownError
		CountDownLatch latch = new CountDownLatch(1)
		
		given: "The upload file command"
		UploadFileCommand command = createValidUploadCommand()

		and: "The file, containing into repository, has the created state"
		fileRepository.findById(FILE_REVISION_NAME) >> Optional.of(createFile(new CreatedFileRevisionState(), FILE_SIZE))

		and: "There is access on write to the filesystem"
		filesystemAccessor.getAccessOnWrite(FILE_REVISION_NAME.getFilesystemName(), FILE_SIZE) >> destination

		and: "The file content transferring is completed successfully"
		initErrorTransferring(error)

		when: "The file upload is requested"
		fileUploader.uploadFileContent(command, source).error(Exception, {err ->
			thrownError = err
			latch.countDown()
		})
		
		await(latch)

		then: "Upload listener should be completed with error"
		thrownError.is(error)
	}
	
	private void await(CountDownLatch latch) {
		if (!latch.await(30, SECONDS)) {
			throw new RuntimeException("Waiting time is out.")
		}
	}

	private UploadFileCommand createValidUploadCommand() {
		return new UploadFileCommand() {
			@Override
			public Optional<FileId> getRevisionName() {
				return Optional.of(FILE_REVISION_NAME);
			}
		}
	}
	
	private UploadFileCommand createUploadCommandWithEmptyRevisonName() {
		new UploadFileCommand() {
			@Override
			public Optional<FileId> getRevisionName() {
				return Optional.empty()
			}
		}
	}
	
	private void initSuccessfulTransferring() {
		dataStreamer.createStream(_, _) >> {
			return Stub(Stream) {
				start() >> {
					return Promises.of({deferred ->
						deferred.resolve(new Stat() {
							@Override
							public Long getSize() {
								return FILE_SIZE;
							}
						})
					})
				}
			}
		}
	}

	private void initErrorTransferring(Exception error) {
		dataStreamer.createStream(_, _) >> {
			return Stub(Stream) {
				start() >> {
					return Promises.of({deferred -> deferred.reject(error)})
				}
			}
		}
	}

	private FileRevision createFile(FileRevisionState fileState, Long fileSize) {
		return FileRevision.builder()
				.revisionName(FILE_REVISION_NAME)
				.fileName(FILE_NAME)
				.mediaType(FILE_MEDIA_TYPE)
				.contentDisposition(FILE_DISPOSITION)
				.creationMoment(CREATION_MOMENT)
				.lastModification(LAST_MODIFICATION)
				.state(fileState)
				.fileSize(fileSize)
				.build()
	}
}
