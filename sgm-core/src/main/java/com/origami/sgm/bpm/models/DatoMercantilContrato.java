/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author Diego Briones
 */
public class DatoMercantilContrato {

    private String tipopersona;
    private BigInteger ente;
    private String apellidos;
    private String nombres;
    private String ci;
    private String tipocompareciente;
    private String razonsocial;
    private String tipocontrato;
    private Date fechainsripcion;
    private BigInteger numinscripcion;
    private String representante;
    private Date fechacancelacion;
    private String tipobien;
    private String chasis;
    private String motor;
    private String marca;
    private String modelo;
    private String aniofabrica;
    private String placa;
    private String ubicaciondato;
    private Date ultimamodificacion;
    private BigInteger identificador;
    private String notaria;
    private String cantonnotaria;
    private Date fechaescritura;
    private String estado;
    private String valoruuid;

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public BigInteger getEnte() {
        return ente;
    }

    public void setEnte(BigInteger ente) {
        this.ente = ente;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getTipocompareciente() {
        return tipocompareciente;
    }

    public void setTipocompareciente(String tipocompareciente) {
        this.tipocompareciente = tipocompareciente;
    }

    public String getTipopersona() {
        return tipopersona;
    }

    public void setTipopersona(String tipopersona) {
        this.tipopersona = tipopersona;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(String tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    public Date getFechainsripcion() {
        return fechainsripcion;
    }

    public void setFechainsripcion(Date fechainsripcion) {
        this.fechainsripcion = fechainsripcion;
    }

    public BigInteger getNuminscripcion() {
        return numinscripcion;
    }

    public void setNuminscripcion(BigInteger numinscripcion) {
        this.numinscripcion = numinscripcion;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public Date getFechacancelacion() {
        return fechacancelacion;
    }

    public void setFechacancelacion(Date fechacancelacion) {
        this.fechacancelacion = fechacancelacion;
    }

    public String getTipobien() {
        return tipobien;
    }

    public void setTipobien(String tipobien) {
        this.tipobien = tipobien;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAniofabrica() {
        return aniofabrica;
    }

    public void setAniofabrica(String aniofabrica) {
        this.aniofabrica = aniofabrica;
    }

    public String getValoruuid() {
        return valoruuid;
    }

    public void setValoruuid(String valoruuid) {
        this.valoruuid = valoruuid;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getUbicaciondato() {
        return ubicaciondato;
    }

    public void setUbicaciondato(String ubicaciondato) {
        this.ubicaciondato = ubicaciondato;
    }

    public Date getUltimamodificacion() {
        return ultimamodificacion;
    }

    public void setUltimamodificacion(Date ultimamodificacion) {
        this.ultimamodificacion = ultimamodificacion;
    }

    public BigInteger getIdentificador() {
        return identificador;
    }

    public void setIdentificador(BigInteger identificador) {
        this.identificador = identificador;
    }

    public String getNotaria() {
        return notaria;
    }

    public void setNotaria(String notaria) {
        this.notaria = notaria;
    }

    public String getCantonnotaria() {
        return cantonnotaria;
    }

    public void setCantonnotaria(String cantonnotaria) {
        this.cantonnotaria = cantonnotaria;
    }

    public Date getFechaescritura() {
        return fechaescritura;
    }

    public void setFechaescritura(Date fechaescritura) {
        this.fechaescritura = fechaescritura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
