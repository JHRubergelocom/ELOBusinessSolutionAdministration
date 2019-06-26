/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.DocVersion;
import de.elo.ix.client.EditInfo;
import de.elo.ix.client.EditInfoC;
import de.elo.ix.client.FindChildren;
import de.elo.ix.client.FindInfo;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.Sord;
import de.elo.ix.client.SordC;
import de.elo.ix.client.SordZ;
import java.io.File;

/**
 *
 * @author ruberg
 */
class ExportElo {
    static private final String ARCPATH = "ARCPATH[1]:/Administration/Business Solutions"; 
    static private final boolean REFERENCES = false; 

    static void StartExportElo(IXConnection ixConn, String name) {
        try {
            String exportPath = "E:\\Temp\\ExportElo\\" + name;
            File exportDir = new File(exportPath);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }  
            FindChildren(ixConn, ARCPATH, exportDir, REFERENCES);
            System.out.println("ticket=" + ixConn.getLoginResult().getClientInfo().getTicket());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void FindChildren(IXConnection ixConn, String arcPath, File exportPath, boolean exportReferences) {        
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
                            FindChildren(ixConn, arcPath + "/" + sord.getName(), subFolderPath, exportReferences);
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
}
