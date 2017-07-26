/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection.model;

import my.onn.jdbcadmin.connection.ConnectionModel;

/**
 *
 * @author onn
 */
public class PostgresqlConnectionModel implements ConnectionModel {

    public PostgresqlConnectionModel() {
    }

    @Override
    public String getDatabasename() {
        return "Postgresql Database";
    }

}
