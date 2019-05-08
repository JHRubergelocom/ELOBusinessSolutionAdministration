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
class Webclient {

    static void StartWebclient(Profiles profiles, int index) {
        IXConnection ixConn;
        try {
            ixConn = Connection.getIxConnection(profiles, index);
            String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
            String ixUrl = ixConn.getEndpointUrl();
            String webclientUrl = ixUrl.replace("ix-", "web-");
            webclientUrl = webclientUrl.replace("/ix", "");
            webclientUrl = webclientUrl + "/?lang=de";
            webclientUrl = webclientUrl + "&ticket=" + ticket;
            webclientUrl = webclientUrl + "&timezone=Europe%2FBerlin";
            Http.OpenUrl(webclientUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
