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
public class DatoMercantilSocietario {

    private String tipopersona;
    private BigInteger ente;
    private String nombrecompania;
    private String ci;
    private String especie;
    private Date fechainscripcion;
    private String apellidoscompareciente;
    private String nombrescompareciente;
    private String cicompareciente;
    private String cargo;
    private String tipocompareciente;
    private String disposicion;
    private String autoridad;
    private Date fechadisposicion;
    private String numdisposicion;
    private Date fechaescritura;
    private String notaria;
    private String cantonnotaria;
    private String tipotramite;
    private String ubicaciondato;
    private Date ultimamodificacion;
    private BigInteger identificador;
    private String estado;
    private String valoruuid;

    public String getNombrecompania() {
        return nombrecompania;
    }

    public void setNombrecompania(String nombrecompania) {
        this.nombrecompania = nombrecompania;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getEspecie() {
        return especie;
    }

    public String getTipopersona() {
        return tipopersona;
    }

    public void setTipopersona(String tipopersona) {
        this.tipopersona = tipopersona;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public Date getFechainscripcion() {
        return fechainscripcion;
    }

    public void setFechainscripcion(Date fechainscripcion) {
        this.fechainscripcion = fechainscripcion;
    }

    public String getApellidoscompareciente() {
        return apellidoscompareciente;
    }

    public void setApellidoscompareciente(String apellidoscompareciente) {
        this.apellidoscompareciente = apellidoscompareciente;
    }

    public String getNombrescompareciente() {
        return nombrescompareciente;
    }

    public void setNombrescompareciente(String nombrescompareciente) {
        this.nombrescompareciente = nombrescompareciente;
    }

    public String getCicompareciente() {
        return cicompareciente;
    }

    public void setCicompareciente(String cicompareciente) {
        this.cicompareciente = cicompareciente;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTipocompareciente() {
        return tipocompareciente;
    }

    public void setTipocompareciente(String tipocompareciente) {
        this.tipocompareciente = tipocompareciente;
    }

    public String getDisposicion() {
        return disposicion;
    }

    public void setDisposicion(String disposicion) {
        this.disposicion = disposicion;
    }

    public String getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(String autoridad) {
        this.autoridad = autoridad;
    }

    public Date getFechadisposicion() {
        return fechadisposicion;
    }

    public void setFechadisposicion(Date fechadisposicion) {
        this.fechadisposicion = fechadisposicion;
    }

    public String getNumdisposicion() {
        return numdisposicion;
    }

    public void setNumdisposicion(String numdisposicion) {
        this.numdisposicion = numdisposicion;
    }

    public Date getFechaescritura() {
        return fechaescritura;
    }

    public void setFechaescritura(Date fechaescritura) {
        this.fechaescritura = fechaescritura;
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

    public String getTipotramite() {
        return tipotramite;
    }

    public void setTipotramite(String tipotramite) {
        this.tipotramite = tipotramite;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigInteger getEnte() {
        return ente;
    }

    public void setEnte(BigInteger ente) {
        this.ente = ente;
    }

    public String getValoruuid() {
        return valoruuid;
    }

    public void setValoruuid(String valoruuid) {
        this.valoruuid = valoruuid;
    }

}
