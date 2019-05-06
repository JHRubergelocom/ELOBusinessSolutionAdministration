/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.json.JSONObject;



/**
 *
 * @author ruberg
 */
public class FXMLDocumentController implements Initializable {
    
    
    
    private Profile[] profiles = null;
    
    private CommandService cmdService = null;
    private PowershellService psService = null;
    
    @FXML
    private void handleBtnShowUnittest(ActionEvent event) {
        
        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();  
        Unittests.ShowUnittestsApp(profiles[index]);
    }
    
    @FXML
    private void handleBtnMatchUnittest(ActionEvent event) {
        
        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();    
        Unittests.ShowReportMatchUnittest(profiles[index]);
    }    
    
    @FXML
    private void handleBtnGitPullAll(ActionEvent event) {

        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();    
        cmdService.SetEloCommand(profiles[index].command);
        if (cmdService.isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            cmdService.reset();
            cmdService.start();
        }        
    }

    @FXML
    private void handleEloPullUnittest(ActionEvent event) {

        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();            
        psService.SetEloCommand(profiles[index].powershell);
        psService.SetPs1(Profile.ELO_PULL_UNITTEST);
        if (psService.isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            psService.reset();
            psService.start();
        }        
    }
    
    @FXML
    private void handleEloPullPackage(ActionEvent event) {
        
        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();            
        psService.SetEloCommand(profiles[index].powershell);
        psService.SetPs1(Profile.ELO_PULL_PACKAGE);
        if (psService.isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            psService.reset();
            psService.start();
        }        
    }
    
    @FXML
    private void handleEloPrepare(ActionEvent event) {

        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();            
        psService.SetEloCommand(profiles[index].powershell);        
        psService.SetPs1(Profile.ELO_PREPARE);
        if (psService.isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            psService.reset();
            psService.start();
        }        
    }
    
    @FXML
    private ComboBox<String> cmbProfile;
    
    @FXML
    private Button btnGitPullAll;
    
    @FXML
    private Button btnEloPullUnittest;
    
    @FXML
    private Button btnEloPrepare;
    
    @FXML
    private Button btnEloPullPackage;

    @FXML
    private TextArea txtOutput;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cmdService = new CommandService(this);        
        psService = new PowershellService(this);  
        
        cmbProfile.getItems().clear();
        JSONObject jobj = JsonUtils.readJson("Profiles.json");
        JSONObject[] jarray = JsonUtils.getArray(jobj, "profiles");        
        profiles = new Profile[jarray.length];
        for (int i = 0; i < jarray.length; i++) {
            Profile p = new Profile(jobj, jarray, i);  
            cmbProfile.getItems().add(p.name);            
            profiles[i] = p;
        } 
        cmbProfile.getSelectionModel().select(0);        
    }  
    
    public void enableControls() {
        btnGitPullAll.setDisable(false);
        btnEloPullUnittest.setDisable(false);
        btnEloPrepare.setDisable(false);
        btnEloPullPackage.setDisable(false);          
    }
    
    public void disableControls() {
        btnGitPullAll.setDisable(true);        
        btnEloPullUnittest.setDisable(true);
        btnEloPrepare.setDisable(true);
        btnEloPullPackage.setDisable(true);  
    }
    
    public ComboBox<String> getCmbProfile() {
        return cmbProfile;
    }

    public Profile[] getProfiles() {
        return profiles;
    }

    public TextArea getTxtOutput() {
        return txtOutput;
    }
        
}
