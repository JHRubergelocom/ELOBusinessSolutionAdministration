package elobusinesssolutionadministration;

import java.util.ArrayList;
import java.util.List;
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
    private List<EloPackage> eloPackages;
    private List<EloCommand> eloCommands;

    Profile(JSONObject obj) {
        name = "";
        eloPackages = new ArrayList<>();
        eloCommands = new ArrayList<>();
        try {
            name = obj.getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject[] jarrayEloPackages = JsonUtils.getArray(obj, "eloPackages");
            for(JSONObject objEloPackage: jarrayEloPackages){
                eloPackages.add(new EloPackage(objEloPackage));
            }
        } catch (JSONException ex) {  
            eloPackages = new ArrayList<>();            
        }
        try {
            JSONObject[] jarrayEloCommands = JsonUtils.getArray(obj, "eloCommands");
            for(JSONObject objEloCommand: jarrayEloCommands){
                eloCommands.add(new EloCommand(objEloCommand));
            }
        } catch (JSONException ex) {    
            eloCommands = new ArrayList<>();
        }        
    }
    
    public String getName() {
        return name;
    }
    
    public List<EloPackage> getEloPackages() {
        return eloPackages;
    }

    public List<EloCommand> getEloCommands() {
        return eloCommands;
    }
    
    public String getIxUrl(String gitUser) {   
        return  "http://" + getStack(gitUser) + ".dev.elo/ix-Solutions/ix";
    }
    
    public String getStack(String gitUser) {   
        if(name.contains("playground")) {
            return "playground";
        }
        return  gitUser + "-" + name;
    }
    
    public String getWorkingDir(String gitSolutionsDir) {
        if(name.contentEquals("recruiting")) {
            return gitSolutionsDir + "\\hr_" + name + ".git";                        
        } else {
            return gitSolutionsDir + "\\" + name + ".git";            
        }        
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
