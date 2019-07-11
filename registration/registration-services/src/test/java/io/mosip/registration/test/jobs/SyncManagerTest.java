package io.mosip.registration.test.jobs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.impl.SyncManagerImpl;
import io.mosip.registration.repositories.SyncTransactionRepository;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

@RunWith(PowerMockRunner.class)
public class SyncManagerTest {

	@Mock
	private SyncTransactionRepository syncTranscRepository;

	@Mock
	private JobExecutionContext jobExecutionContext;

	@Mock
	private JobDetail jobDetail;

	@Mock
	private Trigger trigger;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	JobDataMap jobDataMap = new JobDataMap();

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private SyncManagerImpl syncTransactionManagerImpl;

	@Mock
	UserOnboardDAO onboardDAO;

	SyncJobDef syncJob = new SyncJobDef();

	List<SyncJobDef> syncJobList;

	HashMap<String, SyncJobDef> jobMap = new HashMap<>();

	@Mock
	SyncTransactionDAO jobTransactionDAO;

	@Mock
	SyncJobControlDAO syncJobDAO;

	@Mock
	private MachineMappingDAO machineMappingDAO;

	@Before
	public void initializeSyncJob() throws RegBaseCheckedException {
		syncJob.setId("1");
		syncJob.setName("Name");
		syncJob.setApiName("API");
		syncJob.setCrBy("Yaswanth");
		syncJob.setCrDtime(new Timestamp(System.currentTimeMillis()));
		syncJob.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
		syncJob.setIsActive(true);
		syncJob.setIsDeleted(false);
		syncJob.setLangCode("EN");
		syncJob.setLockDuration("20");
		syncJob.setParentSyncJobId("ParentSyncJobId");
		syncJob.setSyncFrequency("25");
		syncJob.setUpdBy("Yaswanth");
		syncJob.setUpdDtimes(new Timestamp(System.currentTimeMillis()));

		syncJobList = new LinkedList<>();
		syncJobList.add(syncJob);

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		// JobConfigurationServiceImpl.SYNC_JOB_MAP = jobMap;

		Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("CNTR123");
		Mockito.when(onboardDAO.getStationID(Mockito.anyString())).thenReturn("MCHN123");
		//PowerMockito.mockStatic(io.mosip.registration.config.AppConfig.class);
		//when(io.mosip.registration.config.AppConfig.getApplicationProperty(Mockito.anyString())).thenReturn("Appli_Lang");

	}

	private SyncTransaction prepareSyncTransaction() {
		SyncTransaction syncTransaction = new SyncTransaction();

		String transactionId = Integer.toString(new Random().nextInt(10000));
		syncTransaction.setId(transactionId);

		syncTransaction.setSyncJobId(syncJob.getId());

		syncTransaction.setSyncDateTime(new Timestamp(System.currentTimeMillis()));
		syncTransaction.setStatusCode("Completed");
		syncTransaction.setStatusComment("Completed");

		// TODO
		syncTransaction.setTriggerPoint("User");

		syncTransaction.setSyncFrom("Machine");

		// TODO
		syncTransaction.setSyncTo("SERVER???");

		syncTransaction.setMachmId("MachID");

		// TODO
		syncTransaction.setLangCode("EN");

		syncTransaction.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));
		return syncTransaction;

	}

	@Test
	public void createSyncTest() throws RegBaseCheckedException {
		String machineId = RegistrationSystemPropertiesChecker.getMachineId();
		SyncTransaction syncTransaction = prepareSyncTransaction();
		SyncControl syncControl = null;
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.anyString())).thenReturn(syncControl);

		Mockito.when(jobTransactionDAO.save(Mockito.any(SyncTransaction.class))).thenReturn(syncTransaction);
		Mockito.when(machineMappingDAO.getStationID(machineId)).thenReturn(Mockito.anyString());
		
		
		assertSame(syncTransaction.getSyncJobId(),
				syncTransactionManagerImpl.createSyncTransaction("Completed", "Completed", "USER", "1").getSyncJobId());

	}

	@Test
	public void createSyncControlUpdateTest() {
		SyncTransaction syncTransaction = prepareSyncTransaction();
		SyncControl syncControl = new SyncControl();
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.anyString())).thenReturn(syncControl);
		Mockito.when(syncJobDAO.update(Mockito.any(SyncControl.class))).thenReturn(syncControl);
		assertNotNull(syncTransactionManagerImpl.createSyncControlTransaction(syncTransaction));
	}

	@Ignore
	@Test(expected = RegBaseUncheckedException.class)
	public void createSyncTransactionExceptionTest() {
		SyncTransaction syncTransaction = null;
		Mockito.when(jobTransactionDAO.save(Mockito.any(SyncTransaction.class))).thenThrow(RuntimeException.class);
		syncTransactionManagerImpl.createSyncTransaction("Completed", "Completed", "USER", "1");

	}

	@Test
	public void createSyncControlNullTest() {
		SyncTransaction syncTransaction = prepareSyncTransaction();

		SyncControl syncControl = null;
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.any())).thenReturn(syncControl);

		syncControl =new SyncControl();
		syncControl.setId(syncTransaction.getId());
		Mockito.when(syncJobDAO.save(Mockito.any(SyncControl.class))).thenReturn(syncControl);
		assertNotNull(syncTransactionManagerImpl.createSyncControlTransaction(syncTransaction));
	}

}
