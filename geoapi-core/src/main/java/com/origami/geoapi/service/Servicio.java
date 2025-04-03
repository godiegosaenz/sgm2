/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Fernando
 */
public abstract class Servicio {
    
    @PersistenceContext(name = "geoapi-PU")
    protected EntityManager geoEm;
    
}
