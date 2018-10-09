package io.mosip.registration.repositary;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationEntity;

@Repository
public interface RegistrationRepositary extends BaseRepository<RegistrationEntity, String> {

	public List<RegistrationEntity>  findBygroupId(String groupId);
}
