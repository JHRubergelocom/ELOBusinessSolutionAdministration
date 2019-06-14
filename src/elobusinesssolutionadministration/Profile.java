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
    private EloPackage[] eloPackages;
    private EloCommand[] eloCommands;

    Profile(JSONObject[] jarray, int index) {
        name = "";
        eloPackages = new EloPackage[]{};
        eloCommands = new EloCommand[]{};
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject[] jarrayEloPackages = JsonUtils.getArray(jarray[index], "eloPackages");
            eloPackages = new EloPackage[jarrayEloPackages.length];
            for (int i = 0; i < jarrayEloPackages.length; i++) {
                EloPackage ep = new EloPackage(jarrayEloPackages, i);  
                eloPackages[i] = ep;
            }             
        } catch (JSONException ex) {  
            eloPackages = new EloPackage[]{};            
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
    
    public EloPackage[] getEloPackages() {
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
