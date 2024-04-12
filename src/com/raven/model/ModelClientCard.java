/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.model;

import javax.swing.Icon;

/**
 *
 * @author user
 */
public class ModelClientCard {
    private Icon icon;
    private String names;
    private String phoneNo;
    private String houseNo;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }
    public ModelClientCard(Icon icon, String names, String phoneNo, String houseNo) {
        this.icon = icon;
        this.names = names;
        this.phoneNo = phoneNo;
        this.houseNo = houseNo;
    }

    public ModelClientCard() {
    }
}
