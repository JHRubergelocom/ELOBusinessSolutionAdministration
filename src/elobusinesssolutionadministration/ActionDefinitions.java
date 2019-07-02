/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ruberg
 */
class ActionDefinitions {
    private final IXConnection ixConn;

    ActionDefinitions(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    SortedMap<String, Boolean> GetActionDefs(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/Action definitions";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Action definitions";
        }

        RepoUtils rU = new RepoUtils(ixConn);                
        Sord[] sordActionDefInfo = rU.FindChildren(parentId, true);
        SortedMap <String, Boolean> dicActionDefs = new TreeMap<>();
        
        for(Sord s : sordActionDefInfo) {
            String actionDef = s.getName();
            String[] rf = actionDef.split("\\.");
            actionDef = rf[rf.length-1];
            actionDef = "actions." + actionDef;
            if (!dicActionDefs.containsKey(actionDef)) {
                Unittests uT = new Unittests(ixConn);   
                boolean match = uT.Match(actionDef, eloPackage, jsTexts);
                dicActionDefs.put(actionDef, match);
            }
        }
        return dicActionDefs;
    }
    
}
