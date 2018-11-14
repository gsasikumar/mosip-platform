package io.mosip.registration.processor.packet.decryptor.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.auditmanager","io.mosip.registration.processor.core"})
public class PacketDecryptorJobApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketDecryptorJobApplication.class, args);
	}
}
