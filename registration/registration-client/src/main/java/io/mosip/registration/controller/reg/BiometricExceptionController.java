package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

@Controller
public class BiometricExceptionController extends BaseController implements Initializable {
	
	@FXML
	private HBox exceptionBox;

	@FXML
	private ImageView leftEye;

	@FXML
	private ImageView rightEye;

	@FXML
	private Label leftLittle;
	@FXML
	private Label leftIndex;
	@FXML
	private Label leftMiddle;
	@FXML
	private Label leftRing;
	@FXML
	private Label leftThumb;
	@FXML
	private Label rightIndex;
	@FXML
	private Label rightLittle;
	@FXML
	private Label rightMiddle;
	@FXML
	private Label rightRing;
	@FXML
	private Label rightThumb;
	@FXML
	private Pane leftHandPane;
	@FXML
	private Pane rightHandPane;
	@FXML
	private Pane leftEyePane;
	@FXML
	private Pane rightEyePane;
	@FXML
	private Button previousBtn;
	@FXML
	private ImageView homePageImg;
	@FXML
	private Text homePageLbl;
	@FXML
	private AnchorPane biometricException;
	@FXML
	private AnchorPane biometricExceptionLayout;

	@Autowired
	private RegistrationController registrationController;

	private static final Logger LOGGER = AppConfig.getLogger(BiometricExceptionController.class);

	@Autowired
	private UserOnboardController userOnboardController;

	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;
	
	@Autowired
	private IrisCaptureController irisCaptureController;

	private List<String> fingerList = new ArrayList<>();
	private List<String> irisList = new ArrayList<>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setExceptionImage();
		fingerExceptionListener(leftLittle);
		fingerExceptionListener(leftIndex);
		fingerExceptionListener(leftMiddle);
		fingerExceptionListener(leftRing);
		fingerExceptionListener(leftThumb);
		fingerExceptionListener(rightIndex);
		fingerExceptionListener(rightLittle);
		fingerExceptionListener(rightMiddle);
		fingerExceptionListener(rightRing);
		fingerExceptionListener(rightThumb);
		irisExceptionListener(leftEye);
		irisExceptionListener(rightEye);
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			previousBtn.setVisible(false);
		} else {
			previousBtn.setVisible(true);
			homePageLbl.setVisible(false);
			homePageImg.setVisible(false);
			biometricExceptionLayout.getStyleClass().add("removeBorderStyle");
		}
		
		if (applicationContext.getApplicationMap()
				.get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
				.equals(RegistrationConstants.ENABLE)) {
			exceptionBox.getChildren().forEach(bio -> {
				if(bio.getId().equals("fingerBox")) {
					bio.setVisible(false);
					bio.setManaged(false);
				} 
			});
		}
	}

	/**
	 * This method is used to capture the finger click from the UI
	 * 
	 * @param fingerLabel
	 */
	private void fingerExceptionListener(Label fingerLabel) {

		LOGGER.info("REGISTRATION - FINGER_LABEL_LISTENER - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "It will listen the finger click funtionality");

		SimpleBooleanProperty toggleFunctionForFinger = new SimpleBooleanProperty(false);
		toggleFunctionForFinger.addListener(new ChangeListener<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue,
			 * java.lang.Object, java.lang.Object)
			 */
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ImageView image;
				if (fingerLabel.getId().contains("left")) {
					image = (ImageView) leftHandPane.lookup("#" + fingerLabel.getId() + "Img");
				} else {
					image = (ImageView) rightHandPane.lookup("#" + fingerLabel.getId() + "Img");
				}
				if (newValue && !fingerList.contains(fingerLabel.getId())) {
					fingerList.add(fingerLabel.getId());
					image.setVisible(true);
				} else {
					if (fingerList.indexOf(fingerLabel.getId()) >= 0) {
						fingerList.remove(fingerLabel.getId());
					}
					image.setVisible(false);
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("left"))) {
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("right"))) {
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}
			}
		});

		fingerLabel.setOnMouseClicked((event) -> {
			toggleFunctionForFinger.set(!toggleFunctionForFinger.get());
		});

		LOGGER.info("REGISTRATION - FINGER_LABEL_LISTENER_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of Functionality");

	}

	/**
	 * This method is used to capture the Iris click from the UI
	 * 
	 * @param irisImage
	 */
	private void irisExceptionListener(ImageView irisImage) {

		LOGGER.info("REGISTRATION - IRIS_EXCEPTION_LISTENER - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "It will listen the iris on click functionality");

		SimpleBooleanProperty toggleFunctionForIris = new SimpleBooleanProperty(false);
		Pane irisPane = (Pane) biometricException.lookup("#" + irisImage.getId() + "Pane");
		toggleFunctionForIris.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				irisPane.getStyleClass().clear();
				if (newValue && !irisList.contains(irisImage.getId())) {
					irisList.add(irisImage.getId());
					irisPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					if (irisList.indexOf(irisImage.getId()) >= 0) {
						irisList.remove(irisImage.getId());
					}
					irisPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}
			}
		});
		irisImage.setOnMouseClicked((event) -> {
			toggleFunctionForIris.set(!toggleFunctionForIris.get());
		});

		LOGGER.info("REGISTRATION - IRIS_EXCEPTION_LISTENER_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of Iris Functionality");

	}

	/**
	 * This method will call when click on next button and toggle the visibility
	 */
	public void goToNextPage() {

		LOGGER.info("REGISTRATION - NEXT_PAGE - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME, APPLICATION_ID,
				"Going to next page");

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			exceptionDTOCreation();
			userOnboardController.loadFingerPrint();			
		} else {
			exceptionDTOCreation();
			if (fingerList.isEmpty() && irisList.isEmpty()) {
				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationUIConstants.BIOMETRIC_EXCEPTION_ALERT);
			} else {
				if (((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getSelectionListDTO() != null) {

					List<BiometricExceptionDTO> biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
							.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getApplicantBiometricDTO()
									.getBiometricExceptionDTO();

					long fingerPrintCount = biometricExceptionDTOs.stream()
							.filter(bio -> bio.getBiometricType().equals("fingerprint")).count();

					if (fingerPrintCount > 0) {
						fingerPrintCaptureController.clearImage();
						registrationController.toggleBiometricExceptionVisibility(false);
						registrationController.toggleFingerprintCaptureVisibility(true);
					} else {
						registrationController.toggleBiometricExceptionVisibility(false);
						registrationController.toggleIrisCaptureVisibility(true);
					}

				} else {
					fingerPrintCaptureController.clearImage();
					registrationController.toggleBiometricExceptionVisibility(false);
					if (applicationContext.getApplicationMap()
							.get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
							.equals(RegistrationConstants.ENABLE)) {
						
						irisCaptureController.clearIrisBasedOnExceptions();
						registrationController.toggleIrisCaptureVisibility(true);
					} else {
						registrationController.toggleFingerprintCaptureVisibility(true);
					}
				}
			}
		}
	}

	/**
	 * Adding biometric exception details to the Session context
	 */
	private void exceptionDTOCreation() {

		LOGGER.info("REGISTRATION - EXCEPTION_DTO_CREATION - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "Populating the exception dto in session context");
		List<String> bioList = new ArrayList<>();
		bioList.addAll(fingerList);
		bioList.addAll(irisList);
		if (!bioList.isEmpty()) {
			List<BiometricExceptionDTO> biometricExceptionList = new ArrayList<>();
			bioList.forEach(bioType -> {
				BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
				if (bioType.contains("Eye")) {
					biometricExceptionDTO.setBiometricType("iris");
				} else {
					biometricExceptionDTO.setBiometricType("fingerprint");
				}
				biometricExceptionDTO.setMissingBiometric(bioType);
				biometricExceptionList.add(biometricExceptionDTO);
			});
			SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION,
					biometricExceptionList);
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				((BiometricDTO) SessionContext.map()
						.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO()
								.setBiometricExceptionDTO(biometricExceptionList);
			} else {
				((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getApplicantBiometricDTO()
								.setBiometricExceptionDTO(biometricExceptionList);
			}

		}

		LOGGER.info("REGISTRATION - EXCEPTION_DTO_CREATION_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of exception dto creation functionality");

	}

	/**
	 * This method will call on click of previous button and toggle the visibility
	 * based
	 */
	public void goToPreviousPage() {

		LOGGER.info("REGISTRATION - PREVIOUS_PAGE - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME, APPLICATION_ID,
				"It will go to the previous page");

		exceptionDTOCreation();
		if (fingerList.isEmpty() && irisList.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.BIOMETRIC_EXCEPTION_ALERT);
		} else {
			registrationController.getDemoGraphicTitlePane().setExpanded(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void setExceptionImage() {

		fingerList.clear();
		irisList.clear();

		List<BiometricExceptionDTO> biometricExceptionList = (List<BiometricExceptionDTO>) SessionContext.map()
				.get(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);

		if (biometricExceptionList != null && !biometricExceptionList.isEmpty()) {

			biometricExceptionList.forEach(bioException -> {

				if (bioException.getMissingBiometric().contains("left")
						&& !bioException.getMissingBiometric().contains("Eye")) {
					fingerList.add(bioException.getMissingBiometric());
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
					ImageView image = (ImageView) leftHandPane.lookup("#" + bioException.getMissingBiometric() + "Img");
					image.setVisible(true);

				} else if (bioException.getMissingBiometric().contains("right")
						&& !bioException.getMissingBiometric().contains("Eye")) {
					fingerList.add(bioException.getMissingBiometric());
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
					ImageView image = (ImageView) rightHandPane
							.lookup("#" + bioException.getMissingBiometric() + "Img");
					image.setVisible(true);

				} else if (bioException.getMissingBiometric().contains("Eye")) {
					irisList.add(bioException.getMissingBiometric());
					Pane irisPane = (Pane) biometricException.lookup("#" + bioException.getMissingBiometric() + "Pane");
					irisPane.getStyleClass().clear();
					irisPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				}
			});
		} else {
			((ImageView) leftHandPane.lookup("#leftIndexImg")).setVisible(false);
			((ImageView) leftHandPane.lookup("#leftLittleImg")).setVisible(false);
			((ImageView) leftHandPane.lookup("#leftMiddleImg")).setVisible(false);
			((ImageView) leftHandPane.lookup("#leftRingImg")).setVisible(false);
			((ImageView) leftHandPane.lookup("#leftThumbImg")).setVisible(false);
			((ImageView) rightHandPane.lookup("#rightIndexImg")).setVisible(false);
			((ImageView) rightHandPane.lookup("#rightLittleImg")).setVisible(false);
			((ImageView) rightHandPane.lookup("#rightMiddleImg")).setVisible(false);
			((ImageView) rightHandPane.lookup("#rightRingImg")).setVisible(false);
			((ImageView) rightHandPane.lookup("#rightThumbImg")).setVisible(false);
			leftHandPane.getStyleClass().clear();
			rightHandPane.getStyleClass().clear();
			leftEyePane.getStyleClass().clear();
			rightEyePane.getStyleClass().clear();
		}
	}

	public void clearSession() {
		SessionContext.map().put(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION,
				new ArrayList<>());
		SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION,
				new ArrayList<>());
		setExceptionImage();
	}

}
