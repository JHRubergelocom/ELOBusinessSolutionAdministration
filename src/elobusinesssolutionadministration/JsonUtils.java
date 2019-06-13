/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author ruberg
 */
public class JsonUtils {    
    public static JSONObject[] getArray (JSONObject jobj, String key) {
        JSONArray jarr = jobj.getJSONArray(key);
        JSONObject jobjs[] = new JSONObject[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jobjs[i] = jarr.getJSONObject(i);
        } 
        return jobjs;
    }
    public static String[] getStringArray (JSONObject jobj, String key) {
        JSONArray jarr = jobj.getJSONArray(key);
        String jstrings[] = new String[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jstrings[i] = jarr.getString(i);
        } 
        return jstrings;
    }
}
