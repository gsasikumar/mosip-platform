package io.mosip.registration.repositories;

import java.util.Optional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.id.RegistartionCenterId;

/**
 * The repository interface for {@link RegistrationCenter} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, RegistartionCenterId> {

	/**
	 * This method returns the optional of {@link RegistrationCenter} based on id
	 * 
	 * @param id
	 *            the registration center id
	 * @return the optional of {@link RegistrationCenter}
	 */
	Optional<RegistrationCenter> findByRegistartionCenterIdIdAndRegistartionCenterIdLangCodeAndIsActiveTrue(String id,String langCode);

}
