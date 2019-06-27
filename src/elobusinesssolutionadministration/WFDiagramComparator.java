/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.WFDiagram;
import java.util.Comparator;

/**
 *
 * @author ruberg
 */
public class WFDiagramComparator implements Comparator {

    public WFDiagramComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        WFDiagram wf1 = (WFDiagram)o1;
        Integer wf1Id = wf1.getId();
        String wf1Name = wf1.getName();
        
        WFDiagram wf2 = (WFDiagram)o2;
        Integer wf2Id = wf2.getId();
        String wf2Name = wf2.getName();
        
        if (wf1Name.equals(wf2Name)){
            return wf1Id.compareTo(wf2Id);
        }
        return wf1Name.compareTo(wf2Name);        
    }
    
}
