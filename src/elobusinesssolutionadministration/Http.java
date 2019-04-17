/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 *
 * @author ruberg
 */
class Http {

    static void OpenUrl(String url) {
        if(java.awt.Desktop.isDesktopSupported() ) {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        java.net.URI uri;
        try {
            if(desktop.isSupported(java.awt.Desktop.Action.BROWSE) ) {
              uri = new java.net.URI(url);
                try {
                    desktop.browse(uri);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }            
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        } 
      }         
    }

    private static String CreateHtmlHead(String title) {
        String htmlHead = "  <head>\n";
        htmlHead += "    <title>" + title + "</title>\n";
        htmlHead += "  </head>\n";
        return htmlHead;
    }

    private static String CreateHtmlStyle() {
        String htmlStyle = "  <style>\n";

        htmlStyle += "body {\n";
        htmlStyle += "  font-family: 'Segoe UI', Verdana, 'sans serif';\n";
        htmlStyle += "  margin: 15px;\n";
                htmlStyle += "  font-size: 12px;\n";
        htmlStyle += "}\n";
        htmlStyle += "table {\n";
                htmlStyle += "  font-size: 12px;\n";
        htmlStyle += "  padding-left: 10px;\n";
        htmlStyle += "  border-width: 0px;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "  border-color: #A2A2A2;\n";
        htmlStyle += "}\n";
        htmlStyle += "table td {\n";
        htmlStyle += "  padding: 3px 7px;\n";
        htmlStyle += "}\n";
        htmlStyle += "table tr {\n";
        htmlStyle += "  white-space: nowrap;\n";
        htmlStyle += "}\n";
        htmlStyle += ".tdh {\n";
        htmlStyle += "  font-weight: bold;\n";
        htmlStyle += "  padding: 5px 5px 5px 5px;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 1px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1 {\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2 {\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1b {\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2b {\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1r {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2r {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1br {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2br {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += "h1 {\n";
        htmlStyle += "  padding: 30px 0px 0px 0px;\n";
        htmlStyle += "  font-size: 16px;\n";
        htmlStyle += "  font-weight: bold;\n";
        htmlStyle += "}\n";

        htmlStyle += "  </style>\n";
        return htmlStyle;        
    }

    private static String CreateHtmlTable(String header, List<String> cols, List<List<String>> rows) {
        String htmlTable = "    <h1>" + header + "</h1>\n";
        htmlTable += "    <div class='container'>\n";
        htmlTable += "      <table border='2'>\n";
        htmlTable += "        <colgroup>\n";
        for (String col : cols) {
            htmlTable += "          <col width='100'>\n";
        }
        htmlTable += "        </colgroup>\n";
        htmlTable += "        <tr>\n";
        for (String col : cols) {
            htmlTable += "          <td class = 'tdh' align='left' valign='top'>" + col + "</td>\n";
        }
        htmlTable += "        </tr>\n";

        int i = 0;
        for (List<String> row : rows) {
            String td = "td2";
            if ((i % 2) == 0) {
                td = "td1";
            }
            if (i == (rows.size() - 1)) {
                td += "b";
            }
            htmlTable += "        <tr>\n";
            
            for (String cell : row) {
                if(cell.equals("False")) {
                    td += "r";
                }
            }    
            for (String cell : row) {
                htmlTable += "          <td class = '" + td + "' align='left' valign='top'>" + cell + "</td>\n";
            }

            htmlTable += "        </tr>\n";
            i++;
        }
        htmlTable += "      </table>\n";
        htmlTable += "    </div>\n";

        return htmlTable;

    }
    
    static String CreateHtmlReport(SortedMap<String, Boolean> dicRFs, SortedMap<String, Boolean> dicASDirectRules, SortedMap<String, Boolean> dicActionDefs) {
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead("Register Functions matching Unittest");
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        List<String> cols = new ArrayList<>();
        cols.add("RF");
        cols.add("Unittest");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicRFs.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);
        }
        String htmlTable = Http.CreateHtmlTable("Register Functions matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("AS Direct Rule");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicASDirectRules.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("AS Direct Rules matching Unittest", cols, rows);
        htmlBody += htmlTable;

        cols = new ArrayList<>();
        cols.add("Action Definition");
        cols.add("Unittest");
        rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dicActionDefs.entrySet()) {
            List<String> row = new ArrayList();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);            
        }
        htmlTable = Http.CreateHtmlTable("Action Definitions matching Unittest", cols, rows);
        htmlBody += htmlTable;


        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        return htmlDoc;
        
    }


}
