/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin;

import java.util.prefs.Preferences;

/**
 *
 * @author onn
 */
public class ApplicationPreference {

    Preferences pref;

    public void setPluginLocation(String absolutePath) {
        pref.put("juviasql.plugin.path", absolutePath);
    }

}
