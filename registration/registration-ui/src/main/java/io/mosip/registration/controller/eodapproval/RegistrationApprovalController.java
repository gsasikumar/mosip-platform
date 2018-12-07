package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_APPROVAL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.device.FingerPrintAuthenticationController;
import io.mosip.registration.controller.reg.ViewAckController;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * {@code RegistrationApprovalController} is the controller class for
 * Registration approval.
 *
 * @author Mahesh Kumar
 */
@Controller
public class RegistrationApprovalController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private Logger LOGGER = AppConfig.getLogger(RegistrationApprovalController.class);

	/**
	 * object for Registration approval service class
	 */
	@Autowired
	private RegistrationApprovalService registration;

	@Autowired
	private ViewAckController viewAckController;

	/**
	 * Table to display the created packets
	 */
	@FXML
	private TableView<RegistrationApprovalDTO> table;
	/**
	 * Registration Id column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> id;
	/**
	 * Acknowledgement form column in the table
	 */
	@FXML
	private TableColumn<RegistrationApprovalDTO, String> acknowledgementFormPath;
	/**
	 * Button for approval
	 */
	@FXML
	private ToggleButton approvalBtn;
	/**
	 * Button for rejection
	 */
	@FXML
	private ToggleButton rejectionBtn;
	/**
	 * Button for on hold
	 */
	@FXML
	private ToggleButton onHoldBtn;
	/**
	 * Button for on hold
	 */
	@FXML
	private Button submitBtn;
	/** The image view. */
	@FXML
	private ImageView imageView;

	/** The approve registration root sub pane. */
	@FXML
	private AnchorPane approveRegistrationRootSubPane;

	/** The image anchor pane. */
	@FXML
	private AnchorPane imageAnchorPane;

	/** The map list. */
	private List<Map<String, String>> approvalmapList = null;

	@FXML
	private ComboBox<String> deviceCmbBox;

	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reloadTableView();
	}

	/**
	 * Method to reload table
	 */
	public void reloadTableView() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");

		approvalmapList = new ArrayList<>(5);
		submitBtn.setVisible(false);
		approvalBtn.setVisible(false);
		rejectionBtn.setVisible(false);
		onHoldBtn.setVisible(false);
		imageAnchorPane.setVisible(false);

		id.setCellValueFactory(new PropertyValueFactory<RegistrationApprovalDTO, String>("id"));
		acknowledgementFormPath.setCellValueFactory(
				new PropertyValueFactory<RegistrationApprovalDTO, String>("acknowledgementFormPath"));

		populateTable();
		table.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 1) {
				viewAck();
			}
		});

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Page loading has been completed");
	}

	/**
	 * Viewing RegistrationAcknowledgement on selecting the Registration record
	 */
	private void viewAck() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form started");
		if (table.getSelectionModel().getSelectedItem() != null) {

			if (!approvalmapList.isEmpty()) {
				submitBtn.setVisible(true);
			}

			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(false);
			onHoldBtn.setSelected(false);

			approvalBtn.setVisible(true);
			rejectionBtn.setVisible(true);
			onHoldBtn.setVisible(true);
			imageAnchorPane.setVisible(true);

			for (Map<String, String> map : approvalmapList) {

				if (map.get(RegistrationConstants.REGISTRATIONID) == table.getSelectionModel().getSelectedItem()
						.getId()) {
					if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.APPROVED.getCode()) {
						approvalBtn.setSelected(true);
					} else if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.REJECTED
							.getCode()) {
						rejectionBtn.setSelected(true);
					} else if (map.get(RegistrationConstants.STATUSCODE) == RegistrationClientStatusCode.ON_HOLD
							.getCode()) {
						onHoldBtn.setSelected(true);
					}
				}
			}

			try (FileInputStream file = new FileInputStream(
					new File(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath()))) {
				imageView.setImage(new Image(file));
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION_APPROVAL_CONTROLLER - REGSITRATION_ACKNOWLEDGEMNT_PAGE_LOADING_FAILED",
						APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			}

		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Displaying the Acknowledgement form completed");
	}

	/**
	 * Opening registration acknowledgement form on clicking on image.
	 */
	public void openAckForm() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "Opening the Acknowledgement Form");
		viewAckController.viewAck(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(), fXComponents.getStage());

	}

	/**
	 * {@code populateTable} method is used for populating registration data
	 * 
	 */
	public void populateTable() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been started");
		List<RegistrationApprovalDTO> listData = null;

		listData = registration.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());

		if (!listData.isEmpty()) {
			ObservableList<RegistrationApprovalDTO> oList = FXCollections.observableArrayList(listData);
			table.setItems(oList);
		} else {
			approveRegistrationRootSubPane.disableProperty().set(true);
			table.setPlaceholder(new Label(RegistrationConstants.PLACEHOLDER_LABEL));
			table.getItems().clear();
		}

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID, "table population has been ended");
	}

	/**
	 * Event method for Approving packet
	 * 
	 * @param event
	 */
	public void approvePacket() {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Registration approval has been started");

		for (Map<String, String> registrationMap : approvalmapList) {
			if (registrationMap.containsValue(table.getSelectionModel().getSelectedItem().getId())) {
				approvalmapList.remove(registrationMap);
				break;
			}
		}

		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.REGISTRATIONID, table.getSelectionModel().getSelectedItem().getId());
		map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
		map.put(RegistrationConstants.STATUSCOMMENT, "");
		approvalmapList.add(map);
		approvalBtn.setSelected(true);
		rejectionBtn.setSelected(false);
		onHoldBtn.setSelected(false);
		submitBtn.setVisible(true);

		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Registration approval has been ended");
	}

	/**
	 * Event method for packet Rejection
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void rejectPacket() throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					"Rejection of Registration has been started");

			Stage primarystage = new Stage();
			primarystage.initStyle(StageStyle.UNDECORATED);
			RejectionController rejectionController = (RejectionController) Initialization
					.getApplicationContext().getBean(RegistrationConstants.REJECTION_BEAN_NAME);

			rejectionController.initData(table.getSelectionModel().getSelectedItem(), primarystage, approvalmapList);
			loadStage(primarystage, RegistrationConstants.REJECTION_PAGE);
			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(true);
			onHoldBtn.setSelected(false);
			submitBtn.setVisible(true);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Rejection of Registration has been ended");

	}

	/**
	 * Event method for OnHolding Packet
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	public void onHoldPacket() throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
					"OnHold of Registration has been started");

			Stage primarystage = new Stage();
			primarystage.initStyle(StageStyle.UNDECORATED);
			OnHoldController onHoldController = (OnHoldController) Initialization.getApplicationContext()
					.getBean(RegistrationConstants.ONHOLD_BEAN_NAME);
			onHoldController.initData(table.getSelectionModel().getSelectedItem(), primarystage, approvalmapList);
			loadStage(primarystage, RegistrationConstants.ONHOLD_PAGE);
			approvalBtn.setSelected(false);
			rejectionBtn.setSelected(false);
			onHoldBtn.setSelected(true);
			submitBtn.setVisible(true);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"OnHold of Registration has been ended");
	}

	/**
	 * method for Authentication 
	 * 
	 * @throws RegBaseCheckedException
	 */
	public void submit() throws RegBaseCheckedException {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Supervisor Authentication has been started");
		Parent ackRoot;
		try {
			Stage primaryStage = new Stage();
			primaryStage.initStyle(StageStyle.UNDECORATED);
			FXMLLoader fxmlLoader = BaseController
					.loadChild(getClass().getResource(RegistrationConstants.USER_AUTHENTICATION));
			ackRoot = fxmlLoader.load();
			primaryStage.setResizable(false);
			Scene scene = new Scene(ackRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(fXComponents.getStage());
			primaryStage.show();
			FingerPrintAuthenticationController fpcontroller = fxmlLoader.getController();
			fpcontroller.init(this);

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Supervisor Authentication has been ended");
	}

	/**
	 * Loading stage
	 * @param primarystage
	 * @param fxmlPath
	 * @return
	 * @throws RegBaseCheckedException
	 */
	private Stage loadStage(Stage primarystage, String fxmlPath) throws RegBaseCheckedException {

		try {
			AnchorPane authRoot = BaseController.load(getClass().getResource(fxmlPath));
			Scene scene = new Scene(authRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primarystage.setScene(scene);
			primarystage.initModality(Modality.WINDOW_MODAL);
			primarystage.initOwner(fXComponents.getStage());
			primarystage.show();
			primarystage.resizableProperty().set(false);

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		return primarystage;
	}

	@Override
	public void getFingerPrintStatus(Stage primaryStage) {
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status started");
		for (Map<String, String> map : approvalmapList) {
			registrationApprovalService.updateRegistration(map.get(RegistrationConstants.REGISTRATIONID),
					map.get(RegistrationConstants.STATUSCOMMENT), map.get(RegistrationConstants.STATUSCODE));
		}
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.AUTH_APPROVAL_SUCCESS_MSG);
		primaryStage.close();
		reloadTableView();
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Updation of registration according to status ended");
	}
}
