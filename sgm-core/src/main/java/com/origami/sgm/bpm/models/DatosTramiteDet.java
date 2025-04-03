/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosTramiteDet implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long seq;
    private String tarea;
    private String resposnable;
    private Date inicio;
    private Date fin;
    private Long predios;

    public DatosTramiteDet() {
    }

    public DatosTramiteDet(Long seq, String tarea, String resposnable, Date inicio, Date fin) {
        this.seq = seq;
        this.tarea = tarea;
        this.resposnable = resposnable;
        this.inicio = inicio;
        this.fin = fin;
    }
    
    public DatosTramiteDet(Long seq, String tarea, String resposnable, Date inicio, Date fin, Long predios) {
        this.seq = seq;
        this.tarea = tarea;
        this.resposnable = resposnable;
        this.inicio = inicio;
        this.fin = fin;
        this.predios = predios;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getResposnable() {
        return resposnable;
    }

    public void setResposnable(String resposnable) {
        this.resposnable = resposnable;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Long getPredios() {
        return predios;
    }

    public void setPredios(Long predios) {
        this.predios = predios;
    }

}
