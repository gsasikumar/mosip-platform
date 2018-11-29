package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Machine;

/**
 * Repository function to fetching machine details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface MachineRepository extends BaseRepository<Machine, String> {
	/**
	 * This method trigger query to fetch the all Machine details code.
	 * 
	 * @return MachineDetail fetched from database
	 */
	List<Machine> findAllByIsDeletedFalseOrIsDeletedIsNull();

	/**
	 * This method trigger query to fetch the Machine detail for the given machine
	 * id and language code.
	 * 
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * @param langCode
	 *            languageCode provided by user
	 * @return MachineDetail fetched from database
	 */

	Machine findAllByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String id, String langCode);

	/**
	 * This method trigger query to fetch the Machine detail for the given language
	 * code.
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * 
	 * @return MachineDetail fetched from database
	 */

	List<Machine> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

}
