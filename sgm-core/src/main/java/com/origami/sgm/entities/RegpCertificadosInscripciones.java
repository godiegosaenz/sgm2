/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatTransferenciaDominio;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_certificados_inscripciones", schema = SchemasConfig.FLOW)
public class RegpCertificadosInscripciones implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "realizado")
    private Boolean realizado = false;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;
    @Column(name = "tipo_tarea")
    private String tipoTarea;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "num_ficha")
    private Long numFicha;
    @Column(name = "id_movimiento")
    private Long idMovimiento;
    @Column(name = "acl_user")
    private Long aclUser;
    
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpLiquidacionDerechosAranceles liquidacion;
    @JoinColumn(name = "acto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpActosIngreso acto;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "regpCertificadoInscripcion", fetch = FetchType.LAZY)
    private RegMovimiento regMovimiento;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tareaRegistro", fetch = FetchType.LAZY)
    private CatTransferenciaDominio catTransferenciaDominio;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "regpCertificadoInscripciones", fetch = FetchType.LAZY)
    private RegCertificado regCertificado;

    @JoinColumn(name = "tarea_dinardap", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpTareasDinardap tareaDinardap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
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

    public RegpLiquidacionDerechosAranceles getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RegpLiquidacionDerechosAranceles liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RegpActosIngreso getActo() {
        return acto;
    }

    public void setActo(RegpActosIngreso acto) {
        this.acto = acto;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public RegMovimiento getRegMovimiento() {
        return regMovimiento;
    }

    public void setRegMovimiento(RegMovimiento regMovimiento) {
        this.regMovimiento = regMovimiento;
    }

    public String getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(String tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public RegCertificado getRegCertificado() {
        return regCertificado;
    }

    public void setRegCertificado(RegCertificado regCertificado) {
        this.regCertificado = regCertificado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(Long numFicha) {
        this.numFicha = numFicha;
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Long getAclUser() {
        return aclUser;
    }

    public void setAclUser(Long aclUser) {
        this.aclUser = aclUser;
    }

    public RegpTareasDinardap getTareaDinardap() {
        return tareaDinardap;
    }

    public void setTareaDinardap(RegpTareasDinardap tareaDinardap) {
        this.tareaDinardap = tareaDinardap;
    }

    public CatTransferenciaDominio getCatTransferenciaDominio() {
        return catTransferenciaDominio;
    }

    public void setCatTransferenciaDominio(CatTransferenciaDominio catTransferenciaDominio) {
        this.catTransferenciaDominio = catTransferenciaDominio;
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
        if (!(object instanceof RegpCertificadosInscripciones)) {
            return false;
        }
        RegpCertificadosInscripciones other = (RegpCertificadosInscripciones) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpCertificadosInscripciones[ id=" + id + " ]";
    }
}
