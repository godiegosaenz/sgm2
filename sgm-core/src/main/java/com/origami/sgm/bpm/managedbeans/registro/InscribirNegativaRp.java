/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
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
public class InscribirNegativaRp extends BpmManageBeanBaseRoot implements Serializable {
    
    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected RegpLiquidacionDerechosAranceles liq;
    protected String observacion;
    protected HashMap<String, Object> params;
    protected List<RegpCertificadosInscripciones> list = new ArrayList<>();
    protected RegpCertificadosInscripciones tarea;
    protected Integer opcion = 0;
    
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
                ht = reg.getHistoricoTramiteById(id);
                liq = ht.getRegpLiquidacionDerechosAranceles();
                if (liq.getRegpCertificadosInscripcionesCollection().isEmpty()) {
                    list = reg.getListTareasByLiquidacion(liq.getId());
                } else {
                    list = (List<RegpCertificadosInscripciones>) liq.getRegpCertificadosInscripcionesCollection();
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(InscribirNegativaRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void showDlgCompletTask() {
        if (this.validar()) {
            JsfUti.update("formObs");
            JsfUti.executeJS("PF('dlgObsvs').show();");
        } else {
            JsfUti.messageInfo(null, "Debe realizar todas las Tareas del Tramite.", "");
        }
    }
    
    public Boolean validar() {
        for (RegpCertificadosInscripciones cert : list) {
            if (!cert.getRealizado()) {
                return false;
            }
        }
        return true;
    }
    
    public void realizarTarea(RegpCertificadosInscripciones task) {
        try {
            tarea = task;
            if (!tarea.getRealizado()) {
                if (tarea.getActo().getTipoActo().getId().equals(2L) || tarea.getActo().getTipoActo().getId().equals(4L)) {
                    JsfUti.update("formSubeDoc");
                    JsfUti.executeJS("PF('dlgSubeDoc').show();");
                } else {
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/realizarNegativaRP.xhtml?code=" + tarea.getId());
                }
            } else {
                JsfUti.messageInfo(null, "Esta tarea ya fue realizada.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void redirectRealizar() {
        if (tarea != null) {
            if (opcion == 1) {
                String url = reg.getUrlByTarea(tarea.getId());
                if (url != null) {
                    session.setTaskID(this.getTaskId());
                    JsfUti.redirectFaces(url + "?code=" + tarea.getId());
                } else {
                    JsfUti.messageError(null, Messages.error, "");
                }
            } else if (opcion == 2) {
                session.setTaskID(this.getTaskId());
                // Redirec a Negativa Manual
                // JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/subirDocumentoRp.xhtml?code=" + tarea.getId());
            } else {
                JsfUti.messageInfo(null, "Seleccione una opcion ", "");
            }
        }
    }
    
    public void completarTarea(){
        try {
            //setear 4 en la variable iniciarTramite para la entrega de documentos
            params.put("iniciarTramite", 4);
        } catch (Exception e) {
            Logger.getLogger(InscribirNegativaRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RegpLiquidacionDerechosAranceles getLiq() {
        return liq;
    }

    public void setLiq(RegpLiquidacionDerechosAranceles liq) {
        this.liq = liq;
    }

    public List<RegpCertificadosInscripciones> getList() {
        return list;
    }

    public void setList(List<RegpCertificadosInscripciones> list) {
        this.list = list;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }
    
}
