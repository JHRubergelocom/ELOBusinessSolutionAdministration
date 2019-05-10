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
    private String typeCommand;
    
    public EloService(FXMLDocumentController dc) {
        this.dc = dc;        
    }
    
    public void SetTypeCommand(String typeCommand) {
        this.typeCommand = typeCommand;
    } 
    
    public void Run(String typeCommand) {  
        SetTypeCommand(typeCommand);
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
                switch(typeCommand) {
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
                    case EloCommand.GITPULLALL:
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getDevDir());
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir());
                        break;
                    default:
                        EloCommand ec = dc.getProfiles().getProfile(index).getEloCommand(typeCommand);
                        ec.Execute(dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir() + "\\" + dc.getProfiles().getName(index)+ ".git", dc.getProfiles(), index);                        
                        break;
                }
                dc.setDisableControls(false);
                return true;
            }            
        };        
    }
    
}
