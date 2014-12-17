/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author insane
 */
public class Product implements Serializable, Comparable<Product> {

    private int productid;
    private Type type;
    private int price;
    private String color;
    private int size;
    private Set<ProductInShop> shops = new TreeSet<>();

    public Product() {
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getProductid() {
        return productid;
    }

    public Type getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    public Set<ProductInShop> getShops() {
        return shops;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setShops(Set<ProductInShop> shops) {
        this.shops = shops;
    }

    @Override
    public int compareTo(Product o) {
        if (productid > o.getProductid()) {
            return 1;
        } else if (productid < o.getProductid()) {
            return -1;
        } else {
            return 0;
        }
    }

    public Vector getInfo(){
        Vector v = new Vector();
        
        v.add(productid);
        v.add(type.getName());
        v.add(color);
        v.add(price);
        v.add(size);
        return v;
    }
}
