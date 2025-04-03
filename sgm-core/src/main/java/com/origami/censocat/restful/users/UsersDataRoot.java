/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful.users;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class UsersDataRoot implements Serializable {

    public List<UserDataItem> usersDataList;
    public UserDataItem userData;

    public List<UserDataItem> getUsersDataList() {
        return usersDataList;
    }

    public void setUsersDataList(List<UserDataItem> usersDataList) {
        this.usersDataList = usersDataList;
    }

    public UserDataItem getUserData() {
        return userData;
    }

    public void setUserData(UserDataItem userData) {
        this.userData = userData;
    }

}
