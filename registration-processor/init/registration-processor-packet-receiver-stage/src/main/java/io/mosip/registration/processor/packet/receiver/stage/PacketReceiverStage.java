package io.mosip.registration.processor.packet.receiver.stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.manager.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.packet.receiver.builder.PacketReceiverResponseBuilder;
import io.mosip.registration.processor.packet.receiver.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.receiver.exception.PacketReceiverAppException;
import io.mosip.registration.processor.packet.receiver.exception.handler.PacketReceiverExceptionHandler;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.util.StatusMessage;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PacketReceiverStage.
 */

// @RefreshScope
@Service
public class PacketReceiverStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverStage.class);

	/** vertx Cluster Manager Url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	/** server context Path. */
	@Value("${server.servlet.path}")
	private String contextPath;

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The Constant APPLICATION_VERSION. */
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";

	/** The Constant MODULE_ID. */
	private static final String MODULE_ID = "mosip.registration.processor.packet.id";

	/** The Constant APPLICATION_JSON. */
	private static final String APPLICATION_JSON = "application/json";

	/** The Packet Receiver Service. */
	@Autowired
	public PacketReceiverService<File, MessageDTO> packetReceiverService;

	/** Exception handler. */
	@Autowired
	public PacketReceiverExceptionHandler globalExceptionHandler;

	/** The packet receiver response builder. */
	@Autowired
	PacketReceiverResponseBuilder packetReceiverResponseBuilder;

	/**
	 * The mosip event bus.
	 */
	private MosipEventBus mosipEventBus;

	/**  Mosip router for APIs. */
	@Autowired
	MosipRouter router;

	/**
	 * deploys this verticle.
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
	}

	/** The env. */
	@Autowired
	private Environment env;

	/*
	 * (non-Javadoc)
	 *
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		router.setRoute(this.postUrl(vertx, null, MessageBusAddress.PACKET_RECEIVER_OUT));
		this.routes(router);
		this.createServer(router.getRouter(), Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage.
	 *
	 * @param router
	 *            the router
	 */
	private void routes(MosipRouter router) {

		router.post(contextPath + "/registrationpackets");
		router.handler(this::processURL, this::processPacket, this::failure);
	};

	/**
	 * This is for failure handler.
	 *
	 * @param routingContext the routing context
	 */
	public void failure(RoutingContext routingContext) {
		this.setResponseWithDigitalSignature(routingContext, globalExceptionHandler.handler(routingContext.failure()), APPLICATION_JSON);
	}


	/**
	 * Process packet.
	 *
	 * @param ctx the ctx
	 */
	public void processPacket(RoutingContext ctx) {
		File file=null;
		try {
			file=getFileFromCtx(ctx);
			MessageDTO messageDTO = packetReceiverService.processPacket(file);
			messageDTO.setMessageBusAddress(MessageBusAddress.PACKET_RECEIVER_OUT);
			if (messageDTO.getIsValid()) {
				this.sendMessage(messageDTO);
			}
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnexpectedException(e.getMessage());
		} finally {
			if (file != null) {
				if (file.exists()) {
					deleteFile(file);
				}
			}
		 }

	}

	/**
	 * contains process logic for the context passed.
	 *
	 * @param ctx            the ctx
	 * @throws PacketReceiverAppException the packet receiver app exception
	 */
	public void processURL(RoutingContext ctx) throws PacketReceiverAppException {

		try {
			List<String> listObj = new ArrayList<>();
			listObj.add(env.getProperty(MODULE_ID));
			File file=getFileFromCtx(ctx);
			MessageDTO messageDTO = packetReceiverService.validatePacket(file, this.getClass().getSimpleName());
			listObj.add(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			listObj.add(env.getProperty(APPLICATION_VERSION));
			if (messageDTO.getIsValid()) {
				PacketReceiverResponseDTO responseData=PacketReceiverResponseBuilder.buildPacketReceiverResponse(StatusMessage.PACKET_RECEIVED.toString(), listObj);
				this.setResponseWithDigitalSignature(ctx, responseData, APPLICATION_JSON);
			}
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnexpectedException(e.getMessage());
		}

		ctx.next();
	}

	/**
	 * deletes a file.
	 *
	 * @param file
	 *            the file
	 */
	private void deleteFile(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (IOException e) {
			throw new UnexpectedException(e.getMessage());
		}
	}

	/**
	 * sends messageDTO to camel bridge.
	 *
	 * @param messageDTO
	 *            the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.PACKET_RECEIVER_OUT, messageDTO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

	/**
	 * Gets the file from ctx.
	 *
	 * @param ctx the ctx
	 * @return the file from ctx
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File getFileFromCtx(RoutingContext ctx) throws IOException {

		FileUpload fileUpload = ctx.fileUploads().iterator().next();
		FileUtils.copyFile(new File(fileUpload.uploadedFileName()),
				new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName()));
		File file = new File(new File(fileUpload.uploadedFileName()).getParent() + "/" + fileUpload.fileName());
		return file;

	}
}
