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
    private Profile[] profiles;
    private String gitSolutionsDir;
    private String gitDevDir;
    private String gitUser;
    private String arcPath;
    private String user;
    private String pwd;    
    
    Profiles(String jsonFile) {
        profiles = new Profile[]{};
        gitSolutionsDir = "";
        gitDevDir = "";
        gitUser = "";
        arcPath = "";
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
                // System.out.println("Gelesene Zeile: " + line);
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
            gitDevDir = jobjProfiles.getString("gitDevDir");            
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
            user = jobjProfiles.getString("user");            
        } catch (JSONException ex) {            
        }
        try {
            pwd = jobjProfiles.getString("pwd");              
        } catch (JSONException ex) {            
        }
        
    }

    Profiles() {
        profiles = new Profile[]{};
        gitSolutionsDir = "";
        gitDevDir = "";
        gitUser = "";
        arcPath = "";
        user = "";
        pwd = "";
    }

    public String getGitSolutionsDir() {
        return gitSolutionsDir;
    }
    
    public String getDevDir() {
        return gitDevDir;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPwd() {
        return pwd;
    }

    public String getGitUser() {
        return gitUser;
    }

    public int getLength() {
      return profiles.length;  
    } 

    public Profile getProfile(int index) {
        return profiles[index];
    }    
}
