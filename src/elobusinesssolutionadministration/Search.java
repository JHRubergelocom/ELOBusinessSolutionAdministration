/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.WFDiagram;
import de.elo.ix.client.DocMask;
import de.elo.ix.client.IXConnection;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ruberg
 */
class Search {  
    private final IXConnection ixConn;
    private final Pattern p;

    Search(IXConnection ixConn, String searchPattern, boolean caseSensitiv) {
        this.ixConn = ixConn;
        if (caseSensitiv) {
            p = Pattern.compile(searchPattern);
        } else {
            p = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);                        
        }           
    }
    
    private String MarkAllMatches(String htmlText) {
        Matcher matcher = p.matcher( htmlText );
        StringBuffer sb = new StringBuffer( htmlText.length() );
        while ( matcher.find() ) {
          matcher.appendReplacement( sb, "<span>$0</span>" );                    
        }
        matcher.appendTail( sb );
        return new String(sb);                        
    }

    private String CreateReportSearchResult(SortedMap<SordDoc, SortedMap<Integer, String>> dicSordDocLines, 
                                            SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines,
                                            SortedMap<DocMask, SortedMap<Integer, String>> dicDocMaskLines) {
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead("Search Results matching '" + p.toString() + "'");
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        List<String> cols = new ArrayList<>();
        cols.add("Name");
        cols.add("Ext");
        cols.add("Id");
        cols.add("Lineno");
        cols.add("Line");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<SordDoc, SortedMap<Integer, String>> entrySordDoc : dicSordDocLines.entrySet()) {
            SortedMap<Integer, String> dicDocLines = entrySordDoc.getValue();            
            for (Map.Entry<Integer, String> entryDocLines : dicDocLines.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add("<a href='elodms://" + entrySordDoc.getKey().getGuid() + "'>" + entrySordDoc.getKey().getName()); 
                row.add(entrySordDoc.getKey().getExt()); 
                row.add(Integer.toString(entrySordDoc.getKey().getId()));                     
                row.add(entryDocLines.getKey().toString());
                String lineText = entryDocLines.getValue();
                lineText = MarkAllMatches(lineText);
                row.add(lineText);                
                rows.add(row);
            }
        }
        String htmlTable = Http.CreateHtmlTable("Search Results Sord Documents matching <span>'" + p.toString() + "'</span>", cols, rows);
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
                String lineText = entryDocLines.getValue();
                lineText = MarkAllMatches(lineText);                
                row.add(lineText);                
                rows.add(row);
            }
        }
        htmlTable = Http.CreateHtmlTable("Search Results Workflow Templates matching <span>'" + p.toString() + "'</span>", cols, rows);
        htmlBody += htmlTable;
        
        cols = new ArrayList<>();
        cols.add("DocMask");
        cols.add("Lineno");
        cols.add("Line");
        rows = new ArrayList<>();
        for (Map.Entry<DocMask, SortedMap<Integer, String>> entryDocMask : dicDocMaskLines.entrySet()) {
            SortedMap<Integer, String> dicDocLines = entryDocMask.getValue();            
            for (Map.Entry<Integer, String> entryDocLines : dicDocLines.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(entryDocMask.getKey().getName());                
                row.add(entryDocLines.getKey().toString());
                String lineText = entryDocLines.getValue();
                lineText = MarkAllMatches(lineText);
                row.add(lineText);                
                rows.add(row);
            }
        }
        htmlTable = Http.CreateHtmlTable("Search Results Doc Masks matching <span>'" + p.toString() + "'</span>", cols, rows);
        htmlBody += htmlTable;
        
        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        return htmlDoc;        
        
    }

    void ShowSearchResult(List <EloPackage> eloPackages) {
        RepoUtils rU = new RepoUtils(ixConn);   
        SortedMap<SordDoc, SortedMap<Integer, String>> dicSordDocLines = rU.LoadSordDocLines(eloPackages, p);
        Comparator<WFDiagram> byName = Comparator.comparing(wf -> wf.getName());
        Comparator<WFDiagram> byId = Comparator.comparingInt(wf -> wf.getId());
        Comparator<WFDiagram> byWFDiagram = byName.thenComparing(byId);                
        SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines = new TreeMap<>(byWFDiagram);
        
        Comparator<DocMask> byNameDm = Comparator.comparing(dm -> dm.getName());
        Comparator<DocMask> byIdDm = Comparator.comparingInt(dm -> dm.getId());
        Comparator<DocMask> byDocMaskDm = byNameDm.thenComparing(byIdDm);                                
        SortedMap<DocMask, SortedMap<Integer, String>> dicDocMaskLines = new TreeMap<>(byDocMaskDm);
        
        try {
            WfUtils wfU = new WfUtils(ixConn);   
            MaskUtils mU = new MaskUtils(ixConn);               
            dicWorkflowLines = wfU.LoadWorkflowLines(p);
            dicDocMaskLines = mU.LoadDocMaskLines(p);
        } catch (RemoteException | UnsupportedEncodingException ex) {
        }
        
        String htmlDoc = CreateReportSearchResult(dicSordDocLines, dicWorkflowLines, dicDocMaskLines);
        Http.ShowReport(htmlDoc);
    }
    
}
