package com.skt.pe.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.skt.pe.util.io.ExtensionFilter;
import com.skt.pe.util.io.IOUtil;
import com.skt.pe.util.io.PrefixFilter;


/**
 * 파일 유틸리티
 * 
 * @author  : june
 * @date    : $Date: 2009-07-16 14:31:23 +0900 (목, 16 7 2009) $
 * @id      : $Id: FileUtil.java 1 2009-07-16 05:31:23Z june $
 */
public class FileUtil {
    
    public static FileInputStream openInputStream(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if(!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }
    
    public static FileOutputStream openOutputStream(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if(!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if(parent!=null && !parent.exists()) {
                if(!parent.mkdirs()) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file);
    }

    public static String read(File srcFile) throws IOException {
        if(srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if(!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if(srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        
        return doRead(srcFile);
    }
    
    public static String doRead(File srcFile) throws IOException {
        String ret = "";
        FileInputStream inputStream = new FileInputStream(srcFile);
        try {   
            ret = IOUtil.doRead(inputStream);
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        return ret;
    }
    
    public static void copyFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile, false);
    }
    
    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if(srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if(destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if(!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if(srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if(srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        if(destFile.getParentFile()!=null && !destFile.getParentFile().exists()) {
            if (!destFile.getParentFile().mkdirs()) {
                throw new IOException("Destination '" + destFile + "' directory cannot be created");
            }
        }
        if(destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }

        doCopyFile(srcFile, destFile, preserveFileDate);
    }
    
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if(destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream inputStream = new FileInputStream(srcFile);
        try {   
            FileOutputStream outputStream = new FileOutputStream(destFile);
            try {
                IOUtil.doCopy(inputStream, outputStream);
            } finally {
                IOUtil.closeQuietly(outputStream);
            }
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        
        if(srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }

        if(preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }
    
    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, false);
    }

    public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        if(destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if(destDir.exists() && !destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        
        copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, false);
    }
    
    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir, FilenameFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (int i = 0; i < srcFiles.length; i++) {
                    File copiedFile = new File(destDir, srcFiles[i].getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }
    
    private static void doCopyDirectory(File srcDir, File destDir, FilenameFilter filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
        if(destDir.exists()) {
            if(!destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if(!destDir.mkdirs()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
            if(preserveFileDate) {
                destDir.setLastModified(srcDir.lastModified());
            }
        }
        if(!destDir.canWrite()){
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }

        File[] files = (filter==null ? srcDir.listFiles() : srcDir.listFiles(filter));
        if(files == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        }
        for(int i=0; i<files.length; i++) {
            File copiedFile = new File(destDir, files[i].getName());
            if (exclusionList == null || !exclusionList.contains(files[i].getCanonicalPath())) {
                if (files[i].isDirectory()) {
                    doCopyDirectory(files[i], copiedFile, filter, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(files[i], copiedFile, preserveFileDate);
                }
            }
        }
    }
    
    public static void moveFile(File srcFile, File destFile) throws IOException {
        if(srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if(destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if(!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if(srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if(destFile.exists()) {
            throw new IOException("Destination '" + destFile + "' already exists");
        }
        if(destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }

        boolean rename = srcFile.renameTo(destFile);
        if(!rename) {
            copyFile(srcFile, destFile);
            if(!srcFile.delete()) {
                deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile +
                        "' after copy to '" + destFile + "'");
            }
        }
    }
    
    public static void moveFileToDirectory(File srcFile, File destDir) throws IOException {
        if(srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if(destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        }
        if(!destDir.exists()) {
            destDir.mkdirs();
        }
        if(!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        
        moveFile(srcFile, new File(destDir, srcFile.getName()));
    }
    
    public static void moveDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' is not a directory");
        }
        if(destDir.exists()) {
            throw new IOException("Destination '" + destDir + "' already exists");
        }

        boolean rename = srcDir.renameTo(destDir);
        if (!rename) {
            copyDirectory( srcDir, destDir );
            deleteDirectory( srcDir );
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir +
                        "' after copy to '" + destDir + "'");
            }
        }
    }
    
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message =
                "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }
    
    public static boolean deleteQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception e) {
        }

        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }
    
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent){
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message =
                    "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }
    
    public static void writeString(File file, String data) throws IOException {
        writeString(file, data, null);
    }
    
    public static void writeString(File file, String data, String encoding) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file);
            IOUtil.doWrite(data, out, encoding);
        } finally {
            IOUtil.closeQuietly(out);
        }
    }
    
    public static void writeByteArray(File file, byte[] data) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file);
            out.write(data);
        } finally {
            IOUtil.closeQuietly(out);
        }
    }
	
    public static boolean makeDirectory(File directory) {
        if (!directory.exists())
            return directory.mkdirs();
        else
            return true;
    }
    
	public static boolean makeDirectory(String directoryName) {
        return makeDirectory(new File(directoryName));
    }
   
    public static List<File> getAllFiles(String dirPath) {
        return getAllFiles(new File(dirPath), null);
    }

	public static List<File> getAllFilesExtension(String dirPath, String extension) {
	    return getAllFiles(new File(dirPath), new ExtensionFilter(extension));
	}
	
	public static List<File> getAllFilesPrefix(String dirPath, String prefix) {
        return getAllFiles(new File(dirPath), new PrefixFilter(prefix));
    }

    /**
     * 특정 디렉토리의 파일목록을 필터링해서 얻는다.
     * 단, 마지막 레벨의 파일까지 취합한다.
     * 
     * @param dir
     * @param ff
     * @return
     */
    public static List<File> getAllFiles(File dir, FileFilter ff) {
        List<File> list = new ArrayList<File>();
        
        if(!dir.isDirectory())
            return list;
        
        File[] files = getFiles(dir, ff);
        if(files != null) {
            for(File f : files)
                list.add(f);
        }

        File[] buffer = getDirectories(dir);
        if(buffer != null) {
            for(File f : buffer) {
                List<File> frag = getAllFiles(f, ff);
                list.addAll(frag);
            }
        }

        return list;
    }
	
    public static File[] getFiles(File dir, FileFilter ff) {
        if(ff == null) {
            return dir.listFiles();
        } else {
            return dir.listFiles(ff);
        }
    }

	public static File[] getDirectories(File dir) {
		File[] files = dir.listFiles(new FileFilter(){
		    public boolean accept(File pathname){     
		        return pathname.isDirectory();
		    }
		});
		return files;
	}
    
    public static String getSettingPath(String path, String baseDir) throws FileNotFoundException {
        if(path==null || path.trim().length()==0)
            throw new FileNotFoundException("Path[" + path + "] is not found");
        
        if(path.indexOf(":")>-1 || path.indexOf("\\")==0 || path.indexOf("/")==0)
            return path;
        else 
            return baseDir + File.separator + path;
    }
	
}
