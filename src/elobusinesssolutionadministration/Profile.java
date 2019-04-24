package elobusinesssolutionadministration;

import org.json.JSONArray;
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
    
    public String name;
    public String eloPackage;
    public String ixUrl;
    public String psWorkingDir;
    public String arcPath;
    public PowerShell powerShell;
    public Command command;
    public String user;
    public String pwd;

    Profile(JSONObject jobj, JSONObject[] jarray, int index) {
        name = "";
        eloPackage = "";
        ixUrl = "";
        psWorkingDir = "";
        arcPath = "";
        powerShell = null;
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
            ixUrl = jarray[index].getString("ixUrl");            
        } catch (JSONException ex) {            
        }
        try {
            psWorkingDir = jarray[index].getString("psWorkingDir");            
        } catch (JSONException ex) {            
        }
        try {
            arcPath = jobj.getString("arcPath");            
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jps1 = jobj.getJSONObject("powershell");               
            powerShell = new PowerShell(jps1);                      
        } catch (JSONException ex) {            
        }
        try {
            JSONObject jcommand = jobj.getJSONObject("command");
            command = new Command(jcommand);
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
}
