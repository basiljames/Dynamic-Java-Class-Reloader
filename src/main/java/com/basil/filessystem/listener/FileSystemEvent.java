package com.basil.filessystem.listener;

import java.util.EventObject;

/**
 * This event is created and passed when a file system change is detected.
 * @author Basil James
 */
public class FileSystemEvent extends EventObject{
    private String fileName;
    
    public FileSystemEvent(Object source, String name) {
        super(source);
        this.fileName = name;
    }

    public String getFileName() {
        return fileName;
    }
}
