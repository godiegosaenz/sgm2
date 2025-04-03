/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class CargaExpediente implements Serializable {

    @javax.inject.Inject
    private InscripcionNuevaServices ins;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected List<RegLibro> libros;
    protected RegLibro libro;
    protected Calendar cal = Calendar.getInstance();
    protected Date fecha = new Date();
    protected Integer anio;
    protected Integer tomo;
    protected Integer inscripcion;
    protected Integer folioInicio;
    protected Integer folioFin;
    protected String respuesta;
    protected Boolean show = false;

    protected RegMovimientosLazy movsLazy;
    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();
    protected RegMovimiento movimiento = new RegMovimiento();
    protected String urlDownload = "";

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            libros = ins.getRegLibroList();
            movsLazy = new RegMovimientosLazy();
        } catch (Exception e) {
            Logger.getLogger(CargaExpediente.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validarCampos() {
        if (libro == null) {
            return false;
        }
        if (libro.getNombreCarpeta() == null) {
            return false;
        }
        if (tomo == null) {
            return false;
        }
        if (inscripcion == null) {
            return false;
        }
        if (folioInicio == null) {
            return false;
        }
        return folioFin != null;
    }

    public void carga() {
        if (this.validarCampos()) {
            cal.setTime(fecha);
            anio = cal.get(Calendar.YEAR);
            show = true;
            JsfUti.update("panelUpload");
        } else {
            JsfUti.messageError(null, "Error", "Todos los campos son obligatorios.");
        }
    }

    public void cargaDocumento() {
        if (respuesta.equals("true")) {
            show = false;
            JsfUti.update("panelUpload");
            JsfUti.messageInfo(null, "El archivo se cargo correctamente.", "");
        } else {
            JsfUti.messageError(null, "ERROR", "No se pudo cargar documento correctamente!!!");
        }
    }

    public void refreshPage() {
        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/cargaExpediente.xhtml");
    }

    public void cargarDocumentoInscripcion(RegMovimiento mov) {
        movimiento = mov;
        cal.setTime(mov.getFechaInscripcion());
        anio = cal.get(Calendar.YEAR);
        JsfUti.update("uploadDoc");
        JsfUti.executeJS("PF('dlgDigitalizacion').show();");
    }

    public void respuestaCargaDocumento() {
        if (respuesta.equals("true")) {
            JsfUti.executeJS("PF('dlgDigitalizacion').hide();");
            JsfUti.messageInfo(null, "El archivo se cargo correctamente.", "");
        } else {
            JsfUti.messageError(null, "ERROR", "No se pudo cargar documento correctamente!!!");
        }
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
            Logger.getLogger(CargaExpediente.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<RegLibro> getLibros() {
        return libros;
    }

    public void setLibros(List<RegLibro> libros) {
        this.libros = libros;
    }

    public RegLibro getLibro() {
        return libro;
    }

    public void setLibro(RegLibro libro) {
        this.libro = libro;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getTomo() {
        return tomo;
    }

    public void setTomo(Integer tomo) {
        this.tomo = tomo;
    }

    public Integer getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Integer inscripcion) {
        this.inscripcion = inscripcion;
    }

    public Integer getFolioInicio() {
        return folioInicio;
    }

    public void setFolioInicio(Integer folioInicio) {
        this.folioInicio = folioInicio;
    }

    public Integer getFolioFin() {
        return folioFin;
    }

    public void setFolioFin(Integer folioFin) {
        this.folioFin = folioFin;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public RegMovimientosLazy getMovsLazy() {
        return movsLazy;
    }

    public void setMovsLazy(RegMovimientosLazy movsLazy) {
        this.movsLazy = movsLazy;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

}
