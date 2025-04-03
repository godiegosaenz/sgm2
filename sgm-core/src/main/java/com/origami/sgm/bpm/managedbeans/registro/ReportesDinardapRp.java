/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.services.interfaces.registro.AnexosRegistroPropiedadServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class ReportesDinardapRp implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected AnexosRegistroPropiedadServices anexo;

    protected Integer tipoAnexo = 0;
    protected Date fechaInicio = new Date();
    //protected Date fechaFin = new Date();
    protected Date hoy = new Date();
    protected SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {

    }

    public void generarReporteTxt() {
        try {
            //if (fechaFin.after(fechaInicio) || fechaFin.equals(fechaInicio)) {
            if (fechaInicio.before(hoy)) {
                String inicio = fecha.format(fechaInicio);
                //String fin = fecha.format(Utils.sumarRestarDiasFecha(fechaFin, 1));
                switch (tipoAnexo) {
                    case 0:
                        JsfUti.messageWarning(null, "Debe seleccionar el Tipo de Anexo.", "");
                        break;
                    case 1:
                        //anexo.anexoDatoPublico(inicio, fin);
                        String cadena = this.nombreReporte(fechaInicio);
                        anexo.anexoDatoPublico(inicio, cadena);
                        JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/ReporteAnexoRegistro?name=Anexo1&fecha=" + cadena);
                        break;
                    case 2:
                        //anexo.anexoMercantilContratos(inicio, fin);
                        //JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/ReporteAnexoRegistro?name=Anexo2");
                        break;
                    case 3:
                        //anexo.anexoMercantilSociedad(inicio, fin);
                        //JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/ReporteAnexoRegistro?name=Anexo3");
                        break;
                    default:
                        JsfUti.messageWarning(null, Messages.error, "");
                        break;
                }
            } else {
                //JsfUti.messageWarning(null, "Fecha fin debe ser mayor o igual a Fecha de inicio.", "");
                JsfUti.messageWarning(null, "La fecha del reporte no debe ser mayor a Hoy.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(ReportesDinardapRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String nombreReporte(Date fecha) {
        String result = "", mes, dia;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            if (cal.get(Calendar.MONTH) < 9) {
                mes = "0" + (cal.get(Calendar.MONTH) + 1);
            } else {
                mes = (cal.get(Calendar.MONTH) + 1) + "";
            }
            if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                dia = "0" + cal.get(Calendar.DAY_OF_MONTH);
            } else {
                dia = cal.get(Calendar.DAY_OF_MONTH) + "";
            }
            result = cal.get(Calendar.YEAR) + "_" + mes + "_" + dia;
        } catch (Exception e) {
            Logger.getLogger(ReportesDinardapRp.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /*public Date getFechaFin() {
     return fechaFin;
     }

     public void setFechaFin(Date fechaFin) {
     this.fechaFin = fechaFin;
     }*/
    public Integer getTipoAnexo() {
        return tipoAnexo;
    }

    public void setTipoAnexo(Integer tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

}
