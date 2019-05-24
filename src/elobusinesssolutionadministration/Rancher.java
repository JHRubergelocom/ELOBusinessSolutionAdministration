/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

/**
 *
 * @author ruberg
 */
class Rancher {

    static void ShowRancher(Profiles profiles, int index) {
        try {
            String rancherUrl = "http://rancher.elo.local/env/1a81/apps/stacks?tags=" + profiles.getGitUser() + "&which=all";
            Http.OpenUrl(rancherUrl);                    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
