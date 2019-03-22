package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;

/**
 * 
 * @author Sayeri Mishra
 *
 */
public class PacketValidator {

	private static Logger logger = Logger.getLogger(PacketValidator.class);
	RegProcTransactionDb packetTransaction=new RegProcTransactionDb();
	final String configPath= "src/test/resources/regProc/Stagevalidation";
		
	ConnectionUtils connectionUtil = new ConnectionUtils();
	FileSystemAdapter adapter = new HDFSAdapterImpl(connectionUtil);


	/**
	 * This method is used for validation of packet validation stage
	 * 
	 * @param testcaseName
	 * @throws AmazonServiceException
	 * @throws SdkClientException
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean packetValidatorStage(File packet)throws IOException, ParseException{
		String regId = packet.getName();	

		boolean isPacketValidated = false;
		//using decrypt api to extract files present inside the packet
		File[] listOfFiles = packet.listFiles();
		List<String> docListInPacketInfo = new ArrayList<String>();
		for(File f : listOfFiles){

			// extracting file names present in hashsequence1 and hashsequence2 of
			//packet_meta_info.json
			if(f.getName().contains("packet_meta_info.json")){
				JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				docListInPacketInfo = getHashSequenceFiles("hashSequence1",docListInPacketInfo, objectData);
				docListInPacketInfo = getHashSequenceFiles("hashSequence2", docListInPacketInfo, objectData);
				logger.info("refactored docList: "+docListInPacketInfo);
			}
		}

		//Extracting folders/files from the decrpyted packet
		List<String> documents = new ArrayList<>();
		List<String> listOfIDDocs = new ArrayList<>();
		File[] folders = packet.listFiles();
		for (int j = 0; j < folders.length; j++) {
			if (folders[j].isDirectory()) {
				File[] listOfDocs = folders[j].listFiles();
				for (File d : listOfDocs){
					documents.add(folders[j].getName()+"\\"+ d.getName());	
				}

				//read ID.json for document verification
				if(folders[j].getName().matches("Demographic")){
					File[] listOfDocsInDemographics = folders[j].listFiles();
					for (File d : listOfDocsInDemographics){
						if(d.getName().matches("ID.json")){
							JSONObject idData = (JSONObject) new JSONParser().parse(new FileReader(d.getPath()));
							JSONObject identity = (JSONObject) idData.get("identity");
							JSONObject proofOfAddress = (JSONObject) identity.get("proofOfAddress");
							String poa = proofOfAddress.get("value").toString();
							listOfIDDocs.add(poa);
							JSONObject proofOfIdentity = (JSONObject) identity.get("proofOfIdentity");
							String poi = proofOfIdentity.get("value").toString();
							listOfIDDocs.add(poi);
							JSONObject proofOfRelationship = (JSONObject) identity.get("proofOfRelationship");
							String por = proofOfRelationship.get("value").toString();
							listOfIDDocs.add(por);
							JSONObject proofOfDateOfBirth = (JSONObject) identity.get("proofOfDateOfBirth");
							String pob = proofOfDateOfBirth.get("value").toString();
							listOfIDDocs.add(pob);
							JSONObject parentOrGuardianBiometrics = (JSONObject) identity.get("parentOrGuardianBiometrics");
							String poib = parentOrGuardianBiometrics.get("value").toString();
							listOfIDDocs.add(poib);
							logger.info("listOfIDDocs : "+listOfIDDocs);
						}
					}
				}

			}else if(folders[j].isFile() && !folders[j].getName().matches("packet_meta_info.json")&& !folders[j].getName().matches("packet_data_hash.txt")
					&& !folders[j].getName().matches("packet_osi_hash.txt"))
				documents.add(folders[j].getName());
		}
		logger.info("documents : "+documents);

		//file validation
		List<String> modifiedList = new ArrayList<>();
		//documents contains all the files present in decrypted packet
		for(String file : documents){
			if(file.contains("\\"))
				file =file.substring(file.indexOf("\\")+1);
			file = '"'+ file.substring(0,file.indexOf("."))+'"';
			modifiedList.add(file);
		}
		//comparing files present in hashsequences and files present in decrypted packet
		modifiedList.removeAll(docListInPacketInfo);
		logger.info("files not matched between documents and  docListInPacketInfo: "+modifiedList);

		int noFilesPresent = 0;
		int noOfDocuments =0;
		boolean isPresent = false;
		boolean isFileValidated = false;
		//if modifiedList is empty, files present in hashsequences and files present 
		//in decrypted packet is same
		if(modifiedList.isEmpty()){
			for(String file : documents){
				noOfDocuments=+1;
				//checking files present in HDFS
				isPresent = adapter.checkFileExistence(regId, file.substring(0,file.indexOf(".")));
				if(isPresent){
					noFilesPresent=+1;
					logger.info("file present in file system : "+file);
				}else
					logger.info("file not present in file system");						
			}	
			if(noFilesPresent==noOfDocuments)
				isFileValidated = true;
			logger.info("ALL FILES ARE MATCHING..............");	
		}

		//Document validation
		boolean isDocumentValidated = false;
		for(String file : listOfIDDocs){
			file = '"'+ file+'"';
			modifiedList.add(file);
		}
		//modifiedList = listOfIDDocs;
		modifiedList.removeAll(docListInPacketInfo);
		logger.info("files not matched between listOfIDDocs and  docListInPacketInfo : "+modifiedList);
		if(modifiedList.isEmpty()){
			isDocumentValidated = true;
		}

		//CheckSum generation
		byte[] hashCodeGenerated = checksumGeneration(listOfFiles, regId);
		//CheckSum Validation
		InputStream packetDataHashStream = adapter.getFile(regId, "PACKET_DATA_HASH");
		byte[] packetDataHashByte = IOUtils.toByteArray(packetDataHashStream);

		Boolean isChecksumValidated = Arrays.equals(hashCodeGenerated, packetDataHashByte);
		if(isChecksumValidated){
			logger.info("Checksum validated.........");
		}else
			logger.info("checksum validation failed......");

		//Packet validation
		if(isFileValidated && isDocumentValidated && isChecksumValidated){
			isPacketValidated = true ;
		}
		logger.info("isPacketValidated : "+isPacketValidated);
		return isPacketValidated;
	}




	private byte[] checksumGeneration(File[] listOfFiles, String regId) throws IOException, ParseException, FileNotFoundException {
 		JSONArray hashSequence1;
		byte[] hashCodeGenerated = null;
		for(File f : listOfFiles){
			if(f.getName().contains("packet_meta_info.json")){
				JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				JSONObject identity = (JSONObject) objectData.get("identity");
				hashSequence1 = (JSONArray) identity.get("hashSequence1");
				logger.info("hashSequence1....... : "+hashSequence1);
				for(Object obj : hashSequence1){
					JSONObject label = (JSONObject) obj;
					logger.info("obj : "+label.get("label"));
					if(label.get("label").equals("applicantBiometricSequence")){
						List<String> docs = (List<String>) label.get("value");
						generateBiometricsHash(docs, regId);
					}else if(label.get("label").equals("introducerBiometricSequence")){
						List<String> docs = (List<String>) label.get("value");
						generateBiometricsHash(docs, regId);
					}else if(label.get("label").equals("applicantDemographicSequence")){
						List<String> docs = (List<String>) label.get("value");
						generateDemographicHash(docs, regId);
					}
				}	
				hashCodeGenerated = HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
			}
		}
		return hashCodeGenerated;
	}

	private void generateDemographicHash(List<String> docs, String regId2) {
		docs.forEach(document -> {
			byte[] filebyte = null;
			InputStream fileStream = adapter.getFile(regId2,
					"DEMOGRAPHIC\\" + document.toUpperCase());
			try {
				filebyte = IOUtils.toByteArray(fileStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			generateHash(filebyte);
		});

	}

	private void generateBiometricsHash(List<String> docs, String regId) {
		docs.forEach(file -> {
			byte[] filebyte = null;
			InputStream fileStream = adapter.getFile(regId,
					"BIOMETRIC\\"+ file.toUpperCase());

			try {
				filebyte = IOUtils.toByteArray(fileStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			generateHash(filebyte);

		});

	}

	private void generateHash(byte[] fileByte) {
		if (fileByte != null) {
			HMACUtils.update(fileByte);
		}	
	}

	private List<String> getHashSequenceFiles(String hashSeqValue, List<String> docListInPacketInfo, JSONObject objectData) {
		JSONArray hashSequence;
		List<String> splittedList;
		JSONObject identity = (JSONObject) objectData.get("identity");
		hashSequence = (JSONArray) identity.get(hashSeqValue);
		logger.info("hashSequence....... : "+hashSequence);
		for(Object obj : hashSequence){
			JSONObject value = (JSONObject) obj;
			String docs = value.get("value").toString();
			docs=docs.replaceAll("[\\[\\]]", "");
			if(!docs.isEmpty() && docs.contains(",")){
				splittedList = Arrays.asList(docs.split(","));
				docListInPacketInfo.addAll(splittedList);
			}else if (!docs.isEmpty())
				docListInPacketInfo.add(docs);
		}
		return docListInPacketInfo;
	}
	
	@Test
	public void testMethod() throws IOException, ParseException {
		File dummyDecryptFile = new File("src/test/resources/regProc/StageValidation/DummyDecryptedPacket/10031100110025720190228141424");

		try {
			packetValidatorStage(dummyDecryptFile);
		} catch (SdkClientException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}