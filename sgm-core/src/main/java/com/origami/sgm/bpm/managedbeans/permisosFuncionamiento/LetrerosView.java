/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.runtime.ProcessInstance;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class LetrerosView extends BpmManageBeanBaseRoot implements Serializable {

    public static Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;
    @javax.inject.Inject
    private SeqGenMan seq;
    
    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;
    
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private CatEnte solicitante;
    private CmMultas multa;
    private HashMap<String, Object> parametros;
    private BigDecimal valorMulta;
    private GeTipoTramite tipoTramite;
    private RenLocalComercialLazy locales;
    private RenLocalComercial local;
    private HistoricoTramiteDet detalle;
    
    @PostConstruct
    public void initView(){
        try{
            if (session.esLogueado()){ //&& session.getTaskID() != null) {
                entradas = new HashMap<>();
                parametros = new HashMap<>();
                entradas.put("obs", new Observaciones());
                valorMulta = BigDecimal.ZERO;
                
                ht = new HistoricoTramites();
                tipoTramite = services.geTipoTramiteByAbr("COM");
                ht.setTipoTramite(tipoTramite);
                ht.setTipoTramiteNombre(tipoTramite.getDescripcion());

                multa = new CmMultas();
                multa.setValor(BigDecimal.ZERO);
                solicitante = new CatEnte();
                locales = new RenLocalComercialLazy();
                local = new RenLocalComercial();
                local.setRazonSocial(new CatEnte());
                detalle = new HistoricoTramiteDet();
            } else {
                this.continuar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void completarTarea(){
        try{
            if(local != null && detalle.getAreaEdificacion() != null){
                GeTipoTramite tramite = (GeTipoTramite)servicesACL.find(GeTipoTramite.class, 56L);
                ProcessInstance pro = null;
                HashMap<String, Object> paramt = new HashMap();

                pro = this.startProcessByDefinitionKey(tramite.getCarpeta(), paramt);
            }else{
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el local comercial e ingresar el area del letrero");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public CmMultas getMulta() {
        return multa;
    }

    public void setMulta(CmMultas multa) {
        this.multa = multa;
    }

    public HashMap<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(HashMap<String, Object> parametros) {
        this.parametros = parametros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public RenLocalComercialLazy getLocales() {
        return locales;
    }

    public void setLocales(RenLocalComercialLazy locales) {
        this.locales = locales;
    }

    public RenLocalComercial getLocal() {
        return local;
    }

    public void setLocal(RenLocalComercial local) {
        this.local = local;
    }

    public HistoricoTramiteDet getDetalle() {
        return detalle;
    }

    public void setDetalle(HistoricoTramiteDet detalle) {
        this.detalle = detalle;
    }
    
}
