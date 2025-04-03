/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatSolicitudNormaConstruccion;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.lazymodels.CatSolicitudNormaConstruccionLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class SolicitudNCConsulta extends BpmManageBeanBaseRoot implements Serializable{
    @Inject
    private ServletSession ss;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    @javax.inject.Inject
    private Entitymanager service;
    
    protected CatSolicitudNormaConstruccionLazy solicitudesNorma;
    protected PdfReporte reporte;
    
    @PostConstruct
    public void initView() {
        solicitudesNorma=new CatSolicitudNormaConstruccionLazy();
    }
    
    public void imprimirNorma(CatSolicitudNormaConstruccion solicitud){
        try {
            ss.instanciarParametros();
            AclUser user = permisoService.getAclUserByUser(session.getName_user());
            String nombreTecnico=(user!=null&&user.getEnte()!=null)?((user.getEnte().getNombres()!=null?user.getEnte().getNombres().toUpperCase():"")+" "+(user.getEnte().getApellidos()!=null?user.getEnte().getApellidos().toUpperCase():"")):null;
            GeTipoTramite tramite= (GeTipoTramite)service.find(GeTipoTramite.class, 1L);
            AclUser firmaDirector = permisoService.getAclUserByUser(tramite.getUserDireccion());
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            
            if (solicitud.getNormaConstruccion()!=null) {
                ss.agregarParametro("logo", solicitud.getNormaConstruccion().getImafoto()!=null?new ByteArrayInputStream(solicitud.getNormaConstruccion().getImafoto()):null);
                ss.setNombreReporte(solicitud.getNormaConstruccion().getTipoNorma().getIsEspecial()?"normaConstruccionSinCiudadela":"reportePrueba");
            }
            ss.agregarParametro("firmaDirec", path.concat("css/firmas/" + firmaDirector.getRutaImagen() + ".jpg"));
            ss.agregarParametro("firmaTecni",((user!=null&&user.getRutaImagen()!=null)?path.concat("css/firmas/" + user.getRutaImagen() + ".jpg"):null));
            ss.agregarParametro("logotipo", path.concat("css/homeIconsImages/logo.jpg"));
            ss.agregarParametro("id_solicitud", solicitud.getId());
            ss.agregarParametro("nomresponsable", Utils.isEmpty(solicitud.getIdResponsable().getTituloProf()) + " " + solicitud.getIdResponsable().getApellidos() + " " + solicitud.getIdResponsable().getNombres());
            ss.agregarParametro("regprofesional", solicitud.getIdResponsable().getRegProf());
            ss.agregarParametro("ciprofesional", solicitud.getIdResponsable().getCiRuc());
            ss.agregarParametro("SUBREPORT_DIR", path + "reportes/normasConstruccion/");
            ss.agregarParametro("NUMTRAMITE", solicitud.getTramite() + "-" + solicitud.getAnioTramite());
            ss.agregarParametro("NOMBRE_TECNICO", nombreTecnico);
            
            ss.setNombreSubCarpeta("normasConstruccion");
            ss.setTieneDatasource(true);
            
            reporte = new PdfReporte();
            
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            
        } catch (Exception e) {
                Logger.getLogger(SolicitudNCConsulta.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void editar(CatSolicitudNormaConstruccion solicitud){
        ss.instanciarParametros();
        ss.agregarParametro("idSolicitudNorma", solicitud.getId());
        Faces.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/solicitudNCEdicion.xhtml");
    }

    public CatSolicitudNormaConstruccionLazy getSolicitudesNorma() {
        return solicitudesNorma;
    }

    public void setSolicitudesNorma(CatSolicitudNormaConstruccionLazy solicitudesNorma) {
        this.solicitudesNorma = solicitudesNorma;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }
    
}
