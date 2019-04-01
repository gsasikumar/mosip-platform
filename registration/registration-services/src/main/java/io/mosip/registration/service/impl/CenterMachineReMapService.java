package io.mosip.registration.service.impl;

public interface CenterMachineReMapService {

	/**
	 * Checks and handles all the operations to be done when the machine is re
	 * mapped to another center
	 * 
	 * @param step
	 *            - step number for a particular remap operation
	 */
	void handleReMapProcess(int step);

	/**
	 * checks if there is any Registration packets are not yet been processed by the
	 * Reg processor
	 * 
	 * 
	 * @return boolean - true if packet pending for approval
	 */
	boolean isPacketsPendingForProcessing();

	/**
	 * Checks if there is any Reg packets are pending for EOD Approval
	 * 
	 * @return boolean - true if packet pending for EOD
	 */
	boolean isPacketsPendingForEOD();

	/**
	 * checks if the Machine has been re mapped to some other center
	 * 
	 * @return Boolean - true if machine is remappped
	 */
	Boolean isMachineRemapped();
}