/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rummy.server.app;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author pentagonpie
 */
public class passwordManager {
    public static String[] storePass(String password) throws Exception{
        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(password, salt);
        String result[] = new String[2];
        result[0] = hashed;
        result[1] = salt;
        System.out.println("in creating password using salt "+ salt);
        return result;
    }
    
    public static boolean checkPass(String plainPassword, String salt,String realHash){
        
        System.out.println("used salt to check pass: " + salt);
        
        String possibleHash = BCrypt.hashpw(plainPassword, salt);
        return possibleHash.equals(realHash);
     
    }
}
