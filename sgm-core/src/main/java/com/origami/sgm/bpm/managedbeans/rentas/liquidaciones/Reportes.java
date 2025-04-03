/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Xndy Snchez
 * @Date 12/05/2020
 */
@Named
@ViewScoped
public class Reportes implements Serializable {

    @javax.inject.Inject
    private RentasServices services;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;

    private List<RenTipoLiquidacion> titulos;
    private RenTipoLiquidacion titulo;

    private Integer tipoReporte;
    private Integer tipoTitulo;
    private Integer estadoLocal;
    private String fechaLarga;
    private Boolean fechas;
    private Date desde;
    private Date hasta;
    private SimpleDateFormat sdf;
    private Integer anio;
    private Integer anioMax;
    private Boolean desd = false;

    /**
     * Creates a new instance of Reportes
     */
    public Reportes() {
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
        tipoTitulo = 0;
        titulo = new RenTipoLiquidacion();
    }

    public void tituloReportes() {
        titulo = new RenTipoLiquidacion();
        titulos = services.gettiposLiquidacionByCodTitRep(tipoTitulo);
    }

    public void mostrarFechas() {
        if (tipoReporte == 1) {
            fechas = true;
            tipoTitulo = 0;
            tituloReportes();
        } else if (tipoReporte == 2) {
            fechas = true;
            tipoTitulo = 0;
            tituloReportes();
        } else if (tipoReporte == 3) {
            fechas = tipoTitulo == 3;
        } else if (tipoReporte == 4) {
            fechas = true;
        } else if (tipoReporte == 5) {
            /*si esto es verdad entonces*/
            desd = true;
            anio = Utils.getAnio(new Date());
            anioMax = anio;
        }
    }

    public void imprimir(Boolean excel) {
        if (tipoReporte == 0) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar una de las opciones, para realizar la consulta");
            return;
        }

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("rentas/liquidaciones");
        ss.setTieneDatasource(Boolean.TRUE);
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        Map<String, Object> parametrosReporteAdicional = new HashMap<>();// AGREGAR EL NOMBRE Y EL MAP DE PARAMETROS
        switch (tipoReporte) {
            case 1: // Locales Comerciales
                sdf = new SimpleDateFormat("MM-YYYY");
                ss.setNombreDocumento("Locales Comerciales");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.setNombreReporte("sReporteLocalesComerciales");
                ss.agregarParametro("DESDE", sdf.format(desde));
                ss.agregarParametro("HASTA", sdf.format(hasta));
                ss.agregarParametro("USUARIO", session.getName_user());
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
                break;
            case 4:
                ss.setNombreSubCarpeta("rentas");
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
                ss.setNombreReporte("bajaRubrosEmisionesUrbanasDetalle");
                ss.setNombreDocumento("bajaRubrosEmisionesUrbanasDetalle");
                ss.agregarParametro("DESDE", desde);
                ss.agregarParametro("HASTA", hasta);
                ss.agregarParametro("USUARIO", session.getName_user());
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
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

    public List<RenTipoLiquidacion> getTitulos() {
        return titulos;
    }

    public void setTitulos(List<RenTipoLiquidacion> titulos) {
        this.titulos = titulos;
    }

    public RenTipoLiquidacion getTitulo() {
        return titulo;
    }

    public void setTitulo(RenTipoLiquidacion titulo) {
        this.titulo = titulo;
    }

    public Integer getTipoTitulo() {
        return tipoTitulo;
    }

    public void setTipoTitulo(Integer tipoTitulo) {
        this.tipoTitulo = tipoTitulo;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getFechas() {
        return fechas;
    }

    public void setFechas(Boolean fechas) {
        this.fechas = fechas;
    }

    public Boolean getDesd() {
        return desd;
    }

    public void setDesde(Boolean desd) {
        this.desd = desd;
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

    private String obtenerIdTipoLiq(List<RenTipoLiquidacion> titulos) {
        StringBuilder buffer = new StringBuilder();
        for (RenTipoLiquidacion titulo1 : titulos) {
            if (buffer.length() != 0) {
                buffer.append(",");
            }
            buffer.append(titulo1.getId());
        }
        return buffer.toString();
    }

    public Integer getEstadoLocal() {
        return estadoLocal;
    }

    public void setEstadoLocal(Integer estadoLocal) {
        this.estadoLocal = estadoLocal;
    }

}
