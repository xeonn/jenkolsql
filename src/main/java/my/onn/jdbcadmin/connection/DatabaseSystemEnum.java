/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

import javafx.scene.image.Image;

/**
 *
 * @author onn
 */
public enum DatabaseSystemEnum {
    MYSQL {

        private final String PROTOCOL = "jdbc:mysql";

        @Override
        public String getMaintenanceDbPrompt() {
            return "root";
        }

        @Override
        public String getHostPrompt() {
            return "localhost";
        }

        @Override
        public int getPortPrompt() {
            return 3306;
        }

        @Override
        public String getUsernamePrompt() {
            return "root";
        }

        @Override
        public Image getImage() {
            return new Image("images/mysql_160x160.png");
        }

        @Override
        public String getProtocol() {
            return PROTOCOL;
        }

        @Override
        public String getDriverClass() {
            return "com.mysql.jdbc.Driver";
        }

        @Override
        public String getCatalogSql() {
            return "SELECT schema_name FROM information_schema.schemata";
        }

    },
    ORACLE {
        private final String PROTOCOL = "jdbc:oracle:thin";

        @Override
        public String getMaintenanceDbPrompt() {
            return "sys";
        }

        @Override
        public String getHostPrompt() {
            return "localhost";
        }

        @Override
        public int getPortPrompt() {
            return 1521;
        }

        @Override
        public String getUsernamePrompt() {
            return "sys";
        }

        @Override
        public Image getImage() {
            return new Image("images/oracle_160x160.png");
        }

        @Override
        public String getProtocol() {
            return PROTOCOL;
        }

        @Override
        public String getDriverClass() {
            return "oracle.jdbc.OracleDriver";
        }

        @Override
        public String getCatalogSql() {
            return "SELECT username FROM all_users";
        }
    },
    POSTGRES {
        private final String PROTOCOL = "jdbc:postgresql";

        @Override
        public String getMaintenanceDbPrompt() {
            return "postgres";
        }

        @Override
        public String getHostPrompt() {
            return "localhost";
        }

        @Override
        public int getPortPrompt() {
            return 5432;
        }

        @Override
        public String getUsernamePrompt() {
            return "postgres";
        }

        @Override
        public Image getImage() {
            return new Image("images/postgresql_160x160.png");
        }

        @Override
        public String getProtocol() {
            return PROTOCOL;
        }

        @Override
        public String getDriverClass() {
            return "org.postgresql.Driver";
        }

        @Override
        public String getCatalogSql() {
            return "SELECT datname FROM pg_database WHERE datistemplate=false;";
        }
    };

    public abstract String getMaintenanceDbPrompt();

    public abstract String getHostPrompt();

    public abstract int getPortPrompt();

    public abstract String getUsernamePrompt();

    public abstract Image getImage();

    public abstract String getProtocol();

    public abstract String getDriverClass();

    public abstract String getCatalogSql();
}
