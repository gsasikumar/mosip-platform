
package io.mosip.kernel.tests;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class FetchGenderType extends BaseTestCase implements ITest {

	FetchGenderType() {
		super();
	}

	private static Logger logger = Logger.getLogger(FetchGenderType.class);
	private final String moduleName = "kernel";
	private final String apiName = "FetchGenderType";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String FetchGenderType_URI = props.get("FetchGenderType_URI").toString();
	private final String FetchGenderType_id_lang_URI = props.get("FetchGenderType_id_lang_URI").toString();

	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	KernelAuthentication auth = new KernelAuthentication();
	String cookie = null;

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName + "_" + apiName + "_" + object.toString();
		cookie = auth.getAuthForIDA();
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
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
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
	public void fetchGenderType(String testcaseName) throws ParseException {
		logger.info("Test Case Name:" + testcaseName);

		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

		JSONObject objectData = objectDataArray[0];
		responseObject = objectDataArray[1];
		if (objectData != null)
			response = applicationLibrary.getWithPathParam(FetchGenderType_id_lang_URI, objectData, cookie);
		else {
			response = applicationLibrary.getWithoutParams(FetchGenderType_URI, cookie);
			objectData = null;
		}
		// DB validation
		new CommonLibrary().responseAuthValidation(response);
		if (testcaseName.toLowerCase().contains("smoke")) {

			// fetching json object from response
			JSONObject responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(response.asString()))
					.get("response");
			if (responseJson == null || !responseJson.containsKey("genderType"))
				Assert.assertTrue(false, "Response does not contain genderType");
			String queryPart = "select count(*) from master.gender where is_active = true";
			String query = queryPart;
			if (objectData != null) {
				query = queryPart + " and lang_code = '" + objectData.get("langcode") + "'";

			}
			long obtainedObjectsCount = new KernelDataBaseAccess().validateDBCount(query, "masterdata");

			// fetching json array of objects from response
			JSONArray genderTypeFromGet = (JSONArray) responseJson.get("genderType");

			logger.info("===Dbcount===" + obtainedObjectsCount + "===Get-count===" + genderTypeFromGet.size());

			// validating number of objects obtained form db and from get request
			if (genderTypeFromGet.size() == obtainedObjectsCount) {

				// list to validate existance of attributes in response objects
				List<String> attributesToValidateExistance = new ArrayList<String>();
				attributesToValidateExistance.add("code");
				attributesToValidateExistance.add("genderName");
				attributesToValidateExistance.add("isActive");

				// key value of the attributes passed to fetch the data (should be same in all
				// obtained objects)
				HashMap<String, String> passedAttributesToFetch = new HashMap<String, String>();
				if (objectData != null) {
					passedAttributesToFetch.put("langCode", objectData.get("langcode").toString());
				}
				status = AssertKernel.validator(genderTypeFromGet, attributesToValidateExistance,
						passedAttributesToFetch);
			} else
				status = false;

		}

		else {
			// add parameters to remove in response before comparison like time stamp
			ArrayList<String> listOfElementToRemove = new ArrayList<String>();
			listOfElementToRemove.add("responsetime");
			listOfElementToRemove.add("timestamp");
			status = assertions.assertKernel(response, responseObject, listOfElementToRemove);
		}

		if (!status) {
			logger.debug(response);
		}
		Verify.verify(status);
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

}
