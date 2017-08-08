/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;

/**
 *
 * @author onn
 */
public class JdbcLoader {

    private static final Class[] parameters = new Class[]{URL.class};

    static {
        if (Files.exists(Paths.get("./lib"))) {
            try {
                // load jdbc driver from current lib folder
                Files.newDirectoryStream(Paths.get("./lib"), p -> p.toString().endsWith(".jar"))
                        .forEach(s -> {
                            try {
                                addURL(s.toUri().toURL());
                            } catch (IOException | IllegalAccessException | NoSuchMethodException ex) {
                                Logger.getLogger(JdbcLoader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                loadAllDrivers();
            } catch (IOException ex) {
                Logger.getLogger(JdbcLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void addURL(URL u) throws IOException, IllegalAccessException, NoSuchMethodException {

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{u});
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JdbcLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //end try catch

    }//end method

    public static void loadAllDrivers() {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        EnumSet.allOf(DatabaseSystemEnum.class).stream()
                .forEach(e -> {
                    try {
                        Driver d = (Driver) Class.forName(e.getDriverClass(), true, sysloader).newInstance();
                        DriverManager.registerDriver(d);
                    } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(JdbcLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

}
