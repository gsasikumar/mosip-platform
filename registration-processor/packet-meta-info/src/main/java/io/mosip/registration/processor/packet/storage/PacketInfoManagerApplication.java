package io.mosip.registration.processor.packet.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", 
		"io.mosip.registration.processor.core",
		"io.mosip.kernel.auditmanager"})

@PropertySource({"classpath:packet-meta-application.properties"})
public class PacketInfoManagerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}
}