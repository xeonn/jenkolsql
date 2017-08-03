/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser.sqleditor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javax.inject.Inject;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SqlEditorWindow extends FxmlStage {

    private String connectionUrl;
    private String password;
    private String username;

    @Inject
    MainResource resources;

    @FXML
    private TextArea textAreaSql;
    @FXML
    private TableView<ArrayList<String>> tableViewResult;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        String fName = "/fonts/UbuntuMono-R.ttf";
        InputStream is = SqlEditorWindow.class.getResourceAsStream(fName);
        Font ubuntuFont = Font.loadFont(is, Font.getDefault().getSize() + 3);
        textAreaSql.setFont(ubuntuFont);
        try {
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(SqlEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        if (strSql.isEmpty()) {
            return;
        }

        tableViewResult.getItems().clear();
        tableViewResult.getColumns().clear();
        int columnCount = -1;

        //TODO: Refactor to use model instead of directly to ui element (tableViewResult)
        try (Connection cnn = DriverManager.getConnection(this.connectionUrl, this.username, this.password);
                Statement stmt = cnn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet result = stmt.executeQuery(strSql)) {

            ResultSetMetaData rsm = result.getMetaData();
            columnCount = rsm.getColumnCount();

            // Create data model, an array of array limited to 100 records for now
            List<ArrayList<String>> model = new ArrayList<>();

            for (int n = 0; n < columnCount; n++) {
                TableColumn<ArrayList<String>, String> col = new TableColumn<>(rsm.getColumnLabel(n + 1));
                int i = n;
                col.setCellValueFactory(c -> {
                    return new SimpleStringProperty(c.getValue().get(i));
                });
                tableViewResult.getColumns().add(col);
            }
            //TODO: Buffer all record using some format, xml or json as mapped memory file
            int count = 0;
            while (result.next() && count < 100) {
                ArrayList<String> cols = new ArrayList<>();

                for (int n = 0; n < columnCount; n++) {
                    cols.add(result.getString(n + 1));
                }
                tableViewResult.getItems().add(cols);
                count++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(SqlEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public void setConnectionUrl(String url, String username, String password) {
        if (url.isEmpty() || username.isEmpty()) {
            throw new IllegalArgumentException("Database connection not available");
        }

        this.setTitle(String.format("%s - [%s]", resources.getString("sqleditor.title"), url));

        this.connectionUrl = url;
        this.username = username;
        this.password = password;
    }

    private ResultSet executeSql(String sql) throws SQLException {
        try (Connection cnn = DriverManager.getConnection(this.connectionUrl, this.username, this.password);
                Statement stmt = cnn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet result = stmt.executeQuery(sql)) {
            return result;
        }
    }
}
