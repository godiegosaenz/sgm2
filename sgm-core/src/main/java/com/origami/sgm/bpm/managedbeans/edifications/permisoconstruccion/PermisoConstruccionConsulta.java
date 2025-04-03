/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
public class PermisoConstruccionConsulta implements Serializable {

    private static final Logger LOG = Logger.getLogger(PermisoConstruccionConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    
    @javax.inject.Inject
    private Entitymanager service;

    protected PePermisoLazy permisolazy;
    protected AclUser firmaDir;
    protected String path;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;
    
    protected GeTipoTramite tipoTramite;

    public PermisoConstruccionConsulta() {
    }

    @PostConstruct
    public void initView() {
        permisolazy = new PePermisoLazy();

        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        firmaDir = new AclUser();
    }

    public void imprimirLiquidación(PePermiso permiso) {
        if(permiso.getTramite()!=null){
            HistoricoTramites ht = permisoServices.getHistoricoTramiteById(permiso.getTramite().getId());
        }
        tipoTramite=(GeTipoTramite)service.find(GeTipoTramite.class,2L);
        if(tipoTramite!=null && tipoTramite.getUserDireccion()!=null){
            AclUser firmaDirector = permisoServices.getAclUserByUser(tipoTramite.getUserDireccion());
            firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);
        }

//        String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + vd.getCodValidacion();
        AclUser tecnico = permiso.getUsuarioCreador();
        ss.instanciarParametros();
        ss.agregarParametro("permiso", permiso.getId());
        ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
        ss.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
        ss.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
        ss.agregarParametro("firmaTecni", path + "css/firmas/" + tecnico.getRutaImagen() + ".jpg");
        ss.agregarParametro("idUser", tecnico.getId());
        ss.setNombreReporte("LiquidacionTasasPermiso");
        ss.setTieneDatasource(true);
        ss.setNombreSubCarpeta("permisoConstruccion");
//            }

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void imprimirCertificado(PePermiso permiso) {
        ss.instanciarParametros();
        ss.agregarParametro("idpermiso", permiso.getId());
        ss.agregarParametro("logo", path + SisVars.logoReportes);
        ss.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
        ss.agregarParametro("NUMTRAMITEPC", permiso.getNumReporte().toString() + "-" + permiso.getAnioPermiso().toString());
        ss.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
        ss.setNombreReporte("PermisoConstruccion");
        ss.setNombreSubCarpeta("permisoConstruccion");
        ss.setTieneDatasource(true);
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void redirectVistaPermiso(PePermiso permiso) {
        try {
            HistoricoTramites ht = permiso.getTramite();
            if(ht!=null)
                sess.setTaskID(ht.getIdProceso());
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisoConstruccion/permisoConstruccionVista.xhtml?idPermiso="
                    + permiso.getId());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR EN REDIRECCIONAR VISTA PERMISO DE CONSTRUCCION", e);
        }
    }

    public void redirectEditarPermiso(PePermiso permiso) {
        try {
            ss.instanciarParametros();
            if(permiso.getObservacion()!=null){
                if (permiso.getObservacion().contentEquals("Trámite migrado")) {

                } else {
                    HistoricoTramites ht = permiso.getTramite();
                    if (ht!=null) {
                        sess.setTaskID(ht.getIdProceso());
                    }
                }
            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisoConstruccion/editarPermisoConstruccion.xhtml?idPermiso="
                    + permiso.getId());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR EN REDIRECCIONAR EDICION PERMISO DE CONSTRUCCION", e);
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

    public PePermisoLazy getPermisolazy() {
        return permisolazy;
    }

    public void setPermisolazy(PePermisoLazy permisolazy) {
        this.permisolazy = permisolazy;
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
