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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_tareas_dinardap_docs", schema = SchemasConfig.FLOW)
public class RegpTareasDinardapDocs implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "tarea_dinardap", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpTareasDinardap regpTareasDinardap;
    
    @Column(name = "documento")
    private String documento;
    
    @Column(name = "estado")
    private Boolean estado = true;

    @Column(name = "nombre_doc")
    private String nombreDoc;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegpTareasDinardap getRegpTareasDinardap() {
        return regpTareasDinardap;
    }

    public void setRegpTareasDinardap(RegpTareasDinardap regpTareasDinardap) {
        this.regpTareasDinardap = regpTareasDinardap;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
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
        if (!(object instanceof RegpTareasDinardapDocs)) {
            return false;
        }
        RegpTareasDinardapDocs other = (RegpTareasDinardapDocs) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpTareasDinardapDocs[ id=" + id + " ]";
    }
}
