/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.bancos;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "formato_banca", schema = SchemasConfig.BANCOS, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"prefijo"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FormatoBanca.findAll", query = "SELECT f FROM FormatoBanca f")
    , @NamedQuery(name = "FormatoBanca.findById", query = "SELECT f FROM FormatoBanca f WHERE f.id = :id")
    , @NamedQuery(name = "FormatoBanca.findByBanco", query = "SELECT f FROM FormatoBanca f WHERE f.banco = :banco")
    , @NamedQuery(name = "FormatoBanca.findByPrefijo", query = "SELECT f FROM FormatoBanca f WHERE f.prefijo = :prefijo")
    , @NamedQuery(name = "FormatoBanca.findByDelimitador", query = "SELECT f FROM FormatoBanca f WHERE f.delimitador = :delimitador")
    , @NamedQuery(name = "FormatoBanca.findByArchivo", query = "SELECT f FROM FormatoBanca f WHERE f.archivo = :archivo")
    , @NamedQuery(name = "FormatoBanca.findByArchivoNombSec", query = "SELECT f FROM FormatoBanca f WHERE f.archivoNombSec = :archivoNombSec")
    , @NamedQuery(name = "FormatoBanca.findByCodigoConvenio", query = "SELECT f FROM FormatoBanca f WHERE f.codigoConvenio = :codigoConvenio")
    , @NamedQuery(name = "FormatoBanca.findByTipoArchivo", query = "SELECT f FROM FormatoBanca f WHERE f.tipoArchivo = :tipoArchivo")
    , @NamedQuery(name = "FormatoBanca.findByFttp", query = "SELECT f FROM FormatoBanca f WHERE f.fttp = :fttp")
    , @NamedQuery(name = "FormatoBanca.findByWservice", query = "SELECT f FROM FormatoBanca f WHERE f.wservice = :wservice")
    , @NamedQuery(name = "FormatoBanca.findByHostUrl", query = "SELECT f FROM FormatoBanca f WHERE f.hostUrl = :hostUrl")
    , @NamedQuery(name = "FormatoBanca.findByUname", query = "SELECT f FROM FormatoBanca f WHERE f.uname = :uname")
    , @NamedQuery(name = "FormatoBanca.findByUpass", query = "SELECT f FROM FormatoBanca f WHERE f.upass = :upass")
    , @NamedQuery(name = "FormatoBanca.findByFecCre", query = "SELECT f FROM FormatoBanca f WHERE f.fecCre = :fecCre")
    , @NamedQuery(name = "FormatoBanca.findByUsrCre", query = "SELECT f FROM FormatoBanca f WHERE f.usrCre = :usrCre")
    , @NamedQuery(name = "FormatoBanca.findByEstado", query = "SELECT f FROM FormatoBanca f WHERE f.estado = :estado")
    , @NamedQuery(name = "FormatoBanca.findByTipo", query = "SELECT f FROM FormatoBanca f WHERE f.tipo = :tipo")})
public class FormatoBanca implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "banco", nullable = false, length = 200)
    private String banco;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "prefijo", nullable = false, length = 10)
    private String prefijo;
    @Size(max = 2)
    @Column(name = "delimitador", length = 2)
    private String delimitador;
    @Size(max = 50)
    @Column(name = "archivo", length = 50)
    private String archivo;
    @Size(max = 50)
    @Column(name = "archivo_nomb_sec", length = 50)
    private String archivoNombSec;
    @Size(max = 20)
    @Column(name = "codigo_convenio", length = 20)
    private String codigoConvenio;
    @Size(max = 10)
    @Column(name = "tipo_archivo", length = 10)
    private String tipoArchivo;
    @Column(name = "fttp")
    private Boolean fttp;
    @Column(name = "wservice")
    private Boolean wservice;
    @Size(max = 500)
    @Column(name = "host_url", length = 500)
    private String hostUrl;
    @Size(max = 100)
    @Column(name = "uname", length = 100)
    private String uname;
    @Size(max = 100)
    @Column(name = "upass", length = 100)
    private String upass;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 10)
    @Column(name = "tipo", length = 10)
    private String tipo;
    @OrderBy(value = "orden asc")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "formato", fetch = FetchType.LAZY)
    private Collection<BancaDiccionario> bancaDiccionarioCollection;

    public FormatoBanca() {
    }

    public FormatoBanca(Long id) {
        this.id = id;
    }

    public FormatoBanca(Long id, String banco, String prefijo) {
        this.id = id;
        this.banco = banco;
        this.prefijo = prefijo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getDelimitador() {
        return delimitador;
    }

    public void setDelimitador(String delimitador) {
        this.delimitador = delimitador;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getArchivoNombSec() {
        return archivoNombSec;
    }

    public void setArchivoNombSec(String archivoNombSec) {
        this.archivoNombSec = archivoNombSec;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public Boolean getFttp() {
        return fttp;
    }

    public void setFttp(Boolean fttp) {
        this.fttp = fttp;
    }

    public Boolean getWservice() {
        return wservice;
    }

    public void setWservice(Boolean wservice) {
        this.wservice = wservice;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<BancaDiccionario> getBancaDiccionarioCollection() {
        return bancaDiccionarioCollection;
    }

    public void setBancaDiccionarioCollection(Collection<BancaDiccionario> bancaDiccionarioCollection) {
        this.bancaDiccionarioCollection = bancaDiccionarioCollection;
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
        if (!(object instanceof FormatoBanca)) {
            return false;
        }
        FormatoBanca other = (FormatoBanca) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.bancos.FormatoBanca[ id=" + id + " ]";
    }
    
}
