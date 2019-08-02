/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;
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

    static String GitPull (TextArea txtOutput, String htmlBody, String gitDir) throws IOException {
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
                htmlBody += "<h4>"+ line + "</h4>";
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
        return htmlBody;
    }
    
    private static void SubDirectories(TextArea txtOutput, String directory) throws IOException {
        Optional<ArrayList<File>> optFiles = getPaths(new File(directory), new ArrayList<>());
        ArrayList<File> files;
        if (optFiles.isPresent()) {
            files = optFiles.get();
        } else {
            return;
        }
        
        String gitCommand;
        gitCommand = "directory " + directory + ": " + "GitPullAll";
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead(gitCommand);
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        htmlBody += "<h1>"+ gitCommand + "</h1>";
        
        for (int i = 0; i < files.size(); i++) {
            htmlBody += "<h4>"+ files.get(i).getCanonicalPath() + "</h4>";            
            System.out.println(files.get(i).getCanonicalPath()); 
            txtOutput.appendText(files.get(i).getCanonicalPath() + "\n");             
            htmlBody = GitPull(txtOutput, htmlBody, files.get(i).getCanonicalPath());            
        }    
        
        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        Http.ShowReport(htmlDoc);


    }

    private static Optional<ArrayList<File>> getPaths(File file, ArrayList<File> list) {
        if (file == null || list == null || !file.isDirectory())
            return Optional.empty();
        File[] fileArr = file.listFiles(f ->(f.getName().endsWith(".git")) && !(f.getName().contentEquals(".git")));
        for (File f : fileArr) {
            if (f.isDirectory()) {
                getPaths(f, list);
                list.add(f);                
            }
        }
        return Optional.of(list);
    }
    
}
