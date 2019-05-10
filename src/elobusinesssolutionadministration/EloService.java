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
    private EloCommandOld ec;
    private int indexEloCommand;
    
    public EloService(FXMLDocumentController dc) {
        this.dc = dc;        
    }
    
    public void SetEloCommand(EloCommandOld ec, int indexEloCommand) {
        this.ec = ec;
        this.indexEloCommand = indexEloCommand;        
    } 
    
    public void Run(String typeCommand, int indexEloCommand) {        
        switch(typeCommand) {
            case EloCommandOld.SHOWREPORTMATCHUNITTEST:
                SetEloCommand(new EloCommandOld(EloCommandOld.SHOWREPORTMATCHUNITTEST),indexEloCommand);
                break;
            case EloCommandOld.SHOWUNITTESTSAPP:
                SetEloCommand(new EloCommandOld(EloCommandOld.SHOWUNITTESTSAPP),indexEloCommand);
                break;
            case EloCommandOld.STARTADMINCONSOLE:
                SetEloCommand(new EloCommandOld(EloCommandOld.STARTADMINCONSOLE),indexEloCommand);
                break;
            case EloCommandOld.STARTAPPMANAGER:
                SetEloCommand(new EloCommandOld(EloCommandOld.STARTAPPMANAGER),indexEloCommand);
                break;
            case EloCommandOld.STARTWEBCLIENT:
                SetEloCommand(new EloCommandOld(EloCommandOld.STARTWEBCLIENT),indexEloCommand);
                break;
            case EloCommandOld.STARTKNOWLEDGEBOARD:
                SetEloCommand(new EloCommandOld(EloCommandOld.STARTKNOWLEDGEBOARD),indexEloCommand);
                break;
            case EloCommandOld.GITPULLALL:
                SetEloCommand(new EloCommandOld(EloCommandOld.GITPULLALL),indexEloCommand);
                break;
            default:
                SetEloCommand(dc.getProfiles().getEloCommand(), indexEloCommand);        
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
                    case EloCommandOld.SHOWREPORTMATCHUNITTEST:
                        Unittests.ShowReportMatchUnittest(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.SHOWUNITTESTSAPP:
                        Unittests.ShowUnittestsApp(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.STARTADMINCONSOLE:
                        AdminConsole.StartAdminConsole(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.STARTAPPMANAGER:
                        AppManager.StartAppManager(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.STARTWEBCLIENT:
                        Webclient.StartWebclient(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.STARTKNOWLEDGEBOARD:
                        KnowledgeBoard.ShowKnowledgeBoard(dc.getProfiles(), index);
                        break;
                    case EloCommandOld.GITPULLALL:
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getDevDir());
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir());
                        break;
                    default:
                        EloCommandOld.Execute(dc.getProfiles().getEloCommand().getCommand(indexEloCommand), ec ,dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir() + "\\" + dc.getProfiles().getName(index) + ".git");                    
                        break;
                }
                dc.setDisableControls(false);
                return true;
            }            
        };        
    }
    
}
