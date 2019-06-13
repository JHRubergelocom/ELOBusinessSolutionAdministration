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
    private String[] eloPackages;
    private EloCommand[] eloCommands;

    Profile(JSONObject[] jarray, int index) {
        name = "";
        eloPackages = null;
        eloCommands = null;
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            eloPackages = JsonUtils.getStringArray(jarray[index], "packages");
        } catch (JSONException ex) {   
            eloPackages = new String[]{};
        }
        try {
            JSONObject[] jarrayEloCommands = JsonUtils.getArray(jarray[index], "eloCommands");
            eloCommands = new EloCommand[jarrayEloCommands.length];
            for (int i = 0; i < jarrayEloCommands.length; i++) {
                EloCommand ec = new EloCommand(jarrayEloCommands, i);  
                eloCommands[i] = ec;
            }             
        } catch (JSONException ex) {            
        }
        
    }
    
    public String getName() {
        return name;
    }
    
    public String[] getEloPackages() {
        return eloPackages;
    }

    public EloCommand[] getEloCommands() {
        return eloCommands;
    }
    
    EloCommand getEloCommand(String commandName) {
        for (EloCommand ec : getEloCommands()) {
            if (ec.getName().contentEquals(commandName)) {
                return ec;
            }
        } 
        return new EloCommand();       
    }

}
