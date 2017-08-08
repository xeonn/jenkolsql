/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.sqleditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Types.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.inject.Inject;
import my.onn.jdbcadmin.MainResource;
import my.onn.jdbcadmin.ui.util.FxmlStage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class SqlEditorWindow extends FxmlStage {

    private static final Logger logger = Logger.getLogger(SqlEditorWindow.class.getName());

    private String connectionUrl;
    private String password;
    private String username;

    private String openedFile;
    private String stageTitle;

    List<ArrayList<String>> tableViewModel = new ArrayList<>();
    List<String> tableViewColumn = new ArrayList<>();
    private double connectiontime;
    private double querytime;

    private static final String[] KEYWORDS = new String[]{
        "accessible", "account", "add", "after", "aggregate", "algorithm",
        "all", "alter", "always", "analyze", "and", "any", "as", "asc",
        "authorization", "backup",
        "begin", "between", "break", "browse", "bulk", "by", "cascade", "case",
        "check", "checkpoint", "close", "clustered", "coalesce", "collate",
        "column", "commit", "compute", "constraint", "contains", "containstable",
        "continue", "convert", "create", "cross", "current", "current_date",
        "decimal", "desc", "disable", "disk", "do", "dual",
        "else", "elseif", "enable", "ends", "execute", "false", "fetch", "found",
        "from", "is", "join", "key",
        "left", "null", "numeric", "now",
        "on", "order", "right",
        "primary", "returns", "row", "rows",
        "select",
        "table", "text", "to", "trigger", "union", "when", "where", "xor"

    };
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\'([^\'\\\\]|\\\\.)*\'";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")",
             Pattern.CASE_INSENSITIVE);

    @Inject
    MainResource resources;

    private CodeArea textAreaSql;
    @FXML
    private TableView<ArrayList<String>> tableView;
    @FXML
    private StackPane tableStackPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label labelMessage;
    @FXML
    private AnchorPane anchorPaneTextEditor;

    public SqlEditorWindow() {
        textAreaSql = new CodeArea();
    }

    /**
     * Initializes the controller class.
     */
    public void initialize() {

        anchorPaneTextEditor.getChildren().add(textAreaSql);
        textAreaSql.prefWidthProperty().bind(anchorPaneTextEditor.widthProperty());
        textAreaSql.prefHeightProperty().bind(anchorPaneTextEditor.heightProperty());
        textAreaSql.setOnKeyReleased(ev -> onTextAreaSqlKeyReleased(ev));

        textAreaSql.setParagraphGraphicFactory(LineNumberFactory.get(textAreaSql));
        textAreaSql.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    textAreaSql.setStyleSpans(0, computeHighlighting(textAreaSql.getText()));
                });
    }

    private void executeSql(String sql) {
        connectiontime = 0;
        querytime = 0;
        tableViewColumn.clear();
        tableViewModel.clear();
        int columnCount = -1;
        long start_time = System.nanoTime();
        long end_connection_time = 0;

        try (Connection cnn = DriverManager.getConnection(this.connectionUrl, this.username, this.password);
                Statement stmt = cnn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            end_connection_time = System.nanoTime();

            ResultSet result = stmt.executeQuery(sql);
            ResultSetMetaData rsm = result.getMetaData();
            columnCount = rsm.getColumnCount();

            for (int n = 0; n < columnCount; n++) {
                String typename = rsm.getColumnTypeName(n + 1);
                switch (rsm.getColumnType(n + 1)) {
                    case DECIMAL:
                    case NUMERIC:
                        typename = typename + String.format("(%d,%d)",
                                rsm.getPrecision(n + 1),
                                rsm.getScale(n + 1));
                        break;
                    case VARCHAR:
                    case VARBINARY:
                        typename = typename + String.format("(%s)", rsm.getPrecision(n + 1));
                        break;
                }
                tableViewColumn.add(
                        String.format("%s\n%s",
                                rsm.getColumnLabel(n + 1),
                                typename));
            }

            //TODO: Buffer all record using some format, xml or json as mapped memory file
            int count = 0;
            while (result.next() /*&& count < 100*/) {
                ArrayList<String> rows = new ArrayList<>();

                for (int n = 0; n < columnCount; n++) {
                    rows.add(result.getString(n + 1));
                }
                tableViewModel.add(rows);
                count++;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            Platform.runLater(() -> {
                tabPane.getSelectionModel().select(1);
                labelMessage.setText(ex.getLocalizedMessage());
            });
        } finally {
            long end_query_time = System.nanoTime();
            this.connectiontime = (end_connection_time - start_time) / 1e6;
            this.querytime = (end_query_time - end_connection_time) / 1e6;
        }
    }

    private void updateTableView() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        IntStream.range(0, tableViewColumn.size())
                .forEach(idx -> {
                    TableColumn<ArrayList<String>, String> col = new TableColumn<>(tableViewColumn.get(idx));
                    //int i = n;
                    col.setCellValueFactory(c -> {
                        return new SimpleStringProperty(c.getValue().get(idx));
                    });
                    col.setPrefWidth(120);
                    tableView.getColumns().add(col);
                });

        tableViewModel.stream().forEach(row
                -> tableView.getItems().add(row));
        if (tableViewModel.size() > 0) {
            tabPane.getSelectionModel().select(0);
        } else {
            tabPane.getSelectionModel().select(1);
        }
    }

    @FXML
    private void onButtonRun(ActionEvent event) {
        /*
        Execute selected text or fallback to all.
         */

        String strSql = textAreaSql.selectedTextProperty().getValue();

        if (strSql.isEmpty()) {
            strSql = textAreaSql.getText();
        }

        if (strSql.isEmpty()) {
            return;
        }

        //activate progress indicator
        ProgressIndicator pi = new ProgressIndicator();
        HBox hbox = new HBox(pi);
        hbox.setAlignment(Pos.CENTER);
        tableStackPane.getChildren().add(hbox);

        String sql = strSql;
        CompletableFuture.runAsync(() -> {
            executeSql(sql);
        }).thenRun(() -> {
            Platform.runLater(() -> {
                tableStackPane.getChildren().remove(hbox);
                if (tableViewModel.size() > 0) {
                    labelMessage.setText(String.format(
                            "Connection established: %.1f ms\n"
                            + "Total query runtime: %.1f ms\n"
                            + "%d rows retrieved.",
                            connectiontime, querytime, tableViewModel.size()));
                }
                updateTableView();
            });
        });

    }

    @FXML
    private void onActionSave(ActionEvent event) {
        // Save to sql
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as ...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));

        File oFile = null;

        if (openedFile != null && !openedFile.isEmpty()) {
            oFile = new File(openedFile);
        }

        if (oFile != null) {
            fileChooser.setInitialDirectory(oFile.getParentFile());
            fileChooser.setInitialFileName(oFile.getName());
        } else {
            fileChooser.setInitialFileName("New File");
        }
        File file = fileChooser.showSaveDialog(this);

        if (file != null) {
            try {
                try (OutputStream os = new FileOutputStream(file);
                        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    osw.write(textAreaSql.getText());
                    osw.flush();
                    openedFile = file.getAbsolutePath();
                    setTitle(stageTitle + " - [" + file.getName() + "]");
                }
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onActionOpenFile(ActionEvent event) {
        // Save to sql
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SQL file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = fileChooser.showOpenDialog(this);

        if (file != null) {
            try {
                InputStream is = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(is);
                int ch = -1;
                StringBuilder sb = new StringBuilder();
                while ((ch = isr.read()) > 0) {
                    sb.append((char) ch);
                }
                textAreaSql.replaceText(sb.toString());
                openedFile = file.getAbsolutePath();
                setTitle(stageTitle + " - [" + file.getName() + "]");
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setConnectionUrl(String url, String username, String password) {
        if (url.isEmpty() || username.isEmpty()) {
            throw new IllegalArgumentException("Database connection not available");
        }

        stageTitle = String.format("%s - [%s]", resources.getString("sqleditor.title"), url);
        this.setTitle(stageTitle);

        this.connectionUrl = url;
        this.username = username;
        this.password = password;
    }

    private void onTextAreaSqlKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.F5) {
            onButtonRun(null);
        }
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : null;
            /* never happens */ assert styleClass != null;
            logger.info("computeHighlighting " + matcher.group("KEYWORD"));
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
