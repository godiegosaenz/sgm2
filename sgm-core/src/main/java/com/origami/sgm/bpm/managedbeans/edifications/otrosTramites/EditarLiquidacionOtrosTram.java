package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.BaseCalculoOtrosTramites;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EditarLiquidacionOtrosTram implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditarLiquidacionOtrosTram.class.getName());

    @Inject
    private ServletSession servletSession;

    @Inject
    private ReportesView reportes;

    @Inject
    private UserSession uSession;

    @Inject
    private BpmManageBeanBase base;

    @javax.inject.Inject
    private Entitymanager services;
    @javax.inject.Inject
    protected PropiedadHorizontalServices servicesPH;

    @javax.inject.Inject
    protected SeqGenMan secuencia;

    private PePermiso pePermiso = null;
    private List<PePermiso> pePermisoListTemp;
    private PePermisoLazy permisosList;
    private CatPredio datosPredio;
    private HistoricoTramites ht;
    private PdfReporte reporte;
    private HistoricoTramiteDet htd;
    private Boolean clickInspeccion = true;
    private AclUser usuario;
    private CatEnte solicitante, responsableTec;
    private CatCanton canton;
    private CatParroquia parroquia;
    private List<CatPredioPropietario> lisPropietarios;
    private String codigoCatastral, cedulaRucResp;
    private List<CatEnte> enteList;
    private String cedulaRuc;
    private List<BaseCalculoOtrosTramites> baseCalculoList;
    private BaseCalculoOtrosTramites baseSeleccionada;
    private BigDecimal valor;
    private BigDecimal total;
    private String descripcion, nota;
    private PeFirma firma;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private Boolean mostrarLista;
    private OtrosTramites oTramite;
    private OtrosTramitesHasPermiso othp;
    private Long idHtd;

    @PostConstruct
    public void init() {
        try {
            if (uSession.esLogueado()) {
            
                if (servletSession.getParametros() != null) {
                    if (servletSession.getParametros().get("tramite") == null) {
                        JsfUti.messageError(null, "", "No se ha encontrado información de Liquidación.");
                        servletSession.borrarParametros();
                        base.continuar();
                    }

                    iniciarVariables();
                    idHtd = (Long) servletSession.getParametros().get("tramite");
                    llenarVariables();
                    cedulaRucResp = "";
                    gutil = new GroovyUtil(formula.getFormula());

                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error.", e);
        }
    }

    /**
     * Inicializa las vcariables
     */
    private void iniciarVariables() {
        htd = new HistoricoTramiteDet();
        oTramite = new OtrosTramites();
    }

    /**
     * Llena las variables para la vista
     */
    private void llenarVariables() {
        htd = servicesPH.getHistoricoTramiteDetById(idHtd);
        usuario = servicesPH.getPermiso().getAclUserByUser(uSession.getName_user());
        ht = servicesPH.getPermiso().getHistoricoTramiteById(htd.getTramite().getId());

        if (ht != null) {
            oTramite = ht.getSubTipoTramite();
            othp = ht.getOtrosTramitesHasPermiso();
            descripcion = othp.getDescripcion();
            //baseSeleccionada = (BaseCalculoOtrosTramites)EntityBeanCopy.clone(oTramite.getBaseCalculo());
            pePermisoListTemp = (List<PePermiso>) ht.getPePermisoCollection();
            if (pePermisoListTemp != null && !pePermisoListTemp.isEmpty()) {
                pePermiso = pePermisoListTemp.get(0);
            }
            if(ht.getSubTipoTramite().getNecesitaPredio()){
                if(ht.getNumPredio()!=null){
                    datosPredio = (CatPredio) services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                    this.buscarPredio();
                }
            }
            
            solicitante = ht.getSolicitante();
        }
        baseCalculoList = services.findAllEntCopy(Querys.getBaseCalculoOT, new String[]{}, new Object[]{});
        firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
        formula = (MatFormulaTramite) services.find(MatFormulaTramite.class, 28L);
    }

    /**
     * Busca el predio según su código predial. Asimismo carga sus datos, como
     * los propietarios, etc.
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;

        if ((CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()}) != null) {
            datosPredio = (CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
            if (datosPredio.getNumPredio() != null) {
                parroquia = datosPredio.getCiudadela().getCodParroquia();
                canton = parroquia.getIdCanton();
                ht.setNumPredio(datosPredio.getNumPredio());
                JsfUti.messageInfo(null, "Info", "Predio encontrado.");
            } else {
                JsfUti.messageError(null, "Error", "El predio encontrado no tiene número de predio.");
                return;
            }
        } else {
            JsfUti.messageError(null, "Error", "No se encontró el predio.");
            return;
        }
        lisPropietarios = new ArrayList<CatPredioPropietario>();
        codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

        propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

        for (CatPredioPropietario temp : propTemp) {
            if (temp.getEstado().equals("A")) {
                lisPropietarios.add(temp);
            }
        }
    }

    /**
     * Elimina un propietario de la lista de propietarios de un predio.
     *
     * @param prop
     */
    public void eliminarPropietario(CatPredioPropietario prop) {
        if (prop.getId() != null) {
            prop.setEstado("I");
            services.update(prop);
        }
        lisPropietarios.remove(prop);
    }
    
    public void buscarResponsable(){
        try{
            responsableTec = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
            /*if(responsableTec==null)
                JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
            else{
                htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());               
                JsfUti.executeJS("PF('dlgResp').show()");  
                JsfUti.update("respDlgForm");
            }*/
            if(responsableTec != null)
                JsfUti.messageInfo(null, "Info", "Ente encontrado");
            else
                JsfUti.messageInfo(null, "Info", "Ente no encontrado");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Cambia de valor a una variable booleana que controla si muestra o no la
     * lista de PePermiso en el facelete.
     *
     */
    public void mostrarLista() {
        mostrarLista = true;
    }

    /**
     * Al seleccionar un permiso de la lista de permisos, se procede a cargar
     * todos sus datos en memoria y exponerlos al usuario.
     *
     * @param permiso
     */
    public void onRowSelectPermiso(PePermiso permiso) {
        String pagado = null;
        HistoricoTramites htTemp = null;
        mostrarLista = false;
        try {
            if (permiso.getTramite() != null) {
                htTemp = (HistoricoTramites) services.findNoProxy(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{permiso.getTramite().getId()});
            }

            if (htTemp != null) {
                JsfUti.messageInfo(null, "Info", "Trámite asociado encontrado.");
                if (htTemp.getEstado().equals("finalizado") || htTemp.getEstado().equals("Finalizado")) {
                    pePermiso = permiso;
                    JsfUti.update("frmMain");
                    JsfUti.messageInfo(null, "Info", "Tasa cancelada.");
                    JsfUti.messageInfo(null, "Info", "Se cargó la información del permiso.");
                    if (datosPredio == null) {
                        JsfUti.messageInfo(null, "Info", "Trámite no asociado a ningún predio.");
                    } else {
                        JsfUti.messageInfo(null, "Info", "Predio encontrado.");
                    }
                } else {
                    JsfUti.messageError(null, "Error", "Hasta el Momento el Usuario aún NO CANCELA su Tasa de Liquidación.");
                }
            } else {
                JsfUti.messageError(null, "Error", "Trámite asociado no encontrado.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error.", e);
            LOG.log(Level.SEVERE, "Error.", e);
        }
    }

    /**
     * Genera el pdf según la información ingresada por el usuario.
     *
     */
    public void imprimirPDF() {
        try {

            if (othp.getValor() == null || othp.getDescripcion() == null) {
                JsfUti.messageError(null, "Error", "Faltan datos que debe ingresar.");
                return;
            }

            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.instanciarParametros();

            gutil.setProperty("avaluo", this.othp.getValor());
            if (baseSeleccionada == null) {
                gutil.setProperty("base", 1000);
            } else {
                gutil.setProperty("base", this.baseSeleccionada.getValorBase());
            }
            gutil.getExpression("getTotal", null);
            total = (BigDecimal) gutil.getProperty("total");
            total = total.setScale(2, RoundingMode.HALF_UP);
            valor = BigDecimal.valueOf(Double.parseDouble(this.othp.getValor() + "")).setScale(2, RoundingMode.HALF_UP);
            //othp.setValor(total.floatValue());
            ht.setValorLiquidacion(total);
            htd.setTotal(total);
            services.update(ht);
            services.update(htd);
            services.update(othp);

            if (baseSeleccionada != null) {
                oTramite.setBaseCalculo(baseSeleccionada);
            }
            switch (Integer.parseInt(oTramite.getTipoSeleccion() + "")) {
                case 3:
                    servletSession.agregarParametro("sel3", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 4:
                    servletSession.agregarParametro("sel4", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 6:
                    servletSession.agregarParametro("sel6", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 7:
                    servletSession.agregarParametro("sel7", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 8:
                    servletSession.agregarParametro("sel8", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 9:
                    servletSession.agregarParametro("sel9", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 10:
                    servletSession.agregarParametro("sel3", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 11:
                    servletSession.agregarParametro("sel4", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 12:
                    servletSession.agregarParametro("sel5", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
            }
            othp.setPePermiso(pePermiso);
            services.update(oTramite);

            if (solicitante!=null && solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
                servletSession.agregarParametro("nomSolicitante", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getRazonSocial().toUpperCase());
                servletSession.agregarParametro("nomSolicitante", solicitante.getRazonSocial());
            }

            
            servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
            servletSession.agregarParametro("numReporte", ht.getNumTramiteXDepartamento());

            if (datosPredio != null) {
                servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                servletSession.agregarParametro("mz", datosPredio.getMz());
                servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                servletSession.agregarParametro("codCatastral", datosPredio.getCodigoPredial().toUpperCase());
                servletSession.agregarParametro("urb", datosPredio.getCiudadela().getNombre().toUpperCase());
                servletSession.agregarParametro("solar", datosPredio.getSolar());
                servletSession.agregarParametro("codCatAnt", datosPredio.getPredialant());
            }else{
                servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                servletSession.agregarParametro("mz",ht.getMz());
                servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                servletSession.agregarParametro("codCatastral", null);
                servletSession.agregarParametro("urb", ht.getUrbanizacion() != null ? ht.getUrbanizacion().getNombre() : null);
                servletSession.agregarParametro("solar",ht.getSolar());
            }

            CatEnte r=(CatEnte)services.find(CatEnte.class, new Long(othp.getResponsableTec()+""));
            servletSession.agregarParametro("ciResponsable", r.getCiRuc());
            servletSession.agregarParametro("responsable",  r.getNombres()+" "+r.getApellidos());
            servletSession.agregarParametro("regProf", r.getRegProf());
            servletSession.agregarParametro("presupuesto", ht.getValorLiquidacion() + "");
            servletSession.agregarParametro("numTramite", ht.getId() + "-" + new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.agregarParametro("descripcion", othp.getDescripcion().toUpperCase());
            servletSession.agregarParametro("descripcionTramite", ht.getTipoTramiteNombre());
            if (baseSeleccionada == null) {
                servletSession.agregarParametro("base1", "***");
            } else {
                servletSession.agregarParametro("base1", baseSeleccionada.getValorBase() + "%");
            }
            servletSession.agregarParametro("base2", "***");
            servletSession.agregarParametro("total", total + "");
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto().toUpperCase());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase() + " " + firma.getDepartamento());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            if(oTramite.getNecesitaPredio())
                servletSession.setNombreReporte("TasaLiquidacionOT");
            else
                servletSession.setNombreReporte("TasaLiquidacionOTSinPredio");
                
            servletSession.setNombreSubCarpeta("otrosTramites");
            servletSession.setTieneDatasource(false);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "Error.", e);
        }
    }
    
    public void calcularTotal(){
        /*
        gutil.setProperty("avaluo", this.othp.getValor());
        if(baseSeleccionada==null)
                gutil.setProperty("base", 1000);
        else
            gutil.setProperty("base", this.baseSeleccionada.getValorBase());                
        gutil.getExpression("getTotal", null);
        total = (BigDecimal)gutil.getProperty("total");
        total =total.setScale(2, RoundingMode.HALF_UP);
                */
        gutil.setProperty("avaluo", this.othp.getValor());
        if(baseSeleccionada==null)
                gutil.setProperty("base", 1);
        else
            gutil.setProperty("base", this.baseSeleccionada.getValorBase()); 
        gutil.setProperty("divisor", this.oTramite.getFactor());
        gutil.getExpression("getTotal", null);
        total = (BigDecimal)gutil.getProperty("total");
        total =total.setScale(2, RoundingMode.HALF_UP);
    }
    
    public void continuar(){
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/otrosTramites/editarLiquidacionOtrosTram.xhtml");
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<BaseCalculoOtrosTramites> getBaseCalculoList() {
        return baseCalculoList;
    }

    public void setBaseCalculoList(List<BaseCalculoOtrosTramites> baseCalculoList) {
        this.baseCalculoList = baseCalculoList;
    }

    public BaseCalculoOtrosTramites getBaseSeleccionada() {
        return baseSeleccionada;
    }

    public void setBaseSeleccionada(BaseCalculoOtrosTramites baseSeleccionada) {
        this.baseSeleccionada = baseSeleccionada;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public PePermisoLazy getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(PePermisoLazy permisosList) {
        this.permisosList = permisosList;
    }

    public Boolean getMostrarLista() {
        return mostrarLista;
    }

    public void setMostrarLista(Boolean mostrarLista) {
        this.mostrarLista = mostrarLista;
    }

    public OtrosTramites getoTramite() {
        return oTramite;
    }

    public void setoTramite(OtrosTramites oTramite) {
        this.oTramite = oTramite;
    }

    public OtrosTramitesHasPermiso getOthp() {
        return othp;
    }

    public void setOthp(OtrosTramitesHasPermiso othp) {
        this.othp = othp;
    }

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

    public String getCedulaRucResp() {
        return cedulaRucResp;
    }

    public void setCedulaRucResp(String cedulaRucResp) {
        this.cedulaRucResp = cedulaRucResp;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public CatEnte getResponsableTec() {
        return responsableTec;
    }

    public void setResponsableTec(CatEnte responsableTec) {
        this.responsableTec = responsableTec;
    }
    
}
