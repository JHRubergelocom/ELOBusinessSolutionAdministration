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
public class EloCommand {
    static final String CMD = "cmd";
    static final String PS1 = "ps1";
    
    private String cmdCommand;
    private String[] ps1Command;
    private String typeCommand;

    EloCommand(JSONObject jeloCommand, String commandType) {   
        this.typeCommand = commandType;
        if (commandType.contentEquals(CMD)) {
            cmdCommand = "";
            try {
                cmdCommand = jeloCommand.getString("cmd");            
            } catch (JSONException ex) {            
            }
            
        } else if (commandType.contentEquals(PS1)) {
            JSONArray jarr = jeloCommand.getJSONArray("ps1");
            ps1Command = new String[jarr.length()];
            try {
                for (int i = 0; i < jarr.length(); i++) {
                    ps1Command[i] = jarr.getString(i);
                }             
            } catch (JSONException ex) {            
            }
    
        }
    }

    String getCommand(int index) {
        if (typeCommand.contentEquals(CMD)) {
            return cmdCommand;
        } else if (typeCommand.contentEquals(PS1)) {
            return ps1Command[index];
        }
        return "";
    } 
    
    String getType() {
        return typeCommand;
    } 
    
    static void Execute(String eloCommand, EloCommand ec, TextArea txtOutput, String workingDir) {
        
        try {
            txtOutput.setText("");
            ProcessBuilder pb = new ProcessBuilder();  
            if (ec.getType().contentEquals(CMD)) {
                eloCommand = workingDir + "\\" + eloCommand + ".cmd";
            } else if (ec.getType().contentEquals(PS1)) {
                eloCommand = workingDir + "\\" + eloCommand + ".ps1";
            }            
            if (!new File (eloCommand).canExecute()) {
                JOptionPane.showMessageDialog(null, eloCommand + " kann nicht ausgef\u00FChrt werden!", 
                           "canExecute", JOptionPane.INFORMATION_MESSAGE);
                System.out.println(eloCommand + " kann nicht ausgef\u00FChrt werden!");
                txtOutput.appendText(eloCommand + " kann nicht ausgef\u00FChrt werden!" + "\n");                
                return;                 
            }                        
            if (ec.getType().contentEquals(CMD)) {
                pb = new ProcessBuilder(eloCommand);            
            } else if (ec.getType().contentEquals(PS1)) {
                pb = new ProcessBuilder("powershell.exe", eloCommand);                
            }
            
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

            System.out.println("Programmende"); 
            txtOutput.appendText("Programmende" + "\n");                

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "System.IOException message: " + ex.getMessage(), 
                       "IOException", JOptionPane.INFORMATION_MESSAGE);
        } 
    }
}
