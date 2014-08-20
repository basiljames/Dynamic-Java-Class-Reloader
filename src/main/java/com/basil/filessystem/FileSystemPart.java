package com.basil.filessystem;

import com.basil.filessystem.listener.FileSystemEvent;
import com.basil.filessystem.listener.FileSystemListener;
import com.basil.reloaderjvmagent.Configuration;
import com.basil.reloaderjvmagent.RefreshTriggerScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingUtilities;
 
/**
 * This class monitors the given folder for any file changes. Changes are intimated
 * to the caller via the listener
 * @author Basil
 */
public class FileSystemPart {

    private ArrayList listeners;
    private HashMap indexMap = new HashMap();
    private HashMap indexRefresMap = new HashMap();
    private HashMap folderIndexMap = new HashMap();
    private HashMap folderIndexRefresMap = new HashMap();
    private boolean indexingComplete = false;
    private boolean isRefreshing = false;
    private File passedFolder;
    private FileFilter passedFilter;
    private final int FLAG_FILE_MODIFIED = 1;
    private final int FLAG_FILE_ADDED = 2;
    private final int FLAG_FILE_DELETED = 3;
    private final int FLAG_FOLDER_MODIFIED = 4;
    private final int FLAG_FOLDER_ADDED = 5;
    private final int FLAG_FOLDER_DELETED = 6;
    private int refreshInterval = 5000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FileSystemPart fsp = new FileSystemPart("D:/test", null);
        fsp.addFileSystemListener(new FileSystemListener() {

            public void fileChanged(FileSystemEvent fse) {
                System.out.println("Modified - " + fse.getFileName());
            }

            public void fileAdded(FileSystemEvent fse) {
                System.out.println("Added - " + fse.getFileName());
            }

            public void fileDeleted(FileSystemEvent fse) {
                System.out.println("Deleted - " + fse.getFileName());
            }
        });
    }

    public void addFileSystemListener(FileSystemListener fsl) {
        if (listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(fsl);
    }

    public void removeFileSystemListener(FileSystemListener fsl) {
        if (listeners != null) {
            listeners.remove(fsl);
        }
    }

    public FileSystemPart(final String path, final String filter, int passedInterval) {
        if (passedInterval > 5000) {
            this.refreshInterval = passedInterval;
        }
        passedFolder = new File(path);
        //passedFilter = new FileNameFilter("Class Files", "class");
        new Thread(new Runnable() {

            public void run() {
                long time = System.currentTimeMillis();
                setIndexingComplete(false);
                indexFiles();
                setIndexingComplete(true);
                System.out.println("Folder indexing Complete in " + (System.currentTimeMillis() - time) / 1000 + "s");
                if(Configuration.isAutoRefresh()) {
                    new RefreshThread().start();
                }else {
                    showRefreshButton();
                }
            }
        }).start();
    }

    public FileSystemPart(final String path, final String filter) {
        passedFolder = new File(path);
        new Thread(new Runnable() {

            public void run() {
                long time = System.currentTimeMillis();
                setIndexingComplete(false);
                indexFiles(); 
                setIndexingComplete(true);
                System.out.println("Folder indexing Complete in " + (System.currentTimeMillis() - time) / 1000 + "s");
                if(Configuration.isAutoRefresh()) {
                    new RefreshThread().start();
                }else {
                    showRefreshButton();
                }
            }
        }).start();
    }
    
    private void showRefreshButton() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RefreshTriggerScreen screen = new RefreshTriggerScreen();
                screen.setRefreshListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        doManualRefresh();
                    }
                });  
                screen.showScreen();
            }
        });
    }

    private void invokeListners(int flag, String file) {
        if (listeners != null) {
            FileSystemEvent eachEvent = new FileSystemEvent(this, file);
            for (int i = 0; i < listeners.size(); i++) {
                FileSystemListener eachListner = (FileSystemListener) listeners.get(i);
                switch (flag) {
                    case FLAG_FILE_ADDED:
                        eachListner.fileAdded(eachEvent);
                        break;
                    case FLAG_FILE_DELETED:
                        eachListner.fileDeleted(eachEvent);
                        break;
                    case FLAG_FILE_MODIFIED:
                        eachListner.fileChanged(eachEvent);
                }
            }
        }
    }

    private void setIndexingComplete(boolean b) {
        indexingComplete = b;
    }

    private void indexFiles() {
        doIndexing(passedFolder);
        //System.out.println("folderIndexMap=" + folderIndexMap);
        //System.out.println("indexMap=" + indexMap);
    }

    private void doIndexing(File fileToIndex) {
        File[] files = fileToIndex.listFiles();
        for (int i = 0; i < files.length; i++) {
            File eachFile = files[i];
            if (eachFile.isDirectory()) {
                folderIndexMap.put(eachFile.getAbsolutePath(), eachFile.lastModified());
                doIndexing(eachFile);
            } else {
                indexMap.put(eachFile.getAbsolutePath(), eachFile.lastModified());
            }
        }
    }

    private void refreshFileIndex() {
        //long time = System.currentTimeMillis();
        isRefreshing = true;
        refreshIndex(passedFolder);

        updateFolderDeletions();
        updateFileDeletions();
        folderIndexMap.putAll(folderIndexRefresMap);
        indexMap.putAll(indexRefresMap);
        indexRefresMap.clear();
        folderIndexRefresMap.clear();
        isRefreshing = false;
        //System.out.println("Index refreshing Complete in " + (System.currentTimeMillis() - time) / 1000 + "s");
    }

    private void refreshIndex(File passedFolder) {
        File[] files = passedFolder.listFiles();
        if(files == null) {
            //Evrything inside the folder may be deleted
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File eachFile = files[i];
            String fileName = eachFile.getAbsolutePath();
            long fileLastModified = eachFile.lastModified();
            //System.out.println("fileName=" + fileName);
            if (eachFile.isDirectory()) {                
                refreshIndex(eachFile);                    
            } else {
                if (indexMap.containsKey(fileName)) {
                    if (((Long) indexMap.get(fileName)).longValue() != fileLastModified) {
                        invokeListners(FLAG_FILE_MODIFIED, fileName);
                    }
                    indexMap.remove(fileName);
                } else {
                    invokeListners(FLAG_FILE_ADDED, fileName);
                }
                indexRefresMap.put(fileName, fileLastModified);
            }
        }
    }

    private void refreshIndexWithFolder(File passedFolder) {
        File[] files = passedFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File eachFile = files[i];
            String fileName = eachFile.getAbsolutePath();
            long fileLastModified = eachFile.lastModified();
            //System.out.println("fileName=" + fileName);
            if (eachFile.isDirectory()) {
                if (folderIndexMap.containsKey(fileName)) {
                    if (((Long) folderIndexMap.get(fileName)).longValue() != fileLastModified) {
                        invokeListners(FLAG_FOLDER_MODIFIED, fileName);
                        refreshIndex(eachFile);
                    } else {
                        retainFolderIndex(eachFile);
                    }
                    folderIndexMap.remove(fileName);
                } else {
                    invokeListners(FLAG_FOLDER_ADDED, fileName);
                    refreshIndex(eachFile);
                }
                folderIndexRefresMap.put(fileName, fileLastModified);
            } else {
                if (indexMap.containsKey(fileName)) {
                    if (((Long) indexMap.get(fileName)).longValue() != fileLastModified) {
                        invokeListners(FLAG_FILE_MODIFIED, fileName);
                    }
                    indexMap.remove(fileName);
                } else {
                    invokeListners(FLAG_FILE_ADDED, fileName);
                }
                indexRefresMap.put(fileName, fileLastModified);
            }
        }
    }

    private void retainFolderIndex(File eachFile) {
        String fileName = eachFile.getAbsolutePath();
        //System.out.println("fileName\\ = "+fileName+"\\");
        Iterator fileIterator = indexMap.keySet().iterator();
        ArrayList notChangedList =  new ArrayList();
        while(fileIterator.hasNext()) {
            String eachFileName = (String)fileIterator.next();
            if(eachFileName.indexOf(fileName) > -1) {
                indexRefresMap.put(eachFileName, indexMap.get(eachFileName));
                notChangedList.add(eachFileName);
            }
        }
        
        int size = notChangedList.size();
        for(int i = 0; i < size; i ++) {
            indexMap.remove(notChangedList.get(i));
        }
        notChangedList.clear();

        Iterator folderIterator = folderIndexMap.keySet().iterator();
        while(folderIterator.hasNext()) {
            String eachFolderName = (String)folderIterator.next();
            if(eachFolderName.indexOf(fileName) > -1) {
                folderIndexRefresMap.put(eachFolderName, indexMap.get(eachFolderName));
                notChangedList.add(eachFolderName);
            }
        }
        
        size = notChangedList.size();
        for(int i = 0; i < size; i ++) {
            folderIndexMap.remove(notChangedList.get(i));
        }
    }

    private void updateFolderDeletions() {
        Iterator it = folderIndexMap.keySet().iterator();
        while (it.hasNext()) {
            String file = (String) it.next();
            invokeListners(FLAG_FOLDER_DELETED, file);
        }
        folderIndexMap.clear();
    }

    private void updateFileDeletions() {
        Iterator it = indexMap.keySet().iterator();
        while (it.hasNext()) {
            String file = (String) it.next();
            invokeListners(FLAG_FILE_DELETED, file);
        }
        indexMap.clear();
    }
    
    private void doManualRefresh() {
        if (!isRefreshing) {
            refreshFileIndex();
            System.out.println("Manual refresh complete.");
        }        
    }

    public class RefreshThread extends Thread {

        public void run() {
            while (true) {
                try {
                    sleep(refreshInterval);
                } catch (InterruptedException ex) {
                    System.out.println("ex=" + indexingComplete);
                }
                if (!isRefreshing) {
                    refreshFileIndex();
                }
            }
        }
    }
}
