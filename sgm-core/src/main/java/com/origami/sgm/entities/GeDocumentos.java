/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.historic.Predio;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_documentos", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeDocumentos.findAll", query = "SELECT g FROM GeDocumentos g"),
    @NamedQuery(name = "GeDocumentos.findById", query = "SELECT g FROM GeDocumentos g WHERE g.id = :id"),
    @NamedQuery(name = "GeDocumentos.findByNombre", query = "SELECT g FROM GeDocumentos g WHERE g.nombre = :nombre"),
    @NamedQuery(name = "GeDocumentos.findByFechaCreacion", query = "SELECT g FROM GeDocumentos g WHERE g.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "GeDocumentos.findByFechaHasta", query = "SELECT g FROM GeDocumentos g WHERE g.fechaHasta = :fechaHasta"),
    @NamedQuery(name = "GeDocumentos.findByEstado", query = "SELECT g FROM GeDocumentos g WHERE g.estado = :estado"),
    @NamedQuery(name = "GeDocumentos.findByReforma", query = "SELECT g FROM GeDocumentos g WHERE g.reforma = :reforma"),
    @NamedQuery(name = "GeDocumentos.findByDescripcion", query = "SELECT g FROM GeDocumentos g WHERE g.descripcion = :descripcion"),
    @NamedQuery(name = "GeDocumentos.findByRutaDocumento", query = "SELECT g FROM GeDocumentos g WHERE g.rutaDocumento = :rutaDocumento"),
    @NamedQuery(name = "GeDocumentos.findByRaiz", query = "SELECT g FROM GeDocumentos g WHERE g.raiz = :raiz"),
    @NamedQuery(name = "GeDocumentos.findByIdentificacion", query = "SELECT g FROM GeDocumentos g WHERE g.identificacion = :identificacion"),
    @NamedQuery(name = "GeDocumentos.findByIdentDocum", query = "SELECT g FROM GeDocumentos g WHERE g.identDocum = :identDocum"),
    @NamedQuery(name = "GeDocumentos.findByDepartamento", query = "SELECT g FROM GeDocumentos g WHERE g.departamento = :departamento"),
    @NamedQuery(name = "GeDocumentos.findByVisible", query = "SELECT g FROM GeDocumentos g WHERE g.visible = :visible")})
public class GeDocumentos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 255)
    @Column(name = "nombre", length = 255)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "fecha_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHasta;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "reforma")
    private Boolean reforma;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    @Size(max = 4000)
    @Column(name = "ruta_documento", length = 4000)
    private String rutaDocumento;
    @Column(name = "raiz")
    private BigInteger raiz;
    @Size(max = 50)
    @Column(name = "identificacion", length = 50)
    private String identificacion;
    @Size(max = 50)
    @Column(name = "ident_docum", length = 50)
    private String identDocum;
    @Size(max = 100)
    @Column(name = "departamento", length = 100)
    private String departamento;
    @Column(name = "visible")
    private Boolean visible;
    @OneToMany(mappedBy = "baseLegal", fetch = FetchType.LAZY)
    private Collection<GeTramiteBaseLegal> geTramiteBaseLegalCollection;
    @OneToMany(mappedBy = "documento", fetch = FetchType.LAZY)
    private Collection<GeDocumentosEtiquetas> geDocumentosEtiquetasCollection;
    @Column(name = "tipo_legal")
    private BigInteger tipoLegal;
    @OneToMany(mappedBy = "geDocumento")
    private List<Predio> predios;

    public GeDocumentos() {
    }

    public GeDocumentos(Long id) {
        this.id = id;
    }

    public GeDocumentos(Long id, Date fechaCreacion) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getReforma() {
        return reforma;
    }

    public void setReforma(Boolean reforma) {
        this.reforma = reforma;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento = rutaDocumento;
    }

    public BigInteger getRaiz() {
        return raiz;
    }

    public void setRaiz(BigInteger raiz) {
        this.raiz = raiz;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getIdentDocum() {
        return identDocum;
    }

    public void setIdentDocum(String identDocum) {
        this.identDocum = identDocum;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTramiteBaseLegal> getGeTramiteBaseLegalCollection() {
        return geTramiteBaseLegalCollection;
    }

    public void setGeTramiteBaseLegalCollection(Collection<GeTramiteBaseLegal> geTramiteBaseLegalCollection) {
        this.geTramiteBaseLegalCollection = geTramiteBaseLegalCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeDocumentosEtiquetas> getGeDocumentosEtiquetasCollection() {
        return geDocumentosEtiquetasCollection;
    }

    public void setGeDocumentosEtiquetasCollection(Collection<GeDocumentosEtiquetas> geDocumentosEtiquetasCollection) {
        this.geDocumentosEtiquetasCollection = geDocumentosEtiquetasCollection;
    }

    public BigInteger getTipoLegal() {
        return tipoLegal;
    }

    public void setTipoLegal(BigInteger tipoLegal) {
        this.tipoLegal = tipoLegal;
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
        if (!(object instanceof GeDocumentos)) {
            return false;
        }
        GeDocumentos other = (GeDocumentos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeDocumentos[ id=" + id + " ]";
    }

    public List<Predio> getPredios() {
        return predios;
    }

    public void setPredios(List<Predio> predios) {
        this.predios = predios;
    }
    
}
