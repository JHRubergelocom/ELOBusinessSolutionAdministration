/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import de.elo.ix.client.WFDiagram;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author ruberg
 */
class Search {
    
    private static String CreateReportSearchResult(SortedMap<Sord, SortedMap<Integer, String>> dicSordDocLines, SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines, String searchPattern) {
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead("Search Results matching '" + searchPattern + "'");
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        List<String> cols = new ArrayList<>();
        cols.add("Sord");
        cols.add("Lineno");
        cols.add("Line");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<Sord, SortedMap<Integer, String>> entrySord : dicSordDocLines.entrySet()) {
            SortedMap<Integer, String> dicDocLines = entrySord.getValue();            
            for (Map.Entry<Integer, String> entryDocLines : dicDocLines.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(entrySord.getKey().getName());                
                row.add(entryDocLines.getKey().toString());
                row.add(entryDocLines.getValue());
                rows.add(row);
            }
        }
        String htmlTable = Http.CreateHtmlTable("Search Results Sord Documents matching '" + searchPattern + "'", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("Workflow");
        cols.add("Lineno");
        cols.add("Line");
        rows = new ArrayList<>();
        for (Map.Entry<WFDiagram, SortedMap<Integer, String>> entryWorkflow : dicWorkflowLines.entrySet()) {
            SortedMap<Integer, String> dicDocLines = entryWorkflow.getValue();            
            for (Map.Entry<Integer, String> entryDocLines : dicDocLines.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(entryWorkflow.getKey().getName());                
                row.add(entryDocLines.getKey().toString());
                row.add(entryDocLines.getValue());
                rows.add(row);
            }
        }
        htmlTable = Http.CreateHtmlTable("Search Results Workflow Templates matching '" + searchPattern + "'", cols, rows);
        htmlBody += htmlTable;
        
        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        return htmlDoc;        
        
    }

    static void ShowSearchResult(IXConnection ixConn, String searchPattern) {
        SortedMap<Sord, SortedMap<Integer, String>> dicSordDocLines = RepoUtils.LoadSordDocLines("ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions", ixConn, searchPattern);
        SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines = new TreeMap<>(new WFDiagramComparator());
        try {
            dicWorkflowLines = WfUtils.LoadWorkflowLines(ixConn, searchPattern);
        } catch (RemoteException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    
        String htmlDoc = CreateReportSearchResult(dicSordDocLines, dicWorkflowLines, searchPattern);
        Http.ShowReport(htmlDoc);

        
        JOptionPane.showMessageDialog(null, "Noch nicht implementiert", "ShowSearchResult", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
