package io.mosip.registration.processor.packet.storage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO1;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import lombok.Data;

/**
 * 
 * @author Girish Yarru
 *
 */
@Component
@Data
public class Utilities {

	private static final String UIN = "UIN";
	public static final String FILE_SEPARATOR = "\\";

	@Autowired
	private FileSystemAdapter adapter;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity;

	@Value("${registration.processor.document.category}")
	private String getRegProcessorDocumentCategory;

	@Value("${registration.processor.applicant.type}")
	private String getRegProcessorApplicantType;

	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;

	public static String getJson(String configServerFileStorageURL, String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
	}

	/*
	 * public int getApplicantAge(String registrationId) throws IOException,
	 * ApisResourceAccessException, ParseException { return 17;
	 * 
	 * }
	 */

	public int getApplicantAge(String registrationId) throws IOException, ApisResourceAccessException, ParseException {
		RegistrationProcessorIdentity regProcessorIdentityJson = getRegistrationProcessorIdentityJson();
		String ageKey = regProcessorIdentityJson.getIdentity().getAge().getValue();
		String dobKey = regProcessorIdentityJson.getIdentity().getDob().getValue();

		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		String applicantDob = JsonUtil.getJSONValue(demographicIdentity, dobKey);
		Integer applicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
		if (applicantDob != null) {
			return calculateAge(applicantDob);
		} else if (applicantAge != null) {
			return applicantAge;

		} else {
			Long uin = getUIn(registrationId);
			JSONObject identityJSONOject = retrieveIdrepoJson(uin);
			String idRepoApplicantDob = JsonUtil.getJSONValue(identityJSONOject, dobKey);
			if (idRepoApplicantDob != null)
				return calculateAge(idRepoApplicantDob);
			Integer idRepoApplicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
			return idRepoApplicantAge != null ? idRepoApplicantAge : 0;

		}

	}

	public RegistrationProcessorIdentity getRegistrationProcessorIdentityJson() throws IOException {
		String getIdentityJsonString = Utilities.getJson(configServerFileStorageURL, getRegProcessorIdentityJson);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		return mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
	}

	public JSONObject getDemographicIdentityJSONObject(String registrationId) throws IOException {

		InputStream idJsonStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);
		JSONObject demographicIdentityJson = (JSONObject) JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicIdentityJson,
				getRegProcessorDemographicIdentity);

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

		return demographicIdentity;

	}

	private int calculateAge(String applicantDob) throws ParseException {
		DateFormat sdf = new SimpleDateFormat(dobFormat);
		Date birthDate = sdf.parse(applicantDob);
		LocalDate ld = new java.sql.Date(birthDate.getTime()).toLocalDate();
		Period p = Period.between(ld, LocalDate.now());
		return p.getYears();
	}

	public Long getUIn(String registrationId) throws IOException {
		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		Number number = JsonUtil.getJSONValue(demographicIdentity, UIN);
		return number != null ? number.longValue() : null;

	}

	public JSONObject retrieveIdrepoJson(Long uin) throws ApisResourceAccessException, IdRepoAppException {

		if (uin != null) {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(String.valueOf(uin));
			IdResponseDTO1 idResponseDto = (IdResponseDTO1) restClientService.getApi(ApiName.RETRIEVEIDENTITY,
					pathSegments, "", "", IdResponseDTO1.class);
			if (!idResponseDto.getErrors().isEmpty())
				throw new IdRepoAppException(
						PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage() + idResponseDto.getErrors().toString());

			idResponseDto.getResponse().getIdentity();
			ObjectMapper objMapper = new ObjectMapper();
			return objMapper.convertValue(idResponseDto.getResponse().getIdentity(), JSONObject.class);

		}

		return null;
	}
}
