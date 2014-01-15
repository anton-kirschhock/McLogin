/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zapto.muziedev.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Anton
 */
public class StrList {
    public static boolean findElement(List<String> arr,String el){
        boolean returnee = false;
        for (int i = 0; i < arr.size() && !returnee; i++) {
            if(arr.get(i) != null && arr.get(i).equals(el)){
                returnee =  true;
            }
        }
        return returnee;
    }
}
