/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javax.inject.Inject;
import my.onn.jdbcadmin.ui.util.FxmlControllerProducer;
import my.onn.jdbcadmin.ui.util.FxmlStage;
import my.onn.jdbcadmin.ui.util.FxmlUI;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class BrowserController extends FxmlStage {

    @Inject
    FxmlControllerProducer fxmlControllerProducer;

    @FXML
    private Button buttonRefresh;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    @FXML
    private void onActionButtonSqlEditor(ActionEvent event) throws IOException {
        fxmlControllerProducer.getFxmlDialog(FxmlUI.SQLEDITOR).show();
    }

}
