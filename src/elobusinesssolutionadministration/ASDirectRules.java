/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import java.io.StringReader;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author ruberg
 */
class ASDirectRules {
    static SortedMap<String, Boolean> GetRules(IXConnection ixConn, String[] jsTexts, EloPackage eloPackage) {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + eloPackage.getFolder() + "/ELOas Base/Direct";
        if (eloPackage.getFolder().equals("")) {
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/ELOas Base/Direct";
        }
        Sord[] sordRuleInfo = RepoUtils.FindChildren(parentId, ixConn, true);
        SortedMap<String, Boolean> dicRules = new TreeMap<>();
        for(Sord s : sordRuleInfo) {            
            try {
                String xmlText = RepoUtils.DownloadDocumentToString (s, ixConn);             
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                InputSource source = new InputSource(new StringReader(xmlText));            
                String rulesetname = xpath.evaluate("ruleset/base/name", source);
                if (!dicRules.containsKey(rulesetname)) {
                    boolean match = Unittests.Match(ixConn, rulesetname, eloPackage, jsTexts);
                    dicRules.put(rulesetname, match);
                }
            } catch (XPathExpressionException ex) {
                System.err.println("XPathExpressionException: " +  ex.getMessage()); 
                ex.printStackTrace();
            }
        }
        return dicRules;
    }    
}
