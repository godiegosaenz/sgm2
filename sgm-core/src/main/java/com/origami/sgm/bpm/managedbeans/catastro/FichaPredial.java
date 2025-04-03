package com.origami.sgm.bpm.managedbeans.catastro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.origami.config.ConfigFichaPredial;
import com.origami.config.MainConfig;
import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.event.FichaPredialOnLoadEvent;
import com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal.InspeccionFinalConsulta;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioAvalHistorico;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioFusionDivision;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.avaluos.SectorValorizacion;
import com.origami.sgm.geo.GeodataService;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatEscrituraLazy;
import com.origami.sgm.lazymodels.HistoricoPredioLazy;
import com.origami.sgm.lazymodels.PeInspeccionFinalLazy;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.sgm.reportes.ReportesView;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.ejbs.censocat.UploadDocumento;
import com.origami.sgm.services.ejbs.censocat.UploadFotoBean;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import util.EntityBeanCopy;
import util.Faces;
import util.FilesUtil;
import util.HiberUtil;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class FichaPredial extends PredioUtil implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
//    private CatPredio predio;
    @Inject
    protected UserSession sess;
    @Inject
    protected ServletSession ss;
    @Inject
    protected ReportesView reportes;
    @javax.inject.Inject
    protected RecaudacionesService recaudacion;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;
    @Inject
    protected OmegaUploader fserv;
    @Inject
    protected UploadFotoBean fotoBean;
    @Inject
    protected UploadDocumento documentoBean;
    @Inject
    protected InspeccionFinalConsulta inspFinal;
    @javax.inject.Inject
    protected GeodataService geodataService;

    @Inject
    protected Event<FichaPredialOnLoadEvent> loadEvent;

    private ConsultaMovimientoModel modelo;
    private RegMovimiento movimiento = new RegMovimiento();
    protected Boolean editable = false;
    protected CatEscritura escr;
    protected CatCanton canton;
    protected Long predioLink;

    protected RegFicha ficha;
    private CatPredioS12 usos;
    protected CatEscritura escritura = new CatEscritura();
    protected CatEscrituraLazy escrituras;
    protected List<CatPredioPropietario> propietarios, propietariosTitulos;
    protected List<RegMovimientoFicha> movimientos = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(FichaPredial.class.getName());
    protected HistoricoPredioLazy historico;
    private PePermisoLazy permisosconst;
    private PeInspeccionFinalLazy inspecciones;
    protected CtlgItem tipoPrototipos;
    protected List<FotoPredio> fotos;
    protected List<SectorValorizacion> subsectores;
    protected CatParroquia parroquia;
    protected List<CatCanton> cantones;
    protected List<CatEscritura> escriturasConsulta;
    protected Boolean controlAddUp;
    protected CatEnteLazy responsablesLazy;
    protected CatEnte actualizadorPredio;
    protected CatEnte fiscalizadorPredio;
    protected CatEnte enteHorizontal;
    protected Integer tipoEnte;
    protected Boolean esPersona = true;
    protected String controlResponsable;
    protected String elementosConstuctivos;
    protected CatCanton cantonId;

    protected String predioId;
    protected String claveCat;
    protected Boolean coopropietarios;
    protected Boolean ph = Boolean.FALSE;
//variaables para cuando el predio fue dividido y se necesita mostrar la raiz
    protected CatPredioFusionDivision catPredioFusionDivision;

    protected String observacionEliminar;
    protected String updateTableDocumento;
    protected String observacionRestriccion;
    protected List<String> observacionRestricciones;
    protected Object objectoEliminar;
    protected Integer tipoEliminar;

    protected Boolean esTarea = false;
    protected Boolean esTareaAp = false;
    protected List<CatPredioAvalHistorico> avaluosHistoricosPredio;

    protected Short codProvincia = SisVars.PROVINCIA;

    protected RenLiquidacion liquidacion;
    protected CatEnte enteComprador;
    protected String nombreComprador;

    protected String observaciones;
    protected int tipoTarea = 1;
//    @PostConstruct

    private BigDecimal valorMetro2;

    protected String updateScripture;
    protected String updateLinderos;

    private String nombrePropietarioExtras = "";

//    @PostConstruct
    public void load() {
        propietariosTitulos = new ArrayList<>();
        codProvincia = SisVars.PROVINCIA;
        if (!JsfUti.isAjaxRequest()) {
            cargarQueryPar();
            this.cargarDatos();
        }
    }

    public void cargarQueryPar() {
        if (predioId != null) {
            predioLink = Long.valueOf(predioId);
        }

    }

    protected void cargarDatos() {
        try {
            if (sess != null) {
                this.init();
                canton = new CatCanton();
                escr = new CatEscritura();

                Long numeroPredio = null;
                Long id = null;
                if (claveCat != null) {
                    predio = catas.getPredioByClaveCat(claveCat);
                } else {
                    if (ss.getParametros() == null) {
                        JsfUti.redirectFaces2(SisVars.urlbase);
                    }
                    if (predioLink == null) {
                        if ((ss.getParametros().get("numPredio") != null || ss.getParametros().get("idPredio") != null) && ss.getParametros().get("edit") != null) {
                            if (ss.getParametros().get("numPredio") != null) {
                                numeroPredio = Long.parseLong(ss.getParametros().get("numPredio").toString());
                            } else {
                                id = Long.parseLong(ss.getParametros().get("idPredio").toString());
                            }
                            editable = Boolean.parseBoolean(ss.getParametros().get("edit").toString());
                        }
                    } else {
                        editable = false;
                        id = predioLink;
                    }

                    if (numeroPredio != null) {
                        predio = catas.getPredioNumPredio(numeroPredio);
                    } else {
                        if (id == null) {
                            JsfUti.redirectFaces("/vistaprocesos/catastro/prediosSV.xhtml");
                        } else {
                            predio = catas.getPredioId(id);
                        }
                    }
                }
                if (predio != null) {

                    if (predio.getPropiedadHorizontal() != null) {
                        if (predio.getPredioRaiz() != null) {
                            ph = predio.getPropiedadHorizontal() && predio.getPredioRaiz() != null;
                        }

                    }
//                    if (predio.getExtras() == null) {
//                        predio.setExtras(new CatPredioExtras());
//                    }
                    subsectores = em.findAllEntCopy(SectorValorizacion.class);
                    System.out.println("id Predio " + predio.getId());
                    usosA = new ArrayList<>();
                    vias = new ArrayList<>();
                    instalacionesEspeciales = new ArrayList<>();
                    escrituras = new CatEscrituraLazy(predio);
                    catPredioFusionDivision = (CatPredioFusionDivision) em.find(Querys.getPredioRaizDivision, new String[]{"predioResultante"}, new Object[]{predio.getId()});
                    if (predio.getRegFicha() != null) {
                        ficha = predio.getRegFicha();
                        movimientos = catas.propiedadHorizontal().getFichaServices().getRegMovimientoFichasList(ficha.getId());
                    }
                    if (predio.getCiudadela() != null) {
                        if (predio.getTipoConjunto() == null) {
                            if (predio.getCiudadela().getCodTipoConjunto() != null) {
                                predio.setTipoConjunto(predio.getCiudadela().getCodTipoConjunto());
                            }
                        }
                        parroquia = predio.getCiudadela().getCodParroquia();
                    }
                    if (predio.getCatPredioS4() != null) {
                        caracteristicas = predio.getCatPredioS4();
                    } else {
                        caracteristicas = new CatPredioS4();
                        caracteristicas.setPredio(predio);
                    }
                    if (predio.getCatPredioS6() != null) {
                        servicios = predio.getCatPredioS6();
                    } else {
                        servicios = new CatPredioS6();
                        servicios.setPredio(predio);
                    }
                    listarViaseInstalacionesEspeciales();
                    if (predio.getCatPredioPropietarioCollection() != null) {
                        propietarios = (List<CatPredioPropietario>) em.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{predio.getId()});
                    }

                    if (predio.getResponsableActualizadorPredial() != null) {
                        actualizadorPredio = predio.getResponsableActualizadorPredial();
                    }
                    if (predio.getResponsableFiscalizadorPredial() != null) {
                        fiscalizadorPredio = predio.getResponsableFiscalizadorPredial();
                    }
                    if (predio.getEnteHorizontal() != null) {
                        enteHorizontal = predio.getEnteHorizontal();
                    }
                    if (predio.getInformante() != null) {
                        informante = predio.getInformante();
                    }
                    this.setUsr(sess.getName_user());
                    listarUsos();
                    this.cargarFotos();
                    cargarDocumentos();
                    escritura = catas.getEscritura(Querys.getCatEscrituraByPredio, new String[]{"id"}, new Object[]{predio.getId()});
                    if (escritura == null) {
                        cantonId = (CatCanton) em.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA});
                        escritura = new CatEscritura();
                        escritura.setPredio(predio);
                        escritura.setEstado("A");
                        escritura.setSecuencia(new BigInteger("1"));
                        escritura.setCanton(cantonId);
                    }
                    this.getLinderosEscritura();
                    if (propietarios != null && propietarios.size() > 0) {
                        for (CatPredioPropietario pp : propietarios) {
                            if (pp.getEstado().equalsIgnoreCase("A")) {
                                if (pp.getCopropietario() != null && pp.getCopropietario()) {
                                    coopropietarios = true;
                                    break;
                                }
                            }
                        }
                    }
                    getAvalHistorico();
                    this.setNamePredioByCiudadela();
                    try {
                        this.bloques = (List<CatPredioEdificacion>) predio.getCatPredioEdificacionCollection();
                        if (editable) {
                            this.setPredioAnt(generarJson(predio));
                        }
                        if (predio.getCalleAv() != null && predio.getCalleAv().equalsIgnoreCase("1")) {
                            this.setAvCalle(true);
                        } else {
                            this.setAvCalle(false);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                    loadEvent.fire(new FichaPredialOnLoadEvent(predio));
                } else {
                    JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
                }
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public String claveCatastralCompleta() {

        String clave = predio.getClaveCat();
        System.out.println("Clave : " + clave);
        String prov = clave.substring(0, 2);
        if (prov.equals("99")) {
            prov = String.format("%02d", SisVars.PROVINCIA);
        }
        String a = clave.substring(2, clave.length());
        a = prov + a;
        return a;
    }

    public void getAvalHistorico() {
        CatPredioAvalHistorico valorSuelo = null;
        avaluosHistoricosPredio = em.findAll(Querys.getAvaluosHistoricosPorPredios, new String[]{"predio"}, new Object[]{predio.getId()});
        if (!avaluosHistoricosPredio.isEmpty()) {
            if (avaluosHistoricosPredio.size() >= 1) {
                valorSuelo = avaluosHistoricosPredio.get(avaluosHistoricosPredio.size() - 1);
                if (valorSuelo != null) {
                    if (valorSuelo.getValorBaseM2() != null) {
                        valorMetro2 = valorSuelo.getValorBaseM2();
                    }
                }
            }

        }

    }

    public void getLinderosEscritura() {
        try {
            if (escritura != null) {
                predio.getPredioCollection().size();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void imprimirLiquidaciónGuardado(PeInspeccionFinal inspeccion) {
        inspFinal.imprimirLiquidación(inspeccion);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Long fileId = fserv.uploadFile(FilesUtil.copyFileServer1(event), event.getFile().getFileName(), event.getFile().getContentType());
            fotoBean.setNombre(event.getFile().getFileName());
            fotoBean.setPredioId(predio.getNumPredio().longValue());
            fotoBean.setIdPredio(predio.getId());
            fotoBean.setContentType(event.getFile().getContentType());
            fotoBean.setFileId(fileId);
            fotoBean.saveFoto();
            cargarFotos();
            Faces.messageInfo(null, "Nota1", "Foto guardada satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void handleFileDocumentBySave(FileUploadEvent event) {
        try {
            Long documentoId = fserv.uploadFile(FilesUtil.copyFileServer1(event), event.getFile().getFileName(), event.getFile().getContentType());
            documentoBean.setFechaCreacion(new Date());
            documentoBean.setNombre(event.getFile().getFileName());
            documentoBean.setRaiz(predio.getId());
            documentoBean.setContentType(event.getFile().getContentType());
            documentoBean.setDocumentoId(documentoId);
            documentoBean.setIdentificacion("Datos Prediales");
            this.setDocumento(documentoBean.saveDocumento());
            cargarDocumentos();
            Faces.messageInfo(null, "Nota1", "Archivo cargado Satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected void listarUsos() {
        try {
            if (predio.getCatPredioS12() != null) {
                usos = predio.getCatPredioS12();
                if (usos.getUsosList() != null) {
                    for (CtlgItem ci : usos.getUsosList()) {
                        usosA.add(ci);
                    }
                }
            } else {
                usos = new CatPredioS12();
                usos.setPredio(predio);
                usos.setUsosList(usosA);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    protected void listarViaseInstalacionesEspeciales() {
        Collection<CtlgItem> xvias;
        Collection<CtlgItem> xinstalaciones;
        if (predio.getCatPredioS6() != null) {
            xvias = predio.getCatPredioS6().getCtlgItemCollection();
            xinstalaciones = predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial();
            if (xvias != null && !xvias.isEmpty()) {

                for (Iterator<CtlgItem> iterator = xvias.iterator(); iterator.hasNext();) {
                    CtlgItem v = iterator.next();
                    if (!vias.contains(v)) {
                        vias.add(v);
                    }
                }

//                for (CtlgItem v : viasClone) {
//                    if (!vias.contains(v)) {
//                        vias.add(v);
//                    }
//
//                }
            } else {
                servicios.setCtlgItemCollection(vias);
            }
            if (xinstalaciones != null && !xinstalaciones.isEmpty()) {
                for (CtlgItem v : xinstalaciones) {
                    instalacionesEspeciales.add(v);
                }
            } else {
                servicios.setCtlgItemCollectionInstalacionEspecial(instalacionesEspeciales);
            }
        }
    }

    public void cargarFotos() {
        if (predio == null) {
            return;
        }
//        if (predio.getPredioRaiz() == null) {
//            fotos = em.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()});
//        } else {
//            fotos = em.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getPredioRaiz().longValue()});
//        }
        fotos = em.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()});
        System.out.println("tamani fotos: " + fotos.size());

    }

    public void onCellEdit(CellEditEvent event) {
        Object newValue = event.getNewValue();
        if (newValue == null) {
            Faces.messageWarning(null, "Advertencia!", "El dato ingresado es incorrecto");
        }
    }

    public List<CtlgItem> getListado(String argumento) {
        HiberUtil.newTransaction();
        List<CtlgItem> ctlgItem = (List<CtlgItem>) em.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
        return ctlgItem;
    }

    public void propietariosCode(CatPredioPropietario propietario, String control, Boolean esAnterior) {

        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(predio.getId().toString());
        params.put("idPredio", p);
        p = new ArrayList<>();
        if (propietario != null && propietario.getId() != null) {
            p.add(propietario.getId().toString());
        }
        params.put("idCatPredioPro", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        params.put("nuevo", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        p = new ArrayList<>();
        p.add(editable.toString());
        params.put("editar", p);

        if (control.equals("")) {
            p = new ArrayList<>();
            if (esAnterior) {
                p.add("true");
            } else {
                p.add("false");
            }

        } else {
            p.add("false");
        }
        params.put("anterior", p);

        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "85%");
        options.put("height", "450");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/propietarios", options, params);

    }

    public void propietario(CatPredioPropietario propietario, Boolean esAnterior) {
        propietariosCode(propietario, "", esAnterior);
    }

    public void propietario(CatPredioPropietario propietario) {
        propietariosCode(propietario, "-", null);
    }

    public void procesarPropietario(SelectEvent event) {
        CatPredioPropietario propietario = (CatPredioPropietario) event.getObject();
        Boolean existe = false;
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation().serializeNulls();
        Gson gson2 = builder.create();
        if (propietario != null) {
            for (CatPredioPropietario p : propietarios) {
                if (p.getEnte().getId().compareTo(propietario.getEnte().getId()) == 0) {
                    existe = true;
                    break;
                }
            }
            propietario.getPredio().setNombrePropietario(getNamePropietarios(propietario));
            System.out.println("getNamePropietarios(propietario)" + getNamePropietarios(propietario));
            // propietario.getPredio().setExtras(propietario.getPredio().getExtras());
            this.predio = catas.guardarPredio(propietario.getPredio());
            propietario.setPredio(this.predio);
            if (!propietarios.contains(propietario)) {
                propietarios.add(propietario);
            } else {
                propietarios.set(propietarios.indexOf(propietario), propietario);
            }

            for (CatPredioPropietario pp : propietarios) {
                if ((pp.getCopropietario() != null)
                        && (pp.getEstado().equalsIgnoreCase("A")) && (pp.getCopropietario())) {
                    coopropietarios = true;
                    break;
                }
            }

            setFichaEdifAnt(getFichaEdifAct());
            saveHistoric(predio, "Actualizacion Informacion de propietarios", null, null, null, null, Boolean.TRUE);

            Faces.messageInfo(null, "Nota!", "Propietarios actualizadas satisfactoriamente");
        }
    }

    public void eliminarPropietario(CatPredioPropietario propietario) {

        propietario.setEstado("I");
        propietario.setModificado(sess.getName_user());
        propietario = catas.guardarPropietario(propietario, sess.getName_user());
        String nombreDelete = getNamePropietariosDelete(propietario).trim();
        if (propietario.getPredio().getNombrePropietario().contains(nombreDelete)) {
            propietario.getPredio().setNombrePropietario(propietario.getPredio().getNombrePropietario().replace(nombreDelete, ""));

            //  propietario.getPredio().setExtras(propietario.getPredio().getExtras());
            this.predio = catas.guardarPredio(propietario.getPredio());
            propietario.setPredio(this.predio);
        }

        propietarios.remove(propietario);
        JsfUti.messageInfo(null, "Propietario", "Propietario eliminado.");
    }

    public String getNamePropietariosDelete(CatPredioPropietario cpp) {
        StringBuilder sb = new StringBuilder();

        if (cpp.getEnte() != null) {
            String nombres = " ";
            if (cpp.getEnte().getEsPersona()) {
                nombres = (cpp.getEnte().getApellidos() == null ? "" : cpp.getEnte().getApellidos()) + " " + (cpp.getEnte().getNombres() == null ? "" : cpp.getEnte().getNombres());
            } else {
                nombres = cpp.getEnte().getRazonSocial() == null ? "" : cpp.getEnte().getRazonSocial();
            }

            sb.append(nombres.trim()).append(" - ");

        }
        System.out.println("com.origami.catastroextras.cdi.FichaPredialSanVicente.getNamePropietariosDelete()" + sb);
        if (sb.length() >= 3) {
            sb.delete(sb.length() - 3, sb.length() - 1);
        }

        System.out.println("com.origami.catastroextras.cdi.FichaPredialSanVicente.getNamePropietariosDelete()" + sb);
        return sb.toString().toUpperCase();
    }

    public String getNamePropietarios(CatPredioPropietario cpp) {
        StringBuilder sb = new StringBuilder();
        String nombres = "";
        if (cpp.getPredio().getNombrePropietario() != null) {
            if (!cpp.getPredio().getNombrePropietario().equals("")) {
                nombres = cpp.getPredio().getNombrePropietario() + " - ";
            }
        } else {
            nombres = " ";
        }

        if (cpp.getEnte() != null) {
            if (cpp.getEnte().getEsPersona()) {
                nombres = nombres + (cpp.getEnte().getApellidos() == null ? "" : cpp.getEnte().getApellidos()) + " " + (cpp.getEnte().getNombres() == null ? "" : cpp.getEnte().getNombres());
            } else {
                nombres = nombres + (cpp.getEnte().getRazonSocial() == null ? "" : cpp.getEnte().getRazonSocial());
            }
            sb.append(nombres.trim()).append(" - ");
        }

        if (sb.length() >= 3) {
            sb.delete(sb.length() - 3, sb.length() - 1);
        }

        return sb.toString().toUpperCase();
    }

    public void datosMovFicha(RegMovimiento mov) {
        try {
            if (mov != null) {
                movimiento = reg.getRegMovimientoById(mov.getId());
                modelo = new ConsultaMovimientoModel();
                modelo = reg.getConsultaMovimiento(mov.getId());
                if (modelo == null) {
                    Faces.messageError(null, "No se pudo hacer la consulta.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void referenciarTipo(CtlgItem prot) {
        if (prot != null) {
            prototipos = em.findAllEntCopy(Querys.getCtlgItemByParent, new String[]{"padre"}, new Object[]{prot.getId()});
        } else {
            Faces.messageWarning(null, "Advertencia!", "Elija el tipo de prototipo arquitectonico");
        }
    }

    public void selecctionarEdif(CatPredioEdificacion edf) {
        this.setEdif(edf);
    }

    public void getDatosPrototipos() {
        for (CatPredioEdificacion e : this.getBloques()) {
            if (e.getId().equals(this.getEdif().getId())) {
                e = this.getEdif();
            }
        }
    }

    public void linkearFicha() {
        if (predio != null && predio.getRegFicha() != null) {
            Faces.redirectFacesNewTab("/vistaprocesos/registroPropiedad/fichaIngresoEditar.xhtml?idficha=" + predio.getRegFicha().getId());
        }
    }

    public void linkGeoserver() {
        Faces.redirectFacesNewTab("/vistaprocesos/registroPropiedad/fichaIngresoEditar.xhtml?idficha=" + predio.getRegFicha().getId());
    }

    public StreamedContent getContentUbicacion() {
        URL url = null;
        try {
            try {
                String ip = ubicacionPredio();
                url = new URL(ip);
            } catch (NumberFormatException | MalformedURLException e) {
                Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
            }
            if (url != null) {
                return new DefaultStreamedContent(url.openStream());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ubicacionPredio() {
        try {
            String urlPredio = null;
            String claveCat = null;
            if (predio.getPredioRaiz() == null) {
                if (predio.getClaveReordenada() != null) {
                    claveCat = predio.getClaveReordenada().getClaveCat();
                } else {
                    claveCat = predio.getClaveCat();
                }
            } else {
                CatPredio predioRaiz = em.find(CatPredio.class, predio.getPredioRaiz().longValue());
                if (predioRaiz.getClaveReordenada() != null) {
                    claveCat = predioRaiz.getClaveReordenada().getClaveCat();
                } else {
                    claveCat = predioRaiz.getClaveCat();
                }
            }
            if (claveCat == null) {
                return "/css/homeIconsImages/reselladoPlanos.png";
            }
//            urlPredio = "http://200.112.216.17/geoapi-libertad/rest/predio/croquis/" + claveCat;
            try {
                return geodataService.getUrlPredioImage(claveCat, null, null);
            } catch (Exception e) {
                return "/css/homeIconsImages/reselladoPlanos.png";
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return "/css/homeIconsImages/reselladoPlanos.png";
    }

    public String getCroquis() {
        String urlPredio = "";
        if (predio != null) {
            switch (SchemasConfig.DB_ENGINE) {
                case ORACLE:
                    urlPredio = SisVars.URLPLANOIMAGENPREDIO + this.claveCroquis(predio);
                    //urlPredio = "http://200.112.216.17/geoapi-libertad/rest/predio/croquis/" + this.claveCroquis(predio);
                    break;
                case POSTGRESQL:
                    if (predio.getPredioRaiz() != null) {
                        CatPredio predioRaiz = (CatPredio) em.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{predio.getPredioRaiz()});
                        urlPredio = SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio();
                    } else {
                        urlPredio = SisVars.URLPLANOIMAGENPREDIO + predio.getNumPredio();
                    }

                    System.out.println("URL Criquis: " + urlPredio);
                    break;
                default:
                    break;
            }
        }
        return urlPredio;
    }

    public String colindantesPredio() {
        try {
            String claveCat = null;
            if (predio.getPredioRaiz() == null) {
                claveCat = predio.getClaveCat();
                if (predio.getClaveReordenada() != null) {
                    claveCat = predio.getClaveReordenada().getClaveCat();
                } else {
                    claveCat = predio.getClaveCat();
                }
            } else {
                CatPredio predioRaiz = em.find(CatPredio.class, predio.getPredioRaiz().longValue());
                if (predioRaiz.getClaveReordenada() != null) {
                    claveCat = predioRaiz.getClaveReordenada().getClaveCat();
                } else {
                    claveCat = predioRaiz.getClaveCat();
                }
            }
            if (claveCat == null) {
                return "/css/homeIconsImages/reselladoPlanos.png";
            }
            return geodataService.getUrlColindantesImage(claveCat, null, null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return "/css/homeIconsImages/reselladoPlanos.png";
    }

    public void saveEscritura() {
        try {
            Boolean controlEstado = false;
            if (this.predio.getId() != null && this.canton != null && this.escr.getFecInscripcion() != null
                    && this.escr.getNotaria() != null && this.escr.getAreaSolar() != null) {

                Map<String, Object> paramt = new HashMap<>();
                paramt.put("predio", predio);
                escriturasConsulta = em.findAll(Querys.getEscriturasByPredio, new String[]{"predio"}, new Object[]{predio});

                if ((escriturasConsulta.isEmpty() || escriturasConsulta == null) && escr.getEstado().equals("I")) {
                    JsfUti.messageInfo(null, "La primera escritura para el predio no debe estar Inactiva", "");
                    return;
                }
                if (escr.getEstado().equals("A") && !escriturasConsulta.isEmpty() && escriturasConsulta != null) {

                } else if (escr.getEstado().equals("I") && !escriturasConsulta.isEmpty() && escriturasConsulta != null) {
                    for (CatEscritura escri : escriturasConsulta) {
                        if (escri.getEstado().equals("A")) {
                            controlEstado = true;
                        }
                    }
                    if (controlEstado == true) {
                        JsfUti.messageInfo(null, "Las escrituras no pueden tener Estado Inactivo", "");
                        return;
                    }
                }
                escr.setPredio(predio);
                escr.setCanton(canton);
                escr.setFecCre(new Date());

                if (controlAddUp == true) {
                    em.saveAll(this.escr);
                }
                if (controlAddUp == false) {
                    em.persist(escr);
                }
                this.canton = new CatCanton();
                this.escr = new CatEscritura();
                escriturasConsulta = new ArrayList<>();
                escriturasConsulta = em.findAll(Querys.getEscriturasByPredio, new String[]{"predio"}, new Object[]{predio});
                this.predio.setCatEscrituraCollection(escriturasConsulta);
                if (saveHistoric(predio, "ACTUALIZACION DE ESCRITURAS", getFichaEdifAnt(), getFichaEdifAct(), getFichaModelAnt(), getFichaModelAct(), Boolean.TRUE)) {
                    JsfUti.messageInfo(null, "Exito", "Datos grabados Satisfactoriamente");
                } else {
                    JsfUti.messageInfo(null, "Exito", "Datos grabados Satisfactoriamente");
                }
            } else {
                JsfUti.messageInfo(null, "Debe Registrar todos los campos Obligatorios", "");
            }
        } catch (Exception e) {
            JsfUti.messageInfo(null, "Error al Guardar", "");
        }
    }

    public void saveEscrituraControl() {
        cantones = em.findAllEntCopy(CatCanton.class);
        controlAddUp = true;
    }

    public void updateEscrituraControl(CatEscritura e) {
        controlAddUp = false;
        cantones = em.findAllEntCopy(CatCanton.class);
        this.canton = new CatCanton();
        this.escr = new CatEscritura();
        this.escr = e;
        this.canton = e.getCanton();
    }

    public void cambioTipoPersona() {
        if (tipoEnte == 1) {
            esPersona = true;
        }
        if (tipoEnte == 2) {
            esPersona = false;
        }
        responsablesLazy = new CatEnteLazy(esPersona);
        JsfUti.update("frmResponsableDialog:dtresponsable");
    }

    public void loadResponsablesPredio(ActionEvent event) {
        try {
            responsablesLazy = new CatEnteLazy(true);
            if (event.getComponent().getId().equals("btnActualizadorResponsable")) {
                this.controlResponsable = "actualizador";
            }
            if (event.getComponent().getId().equals("btnFiscalizadorResponsable")) {
                this.controlResponsable = "fiscalizador";
            }
            if (event.getComponent().getId().equals("btnEnteHorizontal")) {
                this.controlResponsable = "enteHorizontal";
            }
            if (event.getComponent().getId().equals("btnInformante")) {
                this.controlResponsable = "informante";
            }

            JsfUti.update("frmResponsableDialog:dtresponsable");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, controlResponsable, e);
        }
    }

    public void selectedResponsable(CatEnte responsable) {

        if (controlResponsable.equals("fiscalizador")) {
            this.setFiscalizadorPredio(responsable);
            JsfUti.update(":tdatos:frmResponsables:actPredial");
        }
        if (controlResponsable.equals("actualizador")) {
            this.setActualizadorPredio(responsable);
            JsfUti.update(":tdatos:frmResponsables:actFisc");
        }
        if (controlResponsable.equals("enteHorizontal")) {
            this.setEnteHorizontal(responsable);
            JsfUti.update(":tdatos:frmViviendaCensal");
        }
        if (controlResponsable.equals("informante")) {
            setInformante(responsable);
            predio.setInformante(informante);
            JsfUti.update(":tdatos:tadicionales:frmObservaciones");
        }
    }

    public void listarPredios(Integer linderos) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(predio.getParroquia().toString());
        params.put("parroquia", p);
        p = new ArrayList<>();
        p.add(predio.getZona().toString());
        params.put("zona", p);
        p = new ArrayList<>();
        p.add(predio.getSector().toString());
        params.put("sector", p);
        p = new ArrayList<>();
        p.add(predio.getMz().toString());
        params.put("mz", p);
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "95%");
        options.put("height", "70%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/predios", options, params);
    }

    public void seleccionarPredio(SelectEvent event) {
        try {
            if (event != null) {
                if (event.getObject() == null) {
                    Faces.messageWarning(null, "Advertencia!", "Debe seleccionar el predio...");
                    return;
                }

                List<CatPredio> temp = (List<CatPredio>) event.getObject();
                if (temp != null && temp.size() > 0) {
                    CatPredio cp = temp.get(0);
                    if (Objects.equals(cp.getParroquia(), predio.getParroquia())
                            && Objects.equals(cp.getZona(), predio.getZona())
                            && Objects.equals(cp.getSector(), predio.getSector())
                            && Objects.equals(cp.getMz(), predio.getMz())) {
                        predioColind = cp;
                        nombreLindero = cp.getClaveCat();
                        JsfUti.update(":tdatos:frmLinderos");
                        JsfUti.messageInfo(null, "Información", "El predio Seleccionado: " + cp.getClaveCat());
                    } else {
                        JsfUti.messageInfo(null, "Información", "El predio no corresponde a la misma parroquia, zona, sector o manzana.");
                    }
                } else {
                    JsfUti.messageInfo(null, "Información", "No ha seleccionado ningun predio.");
                }

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, predioId, e);
        }
    }

    public void reporteDetalleCalculos(CatPredioAvalHistorico avalHistorico) {
        try {
            if (predio != null) {
                ss.borrarDatos();
                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("catastro/avaluos");
                ss.setNombreReporte("detalleCalculoEmision");
                ss.agregarParametro("LOGO_FOOTER", path + SisVars.sisLogo1);
                ss.agregarParametro("LOGO", path + SisVars.logoReportes);
                ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
                ss.agregarParametro("ID_AVAL_HISTORICO", avalHistorico.getId());
                ss.agregarParametro("USUARIO", sess.getName_user());
                JsfUti.redirectNewTab("/sgmEE/Documento");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public void redirectFichaPredial(CatPredio predio) {
        if (predio == null) {
            JsfUti.messageInfo(null, "Informacion", "No se encontro predio.");
        }
        if (predio.getId() == null) {
            JsfUti.messageInfo(null, "Informacion", "No se encontro predio.");
        }

        JsfUti.redirectNewTab(SisVars.urlbaseFaces + "vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml?predio=" + predio.getId());
//        JsfUti.redirectNewTab(SisVars.urlbaseFaces + "vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml?claveCat=" + predio.getClaveCat());
    }

    public void redirecGeoportal() {

        JsfUti.redirectNewTab(SisVars.URLPLANOIMAGENPREDIO + "/geoportal");

    }

    public void updateTableDocument() {
        ConfigFichaPredial cfp = new ConfigFichaPredial();
        if (cfp.getRedenerFichaIb()) {
            updateTableDocumento = "tdatos:docAdj";
        } else {
            updateTableDocumento = "";
        }

    }

    public void saveCensalService() {
        try {
            if (predio.getCatPredioS6() != null) {
                this.servicios = predio.getCatPredioS6();
                this.servicios.setPredio(predio);
            } else {
                this.servicios = new CatPredioS6();
                this.servicios.setPredio(predio);
            }
        } catch (NumberFormatException ne) {
            LOG.log(Level.SEVERE, "Obtener datos ficha saveCensalService", ne);
        }
        try {
            listarViaseInstalacionesEspeciales();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Llenar saveCensalService", e);
        }

        try {
            this.guardarServicios(this.servicios, null, this.instalacionesEspeciales, this.enteHorizontal);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Guardar saveCensalService", e);
        }

    }

    public Boolean esPh() {
        return predio.getBloque() > 0;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public CatPredioS12 getUsos() {
        return usos;
    }

    public void setUsos(CatPredioS12 usos) {
        this.usos = usos;
    }

    public List<RegMovimientoFicha> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<RegMovimientoFicha> movimientos) {
        this.movimientos = movimientos;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public HistoricoPredioLazy getHistorico() {
        return historico;
    }

    public void setHistorico(HistoricoPredioLazy historico) {
        this.historico = historico;
    }

    public PePermisoLazy getPermisosconst() {
        return permisosconst;
    }

    public void setPermisosconst(PePermisoLazy permisosconst) {
        this.permisosconst = permisosconst;
    }

    public List<CtlgItem> getUsosA() {
        return usosA;
    }

    public void setUsosA(List<CtlgItem> usosA) {
        this.usosA = usosA;
    }

    public List<FotoPredio> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoPredio> fotos) {
        this.fotos = fotos;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public CtlgItem getTipoPrototipos() {
        return tipoPrototipos;
    }

    public void setTipoPrototipos(CtlgItem tipoPrototipos) {
        this.tipoPrototipos = tipoPrototipos;
    }

    public List<CtlgItem> getPrototipos() {
        return prototipos;
    }

    public void setPrototipos(List<CtlgItem> prototipos) {
        this.prototipos = prototipos;
    }

    public PeInspeccionFinalLazy getInspecciones() {
        return inspecciones;
    }

    public void setInspecciones(PeInspeccionFinalLazy inspecciones) {
        this.inspecciones = inspecciones;
    }

    public InspeccionFinalConsulta getInspFinal() {
        return inspFinal;
    }

    public void setInspFinal(InspeccionFinalConsulta inspFinal) {
        this.inspFinal = inspFinal;
    }

    public List<SectorValorizacion> getSubsectores() {
        return subsectores;
    }

    public void setSubsectores(List<SectorValorizacion> subsectores) {
        this.subsectores = subsectores;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public List<CtlgItem> getVias() {
        return vias;
    }

    public void setVias(List<CtlgItem> vias) {
        this.vias = vias;
    }

    public CatEscrituraLazy getEscrituras() {
        return escrituras;
    }

    public void setEscrituras(CatEscrituraLazy escrituras) {
        this.escrituras = escrituras;
    }

    public List<CatCanton> getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CatEscritura getEscr() {
        return escr;
    }

    public void setEscr(CatEscritura escr) {
        this.escr = escr;
    }

    public Boolean getControlAddUp() {
        return controlAddUp;
    }

    public void setControlAddUp(Boolean controlAddUp) {
        this.controlAddUp = controlAddUp;
    }

    public List<CtlgItem> getInstalacionesEspeciales() {
        return instalacionesEspeciales;
    }

    public void setInstalacionesEspeciales(List<CtlgItem> instalacionesEspeciales) {
        this.instalacionesEspeciales = instalacionesEspeciales;
    }

    public CatEnteLazy getResponsablesLazy() {
        return responsablesLazy;
    }

    public void setResponsablesLazy(CatEnteLazy responsablesLazy) {
        this.responsablesLazy = responsablesLazy;
    }

    public CatEnte getActualizadorPredio() {
        return actualizadorPredio;
    }

    public void setActualizadorPredio(CatEnte actualizadorPredio) {
        this.actualizadorPredio = actualizadorPredio;
    }

    public CatEnte getFiscalizadorPredio() {
        return fiscalizadorPredio;
    }

    public void setFiscalizadorPredio(CatEnte fiscalizadorPredio) {
        this.fiscalizadorPredio = fiscalizadorPredio;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public CatEnte getEnteHorizontal() {
        return enteHorizontal;
    }

    public void setEnteHorizontal(CatEnte enteHorizontal) {
        this.enteHorizontal = enteHorizontal;
    }

    public String getElementosConstuctivos() {
        return elementosConstuctivos;
    }

    public void setElementosConstuctivos(String elementosConstuctivos) {
        this.elementosConstuctivos = elementosConstuctivos;
    }

    public String getPredioId() {
        return predioId;
    }

    public void setPredioId(String predioId) {
        this.predioId = predioId;
    }

    public CatPredio getPredioRaiz() {
        if (predio == null) {
            return null;
        }
        if (predio.getPredioRaiz() != null) {
            return (CatPredio) EntityBeanCopy.clone(em.find(CatPredio.class, predio.getPredioRaiz().longValue()));
        }
        return null;
    }

    public List<CatTiposDominio> getDominios() {
        return em.findAllObjectOrder(CatTiposDominio.class, new String[]{"nombre"}, true);
    }

    public Boolean getCoopropietarios() {
        return coopropietarios;
    }

    public void setCoopropietarios(Boolean coopropietarios) {
        this.coopropietarios = coopropietarios;
    }

    public CatPredioFusionDivision getCatPredioFusionDivision() {
        return catPredioFusionDivision;
    }

    public void setCatPredioFusionDivision(CatPredioFusionDivision catPredioFusionDivision) {
        this.catPredioFusionDivision = catPredioFusionDivision;
    }

    public Boolean getPh() {
        return ph;
    }

    public void setPh(Boolean ph) {
        this.ph = ph;
    }

    @Override
    public void setMainConfig(MainConfig mainConfig) {
        super.setMainConfig(mainConfig); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MainConfig getMainConfig() {
        return super.getMainConfig(); //To change body of generated methods, choose Tools | Templates.
    }

    public void updatePredio() {
        System.out.println("Info:" + predio.getTipoPoseedor().getValor());
    }

    public String getObservacionEliminar() {
        return observacionEliminar;
    }

    public void setObservacionEliminar(String observacionEliminar) {
        this.observacionEliminar = observacionEliminar;
    }

    public Object getObjectoEliminar() {
        return objectoEliminar;
    }

    public void setObjectoEliminar(Object objectoEliminar) {
        this.objectoEliminar = objectoEliminar;
    }

    public void eliminarObs() {
        if (observacionEliminar == null) {
            JsfUti.messageWarning(null, "Advertencia", "Debe ingresar la observación");
            return;
        }
        if (getObjectoEliminar() instanceof CatPredioPropietario) {
            CatPredioPropietario pp = (CatPredioPropietario) getObjectoEliminar();
            pp.setObservaciones(observacionEliminar);
            this.eliminarPropietario(pp);
            JsfUti.update("tdatos:frmPropietarios:dtPropietarios");
        } else if (getObjectoEliminar() instanceof CatPredioEdificacion) {
            CatPredioEdificacion cb = (CatPredioEdificacion) getObjectoEliminar();
            cb.setObservaciones(observacionEliminar);
            this.eliminarBloque(cb);
            JsfUti.update("tdatos:frmEdificaciones:tvEdificaciones:dtBloques");
            JsfUti.update("tdatos:frmViviendaCensal:btnS10");
            JsfUti.update("tdatos:frmViviendaCensal:field1");
            JsfUti.update("tdatos:frmViviendaCensal:fieldJH");
            JsfUti.update("tdatos:frmViviendaCensal:fieldNV");
            JsfUti.update("tdatos:frmViviendaCensal:pngObsVn");
        }
        JsfUti.executeJS("PF('dlgConfirmarEliminacion').hide()");
    }

    public void observacionesEliminar() {

    }

    public void observacionesEliminar(Integer tipoEliminar) {
        this.tipoEliminar = tipoEliminar;
        this.observacionEliminar = null;
        JsfUti.executeJS("PF('dlgConfirmarEliminacion').show()");
        JsfUti.update("frmConfirmarEliminacion");
    }

    public List<CatPredioAvalHistorico> getAvaluosHistoricosPredio() {
        return avaluosHistoricosPredio;
    }

    public void setAvaluosHistoricosPredio(List<CatPredioAvalHistorico> avaluosHistoricosPredio) {
        this.avaluosHistoricosPredio = avaluosHistoricosPredio;
    }

    public void calcularFondoRelativo() {
        if (caracteristicas == null) {
            System.out.println("caracteristicas es nullo.");
            return;
        }
        if (caracteristicas.getFrente1() == null) {
            JsfUti.messageInfo(null, "Debe ingresar el frente.", "");
            return;
        }
        if (predio.getAreaSolar() == null) {
            System.out.println("El area del solar es cero.");
            return;
        }
        if (caracteristicas.getAreaGraficaLote() == null) {
            caracteristicas.setFondo1(predio.getAreaSolar().divide(caracteristicas.getFrente1(), RoundingMode.HALF_UP));
        } else {
            caracteristicas.setFondo1(caracteristicas.getAreaGraficaLote().divide(caracteristicas.getFrente1(), RoundingMode.HALF_UP));
        }
    }

//    public void changeTitlePredialName() {
//        enteComprador = new CatEnte();
//        Integer tamanioList = 0;
//
//        for (CatPredioPropietario cpp : propietarios) {
//            if (cpp.getAfectaNombreTitulo()) {
//                tamanioList = +1;
//            }
//        }
//        for (CatPredioPropietario cpp : propietarios) {
//            if (tamanioList == 1) {
//                if (cpp.getAfectaNombreTitulo()) {
//                    enteComprador = cpp.getEnte();
//                }
//            } else {
//                if (cpp.getAfectaNombreTitulo()) {
//                    nombreComprador = cpp.getEnte().getEsPersona() ? cpp.getEnte().getApellidos() + " " + cpp.getEnte().getNombres()
//                            : cpp.getEnte().getRazonSocial();
//                }
//
//            }
//        }
//        RenLiquidacion liq = catas.getLiquidacionByPredio(this.predio);
//        if (liq != null) {
//            liq.setNombreComprador(null);
//            liq.setComprador(null);
//            if (tamanioList == 1) {
//                liq.setComprador(enteComprador);
//            } else {
//                liq.setNombreComprador(nombreComprador);
//            }
//            recaudacion.editarLiquidacion(liq);
//            JsfUti.messageInfo(null, "Cambios Realizados Correctamente", "");
//        } else {
//            JsfUti.messageInfo(null, "Predio No Posee Emisiones", "");
//        }
//
//    }
    public void changeTitlePredialName() {
        enteComprador = new CatEnte();
        nombreComprador = "";
        Integer tamanioList = 0;
        String ciRuc = "";
        RenLiquidacion liq = catas.getLiquidacionByPredio(this.predio);
        if (liq == null) {
            JsfUti.messageInfo(null, "Predio No Posee Emisiones", "");
            return;
        }
        if (propietariosTitulos.size() == 1) {
            tamanioList = +1;
        }
        for (CatPredioPropietario cpp : propietariosTitulos) {
            if (tamanioList == 1) {
                enteComprador = cpp.getEnte();
            } else {
                nombreComprador = cpp.getEnte().getEsPersona() ? nombreComprador + " " + cpp.getEnte().getApellidos() + " " + cpp.getEnte().getNombres()
                        : nombreComprador + " " + cpp.getEnte().getRazonSocial();
                ciRuc = ciRuc + " " + cpp.getEnte().getCiRuc();
            }
        }
        liq.setNombreComprador(null);
        liq.setComprador(null);
        if (tamanioList == 1) {
            liq.setComprador(enteComprador);
            for (RenPago rp : liq.getRenPagoCollection()) {
                rp.setContribuyente(enteComprador);
                rp.setNombreContribuyente(nombreComprador);
                em.persist(rp);
            }
        } else {
            liq.setNombreComprador(nombreComprador);
            for (RenPago rp : liq.getRenPagoCollection()) {
                rp.setNombreContribuyente(nombreComprador);
                em.persist(rp);
            }
        }
        propietariosTitulos = new ArrayList<>();
        recaudacion.editarLiquidacion(liq);
        JsfUti.messageInfo(null, "Cambios Realizados Correctamente", "");
    }

    public void completarEdicion() {

    }

    public Boolean getEsTarea() {
        return esTarea;
    }

    public void setEsTarea(Boolean esTarea) {
        this.esTarea = esTarea;
    }

    public void aprobar() {

    }

    public void rechazar() {

    }

    public Boolean getEsTareaAp() {
        return esTareaAp;
    }

    public void setEsTareaAp(Boolean esTareaAp) {
        this.esTareaAp = esTareaAp;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
    }

    public String getUpdateTableDocumento() {
        return updateTableDocumento;
    }

    public void setUpdateTableDocumento(String updateTableDocumento) {
        this.updateTableDocumento = updateTableDocumento;
    }

    public Short getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(Short codProvincia) {
        this.codProvincia = codProvincia;
    }

    public String getObservacionRestriccion() {
        return observacionRestriccion;
    }

    public void setObservacionRestriccion(String observacionRestriccion) {
        this.observacionRestriccion = observacionRestriccion;
    }

    public List<String> getObservacionRestricciones() {
        return observacionRestricciones;
    }

    public void setObservacionRestricciones(List<String> observacionRestricciones) {
        this.observacionRestricciones = observacionRestricciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(int tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public void guardarObservaciones() {

    }

    public String getUpdateScripture() {
        return ":tdatos:frmEscrituras";
    }

    public String getUpdateLinderos() {
        return "tdatos:frmLinderos";
    }

    public void setUpdateScripture(String valor) {

    }

    public void showDialogObservaciones(int tipoTarea) {

    }

    public void updateNombreTitulo() {
        System.out.println("Nombre del titulo: " + (predio.getNombreCambiado() != null ? predio.getNombreCambiado() : ""));
        if (!Objects.equals(predio.getCambioNombreTitulo(), Boolean.TRUE)) {
            predio.setNombreCambiado(null);
        }
    }

    public BigDecimal getValorMetro2() {
        return valorMetro2;
    }

    public void setValorMetro2(BigDecimal valorMetro2) {
        this.valorMetro2 = valorMetro2;
    }

    public String getNombrePropietarioExtras() {
        return nombrePropietarioExtras;
    }

    public void setNombrePropietarioExtras(String nombrePropietarioExtras) {
        this.nombrePropietarioExtras = nombrePropietarioExtras;
    }

    public List<CatPredioPropietario> getPropietariosTitulos() {
        return propietariosTitulos;
    }

    public void setPropietariosTitulos(List<CatPredioPropietario> propietariosTitulos) {
        this.propietariosTitulos = propietariosTitulos;
    }

}
