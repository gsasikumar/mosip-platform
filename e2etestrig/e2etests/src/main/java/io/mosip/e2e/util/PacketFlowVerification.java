package io.mosip.e2e.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 */
import io.mosip.main.PacketUpload;

public class PacketFlowVerification {
	PacketUpload upload=new PacketUpload();
	/**
	 * 
	 * @return a list of packets generated by the reg client jar
	 */
	public List<File> readPacket() {
		List<File> packets=new ArrayList<File>();
		File file=new File(BaseUtil.getGlobalResourcePath()+"/src/test/resources/Packets/");
		File[] listOfFiles=file.listFiles();
		for(File packet:listOfFiles) {
			if(packet.getName().contains(".zip")) {
				packets.add(packet);
					return packets;
			}
		}
		return null;
	}
	public boolean syncPacket(File packet) {
		boolean syncStatus=upload.syncPacket(packet);
		return syncStatus;
	}
	public boolean uploadPacket(File packet) {
		boolean uploadStatus=upload.uploadPacket(packet);
		return uploadStatus;
	}
	public List<String> compareDbStatus(File packet) {
		String regId=packet.getName().substring(0, packet.getName().lastIndexOf("."));
		List<String> dbStatus=upload.compareDbStatus(regId);
		return dbStatus;
		 
	}
	
	public String getUin(String regID) {
		String uin=upload.getUin(regID);
		return uin;
	
	}
}
