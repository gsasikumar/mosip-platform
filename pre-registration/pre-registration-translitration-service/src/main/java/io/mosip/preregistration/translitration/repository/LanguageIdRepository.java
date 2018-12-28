package io.mosip.preregistration.translitration.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.translitration.entity.LanguageIdEntity;

/**
 * @author Kishan Rathore
 *
 */
@Repository
public interface LanguageIdRepository extends BaseRepository<LanguageIdEntity, String>{

	/**
	 * @param fromLang
	 * @param toLang
	 * @return the languageId for from language and to language.
	 */
	LanguageIdEntity findByFromLangAndToLang(String fromLang,String toLang);
}
