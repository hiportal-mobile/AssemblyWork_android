package com.skt.pe.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;


public class ExtensionFilter implements FileFilter {

    public static final String NONE_STRING         = "-";
    public static final String WILD_STRING         = "*";

    private static final String EXT_LOCK = "LOCK";

    private Set<String> extensions;
    
    public ExtensionFilter(String extension) {
        extensions = new HashSet<String>();
        extensions.add(extension.toUpperCase());
    }
    
    public ExtensionFilter(String[] extension) {
        extensions = new HashSet<String>();
        for(String s : extension) {
            extensions.add(s);
        }
    }
    
    public ExtensionFilter(Set<String> extension) {
        extensions = extension;
    }
    
    public void add(String extension) {
        extensions.add(extension.toUpperCase());
    }
    
    public boolean accept(File file) { 
        if(file.isDirectory())
            return false;
        
        String fileName = file.getName();
        int offset = fileName.lastIndexOf(".");
        String ext = "";
        if(offset >= 0)
            ext = fileName.substring(offset+1).toUpperCase();
        
        if(EXT_LOCK.equals(ext)) {
            return false;
        } else if(extensions.contains(WILD_STRING)) {
            return true;
        } else if(ext.length()==0 && extensions.contains(NONE_STRING)) {
            return true;
        } else {
            return extensions.contains(ext); 
        }
    }

}