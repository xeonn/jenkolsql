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
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import javafx.util.converter.NumberStringConverter;
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
        this.connectionModel.set(model);
        setConnectionInfo();
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {

        // Enable Test and OK button once all information are available
        buttonOk.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or(
                        textFieldPassword.textProperty().isEmpty())
        );
        buttonTestConnection.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or(
                        textFieldPassword.textProperty().isEmpty())
        );

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
        setConnectionInfo();
    }

    private void setConnectionInfo() {
        if (connectionModel.get() != null) {
            choiceBoxDbSystem.getSelectionModel().select(connectionModel.get().getDatabaseSystem());
            textFieldHost.textProperty().bindBidirectional(connectionModel.get().hostProperty());
            textFieldMaintenanceDB.textProperty().bindBidirectional(connectionModel.get().maintenanceDbProperty());
            textFieldName.textProperty().bindBidirectional(connectionModel.get().nameProperty());
            textFieldPassword.textProperty().bindBidirectional(connectionModel.get().passwordProperty());
            Bindings.bindBidirectional(textFieldPort.textProperty(), connectionModel.get().portProperty(), new NumberStringConverter());
            textFieldUsername.textProperty().bindBidirectional(connectionModel.get().usernameProperty());
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

            ConnectionModel cm = DatabaseSystem.getConnectionModel(dbe);
            cm.setHost(host);
            cm.setMaintenanceDb(maintenanceDb);
            cm.setName(name);
            cm.setPassword(password);
            cm.setPort(port);
            cm.setUsername(username);

            try (Connection cnn = DriverManager.getConnection(cm.getUrl(), cm.getUsername(), cm.getPassword())) {
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

        if (connectionModel.get() == null) {
            ConnectionModel newModel = DatabaseSystem.getConnectionModel(choiceBoxDbSystem.getSelectionModel().getSelectedItem());
            newModel.databaseSystemProperty().set(choiceBoxDbSystem.getSelectionModel().getSelectedItem());
            newModel.setHost(textFieldHost.getText().isEmpty()
                    ? textFieldHost.getPromptText() : textFieldHost.getText());
            newModel.setMaintenanceDb(textFieldMaintenanceDB.getText().isEmpty()
                    ? textFieldMaintenanceDB.getPromptText() : textFieldMaintenanceDB.getText());
            newModel.setName(textFieldName.getText());
            newModel.setPassword(textFieldPassword.getText());
            newModel.setPort(Integer.parseInt(textFieldPort.getText().isEmpty()
                    ? textFieldPort.getPromptText() : textFieldPort.getText()));
            newModel.setUsername(textFieldUsername.getText().isEmpty()
                    ? textFieldUsername.getPromptText() : textFieldUsername.getText());
            connectionModel.set(newModel);
        } else {
            // Other properties are bound except for choicebox.
            connectionModel.get().databaseSystemProperty().set(
                    choiceBoxDbSystem.getSelectionModel().getSelectedItem());
        }

        this.close();
    }

    @FXML
    private void onActionButtonCancel(ActionEvent event) {
        connectionModel.setValue(null);
        this.close();
    }

}
