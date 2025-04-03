/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app.wscliente;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * //@author root
 */
//@Entity
//@Table(name = "cedula_jama", schema = "migracionjc")
//@NamedQueries({
    //@NamedQuery(name = "CedulaJama.findAll", query = "SELECT c FROM CedulaJama c")})
public class CedulaJama implements Serializable {

    private static final long serialVersionUID = 1L;
    //@Id
    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "id")
    private Integer id;
    //@Size(max = 255)
    //@Column(name = "num_ficha")
    private String numFicha;
    //@Size(max = 255)
    //@Column(name = "num_predio")
    private String numPredio;
    //@Size(max = 255)
    //@Column(name = "codigo_anterior")
    private String codigoAnterior;
    //@Size(max = 255)
    //@Column(name = "apellido_sistema")
    private String apellidoSistema;
    //@Size(max = 255)
    //@Column(name = "nombre_sistema")
    private String nombreSistema;
    //@Size(max = 255)
    //@Column(name = "cedula")
    private String cedula;
    //@Size(max = 255)
    //@Column(name = "ruc")
    private String ruc;
    //@Size(max = 255)
    //@Column(name = "pasaporte")
    private String pasaporte;
    //@Size(max = 255)
    //@Column(name = "estado_civil")
    private String estadoCivil;
    //@Size(max = 255)
    //@Column(name = "discapacidad")
    private String discapacidad;
    //@Size(max = 255)
    //@Column(name = "razon_social")
    private String razonSocial;
    //@Size(max = 255)
    //@Column(name = "ruc_cia")
    private String rucCia;
    //@Size(max = 255)
    //@Column(name = "nombre_comercial")
    private String nombreComercial;
    //@Column(name = "estado")
    private String estado;
    //@Size(max = 255)
    //@Column(name = "apellido_dat_seg")
    private String apellidoDatSeg;
    //@Size(max = 255)
    //@Column(name = "nombre_dat_seg")
    private String nombreDatSeg;
    //@Size(max = 255)
    //@Column(name = "discapacidad_dat_seg")
    private String discapacidadDatSeg;
    //@Column(name = "fechanac_dat_seg")
    //@Temporal(TemporalType.DATE)
    private Date fechanacDatSeg;
    //@Size(max = 2147483647)
    //@Column(name = "direccion_dat_seg")
    private String direccionDatSeg;
    //@Size(max = 2147483647)
    //@Column(name = "estado_civil_dat_seg")
    private String estadoCivilDatSeg;

    public CedulaJama() {
    }

    public CedulaJama(Integer id) {
        this.id = id;
    }

    public CedulaJama(Integer id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(String numFicha) {
        this.numFicha = numFicha;
    }

    public String getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(String numPredio) {
        this.numPredio = numPredio;
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public String getApellidoSistema() {
        return apellidoSistema;
    }

    public void setApellidoSistema(String apellidoSistema) {
        this.apellidoSistema = apellidoSistema;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    public void setNombreSistema(String nombreSistema) {
        this.nombreSistema = nombreSistema;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getPasaporte() {
        return pasaporte;
    }

    public void setPasaporte(String pasaporte) {
        this.pasaporte = pasaporte;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRucCia() {
        return rucCia;
    }

    public void setRucCia(String rucCia) {
        this.rucCia = rucCia;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getApellidoDatSeg() {
        return apellidoDatSeg;
    }

    public void setApellidoDatSeg(String apellidoDatSeg) {
        this.apellidoDatSeg = apellidoDatSeg;
    }

    public String getNombreDatSeg() {
        return nombreDatSeg;
    }

    public void setNombreDatSeg(String nombreDatSeg) {
        this.nombreDatSeg = nombreDatSeg;
    }

    public String getDiscapacidadDatSeg() {
        return discapacidadDatSeg;
    }

    public void setDiscapacidadDatSeg(String discapacidadDatSeg) {
        this.discapacidadDatSeg = discapacidadDatSeg;
    }

    public Date getFechanacDatSeg() {
        return fechanacDatSeg;
    }

    public void setFechanacDatSeg(Date fechanacDatSeg) {
        this.fechanacDatSeg = fechanacDatSeg;
    }

    public String getDireccionDatSeg() {
        return direccionDatSeg;
    }

    public void setDireccionDatSeg(String direccionDatSeg) {
        this.direccionDatSeg = direccionDatSeg;
    }

    public String getEstadoCivilDatSeg() {
        return estadoCivilDatSeg;
    }

    public void setEstadoCivilDatSeg(String estadoCivilDatSeg) {
        this.estadoCivilDatSeg = estadoCivilDatSeg;
    }

    //@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    //@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CedulaJama)) {
            return false;
        }
        CedulaJama other = (CedulaJama) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    //@Override
    public String toString() {
        return "com.origami.app.wscliente.CedulaJama[ id=" + id + " ]";
    }
    
}
