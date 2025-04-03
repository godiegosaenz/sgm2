/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app;

/**
 *
 * @author Fernando
 */
public abstract class AppProps {
    
    public static String getString(String name){
        String prop = System.getProperty(name);
        return prop;
    }
    
    public static String getString(String name, String def){
        String prop = System.getProperty(name, def);
        return prop;
    }
    
    public static Boolean getBoolean(String name){
        return "true".equalsIgnoreCase(System.getProperty(name));
    }
    
    public static Boolean getBoolean(String name, Boolean def){
        
        return "true".equalsIgnoreCase(System.getProperty(name, def.toString().toLowerCase()));
    }
    
    
    
}
