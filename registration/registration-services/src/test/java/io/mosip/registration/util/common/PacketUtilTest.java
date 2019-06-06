package io.mosip.registration.util.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

class PacketUtilTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	PacketUtil packetUtil;
	@Test
	void testGetPacketNames() {
		File file1 = new File("..//registration-services/src/test/resources/123456789_Ack.png");
		File file2 = new File("..//registration-services/src/test/resources/123456789_Ack.png");
		File file3 = new File("..//registration-services/src/test/resources/123456789_Ack.png");
		File file4 = new File("..//registration-services/src/test/resources/123456789_Ack.png");
		
		List<File> files =new ArrayList<>();
		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		packetUtil = new PacketUtil();
		List<String> packetNames= packetUtil.getPacketNames(files);
		
		assertNotNull(packetNames);
	}

}
