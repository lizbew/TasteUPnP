package com.viifly.taste.fileshare;

import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class NanoFileUploadServer extends NanoHTTPD {
    private NanoFileUpload uploader;

    private String localStoreDir;

    public NanoFileUploadServer(int port, String localStoreDir) {
        super(port);
        uploader = new NanoFileUpload(new DiskFileItemFactory());
        this.localStoreDir = localStoreDir;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "done";

        if (NanoFileUpload.isMultipartContent(session)) {
            try {
                List<FileItem> files =  uploader.parseRequest(session);
                if (files != null && files.size() >0) {
                    saveFileItemToDisk(files.get(0));
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
                msg = "failed";
            }
        } else {
            msg = "Support POST method to upload file";
        }

        return newFixedLengthResponse(msg);
    }

    private boolean saveFileItemToDisk(FileItem fileItem) {
        try {
            String name = new File(fileItem.getName()).getName();
            File dirFile = new File(localStoreDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File f = new File(dirFile, name);
            fileItem.write(f);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

}
