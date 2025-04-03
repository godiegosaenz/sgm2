/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCiudadela;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_normas_construccion", schema = SchemasConfig.APP1)
public class CatNormasConstruccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "retiro_lateral")
    private String retiroLateral;

    @Column(name = "retiro_posterior")
    private String retiroPosterior;

    @Column(name = "altura_maxima_edificacion")
    private String alturaMaximaEdificacion;

    @Column(name = "altura_maxima_cerramientos")
    private String alturaMaximaCerramientos;

    @Column(name = "altura_antepechos_ventanas")
    private String alturaAntepechosVentanas;

    @Column(name = "estructura")
    private String estructura;

    @Column(name = "cubierta")
    private String cubierta;

    @Column(name = "disposiciones_generales")
    private String disposicionesGenerales;

    @Column(name = "imafoto")
    private byte[] imafoto;

    @Column(name = "retiro_frontal")
    private String retiroFrontal;
    
    @Column(name = "indicaciones_generales")
    private String indicacionesGenerales;
    
    @Column(name = "secuencia")
    private BigInteger secuencia;

    @Column(name = "estado")
    private String estado;
    
    @OneToMany(mappedBy = "normaConstruccion", fetch = FetchType.LAZY)
    private Collection<CatSolicitudNormaConstruccion> catSolicitudNormaConstruccionCollection;
    @JoinColumn(name = "tipo_norma", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatNormasConstruccionTipo tipoNorma;
    @JoinColumn(name = "id_ciudadela", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela idCiudadela;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "normaConstruccion", fetch = FetchType.LAZY)
    @OrderBy("codigo ASC")
    private Collection<CatNormasConstruccionHasRetirosAumento> catNormasConstruccionHasRetirosAumentoCollection;

    public CatNormasConstruccion() {
    }

    public CatNormasConstruccion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRetiroLateral() {
        return retiroLateral;
    }

    public void setRetiroLateral(String retiroLateral) {
        this.retiroLateral = retiroLateral;
    }

    public String getRetiroPosterior() {
        return retiroPosterior;
    }

    public void setRetiroPosterior(String retiroPosterior) {
        this.retiroPosterior = retiroPosterior;
    }

    public String getAlturaMaximaEdificacion() {
        return alturaMaximaEdificacion;
    }

    public void setAlturaMaximaEdificacion(String alturaMaximaEdificacion) {
        this.alturaMaximaEdificacion = alturaMaximaEdificacion;
    }

    public String getAlturaMaximaCerramientos() {
        return alturaMaximaCerramientos;
    }

    public void setAlturaMaximaCerramientos(String alturaMaximaCerramientos) {
        this.alturaMaximaCerramientos = alturaMaximaCerramientos;
    }

    public String getAlturaAntepechosVentanas() {
        return alturaAntepechosVentanas;
    }

    public void setAlturaAntepechosVentanas(String alturaAntepechosVentanas) {
        this.alturaAntepechosVentanas = alturaAntepechosVentanas;
    }

    public String getEstructura() {
        return estructura;
    }

    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }

    public String getCubierta() {
        return cubierta;
    }

    public void setCubierta(String cubierta) {
        this.cubierta = cubierta;
    }

    public String getDisposicionesGenerales() {
        return disposicionesGenerales;
    }

    public void setDisposicionesGenerales(String disposicionesGenerales) {
        this.disposicionesGenerales = disposicionesGenerales;
    }

    public byte[] getImafoto() {
        return imafoto;
    }

    public void setImafoto(byte[] imafoto) {
        this.imafoto = imafoto;
    }

    public String getRetiroFrontal() {
        return retiroFrontal;
    }

    public void setRetiroFrontal(String retiroFrontal) {
        this.retiroFrontal = retiroFrontal;
    }

    public String getIndicacionesGenerales() {
        return indicacionesGenerales;
    }

    public void setIndicacionesGenerales(String indicacionesGenerales) {
        this.indicacionesGenerales = indicacionesGenerales;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Collection<CatSolicitudNormaConstruccion> getCatSolicitudNormaConstruccionCollection() {
        return catSolicitudNormaConstruccionCollection;
    }

    public void setCatSolicitudNormaConstruccionCollection(Collection<CatSolicitudNormaConstruccion> catSolicitudNormaConstruccionCollection) {
        this.catSolicitudNormaConstruccionCollection = catSolicitudNormaConstruccionCollection;
    }

    public CatNormasConstruccionTipo getTipoNorma() {
        return tipoNorma;
    }

    public void setTipoNorma(CatNormasConstruccionTipo tipoNorma) {
        this.tipoNorma = tipoNorma;
    }

    public CatCiudadela getIdCiudadela() {
        return idCiudadela;
    }

    public void setIdCiudadela(CatCiudadela idCiudadela) {
        this.idCiudadela = idCiudadela;
    }

    public Collection<CatNormasConstruccionHasRetirosAumento> getCatNormasConstruccionHasRetirosAumentoCollection() {
        return catNormasConstruccionHasRetirosAumentoCollection;
    }

    public void setCatNormasConstruccionHasRetirosAumentoCollection(Collection<CatNormasConstruccionHasRetirosAumento> catNormasConstruccionHasRetirosAumentoCollection) {
        this.catNormasConstruccionHasRetirosAumentoCollection = catNormasConstruccionHasRetirosAumentoCollection;
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
        if (!(object instanceof CatNormasConstruccion)) {
            return false;
        }
        CatNormasConstruccion other = (CatNormasConstruccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatNormasConstruccion[ id=" + id + " ]";
    }
    
}
