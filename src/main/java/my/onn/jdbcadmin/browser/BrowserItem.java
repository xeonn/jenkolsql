/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import my.onn.jdbcadmin.ui.util.IconsEnum;

/**
 * Immutable model class holding data for Browser window
 *
 * @author onn
 */
public class BrowserItem {

    private final String label;
    private final IconsEnum icon;
    private final ObservableList<BrowserItem> children;

    BrowserItem(String label, IconsEnum icon) {
        this.label = label;
        this.icon = icon;
        this.children = FXCollections.observableArrayList();
    }

    public IconsEnum getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public ObservableList<BrowserItem> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
