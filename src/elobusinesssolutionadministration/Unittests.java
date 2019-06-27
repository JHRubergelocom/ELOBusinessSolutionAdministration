/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Unittests {    
    static Map<String, String> GetUnittestApp(IXConnection ixConn) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        Sord[] sordELOappsClientInfo = RepoUtils.FindChildren(parentId, ixConn, false);
        String configApp = "";
        String configId = "";
        String jsonString;
        
        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = RepoUtils.DownloadDocumentToString(s, ixConn);
            jsonString = jsonString.replaceAll("namespace", "namespace1");
            JSONObject config = new JSONObject(jsonString);    
            JSONObject web = config.getJSONObject("web");                 
            String webId = web.getString("id");
            if (webId != null)
            {
                if (webId.contains("UnitTests"))
                {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = config.getString("id");
                }
            }
         }
         dicApp.put("configApp", configApp);
         dicApp.put("configId", configId);
            
        return dicApp;
    }    

    static void ShowUnittestsApp(IXConnection ixConn) { 
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();            
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetUnittestApp(ixConn);
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);              
    }

    private static String CreateReportMatchUnittest(SortedMap<String, Boolean> dicRFs, SortedMap<String, Boolean> dicASDirectRules, SortedMap<String, Boolean> dicActionDefs) {
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead("Register Functions matching Unittest");
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        List<String> cols = new ArrayList<>();
        cols.add("RF");
        cols.add("Unittest");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicRFs.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);
        }
        String htmlTable = Http.CreateHtmlTable("Register Functions matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("AS Direct Rule");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicASDirectRules.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("AS Direct Rules matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("Action Definition");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicActionDefs.entrySet()) {
            List<String> row = new ArrayList();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("Action Definitions matching Unittest", cols, rows);
        htmlBody += htmlTable;


        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        return htmlDoc;
        
    }
    
    static void ShowReportMatchUnittest(IXConnection ixConn, EloPackage[] eloPackages) {        
        try {
            String[] jsTexts = RepoUtils.LoadTextDocs("ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests", ixConn);   
            SortedMap<String, Boolean> dicRFs = new TreeMap<>();
            SortedMap<String, Boolean> dicASDirectRules = new TreeMap<>();
            SortedMap<String, Boolean> dicActionDefs = new TreeMap<>();
            if (eloPackages.length == 0) {
                dicRFs = RegisterFunctions.GetRFs(ixConn, jsTexts, new EloPackage());        
                dicASDirectRules = ASDirectRules.GetRules(ixConn, jsTexts, new EloPackage());
                dicActionDefs = ActionDefinitions.GetActionDefs(ixConn, jsTexts, new EloPackage());                
            } else {
                for (EloPackage eloPackage : eloPackages) {
                    SortedMap<String, Boolean> dicRF = RegisterFunctions.GetRFs(ixConn, jsTexts, eloPackage);        
                    SortedMap<String, Boolean> dicASDirectRule = ASDirectRules.GetRules(ixConn, jsTexts, eloPackage);
                    SortedMap<String, Boolean> dicActionDef = ActionDefinitions.GetActionDefs(ixConn, jsTexts, eloPackage);
                    dicRFs.putAll(dicRF);
                    dicASDirectRules.putAll(dicASDirectRule);
                    dicActionDefs.putAll(dicActionDef);
                }                
            }
            String htmlDoc = CreateReportMatchUnittest(dicRFs, dicASDirectRules, dicActionDefs);
            Http.ShowReport(htmlDoc);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
    static boolean Match(IXConnection ixConn, String uName, EloPackage eloPackage, String[] jsTexts) {
        for (String jsText : jsTexts) {
            String[] jsLines = jsText.split("\n");
            for (String line : jsLines) {
                if (line.contains(eloPackage.getName())) {
                    if (line.contains(uName)) {
                        return true;
                    }
                }
            }
        }
        return false;        
    }

}
