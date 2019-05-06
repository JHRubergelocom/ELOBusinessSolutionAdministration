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
public class CommandService extends Service<Boolean> {
    
    private final FXMLDocumentController dc;
    private EloCommand ec;
    
    public CommandService(FXMLDocumentController dc) {
        this.dc = dc;        
    }
    
    public void SetEloCommand(EloCommand ec) {
        this.ec = ec;
    } 

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int index;  
                dc.disableControls();
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();                
                if (ec.getType().contentEquals(EloCommand.CMD)) {
                    EloCommand.Execute(dc.getProfiles()[index].command.getCommand(0), ec ,dc.getTxtOutput(), dc.getProfiles()[index].gitSolutionsDir);
                }                
                dc.enableControls();  
                return true;
            }            
        };
    }

    
}
