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
    
    private EloService eloService = null;
    
    @FXML
    private void handleBtnShowUnittest(ActionEvent event) {
        RunEloService(EloCommand.SHOWUNITTESTSAPP, 0);
    }
    
    @FXML
    private void handleBtnMatchUnittest(ActionEvent event) {
        RunEloService(EloCommand.SHOWREPORTMATCHUNITTEST, 0);
    }    
    
    @FXML
    private void handleBtnGitPullAll(ActionEvent event) {
        RunEloService(EloCommand.CMD, Profile.GIT_PULL_ALL);
    }

    @FXML
    private void handleEloPullUnittest(ActionEvent event) {
        RunEloService(EloCommand.PS1, Profile.ELO_PULL_UNITTEST);        
    }
    
    @FXML
    private void handleEloPullPackage(ActionEvent event) {
        RunEloService(EloCommand.PS1, Profile.ELO_PULL_PACKAGE);                
    }
    
    @FXML
    private void handleEloPrepare(ActionEvent event) {
        RunEloService(EloCommand.PS1, Profile.ELO_PREPARE);                
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
        
        eloService = new EloService(this);        
        
        cmbProfile.getItems().clear();
        JSONObject jobj = JsonUtils.readJson("Profiles.json");
        JSONObject[] jarray = JsonUtils.getArray(jobj, "profiles");        
        profiles = new Profile[jarray.length];
        for (int i = 0; i < jarray.length; i++) {
            Profile p = new Profile(jobj, jarray, i);  
            cmbProfile.getItems().add(p.getName());            
            profiles[i] = p;
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

    public Profile[] getProfiles() {
        return profiles;
    }

    public TextArea getTxtOutput() {
        return txtOutput;
    }
    
    private void RunEloService(String typeCommand, int indexEloCommand) {
        int index;        
        index = cmbProfile.getSelectionModel().getSelectedIndex();  
        
        switch(typeCommand) {
        case EloCommand.CMD:
            eloService.SetEloCommand(profiles[index].getCommand(), indexEloCommand);
            break;
        case EloCommand.PS1:
            eloService.SetEloCommand(profiles[index].getPowershell(), indexEloCommand);        
            break;
        case EloCommand.SHOWREPORTMATCHUNITTEST:
            eloService.SetEloCommand(new EloCommand(EloCommand.SHOWREPORTMATCHUNITTEST),indexEloCommand);
            break;
        case EloCommand.SHOWUNITTESTSAPP:
            eloService.SetEloCommand(new EloCommand(EloCommand.SHOWUNITTESTSAPP),indexEloCommand);
            break;
        }        
        if (eloService.isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            eloService.reset();
            eloService.start();
        }   
        
    }
        
}
