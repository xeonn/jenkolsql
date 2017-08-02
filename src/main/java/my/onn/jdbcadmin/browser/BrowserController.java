/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.browser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.inject.Inject;
import my.onn.jdbcadmin.browser.sqleditor.SqlEditorWindow;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.ui.util.FxmlControllerProducer;
import my.onn.jdbcadmin.ui.util.FxmlStage;
import my.onn.jdbcadmin.ui.util.FxmlUI;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class BrowserController extends FxmlStage {

    private static final Logger logger = Logger.getLogger(BrowserController.class.getName());

    ConnectionModel connectionModel;

    @Inject
    FxmlControllerProducer fxmlControllerProducer;

    @FXML
    private Button buttonRefresh;
    @FXML
    private TreeView<?> treeView;
    @FXML
    private Button buttonSqlEditor;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // Connection not yet available here
        buttonSqlEditor.setDisable(true);

        /*
        Get Connection for Sql editor from selected tree item.
        Disable sql button if no child item selected or when root item is selected
         */
        treeView.getSelectionModel().selectedIndexProperty().addListener((obj, oldV, newV) -> {
            if (newV.intValue() > 0) {
                if (treeView.getSelectionModel().getSelectedItem().getParent() == null) {
                    buttonSqlEditor.setDisable(true);
                } else {
                    buttonSqlEditor.setDisable(false);
                }
            } else {
                buttonSqlEditor.setDisable(true);
            }
        });
    }

    /**
     * Set and activate connection for the browser window.
     *
     * @param model
     */
    public void setConnectionModel(ConnectionModel model) {
        if (this.connectionModel == null) {
            this.connectionModel = model;
            logger.info("Connecting to " + model.getHost());
            try (Connection cnn = DriverManager.getConnection(model.getUrl(null), model.getUsername(), model.getPassword())) {
                if (!cnn.isValid(2)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Notification");
                    alert.setContentText("Connection failed");
                    alert.showAndWait();
                } else {
                    refreshTree();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BrowserController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            logger.warning(String.format("Connection exist [%s]. Ignoring new connection set %s",
                    this.connectionModel.getUrl(null), model.getUrl(null)));
        }
    }

    private void refreshTree() throws IOException {
        try (Connection cnn = DriverManager.getConnection(model.getMaintenanceUrl(), model.getUsername(), model.getPassword())) {

            TreeItem rootItem = new TreeItem(String.format("%s\n[%s:%d]", model.getName(), model.getHost(), model.getPort()));
            rootItem.setGraphic(new ImageView(new Image("icons/server_pg.png")));

                TreeItem rootItem = new TreeItem(String.format("%s\n[%s:%d]", connectionModel.getName(), connectionModel.getHost(), connectionModel.getPort()));
                rootItem.setGraphic(new ImageView(new Image("icons/server_pg.png")));

            ResultSet rsdatabase = stmt.executeQuery();

            while (rsdatabase.next()) {
                String catalog = rsdatabase.getString(1);

                TreeItem db = new TreeItem(catalog);
                db.setGraphic(new ImageView(new Image("icons/database.png")));

                try (Connection cnnDb = DriverManager.getConnection(
                        model.getUrl(catalog),
                        model.getUsername(), model.getPassword())) {

                    try (Connection cnnDb = DriverManager.getConnection(connectionModel.getUrl(catalog),
                            connectionModel.getUsername(), connectionModel.getPassword())) {

                        /* Add TABLE to schema */
                        TreeItem ti = new TreeItem("Tables");
                        ti.setGraphic(new ImageView(new Image("icons/notification.png")));
                        String[] ttype = {"SYSTEM TABLE", "TABLE"};
                        ResultSet tables = cnnDb.getMetaData().getTables(catalog, schema, null, ttype);
                        while (tables.next()) {

                            String table = tables.getString(3);
                            TreeItem tti = new TreeItem(table);
                            tti.setGraphic(new ImageView(new Image("icons/table-grid.png")));

                            //Columns
                            ResultSet columns = cnnDb.getMetaData().getColumns(null, null, table, null);
                            while (columns.next()) {

                                TreeItem ci = new TreeItem(String.format("%s [%s(%s)]",
                                        columns.getString(4), columns.getString(6), columns.getString(7)));
                                ci.setGraphic(new ImageView(new Image("icons/column.png")));
                                tti.getChildren().add(ci); // Add column to tables
                            }

                            ti.getChildren().add(tti); // Add tables to table parent
                        }
                        if (!ti.getChildren().isEmpty()) // Add table parent to schema if any
                        {
                            si.getChildren().add(ti);
                        }

                        /* Add VIEW to schema */
                        TreeItem vi = new TreeItem("Views");
                        vi.setGraphic(new ImageView(new Image("icons/screen.png")));
                        String[] vtype = {"VIEW"};
                        ResultSet views = cnnDb.getMetaData().getTables(catalog, schema, null, vtype);
                        while (views.next()) {
                            String view = views.getString(3);
                            TreeItem vvi = new TreeItem(view);
                            vvi.setGraphic(new ImageView(new Image("icons/open-book.png")));

                            //Columns
                            ResultSet columns = cnnDb.getMetaData().getColumns(null, null, view, null);
                            while (columns.next()) {

                                TreeItem ci = new TreeItem(String.format("%s [%s(%s)]",
                                        columns.getString(4), columns.getString(6), columns.getString(7)));
                                ci.setGraphic(new ImageView(new Image("icons/column.png")));
                                vvi.getChildren().add(ci); // Add column to tables
                            }
                            vi.getChildren().add(vvi);
                        }
                        if (!vi.getChildren().isEmpty()) {
                            si.getChildren().add(vi);
                        }

                        if (!si.getChildren().isEmpty()) // Add schema to catalog (if any)
                        {
                            db.getChildren().add(si);
                        }
                    }
                }
                rootItem.getChildren().add(db);
            }

            treeView.setRoot(rootItem);
        } catch (SQLException ex) {
            Logger.getLogger(BrowserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isParentRoot(TreeItem item) {
        return item.getParent().getParent() == null;
    }

    private TreeItem getDatabaseTreeItem(TreeItem item) {
        /*If the root item itself is selected, sql editor button should have
        been disabled and this function never called.
         */
        if (item == null) {
            throw new IllegalStateException("This denotes a bug. Please contact developer");
        }
        if (isParentRoot(item)) {
            return item;
        } else {
            return getDatabaseTreeItem(item.getParent());
        }
    }

    @FXML
    private void onActionButtonSqlEditor(ActionEvent event) throws IOException {
        /* Sql Editor shall work only with valid connection url*/
        String database;
        // find database of selected item url
        TreeItem selectedItem = treeView.getSelectionModel().getSelectedItem();
        database = (String) getDatabaseTreeItem(selectedItem).getValue();

        SqlEditorWindow wnd = (SqlEditorWindow) fxmlControllerProducer.getFxmlDialog(FxmlUI.SQLEDITOR);
        wnd.setConnectionUrl(connectionModel.getUrl(database), connectionModel.getUsername(), connectionModel.getPassword());
        wnd.show();
    }

}
