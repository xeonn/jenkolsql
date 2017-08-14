/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.settings;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SettingsDriverDialog extends FxmlStage {

    @FXML
    private Button buttonAddFolder;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    @FXML
    private void onActionButtonAddFolder(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Plugin Path");
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = fileChooser.showOpenDialog(this);

        if (file != null) {
        }
    }

    @FXML
    private void onActionButtonOK(ActionEvent event) {
        close();
    }

}
