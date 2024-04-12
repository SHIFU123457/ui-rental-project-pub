/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author user
 */
public class SubMenuPanel extends javax.swing.JPanel {

    private DefaultListModel<String> subFunctionalityModel;
    private JList<String> subFunctionalityList;
    
    public SubMenuPanel() {
        initComponents();
        setLayout(new BorderLayout());
        setOpaque(false);
        
        subFunctionalityModel = new DefaultListModel<>();
        subFunctionalityList = new JList<>(subFunctionalityModel);
        JScrollPane scrollPane = new JScrollPane(subFunctionalityList);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, Color.decode("#360033"), 0, getHeight(), Color.decode("#0b8793"));
        //get transition blending color.
        g2.setPaint(g);
        g2.fillRoundRect(10, 0, getWidth() - 20, getHeight(), 5, 5);
        super.paintChildren(grphcs);
    }
    
    public void displaySubFunctionalities(String menuName) {
        // Clear previous sub-functionalities
        subFunctionalityModel.clear();

        // Add sub-functionalities based on the selected menu item
        if (menuName.equals("Finance")) {
            subFunctionalityModel.addElement("Payments");
            subFunctionalityModel.addElement("Expenditures");
            subFunctionalityModel.addElement("Discounts");
            // Add more sub-functionalities as needed
        } else if(menuName.equals("Management")) {
            subFunctionalityModel.addElement("Pricing");
            subFunctionalityModel.addElement("Agents");
            subFunctionalityModel.addElement("Employees");
            subFunctionalityModel.addElement("Structure");
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
