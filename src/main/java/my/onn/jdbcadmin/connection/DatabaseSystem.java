/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import static my.onn.jdbcadmin.connection.DatabaseSystemEnum.MYSQL;
import static my.onn.jdbcadmin.connection.DatabaseSystemEnum.ORACLE;
import static my.onn.jdbcadmin.connection.DatabaseSystemEnum.POSTGRES;
import my.onn.jdbcadmin.connection.exception.DatabaseSystemNotSupportedException;
import my.onn.jdbcadmin.connection.model.MysqlConnectionModel;
import my.onn.jdbcadmin.connection.model.OracleConnectionModel;
import my.onn.jdbcadmin.connection.model.PostgresqlConnectionModel;

/**
 *
 * @author onn
 */
public class DatabaseSystem {
    public static ConnectionModel getConnectionModel(DatabaseSystemEnum dbSys) {
        switch (dbSys) {
            case MYSQL:
                return new MysqlConnectionModel();
            case ORACLE:
                return new OracleConnectionModel();
            case POSTGRES:
                return new PostgresqlConnectionModel();
            default:
                throw new DatabaseSystemNotSupportedException();
        }
    }
}
