/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.servicetask.LectorArchivos;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.Archivo;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class SubirDocumentoRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected RegpCertificadosInscripciones cert;
    protected Long idTarea;
    protected String formatoArchivos = SisVars.formatoArchivos;
    //protected String formatoArchivos = "/(\\.|\\/)(pdf)$/";
    protected LectorArchivos lectorArchivos;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null && idTarea != null) {
                this.setTaskId(session.getTaskID());
                cert = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
                Long id = (Long) this.getVariable(this.getTaskId(), "tramite");
                ht = reg.getHistoricoTramiteById(id);
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(SubirDocumentoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlg() {
        if (this.validaFiles() && this.getFiles().size() == 1) {
            JsfUti.update("formConfirma");
            JsfUti.executeJS("PF('dlgConfirma').show();");
        } else {
            JsfUti.messageError(null, "Debe cargar un documento para completar la tarea.", "");
        }
    }

    public void completarTarea() {
        try {
            List<Archivo> archivos = (List<Archivo>) this.getVariable(this.getTaskId(), "listaArchivos");
            if(archivos == null){
                archivos = new ArrayList<>();
            }
            archivos.add(this.getFiles().get(0));
            this.setVariableByProcessInstance(ht.getIdProceso(), "listaArchivos", archivos);
            this.actualizarTarea();
            this.continuar();
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(SubirDocumentoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarTarea() {
        Calendar cal = Calendar.getInstance();
        try {
            cert.setRealizado(true);
            cert.setFechaFin(cal.getTime());
            acl.persist(cert);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

}
