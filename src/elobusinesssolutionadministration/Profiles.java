/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
public class Profiles {
    static final int ELO_PULL_UNITTEST = 0;
    static final int ELO_PULL_PACKAGE = 1;
    static final int ELO_PREPARE = 2;
    
    static final int GIT_PULL_ALL = 0;

    private Profile[] profiles;
    private String gitSolutionsDir;
    private String gitUser;
    private String arcPath;
    private EloCommand powershell;
    private EloCommand command;
    private String user;
    private String pwd;
    
    
    Profiles(String jsonFile) {
        profiles = null;
        gitSolutionsDir = "";
        gitUser = "";
        arcPath = "";
        powershell = null;
        command = null;
        user = "";
        pwd = "";

        JSONObject jobjProfiles;
        String jsonString = "";
        BufferedReader in = null;
        File file = new File(jsonFile); 
        String line;
        
        try { 
            in = new BufferedReader(new FileReader(jsonFile));
            while ((line = in.readLine()) != null) {
                System.out.println("Gelesene Zeile: " + line);
                jsonString = jsonString.concat(line);
            }            
        } catch (FileNotFoundException ex) {    
            ex.printStackTrace();
        } catch (IOException ex) {            
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        jobjProfiles = new JSONObject(jsonString);        
        JSONObject[] jarrayProfiles = JsonUtils.getArray(jobjProfiles, "profiles");
        
        profiles = new Profile[jarrayProfiles.length];
        for (int i = 0; i < jarrayProfiles.length; i++) {
            Profile p = new Profile(jarrayProfiles, i);  
            profiles[i] = p;
        } 

        try {
            gitSolutionsDir = jobjProfiles.getString("gitSolutionsDir");            
        } catch (JSONException ex) {            
        }
        try {
            gitUser = jobjProfiles.getString("gitUser");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobjProfiles.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jps1 = jobjProfiles.getJSONObject("powershell");               
            powershell = new EloCommand(jps1, EloCommand.PS1);                      
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jcommand = jobjProfiles.getJSONObject("command");
            command = new EloCommand(jcommand, EloCommand.CMD);
        } catch (JSONException ex) {            
        }
        try {
            user = jobjProfiles.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobjProfiles.getString("pwd");              
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
    
    public String getUser() {
        return user;
    }
    
    public String getPwd() {
        return pwd;
    }

    public String getIxUrl(int index) {   
        Profile p = profiles[index];
        if(p.getName().contains("playground")) {
            return "http://playground.dev.elo/ix-Solutions/ix";
        }
        return  "http://" + gitUser + "-" + p.getName() + ".dev.elo/ix-Solutions/ix";
    }
    
    public int getLength() {
      return profiles.length;  
    } 
    
    public Profile getProfile(int index) {
      return profiles[index];
    } 
    
    public String getEloPackage(int index) {
        return profiles[index].getEloPackage();
    }

}
