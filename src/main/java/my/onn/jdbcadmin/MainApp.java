package my.onn.jdbcadmin;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jboss.weld.environment.se.Weld;

public class MainApp extends Application {

    private Weld weld;

    @Override
    public void stop() throws Exception {
        weld.shutdown();
    }

    @Override
    public void init() throws Exception {
        weld = new Weld();
    }

    @Override
    public void start(Stage stage) throws Exception {
        weld.initialize().select(MainSceneController.class).get().start(stage);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, SplashScreenLoader.class, args);
    }

}
