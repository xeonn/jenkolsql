/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.ui.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.inject.Inject;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.connection.ConnectionDialog;
import my.onn.jdbcadmin.connection.ConnectionModel;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *
 * @author onn
 */
public final class FxmlControllerProducer {

    private final WeldContainer container;

    @Inject
    MainResource resources;

    @Inject
    ConnectionDialog dialog;

    @Inject
    public FxmlControllerProducer(WeldContainer container) {
        this.container = container;
    }

    public ConnectionDialog connectionDialogInstance(ConnectionModel model) {
        dialog.setConnectionModel(model);
        return fxmlFactory(model);
    }

    private ConnectionDialog fxmlFactory(ConnectionModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConnectionDialog.fxml"));
            loader.setResources(resources.getBundle());
            loader.setControllerFactory(c -> {
                return dialog;
            });

            dialog.setTitle(resources.getString("database.browser.title"));
            dialog.setScene(new Scene((Parent) loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(FxmlControllerProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dialog;
    }
}
