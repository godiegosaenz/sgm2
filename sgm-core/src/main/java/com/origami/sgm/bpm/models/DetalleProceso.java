/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.activiti.engine.history.HistoricTaskInstance;

/**
 *
 * @author Max
 */
public class DetalleProceso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idProceso;
    private String nombreProceso;
    private Date fechaInicio;
    private Date fechaFin;
    private String prioridad;
    private String estado;
    private List<HistoricTaskInstance> tasks;
    private String instancia;

    public DetalleProceso() {
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<HistoricTaskInstance> getTasks() {
        return tasks;
    }

    public void setTasks(List<HistoricTaskInstance> tasks) {
        this.tasks = tasks;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getInstancia() {
        return instancia;
    }

    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.idProceso != null ? this.idProceso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DetalleProceso other = (DetalleProceso) obj;
        if ((this.idProceso == null) ? (other.idProceso != null) : !this.idProceso.equals(other.idProceso)) {
            return false;
        }
        return true;
    }
}
