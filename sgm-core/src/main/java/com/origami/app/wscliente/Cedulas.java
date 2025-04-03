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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * //@author root
 */
//@Entity
//@Table(name = "cedulas", schema = "migracionjc")
//@NamedQueries({
    //@NamedQuery(name = "Cedulas.findAll", query = "SELECT c FROM Cedulas c")})
public class Cedulas implements Serializable {

    private static final long serialVersionUID = 1L;
    //@Size(max = 255)
    //@Column(name = "cedula")
    private String cedula;
    //@Size(max = 255)
    //@Column(name = "num_predio")
    private String numPredio;
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Basic(optional = false)
    //@Column(name = "id")
    private Long id;
    //@Size(max = 2147483647)
    //@Column(name = "nombres_ws")
    private String nombresWs;
    //@Size(max = 2147483647)
    //@Column(name = "apellidos_ws")
    private String apellidosWs;
    //@Size(max = 2147483647)
    //@Column(name = "direccion")
    private String direccion;
    //@Column(name = "fechanac")
    //@Temporal(TemporalType.DATE)
    private Date fechanac;
    //@Size(max = 2147483647)
    //@Column(name = "obligadocontabilidad")
    private String obligadocontabilidad;
    //@Size(max = 2147483647)
    //@Column(name = "cedula_ws")
    private String cedulaWS;
    //@Size(max = 2147483647)
    //@Column(name = "estado")
    private String estado;

    public Cedulas() {
    }

    public Cedulas(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(String numPredio) {
        this.numPredio = numPredio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombresWs() {
        return nombresWs;
    }

    public void setNombresWs(String nombresWs) {
        this.nombresWs = nombresWs;
    }

    public String getApellidosWs() {
        return apellidosWs;
    }

    public void setApellidosWs(String apellidosWs) {
        this.apellidosWs = apellidosWs;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechanac() {
        return fechanac;
    }

    public void setFechanac(Date fechanac) {
        this.fechanac = fechanac;
    }

    public String getObligadocontabilidad() {
        return obligadocontabilidad;
    }

    public void setObligadocontabilidad(String obligadocontabilidad) {
        this.obligadocontabilidad = obligadocontabilidad;
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
        if (!(object instanceof Cedulas)) {
            return false;
        }
        Cedulas other = (Cedulas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getCedulaWS() {
        return cedulaWS;
    }

    public void setCedulaWS(String cedulaWS) {
        this.cedulaWS = cedulaWS;
    }
    
    

    //@Override
    public String toString() {
        return "com.origami.app.wscliente.Cedulas[ id=" + id + " ]";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
}
