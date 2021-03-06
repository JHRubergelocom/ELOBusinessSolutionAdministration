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
import javax.swing.JOptionPane;

/**
 *
 * @author ruberg
 */
public final class EloProperties extends Properties {
    private File propertiesFile = new File("eloproperties.txt");

    public EloProperties() {
        Reader reader = null;
        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }  
            reader = new FileReader(propertiesFile);
            load(reader);      
        } catch (FileNotFoundException ex) {            
            JOptionPane.showMessageDialog(null, "System.FileNotFoundException message: " + ex.getMessage(), 
                      "FileNotFoundException", JOptionPane.INFORMATION_MESSAGE);            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                      "IOException", JOptionPane.INFORMATION_MESSAGE);            
        } finally {
            if (reader != null) {
                try {            
                    reader.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                      "IOException", JOptionPane.INFORMATION_MESSAGE);                                
                }                
            }
        }
    }
    
    private void saveProperties() {
        Writer writer = null;
        try {
            writer = new FileWriter(propertiesFile);
            store(writer, "EloProperties");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
              "IOException", JOptionPane.INFORMATION_MESSAGE);                                            
        } finally {
            if (writer != null) {
                try {            
                    writer.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                      "IOException", JOptionPane.INFORMATION_MESSAGE);                                                                
                }                
            }
        }           
    }

    void setSelectedProfile(Profile profile) {
        setProperty("SelectedProfile", profile.getName());   
        saveProperties();
    }

    String getSelectedProfile() {
        return getProperty("SelectedProfile");        
    }
    
    void setPattern(String pattern) {
        setProperty("Pattern", pattern);   
        saveProperties();
    }
    String getPattern() {
        return getProperty("Pattern");        
    }

    void setCaseSensitiv(boolean caseSensitiv) {
        setProperty("CaseSensitiv", Boolean.toString(caseSensitiv));   
        saveProperties();
    }
    boolean getCaseSensitiv() {
        String value = getProperty("CaseSensitiv").toLowerCase();
        return value.toLowerCase().equals("true");
    }
    
}
