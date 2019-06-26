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
class EloApplicationServer {

    static void ShowEloApplicationServer(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String[] eloApplicationServer = ixUrl.split("/");
        String eloApplicationServerUrl = eloApplicationServer[0] + "//" + eloApplicationServer[2] + "/manager/html";
        eloApplicationServerUrl = eloApplicationServerUrl + "/?lang=de";
        eloApplicationServerUrl = eloApplicationServerUrl + "&ticket=" + ticket;
        eloApplicationServerUrl = eloApplicationServerUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(eloApplicationServerUrl);                    
    }
    
}
