/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.JOptionPane;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Unittests {
    
    static Map<String, String> GetUnittestApp(IXConnection ixConn) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        List<Sord> sordELOappsClientInfo = RepoUtils.FindChildren(parentId, ixConn, false);
        String configApp = "";
        String configId = "";
        String jsonString = "";
        
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

    static void ShowUnittestsApp(Profile profile) { 
        IXConnection ixConn;
        IXConnFactory connFact;        
        try {
            connFact = new IXConnFactory(profile.getIxUrl(), "Show Unittests", "1.0");            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Verbindungsdaten zu ELO \n" + ex.getMessage(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            return;
        }
        try {
            ixConn = connFact.create(profile.user, profile.pwd, null, null);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Indexserver-Verbindung ungültig \n User: " + profile.user + "\n IxUrl: " + profile.getIxUrl(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("RemoteException message: " + ex.getMessage());            
            return;
        }
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();            
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = Unittests.GetUnittestApp(ixConn);
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);  
    }

    static void ShowReportMatchUnittest(Profile profile) {
        
        IXConnection ixConn;
        IXConnFactory connFact;   
        try {
            connFact = new IXConnFactory(profile.getIxUrl(), "Show Report Match Unittest", "1.0");            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Verbindungsdaten zu ELO \n" + ex.getMessage(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            return;
        }
        try {
            ixConn = connFact.create(profile.user, profile.pwd, null, null);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Indexserver-Verbindung ungültig \n User: " + profile.user + "\n IxUrl: " + profile.getIxUrl(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("RemoteException message: " + ex.getMessage());            
            return;
        }
        
        List<String> jsTexts = RepoUtils.LoadTextDocs("ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests", ixConn);        
        SortedMap<String, Boolean> dicRFs = RegisterFunctions.GetRFs(ixConn, jsTexts, profile.eloPackage);        
        SortedMap<String, Boolean> dicASDirectRules = ASDirectRules.GetRules(ixConn, jsTexts, profile.eloPackage);
        SortedMap<String, Boolean> dicActionDefs = ActionDefinitions.GetActionDefs(ixConn, jsTexts, profile.eloPackage);
        String htmlDoc = Http.CreateHtmlReport(dicRFs, dicASDirectRules, dicActionDefs);
        Http.ShowReport(htmlDoc);
    }

    static boolean Match(IXConnection ixConn, String uName, String eloPackage, List<String> jsTexts) {
        for (String jsText : jsTexts) {
            String[] jsLines = jsText.split("\n");
            for (String line : jsLines) {
                if (line.contains(eloPackage)) {
                    if (line.contains(uName)) {
                        return true;
                    }
                }
            }
        }
        return false;        
    }
    
}
