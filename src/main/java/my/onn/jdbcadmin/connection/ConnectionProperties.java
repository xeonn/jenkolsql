/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.SortedProperties;

/**
 * Serialize user connection properties
 *
 * @author onn
 */
@Singleton
public class ConnectionProperties {

    @Inject
    MainResource resources;

    private final String FILENAME = "jenkolsql";
    private final String EXT = "properties";
    private final String CONFIGROOT = "Connections";

    private final SortedProperties config;

    private final ObservableSet<ConnectionModel> connectionModels = FXCollections.observableSet(new HashSet());
    private final String fileLocation = String.format("%s/%s.%s", System.getProperty("user.dir"), FILENAME, EXT);

    public ConnectionProperties() {
        this.config = new SortedProperties();
    }

    public ObservableSet<ConnectionModel> getConnectionModelsProperty() {
        return connectionModels;
    }

    private void setConnectionModelProperty(ConnectionModel connectionModel) {

        /* Example format for database connection key is
        connections.                        -- static identifier defined by ConnectionProperties.CONFIGROOT
        .[POSTGRES]                         -- enumeration name from DatabaseSystemENUM
        .[maintenancedb]                    -- name of maintenancedb supplied by user
        .host,name,username,password,port   -- other info
         */
        String serverGroup
                = CONFIGROOT + "."
                + connectionModel.getDatabaseSystemEnum().toString() + "."
                + connectionModel.getMaintenanceDb() + ".";

        config.setProperty(serverGroup + "system", connectionModel.getDatabaseSystemEnum().toString());
        config.setProperty(serverGroup + "host", connectionModel.getHost());
        config.setProperty(serverGroup + "maintenancedb", connectionModel.getMaintenanceDb());
        config.setProperty(serverGroup + "name", connectionModel.getName());
        config.setProperty(serverGroup + "username", connectionModel.getUsername());
        config.setProperty(serverGroup + "password", connectionModel.getPassword());
        config.setProperty(serverGroup + "port", Integer.toString(connectionModel.getPort()));

        // TODO: Code to save configuration
//        builder.save();
        //properties
    }

    /**
     * Clear and reload connection model from property file
     */
    private void loadConnection() {
        connectionModels.clear();
        // Group connection by [connections.[SYSTEM].maintenancedb]
        final String token = ".";
        Set<String> connections = config.entrySet().stream()
                .sorted((e1, e2) -> {
                    return e1.getKey().toString().compareTo(e2.getKey().toString());
                }).collect(Collectors.groupingBy(
                (o) -> {
                    String key = o.getKey().toString();
                    key = key.substring(0, key.lastIndexOf(token));
                    return key;
                }, Collectors.counting())).keySet();

        for (String groupKey : connections) {
            DatabaseSystemEnum dse = DatabaseSystemEnum.valueOf(
                    groupKey.substring(
                            groupKey.indexOf(token) + 1,
                            groupKey.lastIndexOf(token)));
            ConnectionModel cm = new ConnectionBuilder()
                    .setDatabaseSystemEnum(dse)
                    .setHost(config.get(groupKey + ".host").toString())
                    .setMaintenanceDb(config.get(groupKey + ".maintenancedb").toString())
                    .setName(config.get(groupKey + ".name").toString())
                    .setPassword(config.get(groupKey + ".password").toString())
                    .setPort(Integer.parseInt(config.get(groupKey + ".port").toString()))
                    .setUsername(config.get(groupKey + ".username").toString())
                    .build();

            connectionModels.add(cm);
        }

        System.out.println(connections.toString());
    }

    @PostConstruct
    void loadFromDisk() {
        File file = new File(fileLocation);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                    InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                config.load(reader);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
                Logger.getLogger(ConnectionProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadConnection();
    }

    @PreDestroy
    void saveToDisk() {
        config.clear();
        connectionModels.stream()
                .forEach(cm -> setConnectionModelProperty(cm));

        File file = new File(fileLocation);
        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            config.store(writer, "JenkolSQL");
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
