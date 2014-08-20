package com.basil.reloaderjvmagent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author Basil_James
 */
public class RefreshTriggerScreen {
    private Window buttonScreen = new Window(null);
    private JButton refreshButton = new JButton();
    
    public RefreshTriggerScreen() {
        initComponents();
    }

    private void initComponents() {
        Dimension dim = new Dimension(15, 15);
        refreshButton.setMinimumSize(dim);
        refreshButton.setMaximumSize(dim);
        buttonScreen.setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-15, 25, 15, 15);
        buttonScreen.setLayout(new BorderLayout());
        buttonScreen.add(refreshButton);        
    }
    
    public void showScreen() {
        buttonScreen.setAlwaysOnTop(true);
        buttonScreen.setVisible(true);
    }
    
    public static void main(String[] args) {
        new RefreshTriggerScreen().showScreen();        
    }
    
    public void setRefreshListener(ActionListener listener) {
        if(listener != null) {
            refreshButton.addActionListener(listener);            
        }
    }
}
