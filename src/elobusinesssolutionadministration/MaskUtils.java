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
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 *
 * @author ruberg
 */
class MaskUtils {
    private final IXConnection ixConn;

    MaskUtils(IXConnection ixConn) {
        this.ixConn = ixConn;
    }
    
    private DocMask[] GetDocMasks() throws RemoteException {        
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
    
    private void clearIds(AclItem[] aclItems) {
        for (AclItem aclItem: aclItems) {
          aclItem.setId(-1);
        }        
    }

    private String getUserName(int id) throws RemoteException {
        String[] ids = new String[]{id + ""};
        UserName[] userNames = ixConn.ix().getUserNames(ids, CheckoutUsersC.BY_IDS_RAW);
        String name = userNames[0].getName();
        return name;        
    }
    
    private void adjustAcl(AclItem[] aclItems) throws RemoteException {
        String aclName;
        String adminName = getUserName(UserInfoC.ID_ADMINISTRATOR);
        String everyoneName = getUserName(UserInfoC.ID_EVERYONE_GROUP);
        for (AclItem aclItem: aclItems) {
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
        dm.setId(-1);
        dm.setTStamp("2018.01.01.00.00.00");
        adjustAcl(dm.getAclItems());

        for (DocMaskLine line: dm.getLines()) {
          line.setMaskId(-1);
          adjustAcl(line.getAclItems());
        }        
    }
    
    private String ExportDocMask(DocMask dm) throws RemoteException {
        dm.setAcl("");
        dm.setDAcl("");
        clearIds(dm.getAclItems());
        clearIds(dm.getDocAclItems());
        for (DocMaskLine line: dm.getLines()) {
          line.setAcl("");
          clearIds(line.getAclItems());
        }
        String json = JsonUtils.getJsonString(dm);
        dm = JsonUtils.getDocMask(json);
        adjustMask(dm);
        json = JsonUtils.formatJsonString(json);
        return json;
    }
    
    SortedMap<DocMask, SortedMap<Integer, String>> LoadDocMaskLines(Pattern p) throws RemoteException {
        Comparator<DocMask> byName = Comparator.comparing(dm -> dm.getName());
        Comparator<DocMask> byId = Comparator.comparingInt(dm -> dm.getId());
        Comparator<DocMask> byDocMask = byName.thenComparing(byId);                        
        SortedMap<DocMask, SortedMap<Integer, String>> dicDocMaskLines = new TreeMap<>(byDocMask);
        DocMask[] docMasks = GetDocMasks();
        for (DocMask dm : docMasks) {
            SortedMap<Integer, String> dmLines = new TreeMap<>();
            String dmJsonText = ExportDocMask(dm);
            String[] lines = dmJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);                    
                if (p.toString().length() > 0) {
                    if (p.matcher(line).find()){
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
