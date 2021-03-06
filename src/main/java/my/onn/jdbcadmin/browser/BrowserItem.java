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
    private final String value;
    private final BrowserItemType type;
    private final ObservableList<BrowserItem> children;
    private final String description;

    BrowserItem(String value, String label, String description, BrowserItemType type) {
        this.value = value;
        this.label = label;
        this.type = type;
        this.description = description;
        this.children = FXCollections.observableArrayList();
    }

    public BrowserItemType getBrowserItemType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public ObservableList<BrowserItem> getChildren() {
        return children;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        if (value == null) {
            return label;
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return this.label;
    }

}
