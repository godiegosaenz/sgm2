/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.managedbeans.component.Busquedas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class AprobarResolucion extends Busquedas implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AprobarResolucion.class.getName());

    @Inject
    private UserSession uSession;

    private Map<String, Object> entradas;
    private HistoricoTramites ht;

    @PostConstruct
    public void initView() {
        try {
            if (session != null && session.getTaskID() != null) {
                entradas = new HashMap<>();
                entradas.put("obs", "");
                this.setTaskId(session.getTaskID());
                ht = getServices().permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    List<CatPredio> ps = new ArrayList<>();
                    if (ht.getHistoricoTramiteDetCollection() != null) {
                        for (HistoricoTramiteDet col : ht.getHistoricoTramiteDetCollection()) {
                            ps.add(col.getPredio());
                        }
                    }
                    entradas.put("solicitud", getServices().getSolicitudExoneracion(ht));
                    entradas.put("predios", ps);
                }

            } else {
                this.continuar();
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "Iniciar Generar Exoneración", e);
        }
    }

    public void mostrarObs() {
//        if (!this.validaFiles()) {
//            JsfUti.messageError(null, "Advertencia", "Debe Subir la resolución");
//            return;
//        }
        entradas.put("obs", "");
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");

    }

    public void procesar() {
        try {
            String obs = (String) entradas.get("obs");
            if (obs == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones.");
                return;
            }
            Observaciones o = new Observaciones();
            o.setEstado(true);
            o.setFecCre(new Date());
            o.setTarea(getTaskDataByTaskID().getDescription());
            o.setIdTramite(ht);
            o.setUserCre(session.getName_user());
            o.setObservacion(obs);
            o = getServices().permisoServices().guardarObservacion(o);
            if (o != null) {
                JsfUti.messageInfo(null, "Información", "Tarea completada correctamete.");
                HashMap<String, Object> map = new HashMap<>();
                map.put("tramite", this.getFiles());
                /*GeTipoTramite rentas = getServices().buscarTipoTramiteDep(12L, "REN");
                if (rentas != null) {
                    map.put("renta", rentas.getUserDireccion());
                }*/
                map.put("aprobado", true);
                map.put("aprobadoRes", true);
                map.put("taskdef", getTaskDataByTaskID().getDescription());
                map.put("carpeta", ht.getCarpetaRep());
                map.put("descripcion", ht.getTipoTramiteNombre());
                map.put("prioridad", 50);
                map.put("oficio", "");
                this.completeTask(this.getTaskId(), map);
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio un error al completar la tarea.");
            }
            
            JsfUti.executeJS("PF('obs').hide()");
            this.continuar();
        } catch (Exception e) {
            LOG.log(Level.OFF, "Completar Tarea aprobar Resolución");
        }
    
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

}
