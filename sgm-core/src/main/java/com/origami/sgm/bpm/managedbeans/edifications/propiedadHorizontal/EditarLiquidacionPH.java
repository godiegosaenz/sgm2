package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
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
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EditarLiquidacionPH extends BpmManageBeanBaseRoot implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditarLiquidacionPH.class.getName());

    @javax.inject.Inject
    protected PropiedadHorizontalServices services;
    @Inject
    private ServletSession ss;

    protected Boolean isPropiedadHorizontal = true;
    protected Boolean impresion = false;
    protected Boolean guardar = true;
    protected Boolean impresoLiquidacion = false;
    protected Boolean dashBoard = false;
    protected String representanteLegal;
    protected String observacion;
    protected String realizadorPor;

    protected HistoricoReporteTramite hr;
    protected MsgFormatoNotificacion ms;
    protected AclUser user;
    protected HistoricoTramites ht;
    protected GeTipoTramite tramite;
    protected CatPredio predio;
    protected CatEnte propietarioNuevo;
    protected HistoricoTramiteDet procesoPH;
    protected CatCanton canton;
    protected CatParroquia parroquia;
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar;
    protected List<CatCanton> catCantonsList;
    protected List<CatParroquia> catParroquiaList;
    protected CatEnteLazy enteLazy;

    /*
     Estas dos variables son para obtener la formulas de calculo.
     */
    protected GroovyUtil groovyUtil;
    protected MatFormulaTramite formulas;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                catCantonsList = new ArrayList<>();
                catParroquiaList = new ArrayList<>();
                predio = new CatPredio();
                ht = new HistoricoTramites();
                procesoPH = new HistoricoTramiteDet();
                tramite = new GeTipoTramite();
                canton = new CatCanton();
                user = new AclUser();
                hr = new HistoricoReporteTramite();
                this.setTaskId(session.getTaskID());
                canton = services.getCatCantonById(1L);
                ht = services.getPermiso().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                tramite = services.getPermiso().getGeTipoTramiteById(ht.getTipoTramite().getId());
                catCantonsList = services.getInscripcion().getCatCantonList();
                catParroquiaList = services.getFichaServices().getCatPerroquiasListByCanton(canton.getId());
                getListPeFirma();
                user = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(session.getName_user()));
//            reporte = services.getProcesoReportes(ht.getId());
                procesoPH = services.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                if (procesoPH != null) {
                    predio = procesoPH.getPredio();
                    listaPropietarios = new ArrayList<>();
                    listaPropietariosEliminar = new ArrayList<>();
                    List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
                    if (!propietariosTemp.isEmpty()) {
                        for (CatPredioPropietario temp : propietariosTemp) {
                            if ("A".equals(temp.getEstado())) {
                                listaPropietarios.add(temp);
                            }
                        }
                    }
                    parroquia = predio.getCiudadela().getCodParroquia();
                    if (parroquia != null) {
                        canton = parroquia.getIdCanton();
                    } else {
                        parroquia = Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1);
                    }
                    hr = services.getPermiso().getHistoricoTramiteDet(ht.getIdProceso(), true);
                    if (hr == null) {
                        hr = services.getPermiso().getHistoricoTramiteDet(ht.getIdProceso(), true);
                    }
                    if (user.getEnte() == null) {
                        realizadorPor = user.getUsuario();
                    } else {
                        realizadorPor = Utils.isEmpty(user.getEnte().getApellidos()) + " " + Utils.isEmpty(user.getEnte().getNombres());
                    }
                }
                formulas = services.getPermiso().getMatFormulaTramite(tramite.getId());
                groovyUtil = new GroovyUtil(formulas.getFormula());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }

//            procesoPH.setBaseCalculo(((BigDecimal) groovyUtil.getProperty("baseCalculo")));
        } else {
            this.continuar();
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
            JsfUti.update("formGenLiqPH:dtProp");
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
        JsfUti.update("formGenLiqPH:dtProp");
    }

    public void calcularAvaluoConstruccion() {
        try {
            if (procesoPH.getAreaEdificacion() != null) {
                groovyUtil.setProperty("liquidacionNueva", procesoPH);
                BigDecimal avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("calcularAvaluoConstruccion", new Object[]{})).setScale(4, RoundingMode.UP);
                if (Utils.isDecimal(avaluoConstruccion.toString())) {
                    procesoPH.setAvaluoConstruccion(avaluoConstruccion);
                    avaluoPropiedad();
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void avaluoPropiedad() {
        try {
            if (procesoPH.getAvaluoSolar() != null) {
                groovyUtil.setProperty("liquidacionNueva", procesoPH);
                BigDecimal calculoAvaluoPropiedad = ((BigDecimal) groovyUtil.getExpression("calculoAvaluoPropiedad", new Object[]{})).setScale(4, RoundingMode.HALF_UP);
                procesoPH.setAvaluoEdificacion(calculoAvaluoPropiedad);
                BigDecimal totalPagar = ((BigDecimal) groovyUtil.getExpression("totalPagar", new Object[]{})).setScale(2, RoundingMode.HALF_UP);
                ht.setValorLiquidacion(totalPagar);
                procesoPH.setFecCre(new Date());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public List<PeFirma> getListPeFirma() {
        return services.getListPeFirma();
    }

    public void guardarProcesoReporte() {
        try {
            if (procesoPH.getFirma() == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Director(a) de Edificaciones: ", "");
                return;
            }
            Collection<HistoricoReporteTramite> desabilitar = ht.getHistoricoReporteTramiteCollection();
            services.getPermiso().actualizarHistoricoReporteTramites(desabilitar);

            procesoPH.setAvaluoPropiedad(procesoPH.getAvaluoEdificacion());
            procesoPH.setTotal(ht.getValorLiquidacion());
            procesoPH = services.modificarTasaLiquidacion(procesoPH, ht, listaPropietarios, listaPropietariosEliminar);
            if (procesoPH != null) {
                guardar = false;
                impresion = true;
                JsfUti.messageInfo(null, "Información Modificada con exito", "");
            } else {
                JsfUti.messageInfo(null, "Error al intentar Modificar la Información", "");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
        JsfUti.update("formGenLiqPH");
    }

    public void iniciarDatos() {
        try {
            Calendar cl = Calendar.getInstance();
            PdfReporte byteReporte = new PdfReporte();
            procesoPH = services.getHistoricoTramiteDetByTramite(ht.getIdTramite());
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//"); ////PropiedadHorizontal.jasper
            hr = services.guardarHistoricoReporteTramite(hr.getSecuencia(), ht, "PropiedadHorizontal", this.getTaskDataByTaskID().getTaskDefinitionKey());

            AclUser firmaDirector = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(tramite.getUserDireccion()));
            if (hr != null) {
                ss.instanciarParametros();
                String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hr.getCodValidacion();
                ss.setNombreReporte("PropiedadHorizontal");
                ss.setNombreSubCarpeta("propiedadHorizontal");
                ss.setTieneDatasource(true);
                ss.agregarParametro("idimprimir", procesoPH.getId());
                ss.agregarParametro("firmaDirec", path + "/css/firmas/" + firmaDirector.getRutaImagen() + ".jpg");
                ss.agregarParametro("firmaTecni", path + "/css/firmas/" + user.getRutaImagen() + ".jpg");
                ss.agregarParametro("numrepor", hr.getSecuencia() + "-" + cl.get(Calendar.YEAR));
                ss.agregarParametro("logo", path + SisVars.logoReportes);
                ss.agregarParametro("seleccionado", path + "/css/homeIconsImages/selecc.png");
                ss.agregarParametro("validador", hr.getId().toString() + "" + ht.getIdProceso());
                ss.agregarParametro("codigoQR", codigoQR);
                ss.agregarParametro("realizadoPor", realizadorPor);
                ss.agregarParametro("anio", cl.get(Calendar.YEAR));
                ss.setReportePDF(byteReporte.generarPdf("reportes/propiedadHorizontal/PropiedadHorizontal.jasper", ss.getParametros()));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
        JsfUti.executeJS("PF('obs').show()");
    }

    public void imprimir() {
        try {
            if (observacion == null) {
                JsfUti.messageInfo(null, "Debe Ingresar las Observaciones para continuar", "");
                return;
            }
            if (hr != null) {
                HashMap<String, Object> paramt = new HashMap<>();
                Observaciones obs = services.guardarObservaciones(ht, session.getName_user(), observacion, this.getTaskDataByTaskID().getTaskDefinitionKey());
                if (ss.getReportePDF() != null) {
                    ms = new MsgFormatoNotificacion();

                    String mensaje = "<h2>Acercarse a cancelar </h2><br/> <h3> Valor a Cancelar :  $ " + ht.getValorLiquidacion() + " <br/>"
                            + "	   att.<br/> Arq. " + procesoPH.getFirma().getNomCompleto().substring(3) + "<br/> ";
                    ms = services.getMsgFormatoNotificacionByTipo(2L); // VERIFICAR 

                    paramt.put("idReporte", hr.getId());
                    paramt.put("prioridad", 50);
                    paramt.put("archivo", ss.getReportePDF());
                    paramt.put("nombreArchivoByteArray", new Date().getTime() + ss.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
                    paramt.put("tipoArchivoByteArray", "application/pdf");
                    paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
//                paramt.put("from", SisVars.correo);
//                paramt.put("to", ht.getCorreos());
                    paramt.put("subject", "La Liquidacion de Propiedad Horizontal del tramite # " + ht.getId() + " esta realizada");
                    paramt.put("massage", ms.getHeader() + mensaje + ms.getFooter());
                    paramt.put("idProcess", session.getTaskID());
                }
                if (obs != null) {
                    this.completeTask(this.getTaskId(), paramt);
                    impresion = false;
                    guardar = false;
                    dashBoard = true;
                    impresoLiquidacion = true;
                    services.getPermiso().actualizarHistoricoReporteTramitesByTaskDef("tasaLiquidaciónPH", ht.getIdProceso());
                }
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
//        FacesContext.getCurrentInstance().responseComplete();
        JsfUti.executeJS("PF('obs').hide()");
        JsfUti.update("formGenLiqPH");
    }

    public void redirectDashBoard() {
        if (impresoLiquidacion) {
            this.continuar();
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

    public EditarLiquidacionPH() {
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

    public HistoricoTramiteDet getProcesoPH() {
        return procesoPH;
    }

    public void setProcesoPH(HistoricoTramiteDet procesoPH) {
        this.procesoPH = procesoPH;
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

}
