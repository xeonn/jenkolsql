/*
 * Copyright (C) Jejavi Sdn Bhd - All Rights Reserved
 *  http://www.jejavi.com
 * This file is part of Jejavi Retail Manager
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package my.onn.jdbcadmin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author onn
 */
public class SplashScreenLoader extends Preloader{

    private Stage splashScreen;
    private ProgressBar progressBar;

    @Override
    public void start(Stage stage) throws Exception {
        splashScreen = stage;
        splashScreen.initStyle(StageStyle.UNDECORATED);
        splashScreen.setScene(createScene());
        splashScreen.show();
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();

        ImageView img = new ImageView("/images/jenkol-sql.png");

        progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        root.setCenter(img);
        progressBar.setMinWidth(485);
        root.setAlignment(progressBar, Pos.BOTTOM_CENTER);
        root.setBottom(progressBar);
        Scene scene = new Scene(root, 500F, 239F);
        return scene;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
//            ProgressNotification pn = (ProgressNotification) info;
//            System.out.println("XXXXXXXXXXXXXXXXXXXXX getProgress : " + pn.getProgress());
//            progressBar.setProgress(pn.getProgress());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if(info.getType() == StateChangeNotification.Type.BEFORE_LOAD) {
//            System.out.println("XXXXXXXXXXXXXXXXXXXXX BEFORE_LOAD : ");
//            progressBar.setProgress(0.2F);
        }
        if(info.getType() == StateChangeNotification.Type.BEFORE_INIT) {
//            System.out.println("XXXXXXXXXXXXXXXXXXXXX BEFORE_INIT : ");
//            progressBar.setProgress(0.3F);
        }
        if(info.getType() == StateChangeNotification.Type.BEFORE_START) {
//            System.out.println("XXXXXXXXXXXXXXXXXXXXX BEFORE_START : ");
//            progressBar.setProgress(0.4F);

/* Using scheduler executor to delay splash screen dissappearing too early
*/
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
            ses.schedule(() -> {
                Platform.runLater(() -> {
                    splashScreen.hide();
                    ses.shutdown();
                });
            }, 2, TimeUnit.SECONDS);
        }
    }

}
