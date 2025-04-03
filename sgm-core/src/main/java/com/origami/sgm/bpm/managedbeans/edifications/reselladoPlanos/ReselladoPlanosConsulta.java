/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.reselladoPlanos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.HistoricoTramiteDetLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class ReselladoPlanosConsulta implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReselladoPlanosConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;

    protected HistoricoTramiteDetLazy lazy;
    protected AclUser firmaDir;
    protected String path;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;

    public ReselladoPlanosConsulta() {
    }

    @PostConstruct
    public void initView() {
        lazy = new HistoricoTramiteDetLazy(36L);

        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        firmaDir = new AclUser();
    }

    public void imprimirLiquidación(HistoricoTramiteDet liquidacion) {
        HistoricoTramites ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
        AclUser firmaDirector = permisoServices.getAclUserByUser(ht.getTipoTramite().getUserDireccion());
        firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);

        AclUser tecnico = null;

        ss.instanciarParametros();
        String qr = SisVars.urlServidorCompleta + "/Documento?param=";
        if (!ht.getHistoricoReporteTramiteCollection().isEmpty()) {
            for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
                if (r.getEstado()) {
                    if (r.getNombreReporte().contains("ReselladoPlanos-")) {
                        qr = qr.concat(r.getCodValidacion());
                    }
                }
            }
        } else {
            qr = qr.concat(null);
        }
        ss.agregarParametro("id", ht.getId());
        ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
        ss.agregarParametro("firmaDirec", JsfUti.getRealPath("/css/firmas/lilianaGuerrero.jpg"));
        ss.agregarParametro("validador", qr);
        ss.agregarParametro("seleccionado", JsfUti.getRealPath("/css/homeIconsImages/selecc.png"));
        ss.setNombreReporte("ReselladoPlanos");
        ss.setNombreSubCarpeta("reselladoPlanos");
        ss.setTieneDatasource(true);
        ss.agregarParametro("codigoQR", qr);
        ss.agregarParametro("det", liquidacion.getId());

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void imprimirCertificado(HistoricoTramiteDet liquidacion) {
        ss.instanciarParametros();
        ss.agregarParametro("idpermiso", liquidacion.getId());
        ss.agregarParametro("logo", path + SisVars.logoReportes);
        ss.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
//        ss.agregarParametro("NUMTRAMITEPC", liquidacion.getNumReporte().toString() + "-" + liquidacion.getAnioTramite().toString());
        ss.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
        ss.setNombreReporte("PermisoConstruccion");
        ss.setNombreSubCarpeta("permisoConstruccion");
        ss.setTieneDatasource(true);
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void redirectVistaPermiso(HistoricoTramiteDet liquidacion) {
        try {
            ss.instanciarParametros();
//            if (liquidacion.getObservacion().contentEquals("Trámite migrado")) {
//
//            } else {
            ss.agregarParametro("tramite", liquidacion.getId());
//            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edifications/selladoplanos/editarTasaLiqRP.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA VISUALIZACION", e);
        }
    }

    public void redirectEditarPermiso(HistoricoTramiteDet liquidacion) {
        try {
            ss.instanciarParametros();
//            if (liquidacion.getObservacion().contentEquals("Trámite migrado")) {
//
//            } else {
            ss.agregarParametro("tramite", liquidacion.getId());
//            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/selladoplanos/editarTasaLiqRP.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA EDICION", e);
        }
    }

    public void mostrar() {
        Calendar cl = Calendar.getInstance();
        anioDesde = (cl.get(Calendar.YEAR));
        memorandum2 = memorandum2 + "" + anioDesde;
        JsfUti.update("frmConsulta");
        JsfUti.executeJS("PF('dlgConsulta').show()");
    }

    public void reporteSemanalPC() {
        ss.instanciarParametros();
        ss.setNombreReporte("listado_permiso_construccion");
        ss.setNombreSubCarpeta("permisoConstruccion");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("MEMO", memorandum + " " + memorandum2);
        ss.agregarParametro("DE", new Long(permisoDesde));
        ss.agregarParametro("HASTA", new Long(permisoHasta));
        ss.agregarParametro("DESDE_ANIO", new Long(anioDesde));
        ss.agregarParametro("DIRIGIDO_A", dirigioA);
        ss.agregarParametro("CARGO", cargo);

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public HistoricoTramiteDetLazy getLazy() {
        return lazy;
    }

    public void setLazy(HistoricoTramiteDetLazy lazy) {
        this.lazy = lazy;
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

    public String getMemorandum() {
        return memorandum;
    }

    public void setMemorandum(String memorandum) {
        this.memorandum = memorandum;
    }

    public String getMemorandum2() {
        return memorandum2;
    }

    public void setMemorandum2(String memorandum2) {
        this.memorandum2 = memorandum2;
    }

    public String getDirigioA() {
        return dirigioA;
    }

    public void setDirigioA(String dirigioA) {
        this.dirigioA = dirigioA;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPermisoDesde() {
        return permisoDesde;
    }

    public void setPermisoDesde(String permisoDesde) {
        this.permisoDesde = permisoDesde;
    }

    public String getPermisoHasta() {
        return permisoHasta;
    }

    public void setPermisoHasta(String permisoHasta) {
        this.permisoHasta = permisoHasta;
    }

    public int getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(int anioDesde) {
        this.anioDesde = anioDesde;
    }

}
