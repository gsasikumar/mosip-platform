package io.mosip.kernel.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
import com.google.common.base.Verify;

import io.mosip.dbDTO.DeviceSpecificationDto;
import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GetHeader;
import io.mosip.util.ReadFolder;
import io.mosip.util.TestCaseReader;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class FetchDevice extends BaseTestCase implements ITest{
	
	
	FetchDevice() {
		super();
	}

	private static Logger logger = Logger.getLogger(FetchDevice.class);
	private static final String jiraID = "MOS-8263";
	private static final String moduleName = "kernel";
	private static final String apiName = "FetchDevice";
	private static final String requestJsonName = "fetchDeviceRequest";
	private static final String outputJsonName = "fetchDeviceOutput";
	private static final String service_lang_URI = "/masterdata/v1.0/devices/{languagecode}";
	private static final String service_id_lang_URI = "/masterdata/v1.0/devices/{languagecode}/{deviceType}";

	protected static String testCaseName = "";
	static SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	static Response response = null;
	static JSONObject responseObject = null;
	private static AssertKernel assertions = new AssertKernel();
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();

	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smoke");

		case "regression":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "regression");
		default:
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smokeAndRegression");
		}

	}

	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void fetchDevice(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {

		logger.info("Test Case Name:" + testcaseName);
		object.put("Test case Name", testcaseName);
		object.put("Jira ID", jiraID);

		String fieldNameArray[] = testcaseName.split("_");
		String fieldName = fieldNameArray[1];

		JSONObject requestJson = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonName);
		
		for (Object key : requestJson.keySet()) {
			if (fieldName.equals(key.toString()))
				object.put(key.toString(), "invalid");
			else
				object.put(key.toString(), "valid");
		}

		String configPath =  "src/test/resources/" + moduleName + "/" + apiName
				+ "/" + testcaseName;
		
		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();
		JSONObject objectData = null;
		for (int k = 0; k < listofFiles.length; k++) {

			if (listofFiles[k].getName().toLowerCase().contains("request")) {
				objectData = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
				logger.info("Json Request Is : " + objectData.toJSONString());

				if(objectData.containsKey("deviceType"))
					response = applicationLibrary.getRequestPathPara(service_id_lang_URI, objectData);
					else
					response = applicationLibrary.getRequestPathPara(service_lang_URI, objectData);

			} else if (listofFiles[k].getName().toLowerCase().contains("response"))
				responseObject = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
		}
		logger.info("Expected Response:" + responseObject.toJSONString());
		
		// add parameters to remove in response before comparison like time stamp
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		
		status = assertions.assertKernel(response, responseObject, listOfElementToRemove);
		if (status) {
			int statusCode = response.statusCode();
			logger.info("Status Code is : " + statusCode);

		
			finalStatus = "Pass";
		}

		else {
			finalStatus = "Fail";
		}

		object.put("status", finalStatus);

		arr.add(object);
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail")) {
			setFinalStatus = false;
			logger.debug(response);
		} else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
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
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * this method write the output to corressponding json
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath =  "src/test/resources/" + moduleName + "/" + apiName
				+ "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
	
	
