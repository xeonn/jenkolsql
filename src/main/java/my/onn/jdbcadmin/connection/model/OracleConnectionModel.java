/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection.model;

import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;

/**
 *
 * @author onn
 */
public final class OracleConnectionModel extends ConnectionModel {

    public OracleConnectionModel() {
        this.databaseSystemProperty().setValue(DatabaseSystemEnum.ORACLE);
    }

    @Override
    public String toString() {
        return "Oracle Server";
    }

    @Override
    public String getUrl(String database) {
        return String.format("jdbc:oracle://%s:%d/%s",
                getHost(), getPort(),
                (database == null || database.isEmpty() ? getMaintenanceDb() : database));
    }

    @Override
    public String getMaintenanceUrl() {
        return String.format("jdbc:oracle://%s:%d/?",
                getHost(), getPort());
    }
}
