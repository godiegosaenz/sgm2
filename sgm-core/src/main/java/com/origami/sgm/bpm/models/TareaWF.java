/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.HistoricoTramites;
import java.io.Serializable;
import java.math.BigInteger;
import org.activiti.engine.task.Task;

/**
 *
 * @author User
 */
public class TareaWF implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Long idTramite;
    protected BigInteger numTramiteXdep;
    protected Task tarea;
    protected Boolean descripcionTareaMayor50char = false;
    protected HistoricoTramites tramite;
    protected Long tramiteRp;
    protected boolean seleccionada = false;

    public TareaWF() {
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public BigInteger getNumTramiteXdep() {
        return numTramiteXdep;
    }

    public void setNumTramiteXdep(BigInteger numTramiteXdep) {
        this.numTramiteXdep = numTramiteXdep;
    }

    public Task getTarea() {
        return tarea;
    }

    public void setTarea(Task tarea) {
        this.tarea = tarea;
    }

    public Boolean getDescripcionTareaMayor50char() {
        return descripcionTareaMayor50char;
    }

    public void setDescripcionTareaMayor50char(Boolean descripcionTareaMayor50char) {
        this.descripcionTareaMayor50char = descripcionTareaMayor50char;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Long getTramiteRp() {
        return tramiteRp;
    }

    public void setTramiteRp(Long tramiteRp) {
        this.tramiteRp = tramiteRp;
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.idTramite != null ? this.idTramite.hashCode() : 0);
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
        final TareaWF other = (TareaWF) obj;
        if ((this.idTramite == null) ? (other.idTramite != null) : !this.idTramite.equals(other.idTramite)) {
            return false;
        }
        return true;
    }
    
}
