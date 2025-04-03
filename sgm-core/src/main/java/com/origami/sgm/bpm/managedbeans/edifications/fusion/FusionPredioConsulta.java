/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.fusion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.HistoricoTramiteDetLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class FusionPredioConsulta extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    
    private HistoricoTramites ht;
    private HistoricoTramiteDetLazy lazy;
    private AclUser firmaDir;
    private String path;
    
    @PostConstruct
    public void initView(){
        if (sess != null) {
            GeTipoTramite tra = permisoServices.getGeTipoTramiteById(6L);
            lazy = new HistoricoTramiteDetLazy(8L);

            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            firmaDir = new AclUser();
            if (tra.getUserDireccion() != null) {
                firmaDir = permisoServices.getAclUserByUser(tra.getUserDireccion());
            }
        } else {
            this.continuar();
        }
    }
    
    public void imprimirLiquidacionGuardado(HistoricoTramiteDet liquidacion) {
        try{
            ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
            if(ht.getObservacion()!=null && ht.getObservacion().equals("Trámite migrado")){
                //this.imprimirLiquidación(liquidacion);
                return;
            }
            HistoricoReporteTramite hrt = null;
            CatEnte solicitante = permisoServices.getFichaServices().getCatEnteById(liquidacion.getTramite().getSolicitante().getId());

            for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
                if (r.getNombreReporte().equalsIgnoreCase("TasaLiq_DivisionPredio-" + solicitante.getCiRuc())) {
                    if (r.getEstado() && r.getUrl() != null) {
                        hrt = r;
                    }
                }
            }
            if (hrt != null) {
                this.descargarDocumento(hrt.getUrl());
            } else {
                JsfUti.messageError(null, "No se ha generado la liquidación aun", "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirLiquidación(HistoricoTramiteDet htd){
        ss.instanciarParametros();
        List<CatPredioPropietario> propietarios;
        CatPredio predio = htd.getPredio();
        propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        
        try{
            ss.agregarParametro("id", ht.getId());
            ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("numrepor", htd.getNumTasa()+ "-" + new SimpleDateFormat("yyyy").format(htd.getFecCre()));
            ss.agregarParametro("base_calc1", ""+htd.getBaseCalculo().setScale(2, RoundingMode.CEILING));
            ss.agregarParametro("base_calc2", 0);
            ss.agregarParametro("total_pagar", htd.getTotal());
            //servletSession.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/lilianaGuerrero.jpg"));
            ss.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/" + firmaDir.getRutaImagen() + ".jpg"));
            ss.agregarParametro("validador", "Tasa de liquidación reimpresa");
            ss.setNombreReporte("fusionPredios - " + ht.getId() + " - " + (htd.getFecCre()).getTime());
            ss.setTieneDatasource(true);
            ss.agregarParametro("responsable", htd.getResponsable());
            ss.agregarParametro("descripcion_edificacion", htd.getDescripcion().toUpperCase());
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//fusion//"));
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public HistoricoTramiteDetLazy getLazy() {
        return lazy;
    }

    public void setLazy(HistoricoTramiteDetLazy lazy) {
        this.lazy = lazy;
    }

    public AclUser getFirmaDir() {
        return firmaDir;
    }

    public void setFirmaDir(AclUser firmaDir) {
        this.firmaDir = firmaDir;
    }
    
    
}
