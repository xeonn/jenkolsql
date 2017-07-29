/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class ConnectionDialog extends Stage {

    private static final Logger logger = Logger.getLogger(ConnectionDialog.class.getName());

    Parent parent;

    private ObjectProperty<ConnectionModel> connectionModel;
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

    public ObjectProperty<ConnectionModel> connectionModel() {
        return connectionModel;
    }

    public ConnectionDialog(ConnectionModel model, Parent parent) throws IOException {
        this.parent = parent;
        this.connectionModel = new SimpleObjectProperty<>(model);

        FXMLLoader loader = new FXMLLoader(ConnectionDialog.class.getResource("/fxml/ConnectionDialog.fxml"));
        loader.setControllerFactory(c -> {
            return this;
        });

        setScene(new Scene((Parent) loader.load()));
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // Enable Test and OK button once all information are available
        buttonOk.disableProperty().bind(
                textFieldHost.textProperty().isEmpty().or(
                        textFieldMaintenanceDB.textProperty().isEmpty()).or(
                        textFieldName.textProperty().isEmpty()).or(
                        textFieldPassword.textProperty().isEmpty()).or(
                        textFieldPort.textProperty().isEmpty()).or(
                        textFieldUsername.textProperty().isEmpty())
        );
        buttonTestConnection.disableProperty().bind(
                textFieldHost.textProperty().isEmpty().or(
                        textFieldMaintenanceDB.textProperty().isEmpty()).or(
                        textFieldName.textProperty().isEmpty()).or(
                        textFieldPassword.textProperty().isEmpty()).or(
                        textFieldPort.textProperty().isEmpty()).or(
                        textFieldUsername.textProperty().isEmpty())
        );

        choiceBoxDbSystem.getItems().setAll(DatabaseSystemEnum.values());
        choiceBoxDbSystem.getSelectionModel().selectedItemProperty().addListener(item -> {
            if (item != null) {
                DatabaseSystemEnum db = choiceBoxDbSystem.getSelectionModel().getSelectedItem();
                textFieldHost.setPromptText(db.getHostPrompt());
                textFieldMaintenanceDB.setPromptText(db.getMaintenanceDbPrompt());
                textFieldPort.setPromptText(Integer.toString(db.getPortPrompt()));
                textFieldUsername.setPromptText(db.getUsernamePrompt());
            }
        });
        choiceBoxDbSystem.getSelectionModel().selectFirst();
        initializeConnectionInfo();
    }

    private void initializeConnectionInfo() {
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

    /**
     * Construct @ConnectionModel object by presenting Connection dialog to user
     *
     *
     * @param model reference to existing connection model or null to create new
     * connection
     * @return
     */
    public static ConnectionModel showConnectionDialog(ConnectionModel model, Parent parent) {
        ConnectionModel cnn = model;
        try {
            ConnectionDialog dialog = new ConnectionDialog(model, parent);
            logger.info("Showing dialog");
            dialog.showAndWait();
            logger.info("Done with the connection dialog");
            cnn = dialog.connectionModel.get();

        } catch (IOException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnn;
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
            try {
                logger.log(Level.INFO, "Simulating getting database connection, sleep for 3 seconds ..... in {0}", Thread.currentThread().getName());
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).thenRun(() -> {
            Platform.runLater(() -> {
                logger.log(Level.INFO, "thenApply .... in {0}", Thread.currentThread().getName());
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
            newModel.setHost(textFieldHost.getText());
            newModel.setMaintenanceDb(textFieldMaintenanceDB.getText());
            newModel.setName(textFieldName.getText());
            newModel.setPassword(textFieldPassword.getText());
            newModel.setPort(Integer.parseInt(textFieldPort.getText()));
            newModel.setUsername(textFieldUsername.getText());
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
