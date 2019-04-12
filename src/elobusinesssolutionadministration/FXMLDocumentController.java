/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.json.JSONObject;



/**
 *
 * @author ruberg
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
     @FXML
    private ComboBox<String> cmbProfile;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbProfile.getItems().clear();
        cmbProfile.getItems().add("Profil1");
        cmbProfile.getItems().add("Profil2");
        cmbProfile.getItems().add("Profil3");
        
        cmbProfile.getSelectionModel().select(0);
        
        // Read Unittest.json
        // JsonUtils.ReadJson("C:\\Users\\Ruberg\\Documents\\NetBeansProjects\\ELOBusinessSolutionAdministration\\src\\elobusinesssolutionadministration\\Unittest.json");
        // JsonUtils.ReadJson("E:\\GitDevelopment\\Netbeans\\test.json");
        
        String jsonString = "{ \"name\": \"Arnold Willemer\", \"alter\": \"12\", \"ort\": \"Norgaardholz\" }"; 
        JSONObject jobj = new JSONObject(jsonString);
        String name     = jobj.getString("name");
        String ort     = jobj.getString("ort");
        String alter     = jobj.getString("alter");
        // int zahl        = jobj.getInt("zahl");
        // double gehalt   = jobj.getDouble("gehalt");
        // boolean istklug = jobj.getBoolean("istklug");
        
    }    
    
}
