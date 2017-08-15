/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.ui.util;

import javafx.scene.image.Image;

/**
 * Standard icon images
 *
 * @author onn
 */
public enum IconsEnum {
    UNKNOWN {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/unknown.png");
            }
            return img;
        }
    },
    COLUMN {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/column.png");
            }
            return img;
        }
    },
    DATABASE {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/database.png");
            }
            return img;
        }
    },
    NOTIFICATION {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/notification.png");
            }
            return img;
        }
    },
    OPENBOOK {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/open-book.png");
            }
            return img;
        }
    },
    SCHEMA {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/schema.png");
            }
            return img;
        }
    },
    SCREEN {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/screen.png");
            }
            return img;
        }
    },
    SERVER {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/server_pg.png");
            }
            return img;
        }
    },
    SETTINGS {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("/icons/settings_16x16.png");
            }
            return img;
        }
    },
    TABLEGRID {
        private Image img;

        @Override
        public Image getImage() {
            if (img == null) {
                img = new Image("icons/table-grid.png");
            }
            return img;
        }
    };

    public abstract Image getImage();
}
