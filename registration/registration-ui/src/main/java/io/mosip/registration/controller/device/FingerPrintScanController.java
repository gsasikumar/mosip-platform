package io.mosip.registration.controller.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_BIOMETRIC_SCAN_CONTROLLER;

/**
 * {@code FingerPrintScanController} is to scan fingerprint biometrics.
 * 
 * @author Mahesh Kumar
 * @since 1.0
 */
@Controller
public class FingerPrintScanController extends BaseController implements Initializable {

	/** The fingerprint capture controller. */
	@Autowired
	private FingerPrintCaptureController fpCaptureController;

	/** The selected anchor pane. */
	@FXML
	private AnchorPane selectedAnchorPane;

	/** The finger print scan image. */
	@FXML
	private ImageView fingerPrintScanImage;
	@FXML
	private Label popupTitle;

	/**
	 * @param popupTitle
	 *            the popupTitle to set
	 */
	public void setPopupTitle(String popupTitle) {
		this.popupTitle.setText(popupTitle);
	}

	/** The primary stage. */
	private Stage primarystage;

	/** The fingerprint details DT os. */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTOs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen started");

		fingerprintDetailsDTOs = new ArrayList<>();

		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen ended");

	}

	/**
	 * {@code init} method is to collect the values passed from
	 * {@link FingerPrintCaptureController}
	 *
	 * @param selectedPane
	 * @param stage
	 * @param detailsDTOs
	 */
	public void init(AnchorPane selectedPane, Stage stage, List<FingerprintDetailsDTO> detailsDTOs) {
		selectedAnchorPane = selectedPane;
		primarystage = stage;
		fingerprintDetailsDTOs = detailsDTOs;
		popupTitle.setText("Fingerprint");
	}

	/**
	 * This method scans the biometric of the individual
	 */
	@FXML
	private void scan() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration");

			if (popupTitle.getText().equalsIgnoreCase("Fingerprint")) {
				scanFinger();
			} else {
				scanIris();
			}

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning biometric details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * {@code ScanFinger} is to scan the fingers.
	 */
	private void scanFinger() {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has started");

		if (selectedAnchorPane.getId() == fpCaptureController.leftHandPalmPane.getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/LEFT HAND");

			Image img = loadImage("src/main/resources/FINGER PRINTS/LeftPalm.png");
			fingerPrintScanImage.setImage(img);
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.leftHandPalmImageview.setImage(img);
			SessionContext.getInstance().getMapObject().put("LEFT_PALM_PATH",
					"src/main/resources/FINGER PRINTS/LeftPalm.png");

		} else if (selectedAnchorPane.getId() == fpCaptureController.rightHandPalmPane.getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/RIGHT HAND");

			Image img = loadImage("src/main/resources/FINGER PRINTS/rightPalm.jpg");
			fingerPrintScanImage.setImage(img);
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.rightHandPalmImageview.setImage(img);
			SessionContext.getInstance().getMapObject().put("RIGHT_PALM_PATH",
					"src/main/resources/FINGER PRINTS/rightPalm.jpg");

		} else if (selectedAnchorPane.getId() == fpCaptureController.thumbPane.getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/THUMB");

			Image img = loadImage("src/main/resources/FINGER PRINTS/thumb.jpg");
			fingerPrintScanImage.setImage(img);
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.thumbImageview.setImage(img);
			SessionContext.getInstance().getMapObject().put("THUMB_PATH", "src/main/resources/FINGER PRINTS/thumb.jpg");

		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has ended");

	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 * 
	 * @param path
	 */
	private void readFingerPrints(String path) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has started");

		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.filter(Files::isRegularFile).forEach(e -> {
				File file = e.getFileName().toFile();
				if (file.getName().equals("FingerImage.bmp")) {
					try {

						FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
						byte[] allBytes = Files.readAllBytes(e.toAbsolutePath());

						fingerprintDetailsDTO.setFingerPrint(allBytes);
						fingerprintDetailsDTO.setFingerType(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setFingerprintImageName(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setNumRetry(1);
						fingerprintDetailsDTO.setForceCaptured(false);
						fingerprintDetailsDTO.setQualityScore(90);

						fingerprintDetailsDTOs.add(fingerprintDetailsDTO);

					} catch (IOException ioException) {
						LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
								ioException.getMessage());
					}
				}
			});
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has ended");
	}

	/**
	 * {@code loadImage} is to load the scanned image.
	 * 
	 * @param imgPath
	 * @return Image
	 */
	protected Image loadImage(String imgPath) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading scanned image started");
		Image img = null;
		try (FileInputStream file = new FileInputStream(new File(imgPath))) {
			img = new Image(file);
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading scanned image ended");
		return img;
	}

	/**
	 * event class to exit from present pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Exit window has been called");
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primarystage.close();

	}

	private void scanIris() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

}
