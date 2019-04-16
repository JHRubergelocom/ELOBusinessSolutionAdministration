/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.DocVersion;
import de.elo.ix.client.EditInfo;
import de.elo.ix.client.EditInfoC;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.Sord;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        BufferedReader in = null;
        String line;
        String jsonString = "";
        String bom = "\uFEFF"; // ByteOrderMark (BOM);
        
        Map<String, String> dicApp = new HashMap<>();
        try {
            for (Sord s : sordELOappsClientInfo) {
                String objId = s.getId() + "";
                EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();
                InputStream inputStream = ixConn.download(url, 0, -1);
                
                try {
                    jsonString = "";
                    in = new BufferedReader(new InputStreamReader(inputStream ));
                    while ((line = in.readLine()) != null) {
                        System.out.println("Gelesene Zeile: " + line);
                        jsonString = jsonString.concat(line);
                    }                       
                } catch (FileNotFoundException ex) {    
                    ex.printStackTrace();
                } catch (IOException ex) {            
                    ex.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                jsonString = jsonString.replaceAll("namespace", "namespace1");
                jsonString = jsonString.replaceAll(bom, "");

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
            
        } catch (RemoteException ex) {    
            ex.printStackTrace();
        }
        return dicApp;
    }    

    static void ShowUnittestsApp(Profile profile) { 
        IXConnection ixConn;
        IXConnFactory connFact;        
        try {
            connFact = new IXConnFactory(profile.ixUrl, "Show Unittests", "1.0");            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Verbindungsdaten zu ELO \n" + ex.getMessage(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            return;
        }
        try {
            ixConn = connFact.create(profile.user, profile.pwd, null, null);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Indexserver-Verbindung ungültig \n User: " + profile.user + "\n IxUrl: " + profile.ixUrl, "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
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
    
}
