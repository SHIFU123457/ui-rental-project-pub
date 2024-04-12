/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.component;

import com.raven.model.ModelClientCard;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author user
 */
public class ClientCard extends javax.swing.JPanel {

    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    private Color color1;
    private Color color2;

    public void setData(ModelClientCard data) {
        lbIcon.setIcon(data.getIcon());
        lbNames.setText(data.getNames());
        lbPhoneNo.setText(data.getPhoneNo());
        lbHouseNo.setText(data.getHouseNo());
    }
    
    public ClientCard() {
        initComponents();
        setOpaque(false);
        color1 = new Color(11, 56, 1);
        color2 = new Color(81, 224, 49);
        lbNames.setForeground(Color.white);
        lbPhoneNo.setForeground(Color.white);
        lbHouseNo.setForeground(Color.white);
        
        lbNames.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            showInfoPanel(e.getComponent());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hideInfoPanel();
        }
        });
    
        lbPhoneNo.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            showInfoPanel(e.getComponent());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hideInfoPanel();
        }
        });
        
        lbIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        // Read the selected image file
                        BufferedImage image = ImageIO.read(selectedFile);
                        // Resize the image to fit available space
                        if(image != null){
                        int width = lbIcon.getWidth();
                        int height = lbIcon.getHeight();
                        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        // Convert the scaled image to ImageIcon
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                       
                        storeImageInDatabase(scaledImage, "png");
                        // Set the scaled icon to lbIcon
                        lbIcon.setIcon(scaledIcon);
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Error updating image", "ERROR!",JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    
    private void storeImageInDatabase(Image image, String format){
         BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage) image;
        } else {
        // Create a new BufferedImage with the same width and height as the Image
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the Image onto the BufferedImage
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }
        
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, format, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();
        
        // Retrieve tenantNo from lbHouseNo
            String houseNo = lbHouseNo.getText();
            String tenantNo = null;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentalproject", "root", "");
               Statement stat = conn.createStatement();
               ResultSet rs = stat.executeQuery("SELECT tenantNo FROM tenants WHERE houseNo = '"+houseNo+"'")) {
               while(rs.next()){
                   tenantNo = rs.getString(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ClientCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        // Update the tenantImage in the database
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentalproject", "root", "");
                PreparedStatement pstmt = conn.prepareStatement("UPDATE tenants SET tenantImage = ? WHERE tenantNo = ?")) {
                pstmt.setBytes(1, imageData);
                pstmt.setString(2, tenantNo);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(ClientCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
        ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        g2.setColor(new Color(255, 255, 255, 50));
        
        g2.fillOval(-150,-120, getHeight(), getHeight());
        g2.fillOval(getWidth() - (getHeight() / 2) - 20, getHeight() / 2 + 20, getHeight(), getHeight());
        super.paintComponent(grphcs);
        
        BufferedImage originalImage = new BufferedImage(lbIcon.getWidth(), lbIcon.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = originalImage.createGraphics();
        lbIcon.paint(g2d);
        g2d.dispose();
    
        BufferedImage circularImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gCircular = circularImage.createGraphics();
        gCircular.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gCircular.setClip(new Ellipse2D.Double(0, 0, originalImage.getWidth(), originalImage.getHeight()));
        gCircular.drawImage(originalImage, 0, 0, null);
        gCircular.dispose();
    
        lbIcon.setIcon(new ImageIcon(circularImage));
    }
    
    private void showInfoPanel(Component component) {
        JPanel infoPanel = new JPanel();
        int x = 0;
        int y = component.getY() + component.getHeight(); // Position below the label
        if (component.getX() + component.getWidth() + infoPanel.getWidth() <= getWidth()) {
            x = component.getX() + component.getWidth(); // Position to the right of the label
        } else {
            x = component.getX() - infoPanel.getWidth(); // Position to the left of the label
        }
        infoPanel.setLocation(x, y);
        add(infoPanel);
        revalidate();
        repaint();
    }
    
    private void hideInfoPanel() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                remove(component);
                revalidate();
                repaint();
                break; // Assuming only one info panel is added at a time
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbIcon = new javax.swing.JLabel();
        lbNames = new javax.swing.JLabel();
        lbPhoneNo = new javax.swing.JLabel();
        lbHouseNo = new javax.swing.JLabel();

        lbIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/client.png"))); // NOI18N
        lbIcon.setText("jLabel1");

        lbNames.setBackground(new java.awt.Color(255, 255, 255));
        lbNames.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbNames.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNames.setText("names");

        lbPhoneNo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbPhoneNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPhoneNo.setText("phoneNo");

        lbHouseNo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbHouseNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbHouseNo.setText("hNo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 35, Short.MAX_VALUE)
                .addComponent(lbIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbPhoneNo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbNames, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbNames, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbPhoneNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbHouseNo, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

//    public void makeCircularIcon() {
//        ImageIcon icon = (ImageIcon) lbIcon.getIcon();
//        Image originalImage = icon.getImage();
//
//        BufferedImage bufferedImage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = bufferedImage.createGraphics();
//        g2d.drawImage(originalImage, 0, 0, null);
//        g2d.dispose();
//
//        BufferedImage circularImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D g = circularImage.createGraphics();
//
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setClip(new Ellipse2D.Double(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()));
//        g.drawImage(bufferedImage, 0, 0, null);
//
//        g.dispose();
//
//        lbIcon.setIcon(new ImageIcon(circularImage));
//    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbHouseNo;
    private javax.swing.JLabel lbIcon;
    private javax.swing.JLabel lbNames;
    private javax.swing.JLabel lbPhoneNo;
    // End of variables declaration//GEN-END:variables
}
