/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import static com.raven.form.ProfitsForm.lastMonthsGrossProfit;
import static com.raven.form.ProfitsForm.thisMonthsGrossProfit;
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
import java.time.YearMonth;
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
public class NetProfitsForm extends javax.swing.JPanel {
    double thisMonthsNetProfit = calculateNetProfit(YearMonth.now());
    double lastMonthsNetProfit = calculateNetProfit(YearMonth.now().minusMonths(1));
    
    /**
     * Creates new form NetProfitsForm
     */
    public NetProfitsForm() {
        initComponents();
        
        smallCard1.setColor1(new Color(0,58,40));
        smallCard1.setColor2(new Color(1,138,96));
        smallCard2.setColor1(new Color(0,58,40));
        smallCard2.setColor2(new Color(1,138,96));
        
        smallCard1.setData(new ModelSmallCard(new ImageIcon(getClass().getResource("/com/raven/icon/profit.png")), "Last Month's Net Profit", "$"+lastMonthsNetProfit+""));
        smallCard2.setData(new ModelSmallCard(new ImageIcon(getClass().getResource("/com/raven/icon/profit.png")), "This Month's Net Profit", "$"+thisMonthsNetProfit+""));
        
        
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

        String query = "SELECT month,\n" +
                "    CASE \n" +
                "        WHEN net_profit < 0 THEN 0 \n" +
                "        ELSE net_profit \n" +
                "    END AS net_profit\n" +
                "FROM (\n" +
                "    SELECT \n" +
                "        DATE_FORMAT(p.timePaid, '%Y-%m') AS month, \n" +
                "        SUM(p.amount) AS total_payment,\n" +
                "        COALESCE(SUM(e.amount), 0) AS total_expenditure,\n" +
                "        SUM(p.amount) - COALESCE(SUM(e.amount), 0) AS net_profit \n" +
                "    FROM \n" +
                "        payments p\n" +
                "    LEFT JOIN \n" +
                "        expenditures e ON YEAR(p.timePaid) = YEAR(e.timePaid) AND MONTH(p.timePaid) = MONTH(e.timePaid)\n" +
                "    GROUP BY \n" +
                "        DATE_FORMAT(p.timePaid, '%Y-%m')\n" +
                ") AS subquery;";

        lineGraph1.plotDataFromMySQL(query, "month", "net_profit");
        lineGraph1.setChartBackground(new Color(0,58,40));
    }
    
    private void populateTableFromDatabase() {
        String driver = "com.mysql.cj.jdbc.Driver";
        // JDBC URL, username, and password
        String url = "jdbc:mysql://localhost:3306/rentalproject";
        String user = "root";
        String password = "";
        
        // SQL query to select data from the database
        String query = "SELECT expenditureID, expenditureType, amount, timeRecorded, timePaid FROM expenditures ORDER BY timePaid DESC"; //add userID interfacing.
        
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
                String expenditureID = resultSet.getString(1);
                String expenditureType = resultSet.getString(2);
                int amount = resultSet.getInt(3);
                String timeRecorded = (String)resultSet.getString(4);
                String timePaid = (String)resultSet.getString(5);
                model.addRow(new Object[]{expenditureID, expenditureType, amount, timeRecorded, timePaid});
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
        String query = "UPDATE expenditures SET " + columnName + " = ? WHERE expenditureID = ?";

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
    
    private static double calculateNetProfit(YearMonth yearMonth) {
        double totalPayments = getTotalPayments(yearMonth);
        double totalExpenditures = getTotalExpenditures(yearMonth);
        return totalPayments - totalExpenditures;
    }
    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/rentalproject";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static double getTotalPayments(YearMonth yearMonth) {
        double totalPayments = 0;
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT SUM(amount) FROM payments WHERE MONTH(timePaid) = ? AND YEAR(timePaid) = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, yearMonth.getMonthValue());
                stmt.setInt(2, yearMonth.getYear());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalPayments = rs.getDouble(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPayments;
    }
    
    private static double getTotalExpenditures(YearMonth yearMonth) {
        double totalExpenditures = 0;
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT SUM(amount) FROM expenditures WHERE MONTH(timePaid) = ? AND YEAR(timePaid) = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, yearMonth.getMonthValue());
                stmt.setInt(2, yearMonth.getYear());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalExpenditures = rs.getDouble(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalExpenditures;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        smallCard1 = new com.raven.component.smallCard();
        smallCard2 = new com.raven.component.smallCard();
        panelBorder2 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        panelBorder1 = new com.raven.swing.PanelBorder();
        lineGraph1 = new com.raven.swing.LineGraph();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Expenditure");

        table1.setBackground(new java.awt.Color(255, 255, 255));
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "expenditureID", "expenditureID", "amount", "timeRecorded", "timePaid"
            }
        ));
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setResizable(false);
            table1.getColumnModel().getColumn(1).setResizable(false);
            table1.getColumnModel().getColumn(2).setResizable(false);
            table1.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineGraph1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineGraph1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(smallCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(smallCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(smallCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(smallCard2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
