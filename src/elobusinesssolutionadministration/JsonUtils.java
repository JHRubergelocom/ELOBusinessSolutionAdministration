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
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author ruberg
 */
public class JsonUtils {
    
    public static JSONObject readJson (String jsonFile) {
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
        return new JSONObject(jsonString);           
    }

    public static JSONObject[] getArray (JSONObject jobj, String key) {
        JSONArray jarr = jobj.getJSONArray(key);
        JSONObject jobjs[] = new JSONObject[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jobjs[i] = jarr.getJSONObject(i);
        } 
        return jobjs;
    }
}
