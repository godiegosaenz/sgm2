/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_categorias_construccion_valores", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatCategoriasConstruccionValores.findAll", query = "SELECT c FROM CatCategoriasConstruccionValores c"),
    @NamedQuery(name = "CatCategoriasConstruccionValores.findById", query = "SELECT c FROM CatCategoriasConstruccionValores c WHERE c.id = :id"),
    @NamedQuery(name = "CatCategoriasConstruccionValores.findByAnioDesde", query = "SELECT c FROM CatCategoriasConstruccionValores c WHERE c.anioDesde = :anioDesde"),
    @NamedQuery(name = "CatCategoriasConstruccionValores.findByAnioHasta", query = "SELECT c FROM CatCategoriasConstruccionValores c WHERE c.anioHasta = :anioHasta"),
    @NamedQuery(name = "CatCategoriasConstruccionValores.findByValorMt2", query = "SELECT c FROM CatCategoriasConstruccionValores c WHERE c.valorMt2 = :valorMt2")})
public class CatCategoriasConstruccionValores implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "anio_desde")
    private Integer anioDesde;
    @Column(name = "anio_hasta")
    private Integer anioHasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_mt2", precision = 15, scale = 2)
    private BigDecimal valorMt2;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCategoriasConstruccion categoria;

    public CatCategoriasConstruccionValores() {
    }

    public CatCategoriasConstruccionValores(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(Integer anioDesde) {
        this.anioDesde = anioDesde;
    }

    public Integer getAnioHasta() {
        return anioHasta;
    }

    public void setAnioHasta(Integer anioHasta) {
        this.anioHasta = anioHasta;
    }

    public BigDecimal getValorMt2() {
        return valorMt2;
    }

    public void setValorMt2(BigDecimal valorMt2) {
        this.valorMt2 = valorMt2;
    }

    public CatCategoriasConstruccion getCategoria() {
        return categoria;
    }

    public void setCategoria(CatCategoriasConstruccion categoria) {
        this.categoria = categoria;
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
        if (!(object instanceof CatCategoriasConstruccionValores)) {
            return false;
        }
        CatCategoriasConstruccionValores other = (CatCategoriasConstruccionValores) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatCategoriasConstruccionValores[ id=" + id + " ]";
    }
    
}
