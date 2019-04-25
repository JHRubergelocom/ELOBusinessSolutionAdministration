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
    
    private FXMLDocumentController dc;
    
    public CommandService(FXMLDocumentController dc) {
        this.dc = dc;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int index;  
                dc.disableControls();
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();
                Command.Execute(dc.getProfiles()[index].command, dc.getTxtOutput());
                dc.enableControls();  
                return true;
            }            
        };
    }

    
}
