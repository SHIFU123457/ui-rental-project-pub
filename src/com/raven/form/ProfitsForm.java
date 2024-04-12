/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import com.raven.model.ModelSmallCard;
import com.raven.swing.ScrollBar;
import com.raven.swing.TableHeader;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GUEST1
 */
public class ProfitsForm extends javax.swing.JPanel {

    
    public ProfitsForm() {
        initComponents();
        getGross();
        smallCard1.setColor1(new Color(22,0,31));
        smallCard1.setColor2(new Color(80,1,114));
        smallCard2.setColor1(new Color(22,0,31));
        smallCard2.setColor2(new Color(80,1,114));
        
        table1.setShowHorizontalLines(true);
        table1.setGridColor(new Color(230, 230, 230));
        table1.setRowHeight(40);
        table1.getTableHeader().setReorderingAllowed(false);
        table1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                TableHeader header = new TableHeader(o + "");
                    header.setHorizontalAlignment(JLabel.CENTER);
                return header;
            }
        });
        table1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean selected, boolean bln1, int i, int i1) {
                
                Component com = super.getTableCellRendererComponent(jtable, o, selected, bln1, i, i1);
                com.setBackground(Color.WHITE);
                setBorder(noFocusBorder); //removes the border in each cell if highlighted or not
                if (selected) {
                    com.setForeground(new Color(15, 89, 140)); //highlights row if value is selected
                } else {
                com.setForeground(new Color(102, 102, 102));
                }
                return com;    
            }
        });
        
        smallCard1.setData(new ModelSmallCard(new ImageIcon(getClass().getResource("/com/raven/icon/stock.png")), "Last Month's Gross Profit", "$"+lastMonthsGrossProfit+""));
        smallCard2.setData(new ModelSmallCard(new ImageIcon(getClass().getResource("/com/raven/icon/stock.png")), "This Month's Gross Profit", "$"+thisMonthsGrossProfit+""));
        
        jScrollPane1.setVerticalScrollBar(new ScrollBar());
        jScrollPane1.getVerticalScrollBar().setBackground(Color.WHITE);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        jScrollPane1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
       
         table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (column != -1) { // Ensure the change is not a deletion
                        DefaultTableModel model = (DefaultTableModel) table1.getModel();
                        String columnName = table1.getColumnName(column);
                        Object data = model.getValueAt(row, column);
                        // Call a method to update the database with the changed data
                        updateDatabase(row, columnName, data);
                    }
                }
            }
        });
         
        populateTableFromDatabase();
        
        String query = "SELECT DATE_FORMAT(timePaid, '%Y-%m') AS month, SUM(amount) AS total_payment "
                     + "FROM payments "
                     + "GROUP BY DATE_FORMAT(timePaid, '%Y-%m')";
        lineGraph1.plotDataFromMySQL(query, "month", "total_payment");
        lineGraph1.setChartBackground(new Color(22,0,31));
    }
    
    
    private void populateTableFromDatabase() {
        String driver = "com.mysql.cj.jdbc.Driver";
        // JDBC URL, username, and password
        String url = "jdbc:mysql://localhost:3306/rentalproject";
        String user = "root";
        String password = "";
        
        // SQL query to select data from the database
        String query = "SELECT * FROM payments ORDER BY timePaid DESC";
        
        try {
            Class.forName(driver);
    // Establish connection to the database
            Connection connection = DriverManager.getConnection(url, user, password);

    // Create a statement
            Statement statement = connection.createStatement();

    // Execute the query
            ResultSet resultSet = statement.executeQuery(query);
            
            DefaultTableModel model = (DefaultTableModel) table1.getModel();

    // Iterate through the result set and add rows to the table
            while (resultSet.next()) {
                String paymentID = resultSet.getString(1);
                String houseNo = resultSet.getString(2);
                String paymentType = resultSet.getString(3);
                int amount = resultSet.getInt(4);
                String timeRecorded = (String)resultSet.getString(5);
                String paymentMethod = resultSet.getString(6);
                String timePaid = (String)resultSet.getString(7);
                model.addRow(new Object[]{paymentID, houseNo, paymentType, amount, timeRecorded, paymentMethod, timePaid});
            }

    // Close the outer result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Form_Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateDatabase(int row, String columnName, Object data) {
        // JDBC URL, username, and password
        String url = "jdbc:mysql://localhost:3306/rentalproject";
        String user = "root";
        String password = "";

        // SQL query to update data in the database
        String query = "UPDATE payments SET " + columnName + " = ? WHERE paymentID = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            String paymentID = model.getValueAt(row, 0).toString(); // Assuming paymentID is in the first column

            // Set parameters for the prepared statement
            preparedStatement.setObject(1, data);
            preparedStatement.setString(2, paymentID);

            // Execute the update query
            preparedStatement.executeUpdate();
            
            // Optionally, commit the transaction if needed
            // connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int thisMonthsGrossProfit = 0;
    public static int lastMonthsGrossProfit = 0;
    private void getGross(){
               String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/rentalproject";
        String user = "root";
        String password = "";

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Month starts from 0
        int currentYear = calendar.get(Calendar.YEAR);

        // Calculate last month and year
        int lastMonth = currentMonth - 1;
        int lastYear = currentYear;
        if (lastMonth == 0) {
            lastMonth = 12; // December
            lastYear = currentYear - 1;
        }

        try {
            // Load the MySQL JDBC driver
            Class.forName(driver);

            // Establish connection
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                // Query for this month's total payments
                String thisMonthQuery = "SELECT SUM(amount) AS total_payment " +
                                        "FROM payments " +
                                        "WHERE MONTH(timePaid) = " + currentMonth + " AND YEAR(timePaid) = " + currentYear;
                ResultSet thisMonthResult = statement.executeQuery(thisMonthQuery);
                if (thisMonthResult.next()) {
                    thisMonthsGrossProfit = thisMonthResult.getInt("total_payment");
                    System.out.println("This month's total payments: $" + thisMonthsGrossProfit);
                }

                // Query for last month's total payments
                String lastMonthQuery = "SELECT SUM(amount) AS total_payment " +
                                        "FROM payments " +
                                        "WHERE MONTH(timePaid) = " + lastMonth + " AND YEAR(timePaid) = " + lastYear;
                ResultSet lastMonthResult = statement.executeQuery(lastMonthQuery);
                if (lastMonthResult.next()) {
                    lastMonthsGrossProfit = lastMonthResult.getInt("total_payment");
                    System.out.println("Last month's total payments: $" + lastMonthsGrossProfit);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        smallCard1 = new com.raven.component.smallCard();
        smallCard2 = new com.raven.component.smallCard();
        panelBorder2 = new com.raven.swing.PanelBorder();
        lineGraph1 = new com.raven.swing.LineGraph();
        panelBorder1 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1157, 191));

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder2Layout.createSequentialGroup()
                .addContainerGap(213, Short.MAX_VALUE)
                .addComponent(lineGraph1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder2Layout.createSequentialGroup()
                .addGap(0, 24, Short.MAX_VALUE)
                .addComponent(lineGraph1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelBorder1.setBackground(new java.awt.Color(230, 230, 230));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel1.setText("Payments");

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "paymentID", "houseNo", "paymentType", "amount", "timeRecorded", "paymentMethod", "timePaid"
            }
        ));
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setResizable(false);
            table1.getColumnModel().getColumn(2).setResizable(false);
            table1.getColumnModel().getColumn(3).setResizable(false);
            table1.getColumnModel().getColumn(5).setResizable(false);
            table1.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(smallCard2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(smallCard1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(51, 51, 51)
                        .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(smallCard1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(smallCard2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.LineGraph lineGraph1;
    private com.raven.swing.PanelBorder panelBorder1;
    private com.raven.swing.PanelBorder panelBorder2;
    private com.raven.component.smallCard smallCard1;
    private com.raven.component.smallCard smallCard2;
    private javax.swing.JTable table1;
    // End of variables declaration//GEN-END:variables
}
