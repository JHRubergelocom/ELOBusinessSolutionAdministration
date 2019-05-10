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
    private EloCommand[] eloCommands;

    Profile(JSONObject[] jarray, int index) {
        name = "";
        eloPackage = "";
        eloCommands = null;
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            eloPackage = jarray[index].getString("package");      
        } catch (JSONException ex) {            
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
    
    public String getEloPackage() {
        return eloPackage;
    }

    public EloCommand[] getEloCommands() {
        return eloCommands;
    }
    
    EloCommand getEloCommand(String commandName) {
        for (int i = 0; i < getEloCommands().length; i++) { 
            EloCommand ec = getEloCommands()[i];
            if (ec.getName().contentEquals(commandName)) {
                return ec;
            }
        } 
        return new EloCommand();       
    }
    


}
