/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful.users;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class UserDataItem implements Serializable{
    
    protected String username;
    protected String password;

    public UserDataItem() {
    }

    public UserDataItem(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
}