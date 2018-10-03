package org.mosip.registration.test.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.dao.impl.RegistrationAppLoginDAOImpl;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.entity.RegistrationAppLoginMethodID;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.repositories.RegistrationAppLoginRepository;

public class RegistrationAppLoginDAOTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationAppLoginDAOImpl registrationAppLoginDAOImpl;
	
	@Mock
	private RegistrationAppLoginRepository registrationAppLoginRepository;
	
	@Test
	public void getModesOfLoginSuccessTest() throws RegBaseCheckedException{
		RegistrationAppLoginMethod registrationAppLoginMethod = new RegistrationAppLoginMethod();
		RegistrationAppLoginMethodID registrationAppLoginMethodID = new RegistrationAppLoginMethodID();
		registrationAppLoginMethodID.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setRegistrationAppLoginMethodID(registrationAppLoginMethodID);
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		loginList.add(registrationAppLoginMethod);
		
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);		
		registrationAppLoginDAOImpl.getModesOfLogin();
	}
	
	@Test
	public void getModesOfLoginFailureTest() throws RegBaseCheckedException{
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();		
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);		
		registrationAppLoginDAOImpl.getModesOfLogin();
	}

}
