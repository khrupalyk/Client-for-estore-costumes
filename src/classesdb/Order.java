/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import java.io.*;
import java.util.*;

/**
 *
 * @author insane
 */
public class Order implements Serializable{
    private int orderId;
    private String status;
    private Buyers buyer;
    private Set<ItemOrder> itemOrders = new TreeSet<>();
    private User user;

    public Order() {
    }

    public Order(int orderId, String status, Buyers buyer) {
        this.orderId = orderId;
        this.status = status;
        this.buyer = buyer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Set<ItemOrder> getItemOrders() {
        return itemOrders;
    }

    public void setItemOrders(Set<ItemOrder> itemOrders) {
        this.itemOrders = itemOrders;
    }
    

    public int getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public Buyers getBuyer() {
        return buyer;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBuyer(Buyers buyer) {
        this.buyer = buyer;
    }
    
    
}
