/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
public class EloPackage {
    private String name;
    private String folder;
    
    EloPackage(JSONObject[] jarray, int index) {
        name = "";
        folder = "";
        
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            folder = jarray[index].getString("folder");      
        } catch (JSONException ex) {            
        }
    }    
    EloPackage() {
        name = "";
        folder = "";
    }
    
    public String getName() {
        return name;
    }
    
    public String getFolder() {
        return folder;
    }
    
}
