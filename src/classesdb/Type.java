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
public class Type implements Serializable{
    private int typeId;
    private String name;
    private int discount;
    private int parentId;

    public Type() {
    }

    public int getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public int getDiscount() {
        return discount;
    }

    public int getParentId() {
        return parentId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
}
