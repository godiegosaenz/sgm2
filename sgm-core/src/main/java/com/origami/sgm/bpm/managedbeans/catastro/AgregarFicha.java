/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatPropiedadItem;
import com.origami.sgm.entities.CatTenenciaItem;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Angel Navarro
 *
 */
@Named
@ViewScoped
public class AgregarFicha implements Serializable {

    private static final Logger LOG = Logger.getLogger(AgregarFicha.class.getName());
    private PdfReporte reporte;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private CatastroServices catas;
    @javax.inject.Inject
    private Entitymanager serv;
    @Inject
    private ServletSession ss;
    @Inject
    private ReportesView reportes;
    private CatPredio predio;
    private CatPredioS4 s4;
    private CatPredioS6 s6;
    private CatPredioS12 s12;
    private CatPredioEdificacion edificacion;
    private CatEnte prop;
    private EnteTelefono telefono;
    private List<EnteTelefono> eliminarTelefono;
    private EnteCorreo correo;
    private CatCiudadela cdlaPr;
    private CatTipoConjunto conjunto;
    private List<EnteCorreo> eliminarCorreo;
    private CatPredioPropietario propPredio;
    private CatEscritura escritura = new CatEscritura();
    private List<CatCiudadela> ciudadelas;
    private List<CatTipoConjunto> conjuntos;
    List<CatPropiedadItem> propiedadItems;
    List<CatTenenciaItem> tenencias;
    private List<CtlgItem> cerramientos, topografias, tipoSuelos, manzanas, accesibilidadC, accesibilidadA, viasC, viasA, abstAguas, aguasServ, abastElects, recBasura, usosA, usosC, tipoPro;
    private List<CatCanton> cantones;
    private HistoricoTramites ht;
    private BigInteger numPredio;
    private boolean editable = false, hab = true, horizontal = false, faltaNumPred = false, nuevo;
    private boolean ps1 = true, ps2 = true, ps3 = true, ps4 = true, ps5 = true, ps6 = true, ps7 = true, ps8 = true, ps9 = true, ps10 = true, ps11 = true, ps12 = true;
    private String representanteLegal;
    protected CatEnteLazy enteLazy;
    private Boolean agregado = false;
    private Boolean proceso = false;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    public void load() {
        try {
            if (sess != null) {
                reporte = new PdfReporte();
                predio = new CatPredio();
                predio.setCalle("VEHICULAR");
                predio.setTipoConjunto(catas.getTipoConjunto(6L));
                predio.setCatPredioPropietarioCollection(new ArrayList<CatPredioPropietario>());

                conjuntos = serv.findAllOrdEntCopy(CatTipoConjunto.class, new String[]{"nombre"}, new Boolean[]{true});
                tenencias = serv.findAllOrdEntCopy(CatTenenciaItem.class, new String[]{"nombre"}, new Boolean[]{false});
                cerramientos = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.cerramiento"});
                topografias = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.topografia"});
                tipoSuelos = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.tipo_suelo"});
                manzanas = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.loc_manzana"});
                abstAguas = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.abastecimiento_agua"});
                aguasServ = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.evac_aguas_serv"});
                abastElects = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.abaste_electrico"});
                recBasura = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.recoleccion_basura"});
                tipoPro = catas.propiedadHorizontal().getCtlgItem("predio.propietario.tipo");
                cantones = serv.findAllOrdEntCopy(CatCanton.class, new String[]{"nombre"}, new Boolean[]{true});
                propiedadItems = serv.findAllEntCopy(CatPropiedadItem.class);

                if (!ss.estaVacio()) {
                    CatCiudadela cdla = null;
                    CatTipoConjunto conj = null;
                    if (ss.getParametros().get("agregado") != null && ss.getParametros().get("idPredio") != null) {
                        agregado = (Boolean) ss.getParametros().get("agregado");

                        if (agregado) {
                            Long idPredio = (Long) ss.getParametros().get("idPredio");
                            predio = new CatPredio();
                            predio = catas.getPredioId(idPredio);
                            cdla = (CatCiudadela) EntityBeanCopy.clone(catas.getCiudadelaById(predio.getCiudadela().getId()));
                            conj = (CatTipoConjunto) EntityBeanCopy.clone(catas.getTipoConjunto(predio.getTipoConjunto().getId()));

                        }
                    }
                    if (ss.getParametros().get("proceso") != null && ss.getParametros().get("idTramite") != null) {
                        Long IdTramite = (Long) ss.getParametros().get("idTramite");
                        proceso = (Boolean) ss.getParametros().get("proceso");
                        if (proceso) {
                            ht = new HistoricoTramites();
                            ht = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{IdTramite});
                            predio = new CatPredio();
                            predio.setCalle("VEHICULAR");
                            predio.setCatPredioPropietarioCollection(new ArrayList<CatPredioPropietario>());
                            predio.setUrbMz(ht.getMz());
                            predio.setUrbSolarnew(ht.getSolar());
                            if (ht.getUrbanizacion() != null) {
                                cdla = (CatCiudadela) EntityBeanCopy.clone(catas.getCiudadelaById(ht.getUrbanizacion().getId()));
                                conj = (CatTipoConjunto) EntityBeanCopy.clone(catas.getTipoConjunto(cdla.getCodTipoConjunto().getId()));
                            }
                            cdlaPr = cdla;
                            conjunto = conj;
                        }
                    }
                    ciudadelas = new ArrayList<>();
                    ciudadelas = (List<CatCiudadela>) EntityBeanCopy.clone(ciudadelasList(conj));
//                    predio.setTipoConjunto(new CatTipoConjunto());
                    predio.setTipoConjunto(conj);
//                    predio.setCiudadela(new CatCiudadela());
                    predio.setCiudadela(cdla);
                    ss.borrarParametros();
                }
                ciudadelas = ciudadelasList(predio.getTipoConjunto());
                cargarListados();

            } else {
                Faces.redirectFaces("/faces/vistaprocesos/catastro/predios.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(AgregarFicha.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void cargarListados() {
        accesibilidadA = new ArrayList<>();
        viasA = new ArrayList<>();
        usosA = new ArrayList<>();
        accesibilidadC = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.accesibilidad"});
        viasC = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.vias"});
        usosC = (List<CtlgItem>) serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{"predio.uso"});

        escritura = new CatEscritura();
        s4 = new CatPredioS4();
        s6 = new CatPredioS6();
        s12 = new CatPredioS12();
        if (agregado) {
            CatPredioS4 cp4 = catas.getCatPredioS4(predio.getId());
            CatPredioS6 cp6 = catas.getCatPredioS6(predio.getId());
            CatPredioS12 cp12 = catas.getCatPredioS12(predio.getId());
            Map paramt = new HashMap<>();
            paramt.put("predio.id", predio.getId());
            paramt.put("estado", "A");
            CatEscritura ce = catas.propiedadHorizontal().getCatEscrituraByNumPredio(paramt);
            if (cp4 != null) {
                s4 = cp4;
            } else {
                setFrentes();
            }
            if (cp6 != null) {
                s6 = cp6;
            } else {
                setValS6();
            }
            if (cp12 != null) {
                s12 = cp12;
            }
            if (ce != null) {
                escritura = ce;
            } else {
                setValEscritura();
            }
        } else {
            setFrentes();
            setValS6();
            setValEscritura();
        }
    }

    private void setFrentes() {
        s4.setFrente1(new BigDecimal(0));
        s4.setFrente2(new BigDecimal(0));
        s4.setFrente3(new BigDecimal(0));
        s4.setFrente4(new BigDecimal(0));
        s4.setFondo1(new BigDecimal(0));
        s4.setFondo2(new BigDecimal(0));
        s4.setFrenteTotal(new BigDecimal(0));
        s4.setAreaCalculada(new BigDecimal(0));
    }

    private void setValS6() {
        s6.setTieneAguaPotable(true);
        s6.setTieneElectricidad(true);
    }

    private void setValEscritura() {
        escritura.setEstado("A");
        escritura.setSecuencia(new BigInteger("1"));
        escritura.setCanton(catas.propiedadHorizontal().getCatCantonById(1L));
    }

    public void imprimirFicha() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            String in = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/catastro/ficha.jasper");
            ss.agregarParametro("predio", predio.getId());
            ss.agregarParametro("SUBREPORT_DIR", path + "reportes//catastro//");
            ss.setReportePDF(reporte.generarPdf("/reportes/catastro/ficha.jasper", ss.getParametros()));
            reportes.descargarPDFarregloBytes(ss.getReportePDF());
        } catch (SQLException | IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void editarS1() {
        if (Utils.isNumberNull(predio.getSector())) {
            JsfUti.messageError(null, "Sector", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getMz())) {
            JsfUti.messageError(null, "Manzana", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getCdla())) {
            JsfUti.messageError(null, "Ciudadela", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getMzdiv())) {
            JsfUti.messageError(null, "Manzana División", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getSolar())) {
            JsfUti.messageError(null, "Solar", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv1())) {
            JsfUti.messageError(null, "División 1 ", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv2())) {
            JsfUti.messageError(null, "División 2", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv3())) {
            JsfUti.messageError(null, "División 3", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv4())) {
            JsfUti.messageError(null, "División 4", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv5())) {
            JsfUti.messageError(null, "División 5", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv6())) {
            JsfUti.messageError(null, "División 6", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv7())) {
            JsfUti.messageError(null, "División 7", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv8())) {
            JsfUti.messageError(null, "División 8", Messages.campoVacioONulo);
            return;
        }
        if (Utils.isNumberNull(predio.getDiv9())) {
            JsfUti.messageError(null, "División 9", Messages.campoVacioONulo);
            return;
        }
        if (proceso) {
            predio.setCiudadela(cdlaPr);
            predio.setTipoConjunto(conjunto);
        }
        if (predio.getCiudadela() == null) {
            JsfUti.messageError(null, "", "Debe Seleccionar una Ciudadela.");
            return;
        }
        if (Utils.isEmpty((List<?>) predio.getCatPredioPropietarioCollection())) {
            JsfUti.messageError(null, "", "Debe ingresar por lo menos un propietario.");
            return;
        }
        try {
            predio.setUsuarioCreador(new AclUser(sess.getUserId()));
            predio.setInstCreacion(new Date());
            predio.setPropiedadHorizontal(horizontal);
            predio.setEstado("A");
            predio.setNombreUrb(predio.getCiudadela().getNombre());
            predio = catas.guardarPredio(predio);
            if (predio != null) {
                if (proceso) {
                    ht.setNumPredio(predio.getNumPredio());
                    serv.persist(ht);
                }
                Faces.messageInfo(null, "Nota", "Los datos para (1. Datos del Predio.), fueron guardados con éxito");
            } else {
                Faces.messageError(null, "Error", "Revise los datos de esta sección, puede ser que predio ya exista.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void generarNumPredio() {
        CatPredio p = catas.generarNumPredio(predio);
        if (p != null) {
            predio = p;
            faltaNumPred = p.getNumPredio() == null;
        }
    }

    public void editarS4() {
        if (predio.getId() == null) {
            JsfUti.messageError(null, "", Messages.PredioNoGrabado);
            return;
        }
        if (Utils.isNumberNull(s4.getFrente1()) && Utils.isNumberNull(s4.getFrente2()) && Utils.isNumberNull(s4.getFrente3()) && Utils.isNumberNull(s4.getFrente4())) {
            JsfUti.messageError(null, "El valor de Frente 1, 2, 3, 4 no debe estar vacio.", "");
            return;
        }
        if (Utils.isNumberNull(s4.getFondo1()) && Utils.isNumberNull(s4.getFondo2())) {
            JsfUti.messageError(null, "El valor de Fondo 1 y 2 no debe estar vacio.", "");
            return;
        }
        if (!Utils.validateDecimalPattern(s4.getFrente1().toString())
                && !Utils.validateDecimalPattern(s4.getFondo1().toString())) {
            JsfUti.messageError(null, "El valor de frente y fondo son incorrectos.", "");
            return;
        }
        if (predio.getPhh() == 0 && predio.getPhv() == 0) {
            if (Utils.isNumberNull(s4.getAreaCalculada())) {
                JsfUti.messageError(null, "El valor de Area Calculada no debe estar vacio.", "");
                return;
            }
            if (!Utils.validateDecimalPattern(s4.getAreaCalculada().toString())) {
                JsfUti.messageError(null, "El valor de Area Calculada en incorrecto.", "");
                return;
            }
        }
        if (s4.getLocManzana() == null) {
            JsfUti.messageError(null, "Debe Ingresar la Localización en Manzana", "");
            return;
        }
        sumaFrentes();
        if (!catas.validarFrente(s4)) {
            JsfUti.messageError(null, "La suma de los Frentes 1,2,3,4\n debe ser igual al\n Frente Total", "");
            return;
        }

        if (!accesibilidadA.isEmpty()) {
            s4.setAccesibilidadList(accesibilidadA);
        }

        s4.setPredio(predio);
        CatPredioS4 ss4 = catas.guardarPredioS4(s4);
        if (ss4 != null) {
            s4 = ss4;
            JsfUti.messageInfo(null, "Infomación.", "Los datos para (Información de Solar.), fueron guardados con éxito");
        } else {
            JsfUti.messageError(null, "Error.", "Ocurrio un error al intentar guardar.");
        }
    }

    public void sumaFrentes() {
        if (s4.getFrente1() != null && s4.getFrente2() != null && s4.getFrente3() != null && s4.getFrente4() != null) {
            BigDecimal total = s4.getFrente1().add(s4.getFrente2().add(s4.getFrente3().add(s4.getFrente4())));
            s4.setFrenteTotal(total);
            verificarFormaIrregulat();
        } else {
            JsfUti.messageError(null, "Un Frente esta vacio debe tener 0 por lo menos", "");
        }
    }

    private void verificarFormaIrregulat() {
        if (s4.getFrente3().compareTo(BigDecimal.ZERO) != 0 && s4.getFrente4().compareTo(BigDecimal.ZERO) != 0) {
            s4.setAreaCalculada(BigDecimal.ZERO);
            JsfUti.messageError(null, "Es un area Irregular", "");
        }
    }

    public void areaCalculada() {
        if (!Utils.isNumberNull(s4.getFondo1()) && !Utils.isNumberNull(s4.getFondo2())) {
            if (s4.getFrente3().compareTo(BigDecimal.ZERO) == 0 && s4.getFrente4().compareTo(BigDecimal.ZERO) == 0) {
                s4.setAreaCalculada(catas.areaCalculada(s4));
            } else {
                s4.setAreaCalculada(BigDecimal.ZERO);
                JsfUti.messageError(null, "Es un area Irregular", "");
            }
        } else {
            JsfUti.messageError(null, "Un Fondo esta vacio debe tener 0 por lo menos", "");
        }
    }

    public void editarS6() {
        if (predio.getId() == null) {
            JsfUti.messageError(null, "", Messages.PredioNoGrabado);
            return;
        }
        if (s6.getTelfFijo() != null) {
            if (Utils.validateNumberPattern(s6.getTelfFijo())) {
                JsfUti.messageError(null, "Número de talefono no es valido", "");
                return;
            }
        }
        try {
            if (!Utils.isNumberNull(s6.getNumRepertorio())) {
                if (s6.getNumRepertorio() < 0) {
                    JsfUti.messageError(null, "El campo Notaria debe contener (números)", "");
                    return;
                }
            }
            if (Utils.isNotEmpty(viasA)) {
                s6.setCtlgItemCollection(viasA);
            }
            if (predio.getCiudadela() != null) {
                if (predio.getCiudadela().getCodParroquia() != null) {
                    s6.setCanton(predio.getCiudadela().getCodParroquia().getIdCanton());
                }
            }
            s6.setPredio(predio);
            CatPredioS6 ok = catas.guardarPredioS6(s6, null, null);
            if (ok != null) {
                s6 = ok;
                JsfUti.messageInfo(null, "Infomación.", "Los datos para (Información de Conexión.), fueron guardados con éxito");
            } else {
                JsfUti.messageError(null, "Error.", "Ocurrio un error al intentar guardar.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void editarS12() {
        if (predio.getId() == null) {
            JsfUti.messageError(null, "", Messages.PredioNoGrabado);
            return;
        }
        try {
//            s12.setPredio(predio);
//            s12.setUsosList(usosA);
//
//            CatPredioS12 ok = catas.guardarPredioS12(s12);
//            if (ok != null) {
//                s12 = ok;
//                JsfUti.messageInfo(null, "Infomación.", "Los datos para (Informacion de Uso y Permiso.), fueron actualizados con éxito");
//            } else {
//                JsfUti.messageError(null, "Error.", "Ocurrio un error al intentar guardar.");
//            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void editarEscritura() {
        if (predio.getId() == null) {
            JsfUti.messageError(null, "", Messages.PredioNoGrabado);
            return;
        }
        try {
            escritura.setPredio(predio);
            if (catas.editarEscritura(escritura)) {
                JsfUti.messageInfo(null, "Información", "Los datos para (Datos de Escritura.), fueron ingresados con éxito");
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
        }
    }

    public void editarEdificacion(CatPredioEdificacion edif) {
        if (predio.getId() == null) {
            JsfUti.messageError(null, "", Messages.PredioNoGrabado);
            return;
        }
        try {
            ss.instanciarParametros();
            edificacion = new CatPredioEdificacion();
            ss.agregarParametro("numPredio", predio.getNumPredio());
            ss.agregarParametro("idPredio", predio.getId());
            ss.agregarParametro("edit", false);
            if (edif != null) {
                edificacion = edif;
                ss.agregarParametro("idIdificacion", edif.getId());
            } else {
                ss.agregarParametro("idIdificacion", null);
            }
            JsfUti.redirectFaces("/faces/vistaprocesos/catastro/edificaciones.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void datosPropietario(CatEnte p) {
        prop = p;
    }

    public void agregarPropietario() {
        nuevo = true;
        prop = new CatEnte();
        correo = new EnteCorreo();
        telefono = new EnteTelefono();
        propPredio = new CatPredioPropietario();
        propPredio.setTipo(new CtlgItem(56L));
        representanteLegal = "";
        JsfUti.update("formProp");
        JsfUti.executeJS("PF('dIngPro').show()");
    }

    public void buscarEnte() {

        if (prop.getCiRuc() != null) {
            try {
                Map paramt = new HashMap<>();
                paramt.put("ciRuc", prop.getCiRuc());
                paramt.put("esPersona", prop.getEsPersona());
                CatEnte newEnt = catas.propiedadHorizontal().getCatEnteByParemt(paramt);
                if (newEnt != null) {
                    for (CatPredioPropietario p : predio.getCatPredioPropietarioCollection()) {
                        if (p.getEnte().getId().compareTo(newEnt.getId()) == 0) {
                            JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                            return;
                        }
                    }
                    eliminarCorreo = new ArrayList<>();
                    eliminarTelefono = new ArrayList<>();
                    prop = newEnt;
                    propPredio.setEnte(prop);
                    JsfUti.update("formProp");
//                JsfUti.executeJS("PF('dIngPro').hide()");
                } else {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
    }

    public void seleccionarReprest() {
        enteLazy = new CatEnteLazy(true);
        JsfUti.update("formSelectRep");
        JsfUti.executeJS("PF('dSelectRep').show();");
    }

    public void agregarRepresentante(CatEnte represt) {
        try {
            prop.setRepresentanteLegal(new BigInteger(represt.getId().toString()));
            representanteLegal = nombreCompletoEnte(prop);
            JsfUti.update("formProp");
            JsfUti.executeJS("PF('dSelectRep').hide()");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agregarTelefono() {
        try {
            if (telefono.getTelefono() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Número de Telefonico.", "");
                return;
            }
            if (!Utils.validateNumberPattern(telefono.getTelefono())) {
                JsfUti.messageInfo(null, "solo debe Ingresar Números", "");
                return;
            }

            if (!PhoneUtils.getValidNumber(telefono.getTelefono(), "EC")) {
                JsfUti.messageInfo(null, "Número Telefonico invalido", "");
                return;
            }
            telefono.setEnte(prop);
            prop.getEnteTelefonoCollection().add(telefono);
            telefono = new EnteTelefono();
            JsfUti.update("formProp:dtTel");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agregarCorreo() {
        try {
            if (correo.getEmail() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Correo.", "");
                return;
            }
            if (!Utils.validarEmailConExpresion(correo.getEmail())) {
                JsfUti.messageInfo(null, "Correo Ingresado es invalido.", "");
                return;
            }
            correo.setEnte(prop);
            prop.getEnteCorreoCollection().add(correo);
            correo = new EnteCorreo();
            JsfUti.update("formProp:dtCorr");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void eliminarTelefono(EnteTelefono tel) {
        try {
            if (tel.getId() != null) {
                eliminarTelefono.add(tel);
            }
            prop.getEnteTelefonoCollection().remove(tel);
            JsfUti.update("formProp:dtTel");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void eliminarCorreo(EnteCorreo corr) {
        try {
            if (corr.getId() != null) {
                eliminarCorreo.add(corr);
            }
            prop.getEnteCorreoCollection().remove(corr);
            JsfUti.update("formProp:dtCorr");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agragarNuevoProp() {
        if (prop.getCiRuc() == null) {
            JsfUti.messageInfo(null, Messages.campoVacio, "Cédula / RUC");
            return;
        }
        if (!Utils.validateNumberPattern(prop.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.valorIncorrecto, "Cédula / RUC");
            return;
        }
        if (!Utils.validateCCRuc(prop.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "Cédula / RUC");
            return;
        }

        try {
            propPredio.setModificado("Catastro");
            propPredio.setPredio(predio);
            propPredio.setEstado("A");

            predio.getCatPredioPropietarioCollection().add(propPredio);

            JsfUti.executeJS("PF('dIngPro').hide()");
            JsfUti.update("frmps1:princ:dtPropietarios");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void eliminarPropietario(CatPredioPropietario p) {
        try {
            int index = 0;
            int i = 0;
            if (!predio.getCatPredioPropietarioCollection().isEmpty()) {
                for (CatPredioPropietario pp : predio.getCatPredioPropietarioCollection()) {
                    if (pp.getEnte().equals(p.getEnte())) {
                        i = index;
                    }
                    index++;
                }
                predio.getCatPredioPropietarioCollection().remove(p);
                JsfUti.update("frmps1:princ:dtPropietarios");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void editarEnte(CatPredioPropietario p) {
        propPredio = new CatPredioPropietario();
        nuevo = false;
        prop = p.getEnte();
        correo = new EnteCorreo();
        telefono = new EnteTelefono();
        propPredio = p;
        representanteLegal = nombreCompletoEnte(prop);
        JsfUti.update("formProp");
        JsfUti.executeJS("PF('dIngPro').show()");
    }

    public void modificarEnte() {
        if (prop.getCiRuc() == null) {
            JsfUti.messageInfo(null, Messages.campoVacio, "Cédula / RUC");
            return;
        }
        if (!Utils.validateNumberPattern(prop.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.valorIncorrecto, "Cédula / RUC");
            return;
        }
        if (!Utils.validateCCRuc(prop.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "Cédula / RUC");
            return;
        }
        JsfUti.update("frmps1:princ:dtPropietarios");
        JsfUti.executeJS("PF('dIngPro').hide()");
    }

    private String nombreCompletoEnte(CatEnte prop) {
        String nombre = null;
        try {
            nombre = null;
            if (prop.getApellidos() != null && prop.getNombres() != null) {
                nombre = prop.getApellidos() + " " + prop.getNombres();
            } else if (prop.getApellidos() != null) {
                nombre = prop.getApellidos();
            } else if (prop.getNombres() != null) {
                nombre = prop.getNombres();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return nombre;
    }

    public void modificarPropietarios() {
        try {
            catas.propiedadHorizontal().getPermiso().guardarOActualizarCatPredioPropietario((List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection());
            JsfUti.messageInfo(null, "Información", "Los datos para (2. Datos Propietarios), fueron actualizados con éxito");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
        }

    }

    public void actualizarCdla() {
        ciudadelas = new ArrayList<>();
        ciudadelas = ciudadelasList(predio.getTipoConjunto());
    }

    public List<CatCiudadela> ciudadelasList(CatTipoConjunto tipo) {
        if (tipo != null) {
            return (List<CatCiudadela>) EntityBeanCopy.clone(catas.getCiudadelasByTipoC(tipo.getId()));
        }
        return null;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public boolean isHab() {
        return hab;
    }

    public void setHab(boolean hab) {
        this.hab = hab;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public CatEnte getProp() {
        return prop;
    }

    public void setProp(CatEnte prop) {
        this.prop = prop;
    }

    public boolean getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public List<CatTipoConjunto> getConjuntos() {
        return conjuntos;
    }

    public void setConjuntos(List<CatTipoConjunto> conjuntos) {
        this.conjuntos = conjuntos;
    }

    public boolean getPs1() {
        return ps1;
    }

    public void setPs1(boolean ps1) {
        this.ps1 = ps1;
    }

    public boolean getPs2() {
        return ps2;
    }

    public void setPs2(boolean ps2) {
        this.ps2 = ps2;
    }

    public boolean getPs3() {
        return ps3;
    }

    public void setPs3(boolean ps3) {
        this.ps3 = ps3;
    }

    public boolean getPs4() {
        return ps4;
    }

    public void setPs4(boolean ps4) {
        this.ps4 = ps4;
    }

    public boolean getPs5() {
        return ps5;
    }

    public void setPs5(boolean ps5) {
        this.ps5 = ps5;
    }

    public boolean getPs6() {
        return ps6;
    }

    public void setPs6(boolean ps6) {
        this.ps6 = ps6;
    }

    public boolean getPs7() {
        return ps7;
    }

    public void setPs7(boolean ps7) {
        this.ps7 = ps7;
    }

    public boolean getPs8() {
        return ps8;
    }

    public void setPs8(boolean ps8) {
        this.ps8 = ps8;
    }

    public boolean getPs9() {
        return ps9;
    }

    public void setPs9(boolean ps9) {
        this.ps9 = ps9;
    }

    public boolean getPs10() {
        return ps10;
    }

    public void setPs10(boolean ps10) {
        this.ps10 = ps10;
    }

    public boolean getPs11() {
        return ps11;
    }

    public void setPs11(boolean ps11) {
        this.ps11 = ps11;
    }

    public boolean getPs12() {
        return ps12;
    }

    public void setPs12(boolean ps12) {
        this.ps12 = ps12;
    }

    public List<CatPropiedadItem> getPropiedadItems() {
        return propiedadItems;
    }

    public void setPropiedadItems(List<CatPropiedadItem> propiedadItems) {
        this.propiedadItems = propiedadItems;
    }

    public List<CatTenenciaItem> getTenencias() {
        return tenencias;
    }

    public void setTenencias(List<CatTenenciaItem> tenencias) {
        this.tenencias = tenencias;
    }

    public List<CtlgItem> getCerramientos() {
        return cerramientos;
    }

    public void setCerramientos(List<CtlgItem> cerramientos) {
        this.cerramientos = cerramientos;
    }

    public List<CtlgItem> getTopografias() {
        return topografias;
    }

    public void setTopografias(List<CtlgItem> topografias) {
        this.topografias = topografias;
    }

    public List<CtlgItem> getTipoSuelos() {
        return tipoSuelos;
    }

    public void setTipoSuelos(List<CtlgItem> tipoSuelos) {
        this.tipoSuelos = tipoSuelos;
    }

    public List<CtlgItem> getManzanas() {
        return manzanas;
    }

    public void setManzanas(List<CtlgItem> manzanas) {
        this.manzanas = manzanas;
    }

    public List<CtlgItem> getAccesibilidadC() {
        return accesibilidadC;
    }

    public void setAccesibilidadC(List<CtlgItem> accesibilidadC) {
        this.accesibilidadC = accesibilidadC;
    }

    public List<CtlgItem> getAccesibilidadA() {
        return accesibilidadA;
    }

    public void setAccesibilidadA(List<CtlgItem> accesibilidadA) {
        this.accesibilidadA = accesibilidadA;
    }

    public List<CatCanton> getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }

    public List<CtlgItem> getViasC() {
        return viasC;
    }

    public void setViasC(List<CtlgItem> viasC) {
        this.viasC = viasC;
    }

    public List<CtlgItem> getViasA() {
        return viasA;
    }

    public void setViasA(List<CtlgItem> viasA) {
        this.viasA = viasA;
    }

    public List<CtlgItem> getAbstAguas() {
        return abstAguas;
    }

    public void setAbstAguas(List<CtlgItem> abstAguas) {
        this.abstAguas = abstAguas;
    }

    public List<CtlgItem> getAguasServ() {
        return aguasServ;
    }

    public void setAguasServ(List<CtlgItem> aguasServ) {
        this.aguasServ = aguasServ;
    }

    public List<CtlgItem> getAbastElects() {
        return abastElects;
    }

    public void setAbastElects(List<CtlgItem> abastElects) {
        this.abastElects = abastElects;
    }

    public List<CtlgItem> getRecBasura() {
        return recBasura;
    }

    public void setRecBasura(List<CtlgItem> recBasura) {
        this.recBasura = recBasura;
    }

    public List<CtlgItem> getUsosA() {
        return usosA;
    }

    public void setUsosA(List<CtlgItem> usosA) {
        this.usosA = usosA;
    }

    public List<CtlgItem> getUsosC() {
        return usosC;
    }

    public void setUsosC(List<CtlgItem> usosC) {
        this.usosC = usosC;
    }

    public boolean isFaltaNumPred() {
        return faltaNumPred;
    }

    public void setFaltaNumPred(boolean faltaNumPred) {
        this.faltaNumPred = faltaNumPred;
    }

    public CatPredioPropietario getPropPredio() {
        return propPredio;
    }

    public void setPropPredio(CatPredioPropietario propPredio) {
        this.propPredio = propPredio;
    }

    public List<CtlgItem> getTipoPro() {
        return tipoPro;
    }

    public void setTipoPro(List<CtlgItem> tipoPro) {
        this.tipoPro = tipoPro;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public EnteCorreo getCorreo() {
        return correo;
    }

    public void setCorreo(EnteCorreo correo) {
        this.correo = correo;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public CatPredioS4 getS4() {
        return s4;
    }

    public void setS4(CatPredioS4 s4) {
        this.s4 = s4;
    }

    public CatPredioS6 getS6() {
        return s6;
    }

    public void setS6(CatPredioS6 s6) {
        this.s6 = s6;
    }

    public CatPredioS12 getS12() {
        return s12;
    }

    public void setS12(CatPredioS12 s12) {
        this.s12 = s12;
    }

    public CatPredioEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(CatPredioEdificacion edificacion) {
        this.edificacion = edificacion;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public Boolean getProceso() {
        return proceso;
    }

    public void setProceso(Boolean proceso) {
        this.proceso = proceso;
    }

}
