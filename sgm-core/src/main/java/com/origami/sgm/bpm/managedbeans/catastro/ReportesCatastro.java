/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Xndy Snchez
 * @Date 12/05/2020
 */
@Named
@ViewScoped
public class ReportesCatastro implements Serializable {

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;

    private Integer tipoReporte;
    private String fechaLarga;
    private Date desde;
    private Date hasta;
    private SimpleDateFormat sdf;

    /**
     * Creates a new instance of Reportes
     */
    public ReportesCatastro() {
    }

    @PostConstruct
    public void initView() {
        iniciarVariables();

    }

    private void iniciarVariables() {
        sdf = new SimpleDateFormat("EEEEE, dd MMMMM yyyy");
        fechaLarga = sdf.format(new Date());
        desde = new Date();
        hasta = new Date();
    }

    public void imprimir(Boolean excel) {
        if (tipoReporte == 0) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar una de las opciones, para realizar la consulta");
            return;
        }
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("catastro/San Vicente");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.agregarParametro("USUARIO", session.getName_user());

        switch (tipoReporte) {
            case 1: // Predios Según Ocupación
                ss.setNombreDocumento("Predios Según Ocupación");
                ss.setNombreReporte("sReportePrediosOcupacion");
                break;
            case 2: //Predios Según Construtividad
                ss.setNombreDocumento("Predios Según Construtividad");
                ss.setNombreReporte("sReportePrediosEdificados");
                break;
            case 3: //Predios Según Cosolidación
                ss.setNombreDocumento("Predios Según Cosolidación");
                ss.setNombreReporte("sReportePrediosConsolidados");
                break;
            case 4: //Predios Según Tipo de Dominio
                ss.setNombreDocumento("Predios Según Tipo de Dominio");
                ss.setNombreReporte("sReportePrediosTipoDominio");
                break;
            case 5: //Predios Según Escrituras
                ss.setNombreDocumento("Predios Según Escrituras");
                ss.setNombreReporte("sReportePrediosEscrituras");
                break;
            case 6: //Predios Según Mejoras / Recargos
                ss.setNombreDocumento("Predios Según Mejoras / Recargos");
                ss.setNombreReporte("sReportePrediosRecargoCem");
                break;
            case 7: //Predios Según Disponibilidad de Agua Potable
                ss.setNombreDocumento("Predios Según Disponibilidad de Agua Potable");
                ss.setNombreReporte("sReportePrediosAguaPotable");
                break;
            case 8: //Valores de Manzana
                ss.setNombreDocumento("Valores de Manzana");
                ss.setNombreReporte("sReporteValoresMz");
                ss.agregarParametro("ANIO", Utils.getAnio(new Date()));
                break;
            case 9: //Totalizado de Predios
                ss.setNombreDocumento("Totalizado de Predios");
                ss.setNombreReporte("sReportePrediosTotales");
                break;
            default:
                break;
        }
        if (excel) {
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
        } else {
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getFechaLarga() {
        return fechaLarga;
    }

    public void setFechaLarga(String fechaLarga) {
        this.fechaLarga = fechaLarga;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

}
