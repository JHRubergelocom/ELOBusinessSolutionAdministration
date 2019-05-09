/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

/**
 *
 * @author ruberg
 */
class GitPullAll {

    static void Execute(TextArea txtOutput, String workingDir) {
        try {
            txtOutput.setText("");
            SubDirectories(txtOutput, workingDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void GitPull (TextArea txtOutput, String gitDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "pull");  
        pb.directory(new File (gitDir));
        Process powerShellProcess = pb.start();
        // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;
        System.out.println("Standard Output:");
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
                txtOutput.appendText(line + "\n"); 
            }
        }
        System.out.println("Standard Error:");
        try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()))) {
            while ((line = stderr.readLine()) != null) {
                System.err.println(line);
            }
        }
        System.out.println("Done");        
        txtOutput.appendText("Done\n"); 
    }
    
    private static void SubDirectories(TextArea txtOutput, String directory) throws IOException {
        ArrayList<File> files = getPaths(txtOutput, new File(directory), new ArrayList<>());
        if(files == null) return;   
        for (int i = 0; i < files.size(); i++) {
            System.out.println(files.get(i).getCanonicalPath()); 
            txtOutput.appendText(files.get(i).getCanonicalPath() + "\n");             
            GitPull(txtOutput, files.get(i).getCanonicalPath());
        }        
    }

    private static ArrayList<File> getPaths(TextArea txtOutput, File file, ArrayList<File> list) {
        if (file == null || list == null || !file.isDirectory())
            return null;
        File[] fileArr = file.listFiles(new FileNameFilter(".git"));
        for (File f : fileArr) {
            if (f.isDirectory()) {
                getPaths(txtOutput, f, list);
                list.add(f);                
            }
        }
        return list;
    }
    
}
