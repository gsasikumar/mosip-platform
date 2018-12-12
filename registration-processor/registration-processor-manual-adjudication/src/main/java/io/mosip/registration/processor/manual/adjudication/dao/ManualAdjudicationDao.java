package io.mosip.registration.processor.manual.adjudication.dao;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;





/**
 * Dao Layer for Manual Verification
 * 
 * @author Shuchita
 * @since 0.0.1
 *
 */
@Component
public class ManualAdjudicationDao {
	@Autowired
	private BasePacketRepository<ManualVerificationEntity, String> manualAdjudiacationRepository;
	/**
	 * This method updates Manual Verification Status in DB
	 * 
	 * @param manualAdjudicationEntity
	 *            Entity {@link io.mosip.registration.processor.status.repositary.ManualVerificationEntity}
	 * @return updated {@link ManualVerificationEntity}
	 */
	public ManualVerificationEntity update(ManualVerificationEntity manualAdjudicationEntity) {
		return manualAdjudiacationRepository.save(manualAdjudicationEntity);
	}

	/**
	 * This method finds earliest created unassigned {@link ManualVerificationEntity}.
	 * 
	 * @return the earliest created unassigned {@link ManualVerificationEntity}
	 */
	public List<ManualVerificationEntity> getFirstApplicantDetails(String status) {
		return manualAdjudiacationRepository
				.getFirstApplicantDetails(status);

	}

	/**
	 * @param regId
	 * @param mvUsrId
	 * @return
	 */
	public ManualVerificationEntity getSingleAssignedRecord(String regId, String refId, String mvUsrId) {
		//TODO TO Specify comment for this method
		return manualAdjudiacationRepository.getSingleAssignedRecord(regId, refId, mvUsrId);
	}
	
	public ManualVerificationEntity getAssignedApplicantDetails(String userId, String status) {
		return manualAdjudiacationRepository.getAssignedApplicantDetails(userId,status);
	}
}
