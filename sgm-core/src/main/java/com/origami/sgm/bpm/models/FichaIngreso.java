/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatEscrituraRural;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegTipoFicha;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modelo para almacenar los Datos para la FichaIngresoNuevo
 * 
 * @author Angel Navarro
 */
public class FichaIngreso implements Serializable{
    
    public static final Long serialVersionUID = 1L;
    
    private Boolean datosImob = false;
    private Boolean datosComp = false;
    private Boolean datosBien = false;
    private Boolean variasFichas = false;
    private Boolean habilitado = true;
    private Boolean urbano = true;
    private Boolean propiedadHorz;
    private Boolean persona = true;
    private Boolean mostrarRural;
    private Boolean mostrarUrbano;
    private Boolean codUrban;
    private Boolean esFichaHistorico;
    private BigInteger numPredio;
    private Long numeroFicha;
    private Long cantidadFichas;
    private String linderos;
    private String codPredio;
    private String correo;
    private String Telefono;
    private String ciRUC;
    private String numIdentificador;
    private List<String> tipo;
    private String mensaje;
    private String observaciones;
    private Long regCatrast;
    private Long identfPredio;
    private Long fichaHistorico;
    private Date fechaInscripcion;
    
    private RegFicha ficha;
    private RegFicha regFichaHistorico;
    private RegFicha fichaBuscada ;
    private CatPredio predio;
    private CatEscritura escritura;
    private CatEscrituraRural escrituraRural;
    private CatEnte ente;
    private RegTipoFicha tipoFicha;
    private RegEnteInterviniente inter = new RegEnteInterviniente();
    
    private List<RegMovimiento> movimientos;
    private List<EnteCorreo> listCorreo;
    private List<EnteTelefono> listTelefonos;
    private List<EnteCorreo> listCorreoElim ;
    private List<EnteTelefono> listTelefonosElim ;

    public FichaIngreso() {
        regFichaHistorico = new RegFicha();
        ficha = new RegFicha() ;
        predio = new CatPredio();
        escritura = new CatEscritura();
        ente = new CatEnte();
        escrituraRural = new CatEscrituraRural();
        listCorreo = new ArrayList<>();
        listTelefonos = new ArrayList<>();
        tipo = new ArrayList<>();
        codUrban = true;
        mostrarRural = false;
        mostrarUrbano = false;
        movimientos = new ArrayList<>();
        esFichaHistorico = false;
        listCorreoElim = new ArrayList<>();
        listTelefonosElim = new ArrayList<>();
    }

    public Boolean getDatosImob() {
        return datosImob;
    }

    public void setDatosImob(Boolean datosImob) {
        this.datosImob = datosImob;
    }

    public Boolean getDatosComp() {
        return datosComp;
    }

    public void setDatosComp(Boolean datosComp) {
        this.datosComp = datosComp;
    }

    public Boolean getDatosBien() {
        return datosBien;
    }

    public void setDatosBien(Boolean datosBien) {
        this.datosBien = datosBien;
    }

    public Boolean getPersona() {
        return persona;
    }

    public void setPersona(Boolean persona) {
        this.persona = persona;
    }

    public Boolean getVariasFichas() {
        return variasFichas;
    }

    public void setVariasFichas(Boolean variasFichas) {
        this.variasFichas = variasFichas;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Boolean getUrbano() {
        return urbano;
    }

    public void setUrbano(Boolean urbano) {
        this.urbano = urbano;
    }

    public Boolean getPropiedadHorz() {
        return propiedadHorz;
    }

    public void setPropiedadHorz(Boolean propiedadHorz) {
        this.propiedadHorz = propiedadHorz;
    }

    public Boolean getMostrarRural() {
        return mostrarRural;
    }

    public void setMostrarRural(Boolean mostrarRural) {
        this.mostrarRural = mostrarRural;
    }

    public Boolean getMostrarUrbano() {
        return mostrarUrbano;
    }

    public void setMostrarUrbano(Boolean mostrarUrbano) {
        this.mostrarUrbano = mostrarUrbano;
    }

    public Long getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(Long numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Long getCantidadFichas() {
        return cantidadFichas;
    }

    public void setCantidadFichas(Long cantidadFichas) {
        this.cantidadFichas = cantidadFichas;
    }

    public String getLinderos() {
        return linderos;
    }

    public void setLinderos(String linderos) {
        this.linderos = linderos;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public Long getRegCatrast() {
        return regCatrast;
    }

    public void setRegCatrast(Long regCatrast) {
        this.regCatrast = regCatrast;
    }

    public Long getIdentfPredio() {
        return identfPredio;
    }

    public void setIdentfPredio(Long identfPredio) {
        this.identfPredio = identfPredio;
    }

    public Long getFichaHistorico() {
        return fichaHistorico;
    }

    public void setFichaHistorico(Long fichaHistorico) {
        this.fichaHistorico = fichaHistorico;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
    }

    public RegFicha getRegFichaHistorico() {
        return regFichaHistorico;
    }

    public void setRegFichaHistorico(RegFicha regFichaHistorico) {
        this.regFichaHistorico = regFichaHistorico;
    }

    public RegFicha getFichaBuscada() {
        return fichaBuscada;
    }

    public void setFichaBuscada(RegFicha fichaBuscada) {
        this.fichaBuscada = fichaBuscada;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public CatEscrituraRural getEscrituraRural() {
        return escrituraRural;
    }

    public void setEscrituraRural(CatEscrituraRural escrituraRural) {
        this.escrituraRural = escrituraRural;
    }

    public List<RegMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<RegMovimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public List<EnteCorreo> getListCorreo() {
        return listCorreo;
    }

    public void setListCorreo(List<EnteCorreo> listCorreo) {
        this.listCorreo = listCorreo;
    }

    public List<EnteTelefono> getListTelefonos() {
        return listTelefonos;
    }

    public void setListTelefonos(List<EnteTelefono> listTelefonos) {
        this.listTelefonos = listTelefonos;
    }

    public String getCiRUC() {
        return ciRUC;
    }

    public void setCiRUC(String ciRUC) {
        this.ciRUC = ciRUC;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public Boolean getCodUrban() {
        return codUrban;
    }

    public void setCodUrban(Boolean codUrban) {
        this.codUrban = codUrban;
    }

    public List<String> getTipo() {
        return tipo;
    }

    public void setTipo(List<String> tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Boolean getEsFichaHistorico() {
        return esFichaHistorico;
    }

    public void setEsFichaHistorico(Boolean esFichaHistorico) {
        this.esFichaHistorico = esFichaHistorico;
    }

    public String getNumIdentificador() {
        return numIdentificador;
    }

    public void setNumIdentificador(String numIdentificador) {
        this.numIdentificador = numIdentificador;
    }

    public List<EnteCorreo> getListCorreoElim() {
        return listCorreoElim;
    }

    public void setListCorreoElim(List<EnteCorreo> listCorreoElim) {
        this.listCorreoElim = listCorreoElim;
    }

    public List<EnteTelefono> getListTelefonosElim() {
        return listTelefonosElim;
    }

    public void setListTelefonosElim(List<EnteTelefono> listTelefonosElim) {
        this.listTelefonosElim = listTelefonosElim;
    }

    public RegTipoFicha getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(RegTipoFicha tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public RegEnteInterviniente getInter() {
        return inter;
    }

    public void setInter(RegEnteInterviniente inter) {
        this.inter = inter;
    }
    
}
