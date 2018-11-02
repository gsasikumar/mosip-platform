package io.mosip.kernel.packetuploader.sftp.util;

import java.io.File;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.mosip.kernel.core.packetuploader.exception.EmptyPathException;
import io.mosip.kernel.core.packetuploader.exception.IllegalConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.IllegalIdentityException;
import io.mosip.kernel.core.packetuploader.exception.NullConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.NullPathException;
import io.mosip.kernel.core.packetuploader.exception.PacketSizeException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConfiguration;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;

/**
 * Util Class for Packet Uploader
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploaderUtils {
	/**
	 * Constructor for this class
	 */
	private PacketUploaderUtils() {
	}

	/**
	 * This configures session with given configuration
	 * 
	 * @param jsch
	 *            {@link JSch} instance
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 * @return configured {@link Session}
	 */
	public static Session configureSession(JSch jsch, PacketUploaderConfiguration configuration) {
		Session session = null;
		try {
			session = jsch.getSession(configuration.getUser(), configuration.getHost(), configuration.getPort());
		} catch (JSchException e) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION.getErrorMessage(), e);
		}
		session.setConfig(PacketUploaderConstant.STR_STRICT_HOST_KEY_CHECKING.getKey(),
				PacketUploaderConstant.STR_STRICT_HOST_KEY_CHECKING.getValue());
		session.setConfig(PacketUploaderConstant.AUTHENTICATIONS.getKey(),
				PacketUploaderConstant.AUTHENTICATIONS.getValue());
		if (configuration.getPrivateKeyFileName() == null) {
			session.setPassword(configuration.getPassword());
		}
		return session;
	}

	/**
	 * This adds private key as identity
	 * 
	 * @param jsch
	 *            {@link JSch} instance
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void addIdentity(JSch jsch, PacketUploaderConfiguration configuration) {
		try {
			if (configuration.getPrivateKeyPassphrase() != null) {
				jsch.addIdentity(configuration.getPrivateKeyFileName(), configuration.getPrivateKeyPassphrase());
			} else {
				jsch.addIdentity(configuration.getPrivateKeyFileName());
			}
		} catch (JSchException e) {
			throw new IllegalIdentityException(
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_IDENTITY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_IDENTITY_EXCEPTION.getErrorMessage(), e);
		}
	}

	/**
	 * Validation method for packetPath
	 * 
	 * @param packetPath
	 *            path of packet to upload
	 */
	public static void check(String packetPath) {
		if (packetPath == null) {
			throw new NullPathException(PacketUploaderExceptionConstant.MOSIP_NULL_PATH_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_PATH_EXCEPTION.getErrorMessage(), null);
		} else if (packetPath.trim().isEmpty()) {
			throw new EmptyPathException(PacketUploaderExceptionConstant.MOSIP_EMPTY_PATH_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_PATH_EXCEPTION.getErrorMessage(), null);
		} else if (new File(packetPath).length() > Long.parseLong(PacketUploaderConstant.PACKET_SIZE_MAX.getValue())
				|| new File(packetPath).length() == Long.parseLong(PacketUploaderConstant.PACKET_SIZE_MIN.getValue())) {
			throw new PacketSizeException(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage(), null);
		}

	}

	/**
	 * Validation method for configurations
	 * 
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void checkConfiguration(PacketUploaderConfiguration configuration) {
		if (configuration == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_CONFIGURATION_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_CONFIGURATION_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getHost() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_HOST_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_HOST_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getHost().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_EMPTY_HOST_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_HOST_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getPort() < Integer.parseInt(PacketUploaderConstant.PORT_MIN.getValue())
				|| configuration.getPort() > Integer.parseInt(PacketUploaderConstant.PORT_MAX.getValue())) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_PORT_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_PORT_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getUser() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getUser().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getSftpRemoteDirectory() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getSftpRemoteDirectory().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION.getErrorMessage(), null);
		}
		checkKey(configuration);
	}

	/**
	 * Validation method for Keys
	 * 
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void checkKey(PacketUploaderConfiguration configuration) {
		if (configuration.getPassword() == null && configuration.getPrivateKeyFileName() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getPassword() == null && configuration.getPrivateKeyFileName().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getPrivateKeyFileName() == null && configuration.getPassword().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), null);
		} else if (configuration.getPassword() != null && configuration.getPrivateKeyFileName() != null
				&& configuration.getPassword().isEmpty() && configuration.getPrivateKeyFileName().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), null);
		}
	}
}
