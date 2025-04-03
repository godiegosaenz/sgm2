/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Por cada ingreso y salida de documentos en un tramite del Registro de la
 * Propiedad se guarda un registro en esta tabla, se la asocia a la tabla
 * RegpLiquidacionDerechosAranceles y sirve para almacenar al cliente que lleva
 * los documentos y al usuario que hace la entrega o recepcion de los mismos
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_entrada_salida_docs", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "RegpEntradaSalidaDocs.findAll", query = "SELECT r FROM RegpEntradaSalidaDocs r")})
public class RegpEntradaSalidaDocs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "cliente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte cliente;
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "observacion")
    private String observacion;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpLiquidacionDerechosAranceles liquidacion;

    public RegpEntradaSalidaDocs() {
    }

    public RegpEntradaSalidaDocs(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public CatEnte getCliente() {
        return cliente;
    }

    public void setCliente(CatEnte cliente) {
        this.cliente = cliente;
    }

    public RegpLiquidacionDerechosAranceles getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RegpLiquidacionDerechosAranceles liquidacion) {
        this.liquidacion = liquidacion;
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
        if (!(object instanceof RegpEntradaSalidaDocs)) {
            return false;
        }
        RegpEntradaSalidaDocs other = (RegpEntradaSalidaDocs) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpEntradaSalidaDocs[ id=" + id + " ]";
    }

}
