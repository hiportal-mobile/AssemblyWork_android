package com.skt.pe.util.io;

import java.io.File;
import java.io.FileFilter;


public class PrefixFilter implements FileFilter {

    private String prefix;
    
    public PrefixFilter(String pPrefix) {
        prefix = pPrefix;
    }
    
    public boolean accept(File file) {
        String fileName = file.getName();

        int offset = fileName.indexOf(prefix);
        
        if(offset == 0) {
            return true;
        } else {
            return false;
        }
    }

}