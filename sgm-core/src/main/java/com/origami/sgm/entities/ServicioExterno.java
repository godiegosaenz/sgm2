/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargaas
 */
@Entity
@Table(name = "servicio_externo", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"identificador"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServicioExterno.findAll", query = "SELECT s FROM ServicioExterno s"),
    @NamedQuery(name = "ServicioExterno.findById", query = "SELECT s FROM ServicioExterno s WHERE s.id = :id"),
    @NamedQuery(name = "ServicioExterno.findByIdentificador", query = "SELECT s FROM ServicioExterno s WHERE s.identificador = :identificador"),
    @NamedQuery(name = "ServicioExterno.findByNombre", query = "SELECT s FROM ServicioExterno s WHERE s.nombre = :nombre"),
    @NamedQuery(name = "ServicioExterno.findByUrl", query = "SELECT s FROM ServicioExterno s WHERE s.url = :url"),
    @NamedQuery(name = "ServicioExterno.findByUsuario", query = "SELECT s FROM ServicioExterno s WHERE s.usuario = :usuario"),
    @NamedQuery(name = "ServicioExterno.findByClave", query = "SELECT s FROM ServicioExterno s WHERE s.clave = :clave"),
    @NamedQuery(name = "ServicioExterno.findByUsrCre", query = "SELECT s FROM ServicioExterno s WHERE s.usrCre = :usrCre"),
    @NamedQuery(name = "ServicioExterno.findByFecCre", query = "SELECT s FROM ServicioExterno s WHERE s.fecCre = :fecCre"),
    @NamedQuery(name = "ServicioExterno.findByFecAct", query = "SELECT s FROM ServicioExterno s WHERE s.fecAct = :fecAct"),
    @NamedQuery(name = "ServicioExterno.findByEstado", query = "SELECT s FROM ServicioExterno s WHERE s.estado = :estado")})
public class ServicioExterno implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "identificador", nullable = false, length = 6)
    private String identificador;
    @Size(max = 200)
    @Column(name = "nombre", length = 200)
    private String nombre;
    @Size(max = 500)
    @Column(name = "url", length = 500)
    private String url;
    @Size(max = 50)
    @Column(name = "usuario", length = 50)
    private String usuario;
    @Size(max = 50)
    @Column(name = "clave", length = 50)
    private String clave;
    @Column(name = "complemento", length = 100)
    private String complemento;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;

    public ServicioExterno() {
    }

    public ServicioExterno(Long id) {
        this.id = id;
    }

    public ServicioExterno(Long id, String identificador) {
        this.id = id;
        this.identificador = identificador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof ServicioExterno)) {
            return false;
        }
        ServicioExterno other = (ServicioExterno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ServicioExterno[ id=" + id + " ]";
    }

}
