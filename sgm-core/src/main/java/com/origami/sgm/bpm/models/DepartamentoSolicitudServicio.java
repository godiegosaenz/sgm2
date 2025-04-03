/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;

/**
 * Representa las variables del subproceso Interno de la Solicitud de Servicios.
 * @author Henry Pilco
 */

public class DepartamentoSolicitudServicio implements Serializable{
    private static final long serialVersionUID = 1L;
    //Variables consultadas de la Tabla SvSoicitudDepartamento
    private Long idDepartamento;
    private String director;
    private Long idDirector;
    private String correoDirector;
    private String responsable;
    private Long idResponsable;
    private String correoResponsable;
    private Long idDetalleSolicitud;
    //Variables actualziadas en la logica del proceso.
    private Long accion;
    private Boolean validar;
    private Long accionValidacion;
    

    public DepartamentoSolicitudServicio() {
    }
    
    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Long getIdDirector() {
        return idDirector;
    }

    public void setIdDirector(Long idDirector) {
        this.idDirector = idDirector;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Long getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Long idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Long getAccion() {
        return accion;
    }

    public void setAccion(Long accion) {
        this.accion = accion;
    }

    public Boolean getValidar() {
        return validar;
    }

    public void setValidar(Boolean validar) {
        this.validar = validar;
    }

    public Long getIdDetalleSolicitud() {
        return idDetalleSolicitud;
    }

    public void setIdDetalleSolicitud(Long idDetalleSolicitud) {
        this.idDetalleSolicitud = idDetalleSolicitud;
    }

    public Long getAccionValidacion() {
        return accionValidacion;
    }

    public void setAccionValidacion(Long accionValidacion) {
        this.accionValidacion = accionValidacion;
    }

    public String getCorreoDirector() {
        return correoDirector;
    }

    public void setCorreoDirector(String correoDirector) {
        this.correoDirector = correoDirector;
    }

    public String getCorreoResponsable() {
        return correoResponsable;
    }

    public void setCorreoResponsable(String correoResponsable) {
        this.correoResponsable = correoResponsable;
    }
    
}
