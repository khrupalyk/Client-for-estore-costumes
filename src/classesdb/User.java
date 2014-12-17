/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesdb;

import client.Constants;
import java.io.*;
import java.util.Vector;
import jdk.nashorn.internal.objects.NativeMath;

/**
 *
 * @author insane
 */
public class User implements Serializable {

    private int userId;
    private int storeId;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private int role;
    private Statistics statistics = new Statistics();

    public User() {
    }

    public User(int userId, int storeId, String login, String password, String firstName, String lastName, int role) {
        this.userId = userId;
        this.storeId = storeId;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Vector getInfo() {
        Vector v = new Vector();
        v.add(userId);
        v.add(login);
        v.add(firstName);
        v.add(lastName);
        v.add(getStringRole(role));
        v.add(statistics.getCountOrders());

        return v;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getStringRole(int role) {
        switch (role) {
            case Constants.ADMINISTRATOR:
                return "Administrator";
            case Constants.DIRECTOR:
                return "Director";
            case Constants.CASHIER:
                return "Cashier";
        }
        return "";
    }

    public int getIntRole(String str) {
        switch (str) {
            case "Administrator":
                return (Constants.ADMINISTRATOR);
            case "Director":
                return (Constants.DIRECTOR);
            case "Cashier":
                return (Constants.CASHIER);
        }
        return 0;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

}
