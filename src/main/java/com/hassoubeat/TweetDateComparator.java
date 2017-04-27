/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hassoubeat;

import java.util.Comparator;
import twitter4j.Status;

/**
 *
 * @author hassoubeat
 */
public class TweetDateComparator implements Comparator<Status>{

    @Override
    public int compare(Status o1, Status o2) {
        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
    }
    
}
