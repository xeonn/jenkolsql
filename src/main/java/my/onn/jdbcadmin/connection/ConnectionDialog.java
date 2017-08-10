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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.converter.IntegerStringConverter;
import javax.inject.Inject;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.ui.util.FxmlStage;
import org.controlsfx.control.PopOver;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

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
    private final ValidationSupport validationSupport;

    public ConnectionDialog() throws IOException {
        this.connectionModel = new SimpleObjectProperty<>();
        validationSupport = new ValidationSupport();
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

    private void setupControlBinding() {
        // Enable Test and OK button once all information are available
        buttonOk.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or((textFieldPassword.textProperty().isEmpty().and(
                        checkBoxEmptyPassword.selectedProperty().not())))
        );
        buttonTestConnection.disableProperty().bind(
                textFieldName.textProperty().isEmpty().or((textFieldPassword.textProperty().isEmpty().and(
                        checkBoxEmptyPassword.selectedProperty().not())))
        );

    }

    private void setupControlListeners() {
        /*
        Toggle check box for empty password. Unchecking a checkbox will clear text
        in password field.
         */
        checkBoxEmptyPassword.selectedProperty().addListener((obj, oldV, newV) -> {
            if (newV) {
                textFieldPassword.setText("");
                textFieldPassword.setDisable(true);
            } else {
                textFieldName.setDisable(false);
            }
        });

        /*
         Populate and repopulate text field prompt text depending on selected
         database type
         */
        choiceBoxDbSystem.getSelectionModel().selectedItemProperty().addListener(item -> {
            if (item != null) {
                DatabaseSystemEnum db = choiceBoxDbSystem.getSelectionModel().getSelectedItem();
                textFieldHost.setPromptText(db.getHostPrompt());
                textFieldMaintenanceDB.setPromptText(db.getMaintenanceDbPrompt());
                textFieldPort.setPromptText(Integer.toString(db.getPortPrompt()));
                textFieldUsername.setPromptText(db.getUsernamePrompt());

                // Set up Port text field to Number only field
                UnaryOperator<Change> integerFilter = c -> {
                    String newText = c.getControlNewText();
                    if (newText.matches("([1-9][0-9]*)?")) {
                        return c;
                    }
                    return null;
                };
                textFieldPort.setTextFormatter(new TextFormatter<>(
                        new IntegerStringConverter(), db.getPortPrompt(), integerFilter));
                /*
             Try loading the selected driver and inform users via Popup
                 */
                try {
                    DriverManager.getDriver(db.getDriverClass()).jdbcCompliant();
                } catch (SQLException ex) {
                    logger.info("Testing the driver");
                    Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (db.getDriverClass().equals("oracle.jdbc.OracleDriver")) {
                    TextFlow flow = new TextFlow();
                    Text title = new Text("Requires Oracle Jdbc driver");
                    flow.getChildren().add(title);
                    HBox hbox1 = new HBox(flow);
                    hbox1.setId("card-title");
                    Label content = new Label(
                            "Due to license restriction, Oracle drivers requires separate download from\n");
                    Text link = new Text("http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html\n");
                    Text footer = new Text(
                            "Unzip the jar and placed them in the lib folder.");

                    VBox vbox = new VBox(hbox1, content, link, footer);
                    vbox.setSpacing(5);
                    vbox.setPadding(new Insets(10));
                    vbox.setId("card");
                    PopOver po = new PopOver(vbox);
                    po.setCornerRadius(4);
                    po.show(choiceBoxDbSystem);
                }
            }

        });

    }

    private void setupControlValidation() {
        validationSupport.registerValidator(textFieldName,
                Validator.createEmptyValidator("Text is required"));
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {

        choiceBoxDbSystem.getItems().setAll(DatabaseSystemEnum.values());

        setupControlBinding();
        setupControlListeners();
        setupControlValidation();

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

            try (Connection cnn = DriverManager.getConnection(cm.getUrl(maintenanceDb), cm.getUsername(), cm.getPassword())) {
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

        if (validationSupport.isInvalid()) {
            return;
        }

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
