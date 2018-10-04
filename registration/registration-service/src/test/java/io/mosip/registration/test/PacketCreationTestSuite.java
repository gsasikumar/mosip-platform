package io.mosip.registration.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import io.mosip.registration.test.dao.impl.RegTransactionDAOTest;
import io.mosip.registration.test.dao.impl.RegistrationDAOTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	AESKeyManagerTest.class,
	AESSeedGeneratorTest.class,
	HMACGenerationTest.class,
	PacketCreationManagerTest.class,
	PacketHandlerAPITest.class,
	PacketLocalStorageTest.class,
	PacketZipCreatorAPITest.class,
	RSAEncryptionManagerTest.class,
	RSAEncryptionTest.class,
	RSAKeyGenerationTest.class,
	RegistrationDAOTest.class,
	RegTransactionDAOTest.class})
public class PacketCreationTestSuite {
	
}
