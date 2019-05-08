/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;
import javax.swing.JOptionPane;

/**
 *
 * @author ruberg
 */
public class Connection {
    static IXConnection getIxConnection(Profiles profiles, int index) throws Exception {
        IXConnection ixConn;
        IXConnFactory connFact;        
        try {
            connFact = new IXConnFactory(profiles.getIxUrl(index), "IXConnection", "1.0");            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Verbindungsdaten zu ELO \n" + ex.getMessage(), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("IllegalStateException message: " +  ex.getMessage());            
            throw new Exception("Connection");
        }
        try {
            ixConn = connFact.create(profiles.getUser(), profiles.getPwd(), null, null);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, "Indexserver-Verbindung ungültig \n User: " + profiles.getUser() + "\n IxUrl: " + profiles.getIxUrl(index), "ELO Connection", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("RemoteException message: " + ex.getMessage());            
            throw new Exception("Connection");
        }
        return ixConn;
    }    
}
