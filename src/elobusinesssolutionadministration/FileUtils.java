/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author ruberg
 */
class FileUtils {

    private static String sanitizeFilename(String fileName) {
        String regex = "[\\/\\?<>\\\\:\\*\\|\":]";
        return fileName.replaceAll(regex, " ");
    }

    static void SaveToFile(String dirName, String fileName, String fileText, String fileExt ) {
        fileName = sanitizeFilename(fileName);
        
        File dir = new File(dirName);
        File file = new File(dirName + "\\" + fileName + "." + fileExt);
        
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }  
            FileWriter fw = new FileWriter(file);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(fileText);
                bw.flush();
            }                                    
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage() + " dirName=" + dirName + " fileName=" + fileName);
            JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                      "IOException", JOptionPane.INFORMATION_MESSAGE);
        }        
    }    
}
