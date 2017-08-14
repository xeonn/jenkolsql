/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Singleton;

/**
 * A singleton class maintaining access to internal resources available within
 * the package jar
 *
 * @author onn
 */
@Singleton
public class MainResource {

    private static final String RESOURCE_BUNDLE = "baselocale";
    private final ResourceBundle bundle;

    public MainResource() {
        Locale defaultLocale = Locale.getDefault();
//        Locale myLocale = new Locale("ms", "MY");
        bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, defaultLocale);
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }
}
