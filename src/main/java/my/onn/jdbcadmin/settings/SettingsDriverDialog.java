/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javax.inject.Inject;
import my.onn.jdbcadmin.ApplicationPreference;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SettingsDriverDialog extends FxmlStage {

    @Inject
    private ApplicationPreference preference;

    @FXML
    private Button buttonAddFolder;
    @FXML
    private TextField textFieldPluginPath;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        this.textFieldPluginPath.setText(preference.getPluginPath());
    }

    @FXML
    private void onActionButtonAddFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Plugin Path");
        File file = directoryChooser.showDialog(this);

        if (file != null) {
            this.textFieldPluginPath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onActionButtonOK(ActionEvent event) {

        if (!textFieldPluginPath.getText().isEmpty()
                && Files.exists(Paths.get(textFieldPluginPath.getText()),
                        LinkOption.NOFOLLOW_LINKS)) {
            preference.setPluginPath(textFieldPluginPath.getText());
            preference.save();
        }

        close();
    }

}
