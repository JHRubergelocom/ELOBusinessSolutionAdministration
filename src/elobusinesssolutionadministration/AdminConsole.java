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
class AdminConsole {
    private final IXConnection ixConn;

    AdminConsole(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    void StartAdminConsole() {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String[] adminConsole = ixUrl.split("/");
        String adminConsoleUrl = adminConsole[0] + "//" + adminConsole[2] + "/AdminConsole";
        adminConsoleUrl = adminConsoleUrl + "/?lang=de";
        adminConsoleUrl = adminConsoleUrl + "&ticket=" + ticket;
        adminConsoleUrl = adminConsoleUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(adminConsoleUrl);                    
    }
    
}
