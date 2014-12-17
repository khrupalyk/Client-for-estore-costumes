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
public class ProductInShop implements Serializable,Comparable<ProductInShop>{
    
    private Product product;
    private Store store;
    private int count;

    public Product getProduct() {
        return product;
    }

    public Store getStore() {
        return store;
    }

    public int getCount() {
        return count;
    }

    public void setProduct(Product productId) {
        this.product = productId;
    }

    public void setStore(Store storesId) {
        this.store = storesId;
    }

    public void setCount(int count) {
        this.count = count;
    }
    @Override
    public int compareTo(ProductInShop o) {
        return 1;
    }
}
