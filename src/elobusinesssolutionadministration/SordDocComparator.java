/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.Sord;
import java.util.Comparator;

/**
 *
 * @author ruberg
 */
public class SordDocComparator implements Comparator {

    public SordDocComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        SordDoc s1 = (SordDoc)o1;
        Integer s1Id = s1.getId();
        String s1Name = s1.getName();
        
        SordDoc s2 = (SordDoc)o2;
        Integer s2Id = s2.getId();
        String s2Name = s2.getName();
        
        if (s1Name.equals(s2Name)){
            return s1Id.compareTo(s2Id);
        }
        return s1Name.compareTo(s2Name);        
    }
    
}
