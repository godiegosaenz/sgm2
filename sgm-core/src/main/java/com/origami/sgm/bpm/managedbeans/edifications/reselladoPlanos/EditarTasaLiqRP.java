package com.origami.sgm.bpm.managedbeans.edifications.reselladoPlanos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
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
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EditarTasaLiqRP implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    @javax.inject.Inject
    protected PropiedadHorizontalServices servicesPH;

    @Inject
    private ServletSession servletSession;
    @Inject
    private ReportesView reportes;
    @Inject
    private BpmManageBeanBase base;

    private HistoricoTramites tramite;
    private CatPredio predio;
    private HistoricoTramiteDet avaluo;
    private List<PeFirma> firmas;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private Observaciones obs;
    private HistoricoReporteTramite rp;
    private boolean cert = false;

    String seleccionado;
    BigInteger sec;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    private void load() {
        try {
            if (servletSession.getParametros() != null && servletSession.getParametros().get("tramite") != null) {
                Long idHtd = (Long) servletSession.getParametros().get("tramite");
                llenarHtd(idHtd);
                if (formula != null) {
                    gutil = new GroovyUtil(formula.getFormula());
                    obs = new Observaciones();
                    cargarDatosAvaluo();
                    seleccionado = JsfUti.getRealPath("/css/homeIconsImages/selecc.png");
                    servletSession.borrarParametros();
                } else {
                    Faces.messageWarning(null, "Advertencia", "No se ha encontrado la formula de calculo para este tramite, por favor verificar");
                }
            } else {
                JsfUti.redirectFaces("/vistaprocesos/edificaciones/selladoplanos/reselladoPlanosConsulta.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(EditarTasaLiqRP.class.getName()).log(Level.SEVERE, "Edición tasa Liquidación", e.getMessage());
        }
    }

    public void llenarHtd(Long id) {
        tramite = new HistoricoTramites();
        firmas = serv.findAll(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
        avaluo = (HistoricoTramiteDet) serv.find(HistoricoTramiteDet.class, id);
        if (avaluo != null) {
            predio = avaluo.getPredio();
        }
        if (avaluo.getTramite() != null) {
            tramite = avaluo.getTramite();
            formula = servicesPH.getPermiso().getMatFormulaTramite(tramite.getTipoTramite().getId());
        } else {
            formula = servicesPH.getPermiso().getMatFormulaTramite(36L);
        }

    }

    private void cargarDatosAvaluo() {
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
            Logger.getLogger(EditarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    public void guardar() {
        tramite = servicesPH.getPermiso().getHistoricoTramiteById(avaluo.getTramite().getId());
        try {
            if (avaluo.getNumPlanos() != null && avaluo.getNumPlanos().compareTo(BigInteger.ZERO) > 0 && avaluo.getTotal() != null && avaluo.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                Calendar cl = Calendar.getInstance();
                tramite.setValorLiquidacion(avaluo.getTotal());

                if (serv.update(avaluo)) {
                    servletSession.instanciarParametros();
                    String qr = SisVars.urlServidorCompleta + "/Documento?param=";
                    if(!tramite.getHistoricoReporteTramiteCollection().isEmpty()){
                        for (HistoricoReporteTramite r : tramite.getHistoricoReporteTramiteCollection()) {
                            if(r.getEstado()){
                                if(r.getNombreReporte().contains("ReselladoPlanos-"))
                                    qr = qr.concat(r.getCodValidacion()) ;
                            }
                        }
                    }else{
                        qr = qr.concat(null) ;
                    }
                    servletSession.agregarParametro("id", tramite.getId());
                    servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/lilianaGuerrero.jpg"));
                    servletSession.agregarParametro("validador", qr);
                    servletSession.agregarParametro("seleccionado", seleccionado);
                    servletSession.setNombreReporte("ReselladoPlanos");
                    servletSession.setNombreSubCarpeta("reselladoPlanos");
                    servletSession.setTieneDatasource(true);
                    servletSession.agregarParametro("codigoQR", qr);
                    servletSession.agregarParametro("det", avaluo.getId());
                    cert = true;
                    obs.setEstado(true);
                    obs.setFecCre(new Date());
                    obs.setIdTramite(tramite);
                    obs.setTarea("Edidicion de Informacion");
                    obs.setObservacion("Edidcion de Informacion del Tasa de liquidación");
                    obs.setUserCre(base.getSession().getName_user());
                    if ((serv.update(tramite) == false) || serv.persist(obs) == null) {
                        JsfUti.messageWarning(null, "Advertencia", "No se pudo realizar la liquidacion");
                    } else {
                        JsfUti.messageInfo(null, "Info", "La tasa de liquidación ha sido actualizada. Proceda a imprimirlo.");
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia", "Ocurrio un error al modificar los datos, verifique los datos y vuelva a intentarlo. ");
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "El numero de pisos como el valor total son  requeridos y deben ser mayores a 0");
            }
        } catch (Exception e) {
            Logger.getLogger(EditarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirLiq() {
        try {
            if (servletSession.getParametros() != null && servletSession.getParametros().get("det") != null) {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }
        } catch (Exception e) {
            Logger.getLogger(EditarTasaLiqRP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }
    
    public void continuar(){
        JsfUti.redirectFaces("/vistaprocesos/edificaciones/selladoplanos/reselladoPlanosConsulta.xhtml");
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

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

}
