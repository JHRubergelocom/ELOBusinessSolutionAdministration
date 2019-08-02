/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.json.JSONObject;
import org.xml.sax.InputSource;

/**
 *
 * @author ruberg
 */
class Unittests {   
    private final IXConnection ixConn;

    Unittests(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    Map<String, String> GetUnittestApp() {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        RepoUtils rU = new RepoUtils(ixConn);        
        Sord[] sordELOappsClientInfo = rU.FindChildren(parentId, false);
        String configApp = "";
        String configId = "";
        String jsonString;
        
        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = rU.DownloadDocumentToString(s);
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

    void ShowUnittestsApp() { 
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();            
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetUnittestApp();
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);              
    }
    
    private String CreateReportMatchUnittest(SortedMap<String, Boolean> dicRFs, SortedMap<String, Boolean> dicASDirectRules, SortedMap<String, Boolean> dicActionDefs) {
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
    
    private SortedMap<String, Boolean> GetActionDefs(String[] jsTexts, EloPackage eloPackage) {
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
                boolean match = Match(actionDef, eloPackage, jsTexts);
                dicActionDefs.put(actionDef, match);
            }
        }
        return dicActionDefs;
    }

    private SortedMap<String, Boolean> GetRules(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/ELOas Base/Direct";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/ELOas Base/Direct";
        }
        RepoUtils rU = new RepoUtils(ixConn);
        Sord[] sordRuleInfo = rU.FindChildren(parentId, true);
        SortedMap<String, Boolean> dicRules = new TreeMap<>();
        for(Sord s : sordRuleInfo) {            
            try {
                String xmlText = rU.DownloadDocumentToString (s);             
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                InputSource source = new InputSource(new StringReader(xmlText));            
                String rulesetname = xpath.evaluate("ruleset/base/name", source);
                if (!dicRules.containsKey(rulesetname)) {
                    boolean match = Match(rulesetname, eloPackage, jsTexts);
                    dicRules.put(rulesetname, match);
                }
            } catch (XPathExpressionException ex) {
                System.err.println("XPathExpressionException: " +  ex.getMessage()); 
                ex.printStackTrace();
            }
        }
        return dicRules;
    }    

    private SortedMap<String, Boolean> GetRFs(String[] jsTexts, EloPackage eloPackage) {
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
                                boolean match = Match(rfName, eloPackage, jsTexts);
                                dicRFs.put(rfName, match);
                            }
                        }                        
                    }
                }                    
            }
           
        }                
        return dicRFs;
    }
    
    private boolean Match(String uName, EloPackage eloPackage, String[] jsTexts) {
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

    void ShowReportMatchUnittest(EloPackage[] eloPackages) {    
        RepoUtils rU = new RepoUtils(ixConn);        
        
        try {
            String[] jsTexts = rU.LoadTextDocs("ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests");   
            SortedMap<String, Boolean> dicRFs = new TreeMap<>();
            SortedMap<String, Boolean> dicASDirectRules = new TreeMap<>();
            SortedMap<String, Boolean> dicActionDefs = new TreeMap<>();
            
            if (eloPackages.length == 0) {
                
                dicRFs = GetRFs(jsTexts, new EloPackage()); 
                dicASDirectRules = GetRules(jsTexts, new EloPackage());
                dicActionDefs = GetActionDefs(jsTexts, new EloPackage());                
            } else {
                for (EloPackage eloPackage : eloPackages) {
                    SortedMap<String, Boolean> dicRF = GetRFs(jsTexts, eloPackage);        
                    SortedMap<String, Boolean> dicASDirectRule = GetRules(jsTexts, eloPackage);
                    SortedMap<String, Boolean> dicActionDef = GetActionDefs(jsTexts, eloPackage);
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
    
}
