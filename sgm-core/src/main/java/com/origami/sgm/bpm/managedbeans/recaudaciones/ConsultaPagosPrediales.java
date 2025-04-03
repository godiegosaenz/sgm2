/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.managedbeans.BusquedaPredios;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
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
public class ConsultaPagosPrediales extends BusquedaPredios implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession uSession;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @Inject
    private ServletSession ss;

    private CatPredio predioConsulta;
    private List<RenLiquidacion> emisionesSeleccionadas;
    private List<CatCiudadela> ciudadelas;
    private CatEnteLazy solicitantes;
    private Long tipoCertificado;
    private Boolean formatoActual;
    protected List<CatParroquia> parroquiasRurales;
    

    @PostConstruct
    public void initView() {
        try {
            if (uSession.esLogueado()) {
                solicitantes = new CatEnteLazy();
                ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
                parroquiasRurales = manager.findAllEntCopy(Querys.parroquiasRurales);
                formatoActual = Boolean.TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void procesarPago(RenLiquidacion liq, Integer tipo) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            List<Long> idsLiqs = new ArrayList();
            if (liq == null) {
                if (emisionesSeleccionadas == null || emisionesSeleccionadas.isEmpty()) {
                    JsfUti.messageInfo(null, "Info", "Debe seleccionar al menos una emisión");
                    return;
                }
                for (RenLiquidacion temp : emisionesSeleccionadas) {
                    idsLiqs.add(temp.getId());
                }
            } else {
                idsLiqs.add(liq.getId());
            }
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("LIQUIDACIONES", (Collection) idsLiqs);
            ss.agregarParametro("FORMATO_ACTUAL", formatoActual);
            ss.agregarParametro("LOGO", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/coactiva/"));
            ss.setNombreSubCarpeta("Emision/");
            ss.agregarParametro("LOGO_URL", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/coactiva/");
            //ss.agregarParametro("FIRMAS_URL", JsfUti.getRealPath("/") + "/css/firmas/");
            ss.setNombreSubCarpeta("Emision");
            switch (tipo) {
                case 1:
                    ss.setNombreReporte("masterTituloCreditoEmisionPredial");
                    break;
                case 2:
                    ss.setNombreReporte("masterTituloCreditoEmisionPredialRural");
                    break;
            }
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }


    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public Long getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(Long tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }

    public List<RenLiquidacion> getEmisionesSeleccionadas() {
        return emisionesSeleccionadas;
    }

    public void setEmisionesSeleccionadas(List<RenLiquidacion> emisionesSeleccionadas) {
        this.emisionesSeleccionadas = emisionesSeleccionadas;
    }

    public Boolean getFormatoActual() {
        return formatoActual;
    }

    public void setFormatoActual(Boolean formatoActual) {
        this.formatoActual = formatoActual;
    }


    public List<CatParroquia> getParroquiasRurales() {
        return parroquiasRurales;
    }

    public void setParroquiasRurales(List<CatParroquia> parroquiasRurales) {
        this.parroquiasRurales = parroquiasRurales;
    }


}
