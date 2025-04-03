/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.application;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "empresa", schema = SchemasConfig.APPLICATION)
@SequenceGenerator(name = "appl_empresa_id_seq", sequenceName = SchemasConfig.APPLICATION + ".empresa_id_seq", allocationSize = 1)
public class ApplEmpresa implements Serializable{
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appl_empresa_id_seq")
    private Long id;
    
    @Column(name = "razon_social", length = 200)
    private String razonSocial;
    
    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;
    
    @JoinColumn(name = "tipo_empresa", referencedColumnName = "id")
    @ManyToOne
    private ApplTipoEmpresa tipoEmpresa;
    
    @JoinColumn(name = "act_econ", referencedColumnName = "id")
    @ManyToOne
    private ApplActividadEconomica actEcon;
    
    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;
    
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    
    @Column(name = "estado")
    private Integer estado;
    
    @Column(name = "codigo_canton")
    private Long codigoCanton;
    
    @Column(name = "codigo_provincia")
    private Long codigoProvincia;

    @Column(name = "url_predio")
    private String urlPredio;

    public String getUrlPredio() {
        return urlPredio;
    }

    public void setUrlPredio(String urlPredio) {
        this.urlPredio = urlPredio;
    }

    
    
    
    public ApplEmpresa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public ApplTipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(ApplTipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public ApplActividadEconomica getActEcon() {
        return actEcon;
    }

    public void setActEcon(ApplActividadEconomica actEcon) {
        this.actEcon = actEcon;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Long getCodigoCanton() {
        return codigoCanton;
    }

    public void setCodigoCanton(Long codigoCanton) {
        this.codigoCanton = codigoCanton;
    }

    public Long getCodigoProvincia() {
        return codigoProvincia;
    }

    public void setCodigoProvincia(Long codigoProvincia) {
        this.codigoProvincia = codigoProvincia;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplEmpresa other = (ApplEmpresa) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}
