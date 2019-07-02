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
import de.elo.ix.client.EditInfo;
import de.elo.ix.client.EditInfoC;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.MaskName;
import de.elo.ix.client.UserInfoC;
import de.elo.ix.client.UserName;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ruberg
 */
class MaskUtils {
    private static DocMask[] GetDocMasks(IXConnection ixConn) throws RemoteException {        
        String arcPath = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
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
        return docMasks;           
    }
    
    private static void clearIds(AclItem[] aclItems) {
        int i; 
        AclItem element;

        for (i = 0; i < aclItems.length; i++) {
          element = aclItems[i];
          element.setId(-1);
        }
        
    }

    private static String getUserName(int id, IXConnection ixConn) throws RemoteException {
        String[] ids = new String[]{id + ""};
        UserName[] userNames = ixConn.ix().getUserNames(ids, CheckoutUsersC.BY_IDS_RAW);
        String name = userNames[0].getName();
        return name;        
    }
    
    private static void adjustAcl(AclItem[] aclItems, IXConnection ixConn) throws RemoteException {
        int i; 
        AclItem aclItem; 
        String aclName;

        String adminName = getUserName(UserInfoC.ID_ADMINISTRATOR, ixConn);
        String everyoneName = getUserName(UserInfoC.ID_EVERYONE_GROUP, ixConn);

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
    
    private static void adjustMask(DocMask dm, IXConnection ixConn) throws RemoteException {
        String[] childMaskNames;
        int i; 
        DocMaskLine line;

        dm.setId(-1);
        dm.setTStamp("2018.01.01.00.00.00");

        adjustAcl(dm.getAclItems(), ixConn);

        for (i = 0; i < dm.getLines().length; i++) {
          line = dm.getLines()[i];
          line.setMaskId(-1);
          adjustAcl(line.getAclItems(), ixConn);
        }        
    }
    
    private static String GetDocMaskAsJsonText(DocMask dm, IXConnection ixConn) throws RemoteException {
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
        adjustMask(dm, ixConn);
        json = JsonUtils.formatJsonString(json);
        return json;
    }
    
    private static String ExportDocMask(DocMask dm, IXConnection ixConn) throws RemoteException {
        return GetDocMaskAsJsonText(dm, ixConn);
    }

    static SortedMap<DocMask, SortedMap<Integer, String>> LoadDocMaskLines(IXConnection ixConn, String searchPattern) throws RemoteException {
        SortedMap<DocMask, SortedMap<Integer, String>> dicDocMaskLines = new TreeMap<>(new DocMaskComparator());
        DocMask[] docMasks = GetDocMasks(ixConn);
        for (DocMask dm : docMasks) {
            SortedMap<Integer, String> dmLines = new TreeMap<>();
            String dmJsonText = ExportDocMask(dm, ixConn);
            String[] lines = dmJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);
                if (searchPattern.length() > 0) {
                    if (line.contains(searchPattern)) {
                        dmLines.put(linenr, line);                            
                    }                    
                }
                linenr++;
            }
            dicDocMaskLines.put(dm, dmLines);
        }
        return dicDocMaskLines;                
    }

}
