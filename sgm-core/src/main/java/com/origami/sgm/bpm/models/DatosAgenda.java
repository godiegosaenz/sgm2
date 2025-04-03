/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.TipoAviso;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosLoorVargas
 */
public class DatosAgenda implements Serializable {
    
    private Agenda agenda;
    private List<AgendaDet> detAgenda;
    private TipoAviso avisos;
    private List<AgendaModel> modelAgenda;
    private String mensaje;
    private HistoricoTramites tramite;
    
    private static final long serialVersionUID = 1L;

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public List<AgendaDet> getDetAgenda() {
        return detAgenda;
    }

    public void setDetAgenda(List<AgendaDet> detAgenda) {
        this.detAgenda = detAgenda;
    }

    public TipoAviso getAvisos() {
        return avisos;
    }

    public void setAvisos(TipoAviso avisos) {
        this.avisos = avisos;
    }

    public List<AgendaModel> getModelAgenda() {
        return modelAgenda;
    }

    public void setModelAgenda(List<AgendaModel> modelAgenda) {
        this.modelAgenda = modelAgenda;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }
    
    
}
