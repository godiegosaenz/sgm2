package com.origami.sgm.bpm.managedbeans.edifications.fusion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import util.EntityBeanCopy;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;
import util.Messages;

/**
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class GenerarTasaLiquidacionFP extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    protected PropiedadHorizontalServices services;
    @javax.inject.Inject
    private SeqGenMan sequence;
    @Inject
    private ServletSession servletSession;
    @Inject
    private ReportesView reportes;
    private HistoricoTramites tramite;
    private CatPredio predio;
    private List<CatPredio> predios;
    private List<CatPredioPropietario> predPropsfus;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private PeFirma firma;
    private List<PeFirma> firmas;
    private HistoricoTramiteDet avaluo;
    private List<HistoricoTramiteDet> avaluos, avalTemp;
    private HistoricoReporteTramite r;
    private GroovyUtil gutil;
    private BigDecimal baseCalculo = BigDecimal.ZERO, total = BigDecimal.ZERO, avalConst = BigDecimal.ZERO, avalProp = BigDecimal.ZERO;
    private MatFormulaTramite formula;
    private boolean maval = false, fin = false;
    private String descripcionPredio;
    private PdfReporte reporte;
    private boolean cert = false;
    private BigInteger sec;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void initView() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                tramite = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                //if (tramite.getNumPredio() != null) {
                formula = (MatFormulaTramite) acl.find(Querys.getMatFormulaByTipoTramiteID, new String[]{"idTipoTramite"}, new Object[]{tramite.getTipoTramite().getId()});
                if (formula != null) {
                    gutil = new GroovyUtil(formula.getFormula());
                    baseCalculo = ((BigDecimal) gutil.getProperty("baseCalculo")).setScale(12, 4);
                    firmas = acl.findAll(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
                    reporte = new PdfReporte();
                    //predio = (CatPredio) acl.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});
                    obs = new Observaciones();
                    params = new HashMap<>();
                    if (tramite.getHistoricoTramiteDetCollection() != null) {
                        avaluos = (List<HistoricoTramiteDet>) tramite.getHistoricoTramiteDetCollection();
                        predios = new ArrayList<>();
                        Hibernate.initialize(tramite.getSolicitante());
                        for (CatPredioPropietario var : tramite.getSolicitante().getCatPredioPropietarioCollection()) {
                            Hibernate.initialize(var.getPredio());
                            if (var.getPredio() != null && var.getPredio().getEstado().equals("A")) {
                                predios.add(var.getPredio());
                            }
                        }
                    } else {
                        avaluos = new ArrayList<>();
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia", "No se ha encontrado la formula de calculo para este tramite, por favor verificar");
                }

                //}
            }
        } catch (NumberFormatException | HibernateException e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void datosAvaluo(CatPredio p) {
        if (p.getNumPredio() != null) {
            avaluo = new HistoricoTramiteDet();
            avaluo.setPredio(p);
            avaluo.setFecCre(new Date());
            avaluo.setEstado(true);
            avaluo.setTramite(tramite);
            Faces.executeJS("PF('dlgAvaluo').show()");
        } else {
            JsfUti.messageWarning(null, "Advertencia", Messages.sinAvaluoPredio);
        }
    }

    public void agregarAvaluo() {
        try {
            int x = 0;
            if (avaluo.getAvaluoSolar().compareTo(BigDecimal.ZERO) > 0 && avaluo.getAvaluoConstruccion().compareTo(BigDecimal.ZERO) >= 0 && avaluo.getPredio().getNumPredio() != null) {
                avaluo.setAvaluoEdificacion(BigDecimal.ZERO);
                if (avaluos == null && avaluos.isEmpty()) {
                    avaluos.add(avaluo);
                } else {
                    for (HistoricoTramiteDet a : avaluos) {
                        if (a.getPredio().getNumPredio().equals(avaluo.getPredio().getNumPredio())) {
                            a.setAvaluoSolar(avaluo.getAvaluoSolar());
                            a.setAvaluoConstruccion(avaluo.getAvaluoConstruccion());
                            x++;
                        }
                    }
                    if (x == 0) {
                        avaluos.add(avaluo);
                    } else {
                        Faces.messageWarning(null, "Advertencia", "El predio seleccionado ya se encuentra registrado");
                    }
                }
                avaluo = null;
            } else {
                JsfUti.messageWarning(null, "Advertencia", Messages.sinAvaluoPredio);
            }
        } catch (Exception e) {
            avaluo = null;
            Logger
                    .getLogger(GenerarTasaLiquidacionFP.class
                            .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void removerAvaluo(HistoricoTramiteDet aval) {
        avaluos.remove(aval);
    }

    public void mostrarAvaluo(HistoricoTramiteDet aval) {
        avaluo = aval;
    }

    public void elimarItem(HistoricoTramiteDet val) {
        if (val != null && val.getId() != null) {
            acl.delete(val);
            Faces.update("mainForm");
        }
    }

    public void actualizarAvaluo() {
        try {
            if (avaluo.getAvaluoConstruccion().compareTo(BigDecimal.ZERO) >= 0 && avaluo.getAvaluoSolar().compareTo(BigDecimal.ZERO) > 0) {
                for (HistoricoTramiteDet d : avaluos) {
                    if (avaluo.equals(d)) {
                        Hibernate.initialize(d.getPredio());
                        d.setAvaluoConstruccion(avaluo.getAvaluoConstruccion());
                        d.setAvaluoSolar(avaluo.getAvaluoSolar());
                        d.setAvaluoEdificacion(BigDecimal.ZERO);
                        d.setFecCre(new Date());
                    }
                }
            } else {
                JsfUti.messageWarning(null, "Advertencia", "Debe ingresar los valores respectivos");

            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    private boolean validarPredio() {
        int cont = 0;
        try {
            if (predPropsfus.isEmpty() != true && predPropsfus.size() > 1) {
                for (CatPredioPropietario pf : predPropsfus) {
                    Hibernate.initialize(pf.getPredio());
                    if (predio.getUrbMz() != null) {
                        if (pf.getPredio().getUrbMz().equals(predio.getUrbMz())) {
                            cont++;
                            if (cont == predPropsfus.size()) {
                                return true;
                            }
                        } else {
                            JsfUti.messageWarning(null, "Advertencia", Messages.predioFueraSector);
                            return false;
                        }
                    } else {
                        JsfUti.messageWarning(null, "Advertencia", "El predio no tiene una manzana referenciada");
                        return false;
                    }
                }
            } else {
                JsfUti.messageWarning(null, "Advertencia", Messages.sinPrediosSuf);

            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void calcular(int x) {
        try {
            gutil.setProperty("avaluos", avaluos);
            avaluos = (List<HistoricoTramiteDet>) gutil.getExpression("getAvaluos", null);
            switch (x) {
                case 1:
                    //if (validarPredio() == true) {
                    total = ((BigDecimal) gutil.getProperty("total")).setScale(12, 4);
                    avalConst = ((BigDecimal) gutil.getProperty("avaluoConstruccion")).setScale(12, 4);
                    avalProp = ((BigDecimal) gutil.getProperty("avaluoPropiedad")).setScale(12, 4);
                    maval = avaluos.isEmpty() != true;
                    //}
                    break;
                case 2:
                    if (avaluos.size() > 1) {
                        total = ((BigDecimal) gutil.getProperty("total")).setScale(12, 4);
                        avalConst = ((BigDecimal) gutil.getProperty("avaluoConstruccion")).setScale(12, 4);
                        avalProp = ((BigDecimal) gutil.getProperty("avaluoPropiedad")).setScale(12, 4);
                        maval = avaluos.isEmpty() != true;
                    }
                    break;

            }

        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarLiquidacion() throws SQLException {
        HistoricoReporteTramite rp;
        try {
            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada");
                return;
            }
            if (avaluos.isEmpty() != true && avaluos.size() > 1 && descripcionPredio != null & total.compareTo(BigDecimal.ZERO) > 0) {
                String qr;
                AclUser firmaDirector = (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(tramite.getTipoTramite().getUserDireccion()));
                //String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                Long per = Long.parseLong(new SimpleDateFormat("yyyy").format(new Date()));
                servletSession.instanciarParametros();
                tramite.setValorLiquidacion(total);
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setUserCre(sess.getName());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                obs.setObservacion(descripcionPredio);
                if (this.getVariable(sess.getTaskID(), "idReporte") == null) {
                    sec = sequence.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{per});
                    r = new HistoricoReporteTramite();
                    r.setTramite(tramite);
                    r.setProceso(tramite.getIdProceso());
                    r.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                    r.setSecuencia(sec);
                    r.setEstado(Boolean.TRUE);
                    r.setNombreReporte("fusionPredioReporte-" + tramite.getSolicitante().getCiRuc());
                    r.setCodValidacion(sec + tramite.getIdProceso());
                } else {
                    r = new HistoricoReporteTramite();
                    HistoricoReporteTramite t = (HistoricoReporteTramite) acl.find(HistoricoReporteTramite.class, this.getVariable(sess.getTaskID(), "idReporte"));
                    t.setEstado(Boolean.FALSE);
                    acl.persist(t);
                    r.setTramite(tramite);
                    r.setNombreTarea("liquidacionFP");
                    r.setSecuencia(t.getSecuencia());
                    r.setNombreReporte("fusionPredioReporte-" + tramite.getSolicitante().getCiRuc());
                    r.setProceso(t.getProceso());
                    r.setEstado(Boolean.TRUE);
                    r.setCodValidacion(t.getCodValidacion());
                    r.setFecCre(new Date());
                }
                rp = (HistoricoReporteTramite) acl.persist(r);
                if (rp != null) {
                    qr = SisVars.urlServidorCompleta + SisVars.urlbase+ "Documento?param=" + rp.getCodValidacion();
                    servletSession.agregarParametro("id", tramite.getId());
                    servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("numrepor", rp.getId() + "-" + per);
                    servletSession.agregarParametro("base_calc1", baseCalculo);
                    servletSession.agregarParametro("base_calc2", 0);
                    servletSession.agregarParametro("total_pagar", total);
                    //servletSession.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/lilianaGuerrero.jpg"));
                    servletSession.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/" + firmaDirector.getRutaImagen() + ".jpg"));
                    servletSession.agregarParametro("validador", qr);
                    servletSession.setNombreReporte("fusionPredios - " + tramite.getId() + " - " + (new Date()).getTime());
                    servletSession.setTieneDatasource(true);
                    servletSession.agregarParametro("responsable", sess.getName_user().toUpperCase());
                    servletSession.agregarParametro("descripcion_edificacion", descripcionPredio.toUpperCase());
                    servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//fusion//"));
                    servletSession.agregarParametro("codigoQR", qr + rp.getId().toString() + tramite.getIdProceso());
                    //servletSession.setReportePDF(reporte.generarPdf("/reportes/fusion/FusionPredios.jasper", servletSession.getParametros()));
                    params.put("prioridad", 50);
                    params.put("idReporte", rp.getId());
                    params.put("archivo", servletSession.getReportePDF());
                    params.put("carpeta", tramite.getCarpetaRep());
                    params.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + tramite.getSolicitante().getCiRuc() + ".pdf");
                    params.put("tipoArchivoByteArray", "application/pdf");
                    params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                    cert = true;
                    //tramite.setNumTramiteXDepartamento(sec);
                    if ((acl.update(tramite) == false || acl.saveList(avaluos) == false) || acl.persist(obs) == null) {
                        cert = false;
                        JsfUti.messageWarning(null, "Advertencia", "No se pudo realizar la liquidacion");
                    } else {
                        JsfUti.messageInfo(null, "Info", "Se ha generado la tasa de liquidacion correctamente. Proceda a imprimirlo.");
                    }
                } else {
                    JsfUti.messageWarning(null, "Advertencia", Messages.sinPrediosSuf);
                }
            } else {
                JsfUti.messageWarning(null, "Advertencia", "Se requiere que la descripcion del predio y los datos del avaluo esten ingresados correctamente");
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirLiq() {
        try {
            if (servletSession.getParametros() != null && avaluos.size() > 1) {
                servletSession.setReportePDF(reporte.generarPdf("/reportes/fusion/FusionPredios.jasper", servletSession.getParametros()));
                reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
                params.put("urlTec", "/faces/vistaprocesos/edificaciones/fusionPredio/fusionPredioFP.xhtml");
                params.put("archivo", servletSession.getReportePDF());
                List<Long> roles = new ArrayList<>();
                    roles.add(91L); // Secretaria General 91
                List<AclUser> users = acl.getTecnicosByRol(roles);
                for (AclUser u : users) {
                    if (u.getSisEnabled() && u.getUserIsDirector()) {
                        params.put("secretariaGeneral", u.getUsuario());
                    }
                }
                AclRol catRol = (AclRol) acl.find(Querys.getAclRolByNombre, new String[]{"nombre"}, new Object[]{"director_catastro"});
                AclRol jurRol = (AclRol) acl.find(Querys.getAclRolByNombre, new String[]{"nombre"}, new Object[]{"director_juridico"});//77L
                if (catRol != null && jurRol != null) {
                    if (!catRol.getAclUserCollection().isEmpty() && !jurRol.getAclUserCollection().isEmpty()) {
                        List<AclUser> cat = (List<AclUser>) catRol.getAclUserCollection();
                        List<AclUser> jur = (List<AclUser>) jurRol.getAclUserCollection();
                        for (AclUser u : cat) {
                            if (u.getSisEnabled() && u.getUserIsDirector()) {
                                params.put("directorCatastro", u.getUsuario());
                            }
                        }
                        for (AclUser usersj : jur) {
                            if (usersj.getSisEnabled() && usersj.getUserIsDirector()) {
                                params.put("directorJuridico", usersj.getUsuario());
                            }
                        }
                        List<Long> renta = new ArrayList<>();
                        renta.add(98L); // Jefe Renta 98
                        List<AclUser> usersRenta = acl.getTecnicosByRol(renta);
                        for (AclUser u : usersRenta) {
                            if (u.getSisEnabled() && u.getUserIsDirector()) {
                                params.put("renta", u.getUsuario());
                            }
                        }

                        params.put("descripcion", tramite.getTipoTramite().getDescripcion());
                        if (this.getTaskDataByTaskID() != null) {
                            this.completeTask(this.getTaskId(), params);
                            servletSession.borrarDatos();
                        } else {
                            this.continuar();
                        }
                    } else {
                        JsfUti.messageWarning(null, "Advertencia", "Para poder contnuar deben existir el personal directivo de catastro o juridico");
                    }
                } else {
                    JsfUti.messageWarning(null, "Advertencia", "Para poder contnuar deben existir el personal directivo de catastro o juridico");
                }
            }
        } catch (SQLException | IOException e) {
            Logger.getLogger(GenerarTasaLiquidacionFP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<CatPredioPropietario> getPredPropsfus() {
        return predPropsfus;
    }

    public void setPredPropsfus(List<CatPredioPropietario> predPropsfus) {
        this.predPropsfus = predPropsfus;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public List<PeFirma> getFirmas() {
        return firmas;
    }

    public void setFirmas(List<PeFirma> firmas) {
        this.firmas = firmas;
    }

    public HistoricoTramiteDet getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(HistoricoTramiteDet avaluo) {
        this.avaluo = avaluo;
    }

    public List<HistoricoTramiteDet> getAvaluos() {
        return avaluos;
    }

    public void setAvaluos(List<HistoricoTramiteDet> avaluos) {
        this.avaluos = avaluos;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean getMaval() {
        return maval;
    }

    public void setMaval(boolean maval) {
        this.maval = maval;
    }

    public String getDescripcionPredio() {
        return descripcionPredio;
    }

    public void setDescripcionPredio(String descripcionPredio) {
        this.descripcionPredio = descripcionPredio;
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

    public boolean getFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public boolean getCert() {
        return cert;
    }

    public void setCert(boolean cert) {
        this.cert = cert;
    }

}
