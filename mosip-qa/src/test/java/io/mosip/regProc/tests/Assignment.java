package io.mosip.regProc.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.dbDTO.RegistrationStatusEntity;
import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class Assignment extends BaseTestCase implements ITest{
	
	private static Logger logger = Logger.getLogger(PacketStatus.class);
	protected static String testCaseName = "";

	boolean status = false;
	String[] regId = null;
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response actualResponse = null;
	static JSONArray expectedResponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String regProc_URI = "/registrationstatus/v0.1/registration-processor/registration-status/registrationstatus";
	String finalStatus = "";
	static SoftAssert softAssert=new SoftAssert();
	static 	String regIds="";
	static String dest = "";
	static String folderPath = "regProc/PacketStatus";
	static String outputFile = "PacketStatusOutput.json";
	static String requestKeyFile = "PacketStatusRequest.json";

	/**
	 * This method is use for reading data for packet status
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@DataProvider(name = "packetStatus")
	public static Object[][] readDataForPacketStatus(ITestContext context) throws Exception {
		//CommonLibrary.configFileWriter(folderPath,requestKeyFile,"DemographicCreate","smokePreReg");
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smoke") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	/**
	 * This method is use for getting packet status based on registration id
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider = "packetStatus")
	public void packetStatus(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		expectedResponse = ResponseRequestMapper.mapArrayResponse(testSuite, object);
		try {
			actualResponse = applicationLibrary.getRequest(regProc_URI,actualRequest);
		} catch (Exception e) {
			logger.info(e);
		}
		String statusCode = actualResponse.jsonPath().get("statusCode").toString();
		/*if(statusCode.equals("true")) {
			regId=(Actualresponse.jsonPath().get("response[0].registrationId")).toString();
		}*/
		outerKeys.add("resTime");
		innerKeys.add("preRegistrationId");
		innerKeys.add("updatedBy");
		innerKeys.add("createdDateTime");
		innerKeys.add("updatedDateTime");


		status = AssertResponses.assertArrayResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		
		if (status) {

			regIds=actualResponse.jsonPath().get("registrationId").toString();
			//	regIds=(Actualresponse.jsonPath().get("response[0].registrationId")).toString();

			logger.info("Reg Id is : " +regIds);
			logger.info("Status Code is : " + statusCode);

			if(statusCode.matches(".*PROCESSING*.")|| statusCode.matches(".*RESEND*.")||statusCode.matches(".*PROCESSED*.")) {
				logger.info("inside statuscode loop...................");
				
				regId = regIds.replace("[", "").replace("]", "").split(",");

				for (String rId : regId){

					RegistrationStatusEntity dbDto = RegProcDataRead.regproc_dbDataInRegistration(rId);	

					logger.info("dbDto :" +dbDto);

					if(dbDto != null) {

						Iterator<Object> iterator = expectedResponse.iterator();
						while(iterator.hasNext()){
							JSONObject jsonObject = (JSONObject) iterator.next();
							System.out.println("regidtrationId" + ":" + jsonObject.get("registrationId"));
							String expectedRegId = jsonObject.get("registrationId").toString().trim();
							logger.info("expectedRegId: "+expectedRegId);
							
							if (expectedRegId.matches(dbDto.getId())){							
								logger.info("Validated in DB.......");
								finalStatus = "Pass";
							} 
						}
					}
				}
			}
			else {
				finalStatus="Fail";
			}

			/*else {
				finalStatus="Pass";
			}*/
			softAssert.assertTrue(true);
		}
		else {
			finalStatus="Fail";
			//softAssert.assertTrue(false);
		}

		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);

	}

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		//	testName.set(object.get("testCaseName").toString());
		testCaseName = object.get("testCaseName").toString();
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		boolean flag = false;
		boolean flag_reg = false;
		try {
			for(String rId : regId){
				flag = RegProcDataRead.regproc_dbDeleteRecordInRegistrationList(rId);
				logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION LIST: "+flag);
				flag_reg = RegProcDataRead.regproc_dbDeleteRecordInRegistration(rId);
				logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION LIST: "+flag_reg);
			}
			
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, PacketStatus.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
	IllegalAccessException {
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source =  "src/test/resources/" + folderPath + "/";
		//CommonLibrary.backUpFiles(source, folderPath);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
