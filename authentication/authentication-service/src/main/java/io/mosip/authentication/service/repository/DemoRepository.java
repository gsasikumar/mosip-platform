package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

/**@author M1046368 Arun Bose
 * The Interface DemoRepository.
 */
@Repository
public interface DemoRepository extends BaseRepository<DemoEntity, Integer> {
	
	/**
	 * Find by uin ref id and lang code.
	 *
	 * @param refId the ref id
	 * @param langCode the lang code
	 * @return the demo entity
	 */
	public DemoEntity findByUinRefIdAndLangCode(String refId,String langCode);
}
