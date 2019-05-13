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
import de.elo.ix.client.FindByIndex;
import de.elo.ix.client.FindChildren;
import de.elo.ix.client.FindInfo;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.Sord;
import de.elo.ix.client.SordC;
import de.elo.ix.client.SordZ;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ruberg
 */
class RepoUtils {
    public static Sord[] FindChildren(String objId, IXConnection ixConn, boolean references) {
        System.out.println("FindChildren: objId " + objId + " ixConn " + ixConn);
        FindResult findResult = new FindResult();
        Sord[] children = new Sord[]{};
        try {
            ixConn.ix().checkoutSord(objId, SordC.mbAll, LockC.NO);
            
            FindInfo findInfo = new FindInfo();
            FindChildren findChildren = new FindChildren();
            FindByIndex findByIndex = new FindByIndex();
            Boolean includeReferences = references;
            SordZ sordZ = SordC.mbAll;
            Boolean recursive = true;
            int level = 3;
            findChildren.setParentId(objId);
            findChildren.setMainParent(!includeReferences);
            findChildren.setEndLevel((recursive) ? level : 1);
            findInfo.setFindChildren(findChildren);
            findInfo.setFindByIndex(findByIndex);

            int idx = 0;
            findResult = ixConn.ix().findFirstSords(findInfo, 1000, sordZ);
            while (true) {
                children = findResult.getSords();
                if (!findResult.isMoreResults()) {
                    break;
                }
                idx += findResult.getSords().length;
                findResult = ixConn.ix().findNextSords(findResult.getSearchId(), idx, 1000, sordZ);
            }
            
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            if (findResult != null)
            {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return children;
    }
    
    static String DownloadDocumentToString (Sord s, IXConnection ixConn) {
        String docText = "";
        try {
            String objId = s.getId() + "";   
            String line;            
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();                    
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                    
                    while ((line = in.readLine()) != null) {
                        System.out.println("Gelesene Zeile: " + line);
                        docText = docText.concat(line);
                    }                       
                } catch (FileNotFoundException ex) {    
                    ex.printStackTrace();
                } catch (IOException ex) {            
                    ex.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                docText = docText.replaceAll(bom, "");
                docText = docText.replaceAll("\b", "");
                docText = docText.replaceAll("\n", "");
            }            
        } catch (RemoteException ex) {
            ex.printStackTrace();            
        }
        return docText;
    }

    static String[] LoadTextDocs(String parentId, IXConnection ixConn) throws RemoteException {
        Sord[] sordRFInfo = RepoUtils.FindChildren(parentId, ixConn, true);
        List<String> docTexts = new ArrayList<>();        
        for (Sord s : sordRFInfo) {
            String docText = DownloadDocumentToString(s, ixConn);
            docTexts.add(docText);
        }
        String[] docArray = new String[docTexts.size()];
        docArray = docTexts.toArray(docArray);
        return docArray;        
    }
}
