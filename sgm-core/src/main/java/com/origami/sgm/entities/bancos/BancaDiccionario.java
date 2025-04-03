/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.bancos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "banca_diccionario", schema = SchemasConfig.BANCOS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BancaDiccionario.findAll", query = "SELECT b FROM BancaDiccionario b")
    , @NamedQuery(name = "BancaDiccionario.findById", query = "SELECT b FROM BancaDiccionario b WHERE b.id = :id")
    , @NamedQuery(name = "BancaDiccionario.findByAtributo", query = "SELECT b FROM BancaDiccionario b WHERE b.atributo = :atributo")
    , @NamedQuery(name = "BancaDiccionario.findByTipo", query = "SELECT b FROM BancaDiccionario b WHERE b.tipo = :tipo")
    , @NamedQuery(name = "BancaDiccionario.findByFormatoFecHora", query = "SELECT b FROM BancaDiccionario b WHERE b.formatoFecHora = :formatoFecHora")
    , @NamedQuery(name = "BancaDiccionario.findByMaxContenido", query = "SELECT b FROM BancaDiccionario b WHERE b.maxContenido = :maxContenido")
    , @NamedQuery(name = "BancaDiccionario.findByLongitud", query = "SELECT b FROM BancaDiccionario b WHERE b.longitud = :longitud")
    , @NamedQuery(name = "BancaDiccionario.findByPosIniLinea", query = "SELECT b FROM BancaDiccionario b WHERE b.posIniLinea = :posIniLinea")
    , @NamedQuery(name = "BancaDiccionario.findByPosFinLinea", query = "SELECT b FROM BancaDiccionario b WHERE b.posFinLinea = :posFinLinea")
    , @NamedQuery(name = "BancaDiccionario.findByDescripcion", query = "SELECT b FROM BancaDiccionario b WHERE b.descripcion = :descripcion")
    , @NamedQuery(name = "BancaDiccionario.findByRequerido", query = "SELECT b FROM BancaDiccionario b WHERE b.requerido = :requerido")
    , @NamedQuery(name = "BancaDiccionario.findByFecCre", query = "SELECT b FROM BancaDiccionario b WHERE b.fecCre = :fecCre")
    , @NamedQuery(name = "BancaDiccionario.findByUsrCre", query = "SELECT b FROM BancaDiccionario b WHERE b.usrCre = :usrCre")
    , @NamedQuery(name = "BancaDiccionario.findByFecAct", query = "SELECT b FROM BancaDiccionario b WHERE b.fecAct = :fecAct")
    , @NamedQuery(name = "BancaDiccionario.findByUsrAct", query = "SELECT b FROM BancaDiccionario b WHERE b.usrAct = :usrAct")
    , @NamedQuery(name = "BancaDiccionario.findByEstado", query = "SELECT b FROM BancaDiccionario b WHERE b.estado = :estado")
    , @NamedQuery(name = "BancaDiccionario.findByReqValRef", query = "SELECT b FROM BancaDiccionario b WHERE b.reqValRef = :reqValRef")
    , @NamedQuery(name = "BancaDiccionario.findByValorRef", query = "SELECT b FROM BancaDiccionario b WHERE b.valorRef = :valorRef")})
public class BancaDiccionario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "atributo", nullable = false, length = 100)
    private String atributo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;
    @Size(max = 20)
    @Column(name = "formato_fec_hora", length = 20)
    private String formatoFecHora;
    @Column(name = "max_contenido")
    private Integer maxContenido;
    @Column(name = "longitud")
    private Integer longitud;
    @Column(name = "pos_ini_linea")
    private Integer posIniLinea;
    @Column(name = "pos_fin_linea")
    private Integer posFinLinea;
    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    @Column(name = "complemento", length = 10)
    private String complemento;
    @Column(name = "orden")
    private Short orden;
    @Column(name = "requerido")
    private Boolean requerido;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Size(max = 50)
    @Column(name = "usr_act", length = 50)
    private String usrAct;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "req_val_ref")
    private Boolean reqValRef;
    @Size(max = 100)
    @Column(name = "valor_ref", length = 100)
    private String valorRef;
    @Column(name = "req_val_ref_adic")
    private Boolean reqValRefAdic;
    @Column(name = "val_ref_adic", length = 1000)
    private String valRefAdic;
    @Column(name = "req_operador")
    private Boolean reqOperador;
    @Column(name = "operador", length = 1000)
    private String operador;
    @Column(name = "sup_vacios")
    private Boolean supVacios;
    @Column(name = "reproceso")
    private Boolean reproceso;
    @JoinColumn(name = "formato", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FormatoBanca formato;

    public BancaDiccionario() {
    }

    public BancaDiccionario(Long id) {
        this.id = id;
    }

    public BancaDiccionario(Long id, String atributo, String tipo) {
        this.id = id;
        this.atributo = atributo;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFormatoFecHora() {
        return formatoFecHora;
    }

    public void setFormatoFecHora(String formatoFecHora) {
        this.formatoFecHora = formatoFecHora;
    }

    public Integer getMaxContenido() {
        return maxContenido;
    }

    public void setMaxContenido(Integer maxContenido) {
        this.maxContenido = maxContenido;
    }

    public Integer getLongitud() {
        return longitud;
    }

    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    public Integer getPosIniLinea() {
        return posIniLinea;
    }

    public void setPosIniLinea(Integer posIniLinea) {
        this.posIniLinea = posIniLinea;
    }

    public Integer getPosFinLinea() {
        return posFinLinea;
    }

    public void setPosFinLinea(Integer posFinLinea) {
        this.posFinLinea = posFinLinea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public Boolean getRequerido() {
        return requerido;
    }

    public void setRequerido(Boolean requerido) {
        this.requerido = requerido;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getReqValRef() {
        return reqValRef;
    }

    public void setReqValRef(Boolean reqValRef) {
        this.reqValRef = reqValRef;
    }

    public String getValorRef() {
        return valorRef;
    }

    public void setValorRef(String valorRef) {
        this.valorRef = valorRef;
    }

    public Boolean getReqValRefAdic() {
        return reqValRefAdic;
    }

    public void setReqValRefAdic(Boolean reqValRefAdic) {
        this.reqValRefAdic = reqValRefAdic;
    }

    public String getValRefAdic() {
        return valRefAdic;
    }

    public void setValRefAdic(String valRefAdic) {
        this.valRefAdic = valRefAdic;
    }

    public Boolean getReqOperador() {
        return reqOperador;
    }

    public void setReqOperador(Boolean reqOperador) {
        this.reqOperador = reqOperador;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Boolean getSupVacios() {
        return supVacios;
    }

    public void setSupVacios(Boolean supVacios) {
        this.supVacios = supVacios;
    }

    public Boolean getReproceso() {
        return reproceso;
    }

    public void setReproceso(Boolean reproceso) {
        this.reproceso = reproceso;
    }

    public FormatoBanca getFormato() {
        return formato;
    }

    public void setFormato(FormatoBanca formato) {
        this.formato = formato;
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
        if (!(object instanceof BancaDiccionario)) {
            return false;
        }
        BancaDiccionario other = (BancaDiccionario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.bancos.BancaDiccionario[ id=" + id + " ]";
    }

}
