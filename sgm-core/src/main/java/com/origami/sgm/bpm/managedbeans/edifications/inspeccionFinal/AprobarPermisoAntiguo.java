/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class AprobarPermisoAntiguo extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;

    @Inject
    private ServletSession servletSession;
    
    @javax.inject.Inject
    private SeqGenMan seq;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private Observaciones obs = null;
    private PePermiso permisoNuevo;
    private String nombreTec;
    protected CatEnte respTec;
    protected CatPredio predio;
    protected PePermisoCabEdificacion permisoSelect;
    protected List<CatEdfCategProp> listCat;
    protected List<PeTipoPermiso> listRequisTra;
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<PePermisoCabEdificacion> DetallesEdific;
    protected List<CatEdfProp> lisCatEdfProp = new ArrayList<>();
    protected List<PeDetallePermiso> lisPeDetallePermisos = new ArrayList<>();
    protected CatEnteLazy enteLazy;
    protected HistoricoTramites ht;
    protected GeTipoTramite tipoTramite;
    private Boolean codUrban;
    
    @PostConstruct
    public void initView() {
        if (uSession.esLogueado()) {
            try {
                this.setTaskId(uSession.getTaskID());
                obs = new Observaciones();
                ht = permisoService.getHistoricoTramiteById(Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString()));
                tipoTramite = ht.getTipoTramite();
                predio = (CatPredio) services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
            }catch(Exception e){
                
            }
        }
    }
    
}
