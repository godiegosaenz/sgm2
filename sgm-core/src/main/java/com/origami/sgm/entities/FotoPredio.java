/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.OrdenDet;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "foto_predio", schema = SchemasConfig.APP1)
@SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 20)
@NamedQueries({
    @NamedQuery(name = "FotoPredio.findByIdPredio", query = "SELECT f FROM FotoPredio f WHERE f.idPredio = :idPredio")})
public class FotoPredio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "file_oid", nullable = false)
    private Long fileOid;

    @Basic(optional = false)
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "content_type", length = 255, nullable = true)
    private String contentType;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Basic(optional = false)
    @Column(name = "sis_enabled", nullable = false)
    private Boolean sisEnabled;

    @Column(name = "cod_predio")
    private Long codPredio;

    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrdenDet predio;

    @Column(name = "id_predio")
    private Long idPredio;
    @Column(name = "bloque")
    private Long bloque;

    public FotoPredio() {
    }

    public FotoPredio(Long fileOid, String nombre, String contentType, String descripcion, Boolean sisEnabled, Long codPredio) {
        this.fileOid = fileOid;
        this.nombre = nombre;
        this.contentType = contentType;
        this.descripcion = descripcion;
        this.sisEnabled = sisEnabled;
        this.codPredio = codPredio;
    }

    public FotoPredio(Long fileOid, String nombre, String contentType, String descripcion, Long codPredio) {
        this.fileOid = fileOid;
        this.nombre = nombre;
        this.contentType = contentType;
        this.descripcion = descripcion;
        this.codPredio = codPredio;
    }

    public FotoPredio(Long fileOid, String nombre, String contentType, String descripcion, Long codPredio, Long idLong) {
        this.fileOid = fileOid;
        this.nombre = nombre;
        this.contentType = contentType;
        this.descripcion = descripcion;
        this.codPredio = codPredio;
        this.idPredio = idLong;
    }

    public FotoPredio(Long fileOid, String nombre, String contentType, String descripcion, Long codPredio, Long idLong, Long bloque) {
        this.fileOid = fileOid;
        this.nombre = nombre;
        this.contentType = contentType;
        this.descripcion = descripcion;
        this.codPredio = codPredio;
        this.idPredio = idLong;
        this.bloque = bloque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileOid() {
        return fileOid;
    }

    public void setFileOid(Long fileOid) {
        this.fileOid = fileOid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getSisEnabled() {
        return sisEnabled;
    }

    public void setSisEnabled(Boolean sisEnabled) {
        this.sisEnabled = sisEnabled;
    }

    public Long getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(Long codPredio) {
        this.codPredio = codPredio;
    }

    public OrdenDet getPredio() {
        return predio;
    }

    public void setPredio(OrdenDet predio) {
        this.predio = predio;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
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
        final FotoPredio other = (FotoPredio) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public Long getBloque() {
        return bloque;
    }

    public void setBloque(Long bloque) {
        this.bloque = bloque;
    }

}
