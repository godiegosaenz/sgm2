/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author Anyelo
 */
public class LiquidacionRegistroAntiguaModel implements Serializable {

    //HistoricoTramite
    private String ci;
    private String nombres;
    private String apellidos;
    private Long idTramite;
    private Date fecha;
    private String nombrePropietario;
    private String correos;
    private String telefonos;
    private Long numLiquidacion;
    private String usuario;
    // RegpLiquidacionDerechosAranceles
    private Long id;
    private BigDecimal tasaCatastro;
    private String infAdicional;
    private BigInteger numTramiteRp;
    private String parentescoSolicitante;
    private BigInteger cantidadTasasCatastro;
    private BigDecimal totalPagar;
    private Boolean isExoneracion;
    //regp_tramitesingresados_has_tipotramite
    private Long tipoActo; // pk de reg_acto - debe ser el mismo en las dos bases
    private BigDecimal numValor;
    private BigDecimal valorPagar;
    private BigDecimal cuantia;
    private BigDecimal avaluo;
    private String observacion;
    //regp_intervinientes
    private Long papel;
    private Boolean esBeneficiario;
    private Boolean isPersona;
    private String documento;
    private String firstName;
    private String secondName;

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getCorreos() {
        return correos;
    }

    public void setCorreos(String correos) {
        this.correos = correos;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public Long getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(Long numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getTasaCatastro() {
        return tasaCatastro;
    }

    public void setTasaCatastro(BigDecimal tasaCatastro) {
        this.tasaCatastro = tasaCatastro;
    }

    public String getInfAdicional() {
        return infAdicional;
    }

    public void setInfAdicional(String infAdicional) {
        this.infAdicional = infAdicional;
    }

    public BigInteger getNumTramiteRp() {
        return numTramiteRp;
    }

    public void setNumTramiteRp(BigInteger numTramiteRp) {
        this.numTramiteRp = numTramiteRp;
    }

    public String getParentescoSolicitante() {
        return parentescoSolicitante;
    }

    public void setParentescoSolicitante(String parentescoSolicitante) {
        this.parentescoSolicitante = parentescoSolicitante;
    }

    public BigInteger getCantidadTasasCatastro() {
        return cantidadTasasCatastro;
    }

    public void setCantidadTasasCatastro(BigInteger cantidadTasasCatastro) {
        this.cantidadTasasCatastro = cantidadTasasCatastro;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public Boolean getIsExoneracion() {
        return isExoneracion;
    }

    public void setIsExoneracion(Boolean isExoneracion) {
        this.isExoneracion = isExoneracion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoActo() {
        return tipoActo;
    }

    public void setTipoActo(Long tipoActo) {
        this.tipoActo = tipoActo;
    }

    public BigDecimal getValorPagar() {
        return valorPagar;
    }

    public void setValorPagar(BigDecimal valorPagar) {
        this.valorPagar = valorPagar;
    }

    public BigDecimal getNumValor() {
        return numValor;
    }

    public void setNumValor(BigDecimal numValor) {
        this.numValor = numValor;
    }

    public BigDecimal getCuantia() {
        return cuantia;
    }

    public void setCuantia(BigDecimal cuantia) {
        this.cuantia = cuantia;
    }

    public BigDecimal getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(BigDecimal avaluo) {
        this.avaluo = avaluo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Long getPapel() {
        return papel;
    }

    public void setPapel(Long papel) {
        this.papel = papel;
    }

    public Boolean getEsBeneficiario() {
        return esBeneficiario;
    }

    public void setEsBeneficiario(Boolean esBeneficiario) {
        this.esBeneficiario = esBeneficiario;
    }

    public Boolean getIsPersona() {
        return isPersona;
    }

    public void setIsPersona(Boolean isPersona) {
        this.isPersona = isPersona;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

}
