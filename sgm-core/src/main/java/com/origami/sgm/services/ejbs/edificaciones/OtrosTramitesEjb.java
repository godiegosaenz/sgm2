/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.OtrosTramitesServices;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author Joao Sanga
 */
@Stateless(name = "otrosTramitesServices")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class OtrosTramitesEjb implements OtrosTramitesServices{
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    private SeqGenMan seqManager;
    
    @Override
    public Boolean guardarOtrosTramites(HistoricoReporteTramite hrt, HistoricoTramiteDet htd, Observaciones obs, OtrosTramitesHasPermiso ohtp, OtrosTramites oTramite, HistoricoTramites ht){
        Boolean b;
        try{
            b = true;
            services.update(hrt);
            services.update(ht);
            services.persist(htd);
            services.update(oTramite);
            services.persist(obs);
            services.persist(ohtp);
            
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean guardarOtrosTramitesEdicion(HistoricoReporteTramite hrt, HistoricoTramiteDet htd, Observaciones obs, OtrosTramitesHasPermiso ohtp, OtrosTramites oTramite, HistoricoTramites ht, List<HistoricoTramiteDet> htdList, List<HistoricoReporteTramite> hrtList){
        Boolean b;
        try{
            b = true;
            services.update(hrt);
            services.update(ht);
            services.persist(htd);
            services.update(oTramite);
            services.persist(obs);
            services.persist(ohtp);
            
            for(HistoricoTramiteDet temp : htdList){
                temp.setEstado(false);
                services.update(temp);
            }
            
            for(HistoricoReporteTramite temp : hrtList){
                temp.setEstado(false);
                services.update(temp);           
            }
            
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    
}
