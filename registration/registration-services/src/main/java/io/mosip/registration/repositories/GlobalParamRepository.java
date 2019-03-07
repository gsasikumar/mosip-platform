package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.entity.GlobalParam;

/**
 * The repository interface for {@link GlobalParam} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamRepository extends BaseRepository<GlobalParam, String> {

	/**
	 * Retrieving global params.
	 *
	 * @return list of global param name
	 */
	List<GlobalParamName> findByIsActiveTrue();

	GlobalParam findByName(String name);

	/**
	 * Get All global params
	 * 
	 * @param names
	 *            global param names
	 * @return list of global param
	 */
	List<GlobalParam> findByNameIn(List<String> names);
}
