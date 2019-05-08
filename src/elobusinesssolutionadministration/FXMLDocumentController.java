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



/**
 *
 * @author ruberg
 */
public class FXMLDocumentController implements Initializable {
    
    
    
    private Profiles profiles = null;    
    private EloService eloService = null;
    
    @FXML
    private void handleBtnShowUnittest(ActionEvent event) {
        eloService.Run(EloCommand.SHOWUNITTESTSAPP, 0);
    }
    
    @FXML
    private void handleBtnMatchUnittest(ActionEvent event) {
        eloService.Run(EloCommand.SHOWREPORTMATCHUNITTEST, 0);
    }    
    
    @FXML
    private void handleBtnGitPullAll(ActionEvent event) {
        eloService.Run(EloCommand.CMD, Profiles.GIT_PULL_ALL);
    }

    @FXML
    private void handleEloPullUnittest(ActionEvent event) {
        eloService.Run(EloCommand.PS1, Profiles.ELO_PULL_UNITTEST);        
    }
    
    @FXML
    private void handleEloPullPackage(ActionEvent event) {
        eloService.Run(EloCommand.PS1, Profiles.ELO_PULL_PACKAGE);                
    }
    
    @FXML
    private void handleEloPrepare(ActionEvent event) {
        eloService.Run(EloCommand.PS1, Profiles.ELO_PREPARE);                
    }
    
    @FXML
    private ComboBox<String> cmbProfile;    
    
    @FXML
    private Button btnShowUnittest;
    
    @FXML
    private Button btnMatchUnittest;
    
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
        
        profiles = new Profiles("Profiles.json");
        eloService = new EloService(this);        
        
        cmbProfile.getItems().clear();
        for (int i = 0; i < profiles.getLength(); i++) {
            cmbProfile.getItems().add(profiles.getProfile(i).getName());            
        } 
        cmbProfile.getSelectionModel().select(0);        
    }  
    
    public void enableControls() {
        cmbProfile.setDisable(false);
        btnGitPullAll.setDisable(false);
        btnShowUnittest.setDisable(false);
        btnMatchUnittest.setDisable(false);
        btnEloPullUnittest.setDisable(false);
        btnEloPrepare.setDisable(false);
        btnEloPullPackage.setDisable(false);          
    }
    
    public void disableControls() {
        cmbProfile.setDisable(true);
        btnGitPullAll.setDisable(true);        
        btnShowUnittest.setDisable(true);
        btnMatchUnittest.setDisable(true);
        btnEloPullUnittest.setDisable(true);
        btnEloPrepare.setDisable(true);
        btnEloPullPackage.setDisable(true);  
    }
    
    public ComboBox<String> getCmbProfile() {
        return cmbProfile;
    }

    public TextArea getTxtOutput() {
        return txtOutput;
    }
    
    public Profiles getProfiles() {
        return profiles;
    }
}
