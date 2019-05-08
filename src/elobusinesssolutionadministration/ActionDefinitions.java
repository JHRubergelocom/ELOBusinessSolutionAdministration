/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ruberg
 */
class ActionDefinitions {
    static SortedMap<String, Boolean> GetActionDefs(IXConnection ixConn, List<String> jsTexts, String eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage + "/Action definitions";
        if (eloPackage.equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Action definitions";
        }
        List<Sord> sordActionDefInfo = RepoUtils.FindChildren(parentId, ixConn, true);
        SortedMap <String, Boolean> dicActionDefs = new TreeMap<>();
        sordActionDefInfo.stream().map((s) -> s.getName()).map((actionDef) -> {
            String[] rf = actionDef.split("\\.");            
            actionDef = rf[rf.length - 1];
            return actionDef;
        }).map((actionDef) -> "actions." + actionDef).filter((actionDef) -> (!dicActionDefs.containsKey(actionDef))).forEachOrdered((actionDef) -> {
            boolean match = Unittests.Match(ixConn, actionDef, eloPackage, jsTexts);
            dicActionDefs.put(actionDef, match);
        });
        return dicActionDefs;
    }
    
}
