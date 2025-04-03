/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.predio.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author OrigamiSolutions
 */
public class ModelLockPredio implements Serializable{
    private Long idpredio;
    private Date fechaBloqueo;

    public Long getIdpredio() {
        return idpredio;
    }

    public void setIdpredio(Long idpredio) {
        this.idpredio = idpredio;
    }

    public Date getFechaBloqueo() {
        return fechaBloqueo;
    }

    public void setFechaBloqueo(Date fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }
    
    public Long getEditTime(){
        return new Date().getTime() - fechaBloqueo.getTime();
    }

    public Integer getExecuteMinute(){
       return  (int) ((this.getEditTime() / (1000*60)) % 60); 
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.idpredio);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelLockPredio other = (ModelLockPredio) obj;
        if (!Objects.equals(this.idpredio, other.idpredio)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModelLockPredio{" + "idpredio=" + idpredio + ", fechaBloqueo=" + fechaBloqueo + '}';
    }
    
    
    
    
}
