/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.rentas;

import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.rentas.CoactivasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author Joao Sanga
 */
@Stateless(name = "coactivasEjb")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class CoactivasEjb implements CoactivasServices{
    private static final Logger LOG = Logger.getLogger(RentasEjb.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    
    @javax.inject.Inject
    private SeqGenMan seq;
}
