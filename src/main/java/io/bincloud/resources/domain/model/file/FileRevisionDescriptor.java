package io.bincloud.resources.domain.model.file;

import java.time.Instant;

public interface FileRevisionDescriptor {
	public String getFileId();

	public Long getResourceId();

	public String getFileName();

	public String getStatus();

	public Instant getCreationMoment();

	public Instant getLastModification();

	public Long getFileSize();
}
