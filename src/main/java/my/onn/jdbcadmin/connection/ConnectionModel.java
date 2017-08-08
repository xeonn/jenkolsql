/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import static my.onn.jdbcadmin.connection.DatabaseSystemEnum.*;

/**
 * Immutable class to represent database connection information.
 *
 * ConnectionModel can and should only be created by {
 *
 * @see my.onn.jdbcadmin.connection.ConnectionBuilder}
 * @author onn
 */
public final class ConnectionModel {

    private final DatabaseSystemEnum databaseSystem;
    private final String maintenanceDb;
    private final String name;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean emptyPassword;

    public ConnectionModel(DatabaseSystemEnum databaseSystem, String maintenanceDb, String name, String host, int port, String username, String password, boolean emptyPassword) {
        this.databaseSystem = databaseSystem;
        this.maintenanceDb = maintenanceDb;
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.emptyPassword = emptyPassword;
    }

    public DatabaseSystemEnum getDatabaseSystemEnum() {
        return databaseSystem;
    }

    public String getMaintenanceDb() {
        return maintenanceDb;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Provide url for a specific database
     *
     * Used in database browser window
     *
     * @param database can be null to use specified maintenanceDb instead
     * @return
     */
    public String getUrl(String database) {
        if (getDatabaseSystemEnum() == ORACLE) {
            // jdbc:oracle:thin:@<hostname>:<port>:<sid>
            return String.format("%s:@%s:%d:%s",
                    getDatabaseSystemEnum().getProtocol(), getHost(), getPort(),
                    getMaintenanceDb());
        }

        if (database == null) {
            if (getDatabaseSystemEnum() == MYSQL) {
                return String.format("%s://%s:%d/%s",
                        getDatabaseSystemEnum().getProtocol(), getHost(), getPort(),
                        getMaintenanceDb());
            }
            return getMaintenanceUrl();
        } else {
            return String.format("%s://%s:%d/%s",
                    getDatabaseSystemEnum().getProtocol(), getHost(), getPort(),
                    database);
        }
    }

    public String getMaintenanceUrl() {
        if (getDatabaseSystemEnum() == ORACLE) {
            // jdbc:oracle:thin:@<hostname>:<port>:<sid>
            return String.format("%s:@%s:%d:%s",
                    getDatabaseSystemEnum().getProtocol(), getHost(), getPort(),
                    getMaintenanceDb());
        }
        return String.format("%s://%s:%d/?",
                getDatabaseSystemEnum().getProtocol(), getHost(), getPort());
    }

    public boolean isEmptyPassword() {
        return emptyPassword;
    }

}
