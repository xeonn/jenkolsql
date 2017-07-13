package my.onn.jdbcadmin;

import java.util.Set;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import my.onn.jdbcadmin.connection.ConnectionDialogController;

public class MainSceneController {

    Set<Button> connections = new TreeSet();

    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button buttonAdd;
    @FXML
    private TilePane tilePane;

    public void initialize() {

    }

    @FXML
    private void onActionButtonAdd(ActionEvent event) {

        if (ConnectionDialogController.showConnectionDialog() != null) {
            Button btn = new Button("New Connection");
            tilePane.getChildren().add(btn);
        }
    }
}
