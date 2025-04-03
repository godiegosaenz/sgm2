package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @Date 08/06/2016
 */
@Named
@ViewScoped
public class GenerarResolucion extends Busquedas implements Serializable {

    public static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(GenerarResolucion.class.getName());

    @javax.inject.Inject
    private Entitymanager services;
    
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenTipoValor tipoValor;

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
                    entradas.put("resolucion", new FnResolucion(new Date(), true, session.getName_user(), new Date()));
                    //entradas.put("predios", ps);
                    entradas.put("valorPorcentaje", false);
                }

            } else {
                this.continuar();
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "Iniciar Generar Exoneración", e);
        }
    }
    
    public void escuchar() {
        if ((((FnSolicitudExoneracion) entradas.get("solicitud")).getTipoValor().getId() == 2)) {
            entradas.put("valorPorcentaje", true);
        } else if ((((FnSolicitudExoneracion) entradas.get("solicitud")).getTipoValor().getId() == 4)) {
            entradas.put("valorPorcentaje", false);
        }
    }
    
    public List<RenTipoValor> getRenTipoValores() {
        Set<Long> ids = new HashSet<>();
        ids.add(2L);
        ids.add(4L);
        return getServices().tipoValorList(ids);
    }

    public void mostrarObs() {
        if (!this.validaFiles()) {
            JsfUti.messageError(null, "Advertencia", "Debe Subir la resolución");
            return;
        }
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");

    }

    public void procesar() {
        if (!this.validaFiles()) {
            JsfUti.messageError(null, "Advertencia", "Debe Subir la resolución");
            return;
        }
        String obs = (String) entradas.get("obs");
        if (obs == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones.");
            return;
        }
        FnSolicitudExoneracion sol = (FnSolicitudExoneracion) entradas.get("solicitud");
        sol.setTipoValor(tipoValor);
        FnResolucion res = (FnResolucion) entradas.get("resolucion");
        if (res.getNumeroResolucion() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar Número de Resolución.");
            return;
        }
        if (res.getNumeroOficio() == null) {
            res.setNumeroOficio("0");
        }
                
        Observaciones o = new Observaciones();
        o.setEstado(true);
        o.setFecCre(new Date());
        o.setTarea(getTaskDataByTaskID().getDescription());
        o.setIdTramite(ht);
        o.setUserCre(session.getName_user());
        o.setObservacion(obs);
        o = getServices().guardarObservacionCraerResolucion(sol, res, o);
//        o = getServices().permisoServices().guardarObservacion(o);
        if (o != null) {
            JsfUti.messageInfo(null, "Información", "Tarea completada correctamete.");
            HashMap<String, Object> map = new HashMap<>();
            map.put("listaArchivos", this.getFiles());
            map.put("listaArchivosFinal", new ArrayList<>());
            map.put("carpeta", ht.getCarpetaRep());
            map.put("tramite", ht.getId());
            map.put("prioridad", 50);
            
            this.completeTask(this.getTaskId(), map);
        } else {
            JsfUti.messageError(null, "Error", "Ocurrio un error al completar la tarea.");
        }

        JsfUti.executeJS("PF('obs').hide()");
        JsfUti.update("frmGenRes");
        this.continuar();
    }

    public GenerarResolucion() {
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

    public RenTipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(RenTipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }
    
}
