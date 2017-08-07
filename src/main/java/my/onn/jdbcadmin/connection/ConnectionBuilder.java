/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import static my.onn.jdbcadmin.connection.DatabaseSystemEnum.*;
import my.onn.jdbcadmin.connection.exception.DatabaseSystemNotSupportedException;

/**
 *
 * @author onn
 */
public class ConnectionBuilder {

    private DatabaseSystemEnum databaseSystemEnum;
    private String maintenanceDb;
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean emptyPassword;

    public ConnectionBuilder setDatabaseSystemEnum(DatabaseSystemEnum databaseSystem) {
        this.databaseSystemEnum = databaseSystem;
        return this;
    }

    public ConnectionBuilder setMaintenanceDb(String maintenanceDb) {
        this.maintenanceDb = maintenanceDb;
        return this;
    }

    public ConnectionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ConnectionBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ConnectionBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ConnectionBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ConnectionBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public ConnectionBuilder setEmptyPassword(boolean notEmpty) {
        this.emptyPassword = notEmpty;
        return this;
    }

    public ConnectionModel build() {
        return new ConnectionModel(
                databaseSystemEnum,
                maintenanceDb,
                name,
                host,
                port,
                username,
                password,
                emptyPassword
        );
    }

    public ConnectionModel copy(ConnectionModel connectionModel) {
        databaseSystemEnum = connectionModel.getDatabaseSystemEnum();
        maintenanceDb = connectionModel.getMaintenanceDb();
        name = connectionModel.getName();
        host = connectionModel.getHost();
        port = connectionModel.getPort();
        username = connectionModel.getUsername();
        password = connectionModel.getPassword();
        emptyPassword = connectionModel.isEmptyPassword();
        return build();
    }
}
