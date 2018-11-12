package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.PolygonBuilder;


/**
 * The enums for introducer types
 * 
 * @author Taleev Aalam
 * @since 1.0.0
 *
 */

@Controller
public class RegistrationController extends BaseController {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(RegistrationController.class);

	@FXML
	private TextField preRegistrationId;

	@FXML
	private TextField fullName;

	@FXML
	private TextField fullName_lc;

	@FXML
	private DatePicker ageDatePicker;

	@FXML
	private TextField ageField;

	@FXML
	private Label toggleLabel1;

	@FXML
	private Label toggleLabel2;

	@FXML
	private AnchorPane childSpecificFields;

	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

	@FXML
	private ComboBox<String> gender;

	@FXML
	private TextField addressLine1;

	@FXML
	private TextField addressLine1_lc;

	@FXML
	private TextField addressLine2;

	@FXML
	private TextField addressLine2_lc;

	@FXML
	private TextField addressLine3;

	@FXML
	private TextField addressLine3_lc;

	@FXML
	private TextField emailId;

	@FXML
	private TextField mobileNo;

	@FXML
	private TextField region;

	@FXML
	private TextField city;

	@FXML
	private TextField province;

	@FXML
	private TextField postalCode;

	@FXML
	private TextField localAdminAuthority;

	@FXML
	private TextField cni_or_pin_number;

	@FXML
	private TextField parentName;

	@FXML
	private TextField uinId;

	@FXML
	private TitledPane demoGraphicTitlePane1;

	@FXML
	private TitledPane demoGraphicTitlePane2;

	@FXML
	private Accordion accord;

	@FXML
	private AnchorPane demoGraphicPane1;

	@FXML
	private AnchorPane demoGraphicPane2;

	@FXML
	private AnchorPane anchor_pane_registration;

	private boolean toggleAgeOrDobField = false;

	private boolean isChild = false;

	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;

	VirtualKeyboard keyboard = new VirtualKeyboard();

	Node keyboardNode = keyboard.view();

	@FXML
	private void initialize() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Entering the LOGIN_CONTROLLER");
		switchedOn.set(false);
		ageDatePicker.setDisable(false);
		ageField.setDisable(true);
		disableFutureDays();
		toggleFunction();
		ageFieldValidations();
		ageValidationInDatePicker();
		dateFormatter();
		loadAddressFromPreviousEntry();
		populateTheLocalLangFields();
		loadLanguageSpecificKeyboard();
		demoGraphicPane1.getChildren().add(keyboardNode);
		keyboardNode.setVisible(false);
	}

	/**
	 * 
	 * Loading the address detail from previous entry
	 * 
	 */
	public void loadAddressFromPreviousEntry() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading address from previous entry");
		Map<String, Object> sessionMapObject = SessionContext.getInstance().getMapObject();
		AddressDTO addressDto = (AddressDTO) sessionMapObject.get("PrevAddress");
		if (addressDto != null) {
			LocationDTO locationDto = addressDto.getLocationDTO();
			region.setText(locationDto.getRegion());
			city.setText(locationDto.getCity());
			province.setText(locationDto.getProvince());
			postalCode.setText(locationDto.getPostalCode());
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
	}

	/**
	 * 
	 * Loading the second demographic pane
	 * 
	 */
	public void gotoSecondDemographicPane() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading the second demographic pane");
		if (validatePaneOne()) {
			demoGraphicTitlePane1.setContent(null);
			demoGraphicTitlePane1.setExpanded(false);
			demoGraphicTitlePane1.setContent(demoGraphicPane2);
			demoGraphicTitlePane1.setExpanded(true);
			anchor_pane_registration.setMaxHeight(700);
		}

	}

	/**
	 * 
	 * Setting the focus to address line 1 local
	 * 
	 */
	public void adressLine1Focus() {
		addressLine1_lc.requestFocus();
		keyboardNode.setTranslateY(400);
		keyboardNode.setTranslateX(150);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Setting the focus to address line 2 local
	 * 
	 */
	public void adressLine2Focus() {
		addressLine2_lc.requestFocus();
		keyboardNode.setTranslateY(480);
		keyboardNode.setTranslateX(150);
		keyboardNode.setVisible(true);
	}
	
	/**
	 * 
	 * Setting the focus to address line 3 local
	 * 
	 */
	public void adressLine3Focus() {
		addressLine3_lc.requestFocus();
		keyboardNode.setTranslateY(550);
		keyboardNode.setTranslateX(150);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Setting the focus to full name local
	 * 
	 */
	public void fullNameFocus() {
		fullName_lc.requestFocus();
		keyboardNode.setTranslateY(150);
		keyboardNode.setTranslateX(150);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	public void saveDetail() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		LocationDTO locationDto = new LocationDTO();
		AddressDTO addressDto = new AddressDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		OSIDataDTO osiDataDto = new OSIDataDTO();
		if (validatePaneTwo()) {
			demographicInfoDTO.setFullName(fullName.getText());
			if (ageDatePicker.getValue() != null) {
				demographicInfoDTO.setDateOfBirth(
						Date.from(ageDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			}
			demographicInfoDTO.setAge(ageField.getText());
			demographicInfoDTO.setGender(gender.getValue());
			addressDto.setAddressLine1(addressLine1.getText());
			addressDto.setAddressLine2(addressLine2.getText());
			addressDto.setLine3(addressLine3.getText());
			locationDto.setProvince(province.getText());
			locationDto.setCity(city.getText());
			locationDto.setRegion(region.getText());
			locationDto.setPostalCode(postalCode.getText());
			addressDto.setLocationDTO(locationDto);
			demographicInfoDTO.setAddressDTO(addressDto);
			demographicInfoDTO.setMobile(mobileNo.getText());
			demographicInfoDTO.setEmailId(emailId.getText());
			demographicInfoDTO.setChild(isChild);
			demographicInfoDTO.setCneOrPINNumber(cni_or_pin_number.getText());
			demographicInfoDTO.setCneOrPINNumber(localAdminAuthority.getText());
			if (isChild) {
				if (uinId.getText().length() == 28) {
					demographicDTO.setIntroducerRID(uinId.getText());
				} else {
					demographicDTO.setIntroducerUIN(uinId.getText());
				}
				osiDataDto.setIntroducerType(IntroducerType.PARENT.getCode());
				demographicInfoDTO.setParentOrGuardianName(parentName.getText());
			}
			demographicDTO.setDemoInUserLang(demographicInfoDTO);
			osiDataDto.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

			registrationDTO.setPreRegistrationId(preRegistrationId.getText());
			registrationDTO.setOsiDataDTO(osiDataDto);
			registrationDTO.setDemographicDTO(demographicDTO);

			LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					"Saved the fields to DTO");

			registrationOfficerPacketController.showReciept(registrationDTO);
		}

	}

	/**
	 * Validating the age field for the child/Infant check.
	 */
	public void ageValidationInDatePicker() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the age given by DatePiker");
		if (ageDatePicker.getValue() != null) {
			LocalDate selectedDate = ageDatePicker.getValue();
			Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			long ageInMilliSeconds = new Date().getTime() - date.getTime();
			long ageInDays = TimeUnit.MILLISECONDS.toDays(ageInMilliSeconds);
			int age = (int) ageInDays / 365;
			if (age < 5) {
				childSpecificFields.setVisible(true);
				isChild = true;
			} else {
				childSpecificFields.setVisible(false);
			}
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the age given by DatePiker");
	}

	/**
	 * Disabling the future days in the date picker calendar.
	 */
	private void disableFutureDays() {
		ageDatePicker.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) > 0);
			}
		});

		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Future dates disabled");
	}

	/**
	 * Populating the user language fields to local language fields
	 */
	private void populateTheLocalLangFields() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Populating the local language fields");
		fullName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				if (!newValue.matches("([A-z]+\\s?\\.?)+")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.FULL_NAME_EMPTY, "Numbers are not allowed");
					fullName.setText(fullName.getText().replaceAll("\\d+", ""));
					fullName.requestFocus();
				} else {
					fullName_lc.setText(fullName.getText());
				}
			}
		});

		addressLine1.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine1_lc.setText(addressLine1.getText());
			}
		});

		addressLine2.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine2_lc.setText(addressLine2.getText());
			}
		});

		addressLine3.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine3_lc.setText(addressLine3.getText());
			}
		});
	}
	
	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void loadLanguageSpecificKeyboard() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading the local language keyboard");
		addressLine1_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		addressLine2_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		addressLine3_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		fullName_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void ageFieldValidations() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Validating the age given by age field");
		ageField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				if (ageField.getText().length() > 2) {
					String age = ageField.getText().substring(0, 2);
					ageField.setText(age);
				}
				if (!newValue.matches("\\d*")) {
					ageField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		ageField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				int ageValue = 0;
				if (!newPropertyValue && !ageField.getText().equals("")) {
					ageValue = Integer.parseInt(ageField.getText());
				}
				if (ageValue < Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))
						&& ageValue != 0) {
					childSpecificFields.setVisible(true);
					isChild = true;
				} else {
					isChild = false;
					childSpecificFields.setVisible(false);
				}
			}
		});
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Validating the age given by age field");
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Entering into toggle function for toggle label 1 and toggle level 2");

		toggleLabel1.setStyle("-fx-background-color: grey;");
		toggleLabel2.setStyle("-fx-background-color: white;");
		switchedOn.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					toggleLabel1.setStyle("-fx-background-color: white;");
					toggleLabel2.setStyle("-fx-background-color: grey;");
					ageField.clear();
					ageDatePicker.setValue(null);
					parentName.clear();
					uinId.clear();
					childSpecificFields.setVisible(false);
					ageDatePicker.setDisable(true);
					ageField.setDisable(false);
					toggleAgeOrDobField = true;

				} else {
					toggleLabel1.setStyle("-fx-background-color: grey;");
					toggleLabel2.setStyle("-fx-background-color: white;");
					ageField.clear();
					ageDatePicker.setValue(null);
					parentName.clear();
					uinId.clear();
					childSpecificFields.setVisible(false);
					ageDatePicker.setDisable(false);
					ageField.setDisable(true);
					toggleAgeOrDobField = false;

				}
			}
		});

		toggleLabel1.setOnMouseClicked((event) -> {
			switchedOn.set(!switchedOn.get());
		});
		toggleLabel2.setOnMouseClicked((event) -> {
			switchedOn.set(!switchedOn.get());
		});
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Exiting the toggle function for toggle label 1 and toggle level 2");
	}

	/**
	 * To dispaly the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	private void dateFormatter() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the date format");
		ageDatePicker.setConverter(new StringConverter<LocalDate>() {
			String pattern = "dd-MM-yyyy";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			{
				ageDatePicker.setPromptText(pattern.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				return date != null ? dateFormatter.format(date) : "";

			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 */
	public void goToHomePage() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		try {
			BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT_LOADING_FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * 
	 * Validates the fields of demographic pane1
	 * 
	 */
	private boolean validatePaneOne() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in first demographic pane");
		boolean gotoNext = false;
		if (validateRegex(fullName, "([A-z]+\\s?\\.?)+")) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.FULL_NAME_EMPTY, "Numbers are not allowed");
			fullName.requestFocus();
		} else {
			if (gender.getValue() == null) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.GENDER_EMPTY);
				gender.requestFocus();
			} else {
				if (validateRegex(addressLine1, "^.{6,20}$")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.ADDRESS_LINE_1_EMPTY, RegistrationConstants.ADDRESS_LINE_WARNING);
					addressLine1.requestFocus();
				} else {
					if (validateRegex(addressLine2, "^.{6,20}$")) {
						generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.ADDRESS_LINE_2_EMPTY, RegistrationConstants.ADDRESS_LINE_WARNING);
						addressLine2.requestFocus();
					} else {
						if (validateRegex(region, "^.{6,20}$")) {
							generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.REGION_EMPTY);
							region.requestFocus();
						} else {
							if (validateRegex(city, "^.{6,20}$")) {
								generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.CITY_EMPTY);
								city.requestFocus();
							} else {
								if (validateRegex(province, "^.{6,20}$")) {
									generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
											RegistrationConstants.PROVINCE_EMPTY);
									province.requestFocus();
								} else {
									if (validateRegex(postalCode, "\\d{1,2}")) {
										generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
												RegistrationConstants.POSTAL_CODE_EMPTY);
										postalCode.requestFocus();
									} else {
										if (validateRegex(localAdminAuthority, "^.{6,20}$")) {
											generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
													RegistrationConstants.LOCAL_ADMIN_AUTHORITY_EMPTY);
											localAdminAuthority.requestFocus();
										} else {
											if (validateRegex(mobileNo, "\\d{1,2}")) {
												generateAlert("Error",
														AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
														RegistrationConstants.MOBILE_NUMBER_EMPTY,
														RegistrationConstants.MOBILE_NUMBER_EXAMPLE);
												mobileNo.requestFocus();
											} else {
												if (validateRegex(emailId, "[A-z]+")) {
													generateAlert("Error",
															AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
															RegistrationConstants.EMAIL_ID_EMPTY,
															RegistrationConstants.EMAIL_ID_EXAMPLE);
													emailId.requestFocus();
												} else {
													if (validateRegex(cni_or_pin_number, "^.{6,20}$")) {
														generateAlert("Error",
																AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
																RegistrationConstants.CNIE_OR_PIN_NUMBER_EMPTY);
														localAdminAuthority.requestFocus();
													} else {
														if (toggleAgeOrDobField) {
															if (validateRegex(ageField, "\\d{1,2}")) {
																generateAlert("Error",
																		AlertType.valueOf(
																				RegistrationConstants.ALERT_ERROR),
																		RegistrationConstants.AGE_EMPTY,
																		RegistrationConstants.AGE_WARNING);
																ageField.requestFocus();
															} else {
																gotoNext = true;
															}
														} else if (!toggleAgeOrDobField) {
															if (ageDatePicker.getValue() == null) {
																generateAlert("Error",
																		AlertType.valueOf(
																				RegistrationConstants.ALERT_ERROR),
																		RegistrationConstants.DATE_OF_BIRTH_EMPTY);
																ageDatePicker.requestFocus();
															} else {
																gotoNext = true;
															}
														}

													}

												}

											}

										}
									}
								}
							}
						}
					}
				}

			}
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the fields");
		return gotoNext;
	}

	/**
	 * 
	 * Validate the fields of demographic pane 2
	 * 
	 */

	private boolean validatePaneTwo() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in second demographic pane");
		boolean gotoNext = false;
		if (isChild) {
			gotoNext = getParentToggle();
		} else {
			gotoNext = true;
		}

		return gotoNext;
	}

	/**
	 * 
	 * Toggles the parent fields
	 * 
	 */
	private boolean getParentToggle() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Toggling for parent/guardian fields");
		boolean gotoNext = false;

		if (isChild) {
			if (validateRegex(parentName, "[[A-z]+\\s?\\.?]+")) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						"Please provide parent name");
				parentName.requestFocus();
			} else {
				if (validateRegex(uinId, "\\d{6,28}")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							"Please provide parent UIN Id");
					uinId.requestFocus();
				} else {
					gotoNext = true;
				}
			}
		}
		return gotoNext;
	}
}

//will remove this file from here in
class VirtualKeyboard {
	  private final VBox root ;
	  
	  /**
	   * Creates a Virtual Keyboard. 
	   * @param target The node that will receive KeyEvents from this keyboard. 
	   * If target is null, KeyEvents will be dynamically forwarded to the focus owner
	   * in the Scene containing this keyboard.
	   */
	  
	  public VirtualKeyboard(ReadOnlyObjectProperty<Node> target) {
	    this.root = new VBox(5);
	    root.setPadding(new Insets(10));
	    root.setId("virtualKeyboard");
	    final Modifiers modifiers = new Modifiers();

	    // Data for regular buttons; split into rows
	    final String[][] unshifted = new String[][] {
	        { "`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=" },
	        { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\" },
	        { "a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'" },
	        { "z", "x", "c", "v", "b", "n", "m", ",", ".", "/" } };

	    final String[][] shifted = new String[][] {
	        { "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+" },
	        { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "{", "}", "|" },
	        { "A", "S", "D", "F", "G", "H", "J", "K", "L", ":", "\"" },
	        { "Z", "X", "C", "V", "B", "N", "M", "<", ">", "?" } };

	    final KeyCode[][] codes = new KeyCode[][] {
	        { KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
	            KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7,
	            KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0, KeyCode.SUBTRACT,
	            KeyCode.EQUALS },
	        { KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y,
	            KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.P, KeyCode.OPEN_BRACKET,
	            KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH },
	        { KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H,
	            KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE },
	        { KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N,
	            KeyCode.M, KeyCode.COMMA, KeyCode.PERIOD, KeyCode.SLASH } };

	    // non-regular buttons (don't respond to Shift)
	    final Button escape = createNonshiftableButton("Esc", KeyCode.ESCAPE, modifiers, target);
	    final Button backspace = createNonshiftableButton("Backspace", KeyCode.BACK_SPACE, modifiers, target);
	    final Button delete = createNonshiftableButton("Del", KeyCode.DELETE, modifiers, target);
	    final Button enter = createNonshiftableButton("Enter", KeyCode.ENTER,  modifiers, target);
	    final Button tab = createNonshiftableButton("Tab", KeyCode.TAB, modifiers, target);

	    // Cursor keys, with graphic instead of text
	    final Button cursorLeft = createCursorKey(KeyCode.LEFT, modifiers, target, 15.0, 5.0, 15.0, 15.0, 5.0, 10.0);
	    final Button cursorRight = createCursorKey(KeyCode.RIGHT, modifiers, target, 5.0, 5.0, 5.0, 15.0, 15.0, 10.0);
	    final Button cursorUp = createCursorKey(KeyCode.UP, modifiers, target, 10.0, 0.0, 15.0, 10.0, 5.0, 10.0);
	    final Button cursorDown = createCursorKey(KeyCode.DOWN, modifiers, target, 10.0, 10.0, 15.0, 0.0, 5.0, 0.0);
	    final VBox cursorUpDown = new VBox(2);
	    cursorUpDown.getChildren().addAll(cursorUp, cursorDown);

	    // "Extras" to go at the left or right end of each row of buttons.
	    final Node[][] extraLeftButtons = new Node[][] { {escape}, {tab}, {modifiers.capsLockKey()}, {modifiers.shiftKey()} };
	    final Node[][] extraRightButtons = new Node[][] { {backspace}, {delete}, {enter}, {modifiers.secondShiftKey()} };

	    // build layout
	    for (int row = 0; row < unshifted.length; row++) {
	      HBox hbox = new HBox(5);
	      hbox.setAlignment(Pos.CENTER);
	      root.getChildren().add(hbox);

	      hbox.getChildren().addAll(extraLeftButtons[row]);
	      for (int k = 0; k < unshifted[row].length; k++) {
	        hbox.getChildren().add( createShiftableButton(unshifted[row][k], shifted[row][k], codes[row][k], modifiers, target));
	      }
	      hbox.getChildren().addAll(extraRightButtons[row]);
	    }

	    final Button spaceBar = createNonshiftableButton(" ", KeyCode.SPACE, modifiers, target);
	    spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
	    HBox.setHgrow(spaceBar, Priority.ALWAYS);

	    final HBox bottomRow = new HBox(5);
	    bottomRow.setAlignment(Pos.CENTER);
	    bottomRow.getChildren().addAll(modifiers.ctrlKey(), modifiers.altKey(),
	        modifiers.metaKey(), spaceBar, cursorLeft, cursorUpDown, cursorRight);
	    root.getChildren().add(bottomRow);    
	  }
	  
	  /**
	   * Creates a VirtualKeyboard which uses the focusProperty of the scene to which it is attached as its target
	   */
	  public VirtualKeyboard() {
	    this(null);
	  }
	  
	  /**
	   * Visual component displaying this keyboard. The returned node has a style class of "virtual-keyboard".
	   * Buttons in the view have a style class of "virtual-keyboard-button".
	   * @return a view of the keyboard.
	   */
	  public Node view() {
	    return root ;
	  }
	  
	  // Creates a "regular" button that has an unshifted and shifted value
	  private Button createShiftableButton(final String unshifted, final String shifted,
	      final KeyCode code, Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
	    final ReadOnlyBooleanProperty letter = new SimpleBooleanProperty( unshifted.length() == 1 && Character.isLetter(unshifted.charAt(0)));
	    final StringBinding text = 
	        Bindings.when(modifiers.shiftDown().or(modifiers.capsLockOn().and(letter)))
	        .then(shifted)
	        .otherwise(unshifted);
	    Button button = createButton(text, code, modifiers, target);
	    return button;
	  }

	  // Creates a button with fixed text not responding to Shift
	  private Button createNonshiftableButton(final String text, final KeyCode code, final Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
	    StringProperty textProperty = new SimpleStringProperty(text);
	    Button button = createButton(textProperty, code, modifiers, target);
	    return button;
	  }
	  
	  // Creates a button with mutable text, and registers listener with it
	  private Button createButton(final ObservableStringValue text, final KeyCode code, final Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
	    final Button button = new Button();
	    button.textProperty().bind(text);
	        
	    button.setFocusTraversable(false);
	        
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent event) {

	        final Node targetNode ;
	        if (target != null) {
	          targetNode = target.get();
	        } else {
	          targetNode = view().getScene().getFocusOwner();
	        }
	        
	        if (targetNode != null) {
	          final String character;
	          if (text.get().length() == 1) {
	            character = text.get();
	          } else {
	            character = KeyEvent.CHAR_UNDEFINED;
	          }
	          final KeyEvent keyPressEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_PRESSED, character, code, modifiers);
	          targetNode.fireEvent(keyPressEvent);
	          final KeyEvent keyReleasedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_RELEASED, character, code, modifiers);
	          targetNode.fireEvent(keyReleasedEvent);
	          if (character != KeyEvent.CHAR_UNDEFINED) {
	            final KeyEvent keyTypedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_TYPED, character, code, modifiers);
	            targetNode.fireEvent(keyTypedEvent);
	          }
	          modifiers.releaseKeys();
	        }
	      }
	    });
	    return button;
	  }

	  // Utility method to create a KeyEvent from the Modifiers
	  private KeyEvent createKeyEvent(Object source, EventTarget target,
	      EventType<KeyEvent> eventType, String character, KeyCode code,
	      Modifiers modifiers) {
	    return new KeyEvent(source, target, eventType, character, code.toString(),
	        code, modifiers.shiftDown().get(), modifiers.ctrlDown().get(),
	        modifiers.altDown().get(), modifiers.metaDown().get());
	  }
	  
	  // Utility method for creating cursor keys:
	  private Button createCursorKey(KeyCode code, Modifiers modifiers, ReadOnlyObjectProperty<Node> target, Double... points) {
	    Button button = createNonshiftableButton("", code, modifiers, target);
	    final Node graphic = PolygonBuilder.create().points(points).build();
	    graphic.setStyle("-fx-fill: -fx-mark-color;");
	    button.setGraphic(graphic);
	    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	    return button ;
	  }
	  
	  // Convenience class to bundle together the modifier keys and their selected state
	  private static class Modifiers {
	    private final ToggleButton shift;
	    private final ToggleButton shift2;
	    private final ToggleButton ctrl;
	    private final ToggleButton alt;
	    private final ToggleButton meta;
	    private final ToggleButton capsLock;

	    Modifiers() {
	      this.shift = createToggle("Shift");
	      this.shift2 = createToggle("Shift");
	      this.ctrl = createToggle("Ctrl");
	      this.alt = createToggle("Alt");
	      this.meta = createToggle("Meta");
	      this.capsLock = createToggle("Caps");

	      shift2.selectedProperty().bindBidirectional(shift.selectedProperty());
	    }

	    private ToggleButton createToggle(final String text) {
	      final ToggleButton tb = new ToggleButton(text);
	      tb.setFocusTraversable(false);
	      return tb;
	    }

	    public Node shiftKey() {
	      return shift;
	    }

	    public Node secondShiftKey() {
	      return shift2;
	    }

	    public Node ctrlKey() {
	      return ctrl;
	    }

	    public Node altKey() {
	      return alt;
	    }

	    public Node metaKey() {
	      return meta;
	    }

	    public Node capsLockKey() {
	      return capsLock;
	    }

	    public BooleanProperty shiftDown() {
	      return shift.selectedProperty();
	    }

	    public BooleanProperty ctrlDown() {
	      return ctrl.selectedProperty();
	    }

	    public BooleanProperty altDown() {
	      return alt.selectedProperty();
	    }

	    public BooleanProperty metaDown() {
	      return meta.selectedProperty();
	    }

	    public BooleanProperty capsLockOn() {
	      return capsLock.selectedProperty();
	    }

	    public void releaseKeys() {
	      shift.setSelected(false);
	      ctrl.setSelected(false);
	      alt.setSelected(false);
	      meta.setSelected(false);
	    }
	  }  
	}
