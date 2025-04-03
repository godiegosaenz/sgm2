/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;

/**
 *
 * @author Fernando
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Lock(LockType.READ)
public class OmegaConfigs {

    @Resource
    private String dburl, dburlTemp;
    @Resource
    private String dbuser, dbuserTemp;
    @Resource
    private String dbpass, dbpassTemp;
    @Resource
    private String tableName, tableNameTemp;

    public String getDburl() {
        return dburl;
    }

    public String getDbuser() {
        return dbuser;
    }

    public String getDbpass() {
        return dbpass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDburlTemp() {
        return dburlTemp;
    }

    public void setDburlTemp(String dburlTemp) {
        this.dburlTemp = dburlTemp;
    }

    public String getDbuserTemp() {
        return dbuserTemp;
    }

    public void setDbuserTemp(String dbuserTemp) {
        this.dbuserTemp = dbuserTemp;
    }

    public String getDbpassTemp() {
        return dbpassTemp;
    }

    public void setDbpassTemp(String dbpassTemp) {
        this.dbpassTemp = dbpassTemp;
    }

    public String getTableNameTemp() {
        return tableNameTemp;
    }

    public void setTableNameTemp(String tableNameTemp) {
        this.tableNameTemp = tableNameTemp;
    }

}
