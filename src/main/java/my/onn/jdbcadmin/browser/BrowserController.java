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
import javafx.scene.layout.VBox;
import my.onn.jdbcadmin.browser.sqleditor.SqlEditorWindow;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class BrowserController extends VBox {

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
        SqlEditorWindow window = new SqlEditorWindow();
        window.show();
    }

}
