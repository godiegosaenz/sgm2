/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Fernando
 */
@Singleton
@Lock(LockType.READ)
@ApplicationScoped
public class SequenceService {
    
    
    
}
