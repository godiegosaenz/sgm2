/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.reingreso;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.reingreso.ReingresoTramService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.Session;
import util.HiberUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless(name = "reingresoTram")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class ReingresoTramiteEjb implements ReingresoTramService {

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private SeqGenMan seq;

    @Override
    public HistoricoTramites reingresarTramite(HistoricoTramites ht, CatEnte e, EnteCorreo c, EnteTelefono t, Observaciones obs) {
        HistoricoTramites tram = null, htram = null;
        Calendar cal = Calendar.getInstance();
        Long numTramite = 0L;
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        try {
            CatEnte ente = (CatEnte) sess.merge(e);
            if (ente != null) {
                if (e.getEnteCorreoCollection().isEmpty()) {
                    if (c.getEmail() != null) {
                        c.setEnte(ente);
                        sess.merge(c);
                    } else {
                        return null;
                    }
                }
                if (e.getEnteTelefonoCollection().isEmpty()) {
                    if (t.getTelefono() != null) {
                        t.setEnte(ente);
                        sess.merge(t);
                    } else {
                        return null;
                    }
                }
                if (ht.getId() != null) {
                    numTramite = ht.getId();
                    ht.setId(null);
                    ht.setEstado("inactivo");
                    sess.merge(ht);
                    sess.flush();                    
                    HistoricoTramites tt = new HistoricoTramites();
                    tt.setId(numTramite);
                    tt.setSolicitante(ente);
                    tt.setNombrePropietario(ht.getNombrePropietario());
                    tt.setEstado("pendiente");
                    //MODIFICADO 17-01-2017 Henry Pilco
                    //tt.setFecha(new Date());
                    tt.setFecha(ht.getFecha());
                    //Fin Modificacion
                    tt.setUserCreador(ht.getUserCreador());
                    tt.setNumPredio(ht.getNumPredio());
                    if (ht.getNumTramiteXDepartamento() != null) {
                        tt.setNumTramiteXDepartamento(ht.getNumTramiteXDepartamento());
                    }
                    tt.setTipoTramite(ht.getTipoTramite());
                    tt.setTipoTramiteNombre(ht.getTipoTramiteNombre());
                    htram = (HistoricoTramites) sess.merge(tt);
                    sess.flush();
                    sess.refresh(htram);
                } else {
                    ht.setNumTramiteXDepartamento(new BigInteger(seq.getMaxSecuenciaTipoTramite(cal.get(Calendar.YEAR), ht.getTipoTramite().getId()).toString()));
                    ht.setEstado("pendiente");
                    ht.setSolicitante(ente);
                    htram = (HistoricoTramites) sess.merge(ht);
                }
                if (htram != null) {
                    obs.setEstado(true);
                    obs.setIdTramite(htram);
                    obs.setFecCre(new Date());
                    obs.setTarea("Reingreso de tramite " + ht.getTipoTramite().getDescripcion());
                    if (sess.merge(obs) != null) {
                        sess.getTransaction().commit();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ReingresoTramiteEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return htram;
    }

}
