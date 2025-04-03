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
@Table(name = "pe_inspeccion_fotos", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeInspeccionFotos.findAll", query = "SELECT p FROM PeInspeccionFotos p"),
    @NamedQuery(name = "PeInspeccionFotos.findById", query = "SELECT p FROM PeInspeccionFotos p WHERE p.id = :id"),
    @NamedQuery(name = "PeInspeccionFotos.findByImagenNombre", query = "SELECT p FROM PeInspeccionFotos p WHERE p.imagenNombre = :imagenNombre")})
public class PeInspeccionFotos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 80)
    @Column(name = "imagen_nombre", length = 80)
    private String imagenNombre;
    @Column(name = "imagen_file")
    private byte[] imagenFile;
    @JoinColumn(name = "inspeccion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeInspeccionFinal inspeccion;

    public PeInspeccionFotos() {
    }

    public PeInspeccionFotos(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagenNombre() {
        return imagenNombre;
    }

    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }

    public byte[] getImagenFile() {
        return imagenFile;
    }

    public void setImagenFile(byte[] imagenFile) {
        this.imagenFile = imagenFile;
    }

    public PeInspeccionFinal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(PeInspeccionFinal inspeccion) {
        this.inspeccion = inspeccion;
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
        if (!(object instanceof PeInspeccionFotos)) {
            return false;
        }
        PeInspeccionFotos other = (PeInspeccionFotos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeInspeccionFotos[ id=" + id + " ]";
    }
    
}
