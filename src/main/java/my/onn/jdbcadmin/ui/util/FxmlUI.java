/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.ui.util;

import my.onn.jdbcadmin.connection.ConnectionDialog;

/**
 *
 * @author onn
 */
public enum FxmlUI {
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
    };

    public abstract String getFxml();

    public abstract String getTitle();

    public abstract Class getDialogClass();
}
