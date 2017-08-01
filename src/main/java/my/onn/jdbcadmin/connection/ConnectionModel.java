/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author onn
 */
public abstract class ConnectionModel {

    private ObjectProperty<DatabaseSystemEnum> databaseSystem = new SimpleObjectProperty<>();
    private StringProperty maintenanceDb = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty host = new SimpleStringProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();

    public ObjectProperty<DatabaseSystemEnum> databaseSystemProperty() {
        return databaseSystem;
    }

    public StringProperty maintenanceDbProperty() {
        return maintenanceDb;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty hostProperty() {
        return host;
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public DatabaseSystemEnum getDatabaseSystem() {
        return this.databaseSystem.get();
    }

    public String getMaintenanceDb() {
        return maintenanceDb.get();
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getHost() {
        return host.get();
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    void setMaintenanceDb(String text) {
        maintenanceDb.set(text);
    }

    /**
     * Provide url for a specific database
     *
     * Used in database browser window
     *
     * @param database
     * @return
     */
    public abstract String getUrl(String database);

    public String getUrl() {
        return getUrl(null);
    }

    /**
     * Provide url string that can be used to list all database.
     *
     * @return
     */
    public abstract String getMaintenanceUrl();

}
