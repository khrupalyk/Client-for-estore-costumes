/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import java.io.*;

/**
 *
 * @author insane
 */
public class ItemOrder implements  Serializable, Comparable<ItemOrder>{
    private int itemOrderId;
    private int count;
    private Product product;
    private int orderId;

    public ItemOrder() {
    }

    public ItemOrder(int itemOrderId, int count, Product product, int orderId) {
        this.itemOrderId = itemOrderId;
        this.count = count;
        this.product = product;
        this.orderId = orderId;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    
    
    public int getItemOrderId() {
        return itemOrderId;
    }

    public int getCount() {
        return count;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setItemOrderId(int itemOrderId) {
        this.itemOrderId = itemOrderId;
    }

    public void setCount(int count) {
        this.count = count;
    }

   

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public int compareTo(ItemOrder o) {
        return 1;
    }
    
    
}
