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
            return new Image("images/mysql_512x512.png");
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

        @Override
        public Image getImage() {
            return new Image("images/oracledb_512x512.png");
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

        @Override
        public Image getImage() {
            return new Image("images/postgresql_512x512.png");
        }
    };

    public abstract String getMaintenanceDbPrompt();

    public abstract String getHostPrompt();

    public abstract int getPortPrompt();

    public abstract String getUsernamePrompt();

    public abstract Image getImage();
}
