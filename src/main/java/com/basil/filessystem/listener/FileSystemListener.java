package com.basil.filessystem.listener;

import java.util.EventListener;

/**
 * The listener interface. This listener presently handles file changes. Folder 
 * events are not considered
 * @author Basil
 */
public interface FileSystemListener extends EventListener {
    //void folderChanged(FileSystemEvent fse);
    //void folderDeleted(FileSystemEvent fse);
    //void folderAdded(FileSystemEvent fse);
    
    /**
     * This method is triggered when a file is modified. File modification is detected
     * based on the last modified time stamp.
     * @param fse 
     */
    void fileChanged(FileSystemEvent fse);
    
    /**
     * This method is triggered when a new file is added into the monitoring 
     * folder.
     * @param fse 
     */
    void fileAdded(FileSystemEvent fse);
    
    /**
     * This is triggered when a file is deleted.
     * @param fse 
     */
    void fileDeleted(FileSystemEvent fse);
}
