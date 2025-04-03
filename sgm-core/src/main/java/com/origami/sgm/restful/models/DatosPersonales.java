package com.origami.sgm.restful.models;

import java.io.Serializable;
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
public class DatosPersonales implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long nacionalidad;
    private Long tipoIdentificacion;
    private String identificacion;
    private Long tipoPersona;
    private String descripcion;
    private String direccion;
    private String emailP;
    private String emailS;
    private String celular;
    private String convencional;
    private Boolean estado;
    private DatosPersonales representante;
    private Date fecha;
    
//    private List<Predio> predios;

    public DatosPersonales() {
    }
    

    public DatosPersonales(Long nacionalidad, Long tipoIdentificacion, String identificacion, Long tipoPersona, String descripcion, String direccion, String emailP, String emailS, String celular, String convencional, Boolean estado, DatosPersonales representante, Date fecha) {
        this.nacionalidad = nacionalidad;
        this.tipoIdentificacion = tipoIdentificacion;
        this.identificacion = identificacion;
        this.tipoPersona = tipoPersona;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.emailP = emailP;
        this.emailS = emailS;
        this.celular = celular;
        this.convencional = convencional;
        this.estado = estado;
        this.representante = representante;
        this.fecha = fecha;
    }

    public Long getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Long nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Long getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(Long tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Long getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(Long tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmailP() {
        return emailP;
    }

    public void setEmailP(String emailP) {
        this.emailP = emailP;
    }

    public String getEmailS() {
        return emailS;
    }

    public void setEmailS(String emailS) {
        this.emailS = emailS;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getConvencional() {
        return convencional;
    }

    public void setConvencional(String convencional) {
        this.convencional = convencional;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public DatosPersonales getRepresentante() {
        return representante;
    }

    public void setRepresentante(DatosPersonales representante) {
        this.representante = representante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    

}
