package my.onn.jdbcadmin;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javax.inject.Inject;
import my.onn.jdbcadmin.connection.ConnectionDialog;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.ui.util.FxmlControllerProducer;
import my.onn.jdbcadmin.ui.util.FxmlUI;

public class MainSceneController {

    Set<Button> connections = new TreeSet();
    ObservableList<ConnectionModel> connectionModels;
    private Stage stage;

    @Inject
    MainResource resources;

    @Inject
    FxmlControllerProducer fxmlControllerProducer;

    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button buttonAdd;
    @FXML
    private TilePane tilePane;

    public MainSceneController() {
        connectionModels = FXCollections.observableArrayList();
    }

    public void initialize() {

    }

    /**
     * Initialized by MainApp::start
     *
     * @param stage
     * @throws IOException
     */
    void start(Stage stage) throws IOException {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
        loader.setResources(resources.getBundle());
        loader.setControllerFactory(c -> {
            return this;
        });

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("/styles/Styles.css");

        this.stage.setTitle(resources.getString("main.title"));
        this.stage.setScene(scene);
        this.stage.show();
    }

    @FXML
    private void onActionButtonAdd(ActionEvent event) {

        ConnectionDialog newConnectionDialog
                = (ConnectionDialog) fxmlControllerProducer.getFxmlDialog(FxmlUI.CONNECTION_DIALOG);
        newConnectionDialog.showAndWait();
        ConnectionModel connectionModel = newConnectionDialog.connectionModel().get();

        if (connectionModel != null) {
            Button btn = new Button(connectionModel.getName() + "\n"
                    + connectionModel.getHost()
                    + "\n" + connectionModel.toString());
            btn.setGraphic(new ImageView(connectionModel.getDatabaseSystem().getImage()));
            btn.setOnAction(e -> {
                fxmlControllerProducer.getFxmlDialog(FxmlUI.BROWSER).show();
            });

            // Add context menu to remove the button
            MenuItem menuEdit = new MenuItem(resources.getString("contextmenu.edit.properties"));
            MenuItem menuDelete = new MenuItem(resources.getString("contextmenu.delete"));
            menuEdit.setOnAction(e -> {
                int idx = tilePane.getChildren().indexOf(btn);
                ConnectionDialog editConnectionDialog
                        = (ConnectionDialog) fxmlControllerProducer.getFxmlDialog(FxmlUI.CONNECTION_DIALOG);
                editConnectionDialog.setConnectionModel(connectionModels.get(idx));
                editConnectionDialog.showAndWait();
                ConnectionModel newModel = editConnectionDialog.connectionModel().get();
                if (newModel != null) {
                    connectionModels.set(idx, newModel);
                    btn.setText(newModel.getName() + "\n"
                            + newModel.getHost() + "\n"
                            + newModel.toString());
                }
            });
            menuDelete.setOnAction(e -> {
                tilePane.getChildren().remove(btn);
            });
            ContextMenu contextMenu = new ContextMenu(menuEdit, menuDelete);
            btn.setContextMenu(contextMenu);

            connectionModels.add(connectionModel);
            tilePane.getChildren().add(btn);
        }
    }
}
