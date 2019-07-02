/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class KnowledgeBoard {
    private final IXConnection ixConn;

    KnowledgeBoard(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    private Map<String, String> GetKnowledgeBoard() {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/knowledge/ELOapps/ClientInfos";
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
            
            String id = config.getString("id");
            if (id != null) {
                if (id.contains("tile-sol-knowledge-board")) {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = id;

                }
            }
        }
        dicApp.put("configApp", configApp);
        dicApp.put("configId", configId);

        return dicApp;
        
    }
    
    void ShowKnowledgeBoard() {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replace("ix-", "wf-");
        appUrl = appUrl.replace("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetKnowledgeBoard();
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);
    }
    
}
