/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;

/**
 *
 * @author ruberg
 */
class AppManager {
    static void StartAppManager(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String appManagerUrl = ixUrl.replace("ix-", "wf-");
        appManagerUrl = appManagerUrl.replace("/ix", "/apps/app");
        appManagerUrl = appManagerUrl + "/elo.webapps.AppManager";
        appManagerUrl = appManagerUrl + "/?lang=de";
        appManagerUrl = appManagerUrl + "&ticket=" + ticket;
        appManagerUrl = appManagerUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appManagerUrl);
    }
    
}
