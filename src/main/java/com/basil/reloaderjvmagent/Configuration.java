package com.basil.reloaderjvmagent;
/**
 * Configuration class for RCR
 * @author Basil_James
 */
public class Configuration {
    private static final int MIN_REFRESH_INTERVAL = 5000;
    private static final String PROP_REFRESH_INTERVAL = "agent.refreshinterval";    
    private static final String PROP_REFRESH_DIR = "agent.refreshdir"; 
    private static final String PROP_AUTO_REFRESH = "agent.autorefresh"; 
        
    public static int getRefreshInterval() {
        String refreshInterval = System.getProperty(PROP_REFRESH_INTERVAL);
        int interval = 0;
        if(refreshInterval != null) {
            try {
                interval = Integer.parseInt(refreshInterval);
            }catch(Exception e) {
                System.out.println("e"+e);
            }            
        }
        return interval < MIN_REFRESH_INTERVAL ? MIN_REFRESH_INTERVAL : interval;
    }
    
    public static String[] getRefreshDir() {
        String refreshDir = System.getProperty(PROP_REFRESH_DIR);
        assert refreshDir != null : "Valid refresh directory is not specified";
        return refreshDir.split(";");
    } 
    
    public static boolean isAutoRefresh() {
        String autoRefresh = System.getProperty(PROP_AUTO_REFRESH);
        boolean isAutoRefresh = true;
        if(autoRefresh != null) {
            try {
                isAutoRefresh = Boolean.parseBoolean(autoRefresh);
            }catch(Exception e) {
                System.out.println("    e"+e);
            }             
        }       
        System.out.println("isAutoRefresh="+isAutoRefresh);
        return isAutoRefresh;
    }
}
 