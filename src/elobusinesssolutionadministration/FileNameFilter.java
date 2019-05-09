/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elobusinesssolutionadministration;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author ruberg
 */
public class FileNameFilter implements FilenameFilter {
    private final String filter;
    
    public FileNameFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (name.contains(filter)) {
            return !name.contentEquals(".git");
        }
        return false;
    }    
}
