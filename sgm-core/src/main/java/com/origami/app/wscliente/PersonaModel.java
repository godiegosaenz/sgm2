/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app.wscliente;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;

/**
 *
 * @author Anyelo
 */
@XmlRootElement(name = "MyTable", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonaModel implements Serializable {

    @XmlElement(name = "IdPersona", namespace = "")
    private Integer idpersona;
    @XmlElement(name = "Tipo")
    private Integer tipo;
    @XmlElement(name = "CedulaRUC", namespace = "")
    private String cedularuc;
    @XmlElement(name = "Propietario", namespace = "")
    private String propietario;
    @XmlElement(name = "direccion", namespace = "")
    private String direccion;
    @XmlElement(name = "fechanac", namespace = "")
    private Date fechanac;
    @XmlElement(name = "Objeto", namespace = "")
    private Integer objeto;
    @XmlElement(name = "Telefono", namespace = "")
    private String telefono;
    @XmlElement(name = "Celular", namespace = "")
    private String celular;
    @XmlElement(name = "Estadocivil", namespace = "")
    private Integer estadocivil;
    @XmlElement(name = "Discapacidad2", namespace = "")
    private Integer discapacidad2;
    @XmlElement(name = "Derecho", namespace = "")
    private Integer derecho;
    @XmlElement(name = "alcance", namespace = "")
    private Integer alcance;
    @XmlElement(name = "Apellidos", namespace="")
    private String apellidos;
    @XmlElement(name = "Nombres", namespace = "")
    private String nombres;
    @XmlElement(name = "discapacidad")
    private String discapacidad;
    @XmlElement(name = "ApellidoP")
    private String apellidop;
    @XmlElement(name = "ApellidoM", namespace="")
    private String apellidom;
    @XmlElement(name = "nombrepropio")
    private String nombrepropio;
    @XmlElement(name = "email")
    private String email;
    @XmlElement(name = "Jubilado")
    private Boolean jubilado;
    @XmlElement(name = "JefaHogar")
    private Boolean jefahogar;
    @XmlElement(name = "MadreSoltera")
    private Boolean madresoltera;
    @XmlElement(name = "CarneDiscapacidad")
    private String carnediscapacidad;
    @XmlElement(name = "PorcentajeDiscapacidad2")
    private BigDecimal porcentajediscapacidad2;
    @XmlElement(name = "PorcentajeDiscapacidad")
    private BigDecimal porcentajediscapacidad;
    @XmlElement(name = "religiosa")
    private Boolean religiosa;
    @XmlElement(name = "Sexo")
    private String sexo;
    @XmlElement(name = "excento")
    private Boolean excento;
    @XmlElement(name = "Validado")
    private Boolean validado;
    @XmlElement(name = "Password")
    private String password;
    @XmlElement(name = "EstadoCuenta")
    private String estadocuenta;
    @XmlElement(name = "CambiarPassword")
    private Boolean cambiarpassword;
    @XmlElement(name = "IdQMATIC")
    private String idqmatic;
    @XmlElement(name = "ObligadoContabilidad")
    private String obligadocontabilidad;
    @XmlElement(name = "TipoDerecho")
    private String tipoderecho;
    @XmlElement(name = "TipoObjeto")
    private String tipoobjeto;
    @XmlElement(name = "voto")
    private Boolean voto;

    public Integer getIdpersona() {
        return idpersona;
    }

    public void setIdpersona(Integer idpersona) {
        this.idpersona = idpersona;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getCedularuc() {
        return cedularuc;
    }

    public void setCedularuc(String cedularuc) {
        this.cedularuc = cedularuc;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
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

    public Integer getObjeto() {
        return objeto;
    }

    public void setObjeto(Integer objeto) {
        this.objeto = objeto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Integer getEstadocivil() {
        return estadocivil;
    }

    public void setEstadocivil(Integer estadocivil) {
        this.estadocivil = estadocivil;
    }

    public Integer getDiscapacidad2() {
        return discapacidad2;
    }

    public void setDiscapacidad2(Integer discapacidad2) {
        this.discapacidad2 = discapacidad2;
    }

    public Integer getDerecho() {
        return derecho;
    }

    public void setDerecho(Integer derecho) {
        this.derecho = derecho;
    }

    public Integer getAlcance() {
        return alcance;
    }

    public void setAlcance(Integer alcance) {
        this.alcance = alcance;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

    public String getApellidop() {
        return apellidop;
    }

    public void setApellidop(String apellidop) {
        this.apellidop = apellidop;
    }

    public String getApellidom() {
        return apellidom;
    }

    public void setApellidom(String apellidom) {
        this.apellidom = apellidom;
    }

    public String getNombrepropio() {
        return nombrepropio;
    }

    public void setNombrepropio(String nombrepropio) {
        this.nombrepropio = nombrepropio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getJubilado() {
        return jubilado;
    }

    public void setJubilado(Boolean jubilado) {
        this.jubilado = jubilado;
    }

    public Boolean getJefahogar() {
        return jefahogar;
    }

    public void setJefahogar(Boolean jefahogar) {
        this.jefahogar = jefahogar;
    }

    public Boolean getMadresoltera() {
        return madresoltera;
    }

    public void setMadresoltera(Boolean madresoltera) {
        this.madresoltera = madresoltera;
    }

    public String getCarnediscapacidad() {
        return carnediscapacidad;
    }

    public void setCarnediscapacidad(String carnediscapacidad) {
        this.carnediscapacidad = carnediscapacidad;
    }

    public BigDecimal getPorcentajediscapacidad2() {
        return porcentajediscapacidad2;
    }

    public void setPorcentajediscapacidad2(BigDecimal porcentajediscapacidad2) {
        this.porcentajediscapacidad2 = porcentajediscapacidad2;
    }

    public BigDecimal getPorcentajediscapacidad() {
        return porcentajediscapacidad;
    }

    public void setPorcentajediscapacidad(BigDecimal porcentajediscapacidad) {
        this.porcentajediscapacidad = porcentajediscapacidad;
    }

    public Boolean getReligiosa() {
        return religiosa;
    }

    public void setReligiosa(Boolean religiosa) {
        this.religiosa = religiosa;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Boolean getExcento() {
        return excento;
    }

    public void setExcento(Boolean excento) {
        this.excento = excento;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstadocuenta() {
        return estadocuenta;
    }

    public void setEstadocuenta(String estadocuenta) {
        this.estadocuenta = estadocuenta;
    }

    public Boolean getCambiarpassword() {
        return cambiarpassword;
    }

    public void setCambiarpassword(Boolean cambiarpassword) {
        this.cambiarpassword = cambiarpassword;
    }

    public String getIdqmatic() {
        return idqmatic;
    }

    public void setIdqmatic(String idqmatic) {
        this.idqmatic = idqmatic;
    }

    public String getObligadocontabilidad() {
        return obligadocontabilidad;
    }

    public void setObligadocontabilidad(String obligadocontabilidad) {
        this.obligadocontabilidad = obligadocontabilidad;
    }

    public String getTipoderecho() {
        return tipoderecho;
    }

    public void setTipoderecho(String tipoderecho) {
        this.tipoderecho = tipoderecho;
    }

    public String getTipoobjeto() {
        return tipoobjeto;
    }

    public void setTipoobjeto(String tipoobjeto) {
        this.tipoobjeto = tipoobjeto;
    }

    public Boolean getVoto() {
        return voto;
    }

    public void setVoto(Boolean voto) {
        this.voto = voto;
    }

    @Override
    public String toString() {
        return "PersonaModel{" + "idpersona=" + idpersona + ", tipo=" + tipo + ", cedularuc=" + cedularuc + ", propietario=" + propietario + ", direccion=" + direccion + ", fechanac=" + fechanac + ", objeto=" + objeto + ", telefono=" + telefono + ", celular=" + celular + ", estadocivil=" + estadocivil + ", discapacidad2=" + discapacidad2 + ", derecho=" + derecho + ", alcance=" + alcance + ", apellidos=" + apellidos + ", nombres=" + nombres + ", discapacidad=" + discapacidad + ", apellidop=" + apellidop + ", apellidom=" + apellidom + ", nombrepropio=" + nombrepropio + ", email=" + email + ", jubilado=" + jubilado + ", jefahogar=" + jefahogar + ", madresoltera=" + madresoltera + ", carnediscapacidad=" + carnediscapacidad + ", porcentajediscapacidad2=" + porcentajediscapacidad2 + ", porcentajediscapacidad=" + porcentajediscapacidad + ", religiosa=" + religiosa + ", sexo=" + sexo + ", excento=" + excento + ", validado=" + validado + ", password=" + password + ", estadocuenta=" + estadocuenta + ", cambiarpassword=" + cambiarpassword + ", idqmatic=" + idqmatic + ", obligadocontabilidad=" + obligadocontabilidad + ", tipoderecho=" + tipoderecho + ", tipoobjeto=" + tipoobjeto + ", voto=" + voto + '}';
    }

    
    
}
