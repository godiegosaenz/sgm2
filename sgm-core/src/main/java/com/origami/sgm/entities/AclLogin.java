/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author XndySxnchez
 */
@Entity
@Table(name = "acl_login", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AclLogin.findAll", query = "SELECT a FROM AclLogin a")
    , @NamedQuery(name = "AclLogin.findById", query = "SELECT a FROM AclLogin a WHERE a.id = :id")
    , @NamedQuery(name = "AclLogin.findByIpUserSession", query = "SELECT a FROM AclLogin a WHERE a.ipUserSession = :ipUserSession")
    , @NamedQuery(name = "AclLogin.findByFechaDoLogin", query = "SELECT a FROM AclLogin a WHERE a.fechaDoLogin = :fechaDoLogin")
    , @NamedQuery(name = "AclLogin.findByFechaDoLogout", query = "SELECT a FROM AclLogin a WHERE a.fechaDoLogout = :fechaDoLogout")
    , @NamedQuery(name = "AclLogin.findByUserSessionName", query = "SELECT a FROM AclLogin a WHERE a.userSessionName = :userSessionName")
    , @NamedQuery(name = "AclLogin.findByUserSesionId", query = "SELECT a FROM AclLogin a WHERE a.userSesionId = :userSesionId")})
public class AclLogin implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM) 
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "ip_user_session")
    private String ipUserSession;
    @Column(name = "fecha_do_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDoLogin;
    @Column(name = "fecha_do_logout")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDoLogout;
    @Column(name = "user_session_name")
    private String userSessionName;
    @Column(name = "user_sesion_id")
    private BigInteger userSesionId;

    public AclLogin() {
    }

    public AclLogin(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpUserSession() {
        return ipUserSession;
    }

    public void setIpUserSession(String ipUserSession) {
        this.ipUserSession = ipUserSession;
    }

    public Date getFechaDoLogin() {
        return fechaDoLogin;
    }

    public void setFechaDoLogin(Date fechaDoLogin) {
        this.fechaDoLogin = fechaDoLogin;
    }

    public Date getFechaDoLogout() {
        return fechaDoLogout;
    }

    public void setFechaDoLogout(Date fechaDoLogout) {
        this.fechaDoLogout = fechaDoLogout;
    }

    public String getUserSessionName() {
        return userSessionName;
    }

    public void setUserSessionName(String userSessionName) {
        this.userSessionName = userSessionName;
    }

    public BigInteger getUserSesionId() {
        return userSesionId;
    }

    public void setUserSesionId(BigInteger userSesionId) {
        this.userSesionId = userSesionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AclLogin)) {
            return false;
        }
        AclLogin other = (AclLogin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.AclLogin[ id=" + id + " ]";
    }
    
}
