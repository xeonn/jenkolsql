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
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.inject.Inject;
import my.onn.jdbcadmin.browser.sqleditor.SqlEditorWindow;
import my.onn.jdbcadmin.connection.ConnectionModel;
import my.onn.jdbcadmin.ui.util.FxmlControllerProducer;
import my.onn.jdbcadmin.ui.util.FxmlStage;
import my.onn.jdbcadmin.ui.util.FxmlUI;
import my.onn.jdbcadmin.ui.util.IconsEnum;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class BrowserController extends FxmlStage {

    private static final Logger logger = Logger.getLogger(BrowserController.class.getName());

    /**
     * Model representation for TreeView component.
     *
     * Mostly modification of item will be done at the model object. The
     * treeViewResult component are refreshed with this model only.
     */
    BrowserItem model = new BrowserItem("empty", IconsEnum.UNKNOWN);

    ConnectionModel connectionModel;

    @Inject
    FxmlControllerProducer fxmlControllerProducer;

    @FXML
    private Button buttonRefresh;
    @FXML
    private TreeView<BrowserItem> treeView;
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

    private Stage startProgressDialog() {
        ProgressBar pb = new ProgressBar();
        Label label = new Label("Connecting to database, please wait ...");
        VBox vbox = new VBox(pb, label);
        Scene scene = new Scene(vbox);
        Stage dialog = new Stage(StageStyle.UNDECORATED);

        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10.0));
        vbox.setSpacing(10.0);

        dialog.initOwner(this);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.centerOnScreen();
        dialog.setAlwaysOnTop(true);
        dialog.setScene(scene);
        return dialog;
    }

    private CompletableFuture fetchModel() {
        return CompletableFuture.runAsync(() -> {
            try (Connection cnn = DriverManager.getConnection(connectionModel.getMaintenanceUrl(), connectionModel.getUsername(), connectionModel.getPassword())) {

                model = new BrowserItem(
                        String.format("%s\n[%s:%d]",
                                connectionModel.getName(),
                                connectionModel.getHost(),
                                connectionModel.getPort()),
                        IconsEnum.SERVER);

                // Database
                PreparedStatement stmt = cnn.prepareStatement(
                        "SELECT datname FROM pg_database WHERE datistemplate=false;",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

                ResultSet rsdatabase = stmt.executeQuery();

                while (rsdatabase.next()) {
                    String catalog = rsdatabase.getString(1);

                    BrowserItem db = new BrowserItem(catalog, IconsEnum.DATABASE);

                    try (Connection cnnDb = DriverManager.getConnection(connectionModel.getUrl(catalog),
                            connectionModel.getUsername(), connectionModel.getPassword())) {

                        ResultSet schemas = cnnDb.getMetaData().getSchemas(catalog, null);
                        while (schemas.next()) {
                            String schema = schemas.getString(1);
                            BrowserItem si = new BrowserItem(schema, IconsEnum.SCHEMA);

                            /* Add TABLE to schema */
                            BrowserItem ti = new BrowserItem("Tables", IconsEnum.NOTIFICATION);

                            String[] ttype = {"SYSTEM TABLE", "TABLE"};
                            ResultSet tables = cnnDb.getMetaData().getTables(catalog, schema, null, ttype);
                            while (tables.next()) {

                                String table = tables.getString(3);
                                BrowserItem tti = new BrowserItem(table, IconsEnum.TABLEGRID);

                                //Columns
                                ResultSet columns = cnnDb.getMetaData().getColumns(null, null, table, null);
                                while (columns.next()) {

                                    BrowserItem ci = new BrowserItem(String.format("%s [%s(%s)]",
                                            columns.getString(4), columns.getString(6), columns.getString(7)),
                                            IconsEnum.COLUMN);

                                    tti.getChildren().add(ci); // Add column to tables
                                }

                                ti.getChildren().add(tti); // Add tables to table parent
                            }
                            if (!ti.getChildren().isEmpty()) // Add table parent to schema if any
                            {
                                si.getChildren().add(ti);
                            }

                            /* Add VIEW to schema */
                            BrowserItem vi = new BrowserItem("Views", IconsEnum.SCREEN);
                            String[] vtype = {"VIEW"};
                            ResultSet views = cnnDb.getMetaData().getTables(catalog, schema, null, vtype);
                            while (views.next()) {
                                String view = views.getString(3);
                                BrowserItem vvi = new BrowserItem(view, IconsEnum.OPENBOOK);

                                //Columns
                                ResultSet columns = cnnDb.getMetaData().getColumns(null, null, view, null);
                                while (columns.next()) {

                                    BrowserItem ci = new BrowserItem(String.format("%s [%s(%s)]",
                                            columns.getString(4), columns.getString(6), columns.getString(7)),
                                            IconsEnum.COLUMN);
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
                    model.getChildren().add(db);
                }

            } catch (SQLException ex) {
                Logger.getLogger(BrowserController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void refreshTree() throws IOException {
        Stage dialog = startProgressDialog();

        fetchModel().thenRun(() -> Platform.runLater(() -> {
            // Fill up TreeView children from model
            TreeItem rootItem = new TreeItem<>(model);
            rootItem.setGraphic(new ImageView(model.getIcon().getImage()));
            treeView.setRoot(rootItem);
            addTreeItemRecursive(model, rootItem);
            treeView.refresh();
            dialog.close();
        }));

        dialog.show();
    }

    private void addTreeItemRecursive(BrowserItem browserItem, TreeItem<BrowserItem> treeItem) {
        for (BrowserItem sub : browserItem.getChildren()) {
            TreeItem<BrowserItem> subItem = new TreeItem<>(sub);
            subItem.setGraphic(new ImageView(sub.getIcon().getImage()));
            treeItem.getChildren().add(subItem);

            addTreeItemRecursive(sub, subItem);
        }
    }

    /**
     * UI handling element
     *
     * @param item
     * @return
     */
    private boolean isParentRoot(TreeItem item) {
        return item.getParent().getParent() == null;
    }

    /**
     * UI handling element
     *
     * @param item
     * @return
     */
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
