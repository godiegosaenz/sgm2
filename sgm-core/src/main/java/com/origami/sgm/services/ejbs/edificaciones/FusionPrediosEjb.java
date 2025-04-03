/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.edificaciones.FusionPrediosServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless(name = "fusionPredios")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class FusionPrediosEjb implements FusionPrediosServices {

    @javax.inject.Inject
    protected Entitymanager manager;

    @Override
    public boolean fusionarPredios(List<HistoricoTramiteDet> det) {
        try {
            for (HistoricoTramiteDet d : det) {
                manager.persist(d.getPredio());
                if (d.getPredio().getCatPredioS4() != null) {
                    manager.persist(d.getPredio().getCatPredioS4());
                }
                if (d.getPredio().getCatPredioS6() != null) {
                    manager.persist(d.getPredio().getCatPredioS6());
                }
            }
            return true;
        } catch (Exception e) {
            Logger.getLogger(FusionPrediosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

}
