/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.SolicitudCorreccionPredio;
import com.origami.sgm.lazymodels.SolicitudCorreccionPredioLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
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
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class CorreccionPredios implements Serializable{
    @Inject
    private UserSession usuario;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private ServletSession ss;
    
    private SolicitudCorreccionPredio solicitud;
    private AclUser director;
    private AclUser tecnico;
    protected SolicitudCorreccionPredioLazy solicitudes;
    protected GeDepartamento departamento;
    
    protected Long estadoReporte;
    protected AclUser solicitanteReporte;
    protected AclUser tecnicoReporte;
    protected Date desdeReporte;
    protected Date hastaReporte;
    protected List<AclUser> solicitantes;
    protected List<AclUser> tecnicos;
    protected GeDepartamento depEdificaciones;
    
    @PostConstruct
    public void initView() {
        try {
            if (usuario!=null && usuario.esLogueado()) {
                departamento=(GeDepartamento)manager.find(GeDepartamento.class, 2L);
                depEdificaciones=(GeDepartamento)manager.find(GeDepartamento.class, 1L);
                tecnicos= new ArrayList<>();
                /*departamento.getAclRolCollection().stream().forEach((aclRolCollection) -> {
                    aclRolCollection.getAclUserCollection().stream().filter((aclUserCollection) -> (!tecnicos.contains(aclUserCollection))).forEach((aclUserCollection) -> {
                        tecnicos.add(aclUserCollection);
                    });
                });*/
                for (AclRol rol : departamento.getAclRolCollection()) {
                    for (AclUser ac : rol.getAclUserCollection()) {
                        if (!tecnicos.contains(ac)) {
                            tecnicos.add(ac);
                        }
                    }
                }
                solicitantes=new ArrayList<>();
                /*depEdificaciones.getAclRolCollection().stream().forEach((rol)->{
                    rol.getAclUserCollection().stream().filter((user)->(!solicitantes.contains(user))).forEach((user)->{
                        solicitantes.add(user);
                    });
                });*/
                for (AclRol rol : depEdificaciones.getAclRolCollection()) {
                    for (AclUser ac : rol.getAclUserCollection()) {
                        if (!solicitantes.contains(ac)) {
                            solicitantes.add(ac);
                        }
                    }
                }
                
                if (usuario.getEsDirector()) {
                    director=(AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{usuario.getName_user()});
                    solicitudes= new SolicitudCorreccionPredioLazy(director,2L);
                }else{
                    tecnico=(AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{usuario.getName_user()});
                    solicitudes= new SolicitudCorreccionPredioLazy(tecnico,3L);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(CorreccionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void asignarSolicitud(SolicitudCorreccionPredio s){
        this.solicitud=s;
    }
    
    public void asignarTecnico(AclUser usuario){
        try{
            solicitud.setTecnicoCatastro(usuario);
            solicitud.setAccion(new BigInteger("1"));//1: ASIGNACION A TECNICO
            solicitud.setFechaAsignacion(new Date());
            solicitud=(SolicitudCorreccionPredio) manager.persist(solicitud);
            Faces.messageInfo(null, "Mensaje", "Asignacion Exitosa.");
        }catch (Exception e) {
            Logger.getLogger(CorreccionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void registarInforme(){
        try{
            solicitud.setAccion(new BigInteger("2"));//2: SOLICITUD REALIZADA 
            solicitud.setFechaInforme(new Date());
            solicitud=(SolicitudCorreccionPredio) manager.persist(solicitud);
            
            if (solicitud!=null) {
                Observaciones observacion= new Observaciones();
                observacion.setIdTramite(solicitud.getTramite());
                observacion.setObservacion("N. PREDIO:"+solicitud.getPredio().getNumPredio()+". INFORME:"+solicitud.getDetalleInforme());
                observacion.setTarea("INFORME CORRECCION PREDIO");
                observacion.setUserCre(usuario.getName_user());
                observacion.setFecCre(new Date());
                observacion.setEstado(Boolean.TRUE);
                manager.persist(observacion);
                Faces.messageInfo(null, "Mensaje", "Registro de Informe Exitoso.");
            }
        }catch (Exception e) {
            Logger.getLogger(CorreccionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void editarPredio(SolicitudCorreccionPredio s){
        this.solicitud=s;
        ss.instanciarParametros();
        ss.agregarParametro("numPredio", s.getPredio().getNumPredio());
        ss.agregarParametro("idPredio", s.getPredio().getId());
        ss.agregarParametro("edit", true);
        Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/editarPredio.xhtml");
    }
    
    public void generarReporte(){
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(true);
            ss.setNombreReporte("solicitudesCorreccionPrediosCatastro");
            ss.setNombreSubCarpeta("catastro");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("ESTADO", this.estadoReporte);
            ss.agregarParametro("DIRECTOR", this.director!=null?this.director.getId():null);
            ss.agregarParametro("NOMBRE_DIRECTOR", this.director!=null?this.director.getUsuario()+(this.director.getEnte()!=null?"-"+this.director.getEnte().getApellidos()+" "+this.director.getEnte().getNombres():""):null);
            ss.agregarParametro("SOLICITANTE", this.solicitanteReporte!=null?this.solicitanteReporte.getId():null);
            ss.agregarParametro("TECNICO", this.tecnicoReporte!=null?this.tecnicoReporte.getId():null);
            ss.agregarParametro("DESDE", this.desdeReporte!=null?this.desdeReporte:(Date) manager.find(Querys.getFechaSolicitudMenor));
            ss.agregarParametro("HASTA", this.hastaReporte!=null?Utils.sumarRestarDiasFecha(this.hastaReporte,1):Utils.sumarRestarDiasFecha((Date) manager.find(Querys.getFechaSolicitudMayor),1));
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            
        } catch (Exception e) {
            Logger.getLogger(CorreccionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public UserSession getUsuario() {
        return usuario;
    }

    public void setUsuario(UserSession usuario) {
        this.usuario = usuario;
    }

    public SolicitudCorreccionPredioLazy getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(SolicitudCorreccionPredioLazy solicitudes) {
        this.solicitudes = solicitudes;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public SolicitudCorreccionPredio getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudCorreccionPredio solicitud) {
        this.solicitud = solicitud;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Long getEstadoReporte() {
        return estadoReporte;
    }

    public void setEstadoReporte(Long estadoReporte) {
        this.estadoReporte = estadoReporte;
    }

    public AclUser getSolicitanteReporte() {
        return solicitanteReporte;
    }

    public void setSolicitanteReporte(AclUser solicitanteReporte) {
        this.solicitanteReporte = solicitanteReporte;
    }

    public AclUser getTecnicoReporte() {
        return tecnicoReporte;
    }

    public void setTecnicoReporte(AclUser tecnicoReporte) {
        this.tecnicoReporte = tecnicoReporte;
    }

    public Date getDesdeReporte() {
        return desdeReporte;
    }

    public void setDesdeReporte(Date desdeReporte) {
        this.desdeReporte = desdeReporte;
    }

    public Date getHastaReporte() {
        return hastaReporte;
    }

    public void setHastaReporte(Date hastaReporte) {
        this.hastaReporte = hastaReporte;
    }

    public List<AclUser> getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(List<AclUser> solicitantes) {
        this.solicitantes = solicitantes;
    }

    public List<AclUser> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<AclUser> tecnicos) {
        this.tecnicos = tecnicos;
    }
    
}
