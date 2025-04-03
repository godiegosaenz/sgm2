/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.models.RenTipoLiquidacionModel;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class FondosAjenosView implements Serializable {

    public static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession uSession;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @Inject
    private ServletSession ss;
    
    private Date fechaIn, fechaFin;
    private Boolean esFondoAjeno;
    private List<RenTipoLiquidacion> tiposLiquidaciones;
    private List<RenTipoLiquidacionModel> tipos, tiposLiquidacionesModel, tiposLiquidacionesModel2;
    private RenTipoLiquidacionModel tipoSeleccionado;
    
    @PostConstruct
    public void initView(){
        try {
            if (uSession.esLogueado()) {
                RenTipoLiquidacionModel t;
                tiposLiquidacionesModel = new ArrayList();
                tiposLiquidacionesModel2 = new ArrayList();
                esFondoAjeno = true;
                tiposLiquidaciones = manager.findAll(QuerysFinanciero.getRenTipoLiquidacionesAll);
                for(RenTipoLiquidacion temp : tiposLiquidaciones){
                    t = new RenTipoLiquidacionModel(temp);
                    if(t.getTieneRubrosAjenos())
                        tiposLiquidacionesModel.add(t);
                    if(t.getTieneRubrosPropios())
                        tiposLiquidacionesModel2.add(t);
                }
                tipos = tiposLiquidacionesModel;
                JsfUti.update("mainForm");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cambiarDT(){
        if(esFondoAjeno)
            tipos = tiposLiquidacionesModel;
        else
            tipos = tiposLiquidacionesModel2;
    }
    
    public void generarReporte(){
        try{
            if(tipoSeleccionado == null){
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el tipo de liquidación");
                return;
            }
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            
            ss.agregarParametro("DEL_MUNICIPIO", !esFondoAjeno);
            ss.agregarParametro("ID_TIPO_LIQUIDACION", tipoSeleccionado.getId());
            ss.agregarParametro("DESDE", fechaIn);
            ss.agregarParametro("HASTA", fechaFin);
            ss.agregarParametro("NOMBRE_TIPO_LIQUIDACION", tipoSeleccionado.getNombreTipoLiq());
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/reportes/"));
            ss.agregarParametro("FIRMAS_URL", JsfUti.getRealPath("/") + "/css/firmas/");
            ss.setNombreSubCarpeta("recaudaciones");
            ss.setNombreReporte("reporte_fondos_ajenos");
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporteExcel(){
        try{
            if(tipoSeleccionado == null){
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el tipo de liquidación");
                return;
            }
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            
            ss.agregarParametro("DEL_MUNICIPIO", !esFondoAjeno);
            ss.agregarParametro("ID_TIPO_LIQUIDACION", tipoSeleccionado.getId());
            ss.agregarParametro("DESDE", fechaIn);
            ss.agregarParametro("HASTA", fechaFin);
            ss.agregarParametro("NOMBRE_TIPO_LIQUIDACION", tipoSeleccionado.getNombreTipoLiq());
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/reportes/").concat("/"));
            ss.agregarParametro("FIRMAS_URL", JsfUti.getRealPath("/") + "/css/firmas/");
            ss.agregarParametro("ruta_firmas", Faces.getRealPath("/css/firmas/").concat("/"));
            ss.setNombreSubCarpeta("recaudaciones/");
            ss.setNombreReporte("reporte_fondos_ajenos");
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Date getFechaIn() {
        return fechaIn;
    }

    public void setFechaIn(Date fechaIn) {
        this.fechaIn = fechaIn;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getEsFondoAjeno() {
        return esFondoAjeno;
    }

    public void setEsFondoAjeno(Boolean esFondoAjeno) {
        this.esFondoAjeno = esFondoAjeno;
    }

    public List<RenTipoLiquidacion> getTiposLiquidaciones() {
        return tiposLiquidaciones;
    }

    public void setTiposLiquidaciones(List<RenTipoLiquidacion> tiposLiquidaciones) {
        this.tiposLiquidaciones = tiposLiquidaciones;
    }

    public List<RenTipoLiquidacionModel> getTiposLiquidacionesModel() {
        return tiposLiquidacionesModel;
    }

    public void setTiposLiquidacionesModel(List<RenTipoLiquidacionModel> tiposLiquidacionesModel) {
        this.tiposLiquidacionesModel = tiposLiquidacionesModel;
    }

    public RenTipoLiquidacionModel getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(RenTipoLiquidacionModel tipoSeleccionado) {
        this.tipoSeleccionado = tipoSeleccionado;
    }

    public List<RenTipoLiquidacionModel> getTiposLiquidacionesModel2() {
        return tiposLiquidacionesModel2;
    }

    public void setTiposLiquidacionesModel2(List<RenTipoLiquidacionModel> tiposLiquidacionesModel2) {
        this.tiposLiquidacionesModel2 = tiposLiquidacionesModel2;
    }

    public List<RenTipoLiquidacionModel> getTipos() {
        return tipos;
    }

    public void setTipos(List<RenTipoLiquidacionModel> tipos) {
        this.tipos = tipos;
    }

}
