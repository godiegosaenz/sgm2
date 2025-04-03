/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.origami.sgm.entities.RenTurismoDetalleHoteles;
import java.io.Serializable;

/**
 *
 * @author Joao Sanga
 */
public class HabitacionTurismo implements Serializable{
    
    private Long idTurismoHabitacion;
    
    private String descripcion;
    
    private RenTurismoDetalleHoteles detalle;
    
    public HabitacionTurismo(Long idTurismo, String desc, RenTurismoDetalleHoteles detalle){
        this.idTurismoHabitacion = idTurismo;
        this.descripcion = desc;
        this.detalle = detalle;
    }

    public Long getIdTurismoHabitacion() {
        return idTurismoHabitacion;
    }

    public void setIdTurismoHabitacion(Long idTurismoHabitacion) {
        this.idTurismoHabitacion = idTurismoHabitacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public RenTurismoDetalleHoteles getDetalle() {
        return detalle;
    }

    public void setDetalle(RenTurismoDetalleHoteles detalle) {
        this.detalle = detalle;
    }
    
}
