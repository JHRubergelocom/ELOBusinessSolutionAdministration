/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.FileData;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.FindWorkflowInfo;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.WFDiagram;
import de.elo.ix.client.WFDiagramC;
import de.elo.ix.client.WFDiagramZ;
import de.elo.ix.client.WFTypeC;
import de.elo.ix.client.WorkflowExportOptions;
import de.elo.ix.client.WorkflowExportOptionsC;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ruberg
 */
public class WfUtils {
    private static WFDiagram[] FindWorkflows(FindWorkflowInfo findWorkflowInfo, IXConnection ixConn) {
        int max = 100;
        int idx = 0;
        FindResult findResult = new FindResult(); 
        List<WFDiagram> wfList = new ArrayList<>();
        WFDiagramZ checkoutOptions = WFDiagramC.mbLean;
        try {
            findResult = ixConn.ix().findFirstWorkflows(findWorkflowInfo, max, checkoutOptions);
            while (true) {
                WFDiagram[] wfArray = findResult.getWorkflows();   
                wfList.addAll(Arrays.asList(wfArray));
                if (!findResult.isMoreResults()) {
                  break;
                }
                idx += wfArray.length;
                findResult = ixConn.ix().findNextWorkflows(findResult.getSearchId(), idx, max);
            }
          } catch (RemoteException ex) {
                ex.printStackTrace();
          } finally {
            if (findResult != null) {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }        
        WFDiagram[] workflows = new WFDiagram[wfList.size()];
        workflows = wfList.toArray(workflows);
        return workflows;        
    }
    
    private static WFDiagram[] GetTemplates(IXConnection ixConn) {
        FindWorkflowInfo info = new FindWorkflowInfo();
        info.setType(WFTypeC.TEMPLATE);
        return FindWorkflows(info, ixConn);
    }

    private static int GetWorkflowTemplateId(String workflowTemplateName, IXConnection ixConn) throws RemoteException {
        WFDiagram wfDiag = ixConn.ix().checkoutWorkflowTemplate(workflowTemplateName, "", new WFDiagramZ(WFDiagramC.mbId), LockC.NO);
        return wfDiag.getId();        
    }

    private static String GetWorkflowAsJsonText(int flowId, IXConnection ixConn) throws RemoteException, UnsupportedEncodingException {
        WorkflowExportOptions workflowExportOptions = new WorkflowExportOptions();
        workflowExportOptions.setFlowId(Integer.toString(flowId));

        workflowExportOptions.setFormat(WorkflowExportOptionsC.FORMAT_JSON);
        FileData fileData = ixConn.ix().exportWorkflow(workflowExportOptions);
        String jsonData = new String(fileData.getData(), "UTF-8");
        return jsonData;
        
    }
    
    private static String ExportWorkflow(int workflowId, IXConnection ixConn) throws RemoteException, UnsupportedEncodingException {
        return GetWorkflowAsJsonText(workflowId, ixConn);
    }
    
    private static String ExportWorkflowTemplate(WFDiagram wf, IXConnection ixConn) throws RemoteException, UnsupportedEncodingException {
        int workflowTemplateId = GetWorkflowTemplateId(wf.getName(), ixConn);  
        return ExportWorkflow(workflowTemplateId, ixConn);
    }
    
    static String[] LoadWorkflowTemplatesAsJsonString(IXConnection ixConn) throws RemoteException, UnsupportedEncodingException{
        WFDiagram[] wfTemplates = GetTemplates(ixConn);
        List<String> wfJsonTexts = new ArrayList<>();        
        for (WFDiagram wf : wfTemplates) {
             String wfJsonText = ExportWorkflowTemplate(wf, ixConn);
             wfJsonText = JsonUtils.formatJsonString(wfJsonText);
             wfJsonTexts.add(wfJsonText);
        }
        String[] wfArray = new String[wfJsonTexts.size()];
        wfArray = wfJsonTexts.toArray(wfArray);
        return wfArray;        
    }
    
    static SortedMap<WFDiagram, SortedMap<Integer, String>> LoadWorkflowLines(IXConnection ixConn, String searchPattern) throws RemoteException, UnsupportedEncodingException {
        SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines = new TreeMap<>(new WFDiagramComparator());
        WFDiagram[] wfTemplates = GetTemplates(ixConn);
        for (WFDiagram wf : wfTemplates) {
            SortedMap<Integer, String> wfLines = new TreeMap<>();
            String wfJsonText = ExportWorkflowTemplate(wf, ixConn);
            wfJsonText = JsonUtils.formatJsonString(wfJsonText);            
            String[] lines = wfJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);
                if (searchPattern.length() > 0) {
                    if (line.contains(searchPattern)) {
                        wfLines.put(linenr, line);                            
                    }                    
                }
                linenr++;
            }
            dicWorkflowLines.put(wf, wfLines);
        }
        return dicWorkflowLines;        
    }

}
