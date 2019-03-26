package io.mosip.registration.service.sync;

import java.net.URISyntaxException;
import java.util.List;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface PacketSynchService {

	/**
	 * This method is used to fetch the packets from the table which needs to be
	 * synched.
	 * 
	 * @return
	 */
	List<PacketStatusDTO> fetchPacketsToBeSynched();

	/**
	 * This method is used to synch the packets to the server
	 * 
	 * @param syncDtoList
	 * @return
	 * @throws RegBaseCheckedException
	 * @throws URISyntaxException
	 * @throws MosipJsonProcessingException
	 */
	ResponseDTO syncPacketsToServer(RegistrationPacketSyncDTO syncDtoList,String triggerPoint)
			throws RegBaseCheckedException, URISyntaxException, JsonProcessingException;

	/**
	 * This method is used to update the synched packets in the table
	 * 
	 * @param synchedPackets
	 * @return
	 */
	Boolean updateSyncStatus(List<PacketStatusDTO> synchedPackets);

	/**
	 * Gets the packet to sync.
	 *
	 * @param rId the registration id
	 * @return the packet to sync
	 * @throws RegBaseCheckedException
	 */
	String packetSync(String rId) throws RegBaseCheckedException;

	/**
	 * Sync EOD packets.
	 *
	 * @return the string
	 * @throws RegBaseCheckedException
	 */
	String syncEODPackets(List<String> regIds) throws RegBaseCheckedException;

	String packetSync(List<PacketStatusDTO> packetsToBeSynched) throws RegBaseCheckedException;

	void syncAllPackets() throws RegBaseCheckedException;
}