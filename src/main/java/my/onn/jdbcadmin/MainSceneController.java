package my.onn.jdbcadmin;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javax.inject.Inject;
import my.onn.jdbcadmin.browser.BrowserController;
import my.onn.jdbcadmin.connection.ConnectionDialog;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.ConnectionProperties;
import my.onn.jdbcadmin.settings.SettingsDriverDialog;
import my.onn.jdbcadmin.ui.util.FxmlControllerProducer;
import my.onn.jdbcadmin.ui.util.FxmlUI;

public class MainSceneController {

    private static final Logger logger = Logger.getLogger(MainSceneController.class.getName());

    Set<Button> connections = new TreeSet();
    private ObservableSet<ConnectionModel> connectionModels;

    private Stage stage;

    @Inject
    ConnectionProperties connectionConfig;

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
    @FXML
    private Label startLabel;
    @FXML
    private Button buttonSettings;

    public void initialize() {

        buttonSettings.setGraphic(new ImageView("/icons/settings_16x16.png"));

        connectionModels = connectionConfig.getConnectionModelsProperty();
        connectionModels.stream().forEach(cm -> createNewButton(cm));

        // Set up connection model listener to add/remove button
        connectionModels.addListener(new SetChangeListener<ConnectionModel>() {
            @Override
            public void onChanged(SetChangeListener.Change<? extends ConnectionModel> c) {

                if (c.wasAdded()) {
                    createNewButton(c.getElementAdded());
                }
                if (c.wasRemoved()) {
                    Button toRemove = (Button) tilePane.getChildren().stream()
                            .filter(b -> b.getId().equals(c.getElementRemoved().getMaintenanceDb()))
                            .findFirst().orElse(null);
                    if (toRemove != null) {
                        tilePane.getChildren().remove(toRemove);
                    }
                }

                boolean labelExist = tilePane.getChildren().stream()
                        .filter(n -> n == startLabel).findFirst().isPresent();

                if (!connectionModels.isEmpty() && labelExist) {
                    tilePane.getChildren().remove(startLabel);
                } else if (connectionModels.isEmpty() && !labelExist) {
                    tilePane.getChildren().add(startLabel);
                }
            }
        }
        );

        if (!connectionModels.isEmpty()) {
            tilePane.getChildren().remove(startLabel);
        }
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

    private void createNewButton(ConnectionModel connectionModel) {
        Button btn = new Button(String.format("%s\n%s-[%s]",
                connectionModel.getName(),
                connectionModel.getMaintenanceDb(),
                connectionModel.getHost()));
        btn.setId(connectionModel.getMaintenanceDb());

        btn.setGraphic(new ImageView(connectionModel.getDatabaseSystemEnum().getImage()));
        btn.setOnAction(e -> {
            BrowserController browser = (BrowserController) fxmlControllerProducer.getFxmlDialog(FxmlUI.BROWSER);

            // Due to unknown bug, calling initOwner disables maximize/minimize capability.
            // Temporarily disable initOwner
            //browser.initOwner(stage);
            browser.show();
            browser.setConnectionModel(connectionModel);
        });

        // Add context menu to remove the button
        MenuItem menuEdit = new MenuItem(resources.getString("contextmenu.edit.properties"));
        MenuItem menuDelete = new MenuItem(resources.getString("contextmenu.delete"));
        menuEdit.setOnAction(e -> {
            ConnectionModel modelToEdit = getConnectionModelFromButton(btn);
            ConnectionDialog editConnectionDialog
                    = (ConnectionDialog) fxmlControllerProducer.getFxmlDialog(FxmlUI.CONNECTION_DIALOG);
            editConnectionDialog.setConnectionModel(modelToEdit);
            editConnectionDialog.showAndWait();
            ConnectionModel newModel = editConnectionDialog.connectionModel().get();
            if (newModel != null) {
                // Using Remove/Add instead of Replace to trigger listevent
                connectionModels.remove(modelToEdit);
                connectionModels.add(newModel);
//                updateButton(connectionModel, btn);
            }
        });
        menuDelete.setOnAction(e -> {
            ConnectionModel toRemove = getConnectionModelFromButton(btn);
            if (toRemove != null) {
                connectionModels.remove(toRemove);
            }
            //tilePane.getChildren().remove(btn);
        });
        ContextMenu contextMenu = new ContextMenu(menuEdit, menuDelete);
        btn.setContextMenu(contextMenu);
        tilePane.getChildren().add(btn);
    }

    @FXML
    private void onActionButtonAdd(ActionEvent event) {

        ConnectionDialog newConnectionDialog
                = (ConnectionDialog) fxmlControllerProducer.getFxmlDialog(FxmlUI.CONNECTION_DIALOG);
        newConnectionDialog.showAndWait();
        ConnectionModel connectionModel = newConnectionDialog.connectionModel().get();

        if (connectionModel != null) {
            connectionModels.add(connectionModel);
        }
    }

    /**
     *
     * @param btn
     * @return null if the model does not exist
     */
    private ConnectionModel getConnectionModelFromButton(Button btn) {
        return connectionModels.stream()
                .filter(c -> c.getMaintenanceDb().equals(btn.getId()))
                .findFirst().orElse(null);
    }

    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        String text = textFieldSearch.getText();

        if (!text.isEmpty()) {
            tilePane.getChildren().stream()
                    .filter(a -> { // filter button only
                        if (a instanceof Button) {
                            a.setDisable(false);
                            ConnectionModel cm = getConnectionModelFromButton((Button) a);
                            String toSearch = cm.getName() + " " + cm.getMaintenanceDb();
                            if (!toSearch.toLowerCase().contains(text.toLowerCase())) {
                                logger.info("CM : " + cm.getMaintenanceDb() + " DOES NOT contain :" + text);
                                return true;
                            }
                        }
                        return false;
                    }).forEach(b -> {
                logger.warning("Disabling " + getConnectionModelFromButton((Button) b).getMaintenanceDb());
                b.setDisable(true);
            });
        } else {
            tilePane.getChildren().forEach(c -> c.setDisable(false));
        }

    }

    @FXML
    private void onActionButtonSettings(ActionEvent event) {
        SettingsDriverDialog dialog = (SettingsDriverDialog) fxmlControllerProducer.getFxmlDialog(FxmlUI.SETTINGSDRIVER);
        dialog.showAndWait();
    }
}
