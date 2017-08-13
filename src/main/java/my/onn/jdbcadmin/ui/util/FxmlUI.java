/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.ui.util;

import my.onn.jdbcadmin.browser.BrowserController;
import my.onn.jdbcadmin.sqleditor.SqlEditorWindow;
import my.onn.jdbcadmin.connection.ConnectionDialog;
import my.onn.jdbcadmin.settings.SettingsDialog;

/**
 *
 * @author onn
 */
public enum FxmlUI {
    BROWSER {
        @Override
        public String getFxml() {
            return "/fxml/Browser.fxml";
        }

        @Override
        public String getTitle() {
            return "database.browser.title";
        }

        @Override
        public Class getDialogClass() {
            return BrowserController.class;
        }

    },
    CONNECTION_DIALOG {
        @Override
        public String getFxml() {
            return "/fxml/ConnectionDialog.fxml";
        }

        @Override
        public String getTitle() {
            return "database.browser.title";
        }

        @Override
        public Class getDialogClass() {
            return ConnectionDialog.class;
        }
    },
    SETTINGS {
        @Override
        public String getFxml() {
            return "/fxml/Settings.fxml";
        }

        @Override
        public String getTitle() {
            return "settings.title";
        }

        @Override
        public Class getDialogClass() {
            return SettingsDialog.class;
        }

    },
    SQLEDITOR {
        @Override
        public String getFxml() {
            return "/fxml/SqlEditor.fxml";
        }

        @Override
        public String getTitle() {
            return "sqleditor.title";
        }

        @Override
        public Class getDialogClass() {
            return SqlEditorWindow.class;
        }
    };

    public abstract String getFxml();

    public abstract String getTitle();

    public abstract Class getDialogClass();
}
