/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosTramite implements Serializable {

    private Long id;
    private Long nTramite;
    private String idProceso;
    private Long numPredio;
    private String ci;
    private BigInteger solicitante;
    private String descSolicitante;
    private String propietario;
    private BigInteger numLiquidacion;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private Long tipoTramite;
    private String nombreTramite;
    private Long subTramite;
    private String estado;
    private Date fecha;
    private String usrCreador;
    private String observacion;
    private Boolean persona;
    private Long lugarAdu;
    private Long cdla;
    private Collection<DatosTramiteDet> detalle;
    private String slUrb;
    private String mzUrb;
    private static final long serialVersionUID = 1L;
    
    private ModelPermisoFunc permisoFunc;

    public DatosTramite() {
    }

    public DatosTramite(Long id, Long nTramite, String idProceso, Long numPredio, 
            String ci, BigInteger solicitante, String descSolicitante, String propietario, 
            BigInteger numLiquidacion, String nombres, String apellidos, String correo, 
            String telefono, Long tipoTramite, String nombreTramite, String estado, 
            Date fecha, String usrCreador, String observacion, Boolean persona, 
            Collection<DatosTramiteDet> detalle, Long subTramite) {
        this.id = id;
        this.nTramite = nTramite;
        this.idProceso = idProceso;
        this.numPredio = numPredio;
        this.ci = ci;
        this.solicitante = solicitante;
        this.descSolicitante = descSolicitante;
        this.propietario = propietario;
        this.numLiquidacion = numLiquidacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoTramite = tipoTramite;
        this.nombreTramite = nombreTramite;
        this.estado = estado;
        this.fecha = fecha;
        this.usrCreador = usrCreador;
        this.observacion = observacion;
        this.persona = persona;
        this.detalle = detalle;
        this.subTramite = subTramite;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getnTramite() {
        return nTramite;
    }

    public void setnTramite(Long nTramite) {
        this.nTramite = nTramite;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getDescSolicitante() {
        return descSolicitante;
    }

    public void setDescSolicitante(String descSolicitante) {
        this.descSolicitante = descSolicitante;
    }

    public BigInteger getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(BigInteger solicitante) {
        this.solicitante = solicitante;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Long getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(Long tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsrCreador() {
        return usrCreador;
    }

    public void setUsrCreador(String usrCreador) {
        this.usrCreador = usrCreador;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public BigInteger getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(BigInteger numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public Boolean getPersona() {
        return persona;
    }

    public void setPersona(Boolean persona) {
        this.persona = persona;
    }

    public Collection<DatosTramiteDet> getDetalle() {
        return detalle;
    }

    public void setDetalle(Collection<DatosTramiteDet> detalle) {
        this.detalle = detalle;
    }

    public String getNombreTramite() {
        return nombreTramite;
    }

    public void setNombreTramite(String nombreTramite) {
        this.nombreTramite = nombreTramite;
    }

    public Long getSubTramite() {
        return subTramite;
    }

    public void setSubTramite(Long subTramite) {
        this.subTramite = subTramite;
    }

    public Long getLugarAdu() {
        return lugarAdu;
    }

    public void setLugarAdu(Long lugarAdu) {
        this.lugarAdu = lugarAdu;
    }

    public Long getCdla() {
        return cdla;
    }

    public void setCdla(Long cdla) {
        this.cdla = cdla;
    }

    public String getSlUrb() {
        return slUrb;
    }

    public void setSlUrb(String slUrb) {
        this.slUrb = slUrb;
    }

    public String getMzUrb() {
        return mzUrb;
    }

    public void setMzUrb(String mzUrb) {
        this.mzUrb = mzUrb;
    }

    public ModelPermisoFunc getPermisoFunc() {
        return permisoFunc;
    }

    public void setPermisoFunc(ModelPermisoFunc permisoFunc) {
        this.permisoFunc = permisoFunc;
    }
    
}
