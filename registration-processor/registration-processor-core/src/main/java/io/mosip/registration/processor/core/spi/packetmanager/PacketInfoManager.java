
package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;

/**
 * The Interface PacketInfoManager.
 *
 * @author Horteppa (M1048399)
 * @param <T>
 *            PacketInfoDto
 * @param <A>
 *            the generic type
 */
public interface PacketInfoManager<T, /** D, M, */
		A> {

	/**
	 * Save packet data.
	 *
	 * @param packetInfo
	 *            the packet info
	 */
	public void savePacketData(T packetInfo);

	/**
	 * Save demographic data.
	 *
	 * @param demographicJsonStream
	 *            the demographic json stream
	 * @param metaData
	 *            the meta data
	 */
	public void saveDemographicInfoJson(byte[] bytes, List<FieldValue> metaData);

	/**
	 * Gets the OsiData for QC user.
	 *
	 * @param regid
	 *            the registration id
	 * @return OsiData for the registration id
	 */
	public RegOsiDto getOsi(String regid);

	
	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcUserId
	 *            the qc user id
	 * @return the packetsfor QC user
	 */
	public List<A> getPacketsforQCUser(String qcUserId);

	/**
	 * Find demo by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public List<DemographicInfoDto> findDemoById(String regId);

	/**
	 * Gets the applicant finger print image name by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the applicant finger print image name by id
	 */
	public List<String> getApplicantFingerPrintImageNameById(String regId);

	/**
	 * Gets the applicant iris image name by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the applicant iris image name by id
	 */
	public List<String> getApplicantIrisImageNameById(String regId);

	/**
	 * Gets the applicant registration id by UIN.
	 *
	 * @param uin the uin
	 * @return the registration id by UIN
	 */
	public List<String> getRegIdByUIN(String uin);

	/**
	 * Gets the documents by reg id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the documents by reg id
	 */
	public List<ApplicantDocument> getDocumentsByRegId(String regId);

	/**
	 * Save manual adjudication data.
	 *
	 * @param uniqueMatchedRefIds
	 *            the unique matched ref ids
	 * @param registrationId
	 *            the registration id
	 */


	public void saveManualAdjudicationData(List<String> uniqueMatchedRefIds, String registrationId, DedupeSourceName sourceName);


	/**
	 * Save abis ref.
	 *
	 * @param regAbisRefDto
	 *            the reg abis ref dto
	 */
	public void saveAbisRef(RegAbisRefDto regAbisRefDto);

	/**
	 * Gets the reference id by rid.
	 *
	 * @param rid
	 *            the rid
	 * @return the reference id by rid
	 */
	public List<String> getReferenceIdByRid(String rid);

	/**
	 * Gets the rid by reference id.
	 *
	 * @param refId
	 *            the ref id
	 * @return the rid by reference id
	 */
	public List<String> getRidByReferenceId(String refId);

	/**
	 * Save documents.
	 *
	 * @param documentDtos the document dtos
	 */
	public void saveDocuments(List<Document> documentDtos);

	/**
	 * Gets the UIN by rid.
	 *
	 * @param Rid the rid
	 * @return the UIN by rid
	 */
	public List<String> getUINByRid(String Rid);


}