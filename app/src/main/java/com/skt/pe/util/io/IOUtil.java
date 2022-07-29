package com.skt.pe.util.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class IOUtil {

    public static void closeQuietly(InputStream inputStream) {
        try {
            if(inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        try {
            if(outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    private static void closeChannel(FileChannel channel) {
        try {
            if(channel != null) {
                channel.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    public static String doRead(InputStream inputStream) throws IOException, UnsupportedEncodingException {
        return doRead(inputStream, "utf-8");
    }
    
    public static String doRead(InputStream inputStream, String charName) throws IOException, UnsupportedEncodingException {
        String line = "";
        StringBuffer sb = new StringBuffer();
        
        InputStreamReader reader = null;
        BufferedReader    buf    = null;
        try {
            reader = new InputStreamReader(inputStream, charName);
            buf    = new BufferedReader(reader);
            while((line=buf.readLine()) != null) {
                sb.append(line + "\n");
            }
        } finally {
            try {reader.close();} catch(Exception e) {}
            try {buf.close();}    catch(Exception e) {}
        }
        
        return sb.toString();
    }
    
    public static void doWrite(String data, OutputStream output, String encoding) throws IOException {
        if(data != null) {
            if(encoding == null) {
                doWrite(data, output);
            } else {
                output.write(data.getBytes(encoding));
            }
        }
    }

    public static void doWrite(String data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }
    
    public static void doCopy(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        doCopyTransfer(inputStream, outputStream);
    }
    
    public static void doCopyIO(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        byte[] buf = new byte[1024];
        for (int i; (i=inputStream.read(buf))!=-1; ) {
            outputStream.write(buf, 0, i);
        }
    }

    public static void doCopyMap(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        FileChannel fcin  = inputStream.getChannel();
        FileChannel fcout = outputStream.getChannel();
        
        try {
            MappedByteBuffer m = fcin.map(FileChannel.MapMode.READ_ONLY, 0, fcin.size());
            fcout.write(m);
        } finally {
            closeChannel(fcin);
            closeChannel(fcout);
        }
    }
    
    public static void doCopyNIO(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        FileChannel fcin  = inputStream.getChannel();
        FileChannel fcout = outputStream.getChannel();

        try {
            ByteBuffer buf = ByteBuffer.allocate(8192);
            while(true) {
                int ret = fcin.read(buf);
                if(ret == -1)
                    break;
                buf.flip();
                fcout.write(buf);
                buf.clear();
            }
        } finally {
            closeChannel(fcin);
            closeChannel(fcout);
        }
    }
    
    public static void doCopyTransfer(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        FileChannel fcin  = inputStream.getChannel();
        FileChannel fcout = outputStream.getChannel();
        
        try {
            // For Windows OS (64 * 1024 * 1024) - (32 * 1024)
            long maxCount = 67076096L;
            long position = 0;
            while(true) {
                long ret = fcin.transferTo(position, maxCount, fcout);
                if(ret == 0)
                    break;
                
                position += ret;
            }
            
        } finally {
            closeChannel(fcin);
            closeChannel(fcout);
        }
    }
    
}
