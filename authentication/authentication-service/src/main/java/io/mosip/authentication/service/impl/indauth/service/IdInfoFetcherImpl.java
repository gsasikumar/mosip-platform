package io.mosip.authentication.service.impl.indauth.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.BiometricProviderFactory;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.authentication.service.impl.indauth.service.bio.BioMatchType;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.util.CryptoUtil;

@Service
public class IdInfoFetcherImpl implements IdInfoFetcher {

	private static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	/** The BiometricProviderFactory value */
	@Autowired
	private BiometricProviderFactory biometricProviderFactory;

	/** The OTPManager */
	@Autowired
	private OTPManager otpManager;

	@Autowired
	private CbeffUtil cbeffUtil;

	@Autowired
	private MasterDataManager masterDataManager;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/**
	 * Fetch language code from properties
	 *
	 * @param langType - the language code
	 * @return the language code
	 */
	@Override
	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(PRIMARY_LANG_CODE);
		} else {
			return environment.getProperty(SECONDARY_LANG_CODE);
		}
	}

	/*
	 * getLanguageCode Fetch language Name based on language code
	 */
	@Override
	public Optional<String> getLanguageName(String languageCode) {
		String languagName = null;
		String key = null;
		if (languageCode != null) {
			key = "mosip.phonetic.lang.".concat(languageCode.toLowerCase()); // mosip.phonetic.lang.
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				String[] split = property.split("-");
				languagName = split[0];
			}
		}
		return Optional.ofNullable(languagName);
	}

	/**
	 * Fetch Identity info based on Match type and Identity
	 *
	 * 
	 * @return Map
	 */
	@Override
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, RequestDTO identity, String language) {
		return getInfo(matchType.getIdentityInfoFunction().apply(identity), language);
	}

	/**
	 * Fetch the Identity info based on Identity Info map and Language.
	 *
	 * @param idInfosMap           the id infos map
	 * @param languageForMatchType the language for match type
	 * @return the info
	 */
	private Map<String, String> getInfo(Map<String, List<IdentityInfoDTO>> idInfosMap, String languageForMatchType) {
		if (idInfosMap != null && !idInfosMap.isEmpty()) {
			return idInfosMap.entrySet().parallelStream()

					.map(entry -> new SimpleEntry<String, String>(entry.getKey(),
							Optional.ofNullable(entry.getValue()).flatMap(value -> value.stream()
									.filter(idInfo -> checkLanguageType(languageForMatchType, idInfo.getLanguage()))
									.map(IdentityInfoDTO::getValue).findAny()).orElse("")))
					.filter(entry -> entry.getValue().length() > 0)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		return Collections.emptyMap();
	}

	/**
	 * Check language type.
	 *
	 * @param languageForMatchType the language for match type
	 * @param languageFromReq      the language from req
	 * @return true, if successful
	 */
	public boolean checkLanguageType(String languageForMatchType, String languageFromReq) {
		if (languageForMatchType == null || languageFromReq == null || languageFromReq.isEmpty()
				|| languageFromReq.equalsIgnoreCase("null")) {
			return languageForMatchType == null
					|| getLanguageCode(LanguageType.PRIMARY_LANG).equalsIgnoreCase(languageForMatchType);
		} else {
			return languageForMatchType.equalsIgnoreCase(languageFromReq);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#getIrisProvider(
	 * io.mosip.authentication.core.dto.indauth.BioInfo)
	 */
	@Override
	public MosipBiometricProvider getIrisProvider(DataDTO bioinfovalue) {
		return biometricProviderFactory.getBiometricProvider(bioinfovalue);
	}

	/**
	 * Gets the finger print provider.
	 *
	 * @param bioinfovalue the bioinfovalue
	 * @return the finger print provider
	 */
	@Override
	public MosipBiometricProvider getFingerPrintProvider(DataDTO bioinfovalue) {
		return biometricProviderFactory.getBiometricProvider(bioinfovalue);
	}

	@Override
	public MosipBiometricProvider getFaceProvider(DataDTO bioinfovalue) {
		return biometricProviderFactory.getBiometricProvider(bioinfovalue);
	}

	@Override
	public ValidateOtpFunction getValidateOTPFunction() {
		return otpManager::validateOtp;
	}

	@Override
	public Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValues(Map<String, List<IdentityInfoDTO>> idEntity,
			CbeffDocType type, MatchType matchType) throws IdAuthenticationBusinessException {
		Optional<String> identityValue = getIdentityValue("documents." + INDIVIDUAL_BIOMETRICS, null, idEntity)
				.findAny();
		if (identityValue.isPresent()) {
			Map<String, String> bdbBasedOnType;
			try {
				bdbBasedOnType = cbeffUtil.getBDBBasedOnType(CryptoUtil.decodeBase64(identityValue.get()),
						type.getName(), null);
			} catch (Exception e) {
				// TODO Add corresponding error code and message
				throw new IdAuthenticationBusinessException("Inside getCbeffValues", "", e);
			}
			return bdbBasedOnType.entrySet().stream()
					.collect(Collectors.toMap(Entry<String, String>::getKey, (Entry<String, String> entry) -> {
						IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
						identityInfoDTO.setValue(entry.getValue());
						List<IdentityInfoDTO> idenityList = new ArrayList<>(1);
						idenityList.add(identityInfoDTO);
						return new SimpleEntry<>(getNameForCbeffName(entry.getKey(), matchType), idenityList);
					}));
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * Fetch the identity value.
	 *
	 * @param name                 the name
	 * @param languageForMatchType the language for match type
	 * @param demoInfo             the demo info
	 * @return the identity value
	 */
	private Stream<String> getIdentityValue(String name, String languageForMatchType,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}

		return Stream.empty();
	}

	private String getNameForCbeffName(String cbeffName, MatchType matchType) {
		return Stream.of(IdaIdMapping.values()).filter(cfg -> matchType.getIdMapping().equals(cfg)
				|| matchType.getIdMapping().getSubIdMappings().contains(cfg)).map(cfg -> {
					String idname;
					Set<IdMapping> subIdMappings = matchType.getIdMapping().getSubIdMappings();
					if (!subIdMappings.isEmpty() && matchType instanceof BioMatchType) {
						idname = Stream.of(((BioMatchType) matchType).getMatchTypesForSubIdMappings(subIdMappings))
								.filter(bioMatchType -> bioMatchType.getIdMapping().getMappingFunction()
										.apply(idMappingConfig, bioMatchType).contains(cbeffName))
								.findFirst().map(MatchType::getIdMapping).map(IdMapping::getIdname)
								.orElse(cfg.getIdname());
					} else {
						idname = cfg.getIdname();
					}
					List<String> cbeffNames = cfg.getMappingFunction().apply(idMappingConfig, matchType);
					return new SimpleEntry<>(idname, cbeffNames);
				}).filter(entry -> entry.getValue().stream().anyMatch(v -> v.equalsIgnoreCase(cbeffName)))
				.map(Entry::getKey).findAny().orElse("");
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public MasterDataFetcher getTitleFetcher() {
		return masterDataManager::fetchTitles;
	}

	/**
	 * Gets the uin or vid.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the uin or vid
	 */
	@Override
	public Optional<String> getUinOrVid(AuthRequestDTO authRequestDTO) {
		String individualId = authRequestDTO.getIndividualId();
		Optional<String> id = Optional.of(individualId);
		if (id.isPresent()) {
			return id;
		}
		return null;
	}

	/**
	 * Gets the uin or vid type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the uin or vid type
	 */
	@Override
	public IdType getUinOrVidType(AuthRequestDTO authRequestDTO) {
		String individualIdType = authRequestDTO.getIndividualIdType();
		if (individualIdType.equals(IdType.UIN.getType())) {
			return IdType.UIN;
		} else if (individualIdType.equals(IdType.VID.getType())) {
			return IdType.VID;
		}
		return null;
	}

}
