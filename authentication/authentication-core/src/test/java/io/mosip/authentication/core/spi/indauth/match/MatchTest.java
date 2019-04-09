package io.mosip.authentication.core.spi.indauth.match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;
import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

public class MatchTest {

	@Test
	public void TestgetMatchProperties() {
		AuthType authType = new AuthType() {

			@Override
			public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAssociatedMatchType(MatchType matchType) {
				return true;
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDisplayName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<MatchType> getAssociatedMatchTypes() {
				return null;
			}

			@Override
			public Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		boolean authTypeInfoAvailable = authType.isAuthTypeInfoAvailable(authRequestDTO);
		assertFalse(authTypeInfoAvailable);
		IdInfoFetcher languageInfoFetcher = null;
		Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, languageInfoFetcher, null);
		System.err.println(matchProperties);
		System.err.println(authType.getDisplayName());
		System.err.println(authType.getAssociatedMatchTypes());
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue("dinesh");
		nameList.add(identityInfoDTO);
		identity.setName(nameList);
		MatchType matchType = new MatchType() {

			@Override
			public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
				return any -> {
					Map<String, List<IdentityInfoDTO>> valuemap = new HashMap<String, List<IdentityInfoDTO>>();
					valuemap.put("name", nameList);
					return valuemap;
				};
			}

			@Override
			public IdMapping getIdMapping() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Category getCategory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
				return getAllowedMatchingStrategy(MatchingStrategyType.EXACT);
			}
		};

		AuthType[] authTypes = new AuthType[] { authType };
		AuthType.getAuthTypeForMatchType(matchType, authTypes);
		assertNotNull(authType.isAssociatedMatchType(matchType));
		Environment environment = null;
		String newlanguageInfoFetcher = null;
		authType.getMatchingThreshold(authRequestDTO, newlanguageInfoFetcher, environment, languageInfoFetcher);
	}

	@Test
	public void TestMatchtype() throws IdAuthenticationBusinessException {
		AuthType authType = new AuthType() {

			@Override
			public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String getType() {
				return "E";
			}

			@Override
			public Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language) {
				return Optional.ofNullable("OTP");
			}

			@Override
			public String getDisplayName() {
				return "OTP";
			}

			@Override
			public Set<MatchType> getAssociatedMatchTypes() {
				return null;
			}
		};
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue("dinesh");
		nameList.add(identityInfoDTO);
		identity.setName(nameList);
		MatchType matchType = new MatchType() {

			@Override
			public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
				return any -> {
					Map<String, List<IdentityInfoDTO>> valuemap = new HashMap<String, List<IdentityInfoDTO>>();
					valuemap.put("name", nameList);
					return valuemap;
				};
			}

			@Override
			public IdMapping getIdMapping() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Category getCategory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
				return null;
			}
		};
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identity);
		matchType.getIdentityInfoList(requestDTO);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.EXACT);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.PHONETICS);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.DEFAULT_MATCHING_STRATEGY);
		matchType.getCategory();
		matchType.getCategory().BIO.getType();
		matchType.getCategory().DEMO.getType();
		matchType.getCategory().OTP.getType();
		matchType.getIdentityInfoFunction();
		matchType.getReqestInfoFunction();
		matchType.hashCode();
		matchType.hasIdEntityInfo();
		matchType.hasRequestEntityInfo();
		matchType.isMultiLanguage();

		Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<>();
		List<IdentityInfoDTO> nameList1 = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("fra");
		identityInfoDTO2.setValue("dinesh");
		nameList1.add(identityInfoDTO2);
		idEntity.put("name", nameList1);
		matchType.mapEntityInfo(idEntity, null);
		MatchType.Category.getCategory("bio");

		MatchingStrategyType.getMatchStrategyType("E");
		IdMapping idMapping = new IdMapping() {

			@Override
			public String getIdname() {
				// TODO Auto-generated method stub
				return "name";
			}

			@Override
			public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<IdMapping> getSubIdMappings() {
				// TODO Auto-generated method stub
				return Collections.emptySet();
			}
		};
		String name = "name";
		IdMapping[] authTypes = new IdMapping[] { idMapping };
		IdMapping.getIdMapping(name, authTypes);

	}

	@Test
	public void testMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchingStrategy matchingStrategy = new MatchingStrategy() {

			@Override
			public MatchingStrategyType getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MatchFunction getMatchFunction() {
				return (reqInfo, entityInfo, matchProperties) -> 50;
			}
		};

		int match = matchingStrategy.match(new HashMap<>(), new HashMap<>(), new HashMap<>());
		assertThat(match).isEqualTo(50);
	}

	@Test
	public void TestAuthtype() {
		AuthType authType = new AuthType() {

			@Override
			public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
				return true;
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				return true;
			}

			@Override
			public String getType() {
				return "otp";
			}

			@Override
			public Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String language) {
				// TODO Auto-generated method stub
				return Optional.ofNullable("E");
			}

			@Override
			public String getDisplayName() {
				// TODO Auto-generated method stub
				return "OTP";
			}

			@Override
			public Set<MatchType> getAssociatedMatchTypes() {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}

}
