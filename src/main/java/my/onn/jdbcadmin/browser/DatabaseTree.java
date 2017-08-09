/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;
import my.onn.jdbcadmin.connection.exception.DatabaseSystemNotSupportedException;

/**
 * Class to interpret database metadata into
 *
 * @author onn
 */
public abstract class DatabaseTree {

    protected ConnectionModel connectionModel;

    static DatabaseTree get(ConnectionModel connectionModel) {
        switch (connectionModel.getDatabaseSystemEnum()) {
            case MYSQL:
                return new DatabaseTreeMysql(connectionModel);
            case POSTGRES:
                return new DatabaseTreePostgres(connectionModel);
            case ORACLE:
                return new DatabaseTreeOracle(connectionModel);
            default:
                throw new DatabaseSystemNotSupportedException();
        }
    }

    public DatabaseTree(ConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
    }

    public abstract List<BrowserItem> getCatalogItem(DatabaseSystemEnum dse);

    public abstract List<BrowserItem> getSchemaItem(BrowserItem catalog);

    public abstract List<BrowserItem> getTables(BrowserItem schema);

    public abstract String getCatalogSql();

    public abstract List<BrowserItem> getCatalogItems(ResultSet catalog_resultset) throws SQLException;

    public abstract List<BrowserItem> getSchemaItems(ResultSet schemas, BrowserItem catalog) throws SQLException;
}
