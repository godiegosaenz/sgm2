package com.origami.sgm.entities;

import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @date 20/07/2016
 */
@Entity
@Table(name = "ren_tasa_turismo", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenTasaTurismo.findAll", query = "SELECT r FROM RenTasaTurismo r"),
    @NamedQuery(name = "RenTasaTurismo.findById", query = "SELECT r FROM RenTasaTurismo r WHERE r.id = :id"),
    @NamedQuery(name = "RenTasaTurismo.findByDescripcion", query = "SELECT r FROM RenTasaTurismo r WHERE r.descripcion = :descripcion"),
    @NamedQuery(name = "RenTasaTurismo.findByTipo", query = "SELECT r FROM RenTasaTurismo r WHERE r.tipo = :tipo"),
    @NamedQuery(name = "RenTasaTurismo.findByValor", query = "SELECT r FROM RenTasaTurismo r WHERE r.valor = :valor"),
    @NamedQuery(name = "RenTasaTurismo.findByMaximo", query = "SELECT r FROM RenTasaTurismo r WHERE r.maximo = :maximo"),
    @NamedQuery(name = "RenTasaTurismo.findByFechaIngreso", query = "SELECT r FROM RenTasaTurismo r WHERE r.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "RenTasaTurismo.findByEstado", query = "SELECT r FROM RenTasaTurismo r WHERE r.estado = :estado"),
    @NamedQuery(name = "RenTasaTurismo.findByActividad", query = "SELECT r FROM RenTasaTurismo r WHERE r.actividad = :actividad")})
public class RenTasaTurismo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    @Column(name = "tipo")
    private Integer tipo;
    @Basic(optional = false)
    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
    @Column(name = "maximo", precision = 19, scale = 2)
    private BigDecimal maximo;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "actividad", referencedColumnName = "id")
    @ManyToOne
    private RenActividadComercial actividad;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne
    private RenLocalCategoria categoria;

    public RenTasaTurismo() {
    }

    public RenTasaTurismo(Long id) {
        this.id = id;
    }

    public RenTasaTurismo(Long id, BigDecimal valor) {
        this.id = id;
        this.valor = valor;
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

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public RenActividadComercial getActividad() {
        return actividad;
    }

    public void setActividad(RenActividadComercial actividad) {
        this.actividad = actividad;
    }

    public RenLocalCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(RenLocalCategoria categoria) {
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
        if (!(object instanceof RenTasaTurismo)) {
            return false;
        }
        RenTasaTurismo other = (RenTasaTurismo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenTasaTurismo[ id=" + id + " ]";
    }

}
