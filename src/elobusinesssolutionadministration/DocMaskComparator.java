/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.DocMask;
import java.util.Comparator;

/**
 *
 * @author ruberg
 */
public class DocMaskComparator implements Comparator {

    public DocMaskComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        DocMask dm1 = (DocMask)o1;
        Integer dm1Id = dm1.getId();
        String dm1Name = dm1.getName();
        
        DocMask dm2 = (DocMask)o2;
        Integer dm2Id = dm2.getId();
        String dm2Name = dm2.getName();
        
        if (dm1Name.equals(dm2Name)){
            return dm1Id.compareTo(dm2Id);
        }
        return dm1Name.compareTo(dm2Name);        
        
    }
    
}
