/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author insane
 */
public abstract class Constants {
    public static final int PORT = 1234;
    public static final String HOST = "localhost";
    
    public static final int AUTHENTICATION_SUCCESS = 1;
    public static final int AUTHENTICATION_FAILED = 2;
    public static final int GET_USERS_LIST = 3;
    public static final int ADMINISTRATOR = 4;
    public static final int DIRECTOR = 5;
    public static final int CASHIER = 6;
    public static final int DROP_USER_BY_ID = 7;
    public static final int ADD_USER = 8;
    public static final int UPDATE_USER_ROLE = 9;
    public static final int GET_TYPE_CATEGORY = 10;
    public static final int ADD_TYPE_CATEGORY = 11;
    public static final int ADD_PRODUCT = 12;
    public static final int GET_ALL_PRODUCT = 13;
    public static final int DROP_PRODUCT_BY_ID = 14;
    public static final int DROP_PRODUCT_FROM_SHOP = 15;
    public static final int UPDATE_PRODUCT = 16;
    public static final int GET_ALL_BUYERS = 17;
    public static final int ADD_BUYERS = 18;
    public static final int ADD_ORDER = 19;
    public static final int GET_ALL_ORDER = 20;
    public static final int UPDATE_ORDER_STATUS = 21;
    public static final int DELETE_ORDER = 22;
    public static final int ADD_SHOP = 23;
    public static final int GET_ALL_SHOPS = 24;
    public static final int GET_ITEM_ORDERS_BY_ORDER = 25;
    public static final int DROP_TYPE = 26;
    public static final int DROP_STORE = 27;
}
