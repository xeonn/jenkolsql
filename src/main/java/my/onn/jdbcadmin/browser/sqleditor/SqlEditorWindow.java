/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser.sqleditor;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SqlEditorWindow extends Stage {

    @FXML
    private TextArea textAreaSql;
    @FXML
    private TableView tableViewResult;
    
    public SqlEditorWindow() throws IOException {

        FXMLLoader loader = new FXMLLoader(SqlEditorWindow.class.getResource("/fxml/SqlEditor.fxml"));
        loader.setControllerFactory(c -> {
            return this;
        });

        setScene(new Scene((Parent) loader.load()));
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }    

    @FXML
    private void onButtonRun(ActionEvent event) {
        /*
        Execute selected text or fallback to all.
        */
        
        String strSql = textAreaSql.selectedTextProperty().get();
        
        if (strSql.isEmpty()) {
            strSql = textAreaSql.getText();
        }
        
        if (strSql.isEmpty())
            return;
        
        //TODO jdbc driver execute sql
        // fill up the rows
        ObservableList<String> row = FXCollections.observableArrayList();
        row.add(strSql);
        tableViewResult.getItems().add(row);
        
        // Add column based on result metadata
        TableColumn col1 = new TableColumn("Test Column 1");
        col1.setCellValueFactory(c -> {
            return new SimpleStringProperty(row.get(0));
        });
        TableColumn col2 = new TableColumn("Test Column 2");
        tableViewResult.getColumns().clear();
        tableViewResult.getColumns().addAll(col1,col2);
        
        tableViewResult.refresh();
    }

    @FXML
    private void onActionSave(ActionEvent event) {
        // Save to sql
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as ...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        fileChooser.setInitialFileName("New File");
        fileChooser.showSaveDialog(this);
    }

    @FXML
    private void onActionOpenFile(ActionEvent event) {
            // Save to sql
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SQL file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        fileChooser.showOpenDialog(this);
    }
    
}
