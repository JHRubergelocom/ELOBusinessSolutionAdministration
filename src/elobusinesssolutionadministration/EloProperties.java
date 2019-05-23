/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 * @author ruberg
 */
public final class EloProperties extends Properties{

    private Reader reader = null;
    private Writer writer = null;
    private File propertiesFile = new File("eloproperties.txt");

    public EloProperties() {
        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }  
            reader = new FileReader(propertiesFile);
            load(reader);            
        } catch (FileNotFoundException ex) {
            
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void SetSelectedProfile(Profile profile) {
        setProperty("SelectedProfile", profile.getName());   
        try {
            writer = new FileWriter(propertiesFile);
            store(writer, "Profiles");
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    String GetSelectedProfile() {
        return getProperty("SelectedProfile");        
    }
}
