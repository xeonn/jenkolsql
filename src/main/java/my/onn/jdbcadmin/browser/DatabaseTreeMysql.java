package my.onn.jdbcadmin.browser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static my.onn.jdbcadmin.browser.BrowserItemType.*;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;
import my.onn.jdbcadmin.ui.util.IconsEnum;

public class DatabaseTreeMysql extends DatabaseTree {

    DatabaseTreeMysql(ConnectionModel connectionModel) {
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
        return "SELECT schema_name FROM information_schema.schemata";
    }

    @Override
    public List<BrowserItem> getCatalogItems(ResultSet catalog_resultset) throws SQLException {
        List<BrowserItem> result = new ArrayList<>();

        /*
            Mysql does not support catalog. Return a standard catalog
         */
        result.add(new BrowserItem(
                connectionModel.getMaintenanceDb(),
                connectionModel.getMaintenanceDb(),
                "Database",
                DATABASE));
        return result;
    }

    @Override
    public List<BrowserItem> getSchemaItems(ResultSet schemas, BrowserItem catalog) {
        /*Ignore schema and return the catalog instead*/
        List<BrowserItem> result = new ArrayList<>();
        result.add(new BrowserItem(catalog.getValue(), catalog.getLabel(), "Schema", SCHEMA));
        return result;
    }
}
