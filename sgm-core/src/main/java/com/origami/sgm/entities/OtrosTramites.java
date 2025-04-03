/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.BaseCalculoOtrosTramites;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "otros_tramites", schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OtrosTramites.findAll", query = "SELECT o FROM OtrosTramites o"),
    @NamedQuery(name = "OtrosTramites.findById", query = "SELECT o FROM OtrosTramites o WHERE o.id = :id"),
    @NamedQuery(name = "OtrosTramites.findByTipoTramite", query = "SELECT o FROM OtrosTramites o WHERE o.tipoTramite = :tipoTramite"),
    @NamedQuery(name = "OtrosTramites.findByNecesitaPredio", query = "SELECT o FROM OtrosTramites o WHERE o.necesitaPredio = :necesitaPredio"),
    @NamedQuery(name = "OtrosTramites.findByEstado", query = "SELECT o FROM OtrosTramites o WHERE o.estado = :estado"),
    @NamedQuery(name = "OtrosTramites.findByNecesitaValija", query = "SELECT o FROM OtrosTramites o WHERE o.necesitaValija = :necesitaValija")})
public class OtrosTramites implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 250)
    @Column(name = "tipo_tramite", length = 250)
    private String tipoTramite;
    @Size(max = 1000)
    @Column(name = "nota", length = 1000)
    private String nota;
    
    @Column(name = "necesita_predio")
    private Boolean necesitaPredio;
    @Column(name = "tipo_seleccion")
    private BigInteger tipoSeleccion;
    @Column(name = "factor")
    private BigDecimal factor;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @Size(max = 10)
    @Column(name = "prefijo", length = 10)
    private String prefijo;
    @Size(max = 150)
    @Column(name = "titulo_reporte", length = 150)
    private String tituloReporte;
    @Size(max = 150)
    @Column(name = "codigo_aplicacion", length = 150)
    private String codigoAplicacion;
    @Column(name = "necesita_valija")
    private Boolean necesitaValija;
    @JoinTable(name = "requisitos_otros_tramites_has", joinColumns = {
        @JoinColumn(name = "otros_tramites", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "requisitos_otros_tramites", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<RequisitosOtrosTramite> requisitosOtrosTramiteCollection;
    @OneToMany(mappedBy = "subTipoTramite", fetch = FetchType.LAZY)
    private Collection<HistoricoTramites> historicoTramitesCollection;
    
    @JoinColumn(name = "tipo_otros_tramites", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoOtrosTramites tipoDeTramite;
    @JoinColumn(name = "base_calculo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private BaseCalculoOtrosTramites baseCalculo;
    
    @OneToMany(mappedBy = "otrosTramites", fetch = FetchType.LAZY)
    private Collection<OtrosTramitesHasPermiso> otrosTramitesHasPermisoCollection;

    public OtrosTramites() {
    }

    public OtrosTramites(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Boolean getNecesitaPredio() {
        return necesitaPredio;
    }

    public void setNecesitaPredio(Boolean necesitaPredio) {
        this.necesitaPredio = necesitaPredio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getNecesitaValija() {
        return necesitaValija;
    }

    public void setNecesitaValija(Boolean necesitaValija) {
        this.necesitaValija = necesitaValija;
    }

    public TipoOtrosTramites getTipoDeTramite() {
        return tipoDeTramite;
    }

    public void setTipoDeTramite(TipoOtrosTramites tipoDeTramite) {
        this.tipoDeTramite = tipoDeTramite;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RequisitosOtrosTramite> getRequisitosOtrosTramiteCollection() {
        return requisitosOtrosTramiteCollection;
    }

    public void setRequisitosOtrosTramiteCollection(Collection<RequisitosOtrosTramite> requisitosOtrosTramiteCollection) {
        this.requisitosOtrosTramiteCollection = requisitosOtrosTramiteCollection;
    }

    public BaseCalculoOtrosTramites getBaseCalculo() {
        return baseCalculo;
    }

    public BigInteger getTipoSeleccion() {
        return tipoSeleccion;
    }

    public void setTipoSeleccion(BigInteger tipoSeleccion) {
        this.tipoSeleccion = tipoSeleccion;
    }

    public void setBaseCalculo(BaseCalculoOtrosTramites baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HistoricoTramites> getHistoricoTramitesCollection() {
        return historicoTramitesCollection;
    }

    public void setHistoricoTramitesCollection(Collection<HistoricoTramites> historicoTramitesCollection) {
        this.historicoTramitesCollection = historicoTramitesCollection;
    }

    public Collection<OtrosTramitesHasPermiso> getOtrosTramitesHasPermisoCollection() {
        return otrosTramitesHasPermisoCollection;
    }

    public void setOtrosTramitesHasPermisoCollection(Collection<OtrosTramitesHasPermiso> otrosTramitesHasPermisoCollection) {
        this.otrosTramitesHasPermisoCollection = otrosTramitesHasPermisoCollection;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTituloReporte() {
        return tituloReporte;
    }

    public void setTituloReporte(String tituloReporte) {
        this.tituloReporte = tituloReporte;
    }

    public String getCodigoAplicacion() {
        return codigoAplicacion;
    }

    public void setCodigoAplicacion(String codigoAplicacion) {
        this.codigoAplicacion = codigoAplicacion;
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
        if (!(object instanceof OtrosTramites)) {
            return false;
        }
        OtrosTramites other = (OtrosTramites) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.OtrosTramites[ id=" + id + " ]";
    }
    
}
