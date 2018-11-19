package io.mosip.registration.processor.packet.decryptor.job.messagesender;

import org.springframework.beans.factory.annotation.Value;import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;


/**
 * The Class DecryptionMessageSender.
 */
@RefreshScope
@Service
public class DecryptionMessageSender extends MosipVerticleManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DecryptionMessageSender.class);


	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;


	/** The audit log request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;


	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/**  Checking transaction status. */
	private boolean isTransactionSuccessful = false;

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {} - {}";

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	private void getEventBus() {
		if (this.mosipEventBus == null) {
			mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		}
	}

	/**
	 * Send message.
	 *
	 * @param message the message
	 */
	public void sendMessage(MessageDTO message) {
		try {
		getEventBus();
		this.send(this.mosipEventBus, MessageBusAddress.BATCH_BUS, message);
		isTransactionSuccessful=true;
		eventId = EventId.RPR_408.toString();
		eventName = EventName.TRIGGER.toString();
		eventType = EventType.SYSTEM.toString();

		}catch(Exception e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();

			LOGGER.error(LOGDISPLAY,"Message triggering failed", e);
		}finally {
			String description = isTransactionSuccessful ? "Message triggered successfully"
					: "Message triggering failed";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}
	}

	/**
	 * Process.
	 *
	 * @param object the object
	 * @return the message DTO
	 */
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		return null;
	}
}
