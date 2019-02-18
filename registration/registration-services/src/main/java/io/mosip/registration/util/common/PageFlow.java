package io.mosip.registration.util.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;

@Component
public class PageFlow {
	
	public void getInitialPageDetails() {
		Map<String, Map<String, Boolean>> registrationMap = new LinkedHashMap<>();
		Map<String, Map<String, Boolean>> onboardMap = new LinkedHashMap<>();
		
		Map<String, Boolean> onboardUserParent = new LinkedHashMap<>();
		onboardUserParent.put(RegistrationConstants.VISIBILITY, true);
		onboardMap.put(RegistrationConstants.ONBOARD_USER_PARENT, onboardUserParent);
				
		Map<String, Boolean> demographicMap = new LinkedHashMap<>();
		demographicMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.DEMOGRAPHIC_DETAIL, demographicMap);
		
		Map<String, Boolean> docMap = new LinkedHashMap<>();
		docMap.put(RegistrationConstants.VISIBILITY, true);
		docMap.put(RegistrationConstants.DOCUMENT_PANE, true);
		docMap.put(RegistrationConstants.EXCEPTION_PANE, true);
		registrationMap.put(RegistrationConstants.DOCUMENT_SCAN, docMap);
	
		Map<String, Boolean> exceptionMap = new LinkedHashMap<>();
		exceptionMap.put(RegistrationConstants.VISIBILITY, true);
		exceptionMap.put(RegistrationConstants.FINGER_PANE, true);
		exceptionMap.put(RegistrationConstants.IRIS_PANE, true);
		registrationMap.put(RegistrationConstants.BIOMETRIC_EXCEPTION, exceptionMap);
		onboardMap.put(RegistrationConstants.BIOMETRIC_EXCEPTION, exceptionMap);
		
		Map<String, Boolean> fingerPrintMap = new LinkedHashMap<>();
		fingerPrintMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.FINGERPRINT_CAPTURE, fingerPrintMap);
		onboardMap.put(RegistrationConstants.FINGERPRINT_CAPTURE, fingerPrintMap);
		
		Map<String, Boolean> irisMap = new LinkedHashMap<>();
		irisMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.IRIS_CAPTURE, irisMap);
		onboardMap.put(RegistrationConstants.IRIS_CAPTURE, irisMap);
		
		Map<String, Boolean> faceMap = new LinkedHashMap<>();
		faceMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.FACE_CAPTURE, faceMap);
		onboardMap.put(RegistrationConstants.FACE_CAPTURE, faceMap);
		
		Map<String, Boolean> previewMap = new LinkedHashMap<>();
		previewMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.REGISTRATION_PREVIEW, previewMap);
		
		Map<String, Boolean> authMap = new LinkedHashMap<>();
		authMap.put(RegistrationConstants.VISIBILITY, true);
		registrationMap.put(RegistrationConstants.OPERATOR_AUTHENTICATION, authMap);
		
		Map<String, Boolean> onBoardSuccessMap = new LinkedHashMap<>();
		onBoardSuccessMap.put(RegistrationConstants.VISIBILITY, true);
		onboardMap.put(RegistrationConstants.ONBOARD_USER_SUCCESS, onBoardSuccessMap);		
		
		updateRegMap(registrationMap, "registration");
		updateRegMap(onboardMap, "onboard");
		
		ApplicationContext.map().put("onboardPageList", getOnboardPageList(onboardMap));
		ApplicationContext.map().put("onboardMap", onboardMap);
		ApplicationContext.map().put("registrationMap", registrationMap);

	}
	
	private void updateRegMap(Map<String, Map<String, Boolean>> registrationMap, String page) {
		
		updateDetailMap(registrationMap, RegistrationConstants.FINGERPRINT_DISABLE_FLAG, RegistrationConstants.FINGERPRINT_CAPTURE, RegistrationConstants.BIOMETRIC_EXCEPTION,"fingerPane", page);
		updateDetailMap(registrationMap, RegistrationConstants.IRIS_DISABLE_FLAG, RegistrationConstants.IRIS_CAPTURE, RegistrationConstants.BIOMETRIC_EXCEPTION,"irisPane", page);
		updateDetailMap(registrationMap, RegistrationConstants.FACE_DISABLE_FLAG, RegistrationConstants.FACE_CAPTURE,"","", page);
		
		if(page.equalsIgnoreCase("registration")) {
			updateDetailMap(registrationMap, RegistrationConstants.DOCUMENT_DISABLE_FLAG, RegistrationConstants.DOCUMENT_PANE,RegistrationConstants.DOCUMENT_SCAN,"", page);
			
			if(!registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get("fingerPane") && !registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get("irisPane")) {
				registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).put(RegistrationConstants.VISIBILITY, false);
				registrationMap.get(RegistrationConstants.DOCUMENT_SCAN).put(RegistrationConstants.EXCEPTION_PANE, false);
			}
			
			if(!registrationMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.VISIBILITY) && registrationMap.containsKey(RegistrationConstants.DOCUMENT_SCAN) &&!registrationMap.get(RegistrationConstants.DOCUMENT_SCAN).get(RegistrationConstants.DOCUMENT_PANE)) {
				registrationMap.get(RegistrationConstants.DOCUMENT_SCAN).put(RegistrationConstants.VISIBILITY, false);
			}
		}
	}
	
	private void updateDetailMap(Map<String, Map<String, Boolean>> detailMap, String flagVal, String pageId, String subPane, String childId, String page) {
		
		if (ApplicationContext.map().containsKey(flagVal)
				&& ApplicationContext.map().get(flagVal)
						.equals(RegistrationConstants.DISABLE)) {
				
				if(pageId.equals(RegistrationConstants.DOCUMENT_PANE)) {
					detailMap.get(subPane).put(pageId, false);
				} else {
					detailMap.get(pageId).put(RegistrationConstants.VISIBILITY, false);
					
					if(!subPane.isEmpty()) {
						detailMap.get(subPane).put(childId, false);
					}
				}
		}
	}
	
	private List<String> getOnboardPageList(Map<String, Map<String, Boolean>> onboardMap){
		List<String> onboardPageList = new ArrayList<>();
		
		for(Map.Entry<String,Map<String,Boolean>> entry : onboardMap.entrySet()) {
			if(entry.getValue().get(RegistrationConstants.VISIBILITY)) {
				onboardPageList.add(entry.getKey());
			}
		}		
		return onboardPageList;
	}

}
