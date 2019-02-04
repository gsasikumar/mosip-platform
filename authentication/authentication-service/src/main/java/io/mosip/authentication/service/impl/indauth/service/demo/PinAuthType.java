package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;

/**
 * The Enum PinAuthType.
 * 
 * @author Sanjay Murali
 */
public enum PinAuthType implements AuthType {

	// @formatter:off

	SPIN("pin", setOf(PinMatchType.SPIN), AuthTypeDTO::isPin, "PIN"),
	OTP("otp", setOf(PinMatchType.OTP), AuthTypeDTO::isOtp, "OTP") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher, String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getPinInfo().stream().filter(pininfo -> pininfo.getType().equalsIgnoreCase(this.getType()))
					.forEach((PinInfo pininfovalue) -> {
						ValidateOtpFunction func = idInfoFetcher
								.getValidateOTPFunction();
						valueMap.put(ValidateOtpFunction.class.getSimpleName(), func);
					});
			return valueMap;
		}
	};

	/** The type. */
	private String type;

	/** The associated match types. */
	private Set<MatchType> associatedMatchTypes;

	/** The auth type predicate. */
	private Predicate<? super AuthTypeDTO> authTypePredicate;

	/** The display name. */
	private String displayName;

	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param langType             the lang type
	 * @param authTypePredicate    the auth type predicate
	 * @param displayName          the display name
	 */
	private PinAuthType(String type, Set<MatchType> associatedMatchTypes,
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		this.type = type;
		this.authTypePredicate = authTypePredicate;
		this.displayName = displayName;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.impl.indauth.builder.AuthType#getDisplayName(
	 * )
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * Sets the of.
	 *
	 * @param supportedMatchTypes the supported match types
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * isAssociatedMatchType(io.mosip.authentication.service.impl.indauth.service.
	 * demo.MatchType)
	 */
	@Override
	public boolean isAssociatedMatchType(MatchType matchType) {
		return associatedMatchTypes.contains(matchType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * isAuthTypeEnabled(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return Optional.of(authReq).map(AuthRequestDTO::getAuthType).filter(authTypePredicate).isPresent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getMatchingStrategy(io.mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * java.util.function.Function)
	 */
	@Override
	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
			String languageInfoFetcher) {
		return getMatchInfo(authReq, languageInfoFetcher, MatchInfo::getMatchingStrategy);

	}

	/**
	 * Gets the match info.
	 *
	 * @param                     <T> the generic type
	 * @param authReq             the auth req
	 * @param languageInfoFetcher the language info fetcher
	 * @param infoFunction        the info function
	 * @return the match info
	 */
	private <T> Optional<T> getMatchInfo(AuthRequestDTO authReq, String languageInfoFetcher,
			Function<? super MatchInfo, ? extends T> infoFunction) {
		return Optional.of(authReq)
				.flatMap(authReqDTO -> getMatchInfo(authReqDTO.getMatchInfo(), languageInfoFetcher, infoFunction));
	}

	/**
	 * Gets the match info.
	 *
	 * @param                     <T> the generic type
	 * @param matchInfos          the match infos
	 * @param languageInfoFetcher the language info fetcher
	 * @param infoFunction        the info function
	 * @return the match info
	 */
	private <T> Optional<T> getMatchInfo(List<MatchInfo> matchInfos, String language,
			Function<? super MatchInfo, ? extends T> infoFunction) {
		if (matchInfos != null) {
			return matchInfos.parallelStream()
					.filter(id -> id.getLanguage() != null && language.equalsIgnoreCase(id.getLanguage())
							&& getType().equals(id.getAuthType()))
					.<T>map(infoFunction).filter(Objects::nonNull).findAny();
		} else {
			return Optional.empty();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getAssociatedMatchTypes()
	 */
	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchProperties(io
	 * .mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher, String language) {
		HashMap<String, Object> valuemap = new HashMap<>();
		Optional<String> languageNameOpt = idInfoFetcher.getLanguageName(language);
		valuemap.put("language", languageNameOpt.orElse("english"));
		return valuemap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#
	 * isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.
	 * AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getPinInfo()).flatMap(
				list -> list.stream().filter(pinInfo -> pinInfo.getType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

}
