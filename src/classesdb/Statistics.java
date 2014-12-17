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
public class Statistics implements Serializable{
    
    private int statisticsId;
    private int countOrders;
    private int userId;

    public Statistics() {
    }

    public Statistics(int statisticsId, int countOrders, int userId) {
        this.statisticsId = statisticsId;
        this.countOrders = countOrders;
        this.userId = userId;
    }

    public int getStatisticsId() {
        return statisticsId;
    }

    public int getCountOrders() {
        return countOrders;
    }

    public int getUserId() {
        return userId;
    }

    public void setStatisticsId(int statisticsId) {
        this.statisticsId = statisticsId;
    }

    public void setCountOrders(int countOrders) {
        this.countOrders = countOrders;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
}
