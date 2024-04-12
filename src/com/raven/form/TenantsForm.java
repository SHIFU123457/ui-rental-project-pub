/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raven.form;

import com.raven.component.ClientCard;
import com.raven.model.ModelClientCard;
import com.raven.swing.RoundedBorder;
import com.raven.swing.ScrollBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author RAVEN
 */
public class TenantsForm extends javax.swing.JPanel {
    
    
    public TenantsForm() {
        initComponents();
        jScrollPane1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            jScrollPane1.setViewportBorder(BorderFactory.createEmptyBorder());
            int cornerRadius = 15;
            jScrollPane1.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(cornerRadius, Color.WHITE),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jScrollPane1.setVerticalScrollBar(new ScrollBar());
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(207,207,207));
        jScrollPane1.getViewport().setBackground(new Color(207,207,207));
        JPanel p = new JPanel();
        p.setBackground(new Color(207,207,207));
        jScrollPane1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        
        createClientCardsFromDatabase();
    }

    private void createClientCardsFromDatabase() {
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/rentalproject";
    String user = "root";
    String password = "";
    try {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);
        String sql = "SELECT tenantImage, names, phoneNo, houseNo FROM tenants";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Create a panel to hold the ClientCard instances
            JPanel cardsPanel = new JPanel();
            cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));

            // Create a nested panel for horizontal alignment
            JPanel horizontalPanel = new JPanel();
            horizontalPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 67, 65));
            horizontalPanel.setBackground(new Color(255,255,255));

            int count = 0; // Count of ClientCard instances added

            while (rs.next()) {
                Icon icon = retrieveIcon(rs.getBytes("tenantImage"));
                String name = rs.getString("names");
                String phoneNo = rs.getString("phoneNo");
                String houseNo = rs.getString("houseNo");

                ClientCard clientCard = new ClientCard();
                clientCard.setData(new ModelClientCard(icon, name, phoneNo, houseNo));

                // Add the ClientCard to the horizontal panel
                horizontalPanel.add(clientCard);
                count++;

                // If the maximum count for horizontal placement is reached (3), 
                // add the horizontal panel to the cardsPanel and reset the horizontal panel
                if (count == 3) {
                    cardsPanel.add(horizontalPanel);
                    horizontalPanel = new JPanel();
                    horizontalPanel.setBackground(new Color(255,255,255));
                    horizontalPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 67, 65));
                    count = 0;
                }
                
                boolean rentPaid = true;
                String sql2 = "SELECT isPaid FROM houses h JOIN tenants t ON h.houseNo = t.houseNo WHERE h.houseNo = '"+houseNo+"';";
                try(PreparedStatement pst = conn.prepareStatement(sql2);
                        ResultSet rs2 = pst.executeQuery()){
                    while(rs2.next()){
                        rentPaid = rs2.getBoolean(1);
                    }
                }
                if(rentPaid == false){
                    clientCard.setColor1(new Color(59, 1, 1));
                    clientCard.setColor2(new Color(190, 1, 1));
                }
            }

            // If there are any remaining ClientCard instances, add the horizontal panel to the cardsPanel
            if (count > 0) {
                cardsPanel.add(horizontalPanel);
            }

            cardsPanel.setBackground(new Color(255,255,255));
            // Set the panel as the viewport of jScrollPane1
            jScrollPane1.setViewportView(cardsPanel);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException ex) {
        Logger.getLogger(TenantsForm.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    private Icon retrieveIcon(byte[] imageData) {
        if (imageData != null) {
            return new ImageIcon(imageData);
        } else {
            return new ImageIcon(getClass().getResource("/com/raven/icon/client.png")); // Default icon
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(204, 204, 204));

        jScrollPane1.setBackground(new java.awt.Color(207, 207, 207));
        jScrollPane1.setForeground(new java.awt.Color(102, 102, 102));

        jButton1.setBackground(new java.awt.Color(4, 127, 5));
        jButton1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jButton1.setText("ADD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TENANTS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(382, 382, 382)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(363, 363, 363)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(367, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
