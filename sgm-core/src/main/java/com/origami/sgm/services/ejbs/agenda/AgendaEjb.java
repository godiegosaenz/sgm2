/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.agenda;

import com.origami.sgm.bpm.models.AgendaModel;
import com.origami.sgm.bpm.models.DatosAgenda;
import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.TipoAviso;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.agenda.AgendaServ;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.Session;
import util.HiberUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless(name = "agenda")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class AgendaEjb implements AgendaServ {

    private Session sess = null;

    @Override
    public DatosAgenda guardarAgenda(DatosAgenda da) {
        DatosAgenda agenda = null;
        List<AgendaModel> model;
        AgendaModel datos;
        Agenda ag;
        AgendaDet dt;
        TipoAviso av;
        HistoricoTramites tr;
        try {
            if (da.getAgenda() != null) {
                agenda = new DatosAgenda();
                HiberUtil.requireTransaction();
                sess = HiberUtil.getSession();
                tr = (HistoricoTramites) sess.merge(da.getTramite());
                if (tr != null) {
                    model = new ArrayList<>();
                    da.getAgenda().setTramite(tr);
                    da.getAgenda().setAgendaDetList(null); 
                    ag = (Agenda) sess.merge(da.getAgenda());
                    if (ag != null) {
                        da.getAvisos().setAgenda(ag);
                        da.getAvisos().setFecCre(new Date());
                        da.getAvisos().setEstado(Boolean.TRUE);
                        av = (TipoAviso) sess.merge(da.getAvisos());
                        for (AgendaDet det : da.getDetAgenda()) {
                            datos = new AgendaModel();
                            
                            AgendaDet adt = new AgendaDet();                            
                            adt.setAgenda(ag);
                            adt.setFecCre(det.getFecCre());
                            adt.setInvolucrado(det.getInvolucrado());
                            adt.setUsuario(det.getUsuario());
                            adt.setEstado(Boolean.TRUE);
                            
                            dt = (AgendaDet) sess.merge(adt);
                            
                            datos.setIdDet(dt.getId());
                            datos.setFecIni(ag.getFInicio());
                            datos.setPrioridad(50);
                            datos.setTarea(ag.getTipo().getDescripcion().toUpperCase());
                            datos.setUrl(ag.getTipo().getUrl());
                            if (ag.getDescripcion() != null) {
                                datos.setDescripcion(ag.getDescripcion().toUpperCase());
                            } else {
                                datos.setDescripcion(ag.getTipo().getDescripcion().toUpperCase());
                            }
                            datos.setResponsable(det.getUsuario());
                            model.add(datos);
                        }
                        agenda.setAgenda(ag);
                        agenda.setAvisos(av);
                        agenda.setTramite(tr);
                        agenda.setModelAgenda(model);
                        agenda.setMensaje("Actividad agendada satisfactoriomente");
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return agenda;
    }

    @Override
    public boolean actIntervinientes(Agenda a, List<AgendaDet> d) {
        boolean flag = false;
        try {
            HiberUtil.requireTransaction();
            sess = HiberUtil.getSession();
            if (d != null) {
                if (!d.isEmpty()) {
                    for (AgendaDet d1 : d) {
                        if (d1.getId() == null) {
                            d1.setEstado(true);
                            d1.setFecCre(new Date());
                            sess.merge(d1);
                        } else {
                            d1.setEstado(false);
                            d1.setFecAct(new Date());
                            sess.merge(d1);
                        }
                    }
                    flag = true;
                }
            } else {
                sess.merge(a);
                flag = true;
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

}
