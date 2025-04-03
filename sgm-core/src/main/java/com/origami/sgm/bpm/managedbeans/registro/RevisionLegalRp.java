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
import java.util.Date;
import java.util.HashMap;
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
public class RevisionLegalRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected RegpLiquidacionDerechosAranceles liq;
    protected String observacion;
    protected HashMap<String, Object> pars;
    protected Integer cantidad;
    protected Integer iniciarTramite;
    protected String abogado;

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
                iniciarTramite = (Integer) this.getVariable(session.getTaskID(), "iniciarTramite");
                abogado = this.getVariable(session.getTaskID(), "abogado").toString();
                ht = reg.getHistoricoTramiteById(id);
                liq = ht.getRegpLiquidacionDerechosAranceles();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RevisionLegalRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgAprobado() {
        observacion = "";
        JsfUti.update("formObs");
        JsfUti.executeJS("PF('dlgObsvs').show();");
    }

    public void showDlgObservacion() {
        observacion = "";
        JsfUti.update("formObsEdit");
        JsfUti.executeJS("PF('dlgEditObs').show();");
    }

    public void completarTarea() {
        try {
            if (observacion != null) {
                liq.setObservacion(null);
                acl.persist(liq);
                this.guardar(observacion);
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            Logger.getLogger(RevisionLegalRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void tramiteConObservaciones() {
        try {
            if (observacion != null) {
                liq.setObservacion(observacion);
                acl.persist(liq);
                this.guardar("Tramite Observado.");
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            Logger.getLogger(RevisionLegalRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardar(String cadena) {
        try {
            Observaciones ob = new Observaciones();
            ob.setObservacion(cadena);
            ob.setEstado(true);
            ob.setFecCre(new Date());
            ob.setIdTramite(ht);
            if(this.getTaskDataByTaskID()==null){
                JsfUti.messageError(null, "Error", "Realize nuevamente la acción");
                return;
            }
            ob.setTarea(this.getTaskDataByTaskID().getName());
            ob.setUserCre(session.getName_user());
            acl.persist(ob);
            if(iniciarTramite != 1){
                reg.updateUserConTareas(abogado, cantidad);
            }
            pars = new HashMap<>();
            pars.put("subCarpeta", ht.getCarpetaRep());
            this.completeTask(this.getTaskId(), pars);
            this.continuar();
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RevisionLegalRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public RegpLiquidacionDerechosAranceles getLiq() {
        return liq;
    }

    public void setLiq(RegpLiquidacionDerechosAranceles liq) {
        this.liq = liq;
    }

}
