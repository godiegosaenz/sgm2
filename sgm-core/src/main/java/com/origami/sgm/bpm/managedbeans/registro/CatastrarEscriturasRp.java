/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
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
 * @author hpilco
 */
@Named
@ViewScoped
public class CatastrarEscriturasRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;
    
    protected HashMap<String, Object> params;
    protected HistoricoTramites ht;
    protected List<RegpCertificadosInscripciones> lista = new ArrayList<>();
    protected String observacion;

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
                lista = reg.saveTareasCatastroTransferencia(ht.getId());
                if (lista.isEmpty()) {
                    JsfUti.messageError(null, Messages.error, "");
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
            this.continuar();
        }
    }

    public void realizarTarea(RegpCertificadosInscripciones tarea) {
        try {
            if (!tarea.getRealizado()) {
                session.setTaskID(this.getTaskId());
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/transferenciaDominio.xhtml?codeTask=" + tarea.getId());
            } else {
                JsfUti.messageInfo(null, "Esta tarea ya fue realizada.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgCompletTask() {
        if (this.validar()) {
            JsfUti.update("formObs");
            JsfUti.executeJS("PF('dlgObsvs').show();");
        } else {
            JsfUti.messageInfo(null, "Debe realizar todas las Tareas del Tramite o no hay tareas para realizar.", "");
        }
    }

    public Boolean validar() {
        if (lista.isEmpty()) {
            return false;
        }
        for (RegpCertificadosInscripciones cert : lista) {
            if (!cert.getRealizado()) {
                return false;
            }
        }
        return true;
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
                
                reg.restarTareasTransferenciaUser(session.getName_user(), lista.size());
                
                params = new HashMap<>();
                params.put("catastrar", 1);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(CatastrarEscriturasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public List<RegpCertificadosInscripciones> getLista() {
        return lista;
    }

    public void setLista(List<RegpCertificadosInscripciones> lista) {
        this.lista = lista;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
