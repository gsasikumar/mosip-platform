package io.mosip.kernel.idrepo.provider.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author Manoj SP
 *
 */
public class FingerprintProviderTest {

	FingerprintProvider fp = new FingerprintProvider();

	@Test
	public void testConvertFIRtoFMR() {
		BIR rFinger = new BIR.BIRBuilder().withBdb("3".getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
								SingleAnySubtypeType.INDEX_FINGER.value()))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
		List<BIR> data = fp.convertFIRtoFMR(Collections.singletonList(rFinger.toBIRType(rFinger)));
		assertTrue(data.get(0).getBdbInfo().getFormatType() == 2);
	}
	
	@Test
	public void testConvertFIRtoFMRUpdate() {
		BIR rFinger = new BIR.BIRBuilder().withBdb("3".getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
								SingleAnySubtypeType.INDEX_FINGER.value()))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
		BIR rFinger2 = new BIR.BIRBuilder().withBdb("3".getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
								SingleAnySubtypeType.INDEX_FINGER.value()))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
		List<BIR> data = fp.convertFIRtoFMR(Lists.newArrayList(rFinger.toBIRType(rFinger), rFinger2.toBIRType(rFinger2)));
		assertTrue(data.get(0).getBdbInfo().getFormatType() == 2);
	}
}
