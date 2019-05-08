package elobusinesssolutionadministration;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ruberg
 */
public class Profile {
    
    
    private String name;
    private String eloPackage;

    Profile(JSONObject[] jarray, int index) {
        name = "";
        eloPackage = "";
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            eloPackage = jarray[index].getString("package");      
        } catch (JSONException ex) {            
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getEloPackage() {
        return eloPackage;
    }

}
