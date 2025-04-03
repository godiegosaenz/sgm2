/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.bancos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
 
/**
 *
 * @author Juan Carlos
 */
@Entity
@Table(name = "consolidacion_banco", schema = SchemasConfig.BANCOS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConsolidacionBanco.findAll", query = "SELECT c FROM ConsolidacionBanco c")
    , @NamedQuery(name = "ConsolidacionBanco.findById", query = "SELECT c FROM ConsolidacionBanco c WHERE c.id = :id")
    , @NamedQuery(name = "ConsolidacionBanco.findByNumPredio", query = "SELECT c FROM ConsolidacionBanco c WHERE c.numPredio = :numPredio")
    , @NamedQuery(name = "ConsolidacionBanco.findByAnio", query = "SELECT c FROM ConsolidacionBanco c WHERE c.anio = :anio")
    , @NamedQuery(name = "ConsolidacionBanco.findByFecha", query = "SELECT c FROM ConsolidacionBanco c WHERE c.fecha = :fecha")
    , @NamedQuery(name = "ConsolidacionBanco.findByValor", query = "SELECT c FROM ConsolidacionBanco c WHERE c.valor = :valor")
    , @NamedQuery(name = "ConsolidacionBanco.findByBanco", query = "SELECT c FROM ConsolidacionBanco c WHERE c.banco = :banco")
    , @NamedQuery(name = "ConsolidacionBanco.findByEstado", query = "SELECT c FROM ConsolidacionBanco c WHERE c.estado = :estado")
    , @NamedQuery(name = "ConsolidacionBanco.findByFechaCobro", query = "SELECT c FROM ConsolidacionBanco c WHERE c.fechaCobro = :fechaCobro")
    , @NamedQuery(name = "ConsolidacionBanco.findByNumComprobante", query = "SELECT c FROM ConsolidacionBanco c WHERE c.numComprobante = :numComprobante")
    , @NamedQuery(name = "ConsolidacionBanco.findByUsrCre", query = "SELECT c FROM ConsolidacionBanco c WHERE c.usrCre = :usrCre")
    , @NamedQuery(name = "ConsolidacionBanco.findByFecCre", query = "SELECT c FROM ConsolidacionBanco c WHERE c.fecCre = :fecCre")})
public class ConsolidacionBanco implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 14, scale = 4)
    private BigDecimal valor;
    @Size(max = 20)
    @Column(name = "banco", length = 20)
    private String banco;
    @Size(max = 10)
    @Column(name = "estado", length = 10)
    private String estado;
    @Column(name = "fecha_cobro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCobro;
    @Column(name = "num_comprobante")
    private BigInteger numComprobante;
    @Size(max = 100)
    @Column(name = "usr_cre", length = 100)
    private String usrCre;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;

    
    public ConsolidacionBanco() {
    }

    public ConsolidacionBanco(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCobro() {
        return fechaCobro;
    }

    public void setFechaCobro(Date fechaCobro) {
        this.fechaCobro = fechaCobro;
    }

    public BigInteger getNumComprobante() {
        return numComprobante;
    }

    public void setNumComprobante(BigInteger numComprobante) {
        this.numComprobante = numComprobante;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConsolidacionBanco)) {
            return false;
        }
        ConsolidacionBanco other = (ConsolidacionBanco) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.bancos.ConsolidacionBanco[ id=" + id + " ]";
    }
    
}
