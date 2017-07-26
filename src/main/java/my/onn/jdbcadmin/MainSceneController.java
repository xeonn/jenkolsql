package my.onn.jdbcadmin;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import my.onn.jdbcadmin.connection.ConnectionDialog;

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

    /**
     * Initialized by MainApp::start
     *
     * @param stage
     * @throws IOException
     */
    void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
        loader.setControllerFactory(c -> {
            return this;
        });

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onActionButtonAdd(ActionEvent event) {

        if (ConnectionDialog.showConnectionDialog() != null) {
            Button btn = new Button("New Connection");
            btn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Browser.fxml"));

                    Stage stage = new Stage();
                    stage.setTitle("Database Browser");
                    Scene scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            tilePane.getChildren().add(btn);
        }
    }
}
