/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_documentos_etiquetas", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeDocumentosEtiquetas.findAll", query = "SELECT g FROM GeDocumentosEtiquetas g"),
    @NamedQuery(name = "GeDocumentosEtiquetas.findById", query = "SELECT g FROM GeDocumentosEtiquetas g WHERE g.id = :id"),
    @NamedQuery(name = "GeDocumentosEtiquetas.findByPalabraClave", query = "SELECT g FROM GeDocumentosEtiquetas g WHERE g.palabraClave = :palabraClave"),
    @NamedQuery(name = "GeDocumentosEtiquetas.findByPalabra", query = "SELECT g FROM GeDocumentosEtiquetas g WHERE g.palabra = :palabra")})
public class GeDocumentosEtiquetas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 255)
    @Column(name = "palabra_clave", length = 255)
    private String palabraClave;
    @Size(max = 255)
    @Column(name = "palabra", length = 255)
    private String palabra;
    @JoinColumn(name = "documento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeDocumentos documento;

    public GeDocumentosEtiquetas() {
    }

    public GeDocumentosEtiquetas(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalabraClave() {
        return palabraClave;
    }

    public void setPalabraClave(String palabraClave) {
        this.palabraClave = palabraClave;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public GeDocumentos getDocumento() {
        return documento;
    }

    public void setDocumento(GeDocumentos documento) {
        this.documento = documento;
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
        if (!(object instanceof GeDocumentosEtiquetas)) {
            return false;
        }
        GeDocumentosEtiquetas other = (GeDocumentosEtiquetas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeDocumentosEtiquetas[ id=" + id + " ]";
    }
    
}
