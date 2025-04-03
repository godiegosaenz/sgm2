/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenTipoEntidadBancaria;
import com.origami.sgm.entities.models.ParteRecaudaciones;
import com.origami.sgm.lazymodels.RenEntidadBancariaLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 26/07/2016
 */
@Named
@ViewScoped
public class ReporteBanco implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReporteBanco.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    private Map<String, Object> paramt;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private Date fecha;
    private Date fechaHasta;
    private Integer numInforme;
    private Integer tipoReporte;
    private Integer anio;
    private Integer anioMax;
    private List<RenEntidadBancaria> bancarias;
    private RenEntidadBancaria banco;
    
    // Variables Render
    private Boolean desde = false;
    private Boolean hasta = false;
    private Boolean informe = false;
    private Boolean oficioRender = false;

    @javax.inject.Inject
    private RecaudacionesService service;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;

    @PostConstruct
    protected void initView() {
        iniciarDatos();
    }

    private void iniciarDatos() {
        fecha = new Date();
        fechaHasta = new Date();
        desde = false;
        hasta = false;
        informe = false;
        oficioRender = false;
    }

    public void mostrarEtiquetas() {

        try {
            iniciarDatos();
            if (tipoReporte == 1 || tipoReporte == 3 || tipoReporte == 4 || tipoReporte == 5 || tipoReporte == 6 || tipoReporte == 8 || tipoReporte == 9|| tipoReporte == 13|| tipoReporte == 14|| tipoReporte == 15) {
                desde = true;
                hasta = true;
            } else if (tipoReporte == 2) {
                desde = true;
                informe = true;
                anio = Utils.getAnio(new Date());
                anioMax = anio;
            } else if (tipoReporte == 7) {
                desde = true;
                informe = true;
                anio = Utils.getAnio(new Date());
                anioMax = anio;
                oficioRender = true;
            } else if (tipoReporte == 12) {
                desde = true;
                hasta = true;
                oficioRender = true;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void generar() {
        if (fecha == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha desde");
            return;
        }
        String path = Faces.getRealPath("//");
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("tesoreria");
        ss.setNombreReporte("reporteChequeBanco");
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
        ss.agregarParametro("DESDE", fecha);
        ss.agregarParametro("HASTA", fechaHasta);
        ss.agregarParametro("BANCO", banco.getId());
        
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Integer getNumInforme() {
        return numInforme;
    }

    public void setNumInforme(Integer numInforme) {
        this.numInforme = numInforme;
    }

    public Boolean getDesde() {
        return desde;
    }

    public void setDesde(Boolean desde) {
        this.desde = desde;
    }

    public Boolean getHasta() {
        return hasta;
    }

    public void setHasta(Boolean hasta) {
        this.hasta = hasta;
    }

    public Boolean getInforme() {
        return informe;
    }

    public void setInforme(Boolean informe) {
        this.informe = informe;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getAnioMax() {
        return anioMax;
    }

    public void setAnioMax(Integer anioMax) {
        this.anioMax = anioMax;
    }

    public Boolean getOficioRender() {
        return oficioRender;
    }

    public void setOficioRender(Boolean oficioRender) {
        this.oficioRender = oficioRender;
    }

    /**
     * Creates a new instance of ReportesDiarios
     */
    public ReporteBanco() {
    }

    public List<RenEntidadBancaria> getBancarias() {
        Map<String, Object> paramts = new HashMap<>();
        paramts.put("tipo", new RenTipoEntidadBancaria(1L));
        return manager.findObjectByParameterList(RenEntidadBancaria.class, paramts);
    }

    public RenEntidadBancaria getBanco() {
        return banco;
    }

    public void setBanco(RenEntidadBancaria banco) {
        this.banco = banco;
    }

}
