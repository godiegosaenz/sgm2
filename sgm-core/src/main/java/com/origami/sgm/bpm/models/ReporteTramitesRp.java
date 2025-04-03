/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Anyelo
 */
public class ReporteTramitesRp implements Serializable {

    public static final Long serialVersionUID = 1L;

    protected Long numtramite;
    protected Long numseguimiento;
    protected String nombre = "";
    protected String assigne = "";
    protected String idprocess;
    protected Date fecha;
    protected String userlegal = "";
    protected String nombrelegal = "";
    protected String beneficiario = "";

    public Long getNumtramite() {
        return numtramite;
    }

    public void setNumtramite(Long numtramite) {
        this.numtramite = numtramite;
    }

    public Long getNumseguimiento() {
        return numseguimiento;
    }

    public void setNumseguimiento(Long numseguimiento) {
        this.numseguimiento = numseguimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAssigne() {
        return assigne;
    }

    public void setAssigne(String assigne) {
        this.assigne = assigne;
    }

    public String getUserlegal() {
        return userlegal;
    }

    public void setUserlegal(String userlegal) {
        this.userlegal = userlegal;
    }

    public String getNombrelegal() {
        return nombrelegal;
    }

    public void setNombrelegal(String nombrelegal) {
        this.nombrelegal = nombrelegal;
    }

    public String getIdprocess() {
        return idprocess;
    }

    public void setIdprocess(String idprocess) {
        this.idprocess = idprocess;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }
}
