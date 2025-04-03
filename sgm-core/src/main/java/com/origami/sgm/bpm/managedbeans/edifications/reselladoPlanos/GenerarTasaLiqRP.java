/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.reselladoPlanos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class GenerarTasaLiqRP extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    @javax.inject.Inject
    private SeqGenMan sequence;
    @Inject
    private ServletSession servletSession;
    @Inject
    private ReportesView reportes;
    private HistoricoTramites tramite;
    private CatPredio predio;
    private HistoricoTramiteDet avaluo, det;
    private List<PeFirma> firmas;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private Observaciones obs;
    private HistoricoReporteTramite r;
    private HashMap<String, Object> params;
    private PdfReporte reporte;
    private HistoricoReporteTramite rp;
    private boolean cert = false;
    private AclUser digitalizador;

    String seleccionado;
    BigInteger sec;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    private void load() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                ParametrosDisparador temp;
                this.setTaskId(sess.getTaskID());
                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                predio = (CatPredio) serv.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});
                formula = (MatFormulaTramite) serv.find(Querys.getMatFormulaByTipoTramiteID, new String[]{"idTipoTramite"}, new Object[]{tramite.getTipoTramite().getId()});
                if (formula != null) {
                    gutil = new GroovyUtil(formula.getFormula());
                    firmas = serv.findAll(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
                    obs = new Observaciones();
                    cargarDatosAvaluo();
                    seleccionado = JsfUti.getRealPath("/css/homeIconsImages/selecc.png");
                    params = new HashMap<>();
                    servletSession.instanciarParametros();
                    reporte = new PdfReporte();
                    temp = (ParametrosDisparador) serv.find(Querys.getDigitalizadorByTipoTramite, new String[]{"tipoTramite", "var"}, new Object[]{tramite.getTipoTramite(), "digitalizador"});
                    digitalizador = temp.getResponsable();
                } else {
                    Faces.messageWarning(null, "Advertencia", "No se ha encontrado la formula de calculo para este tramite, por favor verificar");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    private void cargarDatosAvaluo() {
        if (this.getVariable(this.getTaskId(), "codAvaluo") != null) {//cuando edite la ficha
            avaluo = (HistoricoTramiteDet) serv.find(HistoricoTramiteDet.class, this.getVariable(this.getTaskId(), "codAvaluo"));
            if (avaluo.getAreaEdificacion() == null) {
                avaluo.setAreaEdificacion(BigDecimal.ZERO);
            }
            if (avaluo.getAvaluoConstruccion() == null) {
                avaluo.setAvaluoConstruccion(predio.getAvaluoConstruccion());
            }
            if (avaluo.getAvaluoSolar() == null) {
                avaluo.setAvaluoSolar(predio.getAvaluoConstruccion());
            }
            if (avaluo.getAvaluoPropiedad() == null) {
                if (predio.getAvaluoConstruccion() != null && predio.getAvaluoConstruccion() != null) {
                    avaluo.setAvaluoPropiedad(predio.getAvaluoSolar().add(predio.getAvaluoConstruccion()));
                } else {
                    avaluo.setAvaluoPropiedad(BigDecimal.ZERO);
                }
            }
            if (sess.getNombrePersonaLogeada() != null) {
                avaluo.setResponsable(sess.getNombrePersonaLogeada());
            }
        } else {
            avaluo = new HistoricoTramiteDet();
            avaluo.setTramite(tramite);
            avaluo.setPredio(predio);
            avaluo.setBaseCalculo((BigDecimal) gutil.getProperty("baseCalculo"));
            avaluo.setDescripcion("LOS PLANOS INGRESADOS SON: ");
            if (sess.getNombrePersonaLogeada() != null) {
                avaluo.setResponsable(sess.getNombrePersonaLogeada());
            }
        }
    }

    public void calcularTotal() {
        try {
            if (avaluo.getNumPlanos() != null && avaluo.getNumPlanos().compareTo(BigInteger.ZERO) > 0) {
                gutil.getExpression("setNumPlanos", new Object[]{avaluo.getNumPlanos()});
                avaluo.setTotal((BigDecimal) gutil.getExpression("getValorTotal", new Object[]{}));
                avaluo.setDescripcion("LOS PLANOS INGRESADOS SON: " + avaluo.getNumPlanos().toString() + " * " + avaluo.getBaseCalculo().toString() + " = " + avaluo.getTotal());
            } else {
                Faces.messageWarning(null, "Advertencia", "El numero de pisos requerido debe ser mayor a 0");
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    public void guardar() {
        try {
            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada");
                return;
            }
            if (avaluo.getNumPlanos() != null && avaluo.getNumPlanos().compareTo(BigInteger.ZERO) > 0 && avaluo.getTotal() != null && avaluo.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                r = new HistoricoReporteTramite();
                Calendar cl = Calendar.getInstance();
                tramite.setValorLiquidacion(avaluo.getTotal());
                obs.setEstado(Boolean.TRUE);
                obs.setObservacion(this.getAvaluo().getDescripcion());
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setUserCre(sess.getName());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                if (this.getVariable(sess.getTaskID(), "idReporte") == null) {
                    sec = sequence.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{cl.get(Calendar.YEAR)});
                    r.setTramite(tramite);
                    r.setProceso(tramite.getIdProceso());
                    r.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                    r.setSecuencia(sec);
                    r.setFecCre(new Date());
                    r.setEstado(Boolean.TRUE);
                    r.setNombreReporte("ReselladoPlanos-" + tramite.getSolicitante().getCiRuc() + " - " + (new Date()).getTime());
                    r.setCodValidacion(sec + tramite.getIdProceso());
                } else {
                    HistoricoReporteTramite t = (HistoricoReporteTramite) serv.find(HistoricoReporteTramite.class, this.getVariable(sess.getTaskID(), "idReporte"));
                    t.setEstado(Boolean.FALSE);
                    serv.persist(t);
                    r.setTramite(tramite);
                    r.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                    r.setSecuencia(t.getSecuencia());
                    r.setNombreReporte("ReselladoPlanos-" + tramite.getSolicitante().getCiRuc() + " - " + (new Date()).getTime());
                    r.setProceso(t.getProceso());
                    r.setEstado(Boolean.TRUE);
                    r.setCodValidacion(t.getCodValidacion());
                    r.setFecCre(new Date());
                }
                avaluo.setFecCre(new Date());
                avaluo.setEstado(Boolean.TRUE);
                rp = (HistoricoReporteTramite) serv.persist(r);
                det = (HistoricoTramiteDet) serv.persist(avaluo);
                if (rp != null && det != null) {
                    String qr = SisVars.urlServidorCompleta + "/Documento?param=" + rp.getCodValidacion();
                    servletSession.agregarParametro("id", tramite.getId());
                    servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/lilianaGuerrero.jpg"));
                    servletSession.agregarParametro("validador", qr);
                    servletSession.agregarParametro("seleccionado", seleccionado);
                    servletSession.setNombreReporte("ReselladoPlanos " + tramite.getId() + " " + (new Date()).getTime());
                    servletSession.setTieneDatasource(true);
                    servletSession.agregarParametro("codigoQR", qr + rp.getId().toString() + tramite.getIdProceso());
                    params.put("prioridad", 50);
                    params.put("idReporte", rp.getId());
                    params.put("carpeta", tramite.getCarpetaRep());
                    params.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + tramite.getSolicitante().getCiRuc() + ".pdf");
                    params.put("tipoArchivoByteArray", "application/pdf");
                    params.put("codAvaluo", det.getId());
                    params.put("digitalizador", digitalizador.getUsuario());
                    params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                    servletSession.agregarParametro("det", det.getId());
                    cert = true;
                    if ((serv.update(tramite) == false) || serv.persist(obs) == null) {
                        JsfUti.messageWarning(null, "Advertencia", "No se pudo realizar la liquidacion");
                    } else {
                        if (this.getVariable(this.getTaskId(), "codAvaluo") == null) {
                            JsfUti.messageInfo(null, "Info", "Se ha generado la tasa de liquidación correctamente. Proceda a imprimirlo.");
                        } else {
                            JsfUti.messageInfo(null, "Info", "La tasa de liquidación ha sido actualizada. Proceda a imprimirlo.");
                        }
                    }
                } else {
                    JsfUti.messageWarning(null, "Advertencia", "Ha ocurrido un error al generar la solicitud");
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "El numero de pisos como el valor total son  requeridos y deben ser mayores a 0");
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirLiq() {
        try {
            if (servletSession.getParametros() != null && servletSession.getParametros().get("det") != null) {
                servletSession.setReportePDF(reporte.generarPdf("/reportes/reselladoPlanos/ReselladoPlanos.jasper", servletSession.getParametros()));
                reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
                List<Long> renta = new ArrayList<>();
                renta.add(98L); // Jefe Renta 98
                List<AclUser> usersRenta = acl.getTecnicosByRol(renta);
                for (AclUser u : usersRenta) {
                    if (u.getSisEnabled() && u.getUserIsDirector()) {
                        params.put("renta", u.getUsuario());
                    }
                }
                params.put("descripcion", tramite.getTipoTramite().getDescripcion());
                params.put("archivo", servletSession.getReportePDF());
                if (this.getTaskDataByTaskID() != null) {
                    this.completeTask(this.getTaskId(), params);
                    servletSession.borrarDatos();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public HistoricoTramiteDet getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(HistoricoTramiteDet avaluo) {
        this.avaluo = avaluo;
    }

    public List<PeFirma> getFirmas() {
        return firmas;
    }

    public void setFirmas(List<PeFirma> firmas) {
        this.firmas = firmas;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public boolean getCert() {
        return cert;
    }

    public void setCert(boolean cert) {
        this.cert = cert;
    }

}
