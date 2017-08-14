/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.onn.jdbcadmin.settings;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import my.onn.jdbcadmin.ui.util.FxmlStage;

/**
 * FXML Controller class
 *
 * @author onn
 */
public class AboutDialog extends FxmlStage {

    @FXML
    private ImageView imageView;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        imageView.setImage(new Image("/images/jenkol-sql.png"));
    }

}
