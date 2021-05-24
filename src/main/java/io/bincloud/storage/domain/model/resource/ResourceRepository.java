package io.bincloud.storage.domain.model.resource;

import java.util.Optional;

public interface ResourceRepository {
	public boolean isExists(Long id);
	public Optional<Resource> findById(Long id);
	public void save(Resource resource);
	public void remove(Long id);
}
