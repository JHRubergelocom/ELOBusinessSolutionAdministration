/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

/**
 *
 * @author ruberg
 */
public class EloService extends Service<Boolean> {    
    private FXMLDocumentController dc;
    private String typeCommand;
    
    public EloService(FXMLDocumentController dc) {
        this.dc = dc;        
    }

    public EloService() {
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
            protected Boolean call() {
                EloCommand ec;
                int index;  
                dc.setDisableControls(true);
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();    
                Profiles profiles = dc.getProfiles();
                TextArea txtOutput = dc.getTxtOutput();
                String searchPattern = dc.getTxtPattern().getText();
                String gitUser = profiles.getGitUser();
                String user = profiles.getUser();
                String pwd = profiles.getPwd();
                String gitSolutionsDir = profiles.getGitSolutionsDir();
                Profile profile = profiles.getProfile(index);                
                String workingDir = profile.getWorkingDir(gitSolutionsDir);
                String name = profile.getName();
                EloPackage[] eloPackages = profile.getEloPackages();
                
                switch(typeCommand) {
                    case EloCommand.SHOWRANCHER:
                        Rancher.ShowRancher(profiles);
                        break;
                    case EloCommand.GITPULLALL:
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getDevDir());
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir());
                        break;
                    case EloCommand.ELO_PREPARE:
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getDevDir());
                        GitPullAll.Execute(dc.getTxtOutput(), dc.getProfiles().getGitSolutionsDir()); 
                        ec = dc.getProfiles().getProfile(index).getEloCommand(typeCommand);
                        ec.Execute(txtOutput, profile, workingDir, gitUser);                        
                        break;                        
                    default:
                        break;
                }

                if ((!typeCommand.equals(EloCommand.SHOWRANCHER)) && 
                    (!typeCommand.equals(EloCommand.GITPULLALL)) && 
                    (!typeCommand.equals(EloCommand.ELO_PREPARE))) {
                    IXConnection ixConn;
                    try {
                        ixConn = Connection.getIxConnection(profile, gitUser, user, pwd);
                        switch(typeCommand) {
                            case EloCommand.SHOWREPORTMATCHUNITTEST:
                                Unittests.ShowReportMatchUnittest(ixConn, eloPackages);
                                break;
                            case EloCommand.SHOWUNITTESTSAPP:
                                Unittests.ShowUnittestsApp(ixConn);
                                break;
                            case EloCommand.STARTADMINCONSOLE:
                                AdminConsole.StartAdminConsole(ixConn);
                                break;
                            case EloCommand.STARTAPPMANAGER:
                                AppManager.StartAppManager(ixConn);
                                break;
                            case EloCommand.STARTWEBCLIENT:
                                Webclient.StartWebclient(ixConn);
                                break;
                            case EloCommand.STARTKNOWLEDGEBOARD:
                                KnowledgeBoard.ShowKnowledgeBoard(ixConn);
                                break;
                            case EloCommand.STARTEXPORTELO:
                                ExportElo.StartExportElo(ixConn, name);
                                break;
                            case EloCommand.SHOWELOAPPLICATIONSERVER:
                                EloApplicationServer.ShowEloApplicationServer(ixConn);
                                break;
                            case EloCommand.SHOWSEARCHRESULT:
                                Search.ShowSearchResult(ixConn, searchPattern, eloPackages);
                                break;    
                            case EloCommand.ELO_PULL_PACKAGE:    
                            case EloCommand.ELO_PULL_UNITTEST:
                                ec = dc.getProfiles().getProfile(index).getEloCommand(typeCommand);
                                ec.Execute(txtOutput, profile, workingDir, gitUser);                        
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                }
                
                dc.setDisableControls(false);
                return true;
            }            
        };        
    }
    
}
