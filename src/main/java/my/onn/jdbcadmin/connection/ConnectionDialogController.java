/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import my.onn.jdbcadmin.connection.model.PostgresqlConnectionModel;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class ConnectionDialogController extends BorderPane {

    Parent parent;

    private final ObjectProperty<ConnectionModel> connectionModel;
    @FXML
    private Button buttonTestConnection;

    public ObjectProperty<ConnectionModel> connectionModel() {
        return connectionModel;
    }

    public ConnectionDialogController(Parent parent) {
        this.parent = parent;
        this.connectionModel = new SimpleObjectProperty<>(new PostgresqlConnectionModel());
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    /**
     * Construct @ConnectionModel object by presenting Connection dialog to user
     *
     *
     * @return
     */
    public static ConnectionModel showConnectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(ConnectionDialogController.class.getResource("/fxml/ConnectionDialog.fxml"));
            Dialog<ConnectionModel> dialog = new Dialog<>();
            ConnectionDialogController controller = new ConnectionDialogController(dialog.getDialogPane());
            loader.setControllerFactory((p) -> {
                return controller;
            });
            dialog.setTitle("Connection Properties");
            dialog.setResizable(true);
            //ConnectionDialogController controller = new ConnectionDialogController();
            //loader.setController(controller);
            dialog.getDialogPane().setContent(loader.load());
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("Okay", ButtonData.OK_DONE));

            dialog.setResultConverter((b) -> {
                if (b.getButtonData() == ButtonData.OK_DONE) {
                    return (ConnectionModel) new PostgresqlConnectionModel();
                }
                return null;
            });

            Optional optional = dialog.showAndWait();
            if (optional.isPresent()) {
                return (ConnectionModel) optional.get();
            }
            return null;

        } catch (IOException ex) {
            Logger.getLogger(ConnectionDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @FXML
    private void onActionButtonTestConnection(ActionEvent event) {
    }

}
