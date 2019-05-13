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
    static SortedMap<String, Boolean> GetActionDefs(IXConnection ixConn, String[] jsTexts, String eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage + "/Action definitions";
        if (eloPackage.equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Action definitions";
        }
        Sord[] sordActionDefInfo = RepoUtils.FindChildren(parentId, ixConn, true);
        SortedMap <String, Boolean> dicActionDefs = new TreeMap<>();
        
        for(Sord s : sordActionDefInfo) {
            String actionDef = s.getName();
            String[] rf = actionDef.split("\\.");
            actionDef = rf[rf.length-1];
            actionDef = "actions." + actionDef;
            if (!dicActionDefs.containsKey(actionDef)) {
                boolean match = Unittests.Match(ixConn, actionDef, eloPackage, jsTexts);
                dicActionDefs.put(actionDef, match);
            }
        }
        return dicActionDefs;
    }
    
}
