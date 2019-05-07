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
    static final int ELO_PULL_UNITTEST = 0;
    static final int ELO_PULL_PACKAGE = 1;
    static final int ELO_PREPARE = 2;
    
    static final int GIT_PULL_ALL = 0;
    
    
    private String name;
    private String eloPackage;
    private String gitSolutionsDir;
    private String gitUser;
    private String arcPath;
    private EloCommand powershell;
    private EloCommand command;
    private String user;
    private String pwd;

    Profile(JSONObject jobj, JSONObject[] jarray, int index) {
        name = "";
        eloPackage = "";
        gitSolutionsDir = "";
        gitUser = "";
        arcPath = "";
        powershell = null;
        command = null;
        user = "";
        pwd = "";
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            eloPackage = jarray[index].getString("package");      
        } catch (JSONException ex) {            
        }
        try {
            gitSolutionsDir = jobj.getString("gitSolutionsDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitUser = jobj.getString("gitUser");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobj.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jps1 = jobj.getJSONObject("powershell");               
            powershell = new EloCommand(jps1, EloCommand.PS1);                      
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jcommand = jobj.getJSONObject("command");
            command = new EloCommand(jcommand, EloCommand.CMD);
        } catch (JSONException ex) {            
        }
        try {
            user = jobj.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobj.getString("pwd");              
        } catch (JSONException ex) {            
        }
    }
    
    public EloCommand getCommand() {
        return command;
    }
    
    public EloCommand getPowershell() {
        return powershell;
    }
    
    public String getGitSolutionsDir() {
        return gitSolutionsDir;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPwd() {
        return pwd;
    }

    public String getEloPackage() {
        return eloPackage;
    }

    public String getIxUrl() {        
        if(name.contains("playground")) {
            return "http://playground.dev.elo/ix-Solutions/ix";
        }
        return  "http://" + gitUser + "-" + name + ".dev.elo/ix-Solutions/ix";
    }
}
