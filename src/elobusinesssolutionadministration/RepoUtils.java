/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import byps.RemoteException;
import de.elo.ix.client.FindByIndex;
import de.elo.ix.client.FindByType;
import de.elo.ix.client.FindChildren;
import de.elo.ix.client.FindInfo;
import de.elo.ix.client.FindResult;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.LockC;
import de.elo.ix.client.ObjKey;
import de.elo.ix.client.Sord;
import de.elo.ix.client.SordC;
import de.elo.ix.client.SordZ;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ruberg
 */
class RepoUtils {
    public static List<Sord> FindChildren(String objId, IXConnection ixConn, boolean references) {
        System.out.println("FindChildren: objId " + objId + " ixConn " + ixConn);
        FindResult findResult = new FindResult();
        List<Sord> children = new ArrayList<>();
        try {
            ixConn.ix().checkoutSord(objId, SordC.mbAll, LockC.NO);
            
            FindInfo findInfo = new FindInfo();
            FindChildren findChildren = new FindChildren();
            FindByType findByType = new FindByType();
            FindByIndex findByIndex = new FindByIndex();
            Boolean includeReferences = references;
            SordZ sordZ = SordC.mbAll;
            Boolean recursive = true;
            int level = 3;
            ObjKey[] objKeys = new ObjKey[] { };
            findChildren.setParentId(objId);
            findChildren.setMainParent(!includeReferences);
            findChildren.setEndLevel((recursive) ? level : 1);
            findInfo.setFindChildren(findChildren);
            findInfo.setFindByIndex(findByIndex);

            int idx = 0;
            findResult = ixConn.ix().findFirstSords(findInfo, 1000, sordZ);
            while (true) {
                children.addAll(Arrays.asList(findResult.getSords()));
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
    
}
