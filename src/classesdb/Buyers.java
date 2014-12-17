/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import java.io.*;
import java.util.Vector;

/**
 *
 * @author insane
 */
public class Buyers implements Serializable {

    private int buyersId;
    private String firstName;
    private String lastName;
    private String phone;
    private int discount;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Buyers() {
    }

    public Buyers(int buyersId, String firstName, String lastName, String phone) {
        this.buyersId = buyersId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBuyersId() {
        return buyersId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setBuyersId(int buyersId) {
        this.buyersId = buyersId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Vector getInfo() {
        Vector v = new Vector();
        v.add(buyersId);
        v.add(firstName);
        v.add(lastName);
        v.add(phone);
        v.add(discount);

        return v;
    }
}
