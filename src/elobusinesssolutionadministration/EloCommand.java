/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import javafx.scene.control.TextArea;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
public class EloCommand {
    static final String ELO_PULL_UNITTEST = "eloPullUnittest";
    static final String ELO_PULL_PACKAGE = "eloPullPackage";
    static final String ELO_PREPARE = "eloPrepare";
    
    static final String SHOWREPORTMATCHUNITTEST = "ShowReportMatchUnittest";
    static final String SHOWUNITTESTSAPP = "ShowUnittestsApp";
    static final String STARTADMINCONSOLE = "StartAdminConsole";
    static final String STARTAPPMANAGER = "StartAppManager";
    static final String STARTWEBCLIENT = "StartWebclient";
    static final String STARTKNOWLEDGEBOARD = "ShowKnowledgeBoard";
    static final String GITPULLALL = "GitPullAll";
    static final String STARTEXPORTELO = "StartExportElo";
    static final String SHOWELOAPPLICATIONSERVER = "ShowEloApplicationServer";
    
    private String name;
    private String cmd;
    private String workspace;
    private String version;
    

    EloCommand(JSONObject[] jarray, int index) {
        name = "";
        cmd = "";
        workspace = "";
        version = "";
        
        try {
            name = jarray[index].getString("name");            
        } catch (JSONException ex) {            
        }
        try {
            cmd = jarray[index].getString("cmd");      
        } catch (JSONException ex) {            
        }
        try {
            workspace = jarray[index].getString("workspace");      
        } catch (JSONException ex) {            
        }
        try {
            version = jarray[index].getString("version");      
        } catch (JSONException ex) {            
        }
    }    
    EloCommand() {
        name = "";
        cmd = "";
        workspace = "";
        version = "";
    }
    
    public String getName() {
        return name;
    }
    
    public String getCmd() {
        return cmd;
    }
    
    public String getWorkspace() {
        return workspace;
    }
    
    public String getVersion() {
        return version;
    }
    
    void Execute( TextArea txtOutput, Profiles pfs, int index) {
        
        try {
            txtOutput.setText("");
            String workingDir = pfs.getWorkingDir(index);
            
            String eloCommand = getCmd() + " -stack " + pfs.getStack(index) + " -workspace " + getWorkspace();
            if (getVersion().length() > 0) {
                eloCommand = eloCommand + " -version " + getVersion();                
            }
            
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", eloCommand);                
            pb.directory(new File (workingDir));
            Process p; 
            p = pb.start();  
            
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            String htmlDoc = "<html>\n";
            String htmlHead = Http.CreateHtmlHead(eloCommand);
            String htmlStyle = Http.CreateHtmlStyle();
            String htmlBody = "<body>\n";

            htmlBody += "<h1>"+ eloCommand + "</h1>";

            while ((line = br.readLine()) != null) {
                htmlBody += "<h4>"+ line + "</h4>";
                System.out.println(line);
                txtOutput.appendText(line + "\n");                
                if (line.contains("already exists")) {
                    break;
                }
            }

            if (line != null) {
                if (line.contains("already exists")) {
                    try (OutputStream os = p.getOutputStream()) {
                        OutputStreamWriter osr = new OutputStreamWriter(os);
                        BufferedWriter bw = new BufferedWriter(osr);
                        
                        Scanner sc = new Scanner("n");
                        String input = sc.nextLine();
                        input += "\n";
                        bw.write(input);
                        bw.flush();
                    }
                    br.close();
                }                
            }
            
            htmlBody += "</body>\n";
            htmlDoc += htmlHead;
            htmlDoc += htmlStyle;
            htmlDoc += htmlBody;
            htmlDoc += "</html>\n";

            Http.ShowReport(htmlDoc);

            System.out.println("Programmende"); 
            txtOutput.appendText("Programmende" + "\n");                

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                       "IOException", JOptionPane.INFORMATION_MESSAGE);
        } 
    }

}
