package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.controller.device.FaceCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.SelectionListDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.util.kernal.RIDGenerator;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class for Registration Page Controller
 * 
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */

@Controller
public class RegistrationController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationController.class);

	@Autowired
	private DocumentScanController documentScanController;
	@FXML
	private AnchorPane documentScan;

	@Autowired
	Validations validation;

	@Autowired
	MasterSyncService masterSync;

	@Autowired
	private DemographicDetailController demographicDetailController;
	@FXML
	private AnchorPane demographicDetail;
	@FXML
	private AnchorPane fingerPrintCapture;

	@FXML
	private AnchorPane biometricException;

	@Autowired
	private FaceCaptureController faceCaptureController;
	@FXML
	private AnchorPane faceCapture;

	@Autowired
	IrisCaptureController irisCaptureController;
	@FXML
	private AnchorPane irisCapture;
	@FXML
	private AnchorPane operatorAuthentication;

	@FXML
	public ImageView biometricTracker;
	
	@FXML
	private AnchorPane RegistrationHeader;
	
	@FXML
	private AnchorPane registrationPreviewHeader;
	
	@FXML
	private AnchorPane registrationPreview;
	
	@Autowired
	private RegistrationPreviewController registrationPreviewController;

	@Autowired
	private AuthenticationController authenticationController;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {

			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create RegistrationDTO Object
			if (SessionContext.getInstance().getMapObject().get("operatorAuthentication") != null) {
				boolean isAuthentication = (boolean) SessionContext.getInstance().getMapObject()
						.get("operatorAuthentication");
				if (isAuthentication) {
					SessionContext.getInstance().getMapObject().put("demographicDetail", false);
					showCurrentPage();
				}
			}

			if (getRegistrationDtoContent() == null) {
				createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_NEW);
			}

			if (isEditPage() && getRegistrationDtoContent() != null) {
				prepareEditPageContent();
			}
			uinUpdate();

		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	private void uinUpdate() {
		if (getRegistrationDtoContent().getSelectionListDTO() != null) {
			demographicDetailController.uinUpdate();
			documentScanController.uinUpdate();
		}
	}

	public void init(SelectionListDTO selectionListDTO) {
		createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_UPDATE);
		getRegistrationDtoContent().setSelectionListDTO(selectionListDTO);
	}

	/**
	 * This method is to prepopulate all the values for edit operation
	 */
	private void prepareEditPageContent() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Preparing the Edit page content");
			demographicDetailController.prepareEditPageContent();
			documentScanController.prepareEditPageContent();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, false);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	private void saveDetail() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		try {
			auditFactory.audit(AuditEvent.SAVE_DETAIL_TO_DTO, Components.REGISTRATION_CONTROLLER,
					"Saving the details to respected DTO", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			RegistrationDTO registrationDTO = getRegistrationDtoContent();
			DemographicInfoDTO demographicInfoDTO;

			OSIDataDTO osiDataDTO = registrationDTO.getOsiDataDTO();
			RegistrationMetaDataDTO registrationMetaDataDTO = registrationDTO.getRegistrationMetaDataDTO();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_Child,
					demographicDetailController.isChild);
			demographicInfoDTO = demographicDetailController.buildDemographicInfo();

			if (demographicDetailController.isChild) {

				osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());

				registrationMetaDataDTO.setApplicationType(RegistrationConstants.CHILD);
			} else {
				registrationMetaDataDTO.setApplicationType(RegistrationConstants.ADULT);
			}

			osiDataDTO.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

			registrationDTO.setPreRegistrationId(demographicDetailController.preRegistrationId.getText());
			registrationDTO.getDemographicDTO().setDemographicInfoDTO(demographicInfoDTO);

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * To detect the face part from the applicant photograph to use it for QR
	 * Code generation
	 * 
	 * @param applicantImage
	 *            the image that is captured as applicant photograph
	 * @return BufferedImage the face that is detected from the applicant
	 *         photograph
	 */
	private BufferedImage detectApplicantFace(BufferedImage applicantImage) {
		BufferedImage detectedFace = null;
		HaarCascadeDetector detector = new HaarCascadeDetector();
		List<DetectedFace> faces = null;
		faces = detector.detectFaces(ImageUtilities.createFImage(applicantImage));
		if (!faces.isEmpty()) {
			Iterator<DetectedFace> dfi = faces.iterator();
			while (dfi.hasNext()) {
				DetectedFace face = dfi.next();
				FImage image1 = face.getFacePatch();
				detectedFace = ImageUtilities.createBufferedImage(image1);
			}
		}
		return detectedFace;
	}

	/**
	 * To compress the detected face from the image of applicant and store it in
	 * DTO to use it for QR Code generation
	 * 
	 * @param applicantImage
	 *            the image that is captured as applicant photograph
	 */
	private void compressImageForQRCode(BufferedImage detectedFace) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName(RegistrationConstants.WEB_CAMERA_IMAGE_TYPE);
			ImageWriter writer = writers.next();

			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
			writer.setOutput(imageOutputStream);

			ImageWriteParam param = writer.getDefaultWriteParam();

			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0); // Change the quality value you
											// prefer
			writer.write(null, new IIOImage(detectedFace, null, null), param);
			byte[] compressedPhoto = byteArrayOutputStream.toByteArray();
			if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
				((BiometricDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO().getFaceDetailsDTO()
								.setFace(compressedPhoto);
			} else {
				ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDtoContent().getDemographicDTO()
						.getApplicantDocumentDTO();
				applicantDocumentDTO.setCompressedFacePhoto(compressedPhoto);
			}
			byteArrayOutputStream.close();
			imageOutputStream.close();
			writer.dispose();
		} catch (IOException ioException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}

	public void saveBiometricDetails(BufferedImage applicantBufferedImage, BufferedImage exceptionBufferedImage) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		boolean isValid = true;
		if (!(boolean) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.ONBOARD_USER)) {
		isValid = demographicDetailController.validateThisPane();
		if (isValid) {
			isValid = validateDemographicPane(documentScanController.documentScanPane);
		}
		}
		if (isValid) {
			try {
				BufferedImage detectedFace = detectApplicantFace(applicantBufferedImage);
				if (detectedFace != null) {
					compressImageForQRCode(detectedFace);
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
							byteArrayOutputStream);
					byte[] photoInBytes = byteArrayOutputStream.toByteArray();
					if (!(boolean) SessionContext.getInstance().getMapObject()
							.get(RegistrationConstants.ONBOARD_USER)) {
						ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDtoContent().getDemographicDTO()
								.getApplicantDocumentDTO();
						applicantDocumentDTO.setPhoto(photoInBytes);
						applicantDocumentDTO.setPhotographName(RegistrationConstants.APPLICANT_PHOTOGRAPH_NAME);
						byteArrayOutputStream.close();
						if (exceptionBufferedImage != null) {
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							ImageIO.write(exceptionBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
									outputStream);
							byte[] exceptionPhotoInBytes = outputStream.toByteArray();
							applicantDocumentDTO.setExceptionPhoto(exceptionPhotoInBytes);
							applicantDocumentDTO.setExceptionPhotoName(RegistrationConstants.EXCEPTION_PHOTOGRAPH_NAME);
							applicantDocumentDTO.setHasExceptionPhoto(true);
							outputStream.close();
						} else {
							applicantDocumentDTO.setHasExceptionPhoto(false);
						}

						LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER,
								RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								"showing demographic preview");

						setPreviewContent();
						saveDetail();
						SessionContext.getInstance().getMapObject().put("faceCapture",false);
						SessionContext.getInstance().getMapObject().put("registrationPreview",true);
						RegistrationHeader.setVisible(false);
						registrationPreviewHeader.setVisible(true);
						registrationPreviewController.setUpPreviewContent();
						showCurrentPage();
					} else {
						((BiometricDTO) SessionContext.getInstance().getMapObject()
								.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO()
										.getFaceDetailsDTO().setFace(photoInBytes);
						byteArrayOutputStream.close();
					}
				} else {
					if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
						((BiometricDTO) SessionContext.getInstance().getMapObject()
								.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO()
										.getFaceDetailsDTO().setFace(null);
					}
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_CAPTURE_ERROR);
				}
			} catch (IOException ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		}
	}

	// Operator Authentication
	public void goToAuthenticationPage() {
		try {
			RegistrationHeader.setVisible(true);
			registrationPreviewHeader.setVisible(false);
			SessionContext.getInstance().getMapObject().put("operatorAuthentication", true);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
			loadScreen(RegistrationConstants.CREATE_PACKET_PAGE);
			authenticationController.initData(ProcessNames.PACKET.getType());
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_OPERATOR_AUTHENTICATION_PAGE_LOADING_FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}

	private void setPreviewContent() {
		faceCaptureController.setPreviewContent();
		documentScanController.setPreviewContent();
		demographicDetailController.setPreviewContent();
	}

	public RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	private Boolean isEditPage() {
		if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT) != null)
			return (Boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT);
		return false;
	}

	protected void createRegistrationDTOObject(String registrationCategory) {
		RegistrationDTO registrationDTO = new RegistrationDTO();

		// Set the RID
		registrationDTO.setRegistrationId(RIDGenerator.nextRID());

		// Create objects for Biometric DTOS
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setIntroducerBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setSupervisorBiometricDTO(createBiometricInfoDTO());
		registrationDTO.setBiometricDTO(biometricDTO);

		// Create object for Demographic DTOS
		DemographicDTO demographicDTO = new DemographicDTO();
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();

		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		Identity identity = new Identity();
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);

		registrationDTO.setDemographicDTO(demographicDTO);

		// Create object for OSIData DTO
		registrationDTO.setOsiDataDTO(new OSIDataDTO());

		// Create object for RegistrationMetaData DTO
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setRegistrationCategory(registrationCategory);
		registrationMetaDataDTO.setGeoLatitudeLoc(Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLatitude()));
		registrationMetaDataDTO.setGeoLongitudeLoc(Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLongitude()));
		registrationMetaDataDTO.setCenterId(String.valueOf(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterId()));
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);

		// Put the RegistrationDTO object to SessionContext Map
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
	}

	protected BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		return biometricInfoDTO;
	}

	public void showCurrentPage() {
		demographicDetail.setVisible(getVisiblity("demographicDetail"));
		documentScan.setVisible(getVisiblity("documentScan"));
		fingerPrintCapture.setVisible(getVisiblity("fingerPrintCapture"));
		biometricException.setVisible(getVisiblity("biometricException"));
		faceCapture.setVisible(getVisiblity("faceCapture"));
		irisCapture.setVisible(getVisiblity("irisCapture"));
		registrationPreview.setVisible(getVisiblity("registrationPreview"));
		operatorAuthentication.setVisible(getVisiblity("operatorAuthentication"));
	}

	private boolean getVisiblity(String page) {
		if (SessionContext.getInstance().getMapObject().get(page) != null) {
			return (boolean) SessionContext.getInstance().getMapObject().get(page);
		}
		return false;
	}

	/**
	 * 
	 * Validates the fields of demographic pane1
	 * 
	 */
	public boolean validateDemographicPane(AnchorPane paneToValidate) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in demographic pane");

		boolean gotoNext = true;
		List<String> excludedIds = new ArrayList<String>();
		excludedIds.add("preRegistrationId");
		excludedIds.add("virtualKeyboard");

		validation.setValidationMessage();
		gotoNext = validation.validate(paneToValidate, excludedIds, gotoNext, masterSync);
		displayValidationMessage(validation.getValidationMessage().toString());
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the fields");
		return gotoNext;
	}

	/**
	 * Display the validation failure messages
	 */
	public void displayValidationMessage(String validationMessage) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Showing the validatoin message");
		if (validationMessage.length() > 0) {
			TextArea view = new TextArea(validationMessage);
			view.setEditable(false);
			Scene scene = new Scene(new StackPane(view), 300, 200);
			Stage primaryStage = new Stage();
			primaryStage.setTitle("Invalid input");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(fXComponents.getStage());
			primaryStage.show();

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validatoin message shown successfully");
		}
	}

}
