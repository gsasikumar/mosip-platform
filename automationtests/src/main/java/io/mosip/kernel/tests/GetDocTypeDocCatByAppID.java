package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
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
import org.json.simple.parser.ParseException;
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

import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class GetDocTypeDocCatByAppID extends BaseTestCase implements ITest {

	public GetDocTypeDocCatByAppID() {
		super();
	}
	
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetDocTypeDocCatByAppID.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetDocType_DocCatByAppID";
	public SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String getDocType_DocCatByAppID = props.get("getDocType_DocCatByAppID");
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();
	
	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		if(!lib.isValidToken(individualCookie))
			individualCookie=auth.getAuthForIndividual();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "GetDocType_DocCatByAppID")
	public Object[][] readData1(ITestContext context) throws Exception {	 
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getDocType_DocCatByAppID
	 * Given input Json as per defined folders When GET request is sent to v1/masterdata/validdocuments/{applicantId}?{langcodes}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetDocType_DocCatByAppID")
	public void getDocTypeDocCatByAppID(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {	
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];		
		// Getting the application id
		String applicantId = actualRequest.get("applicantId").toString();
		
		// Getting the List of languages
		List<String> languages = (JSONArray)actualRequest.get("languages");
		
		//Adding application id to path parameters 
		HashMap<String, String> pathPar=new HashMap<>();
		pathPar.put("applicantId", applicantId);
		
		//Adding list of languages as quary parameters 
		HashMap<String, List<String>> queryParam=new HashMap<>();
		queryParam.put("languages",languages);
				
		// Calling the get method
		Response res=applicationLibrary.getWithPathParamQueryParamList(getDocType_DocCatByAppID, pathPar,queryParam,individualCookie);
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		listOfElementToRemove.add("responsetime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
		if (!status) {
			logger.debug(res);
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
