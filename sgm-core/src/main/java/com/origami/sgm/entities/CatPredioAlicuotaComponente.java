/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dfcalderio
 */
@Entity
@Table(name = "cat_predio_alicuota_componente", schema = SchemasConfig.APP1)
@XmlRootElement

public class CatPredioAlicuotaComponente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;

    @Size(max = 150)
    @Column(name = "nombre", length = 150)
    @Expose
    private String nombre;

    @Size(max = 100)
    @Column(name = "numero", length = 100)
    @Expose
    private String numero;

    @Column(name = "area_construccion", precision = 10, scale = 4)
    @Expose
    private BigDecimal areaConstruccion;

    @Column(name = "area_declarada", precision = 10, scale = 4)
    @Expose
    private BigDecimal areaDeclarada;

    @Column(name = "alicuota_util", precision = 10, scale = 4)
    @Expose
    private BigDecimal alicuotaUtil;

    @Column(name = "alicuota_comunal", precision = 10, scale = 4)
    @Expose
    private BigDecimal alicuotaComunal;

    @Size(max = 255)
    @Column(name = "descripcion_ubicacion", length = 255)
    @Expose
    private String descripcionUbicacion;

    @JoinColumn(name = "id_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;

    public CatPredioAlicuotaComponente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAreaDeclarada() {
        return areaDeclarada;
    }

    public void setAreaDeclarada(BigDecimal areaDeclarada) {
        this.areaDeclarada = areaDeclarada;
    }

    public BigDecimal getAlicuotaUtil() {
        return alicuotaUtil;
    }

    public void setAlicuotaUtil(BigDecimal alicuotaUtil) {
        this.alicuotaUtil = alicuotaUtil;
    }

    public BigDecimal getAlicuotaComunal() {
        return alicuotaComunal;
    }

    public void setAlicuotaComunal(BigDecimal alicuotaComunal) {
        this.alicuotaComunal = alicuotaComunal;
    }

    public String getDescripcionUbicacion() {
        return descripcionUbicacion;
    }

    public void setDescripcionUbicacion(String descripcionUbicacion) {
        this.descripcionUbicacion = descripcionUbicacion;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CatPredioAlicuotaComponente) {
            CatPredioAlicuotaComponente other = (CatPredioAlicuotaComponente) obj;
            return this.id.equals(other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
