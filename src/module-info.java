module AegisVault {
	requires javafx.controls;
	requires javafx.fxml;
	
	opens gui.controllers to javafx.fxml;
    opens gui.fxml to javafx.fxml;
	opens gui to javafx.graphics, javafx.fxml;
}
