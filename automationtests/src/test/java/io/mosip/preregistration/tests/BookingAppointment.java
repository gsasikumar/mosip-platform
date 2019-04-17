package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Verify;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Booking Appointment related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class BookingAppointment extends BaseTestCase implements ITest {
	/**
	 * Declaration of all variables
	 **/

	static String preId = "";
	static SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(BookingAppointment.class);
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_URI;
	static String dest = "";
	static String configPaths = "";
	static String folderPath = "preReg/BookingAppointment";
	static String outputFile = "BookingAppointmentOutput.json";
	static String requestKeyFile = "BookingAppointmentRequest.json";
	String testParam = null;
	boolean status_val = false;
	static PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	JSONParser parser = new JSONParser();

	/* implement,IInvokedMethodListener */
	public BookingAppointment() {

	}

	/**
	 * Data Providers to read the input json files from the folders
	 * 
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "bookAppointment")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smoke") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}

	}

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "bookAppointment")
	public void bookingAppointment(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		String testCase = object.get("testCaseName").toString();
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();

		// Document Upload for created application

		//Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse, preId);

		/* PreId of Uploaded document */
		//preId = docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();

		/* Fetch availability[or]center details */
		Response fetchCenter = preRegLib.FetchCentre();

		/* Book An Appointment for the available data */
		Response bookAppointmentResponse = preRegLib.BookAppointment( fetchCenter, preId.toString());

		System.out.println("Book app:"+bookAppointmentResponse.asString());
		
		switch (testCase) {

		case "BookingAppointment_smoke":

			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(bookAppointmentResponse, Expectedresponse, outerKeys, innerKeys);

			break;

		case "empty_registration_center_id":

			String jsonPathTraverse = "$.request[0].preRegistrationId";
			String jsonSetVal = preId;
			String readFilePath = "src/test/resources/" + "preReg/BookingAppointment/"
					+ "empty_registration_center_id/request.json";
			String writeFilePath = "src/test/resources/" + "preReg/BookingAppointment/"
					+ "empty_registration_center_id/request.json";
			ObjectNode jsonPath = preRegLib.dynamicJsonRequest(jsonPathTraverse, jsonSetVal, readFilePath,
					writeFilePath);

			String strPath = jsonPath.toString();
			JSONObject fetchCenterReqjson = (JSONObject) parser.parse(strPath);

			Actualresponse = applicationLibrary.postRequest(fetchCenterReqjson, preReg_URI);

			outerKeys.add("resTime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;
		case "empty_appointment_date":

			String jsonPathTra = "$.request[0].preRegistrationId";
			String jsonSetValue = preId;
			String readJsonFilePath = "src/test/resources/" + "preReg/BookingAppointment/"
					+ "empty_appointment_date/request.json";
			String writeJsonFilePath = "src/test/resources/" + "preReg/BookingAppointment/"
					+ "empty_appointment_date/request.json";
			ObjectNode jsonPathVal = preRegLib.dynamicJsonRequest(jsonPathTra, jsonSetValue, readJsonFilePath,
					writeJsonFilePath);

			String strPathVal = jsonPathVal.toString();
			JSONObject fetchCenterReqjsonVal = (JSONObject) parser.parse(strPathVal);

			Actualresponse = applicationLibrary.postRequest(fetchCenterReqjsonVal, preReg_URI);

			outerKeys.add("resTime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;

		default:

			Actualresponse = applicationLibrary.postRequest(actualRequest, preReg_URI);

			// removing the keys for assertion
			outerKeys.add("resTime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;
		}

		if (status) {
			finalStatus = "Pass";
			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		} else {
			finalStatus = "Fail";
		}

		boolean setFinalStatus = false;

		setFinalStatus = finalStatus.equals("Pass") ? true : false;

		Verify.verify(setFinalStatus);
		softAssert.assertAll();

	}

	/**
	 * Writing output into configpath
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {

		String configPath = "src/test/resources/" + folderPath + "/" + outputFile;

		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}

		String source = "src/test/resources/" + folderPath + "/";
		CommonLibrary.backUpFiles(source, folderPath);

		/*
		 * Add generated PreRegistrationId to list to be Deleted from DB
		 * AfterSuite
		 */

		preIds.add(preId);

	}

	/**
	 * Writing test case name into testng
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, BookingAppointment.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Declaring the Booking Appointment Resource URI and getting the test case
	 * name
	 * 
	 * @param result
	 */
	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();

		/**
		 * Booking Appointment Resource URI
		 */

		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_BookingAppointmentURI");
		authToken=preRegLib.getToken();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}