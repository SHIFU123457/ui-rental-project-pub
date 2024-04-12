/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author user
 */
public class RoundedBorder extends AbstractBorder {
    private final int cornerRadius;
    private final Color color;
    public RoundedBorder(int cornerRadius, Color color) {
        this.cornerRadius = cornerRadius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);
        g2d.dispose();
    }
}