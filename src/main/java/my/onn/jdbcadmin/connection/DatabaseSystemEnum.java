/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.connection;

/**
 *
 * @author onn
 */
public enum DatabaseSystemEnum {
    MYSQL {
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

    },
    ORACLE {
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
    },
    POSTGRES {
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
    };

    public abstract String getMaintenanceDbPrompt();

    public abstract String getHostPrompt();

    public abstract int getPortPrompt();

    public abstract String getUsernamePrompt();
}
