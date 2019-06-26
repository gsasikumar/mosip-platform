package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbHealthcheck.DBHealthCheck;
import io.mosip.dbaccess.PreRegDbread;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.AssertPreReg;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 */
public class FetchTheStatusOfApplication extends BaseTestCase implements ITest {
	public String preId = "";
	public SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(FetchTheStatusOfApplication.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	public Response Actualresponse = null;
	public JSONObject Expectedresponse = null;
	ApplicationLibrary appLib = new ApplicationLibrary();
	private static String preReg_URI;
	public String dest = "";
	public String configPaths = "";
	public String folderPath = "preReg/Fetch_the_status_of_a_application";
	public String outputFile = "Fetch_the_status_of_a_applicationOutput.json";
	public String requestKeyFile = "Fetch_the_status_of_a_applicationRequest.json";
	public static PreRegistrationLibrary lib = new PreRegistrationLibrary();

	FetchTheStatusOfApplication() {
		super();
	}

	private static CommonLibrary commonLibrary = new CommonLibrary();

	static PreRegistrationLibrary preLab = new PreRegistrationLibrary();

	/**
	 * Data Prividers to read the input json files from the folders
	 * 
	 * @param context
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "Fetch_the_status_of_a_application")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	/**
	 * Script for Fetching status of the application
	 * 
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider = "Fetch_the_status_of_a_application")
	public void fetchTheStatusOfApplication(String testSuite, Integer i, JSONObject object) throws Exception {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		if (testCaseName.toLowerCase().contains("smoke")) {
			Response createPreRegResponse = preLab.CreatePreReg(individualToken);
			String preRegistrationId = (createPreRegResponse.jsonPath().get("response.preRegistrationId")).toString();
			Actualresponse = preLab.getPreRegistrationStatus(preRegistrationId, individualToken);
			preId = (Actualresponse.jsonPath().get("response.preRegistartionId")).toString();
			if (preId.equals(Actualresponse.jsonPath().get("response.preRegistartionId").toString()))
				status = true;
		} else {
			Actualresponse = appLib.getWithPathParam(preReg_URI, actualRequest, individualToken);

			outerKeys.add("responsetime");
			outerKeys.add("timestamp");
			innerKeys.add("createdDatTime");
			innerKeys.add("preRegistartionId");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
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
		if (finalStatus.equals("Fail"))
			setFinalStatus = false;
		else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
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
			f.set(baseTestMethod, "Pre Reg_Demographic_" + FetchTheStatusOfApplication.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}

	}

	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();

		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_FetchStatusOfApplicationURI");
		if (!lib.isValidToken(individualToken)) {
			individualToken = lib.getToken();
		}
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}