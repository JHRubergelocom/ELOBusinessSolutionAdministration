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
public class SordComparator implements Comparator {

    public SordComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        Sord s1 = (Sord)o1;
        Integer s1Id = s1.getId();
        String s1Name = s1.getName();
        
        Sord s2 = (Sord)o2;
        Integer s2Id = s2.getId();
        String s2Name = s2.getName();
        
        if (s1Name.equals(s2Name)){
            return s1Id.compareTo(s2Id);
        }
        return s1Name.compareTo(s2Name);        
    }
    
}
