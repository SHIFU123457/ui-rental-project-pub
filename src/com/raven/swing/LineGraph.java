/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author GUEST1
 */
public class LineGraph extends JPanel{
    private XYSeries series;
    private ChartPanel chartPanel;
    private JScrollPane scrollPane;
    private XYPlot plot;
    
    public LineGraph() {
        series = new XYSeries("Data");
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Gross Profit", "YEAR", "AMOUNT", dataset, PlotOrientation.VERTICAL, true, true, false);
        
        plot = (XYPlot) chart.getPlot();

        chartPanel = new ChartPanel(chart);
        chartPanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                JFrame frame = new JFrame();
                JPanel panel = new JPanel();
                frame.setSize(900,700);
                chartPanel.setPreferredSize(new Dimension(850,650));
                panel.add(chartPanel);
                frame.add(panel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });
        chartPanel.setPreferredSize(new Dimension(510,280));
        add(chartPanel);
    }
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
    public void setChartBackground(Color color){
        plot.getRenderer().setSeriesPaint(0, color);
    } 

    public void plotDataFromMySQL(String query, String xColumn, String yColumn) {
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/rentalproject";
    String user = "root";
    String password = "";

    try {
        // Load the MySQL JDBC driver
        Class.forName(driver);

        // Establish connection
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Clear existing data
            series.clear();

            // Iterate through the result set and add data points to the series
            while (resultSet.next()) {
                 String xValue = resultSet.getString(xColumn); // Assuming xColumn is a string representing a date
                double yValue = resultSet.getDouble(yColumn);
                
                // Parse the date string to obtain the year and month
                String[] dateParts = xValue.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                double xDoubleValue = year + (double) month / 100; // Convert the date to a numeric value

                series.add(xDoubleValue, yValue);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
        }
    } catch (ClassNotFoundException e) {
        // Handle class not found exception
        e.printStackTrace();
    }
}
}
