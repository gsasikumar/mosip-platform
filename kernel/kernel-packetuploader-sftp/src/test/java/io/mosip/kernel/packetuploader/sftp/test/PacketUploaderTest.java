package io.mosip.kernel.packetuploader.sftp.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.mosip.kernel.core.packetuploader.exception.MosipConnectionException;
import io.mosip.kernel.core.packetuploader.exception.MosipEmptyPathException;
import io.mosip.kernel.core.packetuploader.exception.MosipIllegalConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.MosipIllegalIdentityException;
import io.mosip.kernel.core.packetuploader.exception.MosipNullConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.MosipNullPathException;
import io.mosip.kernel.core.packetuploader.exception.MosipPacketSizeException;
import io.mosip.kernel.packetuploader.sftp.channel.SftpChannel;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConfiguration;
import io.mosip.kernel.packetuploader.sftp.impl.PacketUploaderImpl;

public class PacketUploaderTest {

	private PacketUploaderConfiguration configuration;
	private static PacketServer server;
	private Path sftpFolder;

	@BeforeClass
	public static void startServer() throws Exception {
		server = new PacketServer();
		server.afterPropertiesSet();
		if (!server.isRunning()) {
			server.start();
		}
	}

	@Before
	public void setUp() {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", "testpassword", "/");
	}

	@AfterClass
	public static void stopServer() {
		if (server.isRunning()) {
			server.stop();
		}
	}

	@Before
	@After
	public void cleanSftpFolder() throws IOException {
		sftpFolder = Files.createTempDirectory("SFTP_UPLOAD_TEST");
		server.setHomeFolder(sftpFolder);
		Files.walk(sftpFolder).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
	}

	@Test
	public void testCreateSFTPChannel() throws MosipConnectionException {

		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test
	public void testUpload() throws IOException, MosipConnectionException {
		Path tempFile = new ClassPathResource("/packet3.zip").getFile().toPath();

		assertEquals(0, Files.list(sftpFolder).count());
		SftpChannel channel = PacketUploaderImpl.createSFTPChannel(configuration);
		PacketUploaderImpl.upload(channel, tempFile.toString());

		List<Path> paths = Files.list(sftpFolder).collect(Collectors.toList());
		assertEquals(1, paths.size());
		assertEquals(tempFile.getFileName(), paths.get(0).getFileName());
	}

	@Test
	public void testCreateSFTPIdentityChannel() throws MosipConnectionException, IOException {
		String path = new ClassPathResource("/id_rsa1").getFile().getPath();
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", path, null, "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalIdentityException.class)
	public void testCreateSFTPOllegalIdentityChannel() throws MosipConnectionException, IOException {
		String path = new ClassPathResource("/id_rsa1").getPath();
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", path, null, "/");
		PacketUploaderImpl.createSFTPChannel(configuration);

	}

	@Test
	public void testReleaseConnection() throws MosipConnectionException, JSchException {
		ChannelSftp channelSftp = Mockito.mock(ChannelSftp.class);
		Session session = Mockito.mock(Session.class);
		SftpChannel channel = new SftpChannel(channelSftp, null);
		when(channelSftp.getSession()).thenReturn(session);
		doNothing().when(session).disconnect();
		doNothing().when(channelSftp).exit();
		PacketUploaderImpl.releaseConnection(channel);
		Mockito.verify(session, times(1)).disconnect();
	}

	@Test(expected = MosipConnectionException.class)
	public void testCreateSFTPConnectionExceptionChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "user", "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipEmptyPathException.class)
	public void testUploadSftpEmptyPathexception() throws IOException, MosipConnectionException {
		SftpChannel channel = PacketUploaderImpl.createSFTPChannel(configuration);
		PacketUploaderImpl.upload(channel, "");
	}

	@Test(expected = MosipPacketSizeException.class)
	public void testUploadSftpSizeException() throws IOException, MosipConnectionException {
		SftpChannel channel = PacketUploaderImpl.createSFTPChannel(configuration);
		Path tempFile = new ClassPathResource("/packet4.zip").getFile().toPath();
		PacketUploaderImpl.upload(channel, tempFile.toString());
	}

	@Test(expected = MosipPacketSizeException.class)
	public void testUploadSFTPException() throws IOException, MosipConnectionException {
		SftpChannel channel = PacketUploaderImpl.createSFTPChannel(configuration);

		PacketUploaderImpl.upload(channel, "/fakePath");
	}

	@Test(expected = MosipNullPathException.class)
	public void testUploadSftpNullPathException() throws IOException, MosipConnectionException {
		SftpChannel channel = PacketUploaderImpl.createSFTPChannel(configuration);
		PacketUploaderImpl.upload(channel, null);
	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPEmptyHostChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("", 10022, "testUser", "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipNullConfigurationException.class)
	public void testCreateSFTPNullHostChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration(null, 10022, "testUser", "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPEmptyUserChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "", "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipNullConfigurationException.class)
	public void testCreateSFTPNullUserChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, null, "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPEmptyDirectoryChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", "testpassword", "");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipNullConfigurationException.class)
	public void testCreateSFTPNullDirectoryChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", "testpassword", null);
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPEmptyKeyChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", "", null, "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPIllegalKeyChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", null, null, "/");
		configuration.setPassword("");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPEmptyPasswordChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", "", "/");
		configuration.setPrivateKeyFileName("");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipNullConfigurationException.class)
	public void testCreateSFTPNullPasswordChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 10022, "testUser", null, "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPMinPortChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", -1, "testUser", "testpassword", "/");
		assertThat(PacketUploaderImpl.createSFTPChannel(configuration), isA(SftpChannel.class));

	}

	@Test(expected = MosipIllegalConfigurationException.class)
	public void testCreateSFTPMaxPortChannel() throws MosipConnectionException {
		configuration = new PacketUploaderConfiguration("127.0.0.1", 65536, "testUser", "testpassword", "/");
		PacketUploaderImpl.createSFTPChannel(configuration);

	}

	@Test(expected = MosipNullConfigurationException.class)
	public void testCreateNUllConfigurationtChannel() throws MosipConnectionException {

		PacketUploaderImpl.createSFTPChannel(null);

	}
}
