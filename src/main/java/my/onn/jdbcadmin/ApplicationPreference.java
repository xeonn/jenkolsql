/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import javafx.scene.control.Alert;

/**
 *
 * @author onn
 */
public class ApplicationPreference {

    private final String PLUGIN_PATH = "plugin.path";

    private Preferences pref;

    public ApplicationPreference() {
        pref = Preferences.userRoot().node(this.getClass().getName());
    }

    public void setPluginPath(String absolutePath) {
        pref.put(PLUGIN_PATH, absolutePath);
    }

    public String getPluginPath() {
        return pref.get(PLUGIN_PATH, "");
    }

    public void save() {
        try {
            pref.sync();
            pref.flush();
        } catch (BackingStoreException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
    }

}
