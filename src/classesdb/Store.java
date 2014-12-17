/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import java.io.Serializable;

/**
 *
 * @author insane
 */
public class Store implements Serializable{
    private int storeId;
    private String address;
    private String phone;

    public int getStoreId() {
        return storeId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    

    public String getAddress() {
        return address;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}
