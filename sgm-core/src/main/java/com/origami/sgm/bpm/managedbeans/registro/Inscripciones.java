/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.NumeroLetra;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class Inscripciones implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    @javax.inject.Inject
    private Entitymanager services;

    @Inject
    private ServletSession servletSession;

    protected RegMovimiento movimiento;
    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();
    protected RegMovimientosLazy movimientosLazy;
    protected Date fechaIngreso = new Date();
    protected Calendar cal = Calendar.getInstance();
    
    protected String urlDownload = "";
    protected Integer anio;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        movimientosLazy = new RegMovimientosLazy();
    }

    public void showDlgRepertorios() {
        JsfUti.update("frmConsultarRepertorio");
        JsfUti.executeJS("PF('dlgRepertorio').show();");
    }
    
    public void redirectFacelet(String cadena){
        JsfUti.redirectFaces(cadena);
    }

    public void imprimirRepertorios() {
        try {
            servletSession.instanciarParametros();
            String fecha;
            Calendar cl = Calendar.getInstance();
            cl.setTime(fechaIngreso);
            Integer year = cl.get(Calendar.YEAR);
            Integer mes = cl.get(Calendar.MONTH) + 1;
            Integer dia = cl.get(Calendar.DAY_OF_MONTH);

            if (mes < 10) {
                fecha = dia.toString() + "/0".concat(mes.toString()) + "/".concat(year.toString());
            } else {
                fecha = dia.toString() + "/".concat(mes.toString()) + "/".concat(year.toString());
            }
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("RepertorioporFecha");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("P_FECINGRESO", fecha);
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            JsfUti.executeJS("PF('dlgRepertorio').hide();");
        } catch (Exception e) {
            Logger.getLogger(Inscripciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editarInscripcion(RegMovimiento mov) {
        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + mov.getId());
    }

    public void imprimirInscripcion(RegMovimiento mov, String reporte) {
        try {
            servletSession.instanciarParametros();
            servletSession.agregarParametro("P_MOVIMIENTO", mov.getId());
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
            servletSession.setNombreReporte(reporte);
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setEncuadernacion(Boolean.TRUE);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            //Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Inscripcion?code=" + mov.getId());
        } catch (Exception e) {
            Logger.getLogger(Inscripciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirRazon(RegMovimiento mov) {
        servletSession.instanciarParametros();
        Calendar cl = Calendar.getInstance();
        cl.setTime(mov.getFechaInscripcion());
        Integer dia = cl.get(Calendar.DAY_OF_MONTH);
        Integer mes = cl.get(Calendar.MONTH) + 1;
        Integer year = cl.get(Calendar.YEAR);
        NumeroLetra n = new NumeroLetra();

        String fecha = n.Convertir(dia.toString(), true) + " DE " + Utils.convertirMesALetra(mes) + " DEL " + n.Convertir(year.toString(), true);
        servletSession.agregarParametro("P_REGISTRO", mov.getNumRepertorio());
        servletSession.agregarParametro("P_ANIO", year.toString());
        servletSession.agregarParametro("P_FECREGISTRO", fecha);
        servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
        servletSession.agregarParametro("ID_MOV", mov.getId());
        servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
        servletSession.agregarParametro("IMG_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        //servletSession.agregarParametro("codigoQR", mov.getId().toString());
        servletSession.setNombreReporte("CabCertificadoPropiedadMercantil");
        servletSession.setNombreSubCarpeta("registroPropiedad");
        servletSession.setTieneDatasource(true);
        servletSession.setEncuadernacion(Boolean.TRUE);
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void showDlgMovSelect(RegMovimiento mov) {
        try {
            movimiento = mov;
            modelo = reg.getConsultaMovimiento(mov.getId());
            if (modelo != null) {
                cal.setTime(mov.getFechaInscripcion());
                anio = cal.get(Calendar.YEAR);
                urlDownload = "/pages" + SisVars.urlbase + "descarga.jsf?nombreLibro=" + movimiento.getLibro().getNombreCarpeta() 
                        + "&anioInscripcion=" + anio + "&numeroTomo=" + movimiento.getNumTomo() + "&numeroInscripcion=" 
                        + movimiento.getNumInscripcion() + "&folioInicial=" + movimiento.getFolioInicio() 
                        + "&folioFinal=" + movimiento.getFolioFin();
                JsfUti.update("formMovRegSelec");
                JsfUti.executeJS("PF('dlgMovRegSelec').show();");
            } else {
                JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirBitacora() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setNombreReporte("bitacoraSgm");
            servletSession.agregarParametro("codMovimiento", movimiento.getId());
            servletSession.agregarParametro("numFicha", null);
            servletSession.agregarParametro("titulo", Messages.bitacoraMovimiento);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, e.getMessage());
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

}
