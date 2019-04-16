/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author ruberg
 */
class Http {

    static void OpenUrl(String url) {
        if(java.awt.Desktop.isDesktopSupported() ) {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        java.net.URI uri;
        try {
            if(desktop.isSupported(java.awt.Desktop.Action.BROWSE) ) {
              uri = new java.net.URI(url);
                try {
                    desktop.browse(uri);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }            
        } catch (URISyntaxException ex) {
        } 
      }         
    }
    
}
