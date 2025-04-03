/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
public class DigitalizarDocumentosRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected String formatoArchivos;
    protected String observacion;
    protected Boolean finalScan = false;
    private HashMap<String, Object> par;

    protected List<RegpCertificadosInscripciones> tareas = new ArrayList<>();
    protected RegMovimiento mov = new RegMovimiento();
    protected Integer anio;
    protected Integer iniciarTramite;
    protected String respuesta;
    protected RegpCertificadosInscripciones cer = new RegpCertificadosInscripciones();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null) {
                this.setTaskId(session.getTaskID());
                Long id = (Long) this.getVariable(session.getTaskID(), "tramite");
                if (id != null) {
                    ht = reg.getHistoricoTramiteById(id);
                    Integer revision = (Integer) this.getVariable(session.getTaskID(), "revision");
                    if (revision != null) {
                        if (revision == 2) {
                            finalScan = true;
                            tareas = reg.saveTareasFinalScann(ht.getId());
                        }
                    }
                } else {
                    this.continuar();
                }
                formatoArchivos = SisVars.formatoArchivos;
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(DigitalizarDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarDocInscripcion(RegpCertificadosInscripciones ce) {
        try {
            if (ce.getIdMovimiento() != null) {
                cer = ce;
                Calendar cal = Calendar.getInstance();
                mov = reg.getRegMovimientoById(ce.getIdMovimiento());
                if (mov != null) {
                    cal.setTime(mov.getFechaInscripcion());
                    anio = cal.get(Calendar.YEAR);
                    JsfUti.update("uploadDoc");
                    JsfUti.executeJS("PF('dlgDigitalizacion').show();");
                } else {
                    JsfUti.messageError(null, "Error", "No se encontro el movimiento.");
                }
            } else {
                JsfUti.messageError(null, "Error", "No hay inscripcion asociada.");
            }
        } catch (Exception e) {
            Logger.getLogger(DigitalizarDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void respuestaCargaDocumento() {
        try {
            if (respuesta.equalsIgnoreCase("true")) {
                if (cer.getId() != null) {
                    cer.setRealizado(true);
                    cer.setFechaFin(new Date());
                    acl.persist(cer);
                    tareas.remove(tareas.indexOf(cer));
                    tareas.add(cer);
                    JsfUti.update("mainForm:mainTab:dtTareas");
                    JsfUti.executeJS("PF('dlgDigitalizacion').hide();");
                    JsfUti.messageInfo(null, "El archivo se cargo correctamente.", "");
                } else {
                    JsfUti.messageError(null, Messages.error, "");
                }
            } else {
                JsfUti.messageError(null, "Error", "No se pudo cargar el archivo correctamente.");
            }
        } catch (Exception e) {
            Logger.getLogger(DigitalizarDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validar() {
        if (tareas.isEmpty()) {
            return false;
        }
        for (RegpCertificadosInscripciones ce : tareas) {
            if (!ce.getRealizado()) {
                JsfUti.messageError(null, "Faltan completar tareas.", "");
                return false;
            }
        }
        return true;
    }

    public void completarTareaDigEscritura() {
        try {
            if (this.validar()) {
                Observaciones ob = new Observaciones();
                ob.setObservacion("Digitalizacion de Escrituras Finales");
                ob.setEstado(true);
                ob.setFecCre(new Date());
                ob.setIdTramite(ht);
                ob.setTarea(this.getTaskDataByTaskID().getName());
                ob.setUserCre(session.getName_user());
                acl.persist(ob);
                par = new HashMap<>();
                this.completeTask(this.getTaskId(), par);
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(DigitalizarDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public void showDlgObservacion() {
        if (this.validaFiles()) {
            observacion = "";
            JsfUti.update("formObs");
            JsfUti.executeJS("PF('dlgObsvs').show();");
        }
    }

    public void completarTarea() {
        try {
            if (observacion != null) {
                Observaciones ob = new Observaciones();
                ob.setObservacion(observacion);
                ob.setEstado(true);
                ob.setFecCre(new Date());
                ob.setIdTramite(ht);
                ob.setTarea(this.getTaskDataByTaskID().getName());
                ob.setUserCre(session.getName_user());
                acl.persist(ob);

                par = new HashMap<>();
                par.put("listaArchivos", this.getFiles());
                par.put("subCarpeta", ht.getCarpetaRep());
                this.completeTask(this.getTaskId(), par);
                this.continuar();

            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(DigitalizarDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Boolean getFinalScan() {
        return finalScan;
    }

    public void setFinalScan(Boolean finalScan) {
        this.finalScan = finalScan;
    }

    public List<RegpCertificadosInscripciones> getTareas() {
        return tareas;
    }

    public void setTareas(List<RegpCertificadosInscripciones> tareas) {
        this.tareas = tareas;
    }

    public RegMovimiento getMov() {
        return mov;
    }

    public void setMov(RegMovimiento mov) {
        this.mov = mov;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

}
