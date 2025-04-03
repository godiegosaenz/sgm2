/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class Logx implements Serializable {

    private static final long serialVersionUID = 1L;
    private String usuario;
    private String clave;
    private String secToken;
    private Date fecha;
    private Long app;

    public Logx() {
    }

    public Logx(String usuario, String clave, String secToken, Date fecha, Long app) {
        this.usuario = usuario;
        this.clave = clave;
        this.secToken = secToken;
        this.fecha = fecha;
        this.app = app;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getSecToken() {
        return secToken;
    }

    public void setSecToken(String secToken) {
        this.secToken = secToken;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getApp() {
        return app;
    }

    public void setApp(Long app) {
        this.app = app;
    }

}
