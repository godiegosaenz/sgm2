
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatClaveReordenada;
import com.origami.sgm.entities.AclUser;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.OrdenDet;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.avaluos.SectorValorizacion;
import com.origami.sgm.entities.models.ModelMap;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_predio", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"num_predio"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredio.findAll", query = "SELECT c FROM CatPredio c")
    ,
    @NamedQuery(name = "CatPredio.findAllByManzana", query = "SELECT p FROM CatPredio p WHERE p.parroquia = :parroquia AND p.zona = :zona AND p.sector = :sector AND p.mz = :mz ORDER BY p.parroquia, p.zona, p.sector, p.mz, p.solar, p.bloque, p.piso, p.unidad")
    ,
    @NamedQuery(name = "CatPredio.findById", query = "SELECT c FROM CatPredio c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredio.findBySector", query = "SELECT c FROM CatPredio c WHERE c.sector = :sector")
    ,
    @NamedQuery(name = "CatPredio.findByMz", query = "SELECT c FROM CatPredio c WHERE c.mz = :mz")
    ,
    @NamedQuery(name = "CatPredio.findByMzdiv", query = "SELECT c FROM CatPredio c WHERE c.mzdiv = :mzdiv")
    ,
    @NamedQuery(name = "CatPredio.findBySolar", query = "SELECT c FROM CatPredio c WHERE c.solar = :solar")
    ,
    @NamedQuery(name = "CatPredio.findByDiv1", query = "SELECT c FROM CatPredio c WHERE c.div1 = :div1")
    ,
    @NamedQuery(name = "CatPredio.findByDiv2", query = "SELECT c FROM CatPredio c WHERE c.div2 = :div2")
    ,
    @NamedQuery(name = "CatPredio.findByDiv3", query = "SELECT c FROM CatPredio c WHERE c.div3 = :div3")
    ,
    @NamedQuery(name = "CatPredio.findByDiv4", query = "SELECT c FROM CatPredio c WHERE c.div4 = :div4")
    ,
    @NamedQuery(name = "CatPredio.findByDiv5", query = "SELECT c FROM CatPredio c WHERE c.div5 = :div5")
    ,
    @NamedQuery(name = "CatPredio.findByDiv6", query = "SELECT c FROM CatPredio c WHERE c.div6 = :div6")
    ,
    @NamedQuery(name = "CatPredio.findByDiv7", query = "SELECT c FROM CatPredio c WHERE c.div7 = :div7")
    ,
    @NamedQuery(name = "CatPredio.findByDiv8", query = "SELECT c FROM CatPredio c WHERE c.div8 = :div8")
    ,
    @NamedQuery(name = "CatPredio.findByDiv9", query = "SELECT c FROM CatPredio c WHERE c.div9 = :div9")
    ,
    @NamedQuery(name = "CatPredio.findByPhv", query = "SELECT c FROM CatPredio c WHERE c.phv = :phv")
    ,
    @NamedQuery(name = "CatPredio.findByPhh", query = "SELECT c FROM CatPredio c WHERE c.phh = :phh")
    ,
    @NamedQuery(name = "CatPredio.findByNombreUrb", query = "SELECT c FROM CatPredio c WHERE c.nombreUrb = :nombreUrb")
    ,
    @NamedQuery(name = "CatPredio.findByUrbSec", query = "SELECT c FROM CatPredio c WHERE c.urbSec = :urbSec")
    ,
    @NamedQuery(name = "CatPredio.findByNumPredio", query = "SELECT c FROM CatPredio c WHERE c.numPredio = :numPredio")
    ,
    @NamedQuery(name = "CatPredio.findBySoportaHipoteca", query = "SELECT c FROM CatPredio c WHERE c.soportaHipoteca = :soportaHipoteca")
    ,
    @NamedQuery(name = "CatPredio.findByUrbMz", query = "SELECT c FROM CatPredio c WHERE c.urbMz = :urbMz")
    ,
    @NamedQuery(name = "CatPredio.findByNumeroFicha", query = "SELECT c FROM CatPredio c WHERE c.numeroFicha = :numeroFicha")
    ,
    @NamedQuery(name = "CatPredio.findByInstCreacion", query = "SELECT c FROM CatPredio c WHERE c.instCreacion = :instCreacion")
    ,
    @NamedQuery(name = "CatPredio.findByCdla", query = "SELECT c FROM CatPredio c WHERE c.cdla = :cdla")
    ,
    @NamedQuery(name = "CatPredio.findByUrbSolarnew", query = "SELECT c FROM CatPredio c WHERE c.urbSolarnew = :urbSolarnew")
    ,
    @NamedQuery(name = "CatPredio.findByUrbSecnew", query = "SELECT c FROM CatPredio c WHERE c.urbSecnew = :urbSecnew")
    ,
    @NamedQuery(name = "CatPredio.findByNomCompPago", query = "SELECT c FROM CatPredio c WHERE c.nomCompPago = :nomCompPago")
    ,
    @NamedQuery(name = "CatPredio.findByPropiedadHorizontal", query = "SELECT c FROM CatPredio c WHERE c.propiedadHorizontal = :propiedadHorizontal")
    ,
    @NamedQuery(name = "CatPredio.findByPredioRaiz", query = "SELECT c FROM CatPredio c WHERE c.predioRaiz = :predioRaiz")
    ,
    @NamedQuery(name = "CatPredio.findByEstado", query = "SELECT c FROM CatPredio c WHERE c.estado = :estado")
    ,
    @NamedQuery(name = "CatPredio.findByAvaluoSolar", query = "SELECT c FROM CatPredio c WHERE c.avaluoSolar = :avaluoSolar")
    ,
    @NamedQuery(name = "CatPredio.findByAvaluoConstruccion", query = "SELECT c FROM CatPredio c WHERE c.avaluoConstruccion = :avaluoConstruccion")
    ,
    @NamedQuery(name = "CatPredio.findByAvaluoMunicipal", query = "SELECT c FROM CatPredio c WHERE c.avaluoMunicipal = :avaluoMunicipal")})
@SequenceGenerator(name = "cat_predio_id_seq", sequenceName = SchemasConfig.APP1 + ".cat_predio_id_seq", allocationSize = 1)
public class CatPredio extends ModelMap implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_predio_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sector", nullable = false)
    @Expose
    private Short sector = new Short("0");
    @Column(name = "provincia")
    @Expose
    private Short provincia = 13;
    @Column(name = "canton")
    @Expose
    private Short canton = 22;
    @Column(name = "parroquia")
    @Expose
    private Short parroquia = new Short("0");
    @Column(name = "mz")
    @Expose
    private Short mz = new Short("0");
    @Column(name = "mzdiv")
    private Short mzdiv;
    @Column(name = "solar")
    private Short solar = new Short("0");
    @Column(name = "div1")
    private Short div1;
    @Column(name = "div2")
    private Short div2;
    @Column(name = "div3")
    private Short div3;
    @Column(name = "div4")
    private Short div4;
    @Column(name = "div5")
    private Short div5;
    @Column(name = "div6")
    private Short div6;
    @Column(name = "div7")
    private Short div7;
    @Column(name = "div8")
    private Short div8;
    @Column(name = "div9")
    private Short div9;
    @Column(name = "phv")
    private Short phv;
    @Column(name = "phh")
    private Short phh;
    @Column(name = "cdla")
    private Short cdla;
    @Size(max = 50)
    @Column(name = "lind_superior", length = 50)
    @Expose
    private String lindSuperior;
    @Size(max = 50)
    @Column(name = "lind_inferior", length = 50)
    @Expose
    private String lindInferior;

    @Basic(optional = false)
    @NotNull
    @Column(name = "zona", nullable = false)
    @Expose
    private Short zona = new Short("0");
    @Size(max = 80)
    @Column(name = "nombre_urb", length = 80)
    @Expose
    private String nombreUrb;
    @Column(name = "urb_sec")
    @Expose
    private Short urbSec;
    @Column(name = "num_hogares")
    @Expose
    private Short numHogares;
    @Column(name = "num_espacios_banios")
    @Expose
    private Short numEspaciosBanios;
    @Column(name = "num_celulares")
    @Expose
    private Short numCelulares;
    @Column(name = "num_habitaciones")
    @Expose
    private Short numHabitaciones;
    @Column(name = "num_dormitorios")
    @Expose
    private Short numDormitorios;
    @Column(name = "num_predio")
    @Expose
    private BigInteger numPredio;
    /*@Column(name = "subsector")
    private BigInteger subsector;*/
    @Size(max = 100)
    @Column(name = "clave_cat", length = 100)
    @Expose
    private String claveCat;
    @Column(name = "soporta_hipoteca")
    @Expose
    private Boolean soportaHipoteca;
    @Column(name = "esta_dibujado")
    @Expose
    private Boolean estaDibujado;
    @Size(max = 20)
    @Column(name = "urb_mz", length = 20)
    @Expose
    private String urbMz;
    @Column(name = "numero_ficha")
    @Expose
    private BigInteger numeroFicha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "inst_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Expose(serialize = false, deserialize = false)
    private Date instCreacion;
    @Size(max = 100)
    @Column(name = "urb_solarnew", length = 100)
    @Expose
    private String urbSolarnew;
    @Size(max = 50)
    @Column(name = "urb_secnew", length = 50)
    @Expose
    private String urbSecnew;
    @Column(name = "observaciones", length = 5000)
    @Expose
    private String observaciones;
    @Size(max = 150)
    @Column(name = "predial97", length = 150)
    private String predial97;
    @Size(max = 150)
    @Column(name = "predialant", length = 150)
    @Expose
    private String predialant;

    @Size(max = 150)
    @Column(name = "calle_av", length = 150)
    @Expose
    private String calleAv;

    @Size(max = 150)
    @Column(name = "calle", length = 150)
    @Expose
    private String calle;

    @Size(max = 150)
    @Column(name = "calle_s", length = 150)
    @Expose
    private String calleS;

    @Size(max = 150)
    @Column(name = "numero_vivienda", length = 150)
    @Expose
    private String numeroVivienda;

    @Column(name = "alicuota_util", precision = 12, scale = 4)
    @Expose
    private BigDecimal alicuotaUtil;

    @Column(name = "alicuota_const", precision = 12, scale = 4)
    @Expose
    private BigDecimal alicuotaConst;

    @Size(max = 150)
    @Column(name = "num_departamento", length = 150)
    @Expose
    private String numDepartamento;
    @Size(max = 150)
    @Column(name = "nombre_edificio", length = 150)
    @Expose
    private String nombreEdificio;
    @Size(max = 200)
    @Column(name = "nom_comp_pago", length = 200)
    @Expose
    private String nomCompPago;
    @Column(name = "propiedad_horizontal")
    @Expose
    private Boolean propiedadHorizontal;
    @Column(name = "predio_raiz")
    @Expose
    private BigInteger predioRaiz;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    @Expose
    private String estado;
    @Column(name = "tipo_predio", length = 10)
    @Expose
    private String tipoPredio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "avaluo_solar", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_construccion", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_municipal", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoMunicipal;
    @Column(name = "avaluo_cultivos", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoCultivos;
    @Column(name = "uso_ph")
    @Expose
    private String usoPh;
    @Column(name = "division_urb", length = 200)
    @Expose
    private String divisionUrb;
    @Column(name = "num_pisos")
    @Expose
    private BigInteger numPisos;
    @Column(name = "coordx", precision = 14, scale = 4)
    @Expose
    private BigDecimal coordx;
    @Column(name = "coordy", precision = 14, scale = 4)
    @Expose
    private BigDecimal coordy;
    @Column(name = "fec_mod")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose(serialize = true, deserialize = false)
    private Date fecMod;
    @Size(max = 50)
    @Column(name = "usr_mod", length = 50)
    private String usrMod;
    @Column(name = "revisado")
    @Expose
    private Boolean revisado;
    @Column(name = "procesados")
    @Expose
    private Boolean procesados;
    @Column(name = "habitantes")
    @Expose
    private Integer habitantes;
    @Column(name = "nuevo")
    @Expose
    private Boolean nuevo;
    @Column(name = "tipo_vivienda_horizontal")
    @Expose
    private Boolean tipoViviendaHorizontal;
    @Column(name = "ocupacion_viv_horizontal")
    @Expose
    private Boolean ocupacionViviendaHorizontal;
    @OneToOne(mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose
    private CatPredioS4 catPredioS4;
    @OneToOne(mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose
    private CatPredioS6 catPredioS6;
    @OneToOne(mappedBy = "predio", fetch = FetchType.LAZY)
    private RegFicha regFicha;
    @JoinColumn(name = "tipo_conjunto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatTipoConjunto tipoConjunto;
    @JoinColumn(name = "responsable_actualizador_predial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatEnte responsableActualizadorPredial;
    @JoinColumn(name = "responsable_fiscalizador_predial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatEnte responsableFiscalizadorPredial;
    @JoinColumn(name = "ente_horizontal", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatEnte enteHorizontal;
    @JoinColumn(name = "tenencia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatTenenciaItem tenencia;
    @JoinColumn(name = "propiedad", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatPropiedadItem propiedad;

    @JoinColumn(name = "ciudadela", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatCiudadela ciudadela;
    @JoinColumn(name = "usuario_creador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioCreador;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose
    private CatPredioS12 catPredioS12;
    @Column(name = "area_solar", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaSolar;
    @Column(name = "area_obras", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaObras;
    @Column(name = "area_cultivos", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaCultivos;
    @Column(name = "area_declarada_const", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaDeclaradaConst;
    @Column(name = "amri", precision = 12, scale = 2)
    @Expose
    private BigDecimal amri;
    @Column(name = "zona_pu")
    @Expose
    private String zonaPu;
    @JoinColumn(name = "forma_solar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem formaSolar;
    @Size(max = 6)
    @Column(name = "cod_categoria", length = 6)
    @Expose
    private String codCategoria;
    @JoinColumn(name = "topografia_solar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem topografiaSolar;

    @JoinColumn(name = "tipo_via", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipovia;

    @JoinColumn(name = "otro_tipo_via", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem otroTipovia;
    @JoinColumn(name = "unidad_medida", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem unidadMedida;
    @JoinColumn(name = "clasif_horizontal", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem clasificacionViviendaHorizontal;
    @JoinColumn(name = "tipo_suelo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipoSuelo;
    @JoinColumn(name = "tenencia_vivienda", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tenenciaVivienda;
    @JoinColumn(name = "uso_solar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose(serialize = true, deserialize = true)
    private CtlgItem usoSolar;
    @JoinColumn(name = "constructividad", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem constructividad;

    @JoinColumn(name = "subsector", referencedColumnName = "sector")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private SectorValorizacion subsector;
    @Column(name = "lote")
    @Expose
    private Short lote = new Short("0");
    @Basic(optional = false)
    @NotNull
    @Column(name = "bloque", nullable = false)
    @Expose
    private Short bloque = new Short("0");
    @Basic(optional = false)
    @NotNull
    @Column(name = "piso", nullable = false)
    @Expose
    private Short piso = new Short("0");
    @Basic(optional = false)
    @NotNull
    @Column(name = "unidad", nullable = false)
    @Expose
    private Short unidad = new Short("0");
    @Column(name = "area_const_ph", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaConstPh;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<OrdenDet> ordenDetEspecialCollection;
    @Column(name = "requiere_perfeccionamiento")
    @Expose
    private Boolean requierePerfeccionamiento = false;
    @Column(name = "anios_sin_perfeccionamiento")
    @Expose
    private Integer aniosSinPerfeccionamiento;
    @Column(name = "anios_posesion")
    @Expose
    private Integer aniosPosesion;
    @Column(name = "nombre_pueblo_etnia")
    @Expose
    private String nombrePuebloEtnia;
    @Column(name = "afectacion_por_danios")
    @Expose
    private Boolean afectaccionPorDanios;
    @Column(name = "observacion_por_danios")
    @Expose
    private Boolean observacionPorDanios;
    @JoinColumn(name = "clasificacion_suelo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem clasificacionSuelo;

    @Transient
    private Boolean crear;
    @Transient
    private CatParroquia catParroquia;
    @Transient
    @Expose
    private CatEscritura escrituraLinderos;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "predio")
    @Expose
    private CatClaveReordenada claveReordenada;
    @JoinColumn(name = "tipo_poseedor", referencedColumnName = "id")
    @Expose
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem tipoPoseedor;
    @JoinColumn(name = "informante", referencedColumnName = "id")
    @Expose
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte informante;
    @Expose
    @Column(name = "ficha_madre")
    private Boolean fichaMadre;

    @Size(max = 500)
    @Column(name = "admin_nombres_apellidos", length = 500)
    @Expose
    private String adminFullName;

    @Size(max = 20)
    @Column(name = "admin_cedula", length = 20)
    @Expose
    private String adminCedula;
    @Size(max = 50)
    @Column(name = "admin_telefono", length = 50)
    @Expose
    private String adminTelefono;
    @Size(max = 50)
    @Column(name = "admin_celular", length = 50)
    @Expose
    private String adminCelular;
    @Column(name = "cant_alicuotas")
    @Expose
    private Integer cantAlicuotas;

    @Expose
    @Column(name = "tiene_escritura")
    private Boolean tieneEscritura;

    @Transient
    private CatEscritura escritura;
    // version de ibarra
    @JoinColumn(name = "clasificacion_vivienda", referencedColumnName = "id")
    @Expose
    @ManyToOne
    private CtlgItem clasificacionVivienda;
    @JoinColumn(name = "tipo_vivienda", referencedColumnName = "id")
    @Expose
    @ManyToOne
    private CtlgItem tipoVivienda;
    @JoinColumn(name = "condicion_vivienda", referencedColumnName = "id")
    @Expose
    @ManyToOne
    private CtlgItem condicionVivienda;
    @Column(name = "avaluo_Obra_Complement", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoObraComplement;
    @Column(name = "avaluo_plussolar", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoPlussolar;
    @Column(name = "avaluo_plusconstruccion", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoPlusconstruccion;
    @Column(name = "avaluo_plusmunicipal", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoPlusmunicipal;
    @Column(name = "avaluo_pluscultivos", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoPluscultivos;
    @Column(name = "avaluo_Plu_Obra_Complement", precision = 15, scale = 4)
    @Expose
    private BigDecimal avaluoPluObraComplement;

    @Column(name = "base_imponible", precision = 15, scale = 4)
    @Expose
    private BigDecimal baseImponible;
    @JoinColumn(name = "uso_via", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem usoVia;
    @JoinColumn(name = "forma_adquisicion", referencedColumnName = "id")
    @Expose
    @ManyToOne
    private CtlgItem formaAdquisicion;
    @JoinColumn(name = "viv_cencal_acabado_piso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem vivCencalAcabadoPiso;
    @JoinColumn(name = "viv_cencal_estado_acabado_piso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem vivCencalEstadoAcabadoPiso;
    @Column(name = "viv_cencal_posee_telf_convencional")
    @Expose
    private Boolean vivCencalPoseeTelfConvencional;
    @Column(name = "viv_cencal_serv_internet")
    @Expose
    private Boolean vivCencalServInternet;
    @Column(name = "prop_anterior_predio")
    @Expose
    private String propAnteriorPredio;
    @Column(name = "cedula_prop_anterior")
    @Expose
    private String cedulaPropAnterior;
    @Column(name = "area_terreno_alicuota", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaTerrenoAlicuota;
    @Column(name = "area_aumento_cons", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaAumentoCons;
    @Column(name = "area_total_cons", precision = 12, scale = 2)
    @Expose
    private BigDecimal areaTotalCons;
    @Column(name = "alicuota_terreno", precision = 12, scale = 2)
    @Expose
    private BigDecimal alicuotaTerreno;
    @Column(name = "ci_ruc_informante")
    @Expose
    private String ciRucInformante;
    @Column(name = "nombre_informante")
    @Expose
    private String nombreInformante;
    @Column(name = "apellidos_informante")
    @Expose
    private String apellidosInformante;

    @Expose
    @Column(name = "ciu_actualizador")
    private String ciuActualizador;
    @Expose
    @Column(name = "ciu_fiscalizador")
    private String ciuFiscalizador;
    @Expose
    @Column(name = "ciu_nombre_actualizador")
    private String ciuNombresActualizador;
    @Expose
    @Column(name = "ciu_nombre_fiscalizador")
    private String ciuNombresFiscalizador;
    @Expose
    @Column(name = "ciu_informante")
    private String ciuInformante;
    @Expose
    @Column(name = "ciu_nombre_informante")
    private String ciuNombresInformante;
    @Column(name = "ciu_horizontal")
    @Expose
    private String ciuHorizontal;
    @Column(name = "ciu_nombre_Horizontal")
    @Expose
    private String ciuNombresHorizontal;
    @Column(name = "predialant_ant", length = 100)
    @Expose
    private String predialantAnt;

    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @Where(clause = "estado_prestamo = 1")
    @Expose
    private List<FinanPrestamoPredio> finanPrestamoPredioCollection;
    @Where(clause = "estado = 'A'")
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose
    private Collection<CatPredioObraInterna> catPredioObraInternaCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose
    @Where(clause = "estado = 'A'")
    private List<CatPredioClasificRural> catPredioClasificRuralCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "predio")
    private List<CatPredioAlicuotaComponente> alicuotaComponentes;
    @OneToMany(mappedBy = "predioColindante", fetch = FetchType.LAZY)
    private List<CatPredioLinderos> catPredioLinderosCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "predio", fetch = FetchType.LAZY)
    @Where(clause = "estado = 'A'")
    @Expose
    private List<CatPredioLinderos> predioCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "predio", fetch = FetchType.LAZY)
    @Where(clause = "estado = 'A'")
    @Expose
    private List<CatPredioCultivo> catPredioCultivoCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<FnSolicitudExoneracionPredios> prediosCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<CatCertificadoAvaluo> catCertificadoAvaluoCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @OrderBy(clause = "anio DESC")
    private Collection<RenLiquidacion> renLiquidacionCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private List<FnSolicitudExoneracion> fnSolicitudExoneracions;
    @OneToMany(mappedBy = "prediosAsociados")
    private List<RenDetallePlusvalia> renDetallePlusvalias;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private List<CmMultas> cmMultass;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<ProcesoFusionPredios> procesoFusionPrediosCollection;
    @OneToMany(mappedBy = "idPredio", fetch = FetchType.LAZY)
    private Collection<CatSolicitudNormaConstruccion> catSolicitudNormaConstruccionCollection;
    @OneToMany(mappedBy = "idPredio", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<ProcesoReporte> procesoReporteCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @Filter(name = "activosString")
    @Expose
    private Collection<CatEscritura> catEscrituraCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection;
    @OneToMany(mappedBy = "idPredio", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection;
    @Where(clause = "estado = 'A'")
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    @Expose(serialize = true, deserialize = true)
    private Collection<CatPredioPropietario> catPredioPropietarioCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "predio", fetch = FetchType.LAZY)
    @Where(clause = "estado='A'")
    @OrderBy(clause = "no_edificacion ASC")
    @Expose
    private Collection<CatPredioEdificacion> catPredioEdificacionCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<HistoricoTramiteDet> historicoTramiteDetCollection;
    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private Collection<FnSolicitudCondonacion> solicitudCondonacionCollection;
//    @Embedded
//    @Expose
//    protected CatPredioExtras extras;
    @OneToMany(mappedBy = "predio")
    private List<CatPredioAvalHistorico> catPredioAvalHistoricos;
    @Expose
    @Transient
    private List<String> fotos;
    @Expose
    @Transient
    private Boolean frentista = Boolean.FALSE;
    @Column(name = "cambio_nombre")
    @Expose
    private Boolean cambioNombreTitulo = Boolean.FALSE;
    @Size(max = 4000)
    @Column(name = "nombre_cambiado", length = 4000)
    @Expose
    private String nombreCambiado;
    //columna para obtener los datossa de los avuos de lo pedios es el valor base multiplicado por la sumatoria de llos coefic ientes de construccion
    @Column(name = "costo_directo")
    @Expose
    private BigDecimal valorAfectacionCoeficenteTerreno;
    @Column(name = "es_avaluo_especial")
    @Expose
    private Boolean isAvalauoEspecial;
    @Column(name = "es_avaluo_verificado")
    @Expose
    private Boolean esAvaluoVerificado;

    @OneToMany(mappedBy = "predio", fetch = FetchType.LAZY)
    private List<SvSolicitudServiciosPredios> solicitudServicioPrediosist;

    @Column(name = "es_tributario")
    @Expose
    private Boolean esTributario = Boolean.FALSE;

    @Column(name = "esta_exonerado")
    @Expose
    private Boolean estaExonerado = Boolean.FALSE;

    @Column(name = "clave_anterior_verificada")
    @Expose
    private Boolean claveAnteriorVerificada = Boolean.FALSE;

    public Collection<FnSolicitudExoneracionPredios> getPrediosCollection() {
        return prediosCollection;
    }

    public void setPrediosCollection(Collection<FnSolicitudExoneracionPredios> prediosCollection) {
        this.prediosCollection = prediosCollection;
    }

    public String getUsoPh() {
        return usoPh;
    }

    public void setUsoPh(String usoPh) {
        this.usoPh = usoPh;
    }

    public String getDivisionUrb() {
        return divisionUrb;
    }

    public void setDivisionUrb(String divisionUrb) {
        this.divisionUrb = divisionUrb;
    }

    public BigInteger getNumPisos() {
        return numPisos;
    }

    public void setNumPisos(BigInteger numPisos) {
        this.numPisos = numPisos;
    }

    public BigDecimal getCoordx() {
        return coordx;
    }

    public void setCoordx(BigDecimal coordx) {
        this.coordx = coordx;
    }

    public BigDecimal getCoordy() {
        return coordy;
    }

    public void setCoordy(BigDecimal coordy) {
        this.coordy = coordy;
    }

    public Date getFecMod() {
        return fecMod;
    }

    public void setFecMod(Date fecMod) {
        this.fecMod = fecMod;
    }

    public String getUsrMod() {
        return usrMod;
    }

    public void setUsrMod(String usrMod) {
        this.usrMod = usrMod;
    }

    public Boolean getRevisado() {
        return revisado;
    }

    public void setRevisado(Boolean revisado) {
        this.revisado = revisado;
    }

    public Boolean getProcesados() {
        return procesados;
    }

    public void setProcesados(Boolean procesados) {
        this.procesados = procesados;
    }

    public Integer getHabitantes() {
        return habitantes;
    }

    public void setHabitantes(Integer habitantes) {
        this.habitantes = habitantes;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    @Transient
    private String propietarios;

    @Transient
    private Boolean tienePermiso = false;

    public CatPredio() {

    }

    public CatPredio(Long id) {
        this.id = id;
    }

    public CatPredio(Long id, Short sector, Short mz, Short mzdiv, Short solar, Short div1, Short div2, Short div3, Short div4, Short div5, Short div6, Short div7, Short div8, Short div9, Short phv, Short phh, Date instCreacion, Short cdla) {
        this.id = id;
        this.sector = sector;
        this.mz = mz;
        this.mzdiv = mzdiv;
        this.solar = solar;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
        this.div4 = div4;
        this.div5 = div5;
        this.div6 = div6;
        this.div7 = div7;
        this.div8 = div8;
        this.div9 = div9;
        this.phv = phv;
        this.phh = phh;
        this.instCreacion = instCreacion;
        this.cdla = cdla;
    }

    public String getCodigoPredial() {
        return this.claveCat;
    }

    public String getCodigoPredialCompleto() {
        return this.claveCat;
    }

    public String getCodigoPredialCompletoFormatoG() {
        return this.claveCat;
    }

    public String getCodigoPredialCompletoFormatoP() {
        return this.claveCat;
    }

    public String getCodigoPredialCompletoSinFormato() {
        return this.claveCat;
    }

    public void setCodigoPredialCompleto(String codigoPredial) {

    }

    public String getNombrePropietarios() {
        String nombres = "";
        StringBuilder sb = new StringBuilder();

        if (Objects.equals(cambioNombreTitulo, Boolean.TRUE)) {
            if (nombreCambiado != null) {
                if (!nombreCambiado.equals("")) {
                    nombres = nombreCambiado;
                    return nombres.toUpperCase();
                }
            }
        }

        if (this.catPredioPropietarioCollection != null && !this.catPredioPropietarioCollection.isEmpty()) {
            for (CatPredioPropietario cpp : catPredioPropietarioCollection) {

                if (cpp.getEnte() != null) {
                    if (cpp.getEnte().getEsPersona()) {
                        nombres = (cpp.getEnte().getApellidos() == null ? "" : cpp.getEnte().getApellidos())
                                + " " + (cpp.getEnte().getNombres() == null ? "" : cpp.getEnte().getNombres());
                    } else {
                        nombres = (cpp.getEnte().getRazonSocial() == null ? "" : cpp.getEnte().getRazonSocial())
                                + " " + (cpp.getEnte().getNombreComercial() == null ? "" : cpp.getEnte().getNombreComercial());
                    }
                    sb.append(nombres).append(" - ");
                }

            }
        }
        if (sb.length() >= 3) {
            sb.delete(sb.length() - 3, sb.length() - 1);
        }
        return sb.toString().toUpperCase();
    }

    public Collection<FnSolicitudCondonacion> getSolicitudCondonacionCollection() {
        return solicitudCondonacionCollection;
    }

    public void setSolicitudCondonacionCollection(Collection<FnSolicitudCondonacion> solicitudCondonacionCollection) {
        this.solicitudCondonacionCollection = solicitudCondonacionCollection;
    }

    public String getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(String propietarios) {
        this.propietarios = propietarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumDepartamento() {
        return numDepartamento;
    }

    public void setNumDepartamento(String numDepartamento) {
        this.numDepartamento = numDepartamento;
    }

    public String getNombreEdificio() {
        return nombreEdificio;
    }

    public void setNombreEdificio(String nombreEdificio) {
        this.nombreEdificio = nombreEdificio;
    }

    public String getNombreUrb() {
        return nombreUrb;
    }

    public void setNombreUrb(String nombreUrb) {
        this.nombreUrb = nombreUrb;
    }

    public Short getUrbSec() {
        return urbSec;
    }

    public void setUrbSec(Short urbSec) {
        this.urbSec = urbSec;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public SectorValorizacion getSubsector() {
        return subsector;
    }

    public void setSubsector(SectorValorizacion subsector) {
        this.subsector = subsector;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
    }

    public Boolean getSoportaHipoteca() {
        return soportaHipoteca;
    }

    public void setSoportaHipoteca(Boolean soportaHipoteca) {
        this.soportaHipoteca = soportaHipoteca;
    }

    public String getUrbMz() {
        return urbMz;
    }

    public void setUrbMz(String urbMz) {
        this.urbMz = urbMz;
    }

    public BigInteger getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(BigInteger numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Date getInstCreacion() {
        return instCreacion;
    }

    public void setInstCreacion(Date instCreacion) {
        this.instCreacion = instCreacion;
    }

    public String getUrbSolarnew() {
        return urbSolarnew;
    }

    public void setUrbSolarnew(String urbSolarnew) {
        this.urbSolarnew = urbSolarnew;
    }

    public String getUrbSecnew() {
        return urbSecnew;
    }

    public void setUrbSecnew(String urbSecnew) {
        this.urbSecnew = urbSecnew;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPredial97() {
        return predial97;
    }

    public void setPredial97(String predial97) {
        this.predial97 = predial97;
    }

    public String getCalleAv() {
        return calleAv;
    }

    public void setCalleAv(String calleAv) {
        this.calleAv = calleAv;
    }

    public String getPredialant() {
        return predialant;
    }

    public void setPredialant(String predialant) {
        this.predialant = predialant;
    }

    public String getNomCompPago() {
        return nomCompPago;
    }

    public void setNomCompPago(String nomCompPago) {
        this.nomCompPago = nomCompPago;
    }

    public Boolean getPropiedadHorizontal() {
        return propiedadHorizontal;
    }

    public void setPropiedadHorizontal(Boolean propiedadHorizontal) {
        this.propiedadHorizontal = propiedadHorizontal;
    }

    public BigInteger getPredioRaiz() {
        return predioRaiz;
    }

    public void setPredioRaiz(BigInteger predioRaiz) {
        this.predioRaiz = predioRaiz;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getAvaluoCultivos() {
        return avaluoCultivos;
    }

    public void setAvaluoCultivos(BigDecimal avaluoCultivos) {
        this.avaluoCultivos = avaluoCultivos;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProcesoFusionPredios> getProcesoFusionPrediosCollection() {
        return procesoFusionPrediosCollection;
    }

    public void setProcesoFusionPrediosCollection(Collection<ProcesoFusionPredios> procesoFusionPrediosCollection) {
        this.procesoFusionPrediosCollection = procesoFusionPrediosCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatSolicitudNormaConstruccion> getCatSolicitudNormaConstruccionCollection() {
        return catSolicitudNormaConstruccionCollection;
    }

    public void setCatSolicitudNormaConstruccionCollection(Collection<CatSolicitudNormaConstruccion> catSolicitudNormaConstruccionCollection) {
        this.catSolicitudNormaConstruccionCollection = catSolicitudNormaConstruccionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection() {
        return pePermisoCollection;
    }

    public void setPePermisoCollection(Collection<PePermiso> pePermisoCollection) {
        this.pePermisoCollection = pePermisoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProcesoReporte> getProcesoReporteCollection() {
        return procesoReporteCollection;
    }

    public void setProcesoReporteCollection(Collection<ProcesoReporte> procesoReporteCollection) {
        this.procesoReporteCollection = procesoReporteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEscritura> getCatEscrituraCollection() {
        return catEscrituraCollection;
    }

    public void setCatEscrituraCollection(Collection<CatEscritura> catEscrituraCollection) {
        this.catEscrituraCollection = catEscrituraCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection() {
        return pePermisosAdicionalesCollection;
    }

    public void setPePermisosAdicionalesCollection(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection) {
        this.pePermisosAdicionalesCollection = pePermisosAdicionalesCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection() {
        return peEstructuraEspecialCollection;
    }

    public void setPeEstructuraEspecialCollection(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection) {
        this.peEstructuraEspecialCollection = peEstructuraEspecialCollection;
    }

    public CatPredioS4 getCatPredioS4() {
        return catPredioS4;
    }

    public void setCatPredioS4(CatPredioS4 catPredioS4) {
        this.catPredioS4 = catPredioS4;
    }

    public CatPredioS6 getCatPredioS6() {
        return catPredioS6;
    }

    public void setCatPredioS6(CatPredioS6 catPredioS6) {
        this.catPredioS6 = catPredioS6;
    }

    public Collection<CatPredioPropietario> getCatPredioPropietarioCollection() {
        return catPredioPropietarioCollection;
    }

    public void setCatPredioPropietarioCollection(Collection<CatPredioPropietario> catPredioPropietarioCollection) {
        this.catPredioPropietarioCollection = catPredioPropietarioCollection;
    }

    public RegFicha getRegFicha() {
        return regFicha;
    }

    public void setRegFicha(RegFicha regFicha) {
        this.regFicha = regFicha;
    }

    public CatTipoConjunto getTipoConjunto() {
        return tipoConjunto;
    }

    public void setTipoConjunto(CatTipoConjunto tipoConjunto) {
        this.tipoConjunto = tipoConjunto;
    }

    public CatTenenciaItem getTenencia() {
        return tenencia;
    }

    public void setTenencia(CatTenenciaItem tenencia) {
        this.tenencia = tenencia;
    }

    public CatPropiedadItem getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(CatPropiedadItem propiedad) {
        this.propiedad = propiedad;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public AclUser getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(AclUser usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public CatPredioS12 getCatPredioS12() {
        return catPredioS12;
    }

    public void setCatPredioS12(CatPredioS12 catPredioS12) {
        this.catPredioS12 = catPredioS12;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioEdificacion> getCatPredioEdificacionCollection() {
        return catPredioEdificacionCollection;
    }

    public void setCatPredioEdificacionCollection(Collection<CatPredioEdificacion> catPredioEdificacionCollection) {
        this.catPredioEdificacionCollection = catPredioEdificacionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection() {
        return peInspeccionFinalCollection;
    }

    public void setPeInspeccionFinalCollection(Collection<PeInspeccionFinal> peInspeccionFinalCollection) {
        this.peInspeccionFinalCollection = peInspeccionFinalCollection;
    }

    public Collection<HistoricoTramiteDet> getHistoricoTramiteDetCollection() {
        return historicoTramiteDetCollection;
    }

    public void setHistoricoTramiteDetCollection(Collection<HistoricoTramiteDet> historicoTramiteDetCollection) {
        this.historicoTramiteDetCollection = historicoTramiteDetCollection;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
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
        if (!(object instanceof CatPredio)) {
            return false;
        }
        CatPredio other = (CatPredio) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaObras() {
        return areaObras;
    }

    public void setAreaObras(BigDecimal areaObras) {
        this.areaObras = areaObras;
    }

    public BigDecimal getAreaCultivos() {
        return areaCultivos;
    }

    public void setAreaCultivos(BigDecimal areaCultivos) {
        this.areaCultivos = areaCultivos;
    }

    public BigDecimal getAreaDeclaradaConst() {
        return areaDeclaradaConst;
    }

    public void setAreaDeclaradaConst(BigDecimal areaDeclaradaConst) {
        this.areaDeclaradaConst = areaDeclaradaConst;
    }

    public BigDecimal getAmri() {
        return amri;
    }

    public void setAmri(BigDecimal amri) {
        this.amri = amri;
    }

    public String getZonaPu() {
        return zonaPu;
    }

    public void setZonaPu(String zonaPu) {
        this.zonaPu = zonaPu;
    }

    public CtlgItem getFormaSolar() {
        return formaSolar;
    }

    public void setFormaSolar(CtlgItem formaSolar) {
        this.formaSolar = formaSolar;
    }

    public String getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }

    public CtlgItem getTopografiaSolar() {
        return topografiaSolar;
    }

    public void setTopografiaSolar(CtlgItem topografiaSolar) {
        this.topografiaSolar = topografiaSolar;
    }

    public CtlgItem getTipovia() {
        return tipovia;
    }

    public void setTipovia(CtlgItem tipovia) {
        this.tipovia = tipovia;
    }

    public CtlgItem getTipoSuelo() {
        return tipoSuelo;
    }

    public void setTipoSuelo(CtlgItem tipoSuelo) {
        this.tipoSuelo = tipoSuelo;
    }

    public Collection<CatCertificadoAvaluo> getCatCertificadoAvaluoCollection() {
        return catCertificadoAvaluoCollection;
    }

    public void setCatCertificadoAvaluoCollection(Collection<CatCertificadoAvaluo> catCertificadoAvaluoCollection) {
        this.catCertificadoAvaluoCollection = catCertificadoAvaluoCollection;
    }

    public Boolean getTienePermiso() {
        return tienePermiso;
    }

    public void setTienePermiso(Boolean tienePermiso) {
        this.tienePermiso = tienePermiso;
    }

    public Collection<RenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<RenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
    }

    public List<FnSolicitudExoneracion> getFnSolicitudExoneracions() {
        return fnSolicitudExoneracions;
    }

    public void setFnSolicitudExoneracions(List<FnSolicitudExoneracion> fnSolicitudExoneracions) {
        this.fnSolicitudExoneracions = fnSolicitudExoneracions;
    }

    public List<RenDetallePlusvalia> getRenDetallePlusvalias() {
        return renDetallePlusvalias;
    }

    public void setRenDetallePlusvalias(List<RenDetallePlusvalia> renDetallePlusvalias) {
        this.renDetallePlusvalias = renDetallePlusvalias;
    }

    public void setCmMultass(List<CmMultas> cmMultass) {
        this.cmMultass = cmMultass;
    }

    public List<CmMultas> getCmMultass() {
        return cmMultass;
    }

    public String getCalleS() {
        return calleS;
    }

    public void setCalleS(String calleS) {
        this.calleS = calleS;
    }

    public String getNumeroVivienda() {
        return numeroVivienda;
    }

    public void setNumeroVivienda(String numeroVivienda) {
        this.numeroVivienda = numeroVivienda;
    }

    public BigDecimal getAlicuotaUtil() {
        return alicuotaUtil;
    }

    public void setAlicuotaUtil(BigDecimal alicuotaUtil) {
        this.alicuotaUtil = alicuotaUtil;
    }

    public BigDecimal getAlicuotaConst() {
        return alicuotaConst;
    }

    public void setAlicuotaConst(BigDecimal alicuotaConst) {
        this.alicuotaConst = alicuotaConst;
    }

    public CtlgItem getUsoSolar() {
        return usoSolar;
    }

    public void setUsoSolar(CtlgItem usoSolar) {
        this.usoSolar = usoSolar;
    }

    public CtlgItem getConstructividad() {
        return constructividad;
    }

    public void setConstructividad(CtlgItem constructividad) {
        this.constructividad = constructividad;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getLote() {
        return lote;
    }

    public void setLote(Short lote) {
        this.lote = lote;
    }

    public Short getBloque() {
        return bloque;
    }

    public void setBloque(Short bloque) {
        this.bloque = bloque;
    }

    public Short getPiso() {
        return piso;
    }

    public void setPiso(Short piso) {
        this.piso = piso;
    }

    public Short getUnidad() {
        return unidad;
    }

    public void setUnidad(Short unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getAreaConstPh() {
        return areaConstPh;
    }

    public void setAreaConstPh(BigDecimal areaConstPh) {
        this.areaConstPh = areaConstPh;
    }

    public Collection<OrdenDet> getOrdenDetEspecialCollection() {
        return ordenDetEspecialCollection;
    }

    public void setOrdenDetEspecialCollection(Collection<OrdenDet> ordenDetEspecialCollection) {
        this.ordenDetEspecialCollection = ordenDetEspecialCollection;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public Short getProvincia() {
        return provincia;
    }

    public void setProvincia(Short provincia) {
        this.provincia = provincia;
    }

    public Short getCanton() {
        return canton;
    }

    public void setCanton(Short canton) {
        this.canton = canton;
    }

    public Short getParroquia() {
        return parroquia;
    }

    public void setParroquia(Short parroquia) {
        this.parroquia = parroquia;
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }

    public Short getMzdiv() {
        return mzdiv;
    }

    public void setMzdiv(Short mzdiv) {
        this.mzdiv = mzdiv;
    }

    public Short getSolar() {
        return solar;
    }

    public void setSolar(Short solar) {
        this.solar = solar;
    }

    public Short getDiv1() {
        return div1;
    }

    public void setDiv1(Short div1) {
        this.div1 = div1;
    }

    public Short getDiv2() {
        return div2;
    }

    public void setDiv2(Short div2) {
        this.div2 = div2;
    }

    public Short getDiv3() {
        return div3;
    }

    public void setDiv3(Short div3) {
        this.div3 = div3;
    }

    public Short getDiv4() {
        return div4;
    }

    public void setDiv4(Short div4) {
        this.div4 = div4;
    }

    public Short getDiv5() {
        return div5;
    }

    public void setDiv5(Short div5) {
        this.div5 = div5;
    }

    public Short getDiv6() {
        return div6;
    }

    public void setDiv6(Short div6) {
        this.div6 = div6;
    }

    public Short getDiv7() {
        return div7;
    }

    public void setDiv7(Short div7) {
        this.div7 = div7;
    }

    public Short getDiv8() {
        return div8;
    }

    public void setDiv8(Short div8) {
        this.div8 = div8;
    }

    public Short getDiv9() {
        return div9;
    }

    public void setDiv9(Short div9) {
        this.div9 = div9;
    }

    public Short getPhv() {
        return phv;
    }

    public void setPhv(Short phv) {
        this.phv = phv;
    }

    public Short getPhh() {
        return phh;
    }

    public void setPhh(Short phh) {
        this.phh = phh;
    }

    public Short getCdla() {
        return cdla;
    }

    public void setCdla(Short cdla) {
        this.cdla = cdla;
    }

    public String getLindSuperior() {
        return lindSuperior;
    }

    public void setLindSuperior(String lindSuperior) {
        this.lindSuperior = lindSuperior;
    }

    public String getLindInferior() {
        return lindInferior;
    }

    public void setLindInferior(String lindInferior) {
        this.lindInferior = lindInferior;
    }

    public Boolean getCrear() {
        return crear;
    }

    public void setCrear(Boolean crear) {
        this.crear = crear;
    }

    public CtlgItem getOtroTipovia() {
        return otroTipovia;
    }

    public void setOtroTipovia(CtlgItem otroTipovia) {
        this.otroTipovia = otroTipovia;
    }

    public CatEnte getResponsableActualizadorPredial() {
        return responsableActualizadorPredial;
    }

    public void setResponsableActualizadorPredial(CatEnte responsableActualizadorPredial) {
        this.responsableActualizadorPredial = responsableActualizadorPredial;
    }

    public CatEnte getResponsableFiscalizadorPredial() {
        return responsableFiscalizadorPredial;
    }

    public void setResponsableFiscalizadorPredial(CatEnte responsableFiscalizadorPredial) {
        this.responsableFiscalizadorPredial = responsableFiscalizadorPredial;
    }

    public CtlgItem getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(CtlgItem unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Boolean getTipoViviendaHorizontal() {
        return tipoViviendaHorizontal;
    }

    public void setTipoViviendaHorizontal(Boolean tipoViviendaHorizontal) {
        this.tipoViviendaHorizontal = tipoViviendaHorizontal;
    }

    public Boolean getOcupacionViviendaHorizontal() {
        return ocupacionViviendaHorizontal;
    }

    public void setOcupacionViviendaHorizontal(Boolean ocupacionViviendaHorizontal) {
        this.ocupacionViviendaHorizontal = ocupacionViviendaHorizontal;
    }

    public CatEnte getEnteHorizontal() {
        return enteHorizontal;
    }

    public void setEnteHorizontal(CatEnte enteHorizontal) {
        this.enteHorizontal = enteHorizontal;
    }

    public CtlgItem getClasificacionViviendaHorizontal() {
        return clasificacionViviendaHorizontal;
    }

    public void setClasificacionViviendaHorizontal(CtlgItem clasificacionViviendaHorizontal) {
        this.clasificacionViviendaHorizontal = clasificacionViviendaHorizontal;
    }

    public Short getNumHogares() {
        return numHogares;
    }

    public void setNumHogares(Short numHogares) {
        this.numHogares = numHogares;
    }

    public Short getNumHabitaciones() {
        return numHabitaciones;
    }

    public void setNumHabitaciones(Short numHabitaciones) {
        this.numHabitaciones = numHabitaciones;
    }

    public Short getNumDormitorios() {
        return numDormitorios;
    }

    public void setNumDormitorios(Short numDormitorios) {
        this.numDormitorios = numDormitorios;
    }

    public Short getNumEspaciosBanios() {
        return numEspaciosBanios;
    }

    public void setNumEspaciosBanios(Short numEspaciosBanios) {
        this.numEspaciosBanios = numEspaciosBanios;
    }

    public Short getNumCelulares() {
        return numCelulares;
    }

    public void setNumCelulares(Short numCelulares) {
        this.numCelulares = numCelulares;
    }

    public Boolean getRequierePerfeccionamiento() {
        return requierePerfeccionamiento;
    }

    public void setRequierePerfeccionamiento(Boolean requierePerfeccionamiento) {
        this.requierePerfeccionamiento = requierePerfeccionamiento;
    }

    public Integer getAniosSinPerfeccionamiento() {
        return aniosSinPerfeccionamiento;
    }

    public void setAniosSinPerfeccionamiento(Integer aniosSinPerfeccionamiento) {
        this.aniosSinPerfeccionamiento = aniosSinPerfeccionamiento;
    }

    public Integer getAniosPosesion() {
        return aniosPosesion;
    }

    public void setAniosPosesion(Integer aniosPosesion) {
        this.aniosPosesion = aniosPosesion;
    }

    public String getNombrePuebloEtnia() {
        return nombrePuebloEtnia;
    }

    public void setNombrePuebloEtnia(String nombrePuebloEtnia) {
        this.nombrePuebloEtnia = nombrePuebloEtnia;
    }

    public CtlgItem getTenenciaVivienda() {
        return tenenciaVivienda;
    }

    public void setTenenciaVivienda(CtlgItem tenenciaVivienda) {
        this.tenenciaVivienda = tenenciaVivienda;
    }

    public CtlgItem getClasificacionSuelo() {
        return clasificacionSuelo;
    }

    public void setClasificacionSuelo(CtlgItem clasificacionSuelo) {
        this.clasificacionSuelo = clasificacionSuelo;
    }

    public CatEscritura getEscrituraLinderos() {
        return escrituraLinderos;
    }

    public void setEscrituraLinderos(CatEscritura escrituraLinderos) {
        this.escrituraLinderos = escrituraLinderos;
    }

    public CatClaveReordenada getClaveReordenada() {
        return claveReordenada;
    }

    public void setClaveReordenada(CatClaveReordenada claveReordenada) {
        this.claveReordenada = claveReordenada;
    }

    public CtlgItem getTipoPoseedor() {
        return tipoPoseedor;
    }

    public void setTipoPoseedor(CtlgItem tipoPoseedor) {
        this.tipoPoseedor = tipoPoseedor;
    }

    public CatEnte getInformante() {
        return informante;
    }

    public void setInformante(CatEnte informante) {
        this.informante = informante;
    }

    public String getDireccion() {

        return (this.ciudadela == null ? "" : this.ciudadela.getNombre())
                + (calle == null ? "" : " Calle principal: " + calle)
                + (calleS == null ? "" : " Calle Secudaria: " + calleS);
    }

    public Boolean getFichaMadre() {
        return fichaMadre;
    }

    public void setFichaMadre(Boolean fichaMadre) {
        this.fichaMadre = fichaMadre;
    }

    public String getAdminFullName() {
        return adminFullName;
    }

    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }

    public String getAdminCedula() {
        return adminCedula;
    }

    public void setAdminCedula(String adminCedula) {
        this.adminCedula = adminCedula;
    }

    public String getAdminTelefono() {
        return adminTelefono;
    }

    public void setAdminTelefono(String adminTelefono) {
        this.adminTelefono = adminTelefono;
    }

    public String getAdminCelular() {
        return adminCelular;
    }

    public void setAdminCelular(String adminCelular) {
        this.adminCelular = adminCelular;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public Boolean getTieneEscritura() {
        return tieneEscritura;
    }

    public void setTieneEscritura(Boolean tieneEscritura) {
        this.tieneEscritura = tieneEscritura;
    }

    public Integer getCantAlicuotas() {
        return cantAlicuotas;
    }

    public void setCantAlicuotas(Integer cantAlicuotas) {
        this.cantAlicuotas = cantAlicuotas;
    }

    public List<CatPredioAlicuotaComponente> getAlicuotaComponentes() {
        return alicuotaComponentes;
    }

    public void setAlicuotaComponentes(List<CatPredioAlicuotaComponente> alicuotaComponentes) {
        this.alicuotaComponentes = alicuotaComponentes;
    }

    public List<CatPredioLinderos> getCatPredioLinderosCollection() {
        return catPredioLinderosCollection;
    }

    public void setCatPredioLinderosCollection(List<CatPredioLinderos> catPredioLinderosCollection) {
        this.catPredioLinderosCollection = catPredioLinderosCollection;
    }

    public List<CatPredioLinderos> getPredioCollection() {
        return predioCollection;
    }

    public void setPredioCollection(List<CatPredioLinderos> predioCollection) {
        this.predioCollection = predioCollection;
    }

    public CtlgItem getClasificacionVivienda() {
        return clasificacionVivienda;
    }

    public void setClasificacionVivienda(CtlgItem clasificacionVivienda) {
        this.clasificacionVivienda = clasificacionVivienda;
    }

    public CtlgItem getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(CtlgItem tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public CtlgItem getCondicionVivienda() {
        return condicionVivienda;
    }

    public void setCondicionVivienda(CtlgItem condicionVivienda) {
        this.condicionVivienda = condicionVivienda;
    }

    public BigDecimal getAvaluoObraComplement() {
        return avaluoObraComplement;
    }

    public void setAvaluoObraComplement(BigDecimal avaluoObraComplement) {
        this.avaluoObraComplement = avaluoObraComplement;
    }

    public BigDecimal getAvaluoPlussolar() {
        return avaluoPlussolar;
    }

    public void setAvaluoPlussolar(BigDecimal avaluoPlussolar) {
        this.avaluoPlussolar = avaluoPlussolar;
    }

    public BigDecimal getAvaluoPlusconstruccion() {
        return avaluoPlusconstruccion;
    }

    public void setAvaluoPlusconstruccion(BigDecimal avaluoPlusconstruccion) {
        this.avaluoPlusconstruccion = avaluoPlusconstruccion;
    }

    public BigDecimal getAvaluoPlusmunicipal() {
        return avaluoPlusmunicipal;
    }

    public void setAvaluoPlusmunicipal(BigDecimal avaluoPlusmunicipal) {
        this.avaluoPlusmunicipal = avaluoPlusmunicipal;
    }

    public BigDecimal getAvaluoPluscultivos() {
        return avaluoPluscultivos;
    }

    public void setAvaluoPluscultivos(BigDecimal avaluoPluscultivos) {
        this.avaluoPluscultivos = avaluoPluscultivos;
    }

    public BigDecimal getAvaluoPluObraComplement() {
        return avaluoPluObraComplement;
    }

    public void setAvaluoPluObraComplement(BigDecimal avaluoPluObraComplement) {
        this.avaluoPluObraComplement = avaluoPluObraComplement;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public CtlgItem getUsoVia() {
        return usoVia;
    }

    public void setUsoVia(CtlgItem usoVia) {
        this.usoVia = usoVia;
    }

    public CtlgItem getFormaAdquisicion() {
        return formaAdquisicion;
    }

    public void setFormaAdquisicion(CtlgItem formaAdquisicion) {
        this.formaAdquisicion = formaAdquisicion;
    }

    public CtlgItem getVivCencalAcabadoPiso() {
        return vivCencalAcabadoPiso;
    }

    public void setVivCencalAcabadoPiso(CtlgItem vivCencalAcabadoPiso) {
        this.vivCencalAcabadoPiso = vivCencalAcabadoPiso;
    }

    public CtlgItem getVivCencalEstadoAcabadoPiso() {
        return vivCencalEstadoAcabadoPiso;
    }

    public void setVivCencalEstadoAcabadoPiso(CtlgItem vivCencalEstadoAcabadoPiso) {
        this.vivCencalEstadoAcabadoPiso = vivCencalEstadoAcabadoPiso;
    }

    public Boolean getVivCencalPoseeTelfConvencional() {
        return vivCencalPoseeTelfConvencional;
    }

    public void setVivCencalPoseeTelfConvencional(Boolean vivCencalPoseeTelfConvencional) {
        this.vivCencalPoseeTelfConvencional = vivCencalPoseeTelfConvencional;
    }

    public Boolean getVivCencalServInternet() {
        return vivCencalServInternet;
    }

    public void setVivCencalServInternet(Boolean vivCencalServInternet) {
        this.vivCencalServInternet = vivCencalServInternet;
    }

    public String getPropAnteriorPredio() {
        return propAnteriorPredio;
    }

    public void setPropAnteriorPredio(String propAnteriorPredio) {
        this.propAnteriorPredio = propAnteriorPredio;
    }

    public String getCedulaPropAnterior() {
        return cedulaPropAnterior;
    }

    public void setCedulaPropAnterior(String cedulaPropAnterior) {
        this.cedulaPropAnterior = cedulaPropAnterior;
    }

    public BigDecimal getAreaTerrenoAlicuota() {
        return areaTerrenoAlicuota;
    }

    public void setAreaTerrenoAlicuota(BigDecimal areaTerrenoAlicuota) {
        this.areaTerrenoAlicuota = areaTerrenoAlicuota;
    }

    public BigDecimal getAreaAumentoCons() {
        return areaAumentoCons;
    }

    public void setAreaAumentoCons(BigDecimal areaAumentoCons) {
        this.areaAumentoCons = areaAumentoCons;
    }

    public BigDecimal getAreaTotalCons() {
        return areaTotalCons;
    }

    public void setAreaTotalCons(BigDecimal areaTotalCons) {
        this.areaTotalCons = areaTotalCons;
    }

    public BigDecimal getAlicuotaTerreno() {
        return alicuotaTerreno;
    }

    public void setAlicuotaTerreno(BigDecimal alicuotaTerreno) {
        this.alicuotaTerreno = alicuotaTerreno;
    }

    public List<CatPredioCultivo> getCatPredioCultivoCollection() {
        return catPredioCultivoCollection;
    }

    public void setCatPredioCultivoCollection(List<CatPredioCultivo> catPredioCultivoCollection) {
        this.catPredioCultivoCollection = catPredioCultivoCollection;
    }

    public String getCiRucInformante() {
        return ciRucInformante;
    }

    public void setCiRucInformante(String ciRucInformante) {
        this.ciRucInformante = ciRucInformante;
    }

    public String getNombreInformante() {
        return nombreInformante;
    }

    public void setNombreInformante(String nombreInformante) {
        this.nombreInformante = nombreInformante;
    }

    public String getApellidosInformante() {
        return apellidosInformante;
    }

    public void setApellidosInformante(String apellidosInformante) {
        this.apellidosInformante = apellidosInformante;
    }

    public String getCiuActualizador() {
        return ciuActualizador;
    }

    public void setCiuActualizador(String ciuActualizador) {
        this.ciuActualizador = ciuActualizador;
    }

    public String getCiuFiscalizador() {
        return ciuFiscalizador;
    }

    public void setCiuFiscalizador(String ciuFiscalizador) {
        this.ciuFiscalizador = ciuFiscalizador;
    }

    public String getCiuNombresActualizador() {
        return ciuNombresActualizador;
    }

    public void setCiuNombresActualizador(String ciuNombresActualizador) {
        this.ciuNombresActualizador = ciuNombresActualizador;
    }

    public String getCiuNombresFiscalizador() {
        return ciuNombresFiscalizador;
    }

    public void setCiuNombresFiscalizador(String ciuNombresFiscalizador) {
        this.ciuNombresFiscalizador = ciuNombresFiscalizador;
    }

    public String getCiuInformante() {
        return ciuInformante;
    }

    public void setCiuInformante(String ciuInformante) {
        this.ciuInformante = ciuInformante;
    }

    public String getCiuNombresInformante() {
        return ciuNombresInformante;
    }

    public void setCiuNombresInformante(String ciuNombresInformante) {
        this.ciuNombresInformante = ciuNombresInformante;
    }

    public String getCiuHorizontal() {
        return ciuHorizontal;
    }

    public void setCiuHorizontal(String ciuHorizontal) {
        this.ciuHorizontal = ciuHorizontal;
    }

    public String getCiuNombresHorizontal() {
        return ciuNombresHorizontal;
    }

    public void setCiuNombresHorizontal(String ciuNombresHorizontal) {
        this.ciuNombresHorizontal = ciuNombresHorizontal;
    }

    public List<FinanPrestamoPredio> getFinanPrestamoPredioCollection() {
        return finanPrestamoPredioCollection;
    }

    public void setFinanPrestamoPredioCollection(List<FinanPrestamoPredio> finanPrestamoPredioCollection) {
        this.finanPrestamoPredioCollection = finanPrestamoPredioCollection;
    }

    public Collection<CatPredioObraInterna> getCatPredioObraInternaCollection() {
        return catPredioObraInternaCollection;
    }

    public void setCatPredioObraInternaCollection(Collection<CatPredioObraInterna> catPredioObraInternaCollection) {
        this.catPredioObraInternaCollection = catPredioObraInternaCollection;
    }

    public List<CatPredioClasificRural> getCatPredioClasificRuralCollection() {
        return catPredioClasificRuralCollection;
    }

    public void setCatPredioClasificRuralCollection(List<CatPredioClasificRural> catPredioClasificRuralCollection) {
        this.catPredioClasificRuralCollection = catPredioClasificRuralCollection;
    }

    public String getPredialantAnt() {
        return predialantAnt;
    }

    public void setPredialantAnt(String predialantAnt) {
        this.predialantAnt = predialantAnt;
    }

    public List<CatPredioAvalHistorico> getCatPredioAvalHistoricos() {
        return catPredioAvalHistoricos;
    }

    public void setCatPredioAvalHistoricos(List<CatPredioAvalHistorico> catPredioAvalHistoricos) {
        this.catPredioAvalHistoricos = catPredioAvalHistoricos;
    }

    public List<CatPredioPropietario> propietarios() {

        if (this.catPredioPropietarioCollection != null) {
            return (ArrayList<CatPredioPropietario>) catPredioPropietarioCollection;
        }

        return new ArrayList<>();
    }
//
//    public CatPredioExtras getExtras() {
//        if (extras == null) {
//            extras = new CatPredioExtras();
//        }
//        return extras;
//    }
//
//    public void setExtras(CatPredioExtras extras) {
//        this.extras = extras;
//    }

    public CatParroquia getCatParroquia() {
        return catParroquia;
    }

    public void setCatParroquia(CatParroquia catParroquia) {
        this.catParroquia = catParroquia;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public Boolean getCambioNombreTitulo() {
        return cambioNombreTitulo;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public void setCambioNombreTitulo(Boolean cambioNombreTitulo) {
        this.cambioNombreTitulo = cambioNombreTitulo;
    }

    public Boolean getIsAvalauoEspecial() {
        return isAvalauoEspecial;
    }

    public void setIsAvalauoEspecial(Boolean isAvalauoEspecial) {
        this.isAvalauoEspecial = isAvalauoEspecial;
    }

    public String getNombreCambiado() {
        return nombreCambiado;
    }

    public void setNombreCambiado(String nombreCambiado) {
        this.nombreCambiado = nombreCambiado;
    }

    public BigDecimal getValorAfectacionCoeficenteTerreno() {
        return valorAfectacionCoeficenteTerreno;
    }

    public void setValorAfectacionCoeficenteTerreno(BigDecimal valorAfectacionCoeficenteTerreno) {
        this.valorAfectacionCoeficenteTerreno = valorAfectacionCoeficenteTerreno;
    }

    public Boolean getFrentista() {
        return frentista;
    }

    public void setFrentista(Boolean frentista) {
        this.frentista = frentista;
    }

    public Boolean getEstaDibujado() {
        return estaDibujado;
    }

    public void setEstaDibujado(Boolean estaDibujado) {
        this.estaDibujado = estaDibujado;
    }

    public Boolean getAfectaccionPorDanios() {
        return afectaccionPorDanios;
    }

    public void setAfectaccionPorDanios(Boolean afectaccionPorDanios) {
        this.afectaccionPorDanios = afectaccionPorDanios;
    }

    public Boolean getObservacionPorDanios() {
        return observacionPorDanios;
    }

    public void setObservacionPorDanios(Boolean observacionPorDanios) {
        this.observacionPorDanios = observacionPorDanios;
    }

    public List<SvSolicitudServiciosPredios> getSolicitudServicioPrediosist() {
        return solicitudServicioPrediosist;
    }

    public void setSolicitudServicioPrediosist(List<SvSolicitudServiciosPredios> solicitudServicioPrediosist) {
        this.solicitudServicioPrediosist = solicitudServicioPrediosist;
    }

    public Boolean getEsAvaluoVerificado() {
        return esAvaluoVerificado;
    }

    public void setEsAvaluoVerificado(Boolean esAvaluoVerificado) {
        this.esAvaluoVerificado = esAvaluoVerificado;
    }

    @Column(name = "valor_m2")
    @Expose
    private BigDecimal valorM2;

    @Column(name = "valor_base_M2")
    @Expose
    private BigDecimal valorBaseM2;

    @Column(name = "nombres_propietario")
    @Expose
    private String nombrePropietario;

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public BigDecimal getValorBaseM2() {
        return valorBaseM2;
    }

    public void setValorBaseM2(BigDecimal valorBaseM2) {
        this.valorBaseM2 = valorBaseM2;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public Boolean getEsTributario() {
        return esTributario;
    }

    public void setEsTributario(Boolean esTributario) {
        this.esTributario = esTributario;
    }

    public Boolean getEstaExonerado() {
        return estaExonerado;
    }

    public void setEstaExonerado(Boolean estaExonerado) {
        this.estaExonerado = estaExonerado;
    }

    public Boolean getClaveAnteriorVerificada() {
        return claveAnteriorVerificada;
    }

    public void setClaveAnteriorVerificada(Boolean claveAnteriorVerificada) {
        this.claveAnteriorVerificada = claveAnteriorVerificada;
    }

}
