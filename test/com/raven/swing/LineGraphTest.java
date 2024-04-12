/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.raven.swing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author GUEST1
 */
public class LineGraphTest {
    
    public LineGraphTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of setTitle method, of class LineGraph.
     */
    @Test
    public void testSetTitle() {
        System.out.println("setTitle");
        String title = "";
        LineGraph instance = new LineGraph();
        instance.setTitle(title);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setXAxisLabel method, of class LineGraph.
     */
    @Test
    public void testSetXAxisLabel() {
        System.out.println("setXAxisLabel");
        String XAxisLabel = "";
        LineGraph instance = new LineGraph();
        instance.setXAxisLabel(XAxisLabel);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setYAxisLabel method, of class LineGraph.
     */
    @Test
    public void testSetYAxisLabel() {
        System.out.println("setYAxisLabel");
        String YAxisLabel = "";
        LineGraph instance = new LineGraph();
        instance.setYAxisLabel(YAxisLabel);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of plotDataFromMySQL method, of class LineGraph.
     */
    @Test
    public void testPlotDataFromMySQL() {
        System.out.println("plotDataFromMySQL");
        String query = "";
        String xColumn = "";
        String yColumn = "";
        LineGraph instance = new LineGraph();
        instance.plotDataFromMySQL(query, xColumn, yColumn);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
