/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Guardar los registro de los retiros y alturas de la normas ingresadas, cada
 * uno de los registros del retiro y alturas van identificadas el la columna
 * código que van des 1 hasta el siete.
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_normas_construccion_has_retiros_aumento", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatNormasConstruccionHasRetirosAumento.findAll", query = "SELECT c FROM CatNormasConstruccionHasRetirosAumento c"),
    @NamedQuery(name = "CatNormasConstruccionHasRetirosAumento.findById", query = "SELECT c FROM CatNormasConstruccionHasRetirosAumento c WHERE c.id = :id"),
    @NamedQuery(name = "CatNormasConstruccionHasRetirosAumento.findByCodigo", query = "SELECT c FROM CatNormasConstruccionHasRetirosAumento c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "CatNormasConstruccionHasRetirosAumento.findByDescripcion", query = "SELECT c FROM CatNormasConstruccionHasRetirosAumento c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "CatNormasConstruccionHasRetirosAumento.findByEstado", query = "SELECT c FROM CatNormasConstruccionHasRetirosAumento c WHERE c.estado = :estado")})
public class CatNormasConstruccionHasRetirosAumento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    /**
     * 1=retiro_frontal; 2=retiro_lateral; 3=retiro_posterior;
     * 4=aumento_max_edif; 5=aumento_max_cerramiento;
     * 6=aumento_antepecho_ventana; 7=disposicion_general;
     *
     */
    @Column(name = "codigo")
    private BigInteger codigo;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @JoinColumn(name = "norma_construccion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatNormasConstruccion normaConstruccion;

    @Transient
    private String nombreCodigo;

    public CatNormasConstruccionHasRetirosAumento() {
    }

    public CatNormasConstruccionHasRetirosAumento(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 1=retiro_frontal; 2=retiro_lateral; 3=retiro_posterior;
     * 4=aumento_max_edif; 5=aumento_max_cerramiento;
     * 6=aumento_antepecho_ventana; 7=disposicion_general;
     *
     */
    public BigInteger getCodigo() {
        return codigo;
    }

    /**
     * 1=retiro_frontal; 2=retiro_lateral; 3=retiro_posterior;
     * 4=aumento_max_edif; 5=aumento_max_cerramiento;
     * 6=aumento_antepecho_ventana; 7=disposicion_general;
     *
     */
    public void setCodigo(BigInteger codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public CatNormasConstruccion getNormaConstruccion() {
        return normaConstruccion;
    }

    public void setNormaConstruccion(CatNormasConstruccion normaConstruccion) {
        this.normaConstruccion = normaConstruccion;
    }

    public String getNombreCodigo() {
        return nombreCodigo;
    }

    public void setNombreCodigo(String nombreCodigo) {
        this.nombreCodigo = nombreCodigo;
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
        if (!(object instanceof CatNormasConstruccionHasRetirosAumento)) {
            return false;
        }
        CatNormasConstruccionHasRetirosAumento other = (CatNormasConstruccionHasRetirosAumento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatNormasConstruccionHasRetirosAumento[ id=" + id + " ]";
    }

}
