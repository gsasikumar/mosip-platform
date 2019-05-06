
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
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Test Class to perform Create new pre-registration related Positive and
 * Negative test cases
 * 
 * @author Ashish Rastogi
 * @since 1.0.0
 */

public class CreatePreRegistration extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	static String preId = "";
	static SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(CreatePreRegistration.class);
	static PreRegistrationLibrary lib = new PreRegistrationLibrary();
	boolean status = false;
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
	static String folderPath = "preReg\\Create_PreRegistration";
	static String outputFile = "Create_PreRegistrationOutput.json";
	static String requestKeyFile = "Create_PreRegistrationRequest.json";

	CreatePreRegistration() {
		super();
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
	@DataProvider(name = "createPreReg")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		String testParam = context.getCurrentXmlTest().getParameter("testType");
<<<<<<< HEAD
		switch ("smoke") {
=======
		switch ("smokeAndRegression") {
>>>>>>> 54f068b02e79a307f7b0561c88e7386801edf1f0
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}

	}

	/*
	 * Given Create PreRegistration valid request when I Send POST request to
	 * /demographic/v0.1/pre-registration/applications Then I should get success
	 * response with elements defined as per specifications Given Invalid request
	 * when I send POST request to /demographic/v0.1/pre-registration/applications
	 * Then I should get Error response along with Error Code and Error messages as
	 * per Specification
	 * 
	 */

	@Test(dataProvider = "createPreReg")
	public void createPreRegistration(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		actualRequest.put("requesttime", lib.getCurrentDate());
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		try {

			Actualresponse = applicationLibrary.postRequest(actualRequest.toJSONString(), preReg_URI);
		} catch (Exception e) {
			logger.info(e);
		}
		outerKeys.add("responsetime");
		outerKeys.add("timestamp");
		outerKeys.add("message");
		outerKeys.add("path");
		innerKeys.add("preRegistrationId");
		innerKeys.add("updatedDateTime");
		innerKeys.add("createdDateTime");
		innerKeys.add("IDSchemaVersion");
		status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
		if (status) {
			finalStatus = "Pass";
		} else {
			finalStatus = "Fail";
		}

		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);

		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail"))
			setFinalStatus = false;
		else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();

	}

	/**
	 * Writing output into configure path
	 * 
	 * @throws IOException
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/preReg/Create_PreRegistrationCreate_PreRegistrationOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info(
					"Successfully updated Results to Create_PreRegistrationOutput.json file.......................!!");

		}
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, CreatePreRegistration.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
		lib.logOut();
	}

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		/*
		 * CreatePreRegistration Resource URI
		 */

		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_CreateApplnURI");
		authToken = lib.getToken();

	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
