package org.mosip.registration.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class RegistrationOfficerUpdateController extends BaseController{

	@FXML
	private Label scanDate;
	
	@FXML
	private Label updateDate;
	
	@FXML
	private Label syncDate;
	
	@FXML
	private Label downloadDate;
	
	public void initialize() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm:ss");
		updateDate.setText(sdf.format(new Date()));
		syncDate.setText(sdf.format(new Date()));
		downloadDate.setText(sdf.format(new Date()));
		scanDate.setText(sdf.format(new Date()));
	}
}
