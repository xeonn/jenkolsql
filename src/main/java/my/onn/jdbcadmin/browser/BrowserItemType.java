/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser;

import javafx.scene.image.Image;
import my.onn.jdbcadmin.ui.util.IconsEnum;

/**
 *
 * @author onn
 */
public enum BrowserItemType {
    SERVER {
        @Override
        public Image getIcon() {
            return IconsEnum.SERVER.getImage();
        }
    }, SCHEMA {
        @Override
        public Image getIcon() {
            return IconsEnum.SCHEMA.getImage();
        }
    }, DATABASE {
        @Override
        public Image getIcon() {
            return IconsEnum.DATABASE.getImage();
        }
    }, ALL_TABLE {
        @Override
        public Image getIcon() {
            return IconsEnum.NOTIFICATION.getImage();
        }
    }, TABLE {
        @Override
        public Image getIcon() {
            return IconsEnum.TABLEGRID.getImage();
        }
    }, ALL_VIEW {
        @Override
        public Image getIcon() {
            return IconsEnum.SCREEN.getImage();
        }
    }, VIEW {
        @Override
        public Image getIcon() {
            return IconsEnum.OPENBOOK.getImage();
        }
    }, COLUMN {
        @Override
        public Image getIcon() {
            return IconsEnum.COLUMN.getImage();
        }
    }, PROCEDURE {
        @Override
        public Image getIcon() {
            return IconsEnum.UNKNOWN.getImage();
        }
    };

    public abstract Image getIcon();
}
