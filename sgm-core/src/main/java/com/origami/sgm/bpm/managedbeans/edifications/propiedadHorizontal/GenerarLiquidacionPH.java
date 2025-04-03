/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class GenerarLiquidacionPH extends BpmManageBeanBaseRoot implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GenerarLiquidacionPH.class.getName());

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
    protected String realizadorPor;
    protected HashMap<String, Object> paramt;
    protected BigDecimal baseCalculo1;
    protected BigDecimal baseCalculo2;

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
                procesoPH = new HistoricoTramiteDet();
                ht = new HistoricoTramites();
                tramite = new GeTipoTramite();
                canton = new CatCanton();
                user = new AclUser();
                listaPropietarios = new ArrayList<>();
                listaPropietariosEliminar = new ArrayList<>();

                this.setTaskId(session.getTaskID());
                canton = services.getCatCantonById(1L);
                ht = services.getPermiso().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));

                procesoPH = services.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                catCantonsList = services.getInscripcion().getCatCantonList();
                catParroquiaList = services.getFichaServices().getCatPerroquiasListByCanton(canton.getId());
                user = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(session.getName_user()));
                if (user.getEnte() == null) {
                    realizadorPor = user.getUsuario();
                } else {
                    realizadorPor = Utils.isEmpty(user.getEnte().getApellidos()) + " " + Utils.isEmpty(user.getEnte().getNombres());
                }
                try {
                    predio = procesoPH.getPredio();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Inciar Liquidacion de PH: (" + procesoPH + ")", ex);
                }
                if (predio != null) {
                    consultarCodPredio();
                }

                if (procesoPH != null) {
                    procesoPH.setValorXMetro(new BigDecimal(520));
                    procesoPH.setDepreciacion(new BigDecimal(1));
                }
                tramite = services.getPermiso().getGeTipoTramiteById(ht.getTipoTramite().getId());
                formulas = services.getPermiso().getMatFormulaTramite(tramite.getId());
                groovyUtil = new GroovyUtil(formulas.getFormula());

                baseCalculo1 = ((BigDecimal) groovyUtil.getProperty("baseCalculo"));
                baseCalculo2 = ((BigDecimal) groovyUtil.getProperty("baseCalculo1"));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }

        } else {
            this.continuar();
        }
    }

    public void consultarCodPredio() {
        try {
            predio.setPhh(new Short("0"));
            predio.setPhv(new Short("0"));
            CatPredio pred = services.getPermiso().getCatPredioByCodigoPredio(predio.getZona(),predio.getSector(), predio.getMz(), predio.getSolar());
            if (pred != null) {
                predio = pred;
                listaPropietarios = new ArrayList<>();
                List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
                if (!propietariosTemp.isEmpty()) {
                    for (CatPredioPropietario temp : propietariosTemp) {
                        if ("A".equals(temp.getEstado())) {
                            listaPropietarios.add(temp);
                        }
                    }
                }
                if (pred.getCiudadela() != null) {
                    parroquia = pred.getCiudadela().getCodParroquia();
                }
                if (parroquia != null) {
                    canton = parroquia.getIdCanton();
                } else {
                    parroquia = Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1);
                }

                procesoPH.setPredio(predio);
                listaPropietariosEliminar = new ArrayList<>();
            } else {
                JsfUti.messageError(null, "No hay registro con el Código de predio ingresado ir al DEPARTAMENTO DE CATASTRO PARA QUE INGRESEN EL PREDIO", "");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
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
                    JsfUti.update("formGenLiqPH:dtProp");
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
                    buscarPropietario();
                    return;
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
            if (procesoPH.getAreaEdificacion() != null && procesoPH.getDepreciacion() != null && procesoPH.getValorXMetro() != null) {
                System.out.println("/*** " + procesoPH.getValorXMetro());
                System.out.println("/*** " + procesoPH.getDepreciacion());
                groovyUtil.setProperty("liquidacionNueva", procesoPH);
                groovyUtil.setProperty("multiplyAvaluo", procesoPH.getValorXMetro());
                groovyUtil.setProperty("factorDepreciacion", procesoPH.getDepreciacion());
                BigDecimal avaluoConstruccion = ((BigDecimal) groovyUtil.getExpression("calcularAvaluoConstruccion", new Object[]{})).setScale(4, RoundingMode.UP);
                if (Utils.isDecimal(avaluoConstruccion.toString())) {
                    procesoPH.setAvaluoConstruccion(avaluoConstruccion);
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
                BigDecimal avalPropiedad = new BigDecimal(200000);
                if (calculoAvaluoPropiedad.longValue() <= avalPropiedad.longValue()) {
                    procesoPH.setBaseCalculo(baseCalculo1);
                } else {
                    procesoPH.setBaseCalculo(baseCalculo2);
                }

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

    public void validar() {
        if (procesoPH.getFirma() == null) {
            JsfUti.messageInfo(null, "Debe Seleccionar Director(a) de Edificaciones: ", "");
            return;
        }
        if (procesoPH.getDescripcion() == null) {
            JsfUti.messageInfo(null, "Ingrese Descripción de la Edificación.", "");
            return;
        }
        JsfUti.update("frmConf");
        JsfUti.executeJS("PF('confCartaAdo').show()");
    }

    public void guardarProcesoReporte() {
        try {
            if (procesoPH.getFirma() == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Director(a) de Edificaciones: ", "");
                return;
            }
            if (procesoPH.getDescripcion() == null) {
                JsfUti.messageInfo(null, "Ingrese Descripción de la Edificación.", "");
                return;
            }
            procesoPH.setAvaluoPropiedad(procesoPH.getAvaluoEdificacion());
            procesoPH.setTotal(ht.getValorLiquidacion().setScale(2, RoundingMode.HALF_UP));
            procesoPH = services.guadarTasaLiquidacion(procesoPH, ht, listaPropietarios, listaPropietariosEliminar);
            if (procesoPH != null) {
                final Long idTramite = ht.getIdTramite();

                services.guardarObservaciones(ht, session.getName_user(), procesoPH.getDescripcion(), this.getTaskDataByTaskID().getTaskDefinitionKey());
                procesoPH = new HistoricoTramiteDet();
                HistoricoTramiteDet det = services.getHistoricoTramiteDetByTramite(idTramite);
                procesoPH = (HistoricoTramiteDet) EntityBeanCopy.clone(det);
                procesoPH.setFirma((PeFirma) EntityBeanCopy.clone(det.getFirma()));
                guardar = false;
                impresion = true;
                JsfUti.messageInfo(null, "Guardado Exitoso", "");

                JsfUti.update("formGenLiqPH:firm");
                JsfUti.update("formGenLiqPH");
            } else {
                JsfUti.messageInfo(null, "Error al Guardar", "");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }

    }

    public void imprimir() {
        try {
            final Long idTramite = ht.getId();
            Calendar cl = Calendar.getInstance();
            PdfReporte byteReporte = new PdfReporte();
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//"); ////PropiedadHorizontal.jasper

            ht = new HistoricoTramites();
            ht = services.getPermiso().getHistoricoTramiteById(idTramite);
            HistoricoReporteTramite hr = services.guardarHistoricoReporteTramite(null, ht, "PropiedadHorizontal", this.getTaskDataByTaskID().getTaskDefinitionKey());

            procesoPH = new HistoricoTramiteDet();
            procesoPH = services.getHistoricoTramiteDetByTramite(ht.getIdTramite());
            AclUser firmaDirector = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(tramite.getUserDireccion()));

            if (hr != null) {
                ss.instanciarParametros();
                String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hr.getCodValidacion();
                ss.setNombreReporte("PropiedadHorizontal");
                ss.setNombreSubCarpeta("propiedadHorizontal");
                ss.setTieneDatasource(true);
                ss.agregarParametro("idimprimir", procesoPH.getId());
                ss.agregarParametro("firmaDirec", path + "/css/firmas/" + firmaDirector.getRutaImagen() + ".jpg");
                ss.agregarParametro("numrepor", hr.getSecuencia() + "-" + cl.get(Calendar.YEAR));
                ss.agregarParametro("logo", path + SisVars.logoReportes);
                ss.agregarParametro("seleccionado", path + "/css/homeIconsImages/selecc.png");
                ss.agregarParametro("validador", hr.getId().toString() + "" + ht.getIdProceso());
                ss.agregarParametro("codigoQR", codigoQR);
                ss.agregarParametro("realizadoPor", realizadorPor);
                ss.agregarParametro("anio", cl.get(Calendar.YEAR));

                ss.setReportePDF(byteReporte.generarPdf("reportes/propiedadHorizontal/PropiedadHorizontal.jasper", ss.getParametros()));
                if (ss.getReportePDF() != null) {
                    ms = new MsgFormatoNotificacion();
                    ms = services.getMsgFormatoNotificacionByTipo(2L);
                    String mensaje = ms.getHeader()
                            + "<br/><h2>Acercarse a cancelar </h2><br/> <h3> Valor a Cancelar :  $ "
                            + ht.getValorLiquidacion() + " <br/><br/><br/>"
                            + "att.<br/> Arq. " + procesoPH.getFirma().getNomCompleto().substring(3) + "<br/><br/><br/> "
                            + ms.getFooter();
                    paramt = new HashMap<>();

                    List<Long> roles = new ArrayList<>();
                    roles.add(110L); // Secretaria General 72
                    List<AclUser> users = acl.getTecnicosByRol(roles);
                    for (AclUser u : users) {
                        if (u.getSisEnabled() && u.getUserIsDirector()) {
                            paramt.put("secretariaGeneral", u.getUsuario());
                        }
                    }

                    paramt.put("from", SisVars.correo);
                    paramt.put("to", ht.getCorreos());
                    paramt.put("subject", "La Liquidacion de Propiedad Horizontal del tramite # " + ht.getId() + " esta realizada");
                    paramt.put("message", mensaje);
                    paramt.put("idReporte", hr.getId());
                    paramt.put("prioridad", 50);
                    paramt.put("archivo", ss.getReportePDF());
                    paramt.put("carpeta", ht.getCarpetaRep());
                    paramt.put("nombreArchivoByteArray", new Date().getTime() + ss.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
                    paramt.put("tipoArchivoByteArray", "application/pdf");
                    paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                    paramt.put("idProcess", session.getTaskID());

                    if (paramt != null) {
                        this.completeTask(session.getTaskID(), paramt);
                    }
                }
            }
            impresion = false;
            guardar = false;
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            dashBoard = true;
            impresoLiquidacion = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
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

    public GenerarLiquidacionPH() {
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

    public String getRealizadorPor() {
        return realizadorPor;
    }

    public void setRealizadorPor(String realizadorPor) {
        this.realizadorPor = realizadorPor;
    }

    public BigDecimal getBaseCalculo1() {
        return baseCalculo1;
    }

    public void setBaseCalculo1(BigDecimal baseCalculo1) {
        this.baseCalculo1 = baseCalculo1;
    }

    public BigDecimal getBaseCalculo2() {
        return baseCalculo2;
    }

    public void setBaseCalculo2(BigDecimal baseCalculo2) {
        this.baseCalculo2 = baseCalculo2;
    }

}
