/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.FnSolicitudExoneracion;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_tipo_valor", schema = SchemasConfig.FINANCIERO)
public class RenTipoValor implements Serializable {
    
    @OneToMany(mappedBy = "tipoValor")
    private Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "es_porcentaje")
    private Boolean esPorcentaje;
    @Size(max = 3)
    @Column(name = "prefijo")
    private String prefijo;
    @OneToMany(mappedBy = "tipoValor", fetch = FetchType.LAZY)
    private Collection<RenRubrosLiquidacion> renRubrosLiquidacionCollection;  

    public RenTipoValor(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RenTipoValor other = (RenTipoValor) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenTipoValor{" + "id=" + id + '}';
    }

    public Collection<RenRubrosLiquidacion> getRenRubrosLiquidacionCollection() {
        return renRubrosLiquidacionCollection;
    }

    public void setRenRubrosLiquidacionCollection(Collection<RenRubrosLiquidacion> renRubrosLiquidacionCollection) {
        this.renRubrosLiquidacionCollection = renRubrosLiquidacionCollection;
    }

    public RenTipoValor() {
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<FnSolicitudExoneracion> getFnSolicitudExoneracionCollection() {
        return fnSolicitudExoneracionCollection;
    }

    public void setFnSolicitudExoneracionCollection(Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection) {
        this.fnSolicitudExoneracionCollection = fnSolicitudExoneracionCollection;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Boolean getEsPorcentaje() {
        return esPorcentaje;
    }

    public void setEsPorcentaje(Boolean esPorcentaje) {
        this.esPorcentaje = esPorcentaje;
    }
    
}
