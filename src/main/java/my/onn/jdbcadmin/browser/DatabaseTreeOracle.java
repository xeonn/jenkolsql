package my.onn.jdbcadmin.browser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static my.onn.jdbcadmin.browser.BrowserItemType.*;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;

public class DatabaseTreeOracle extends DatabaseTree {

    DatabaseTreeOracle(ConnectionModel connectionModel) {
        super(connectionModel);
    }

    @Override
    public List<BrowserItem> getCatalogItem(DatabaseSystemEnum dse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BrowserItem> getSchemaItem(BrowserItem catalog) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BrowserItem> getTables(BrowserItem schema) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCatalogSql() {
        return "SELECT username FROM all_users";
    }

    @Override
    public List<BrowserItem> getCatalogItems(ResultSet catalog_resultset) throws SQLException {
        List<BrowserItem> result = new ArrayList<>();

        /* Oracle does not support catalog. We ignore the resultset and return
        a catalog from connection model
         */
        result.add(new BrowserItem(
                null,
                connectionModel.getMaintenanceDb(),
                "Database",
                DATABASE));
        return result;
    }

    @Override
    public List<BrowserItem> getSchemaItems(ResultSet schemas, BrowserItem catalog) {
        /*Ignore schema and return the catalog instead*/
        List<BrowserItem> result = new ArrayList<>();
        result.add(new BrowserItem(connectionModel.getUsername(), catalog.getLabel(), "Schema", SCHEMA));
        return result;
    }

}
