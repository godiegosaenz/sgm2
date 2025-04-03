/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Mariuly
 */
@Named
@ViewScoped
public class ReporteRecaudaciones implements Serializable {

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private ServletSession ss;
    private Integer tipoReporte;
    private Boolean fecha;
    private Date desde;
    private Date hasta;
    private SimpleDateFormat sdf;

    public ReporteRecaudaciones() {
    }

    @PostConstruct
    public void InitView() {
        desde = new Date();
        hasta = new Date();
    }

    public void imprimir() {
        ss.instanciarParametros();
        String path = Faces.getRealPath("//");
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("informeDiarios");
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));

        switch (tipoReporte) {
            case 1:
                if (fecha == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha desde");
                }

                sdf = new SimpleDateFormat("dd/MM/yyyy");
                ss.setNombreReporte("recaudacionesPorTransaccion");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("FECHA", sdf.format(hasta));
                ss.agregarParametro("DESDE", sdf.format(desde));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
                break;

        }

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Boolean getFecha() {
        return fecha;
    }

    public void setFecha(Boolean fecha) {
        this.fecha = fecha;
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

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

}
