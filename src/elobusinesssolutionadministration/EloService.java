/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author ruberg
 */
public class EloService extends Service<Boolean>{    
    private final FXMLDocumentController dc;
    private EloCommand ec;
    private int indexEloCommand;
    
    public EloService(FXMLDocumentController dc) {
        this.dc = dc;        
    }
    
    public void SetEloCommand(EloCommand ec, int indexEloCommand) {
        this.ec = ec;
        this.indexEloCommand = indexEloCommand;        
    } 
    
    public void Run(String typeCommand, int indexEloCommand) {
        int index;        
        index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();  
        
        switch(typeCommand) {
            case EloCommand.CMD:
                SetEloCommand(dc.getProfiles().getCommand(), indexEloCommand);
                break;
            case EloCommand.PS1:
                SetEloCommand(dc.getProfiles().getPowershell(), indexEloCommand);        
                break;
            case EloCommand.SHOWREPORTMATCHUNITTEST:
                SetEloCommand(new EloCommand(EloCommand.SHOWREPORTMATCHUNITTEST),indexEloCommand);
                break;
            case EloCommand.SHOWUNITTESTSAPP:
                SetEloCommand(new EloCommand(EloCommand.SHOWUNITTESTSAPP),indexEloCommand);
                break;
            case EloCommand.STARTADMINCONSOLE:
                SetEloCommand(new EloCommand(EloCommand.STARTADMINCONSOLE),indexEloCommand);
                break;
            case EloCommand.STARTAPPMANAGER:
                SetEloCommand(new EloCommand(EloCommand.STARTAPPMANAGER),indexEloCommand);
                break;
            case EloCommand.STARTWEBCLIENT:
                SetEloCommand(new EloCommand(EloCommand.STARTWEBCLIENT),indexEloCommand);
                break;
            case EloCommand.STARTKNOWLEDGEBOARD:
                SetEloCommand(new EloCommand(EloCommand.STARTKNOWLEDGEBOARD),indexEloCommand);
                break;
        }        
        if (isRunning()) {
            System.out.println("Already running. Nothing to do.");
        } else {
            reset();
            start();
        }   
        
    }


    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int index;  
                dc.setDisableControls(true);
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();                
                switch(ec.getType()) {
                    case EloCommand.CMD:
                        EloCommand.Execute(dc.getProfiles().getCommand().getCommand(indexEloCommand), ec, dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir());
                        break;
                    case EloCommand.PS1:
                        EloCommand.Execute(dc.getProfiles().getPowershell().getCommand(indexEloCommand), ec ,dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir() + "\\" + dc.getProfiles().getName(index) + ".git");                    
                        break;
                    case EloCommand.SHOWREPORTMATCHUNITTEST:
                        Unittests.ShowReportMatchUnittest(dc.getProfiles(), index);
                        break;
                    case EloCommand.SHOWUNITTESTSAPP:
                        Unittests.ShowUnittestsApp(dc.getProfiles(), index);
                        break;
                    case EloCommand.STARTADMINCONSOLE:
                        AdminConsole.StartAdminConsole(dc.getProfiles(), index);
                        break;
                    case EloCommand.STARTAPPMANAGER:
                        AppManager.StartAppManager(dc.getProfiles(), index);
                        break;
                    case EloCommand.STARTWEBCLIENT:
                        Webclient.StartWebclient(dc.getProfiles(), index);
                        break;
                    case EloCommand.STARTKNOWLEDGEBOARD:
                        KnowledgeBoard.ShowKnowledgeBoard(dc.getProfiles(), index);
                        break;
                }
                dc.setDisableControls(false);
                return true;
            }            
        };        
    }
    
}
