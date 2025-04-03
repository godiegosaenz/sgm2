/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.coactiva;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.CoaEstadoJuicio;
import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
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
public class OficioCoactiva extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RecaudacionesService rs;

    protected String observacion;
    protected String formatoArchivos;
    protected Integer tarea;
    protected Integer estado = 0;
    protected HistoricoTramites ht;
    protected CoaJuicio juicio;
    protected List<RenLiquidacion> titulos = new ArrayList<>();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null) {
                Object ob = this.getVariable(session.getTaskID(), "tramite");
                if (ob != null) {
                    this.setTaskId(session.getTaskID());
                    ht = rs.getHistoricoTramiteByNumTramite((Long) ob);
                    juicio = ht.getCoaJuicio();
                    formatoArchivos = SisVars.formatoArchivos;
                    tarea = (Integer) this.getVariable(session.getTaskID(), "tarea");
                    // corregir consulta salen id duplicados cuando la misma liquidacion se encuentra en dos o mas juicios
                    // no deberia darse ese caso de duplicidad, pero aparece por la pruebas con los datos en desarrollo
                    titulos = rs.getLiquidacionesCoactivaByJuicio(juicio.getId());
                } else {
                    this.continuar();
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(OficioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgObservacion(Integer in) {
        estado = in;
        JsfUti.update("formDoc");
        JsfUti.executeJS("PF('dlgDocumento').show();");
    }

    public void completarTarea() {
        try {
            if (tarea != null) {
                HashMap par = new HashMap<>();
                par.put("listaArchivos", this.getFiles());
                // SE HIZO CON CASE POR MOTIVO DE QUE EL FLUJO COACTIVA PODRIA CRECER Y AUMENTAR LAS TAREAS 
                // POR EL NUMERO DE LA TAREA EN EL FLUJO
                switch (tarea) {
                    case 1:
                        Boolean temp = rs.updateEmisionesJuicio(juicio.getId());
                        if (!temp) {
                            return;
                        }
                        this.updateJuicio(2L); // ID ESTADO REPORTE DE CITACIONES
                        par.put("tarea", estado);
                        par.put("citacion", estado);
                        break;
                    case 2:
                        this.updateJuicio(3L); // ID ESTADO EMISION MEDIDAS CAUTELARES
                        if (estado == 2) {
                            par.put("tarea", 3);
                        }
                        par.put("medidas", estado);
                        break;
                    case 3:
                        /*
                        for (RenLiquidacion titulo : titulos) {
                            if (titulo.getEstadoLiquidacion().getId().equals(2L)) {
                                JsfUti.messageInfo(null, "Exite Emisiones asociadas al Juicio en Estado Pendiente de Pago", "");
                                return;
                            }
                        }*/
                        for (CoaJuicioPredio jp : juicio.getCoaJuicioPredioCollection()) {
                            if (jp.getEstado()&& jp.getLiquidacion().getEstadoLiquidacion().getId().equals(2L)) {
                                JsfUti.messageInfo(null, "Exite Emisiones asociadas al Juicio en Estado Pendiente de Pago", "");
                                return;
                            }
                        }
                        this.updateJuicio(4L); // ID ESTADO BAJA Y ARCHIVO
                        ht.setEstado("Finalizado");
                        acl.persist(ht);
                        break;
                    default:
                        JsfUti.messageError(null, Messages.error, "");
                        break;
                }
                this.saveObservacion();
                this.completeTask(this.getTaskId(), par);
                this.continuar();
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            Logger.getLogger(OficioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void saveObservacion() {
        try {
            if (observacion != null && !observacion.trim().isEmpty()) {
                Observaciones ob = new Observaciones();
                ob.setFecCre(new Date());
                ob.setIdTramite(ht);
                ob.setObservacion(observacion);
                ob.setUserCre(session.getName_user());
                ob.setTarea(this.getTaskDataByTaskID().getName());
                acl.persist(ob);
            }
        } catch (Exception e) {
            Logger.getLogger(OficioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void updateJuicio(Long idEstado) {
        try {
            if (idEstado != null) {
                juicio.setEstadoJuicio(new CoaEstadoJuicio(idEstado));
                acl.persist(juicio);
            }
        } catch (Exception e) {
            Logger.getLogger(OficioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getTarea() {
        return tarea;
    }

    public void setTarea(Integer tarea) {
        this.tarea = tarea;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CoaJuicio getJuicio() {
        return juicio;
    }

    public void setJuicio(CoaJuicio juicio) {
        this.juicio = juicio;
    }

    public List<RenLiquidacion> getTitulos() {
        return titulos;
    }

    public void setTitulos(List<RenLiquidacion> titulos) {
        this.titulos = titulos;
    }

}
