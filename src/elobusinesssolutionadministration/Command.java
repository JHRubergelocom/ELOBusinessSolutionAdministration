/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.awt.Cursor;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class Command {

    public String cmd;
    public String workingDir; 
    
    Command(JSONObject jcommand) {
        cmd = "";
        workingDir = "";
        try {
            cmd = jcommand.getString("cmd");            
        } catch (JSONException ex) {            
        }
        try {
            workingDir = jcommand.getString("workingDir");      
        } catch (JSONException ex) {            
        }
    }

    static void Execute(Command command) {
        try {
            String cmdPath = command.workingDir + "\\" + command.cmd + ".cmd";
            if (!new File (cmdPath).canExecute()) {
                JOptionPane.showMessageDialog(null, cmdPath + " kann nicht ausgef\u00FChrt werden!", 
                           "canExecute", JOptionPane.INFORMATION_MESSAGE);
                System.out.println(cmdPath + " kann nicht ausgef\u00FChrt werden!");
                return;                 
            }
            
            try {
                ProcessBuilder pb = new ProcessBuilder(cmdPath);
                pb.directory(new File (command.workingDir));
                Process p = null; 
                p = pb.start();            
                int status = p.waitFor();
                System.out.println("Exit status: " + status); 
                
                InputStream is = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                String htmlDoc = "<html>\n";
                String htmlHead = Http.CreateHtmlHead(cmdPath);
                String htmlStyle = Http.CreateHtmlStyle();
                String htmlBody = "<body>\n";
                
                htmlBody += "<h1>"+ cmdPath + "</h1>";
                
                while ((line = br.readLine()) != null) {
                    htmlBody += "<h4>"+ line + "</h4>";
                    System.out.println(line);
                }
                htmlBody += "</body>\n";
                htmlDoc += htmlHead;
                htmlDoc += htmlStyle;
                htmlDoc += htmlBody;
                htmlDoc += "</html>\n";
                
                Http.ShowReport(htmlDoc);

                JOptionPane.showMessageDialog(null, cmdPath + " ausgeführt", 
                           "Execute", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Programmende"); 
                
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "System.InterruptedException message: " + ex.getMessage(), 
                           "InterruptedException", JOptionPane.INFORMATION_MESSAGE);
                
            }
        } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                           "IOException", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}
