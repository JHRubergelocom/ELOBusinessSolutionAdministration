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
import javafx.scene.control.TextField;



/**
 *
 * @author ruberg
 */
public class FXMLDocumentController implements Initializable {
    private Profiles profiles = new Profiles();    
    private EloService eloService = new EloService();
    private EloProperties eloProperties = new EloProperties();
    
    @FXML
    private void handleBtnShowUnittest(ActionEvent event) {
        eloService.Run(EloCommand.SHOWUNITTESTSAPP);
    }
    
    @FXML
    private void handleBtnMatchUnittest(ActionEvent event) {
        eloService.Run(EloCommand.SHOWREPORTMATCHUNITTEST);
    }    
    
    @FXML
    private void handleBtnGitPullAll(ActionEvent event) {
        eloService.Run(EloCommand.GITPULLALL);
    }

    @FXML
    private void handleEloPullUnittest(ActionEvent event) {
        eloService.Run(EloCommand.ELO_PULL_UNITTEST);
    }
    
    @FXML
    private void handleEloPullPackage(ActionEvent event) {
        eloService.Run(EloCommand.ELO_PULL_PACKAGE);                
    }
    
    @FXML
    private void handleEloPrepare(ActionEvent event) {
        eloService.Run(EloCommand.ELO_PREPARE);                
    }

    @FXML
    private void handleBtnAdminConsole(ActionEvent event) {
        eloService.Run(EloCommand.STARTADMINCONSOLE);
    }

    @FXML
    private void handleBtnLoadEloScripte(ActionEvent event) {
        eloService.Run(EloCommand.STARTEXPORTELO);
    }

    @FXML
    private void handleBtnEloApplicationServer(ActionEvent event) {
        eloService.Run(EloCommand.SHOWELOAPPLICATIONSERVER);
    }

    @FXML
    private void handleBtnRancher(ActionEvent event) {
        eloService.Run(EloCommand.SHOWRANCHER);
    }

    @FXML
    private void handleBtnAppManager(ActionEvent event) {
        eloService.Run(EloCommand.STARTAPPMANAGER);
    }

    @FXML
    private void handleBtnWebclient(ActionEvent event) {
        eloService.Run(EloCommand.STARTWEBCLIENT);
    }

    @FXML
    private void handleBtnKnowledgeBoard(ActionEvent event) {
        eloService.Run(EloCommand.STARTKNOWLEDGEBOARD);
    }

    @FXML
    private void handleBtnSearch(ActionEvent event) {
        eloProperties.setPattern(txtPattern.getText());
        eloService.Run(EloCommand.SHOWSEARCHRESULT);
    }    
    
    @FXML
    private void handleCmbProfile(ActionEvent event) {
        int index = cmbProfile.getSelectionModel().getSelectedIndex();
        Profile profile = profiles.getProfile(index);
        eloProperties.setSelectedProfile(profile);
        System.out.println(event.getEventType() + "Profil: " + profile.getName());
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
    private Button btnAppManager;

    @FXML
    private Button btnWebclient;

    @FXML
    private Button btnKnowledgeBoard;

    @FXML
    private Button btnAdminConsole;

    @FXML
    private Button btnLoadEloScripte;

    @FXML
    private Button btnEloApplicationServer;

    @FXML
    private Button btnRancher;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField txtPattern;

    @FXML
    private TextArea txtOutput;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        profiles = new Profiles("Profiles.json");
        eloService = new EloService(this);   
        eloProperties = new EloProperties();
        
        cmbProfile.getItems().clear();
        for (int i = 0; i < profiles.getLength(); i++) {
            Profile p = profiles.getProfile(i);
            cmbProfile.getItems().add(p.getName());            
        } 
        
        String selectedProfileName = eloProperties.getSelectedProfile();
        if (selectedProfileName != null) {
            cmbProfile.getSelectionModel().select(selectedProfileName);            
        } else {
            cmbProfile.getSelectionModel().select(0);                    
        }
        txtPattern.setText(eloProperties.getPattern());
        
    }  
        
    public void setDisableControls(boolean value) {
        cmbProfile.setDisable(value);
        btnGitPullAll.setDisable(value);        
        btnShowUnittest.setDisable(value);
        btnMatchUnittest.setDisable(value);
        btnEloPullUnittest.setDisable(value);
        btnEloPrepare.setDisable(value);
        btnEloPullPackage.setDisable(value);  
        btnAppManager.setDisable(value);
        btnWebclient.setDisable(value);
        btnKnowledgeBoard.setDisable(value);
        btnAdminConsole.setDisable(value);        
        btnLoadEloScripte.setDisable(value);        
        btnEloApplicationServer.setDisable(value);        
        btnRancher.setDisable(value);        
        btnSearch.setDisable(value);        
        txtPattern.setDisable(value);        
    }
    
    public ComboBox<String> getCmbProfile() {
        return cmbProfile;
    }

    public TextArea getTxtOutput() {
        return txtOutput;
    }
    
    public TextField getTxtPattern() {
        return txtPattern;
    }
    
    public Profiles getProfiles() {
        return profiles;
    }
}
