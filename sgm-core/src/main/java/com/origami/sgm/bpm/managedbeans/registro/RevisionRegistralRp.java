/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class RevisionRegistralRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected RegpLiquidacionDerechosAranceles liq;
    protected String observacion;
    protected HashMap<String, Object> par;
    protected Integer cantidad;
    protected String tecnico;
    protected String formatoArchivos = "/(\\.|\\/)(pdf)$/";

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
                cantidad = (Integer) this.getVariable(session.getTaskID(), "cantidad");
                tecnico = this.getVariable(session.getTaskID(), "tecnico").toString();
                ht = reg.getHistoricoTramiteById(id);
                liq = ht.getRegpLiquidacionDerechosAranceles();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgObs() {
        if (liq.getObservacion() != null) {
            JsfUti.update("formObsEdit");
            JsfUti.executeJS("PF('dlgEditObs').show();");
        } else {
            JsfUti.messageInfo(null, "No hay Observacion Legal.", "");
        }
    }

    public void showDlgAprobado() {
        observacion = "";
        JsfUti.update("formObs");
        JsfUti.executeJS("PF('dlgObsvs').show();");
    }

    public void showDlgDevolutiva() {
        this.setFiles(new ArrayList<Archivo>());
        JsfUti.update("formDevolutiva");
        JsfUti.executeJS("PF('dlgDevolutiva').show();");
    }

    /*public void showDlgNegativa() {
        observacion = "";
        JsfUti.update("formNegativa");
        JsfUti.executeJS("PF('dlgObsNegativa').show();");
    }*/
    
    public void completarTarea(){
        try {
            if(observacion != null){
                this.guardarObservacion(observacion);
                par = new HashMap<>();
                par.put("aprobado", 1);
                par.put("revision", 2);
                this.completeTask(this.getTaskId(), par);
                this.continuar();
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void enviarDevolutiva() {
        try {
            if (this.getFiles().size() == 1) {
                this.guardarObservacion("Se adjunta nota Devolutiva.");
                par = new HashMap<>();
                par.put("aprobado", 2);
                par.put("devolutiva", Boolean.TRUE);
                par.put("listaArchivos", this.getFiles());
                par.put("subCarpeta", ht.getCarpetaRep());
                this.completeTask(this.getTaskId(), par);
                this.continuar();
            } else {
                JsfUti.messageInfo(null, "Debe subir un Documento.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /* SE COMENTA POR MOTIVO QUE EL FLUJO DE REGISTRO PROPIEDAD SE ELIMINA LA TAREA NEGATIVA
    YA QUE ES UNA INSCRIPCION NORMAL*/
    /*public void tramiteConNegativa(){
        try {
            if(observacion != null){
                this.guardarObservacion(observacion);
                params = new HashMap<>();
                params.put("aprobado", 1);
                params.put("revision", 1);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }*/

    public void guardarObservacion(String obv) {
        try {
            if(this.getTaskDataByTaskID()!=null){
                Observaciones ob = new Observaciones();
                ob.setObservacion(obv);
                ob.setEstado(true);
                ob.setFecCre(new Date());
                ob.setIdTramite(ht);
                ob.setTarea(this.getTaskDataByTaskID().getName());
                ob.setUserCre(session.getName_user());
                acl.persist(ob);
            }
        } catch (Exception e) {
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

}
