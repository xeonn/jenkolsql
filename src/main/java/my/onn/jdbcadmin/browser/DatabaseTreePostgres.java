package my.onn.jdbcadmin.browser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static my.onn.jdbcadmin.browser.BrowserItemType.*;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.connection.DatabaseSystemEnum;

public class DatabaseTreePostgres extends DatabaseTree {

    DatabaseTreePostgres(ConnectionModel connectionModel) {
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
        return "SELECT datname FROM pg_database WHERE datistemplate=false;";
    }

    @Override
    public List<BrowserItem> getCatalogItems(ResultSet catalog_resultset) throws SQLException {
        List<BrowserItem> result = new ArrayList<>();
        while (catalog_resultset.next()) {
            result.add(new BrowserItem(
                    catalog_resultset.getString(1),
                    catalog_resultset.getString(1),
                    "Database",
                    DATABASE));
        }
        return result;
    }

    @Override
    public List<BrowserItem> getSchemaItems(ResultSet schema_resultset, BrowserItem catalog) throws SQLException {
        List<BrowserItem> result = new ArrayList<>();
        while (schema_resultset.next()) {
            result.add(new BrowserItem(
                    schema_resultset.getString(1),
                    schema_resultset.getString(1),
                    "Schema",
                    SCHEMA));
        }
        return result;
    }

}
