/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Fernando
 */
@RequestScoped
public class PersistenceState implements Serializable{
    
    private Boolean transactionEnabled = true;

    public PersistenceState() {
    }
    
    @PostConstruct
    public void init(){
        setTransactionEnabled(true);
    }

    public Boolean getTransactionEnabled() {
        return transactionEnabled;
    }

    public void setTransactionEnabled(Boolean transactionEnabled) {
        this.transactionEnabled = transactionEnabled;
    }
    
    
    
}
