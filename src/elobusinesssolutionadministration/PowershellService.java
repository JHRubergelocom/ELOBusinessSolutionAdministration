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
public class PowershellService extends Service <Boolean>{
    
    private FXMLDocumentController dc;
    private int indexPs1;
    
    public PowershellService(FXMLDocumentController dc) {
        this.dc = dc;        
    }
    
    public void SetPs1(int indexPs1) {
        this.indexPs1 = indexPs1;        
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int index;        
                dc.disableControls();
                index = dc.getCmbProfile().getSelectionModel().getSelectedIndex();
                PowerShell.Execute(dc.getProfiles()[index].powerShell.ps1[indexPs1], dc.getProfiles()[index].psWorkingDir, dc.getTxtOutput());
                dc.enableControls();
                return true;
            }            
        };
    }
    
}
