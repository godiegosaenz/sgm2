/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

/**
 *
 * @author User
 */
public class ParametrosProceso {

    protected String idprocess;
//    protected Long numpredio;
//    protected String ci;
//    protected BigDecimal valorCuantia;
    protected String observacionRequerimiento;
    protected String nombDepartamento;
    protected String nombSolicitante;
    protected String numTramite;

    public ParametrosProceso() {
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public String getNombDepartamento() {
        return nombDepartamento;
    }

    public void setNombDepartamento(String nombDepartamento) {
        this.nombDepartamento = nombDepartamento;
    }

    public String getNombSolicitante() {
        return nombSolicitante;
    }

    public void setNombSolicitante(String nombSolicitante) {
        this.nombSolicitante = nombSolicitante;
    }

    public String getObservacionRequerimiento() {
        return observacionRequerimiento;
    }

    public void setObservacionRequerimiento(String observacionRequerimiento) {
        this.observacionRequerimiento = observacionRequerimiento;
    }

    public String getIdprocess() {
        return idprocess;
    }

    public void setIdprocess(String idprocess) {
        this.idprocess = idprocess;
    }
//    public Long getNumpredio() {
//        return numpredio;
//    }
//
//    public void setNumpredio(Long numpredio) {
//        this.numpredio = numpredio;
//    }
//
//    public String getCi() {
//        return ci;
//    }
//
//    public void setCi(String ci) {
//        this.ci = ci;
//    }
//
//    public BigDecimal getValorCuantia() {
//        return valorCuantia;
//    }
//
//    public void setValorCuantia(BigDecimal valorCuantia) {
//        this.valorCuantia = valorCuantia;
//    }
//    
}
