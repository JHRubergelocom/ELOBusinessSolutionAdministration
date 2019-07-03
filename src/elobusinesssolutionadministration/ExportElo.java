/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.AccessC;
import de.elo.ix.client.AclItem;
import de.elo.ix.client.CheckoutUsersC;
import de.elo.ix.client.DocMask;
import de.elo.ix.client.DocMaskC;
import de.elo.ix.client.DocMaskLine;
import de.elo.ix.client.DocVersion;
import de.elo.ix.client.EditInfo;
import de.elo.ix.client.EditInfoC;
import de.elo.ix.client.FileData;
import de.elo.ix.client.FindChildren;
import de.elo.ix.client.FindInfo;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.FindWorkflowInfo;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.MaskName;
import de.elo.ix.client.Sord;
import de.elo.ix.client.SordC;
import de.elo.ix.client.SordZ;
import de.elo.ix.client.UserInfoC;
import de.elo.ix.client.UserName;
import de.elo.ix.client.WFDiagram;
import de.elo.ix.client.WFDiagramC;
import de.elo.ix.client.WFDiagramZ;
import de.elo.ix.client.WFTypeC;
import de.elo.ix.client.WorkflowExportOptions;
import de.elo.ix.client.WorkflowExportOptionsC;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ruberg
 */
class ExportElo {
    static private final String ARCPATH = "ARCPATH[1]:/Administration/Business Solutions"; 
    static private final boolean REFERENCES = false; 
    
    private final IXConnection ixConn;

    ExportElo(IXConnection ixConn) {
        this.ixConn = ixConn;
    }

    void StartExportElo(String name) {
        try {
            String exportPath = "E:\\Temp\\ExportElo\\" + name;
            File exportDir = new File(exportPath);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }  
            FindChildren(ARCPATH, exportDir, REFERENCES);
            FindWorkflows(exportDir);
            FindDocMasks(exportDir);
            System.out.println("ticket=" + ixConn.getLoginResult().getClientInfo().getTicket());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void FindChildren(String arcPath, File exportPath, boolean exportReferences) {        
        FindResult fr = new FindResult();
        try {
            EditInfo ed = ixConn.ix().checkoutSord(arcPath, EditInfoC.mbOnlyId, LockC.NO);
            
            int parentId = ed.getSord().getId();

            FindInfo fi = new FindInfo();
            fi.setFindChildren(new FindChildren());
            fi.getFindChildren().setParentId(Integer.toString(parentId));
            fi.getFindChildren().setEndLevel(1);
            SordZ sordZ = SordC.mbMin;

            int idx = 0;
            fr = ixConn.ix().findFirstSords(fi, 1000, sordZ);
            while (true) {
                for (Sord sord : fr.getSords()) {
                    boolean isFolder = sord.getType() < SordC.LBT_DOCUMENT;
                    boolean isDocument = sord.getType() >= SordC.LBT_DOCUMENT && sord.getType() <= SordC.LBT_DOCUMENT_MAX;
                    boolean isReference = sord.getParentId() != parentId;

                    boolean doExportScript = false;
                    // Keine Referenzen ausgeben
                    if (!exportReferences) {
                        if (!isReference) {
                            doExportScript = true;
                        }
                    }
                    // Referenzen mit ausgeben
                    else {
                        doExportScript = true;
                    }
                    if (doExportScript) {
                        // Wenn Ordner rekursiv aufrufen
                        if (isFolder) {
                            // Neuen Ordner in Windows anlegen, falls noch nicht vorhanden
                            File subFolderPath = new File(exportPath + "\\" + sord.getName());
                            if (!subFolderPath.exists()) {
                                try {
                                    subFolderPath.mkdirs();
                                } catch (Exception ex) {
                                    System.out.println("Exception mkdir(): " + ex.getMessage() + " " + subFolderPath);
                                    ex.printStackTrace();
                                }
                            }
                            FindChildren(arcPath + "/" + sord.getName(), subFolderPath, exportReferences);
                        }
                        // Wenn Dokument Pfad und Name ausgeben
                        if (isDocument) {
                            File outFile = new File("");
                            try {
                                // Dokument aus Archiv downloaden und in Windows anlegen
                                ed = ixConn.ix().checkoutDoc(Integer.toString(sord.getId()), null, EditInfoC.mbDocument, LockC.NO);
                                DocVersion dv = ed.getDocument().getDocs()[0];
                                outFile = new File(exportPath + "\\" + sord.getName() + "." + dv.getExt());
                                if (outFile.exists()) {
                                    outFile.delete();
                                }
                                ixConn.download(dv.getUrl(), outFile);    
                                System.out.println("Arcpath=" + arcPath + "/" + sord.getName() + "  Maskname=" + sord.getMaskName());
                            } catch (RemoteException ex) {
                                System.out.println("RemoteException: " + ex.getMessage() + " " + outFile);
                                ex.printStackTrace();
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                System.out.println("ArrayIndexOutOfBoundsException: " + ex.getMessage() + " " + outFile);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                if (!fr.isMoreResults()) break;
                idx += fr.getSords().length;
                fr = ixConn.ix().findNextSords(fr.getSearchId(), idx, 1000, sordZ);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    ixConn.ix().findClose(fr.getSearchId());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }            
        }
    } 
    
    private void FindWorkflows(File exportPath) {
        FindWorkflowInfo findWorkflowInfo = new FindWorkflowInfo();
        findWorkflowInfo.setType(WFTypeC.TEMPLATE);
        
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

        for (WFDiagram wf : workflows) {
            ExportWorkflow(exportPath, wf);
        }
        
    }

    private void ExportWorkflow(File exportPath, WFDiagram wf) {
        try {
            WFDiagram wfDiag = ixConn.ix().checkoutWorkflowTemplate(wf.getName(), "", new WFDiagramZ(WFDiagramC.mbId), LockC.NO);
            WorkflowExportOptions workflowExportOptions = new WorkflowExportOptions();
            workflowExportOptions.setFlowId(Integer.toString(wfDiag.getId()));

            workflowExportOptions.setFormat(WorkflowExportOptionsC.FORMAT_JSON);
            FileData fileData = ixConn.ix().exportWorkflow(workflowExportOptions);
            String jsonData = new String(fileData.getData(), "UTF-8");
            jsonData = JsonUtils.formatJsonString(jsonData);  
            
            String dirName = exportPath + "\\Workflows";
            String fileName = wf.getName();
            FileUtils.SaveToFile(dirName, fileName, jsonData, "json");
            System.out.println("Save Workflow: '" + wf.getName() + "'");                        
        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            System.out.println("UnsupportedEncodingException: " + ex.getMessage());
            ex.printStackTrace();
        }
        
    }
    
    private void clearIds(AclItem[] aclItems) {
        int i; 
        AclItem element;

        for (i = 0; i < aclItems.length; i++) {
          element = aclItems[i];
          element.setId(-1);
        }
        
    }

    private String getUserName(int id) throws RemoteException {
        String[] ids = new String[]{id + ""};
        UserName[] userNames = ixConn.ix().getUserNames(ids, CheckoutUsersC.BY_IDS_RAW);
        String name = userNames[0].getName();
        return name;        
    }
    
    private void adjustAcl(AclItem[] aclItems) throws RemoteException {
        int i; 
        AclItem aclItem; 
        String aclName;

        String adminName = getUserName(UserInfoC.ID_ADMINISTRATOR);
        String everyoneName = getUserName(UserInfoC.ID_EVERYONE_GROUP);

        for (i = 0; i < aclItems.length; i++) {
          aclItem = aclItems[i];
          aclName = aclItem.getName();
          if (aclName.equals(adminName)) {
              aclItem.setId(0);
              aclItem.setName("");              
          } else if (aclName.equals(everyoneName)) {
              aclItem.setId(9999);
              aclItem.setName("");              
          }
        }        
    }
    
    private void adjustMask(DocMask dm) throws RemoteException {
        String[] childMaskNames;
        int i; 
        DocMaskLine line;

        dm.setId(-1);
        dm.setTStamp("2018.01.01.00.00.00");

        adjustAcl(dm.getAclItems());

        for (i = 0; i < dm.getLines().length; i++) {
          line = dm.getLines()[i];
          line.setMaskId(-1);
          adjustAcl(line.getAclItems());
        }        
    }
    
    private void ExportDocMask(File exportPath, DocMask dm) {
        try {
            int i;
            dm.setAcl("");
            dm.setDAcl("");
            clearIds(dm.getAclItems());
            clearIds(dm.getDocAclItems());
            for (i = 0; i < dm.getLines().length; i++) {
              DocMaskLine line = dm.getLines()[i];
              line.setAcl("");
              clearIds(line.getAclItems());
            }
            String json = JsonUtils.getJsonString(dm);
            dm = JsonUtils.getDocMask(json);
            adjustMask(dm);
            json = JsonUtils.formatJsonString(json);     
            
            String dirName = exportPath + "\\DocMasks";
            String fileName = dm.getName();
            FileUtils.SaveToFile(dirName, fileName, json, "json");
            System.out.println("Save DocMask: '" + dm.getName() + "'");                        
            
        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void FindDocMasks(File exportPath) {
        String arcPath = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
        try {
            EditInfo ed = ixConn.ix().checkoutSord(arcPath, EditInfoC.mbAll, LockC.NO);
            List<DocMask> dmList = new ArrayList<>();
            for (MaskName mn : ed.getMaskNames()) { 
                boolean canRead = (mn.getAccess() & AccessC.LUR_READ) != 0; 
                System.out.println("id=" + Integer.toString(mn.getId()) +
                    ", name=" + mn.getName() + 
                    ", folderMask=" + Boolean.toString(mn.isFolderMask()) + 
                    ", documentMask=" + Boolean.toString(mn.isDocumentMask()) + 
                    ", searchMask=" + Boolean.toString(mn.isSearchMask()) + 
                    ", canRead=" + Boolean.toString(canRead)); 

                DocMask dm = ixConn.ix().checkoutDocMask(mn.getName(), DocMaskC.mbAll, LockC.NO);
                dmList.add(dm);            
            }
            DocMask[] docMasks = new DocMask[dmList.size()];
            docMasks = dmList.toArray(docMasks);   
            
            for (DocMask dm : docMasks) {
                ExportDocMask(exportPath, dm);                
            }
        } catch (RemoteException ex) {
                ex.printStackTrace();
        }
        
    }

}
