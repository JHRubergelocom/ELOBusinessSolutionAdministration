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
public class RegisterFunctions {    
    public static SortedMap<String, Boolean> GetRFs(IXConnection ixConn, List<String> jsTexts, String eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage + "/IndexServer Scripting Base";
        if (eloPackage.equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/IndexServer Scripting Base/_ALL/business_solutions";
        }
        List<Sord> sordRFInfo = RepoUtils.FindChildren(parentId, ixConn, true);
        SortedMap<String, Boolean> dicRFs = new TreeMap<>();
        sordRFInfo.stream().map((s) -> RepoUtils.DownloadDocumentToString(s, ixConn)).filter((jsText) -> (jsText.length() > 0 )).map((jsText) -> jsText.replaceAll("\b", "")).map((jsText) -> jsText.replaceAll("\n", " ")).map((jsText) -> jsText.split(" ")).forEachOrdered((String[] jsLines) -> {
            for (String line : jsLines) {
                if (line.contains("RF_")) {
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
                            boolean match = Unittests.Match(ixConn, rfName, eloPackage, jsTexts);
                            dicRFs.put(rfName, match);
                        }
                    }
                }                    
            }
        });        
        return dicRFs;
    }
    
}
