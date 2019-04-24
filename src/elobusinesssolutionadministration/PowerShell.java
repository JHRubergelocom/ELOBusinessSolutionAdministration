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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruberg
 */
class PowerShell {

    public String[] ps1;
    
    PowerShell(JSONObject jps1) {
        JSONArray jarr = jps1.getJSONArray("ps1");
        ps1 = new String[jarr.length()];
        try {
            for (int i = 0; i < jarr.length(); i++) {
                ps1[i] = jarr.getString(i);
            }             
        } catch (JSONException ex) {            
        }
    }
    
     static void Execute(String psCommand, String psWorkingDir, TextArea txtOutput) {
        try {
            txtOutput.setText("");
            String ps1Path = psWorkingDir + "\\" + psCommand + ".ps1";
            if (!new File (ps1Path).canExecute()) {
                JOptionPane.showMessageDialog(null, ps1Path + " kann nicht ausgef\u00FChrt werden!", 
                           "canExecute", JOptionPane.INFORMATION_MESSAGE);
                System.out.println(ps1Path + " kann nicht ausgef\u00FChrt werden!");
                txtOutput.appendText(ps1Path + " kann nicht ausgef\u00FChrt werden!" + "\n");                
                return;                 
            }
            
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", ps1Path);
            pb.directory(new File (psWorkingDir));
            Process p; 
            p = pb.start();            

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
                                    
            String line;
            String htmlDoc = "<html>\n";
            String htmlHead = Http.CreateHtmlHead(ps1Path);
            String htmlStyle = Http.CreateHtmlStyle();
            String htmlBody = "<body>\n";

            htmlBody += "<h1>"+ ps1Path + "</h1>";

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
                    OutputStream os = p.getOutputStream();
                    OutputStreamWriter osr = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osr);

                    Scanner sc = new Scanner("n");
                    String input = sc.nextLine();
                    input += "\n";
                    bw.write(input);
                    bw.flush();    

                    br.close();
                }                
            }
            
            
            
            htmlBody += "</body>\n";
            htmlDoc += htmlHead;
            htmlDoc += htmlStyle;
            htmlDoc += htmlBody;
            htmlDoc += "</html>\n";

            Http.ShowReport(htmlDoc);

            JOptionPane.showMessageDialog(null, ps1Path + " ausgeführt", 
                       "Execute", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Programmende"); 
            txtOutput.appendText("Programmende" + "\n");                
        } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                           "IOException", JOptionPane.INFORMATION_MESSAGE);
        }
         
    }

   
    
}
