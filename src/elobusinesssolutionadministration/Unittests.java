/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.json.JSONObject;
import org.xml.sax.InputSource;

/**
 *
 * @author ruberg
 */
class Unittests {   
    private final IXConnection ixConn;

    Unittests(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    Map<String, String> GetUnittestApp() {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        RepoUtils rU = new RepoUtils(ixConn);        
        Sord[] sordELOappsClientInfo = rU.FindChildren(parentId, false, true);
        String configApp = "";
        String configId = "";
        String jsonString;
        
        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = rU.DownloadDocumentToString(s);
            jsonString = jsonString.replaceAll("namespace", "namespace1");
            JSONObject config = new JSONObject(jsonString);    
            JSONObject web = config.getJSONObject("web");                 
            String webId = web.getString("id");
            if (webId != null)
            {
                if (webId.contains("UnitTests"))
                {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = config.getString("id");
                }
            }
         }
         dicApp.put("configApp", configApp);
         dicApp.put("configId", configId);
            
        return dicApp;
    }    

    void ShowUnittestsApp() { 
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();            
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetUnittestApp();
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);              
    }
    
    private String CreateReportMatchUnittest(SortedMap<String, Boolean> dicRFs, SortedMap<String, Boolean> dicASDirectRules, SortedMap<String, Boolean> dicActionDefs) {
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
    
    private SortedMap<String, Boolean> GetActionDefs(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/Action definitions";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Action definitions";
        }

        RepoUtils rU = new RepoUtils(ixConn);                
        Sord[] sordActionDefInfo = rU.FindChildren(parentId, true, true);
        SortedMap <String, Boolean> dicActionDefs = new TreeMap<>();
        
        for(Sord s : sordActionDefInfo) {
            String actionDef = s.getName();
            String[] rf = actionDef.split("\\.");
            actionDef = rf[rf.length-1];
            actionDef = "actions." + actionDef;
            if (!dicActionDefs.containsKey(actionDef)) {
                boolean match = Match(actionDef, eloPackage, jsTexts);
                if(eloPackage.getName().equals("privacy") || eloPackage.getName().equals("pubsec")) {
                    match = true;
                }                
                dicActionDefs.put(actionDef, match);
            }
        }
        return dicActionDefs;
    }

    private SortedMap<String, Boolean> GetRules(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/ELOas Base/Direct";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/ELOas Base/Direct";
        }
        RepoUtils rU = new RepoUtils(ixConn);
        Sord[] sordRuleInfo = rU.FindChildren(parentId, true, true);
        SortedMap<String, Boolean> dicRules = new TreeMap<>();
        for(Sord s : sordRuleInfo) {            
            try {
                String xmlText = rU.DownloadDocumentToString (s);             
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                InputSource source = new InputSource(new StringReader(xmlText));            
                String rulesetname = xpath.evaluate("ruleset/base/name", source);
                if (!dicRules.containsKey(rulesetname)) {
                    boolean match = Match(rulesetname, eloPackage, jsTexts);
                    dicRules.put(rulesetname, match);
                }
            } catch (XPathExpressionException ex) {
                System.err.println("XPathExpressionException: " +  ex.getMessage()); 
                ex.printStackTrace();
            }
        }
        return dicRules;
    }    

    private SortedMap<String, Boolean> GetRFs(String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/IndexServer Scripting Base";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/IndexServer Scripting Base/_ALL/business_solutions";
        }
        RepoUtils rU = new RepoUtils(ixConn);  
        Sord[] sordRFInfo = rU.FindChildren(parentId, true, true);
        SortedMap<String, Boolean> dicRFs = new TreeMap<>();
        
        for(Sord s : sordRFInfo) {  
           String jsText = rU.DownloadDocumentToString (s);  
           jsText = jsText.replaceAll("\b", "");
           jsText = jsText.replaceAll("\n", " ");
           String[] jsLines = jsText.split(" ");
            for (String line : jsLines) {
                if (line.contains("RF_")) {
                    if (eloPackage.getName().equals("") || (!eloPackage.getName().equals("") && line.contains(eloPackage.getName()))) {
                        String rfName = line;
                        String[] rfNames = rfName.split("\\(");
                        rfName = rfNames[0];
                        if (!line.endsWith(",")
                                && rfName.startsWith("RF_") && !line.contains("RF_ServiceBaseName") && !line.endsWith(".")
                                && !line.contains("RF_FunctionName") && !line.contains("RF_MyFunction")
                                && !line.contains("RF_custom_functions_MyFunction") && !line.contains("RF_custom_services_MyFunction")
                                && !line.contains("RF_sol_function_FeedComment}.") && !line.contains("RF_sol_my_actions_MyAction")
                                && !line.contains("RF_sol_service_ScriptVersionReportCreate")) {
                            if (!dicRFs.containsKey(rfName)) {
                                boolean match = Match(rfName, eloPackage, jsTexts);
                                dicRFs.put(rfName, match);
                            }
                        }                        
                    }
                }                    
            }
           
        }                
        return dicRFs;
    }
    
    private boolean Match(String uName, EloPackage eloPackage, String[] jsTexts) {
        for (String jsText : jsTexts) {
            String[] jsLines = jsText.split("\n");
            for (String line : jsLines) {
                if (line.contains(eloPackage.getName())) {
                    if (line.contains(uName)) {
                        return true;
                    }
                }
            }
        }
        return false;        
    }

    void ShowReportMatchUnittest(EloPackage[] eloPackages) {    
        RepoUtils rU = new RepoUtils(ixConn);        
        
        try {
            String[] jsTexts = rU.LoadTextDocs("ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests");   
            SortedMap<String, Boolean> dicRFs = new TreeMap<>();
            SortedMap<String, Boolean> dicASDirectRules = new TreeMap<>();
            SortedMap<String, Boolean> dicActionDefs = new TreeMap<>();
            
            if (eloPackages.length == 0) {
                
                dicRFs = GetRFs(jsTexts, new EloPackage()); 
                dicASDirectRules = GetRules(jsTexts, new EloPackage());
                dicActionDefs = GetActionDefs(jsTexts, new EloPackage());                
            } else {
                for (EloPackage eloPackage : eloPackages) {
                    SortedMap<String, Boolean> dicRF = GetRFs(jsTexts, eloPackage);        
                    SortedMap<String, Boolean> dicASDirectRule = GetRules(jsTexts, eloPackage);
                    SortedMap<String, Boolean> dicActionDef = GetActionDefs(jsTexts, eloPackage);
                    dicRFs.putAll(dicRF);
                    dicASDirectRules.putAll(dicASDirectRule);
                    dicActionDefs.putAll(dicActionDef);
                }                
            }
            String htmlDoc = CreateReportMatchUnittest(dicRFs, dicASDirectRules, dicActionDefs);
            Http.ShowReport(htmlDoc);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private SortedMap<String, SortedMap<String, List<String>>> GetLibs(EloPackage eloPackage, String libDir) {
        if (eloPackage.getFolder().equals("")) {
            return new TreeMap<>();        
        }        
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/" + libDir;
        RepoUtils rU = new RepoUtils(ixConn);  
        Sord[] sordRFInfo = rU.FindChildren(parentId, true, false);
        SortedMap<String, SortedMap<String, List<String>>> dicLibs = new TreeMap<>();        
        for(Sord s : sordRFInfo) {  
           String libName = ""; 
           List<String> jsLines = rU.DownloadDocumentToList (s);  
            for (String line : jsLines) {
                if (line.contains("sol.define(")) {
                    libName = line;                    
                    if (libName.split("\"").length > 1) {
                        libName = libName.split("\"")[1].trim();                    
                        if (!dicLibs.containsKey(libName)) {
                            dicLibs.put(libName, new TreeMap<>());
                        }                        
                    }
                }
                if (dicLibs.containsKey(libName)) {
                    if (line.contains("function") && line.contains(":") && line.contains("(") && line.contains(")")&& !line.contains("*")){
                        String fName = line;
                        if (fName.split(":").length > 0) {
                            fName = fName.split(":")[0].trim();
                            List<String> params = new ArrayList<>();                            
                            String pNames = line;
                            pNames = pNames.trim();
                            pNames = pNames.split("\\(")[1];
                            pNames = pNames.split("\\)")[0];
                            String [] ps = pNames.split(",");
                            for (String p: ps) {
                                params.add(p.trim());
                            } 
                            dicLibs.get(libName).put(fName, params);                            
                        }                        
                    }                       
                }                
            }           
        }                
        return dicLibs;
    }
    
    private void Debug(SortedMap<String, SortedMap<String, List<String>>> dicLibs, String dicLibsName) {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("dicLibs: " + dicLibsName);
        for (Map.Entry<String, SortedMap<String, List<String>>> entryLib : dicLibs.entrySet()) {
            System.out.println("Lib: " + entryLib.getKey());            
            entryLib.getValue().entrySet().stream().map((entryFunc) -> {
                System.out.println("    Function: " + entryFunc.getKey()); 
                return entryFunc;
            }).forEachOrdered((entryFunc) -> {
                entryFunc.getValue().forEach((p) -> {
                    System.out.println("      Parameter: " + p);
                });
            });
        }
    }
    
    private void SaveUnittestLib(String lib, String jsScript, String profileName, String libDir, String libixas) {
        String exportPath = "C:\\Temp\\Unittests\\" + profileName + "\\"  + libDir;
        
        String eloPackage = "";
        String eloLibModul = "";        
        try {
            eloPackage = lib.split("\\.")[1];
            eloLibModul = lib.split("\\.")[2];
            if (libixas.contains("as") || libixas.contains("ix")) {
                eloLibModul = lib.split("\\.")[3]; 
                switch (eloLibModul) {
                    case "functions":
                    case "renderer":
                    case "actions":
                    case "analyzers":
                    case "collectors":
                    case "executors":
                        eloLibModul = eloLibModul + lib.split("\\.")[4];
                        break;
                }
            }            
        } catch (Exception ex){
        }
        
        String fileName;
        if (libixas.contains("as")) {
            fileName = "[" + libixas + "] sol.unittest.as.services.sol<PACKAGE><LIBMODUL>";                        
        } else {
            fileName = "[" + libixas + "] sol.unittest.ix.services.sol<PACKAGE><LIBMODUL>";            
        }
        
        fileName = fileName.replaceAll("<PACKAGE>", eloPackage);
        fileName = fileName.replaceAll("<LIBMODUL>", eloLibModul);
        
        File exportDir = new File(exportPath);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }          
        FileUtils.SaveToFile(exportPath, fileName, jsScript, "js");        
    }
        
    private String CreateUnittestLibBeforeAll() {
        String jsScript = "";
        
        jsScript += "  beforeAll(function (done) {\n";
        jsScript += "    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;\n";
        jsScript += "    jasmine.DEFAULT_TIMEOUT_INTERVAL = 100000;\n";
        jsScript += "    expect(function () {\n";
        jsScript += "      test.Utils.createTempSord(\"sol<PACKAGE><LIBMODUL>\").then(function success(obsol<PACKAGE><LIBMODUL>Id) {\n";
        jsScript += "        test.Utils.getSord(\"ARCPATH:/Administration/Business Solutions/<PACKAGE> [unit tests]/Resources/<LIBMODUL>\").then(function success1(<LIBMODUL>Sord1) {\n";
        jsScript += "          <LIBMODUL>Sord = <LIBMODUL>Sord1;\n";
        jsScript += "          userName = test.Utils.getCurrentUserName();\n";
        jsScript += "          test.Utils.getUserInfo(userName).then(function success3(userInfo1) {\n";
        jsScript += "            userInfo = userInfo1;\n";
        jsScript += "            done();\n";
        jsScript += "          }, function error(err) {\n";
        jsScript += "            fail(err);\n";
        jsScript += "            console.error(err);\n";
        jsScript += "            done();\n";
        jsScript += "          }\n";
        jsScript += "          );\n";
        jsScript += "        }, function error(err) {\n";
        jsScript += "          fail(err);\n";
        jsScript += "          console.error(err);\n";
        jsScript += "          done();\n";
        jsScript += "        }\n";
        jsScript += "        );\n";
        jsScript += "      }, function error(err) {\n";
        jsScript += "        fail(err);\n";
        jsScript += "        console.error(err);\n";
        jsScript += "        done();\n";
        jsScript += "      }\n";
        jsScript += "      );\n";
        jsScript += "    }).not.toThrow();\n";
        jsScript += "  });\n";
        
        return jsScript;
    }
    
    private String CreateUnittestLibDescribeTestLibFunctions(String lib, SortedMap<String, List<String>> dicFunctions, String libixas) {
        String jsScript = "";
        
        jsScript += "  describe(\"Test Lib Functions\", function () {\n";
        jsScript += "    describe(\"sol.<PACKAGE>.<LIBMODUL>\", function () {\n";
        
        for (Map.Entry<String, List<String>> entryFunction : dicFunctions.entrySet()) {
            String functionName = entryFunction.getKey();
            List<String> parameters = entryFunction.getValue();
            
            jsScript += "      xit(\"" + functionName + "\", function (done) {\n";
            jsScript += "        expect(function () {\n";
            
            jsScript = parameters.stream().filter((p) -> (p.length() > 0)).map((p) -> "          " + p + " = PVALUE;\n").reduce(jsScript, String::concat);                

            if (libixas.contains("as")) {
                jsScript += "          test.Utils.execute(\"RF_sol_common_service_ExecuteAsAction\", {\n";
                jsScript += "            action: \"sol.unittest.as.services.ExecuteLib\",\n";
                jsScript += "            config: {\n";                
                jsScript += "              className: \"" + lib + "\",\n";
                jsScript += "              classConfig: {},\n";
                jsScript += "              method: \"" + functionName + "\",\n";

                boolean firstitem = true;
                jsScript += "              params: [";
                for (String p : parameters) {
                    if (!firstitem) {
                        jsScript += ", ";
                    }
                    jsScript += p;
                    firstitem = false;
                }
                jsScript += "]\n";
                
                jsScript += "            }\n";                                
                jsScript += "          }).then(function success(jsonResult) {\n";                
                jsScript += "            content = jsonResult.content;\n";
                jsScript += "            if (content.indexOf(\"exception\") != -1) {\n";
                jsScript += "              fail(jsonResult.content);\n";
                jsScript += "            }\n";
                jsScript += "            done();\n";
                jsScript += "          }, function error(err) {\n";
                jsScript += "            fail(err);\n";
                jsScript += "            console.error(err);\n";
                jsScript += "            done();\n";
                jsScript += "          }\n";
                jsScript += "          );\n";
                jsScript += "        }).not.toThrow();\n";
                jsScript += "      });\n";                                    
            } else {
                jsScript += "          test.Utils.execute(\"RF_sol_unittest_service_ExecuteLib\", {\n";
                jsScript += "            className: \"" + lib + "\",\n";
                jsScript += "            classConfig: {},\n";
                jsScript += "            method: \"" + functionName + "\",\n";

                boolean firstitem = true;
                jsScript += "            params: [";
                for (String p : parameters) {
                    if (!firstitem) {
                        jsScript += ", ";
                    }
                    jsScript += p;
                    firstitem = false;
                }
                jsScript += "]\n";
                jsScript += "          }).then(function success(jsonResult) {\n";
                jsScript += "            done();\n";
                jsScript += "          }, function error(err) {\n";
                jsScript += "            fail(err);\n";
                jsScript += "            console.error(err);\n";
                jsScript += "            done();\n";
                jsScript += "          }\n";
                jsScript += "          );\n";
                jsScript += "        }).not.toThrow();\n";
                jsScript += "      });\n";                    
            }
            
        }
        jsScript += "    });\n";
        jsScript += "  });\n";
        
        return jsScript;
    }

    private String CreateUnittestLibAfterAll() {
        String jsScript = "";
        
        jsScript += "  afterAll(function (done) {\n";
        jsScript += "    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;\n";
        jsScript += "    expect(function () {\n";
        jsScript += "      test.Utils.getTempfolder().then(function success(tempfolder) {\n";
        jsScript += "        test.Utils.deleteSord(tempfolder).then(function success1(deleteResult) {\n";        
        jsScript += "          test.Utils.getFinishedWorkflows().then(function success2(wfs) {\n";
        jsScript += "            test.Utils.removeFinishedWorkflows(wfs).then(function success3(removeFinishedWorkflowsResult) {\n";
        jsScript += "              done();\n";
        jsScript += "            }, function error(err) {\n";
        jsScript += "              fail(err);\n";
        jsScript += "              console.error(err);\n";
        jsScript += "              done();\n";
        jsScript += "            }\n";
        jsScript += "            );\n";
        jsScript += "          }, function error(err) {\n";
        jsScript += "            fail(err);\n";
        jsScript += "            console.error(err);\n";
        jsScript += "            done();\n";
        jsScript += "          }\n";
        jsScript += "          );\n";
        jsScript += "        }, function error(err) {\n";
        jsScript += "          fail(err);\n";
        jsScript += "          console.error(err);\n";
        jsScript += "          done();\n";
        jsScript += "        }\n";
        jsScript += "        );\n";
        jsScript += "      }, function error(err) {\n";
        jsScript += "        fail(err);\n";
        jsScript += "        console.error(err);\n";
        jsScript += "        done();\n";
        jsScript += "      }\n";
        jsScript += "      );\n";
        jsScript += "    }).not.toThrow();\n";
        jsScript += "  });\n";
        
        return jsScript;
    }
    
    private String CreateUnittestLibDescribe(String lib, SortedMap<String, List<String>> dicFunctions, String libixas) {
        String varParameters = "";
        for (Map.Entry<String, List<String>> entryFunction : dicFunctions.entrySet()) {
            List<String> parameters = entryFunction.getValue();
            for (String p : parameters) {
                if (!varParameters.contains(p)) {
                    varParameters += ", ";
                    varParameters += p;
                }                
            }            
        }

        String eloPackage = "";
        String eloLibModul = "";        
        try {
            eloPackage = lib.split("\\.")[1];
            eloLibModul = lib.split("\\.")[2];
            if (libixas.contains("as") || libixas.contains("ix")) {
                eloLibModul = lib.split("\\.")[3];    
                switch (eloLibModul) {
                    case "functions":
                    case "renderer":
                    case "actions":
                    case "analyzers":
                    case "collectors":
                    case "executors":
                        eloLibModul = eloLibModul + lib.split("\\.")[4];
                        break;
                }
            }
        } catch (Exception ex){
        }
        
        String jsScript = "";
        jsScript += "\n";        
        
        if (libixas.contains("as")) {
            jsScript += "describe(\"[" + libixas + "] sol.unittest.as.services.sol<PACKAGE><LIBMODUL>\", function () {\n";                        
        } else {
            jsScript += "describe(\"[" + libixas + "] sol.unittest.ix.services.sol<PACKAGE><LIBMODUL>\", function () {\n";            
        }
        
        if (libixas.contains("as")) {
            jsScript += "  var <LIBMODUL>Sord, userName, userInfo, originalTimeout, content" + varParameters + ";\n";            
            
        } else {
            jsScript += "  var <LIBMODUL>Sord, userName, userInfo, originalTimeout" + varParameters + ";\n";            
        }
        
        
        jsScript += "\n";        
        jsScript += CreateUnittestLibBeforeAll();
        jsScript += CreateUnittestLibDescribeTestLibFunctions(lib, dicFunctions, libixas);
        jsScript += CreateUnittestLibAfterAll();
        jsScript += "});";
        
        jsScript = jsScript.replaceAll("<PACKAGE>", eloPackage);
        jsScript = jsScript.replaceAll("<LIBMODUL>", eloLibModul);
        
        return jsScript;
    }
    
    private void CreateUnittestLib(String lib, SortedMap<String, List<String>> dicFunctions, String profileName, String libDir, String libixas) {
        
        String jsScript = CreateUnittestLibDescribe(lib, dicFunctions, libixas);
        SaveUnittestLib(lib, jsScript, profileName, libDir, libixas);
    }

    private void CreateUnittestLibs(SortedMap<String, SortedMap<String, List<String>>> dicLibs, String profileName, String libDir, String libixas) {
        // Debug(dicLibs, "dicLibs");
        dicLibs.entrySet().forEach((entryLib) -> {
            CreateUnittestLib(entryLib.getKey(), entryLib.getValue(), profileName, libDir, libixas);
        });
    }
        
    void CreateUnittest(EloPackage[] eloPackages, String profileName) {
        SortedMap<String, SortedMap<String, List<String>>> dicAlls = new TreeMap<>();
        SortedMap<String, SortedMap<String, List<String>>> dicAllRhinos = new TreeMap<>();
        SortedMap<String, SortedMap<String, List<String>>> dicIndexServerScriptingBases = new TreeMap<>();
        SortedMap<String, SortedMap<String, List<String>>> dicELOasBases = new TreeMap<>();
        
        if (eloPackages.length == 0) {                
            dicAlls = GetLibs(new EloPackage(), "All"); 
            dicAllRhinos = GetLibs(new EloPackage(), "All Rhino"); 
            dicIndexServerScriptingBases = GetLibs(new EloPackage(), "IndexServer Scripting Base"); 
            dicELOasBases = GetLibs(new EloPackage(), "ELOas Base/OptionalJsLibs");             
        } else {
            for (EloPackage eloPackage : eloPackages) {
                SortedMap<String, SortedMap<String, List<String>>> dicAll = GetLibs(eloPackage, "All");    
                SortedMap<String, SortedMap<String, List<String>>> dicAllRhino = GetLibs(eloPackage, "All Rhino");    
                SortedMap<String, SortedMap<String, List<String>>> dicIndexServerScriptingBase = GetLibs(eloPackage, "IndexServer Scripting Base");    
                SortedMap<String, SortedMap<String, List<String>>> dicELOasBase = GetLibs(eloPackage, "ELOas Base/OptionalJsLibs");    
                
                dicAlls.putAll(dicAll);
                dicAllRhinos.putAll(dicAllRhino);
                dicIndexServerScriptingBases.putAll(dicIndexServerScriptingBase);                
                dicELOasBases.putAll(dicELOasBase);                
            }                
        }
        CreateUnittestLibs(dicAlls, profileName, "All", "lib");
        CreateUnittestLibs(dicAllRhinos, profileName, "All Rhino", "lib");
        CreateUnittestLibs(dicIndexServerScriptingBases, profileName, "IndexServer Scripting Base", "libix");
        CreateUnittestLibs(dicELOasBases, profileName, "ELOas Base/OptionalJsLibs", "libas");
        
        JOptionPane.showMessageDialog(null, "Unittests created", "CreateUnittest", JOptionPane.INFORMATION_MESSAGE);
    }

}
