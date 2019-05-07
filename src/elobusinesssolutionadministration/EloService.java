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
            SetEloCommand(dc.getProfiles()[index].getCommand(), indexEloCommand);
            break;
        case EloCommand.PS1:
            SetEloCommand(dc.getProfiles()[index].getPowershell(), indexEloCommand);        
            break;
        case EloCommand.SHOWREPORTMATCHUNITTEST:
            SetEloCommand(new EloCommand(EloCommand.SHOWREPORTMATCHUNITTEST),indexEloCommand);
            break;
        case EloCommand.SHOWUNITTESTSAPP:
            SetEloCommand(new EloCommand(EloCommand.SHOWUNITTESTSAPP),indexEloCommand);
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
                dc.disableControls();
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();                
                switch(ec.getType()) {
                case EloCommand.CMD:
                    EloCommand.Execute(dc.getProfiles()[index].getCommand().getCommand(indexEloCommand), ec ,dc.getTxtOutput(), dc.getProfiles()[index].getGitSolutionsDir());
                    break;
                case EloCommand.PS1:
                    EloCommand.Execute(dc.getProfiles()[index].getPowershell().getCommand(indexEloCommand), ec ,dc.getTxtOutput(), dc.getProfiles()[index].getGitSolutionsDir() + "\\" + dc.getProfiles()[index].getName() + ".git");                    
                    break;
                case EloCommand.SHOWREPORTMATCHUNITTEST:
                    Unittests.ShowReportMatchUnittest(dc.getProfiles()[index]);
                    break;
                case EloCommand.SHOWUNITTESTSAPP:
                    Unittests.ShowUnittestsApp(dc.getProfiles()[index]);
                    break;
                }
                dc.enableControls();  
                return true;
            }            
        };        
    }
    
}
