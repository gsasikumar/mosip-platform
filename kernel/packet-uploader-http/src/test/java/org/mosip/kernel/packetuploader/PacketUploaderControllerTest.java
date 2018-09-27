package org.mosip.kernel.packetuploader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.kernel.httppacketuploader.controller.PacketUploaderController;
import org.mosip.kernel.httppacketuploader.dto.PacketUploaderResponceDTO;
import org.mosip.kernel.httppacketuploader.service.impl.PacketUploaderServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
public class PacketUploaderControllerTest {

	@Mock
	PacketUploaderServiceImpl service;
	@InjectMocks
	PacketUploaderController controller;

	@Test
	public void uploadTest() {
		MultipartFile file = new MockMultipartFile("testFile.zip", "testFile.zip", null, new byte[1100]);
		PacketUploaderResponceDTO responceDTO = new PacketUploaderResponceDTO("testFile.zip", 1400);
		doReturn(responceDTO).when(service).storePacket(file);
		assertThat(controller.upload(file), is(new ResponseEntity<>(responceDTO, HttpStatus.CREATED)));
	}

}
