package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 * Edita los valores de la tasa de liquidacón de los trámites de División de
 * Predio, Propiedad Horizontal, Otros Tramites, Resellado de Planos y Permisos
 * Adicionales.
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class EditarLiquidacionHTD implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditarLiquidacionHTD.class.getName());

    @javax.inject.Inject
    protected PropiedadHorizontalServices services;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;
    @Inject
    private BpmManageBeanBase base;

    protected Boolean isPropiedadHorizontal = true;
    protected Boolean impresion = false;
    protected Boolean guardar = true;
    protected Boolean impresoLiquidacion = false;
    protected Boolean dashBoard = false;
    protected Boolean areaEdi = true;
    protected Boolean avaluoConst = true;
    protected Boolean avaluoSolar = true;
    protected Boolean avaluoProp = true;
    protected String representanteLegal;
    protected String observacion;
    protected String realizadorPor;
    protected Long idHtd;

    protected MsgFormatoNotificacion ms;
    protected AclUser user;
    protected HistoricoTramites ht;
    protected GeTipoTramite tramite;
    protected CatPredio predio;
    protected CatEnte propietarioNuevo;
    protected HistoricoTramiteDet liquidacion;
    protected CatCanton canton;
    protected CatParroquia parroquia;
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar;
    protected List<CatCanton> catCantonsList;
    protected List<CatParroquia> catParroquiaList;
    protected CatEnteLazy enteLazy;
    private Long tipoLiq;

    /*
     Estas dos variables son para obtener la formulas de calculo.
     */
    protected GroovyUtil groovyUtil;
    protected MatFormulaTramite formulas;

    @PostConstruct
    public void initView() {
        if (ss != null && ss.getParametros() != null) {
            if (ss.getParametros().get("tramite") == null) {
                JsfUti.messageError(null, "", "No se ha encontrado información de Liquidación.");
                ss.borrarParametros();
                base.continuar();
//                JsfUti.redirectFaces("vistaprocesos/edificaciones/divisionPredio/divisionPredioConsulta.xhtml");
            }
            idHtd = (Long) ss.getParametros().get("tramite");
            
            try {
                iniciarVariables();
                llenarVariables();
                if (liquidacion != null) {
                    predio = liquidacion.getPredio();
                    if (predio != null) {
                        listaPropietarios = llenarPropietarios(predio.getCatPredioPropietarioCollection());
                        llenarParroquia();
                    }
                }
                llenarRealizado();
                formulas = services.getPermiso().getMatFormulaTramite(tramite.getId());
                groovyUtil = new GroovyUtil(formulas.getFormula());
                habilitarCampo(tramite);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }
            //            liquidacion.setBaseCalculo(((BigDecimal) groovyUtil.getProperty("baseCalculo")));
        } else {
            base.continuar();
        }
    }

    /**
     * Inicializa las variables.
     */
    private void iniciarVariables() {
        catCantonsList = new ArrayList<>();
        catParroquiaList = new ArrayList<>();
        listaPropietarios = new ArrayList<>();
        listaPropietariosEliminar = new ArrayList<>();
        predio = new CatPredio();
        ht = new HistoricoTramites();
        liquidacion = new HistoricoTramiteDet();
        tramite = new GeTipoTramite();
        canton = new CatCanton();
        parroquia = new CatParroquia();
        user = new AclUser();
    }

    /**
     * Llena los lista y consulta los datos de la tabla
     * {@link HistoricoTramiteDet}
     */
    private void llenarVariables() {
        canton = services.getCatCantonById(1L);
        catCantonsList = services.getInscripcion().getCatCantonList();
        catParroquiaList = services.getFichaServices().getCatPerroquiasListByCanton(canton.getId());
        getListPeFirma();
        if (session != null) {
            user = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(session.getName_user()));
        }
        liquidacion = services.getHistoricoTramiteDetById(idHtd);
        ht = services.getPermiso().getHistoricoTramiteById(liquidacion.getTramite().getId());
        tipoLiq = ht.getTipoTramite().getId();
        tramite = services.getPermiso().getGeTipoTramiteById(ht.getTipoTramite().getId());
    }

    /**
     * Llena al responsable de edición
     */
    private void llenarRealizado() {
        if (user.getEnte() != null) {
            realizadorPor = ((user.getEnte().getApellidos() == null) ? "" : user.getEnte().getApellidos())
                    + " " + ((user.getEnte().getNombres() == null) ? "" : user.getEnte().getNombres());
        } else {
            realizadorPor = user.getUsuario();
        }
    }

    /**
     * Carga la lista de propietarios activos del predio.
     *
     * @param propietarioCollection lista de Propietarios.
     * @return Lista de Propietarios Activos.
     */
    private List<CatPredioPropietario> llenarPropietarios(Collection<CatPredioPropietario> propietarioCollection) {
        List<CatPredioPropietario> propietariosTemp = new ArrayList<>();
        if (Utils.isNotEmpty((List<?>) propietarioCollection)) {
            for (CatPredioPropietario temp : propietarioCollection) {
                if ("A".equals(temp.getEstado())) {
                    propietariosTemp.add(temp);
                }
            }
        }
        return propietariosTemp;
    }

    private void habilitarCampo(GeTipoTramite tipo) {
        switch (tipo.getId().intValue()) {
            case 7:
                avaluoProp = false;
                break;
            case 9:
                areaEdi = false;
                avaluoConst = false;
                avaluoSolar = false;
                avaluoProp = true;
                break;
            default:

                break;
        }
    }

    private void llenarParroquia() {
        if (predio.getCiudadela() != null) {
            parroquia = (CatParroquia) EntityBeanCopy.clone(predio.getCiudadela().getCodParroquia());
        } else {
            parroquia = (CatParroquia) EntityBeanCopy.clone(services.getCatParroquia(1L));
        }
        if (parroquia != null) {
            canton = (CatCanton) EntityBeanCopy.clone(predio.getCiudadela().getCodParroquia().getIdCanton());
        } else {
            parroquia = (CatParroquia) EntityBeanCopy.clone(Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1));
        }
    }

    public void renderPersNat() {
        propietarioNuevo = new CatEnte();
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgNuevPro').show()");
    }

    public void buscarPropietario() {
        if (propietarioNuevo.getCiRuc() != null) {
            try {
                CatEnte nuwEnt = services.getFichaServices().getCatEnte(propietarioNuevo.getCiRuc());
                if (nuwEnt != null) {
                    for (CatPredioPropietario listCat1 : listaPropietarios) {
                        if (listCat1.getEnte().getId().compareTo(nuwEnt.getId()) == 0) {
                            JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                            return;
                        }
                    }
                    CatPredioPropietario entP = new CatPredioPropietario();
                    propietarioNuevo = nuwEnt;
                    entP.setEnte(propietarioNuevo);
                    entP.setPredio(predio);
                    entP.setEstado("A");
                    entP.setModificado("Edificaciones");
                    listaPropietarios.add(entP);
                    propietarioNuevo = new CatEnte();
                    JsfUti.update("forGenLiq:dtProp");
                    JsfUti.executeJS("PF('dlgNuevPro').hide()");
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }
        }
    }

    public void seleccionarReprest() {
        enteLazy = new CatEnteLazy(true);
        JsfUti.update("formSelectInterv");
        JsfUti.executeJS("PF('dlgSelectReprest').show();");
    }

    public void agregarRepresentante(CatEnte represt) {
        propietarioNuevo.setRepresentanteLegal(new BigInteger(represt.getId().toString()));
        if (represt.getApellidos() != null && represt.getNombres() != null) {
            representanteLegal = represt.getApellidos() + " " + represt.getNombres();
        } else if (represt.getApellidos() != null) {
            representanteLegal = represt.getApellidos();
        } else if (propietarioNuevo.getNombres() != null) {
            representanteLegal = represt.getNombres();
        }
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgSelectReprest').hide()");
    }

    public void agregarPropietario() {
        if (propietarioNuevo != null) {
            try {
                if (!propietarioNuevo.getEsPersona()) {
                    if (representanteLegal == null) {
                        JsfUti.messageInfo(null, "Debe seleccionar el Representante Legal", "");
                        return;
                    }
                }
                CatEnte nuwEnt = null;
                if (propietarioNuevo.getCiRuc() != null) {
                    nuwEnt = services.getFichaServices().getCatEnte(propietarioNuevo.getCiRuc());
                }
                if (nuwEnt != null) {
                    for (CatPredioPropietario listCat1 : listaPropietarios) {
                        if (listCat1.getEnte().getCiRuc().compareTo(propietarioNuevo.getCiRuc()) == 0) {
                            JsfUti.messageInfo(null, "Ya fue agregado un propietario con el mismo número de documento", "");
                            return;
                        }
                    }
                }
                CatPredioPropietario entP = new CatPredioPropietario();
                entP.setEstado("A");
                entP.setEnte(propietarioNuevo);
                entP.setPredio(predio);
                entP.setModificado("edificacion");
                listaPropietarios.add(entP);
                propietarioNuevo = null;
                representanteLegal = "";
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }
            JsfUti.executeJS("PF('dlgNuevPro').hide()");
            JsfUti.update("frmLiquiHTD:dtProp");
        }
    }

    public void eliminarProp(CatPredioPropietario prop) {
        if (listaPropietarios.size() == 1) {
            JsfUti.messageError(null, "No puede Eliminar todos los propietarios", "");
            return;
        }
        prop.setModificado("edificacion");
        prop.setEstado("I");
        int index = 0;
        int i = 0;
        for (CatPredioPropietario listCat1 : listaPropietarios) {
            if (prop.getId() != null) {
                if (listCat1.getId().compareTo(prop.getId()) == 0) {
                    index = i;
                    break;
                }
            } else {
                if (listCat1.getEnte().getCiRuc().compareTo(prop.getEnte().getCiRuc()) == 0) {
                    index = i;
                    break;
                }
            }
            i++;
        }
        if (prop.getId() != null) {
            listaPropietariosEliminar.add(listaPropietarios.get(index));
        }
        listaPropietarios.remove(index);
        JsfUti.update("frmLiquiHTD:dtProp");
    }

    public void calcularAvaluoConstruccion() {
        try {
            BigDecimal avaluoConstruccion = null;
            if (liquidacion.getAreaEdificacion() != null) {
                switch (tramite.getId().intValue()) {
                    case 7:
                        groovyUtil.setProperty("areaEdif", liquidacion.getAreaEdificacion());
                        avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("getAvaluosConstruccion", null)).setScale(2, RoundingMode.HALF_UP);
                        break;
                    case 9:
                        groovyUtil.setProperty("liquidacionNueva", liquidacion);
                        avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("calcularAvaluoConstruccion", new Object[]{})).setScale(4, RoundingMode.HALF_UP);
                        //avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("totalPagar", new Object[]{})).setScale(2, RoundingMode.HALF_UP);
                    default:

                        break;
                }
                /*groovyUtil.setProperty("liquidacionNueva", liquidacion);
                BigDecimal avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("calcularAvaluoConstruccion", new Object[]{})).setScale(4, RoundingMode.UP);
                if (Utils.isDecimal(avaluoConstruccion.toString())) {
                    liquidacion.setAvaluoConstruccion(avaluoConstruccion);
                    avaluoPropiedad();
                }*/
                avaluoPropiedad();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void avaluoPropiedad() {
        try {
            if (liquidacion.getAvaluoPropiedad() != null) {
                BigDecimal total = null;
                BigDecimal calculoAvaluoPropiedad = null;
                switch (tramite.getId().intValue()) {
                    case 7:
                        groovyUtil.setProperty("avaluoP", liquidacion.getAvaluoPropiedad());
                        total = ((BigDecimal) groovyUtil.getExpression("getAvaluoTotal", null)).setScale(2, RoundingMode.HALF_UP);
                        break;
                    case 9:
                        groovyUtil.setProperty("liquidacionNueva", liquidacion);
                        calculoAvaluoPropiedad = ((BigDecimal) groovyUtil.getExpression("calculoAvaluoPropiedad", new Object[]{})).setScale(4, RoundingMode.HALF_UP);
                        total = ((BigDecimal) groovyUtil.getExpression("totalPagar", new Object[]{})).setScale(2, RoundingMode.HALF_UP);
                    default:

                        break;
                }
                liquidacion.setAvaluoEdificacion(calculoAvaluoPropiedad);
                liquidacion.setAvaluoPropiedad(calculoAvaluoPropiedad);
                liquidacion.setFecCre(new Date());
                liquidacion.setTotal(total);

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }
    
    public void getAvaluoTotal(){
        if(liquidacion.getAvaluoPropiedad()!=null){
            groovyUtil.setProperty("avaluoP", liquidacion.getAvaluoPropiedad());
            groovyUtil.getExpression("getAvaluoTotal", null);
            liquidacion.setTotal(((BigDecimal) groovyUtil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
        }
        return;
    }

    public List<PeFirma> getListPeFirma() {
        return services.getListPeFirma();
    }

    public void validar() {
        if (liquidacion.getFirma() == null) {
            JsfUti.messageInfo(null, "Debe Seleccionar Director(a) de Edificaciones: ", "");
            return;
        }
        if (liquidacion.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUti.messageInfo(null, "El valor de la liquidación no debe ser menor a cero.", "");
            return;
        }
        JsfUti.executeJS("PF('obs').show()");
    }

    public void guardarLiquidacion() {
        try {
            if (liquidacion.getFirma() == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Director(a) de Edificaciones: ", "");
                return;
            }
            if (observacion == null) {
                JsfUti.messageInfo(null, "Debe Ingresar las Observaciones para continuar", "");
                return;
            }
            ht.setValorLiquidacion(liquidacion.getTotal());
            liquidacion = services.modificarTasaLiquidacion(liquidacion, ht, listaPropietarios, listaPropietariosEliminar);
            if (liquidacion != null) {
                services.guardarObservaciones(ht, session.getName_user(), observacion, "Edicion Tasa Externo al Trámite.");
                guardar = false;
                impresion = true;
                llenarParroquia();
                JsfUti.messageInfo(null, "Información Modificada con exito", "");
            } else {
                JsfUti.messageInfo(null, "Error al intentar Modificar la Información", "");
            }
            ss.borrarParametros();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
        JsfUti.executeJS("PF('obs').hide()");
        JsfUti.update("frmLiquiHTD");
    }

    public void imprimir() {
        liquidacion = services.getHistoricoTramiteDetByTramite(ht.getIdTramite());
        HistoricoReporteTramite reporteAnterior = null;
        CatEnte solicitante = null;
        AclUser tecnico = services.getPermiso().getAclUserByUser(session.getName_user());
        CatPredio datPredio = null;
        Collection<HistoricoReporteTramite> deshabilitar = ht.getHistoricoReporteTramiteCollection();
        for (HistoricoReporteTramite ra : deshabilitar) {
            if (ra.getNombreReporte().contains("TasaLiq_DivisionPredio-") || 
                    ra.getNombreReporte().contains("PropiedadHorizontal")) {
                if (ra.getEstado()) {
                    reporteAnterior = ra;
                }
            }
        }
        if (ht != null) {
            solicitante = ht.getSolicitante();
        }
        datPredio = liquidacion.getPredio();
        llenarParroquia();
        try {
            switch (tramite.getId().intValue()) {
                case 7:
                    imprimirPDF(datPredio, tecnico, solicitante, reporteAnterior);
                    break;
                case 9:
                    imprimirPH(predio, user, propietarioNuevo, reporteAnterior);
                    break;

            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
        dashBoard = true;
        impresion = false;
        impresoLiquidacion = true;
        JsfUti.update("frmLiquiHTD");
    }

    /**
     * Genera lade liquidación de la división de predio.
     * @param datPredio
     * @param tecnico
     * @param solicitante
     * @param reporteAnterior
     *
     * @throws IOException
     */
    public void imprimirPDF(CatPredio datPredio, AclUser tecnico, CatEnte solicitante, HistoricoReporteTramite reporteAnterior) throws IOException {


        try {
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            ss.instanciarParametros();
            String lugarYFecha = parroquia.getDescripcion() + ", " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            liquidacion.setEstado(true);
            ss.agregarParametro("nombreCanton", "Gobierno Autónomo Descentralizado Municipal del Cantón " + canton.getNombre());
            ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("lugarYFecha", lugarYFecha);
            if (reporteAnterior != null) {
                if (reporteAnterior.getSecuencia() != null) {
                    ss.agregarParametro("numReporte", reporteAnterior.getSecuencia().longValue());
                }
                ss.agregarParametro("numValidador", reporteAnterior.getCodValidacion());
                ss.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + reporteAnterior.getCodValidacion());
            } else {
                ss.agregarParametro("numReporte", 0L);
                ss.agregarParametro("numValidador", "0");
                ss.agregarParametro("codigoQR", SisVars.urlServidorCompleta);
            }
            ss.agregarParametro("numeroTramite", ht.getId() + "-" + anio);
            ss.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));

            if (solicitante != null) {
                ss.agregarParametro("nombres", base.getNombrePropietario(solicitante));
                ss.agregarParametro("ciRuc", solicitante.getCiRuc());
            }

            ss.agregarParametro("canton", canton.getNombre());
            ss.agregarParametro("parroquia", parroquia.getDescripcion());
            if (datPredio.getCiudadela() != null) {
                ss.agregarParametro("urbanizacion", datPredio.getCiudadela().getNombre());
            } else {
                ss.agregarParametro("urbanizacion", "La Puntilla.");
            }

            ss.agregarParametro("calle", "Vehicular");
            ss.agregarParametro("codigoCatastral", datPredio.getCodigoPredialCompleto());
            ss.agregarParametro("mz", "" + datPredio.getMz());
            ss.agregarParametro("solar", "" + datPredio.getSolar());
            if (liquidacion.getAvaluoConstruccion() != null) {
                ss.agregarParametro("avaluoConstruccion", "" + liquidacion.getAvaluoConstruccion());
            }
            if (liquidacion.getAreaEdificacion() != null) {
                ss.agregarParametro("areaEdif", "" + liquidacion.getAreaEdificacion());
            }
            if (liquidacion.getAvaluoConstruccion() != null) {
                ss.agregarParametro("avaluoConstruccion", "" + liquidacion.getAvaluoConstruccion().toString());
            }
            if (liquidacion.getAvaluoSolar() != null) {
                ss.agregarParametro("avaluoSolar", "" + liquidacion.getAvaluoSolar().toString());
            }
            if (liquidacion.getAvaluoPropiedad() != null) {
                ss.agregarParametro("avaluoPropiedad", "" + liquidacion.getAvaluoPropiedad().toString());
            }
            ss.agregarParametro("descripcion", "" + liquidacion.getDescripcion());
            ss.agregarParametro("baseCalculo1", "" + liquidacion.getBaseCalculo());
            ss.agregarParametro("baseCalculo2", "");
            ss.agregarParametro("totalAPagar", "" + liquidacion.getTotal());
            ss.agregarParametro("nombreIng", liquidacion.getFirma().getNomCompleto());
            ss.agregarParametro("pisosSNB", liquidacion.getNumPisosSobreBord());
            ss.agregarParametro("pisosBNB", liquidacion.getNumPisosBajoBord());
            ss.agregarParametro("areaEdif", liquidacion.getAreaEdificacion());
            ss.agregarParametro("cargoIng", liquidacion.getFirma().getCargo() + " " + liquidacion.getFirma().getDepartamento());
            if (tecnico.getEnte() != null) {
                ss.agregarParametro("responsable", tecnico.getEnte().getNombres() + " " + tecnico.getEnte().getApellidos());
            } else {
                ss.agregarParametro("responsable", tecnico.getUsuario());
            }
            ss.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas/lilianaGuerrero.jpg"));
            ss.setNombreReporte("divisionPredioReporte");
            ss.setTieneDatasource(false);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al generar reporte.", e);
        }
    }

    public void imprimirPH(CatPredio datPredio, AclUser tecnico, CatEnte solicitante, HistoricoReporteTramite reporteAnterior) {
        try {
            Calendar cl = Calendar.getInstance();
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//"); ////PropiedadHorizontal.jasper

            HistoricoTramites ht1 = services.getPermiso().getHistoricoTramiteById(ht.getId());

            AclUser firmaDirector = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(tramite.getUserDireccion()));

            if (reporteAnterior != null) {
                ss.instanciarParametros();
                String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + reporteAnterior.getCodValidacion();
                ss.setNombreReporte("PropiedadHorizontal");
                ss.setNombreSubCarpeta("propiedadHorizontal");
                ss.setTieneDatasource(true);
                ss.agregarParametro("idimprimir", liquidacion.getId());
                ss.agregarParametro("firmaDirec", path + "/css/firmas/" + firmaDirector.getRutaImagen() + ".jpg");
                ss.agregarParametro("numrepor", reporteAnterior.getSecuencia() + "-" + cl.get(Calendar.YEAR));
                ss.agregarParametro("logo", path + SisVars.logoReportes);
                ss.agregarParametro("seleccionado", path + "/css/homeIconsImages/selecc.png");
                ss.agregarParametro("validador", reporteAnterior.getId().toString() + "" + ht.getIdProceso());
                ss.agregarParametro("codigoQR", codigoQR);
                ss.agregarParametro("realizadoPor", realizadorPor);
                ss.agregarParametro("anio", cl.get(Calendar.YEAR));

            }
            impresion = false;
            guardar = false;
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            dashBoard = true;
            impresoLiquidacion = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al generar Liquidación PH.", e);
        }
        JsfUti.update("frmLiquiHTD");
    }

    public void redirectDashBoard() {
        if (impresoLiquidacion) {
            base.continuar();
        } else {
            JsfUti.messageError(null, "Debe Imprimir Documento para Completar la Tarea.", "");
        }
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public Boolean getIsPropiedadHorizontal() {
        return isPropiedadHorizontal;
    }

    public void setIsPropiedadHorizontal(Boolean isPropiedadHorizontal) {
        this.isPropiedadHorizontal = isPropiedadHorizontal;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public CatEnte getPropietarioNuevo() {
        return propietarioNuevo;
    }

    public void setPropietarioNuevo(CatEnte propietarioNuevo) {
        this.propietarioNuevo = propietarioNuevo;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public EditarLiquidacionHTD() {
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
    }

    public List<CatParroquia> getCatParroquiaList() {
        return catParroquiaList;
    }

    public void setCatParroquiaList(List<CatParroquia> catParroquiaList) {
        this.catParroquiaList = catParroquiaList;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public Boolean getDashBoard() {
        return dashBoard;
    }

    public void setDashBoard(Boolean dashBoard) {
        this.dashBoard = dashBoard;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getImpresion() {
        return impresion;
    }

    public void setImpresion(Boolean impresion) {
        this.impresion = impresion;
    }

    public Boolean getGuardar() {
        return guardar;
    }

    public void setGuardar(Boolean guardar) {
        this.guardar = guardar;
    }

    public HistoricoTramiteDet getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(HistoricoTramiteDet liquidacion) {
        this.liquidacion = liquidacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getRealizadorPor() {
        return realizadorPor;
    }

    public void setRealizadorPor(String realizadorPor) {
        this.realizadorPor = realizadorPor;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

    public Boolean getAreaEdi() {
        return areaEdi;
    }

    public void setAreaEdi(Boolean areaEdi) {
        this.areaEdi = areaEdi;
    }

    public Boolean getAvaluoConst() {
        return avaluoConst;
    }

    public void setAvaluoConst(Boolean avaluoConst) {
        this.avaluoConst = avaluoConst;
    }

    public Boolean getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(Boolean avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public Boolean getAvaluoProp() {
        return avaluoProp;
    }

    public void setAvaluoProp(Boolean avaluoProp) {
        this.avaluoProp = avaluoProp;
    }

    public Long getTipoLiq() {
        return tipoLiq;
    }

    public void setTipoLiq(Long tipoLiq) {
        this.tipoLiq = tipoLiq;
    }

}
