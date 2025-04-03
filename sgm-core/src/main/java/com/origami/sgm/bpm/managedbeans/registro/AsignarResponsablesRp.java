/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
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
 * @author Anyelo
 */
@Named
@ViewScoped
public class AsignarResponsablesRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected RegpLiquidacionDerechosAranceles liq;
    protected HistoricoTramites ht;
    protected String observacion;
    protected HashMap<String, Object> params;

    protected List<AclUser> amanuenses = new ArrayList<>();
    protected List<AclUser> abogados = new ArrayList<>();
    protected Integer asignado;
    protected Boolean mostrarAbogados = false;
    protected String usuarioTecnicoAbogado;
    protected String usuarioTecnicoAmanuense;

    protected String abogado;
    protected String tecnico;

    protected Boolean completado = false;

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
                abogado = (String) this.getVariable(session.getTaskID(), "abogado");
                tecnico = (String) this.getVariable(session.getTaskID(), "tecnico");
                asignado = (Integer) this.getVariable(session.getTaskID(), "asignado");
                if (asignado != 1) {
                    if (abogado.equals("")) {
                        mostrarAbogados = true;
                        abogados = reg.getUsuariosByRolId(100L);
                    }
                }
                amanuenses = reg.getUsuariosByRolId(80L);
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(AsignarResponsablesRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDialog() {
        if (this.validar()) {
            JsfUti.update("formSelecResp");
            JsfUti.executeJS("PF('dlgSelecResp').show();");
        }
    }

    public void completarTarea() {
        try {
            completado = true;
            JsfUti.update("formSelecResp");
            params = new HashMap<>();
            if (asignado == 1) {
                params.put("revision", 2);
                params.put("tecnico", usuarioTecnicoAmanuense);
            } else {
                if (abogado.equals("")) {
                    params.put("abogado", usuarioTecnicoAbogado);
                    if (usuarioTecnicoAmanuense == null) {
                        params.put("tecnicoAsignado", 2);
                    } else {
                        params.put("tecnicoAsignado", 1);
                        params.put("tecnico", usuarioTecnicoAmanuense);
                    }
                } else {
                    params.put("tecnicoAsignado", 1);
                    params.put("tecnico", usuarioTecnicoAmanuense);
                }
            }
            this.guardarObservacion();
            this.completeTask(this.getTaskId(), params);
            this.continuar();
        } catch (Exception e) {
            JsfUti.messageWarning(null, Messages.error, "");
            Logger.getLogger(AsignarResponsablesRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validar() {
        if (asignado == 1) {
            if (usuarioTecnicoAmanuense != null) {
                return true;
            } else {
                JsfUti.messageWarning(null, "Debe selleccionar el Amanuense.", "");
            }
        } else {
            if (abogado.equals("")) {
                if (usuarioTecnicoAbogado != null) {
                    return true;
                } else {
                    JsfUti.messageWarning(null, "Obligatorio seleccionar Abogado.", "");
                }
            } else {
                if (usuarioTecnicoAmanuense != null) {
                    return true;
                } else {
                    JsfUti.messageWarning(null, "Ya fue seleccionado el abogado: " + abogado + ", debe seleccionar el amanuense.", "");
                }
            }

        }
        return false;
    }

    public void guardarObservacion() {
        String obv;
        if (usuarioTecnicoAmanuense != null && usuarioTecnicoAbogado != null) {
            obv = "Tecnico asignado: " + usuarioTecnicoAmanuense + ", abogado asignado: " + usuarioTecnicoAbogado;
        } else if (usuarioTecnicoAmanuense != null) {
            obv = "Tecnico asignado: " + usuarioTecnicoAmanuense;
        } else {
            obv = "Abogado asignado: " + usuarioTecnicoAbogado;
        }
        try {
            Observaciones ob = new Observaciones();
            ob.setObservacion(obv);
            ob.setEstado(true);
            ob.setFecCre(new Date());
            ob.setIdTramite(ht);
            ob.setTarea(this.getTaskDataByTaskID().getName());
            ob.setUserCre(session.getName_user());
            acl.persist(ob);
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

    public Boolean getMostrarAbogados() {
        return mostrarAbogados;
    }

    public void setMostrarAbogados(Boolean mostrarAbogados) {
        this.mostrarAbogados = mostrarAbogados;
    }

    public List<AclUser> getAmanuenses() {
        return amanuenses;
    }

    public void setAmanuenses(List<AclUser> amanuenses) {
        this.amanuenses = amanuenses;
    }

    public List<AclUser> getAbogados() {
        return abogados;
    }

    public void setAbogados(List<AclUser> abogados) {
        this.abogados = abogados;
    }

    public String getUsuarioTecnicoAbogado() {
        return usuarioTecnicoAbogado;
    }

    public void setUsuarioTecnicoAbogado(String usuarioTecnicoAbogado) {
        this.usuarioTecnicoAbogado = usuarioTecnicoAbogado;
    }

    public String getUsuarioTecnicoAmanuense() {
        return usuarioTecnicoAmanuense;
    }

    public void setUsuarioTecnicoAmanuense(String usuarioTecnicoAmanuense) {
        this.usuarioTecnicoAmanuense = usuarioTecnicoAmanuense;
    }

    public Boolean getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }

}
