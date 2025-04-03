package com.origami.sgm.entities;

/**
 *
 * @author elcid
 */
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "cat_predio_cultivo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioCultivo.findAll", query = "SELECT c FROM CatPredioCultivo c")
    ,
    @NamedQuery(name = "CatPredioCultivo.findById", query = "SELECT c FROM CatPredioCultivo c WHERE c.id = :id")
    ,    
    @NamedQuery(name = "CatPredioCultivo.findByModificado", query = "SELECT c FROM CatPredioCultivo c WHERE c.modificado = :modificado")
    ,
    @NamedQuery(name = "CatPredioCultivo.findByEstado", query = "SELECT c FROM CatPredioCultivo c WHERE c.estado = :estado")})
public class CatPredioCultivo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;

    @Size(max = 20)
    @Column(name = "modificado", length = 20)
    @Expose
    private String modificado;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    @Expose
    private String estado = "A";
    @Column(name = "usuario", length = 100)
    @Expose
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;
//    @Column(name = "tipo")
//    private CtlgItem tipo;

    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipo;

    @Column(name = "plantacion", length = 100)
    @Expose
    private String plantacion;
    @Column(name = "area")
    @Expose
    private BigDecimal area;
    @Column(name = "cantidad")
    @Expose
    private Integer cantidad;
    @Column(name = "valor")
    @Expose
    private BigDecimal valor;
    @Column(name = "edad")
    @Expose
    private short edad;
//    @Column(name = "conservacion", length = 1)
//    private CtlgItem conservacion;
    @JoinColumn(name = "conservacion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Expose
    private CtlgItem conservacion;
    @Column(name = "observaciones", length = 5000)
    @Expose
    private String observaciones;

    public CatPredioCultivo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public String getPlantacion() {
        return plantacion;
    }

    public void setPlantacion(String plantacion) {
        this.plantacion = plantacion;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public short getEdad() {
        return edad;
    }

    public void setEdad(short edad) {
        this.edad = edad;
    }

    public CtlgItem getTipo() {
        return tipo;
    }

    public void setTipo(CtlgItem tipo) {
        this.tipo = tipo;
    }

    public CtlgItem getConservacion() {
        return conservacion;
    }

    public void setConservacion(CtlgItem conservacion) {
        this.conservacion = conservacion;
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
        if (!(object instanceof CatPredioCultivo)) {
            return false;
        }
        CatPredioCultivo other = (CatPredioCultivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioCultivo[ id=" + id + " ]";
    }
}
