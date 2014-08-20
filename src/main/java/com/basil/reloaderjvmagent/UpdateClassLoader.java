/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.basil.reloaderjvmagent;

import com.basil.filessystem.FileSystemPart;
import com.basil.filessystem.listener.FileSystemEvent;
import com.basil.filessystem.listener.FileSystemListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Basil
 */
public class UpdateClassLoader {

    private static UpdateClassLoader loader;
    private static Instrumentation inst;
    private String refreshDir;
    private URLClassLoader customClassLoader;
    private static ClassLoader cl = null;

    public static UpdateClassLoader getInstance() {
        return loader == null ? loader = new UpdateClassLoader() : loader;
    }

    public static void setInstrumentation(Instrumentation val) {
        inst = val;
    }
    
    public static void setAppClassLoader(ClassLoader clo) {
        //System.out.println("ClassLoader is set");
        cl = clo;
    }

    public void checkAndReloadClass(String classFileName) {
        String className = getClassName(classFileName);
        byte[] newClassBytes = readNewClass(classFileName);
        if (newClassBytes != null) {
            Class c = null;
            //try {
                Map m = Thread.getAllStackTraces();
                Iterator it = m.keySet().iterator();
                int threadCount = 0;
                while (it.hasNext()) {
                    Thread th = (Thread) it.next();
                    try {
                         if(th.getContextClassLoader() != null) {
                            c = th.getContextClassLoader().loadClass(className);
                            //System.out.println("Loaded !!!"+c);
                            //System.out.println("Thread ClassLoader = " + th.getContextClassLoader());
                            //System.out.println(threadCount++ + " " + th.getName());
                            if(customClassLoader == null) {
                                initUpdateClassLoader(th.getContextClassLoader());
                            }
                            break;
                         }
                    } catch (ClassNotFoundException ex) {
                        //ex.printStackTrace();
                        //System.out.println(ex);
                    }

                }
                //c = Thread.currentThread().getContextClassLoader().loadClass(className);
            //c = ClassLoader.getSystemClassLoader().loadClass(className);
            //c = Class.forName(className);
            //} catch (ClassNotFoundException ex) {
            //    System.out.println(UpdateClassLoader.class.getName() + ex);
            //}
            if(c == null) {
                try {
                    c = cl.loadClass(className);
                    //System.out.println("Loaded by set loader!!!"+c);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            }
                
            if (c != null) {
                doRedefenition(c, className, newClassBytes);
                //System.out.println("Reload complete:'" + className);
            } else {
                System.out.println("New class Found -->'" + className);
                System.out.println("'. This version does not support new classes.");
            //Class newClass = defineClass(className, newClassBytes, 0, newClassBytes.length);
            //System.out.println("New Loader ---"+ newClass.getClassLoader());
            //resolveClass(newClass);
            //doRedefenition(newClass, className, newClassBytes);
            //try {
            //    ClassLoader.getSystemClassLoader().loadClass(className);
            //} catch (ClassNotFoundException ex) {
            //    Logger.getLogger(UpdateClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            //}
            }
        }
    }

    private UpdateClassLoader() {
        //super(ClassLoader.getSystemClassLoader());
        refreshDir = Configuration.getRefreshDir()[0];
        if (refreshDir == null) {
            System.out.println("----------");
            System.out.println("No directory mentioned !!!");
            System.out.println("Mention the directory to be monitored as -Dagent.refreshdir=D:/xyz/xyz");
            System.out.println("----------");
        } else {
            System.out.println("----------");
            System.out.println("Monitoring the directory '" + refreshDir + "'");
            System.out.println("Any changes to this folder contents will be monitored and reloaded by the Agent");
            System.out.println("----------");
        }
        // correct this.. each file can have separate classloader
        int refreshInterval = Configuration.getRefreshInterval();
        
        FileSystemPart fsp = new FileSystemPart(refreshDir, null, refreshInterval);
        fsp.addFileSystemListener(new FileSystemListener() {

            public void fileChanged(FileSystemEvent fse) {
                String fileName = fse.getFileName();
                System.out.println("Modified - " + getFormattedName(fileName));
                checkAndReloadClass(fileName);
            }

            public void fileAdded(FileSystemEvent fse) {
                String fileName = fse.getFileName();
                System.out.println("Added - " + getFormattedName(fileName));
                checkAndReloadClass(fileName);
            }

            public void fileDeleted(FileSystemEvent fse) {
                System.out.println("Deleted - " + getFormattedName(fse.getFileName()));
            }
        });
    }
    
    /**
     * Simple formatting logic to truncate filenames with long path.
     * @param rawString
     * @return 
     */
    private String getFormattedName(String rawString) {
        if(rawString.length() > 75) {
            return ".."+rawString.substring(rawString.indexOf("\\", rawString.length() - 73), rawString.length());
        }else {
            return rawString;
        }
    }

    private void doRedefenition(Class classObj, String className, byte[] classBytes) {
        //SimpleTransformer_1 st = new SimpleTransformer_1();
        try {
            //byte[] newClass = st.transform(ClassLoader.getSystemClassLoader(), className, null, null, classBytes);
            //System.out.println(className + " ClassLoader is " + classObj.getClassLoader());
            //byte[] newClass = st.transform(classObj.getClassLoader(), className, null, null, classBytes);
            ClassDefinition[] loaderClassDef = new ClassDefinition[]{new ClassDefinition(classObj, classBytes)};
            inst.redefineClasses(loaderClassDef);
        } catch (ClassNotFoundException ex) {
            System.out.println(UpdateClassLoader.class.getName() + ex);
        } catch (UnmodifiableClassException ex) {
            System.out.println(UpdateClassLoader.class.getName() + ex);
//        } catch (IllegalClassFormatException ex) {
//            System.out.println(UpdateClassLoader.class.getName() + ex);
        } catch (Exception ex) {
            System.out.println(UpdateClassLoader.class.getName() + ex);
        }
    }

    private String getClassName(String classFileName) {
        classFileName = classFileName.substring(refreshDir.length() + 1);
        classFileName = classFileName.replace('\\', '.');
        classFileName = classFileName.replaceAll(".class", "");
        //System.out.println("Derived className = "+classFileName);
        return classFileName;
    }

    private void initUpdateClassLoader(ClassLoader contextClassLoader) {
        //URL[] urls = new URL[1];
        //URL updateDir = new URL
        //customClassLoader = new URLClassLoader (contextClassLoader);
    }

    private byte[] readNewClass(String fileName) {
        //Class c = findLoadedClass(name);
        byte[] result = null;
        //if (c == null) {
        //    return result;
        //}
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            result = new byte[(int) file.length()];
            file.readFully(result);
            file.close();
        } catch (IOException e) {
            System.out.println("e");
        }
        return result;
    }
}
