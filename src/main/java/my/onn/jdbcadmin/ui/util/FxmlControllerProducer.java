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
    public FxmlControllerProducer(WeldContainer container) {
        this.container = container;
    }

    public FxmlStage getFxmlDialog(FxmlUI fxmlUi) {
        try {
            FxmlStage dialog = (FxmlStage) container.select(fxmlUi.getDialogClass()).get();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlUi.getFxml()));
            loader.setResources(resources.getBundle());
            loader.setControllerFactory(c -> {
                return dialog;
            });

            dialog.setTitle(resources.getString(fxmlUi.getTitle()));

            Scene scene = new Scene((Parent) loader.load());
            scene.getStylesheets().add("/styles/Styles.css");
            dialog.setScene(scene);
            return dialog;
        } catch (IOException ex) {
            Logger.getLogger(FxmlControllerProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new MissingUiDialogException("No instance of " + fxmlUi.getDialogClass().getName() + " exist");
    }
}
