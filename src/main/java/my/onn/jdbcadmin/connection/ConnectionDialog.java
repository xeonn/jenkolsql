/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import my.onn.jdbcadmin.connection.model.PostgresqlConnectionModel;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class ConnectionDialog extends Stage {

    Parent parent;

    private final ObjectProperty<ConnectionModel> connectionModel;
    @FXML
    private Button buttonTestConnection;

    public ObjectProperty<ConnectionModel> connectionModel() {
        return connectionModel;
    }

    public ConnectionDialog() throws IOException {
        this.parent = parent;
        this.connectionModel = new SimpleObjectProperty<>(new PostgresqlConnectionModel());

        FXMLLoader loader = new FXMLLoader(ConnectionDialog.class.getResource("/fxml/ConnectionDialog.fxml"));
        loader.setController(this);

        setScene(new Scene((Parent) loader.load()));
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

            ConnectionDialog dialog = new ConnectionDialog();
            dialog.showAndWait();

            return null;

        } catch (IOException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @FXML
    private void onActionButtonTestConnection(ActionEvent event) {
    }

}
