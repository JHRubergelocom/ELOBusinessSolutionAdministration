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
public class RegisterFunctions {  
    private final IXConnection ixConn;

    RegisterFunctions(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    public SortedMap<String, Boolean> GetRFs(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/IndexServer Scripting Base";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/IndexServer Scripting Base/_ALL/business_solutions";
        }
        RepoUtils rU = new RepoUtils(ixConn);  
        Sord[] sordRFInfo = rU.FindChildren(parentId, true);
        SortedMap<String, Boolean> dicRFs = new TreeMap<>();
        
        for(Sord s : sordRFInfo) {  
           String jsText = rU.DownloadDocumentToString (s);  
           jsText = jsText.replaceAll("\b", "");
           jsText = jsText.replaceAll("\n", " ");
           String[] jsLines = jsText.split(" ");
            for (String line : jsLines) {
                if (line.contains("RF_")) {
                    if (eloPackage.getName().equals("") || (!eloPackage.getName().equals("") && line.contains(eloPackage.getName()))) {
                        String rfName = line;
                        String[] rfNames = rfName.split("\\(");
                        rfName = rfNames[0];
                        if (!line.endsWith(",")
                                && rfName.startsWith("RF_") && !line.contains("RF_ServiceBaseName") && !line.endsWith(".")
                                && !line.contains("RF_FunctionName") && !line.contains("RF_MyFunction")
                                && !line.contains("RF_custom_functions_MyFunction") && !line.contains("RF_custom_services_MyFunction")
                                && !line.contains("RF_sol_function_FeedComment}.") && !line.contains("RF_sol_my_actions_MyAction")
                                && !line.contains("RF_sol_service_ScriptVersionReportCreate")) {
                            if (!dicRFs.containsKey(rfName)) {
                                Unittests uT = new Unittests(ixConn); 
                                boolean match = uT.Match(rfName, eloPackage, jsTexts);
                                dicRFs.put(rfName, match);
                            }
                        }                        
                    }
                }                    
            }
           
        }
                
        return dicRFs;
    }
    
}
