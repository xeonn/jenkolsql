/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import javax.inject.Inject;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class ConnectionDialog extends FxmlStage {

    private static final Logger logger = Logger.getLogger(ConnectionDialog.class.getName());

    Parent parent;

    private ObjectProperty<ConnectionModel> connectionModel;

    @Inject
    MainResource resources;

    @FXML
    private Button buttonTestConnection;
    @FXML
    private Button buttonOk;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldHost;
    @FXML
    private TextField textFieldPort;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private TextField textFieldMaintenanceDB;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ChoiceBox<DatabaseSystemEnum> choiceBoxDbSystem;
    @FXML
    private CheckBox checkBoxEmptyPassword;

    public ConnectionDialog() throws IOException {
        this.connectionModel = new SimpleObjectProperty<>();
    }

    public ObjectProperty<ConnectionModel> connectionModel() {
        return connectionModel;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setConnectionModel(ConnectionModel model) {
        this.connectionModel.set(new ConnectionBuilder().copy(model));
        updateFieldText();
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {

        // Enable Test and OK button once all information are available
        buttonOk.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or((textFieldPassword.textProperty().isEmpty().and(
                        checkBoxEmptyPassword.selectedProperty().not())))
        );
        buttonTestConnection.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or((textFieldPassword.textProperty().isEmpty().and(
                        checkBoxEmptyPassword.selectedProperty().not())))
        );
        checkBoxEmptyPassword.selectedProperty().addListener((obj, oldV, newV) -> {
            if (newV) {
                textFieldPassword.setText("");
                textFieldPassword.setDisable(true);
            } else {
                textFieldName.setDisable(false);
            }
        });

        choiceBoxDbSystem.getItems().setAll(DatabaseSystemEnum.values());
        choiceBoxDbSystem.getSelectionModel().selectedItemProperty().addListener(item -> {
            if (item != null) {
                DatabaseSystemEnum db = choiceBoxDbSystem.getSelectionModel().getSelectedItem();
                textFieldHost.setPromptText(db.getHostPrompt());
                textFieldMaintenanceDB.setPromptText(db.getMaintenanceDbPrompt());
                textFieldPort.setPromptText(Integer.toString(db.getPortPrompt()));
                textFieldUsername.setPromptText(db.getUsernamePrompt());

                // Number only field
                UnaryOperator<Change> integerFilter = c -> {
                    String newText = c.getControlNewText();
                    if (newText.matches("([1-9][0-9]*)?")) {
                        return c;
                    }
                    return null;
                };
                textFieldPort.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), db.getPortPrompt(), integerFilter));
            }
        });
        choiceBoxDbSystem.getSelectionModel().selectFirst();
        updateFieldText();
    }

    private void updateFieldText() {
        if (connectionModel.get() != null) {
            choiceBoxDbSystem.getSelectionModel().select(connectionModel.get().getDatabaseSystemEnum());
            textFieldHost.setText(connectionModel.get().getHost());
            textFieldMaintenanceDB.setText(connectionModel.get().getMaintenanceDb());
            textFieldName.setText(connectionModel.get().getName());
            textFieldPassword.setText(connectionModel.get().getPassword());
            textFieldPort.setText(Integer.toString(connectionModel.get().getPort()));
            textFieldUsername.setText(connectionModel.get().getUsername());
            checkBoxEmptyPassword.setSelected(connectionModel.get().isEmptyPassword());
        }
    }

    @FXML
    private void onActionButtonTestConnection(ActionEvent event) throws InterruptedException, ExecutionException {
        borderPane.setDisable(true);

        ProgressIndicator pgind = new ProgressIndicator();
        HBox hbox = new HBox(pgind);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(hbox);
        vbox.setAlignment(Pos.CENTER);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vbox);

        CompletableFuture.runAsync(() -> {
            // setup temporary connection model for testing purpose
            DatabaseSystemEnum dbe = choiceBoxDbSystem.getSelectionModel().getSelectedItem();
            String host = textFieldHost.getText().isEmpty()
                    ? textFieldHost.getPromptText() : textFieldHost.getText();
            String maintenanceDb = textFieldMaintenanceDB.getText().isEmpty()
                    ? textFieldMaintenanceDB.getPromptText() : textFieldMaintenanceDB.getText();
            String name = textFieldName.getText();
            String password = textFieldPassword.getText();
            int port = Integer.parseInt(textFieldPort.getText().isEmpty()
                    ? textFieldPort.getPromptText() : textFieldPort.getText());
            String username = textFieldUsername.getText().isEmpty()
                    ? textFieldUsername.getPromptText() : textFieldUsername.getText();
            boolean isEmptyPassword = checkBoxEmptyPassword.selectedProperty().get();

            ConnectionModel cm = new ConnectionBuilder()
                    .setDatabaseSystemEnum(dbe)
                    .setHost(host)
                    .setMaintenanceDb(maintenanceDb)
                    .setName(name)
                    .setPassword(password)
                    .setPort(port)
                    .setUsername(username)
                    .setEmptyPassword(isEmptyPassword)
                    .build();

            try {
                Class.forName(cm.getDatabaseSystemEnum().getDriverClass());
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            try (Connection cnn = DriverManager.getConnection(cm.getUrl(null), cm.getUsername(), cm.getPassword())) {
                if (cnn.isValid(2)) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Connection test");
                        alert.setHeaderText("Connection successful!!");
                        alert.setContentText(dbe.name());
                        alert.showAndWait();
                    });
                }
            } catch (SQLException ex) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection test");
                    alert.setContentText(ex.getLocalizedMessage());
                    alert.showAndWait();
                });
                Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).thenRun(() -> {
            Platform.runLater(() -> {
                stackPane.getChildren().remove(vbox);
                borderPane.setDisable(false);
            });
        });
    }

    @FXML
    private void onActionButtonOk(ActionEvent event) {

        ConnectionModel newModel = new ConnectionBuilder()
                .setDatabaseSystemEnum(choiceBoxDbSystem.getSelectionModel().getSelectedItem())
                .setHost(textFieldHost.getText().isEmpty()
                        ? textFieldHost.getPromptText() : textFieldHost.getText())
                .setMaintenanceDb(textFieldMaintenanceDB.getText().isEmpty()
                        ? textFieldMaintenanceDB.getPromptText() : textFieldMaintenanceDB.getText())
                .setName(textFieldName.getText())
                .setPassword(textFieldPassword.getText())
                .setPort(Integer.parseInt(textFieldPort.getText().isEmpty()
                        ? textFieldPort.getPromptText() : textFieldPort.getText()))
                .setUsername(textFieldUsername.getText().isEmpty()
                        ? textFieldUsername.getPromptText() : textFieldUsername.getText())
                .setEmptyPassword(checkBoxEmptyPassword.selectedProperty().get())
                .build();
        connectionModel.set(newModel);

        this.close();
    }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        connectionModel.setValue(null);
        this.close();
    }

}
