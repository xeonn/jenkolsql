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
import javafx.stage.DirectoryChooser;
import my.onn.jdbcadmin.ApplicationPreference;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SettingsDriverDialog extends FxmlStage {

    @FXML
    private Button buttonAddFolder;
    private ApplicationPreference preference;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    @FXML
    private void onActionButtonAddFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Plugin Path");
        File file = directoryChooser.showDialog(this);

        if (file != null) {
            this.preference.setPluginLocation(file.getAbsolutePath());
        }
    }

    @FXML
    private void onActionButtonOK(ActionEvent event) {
        close();
    }

}
