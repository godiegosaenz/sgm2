/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
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
public class SolicitudCorreccionPredioEdif implements Serializable{
    @Inject
    private UserSession usuario;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private ServletSession ss;
    private HistoricoTramites tramite;
    private Long numTramite;
    private CatPredio predio;
    private Long numPredio;
    private SolicitudCorreccionPredio solicitud;
    private Boolean solicitudRegistrada= Boolean.FALSE;
    private AclUser solicitante;
    
    protected SolicitudCorreccionPredioLazy solicitudes;
    protected SolicitudCorreccionPredio solicitudView;
    
    protected Long estadoReporte;
    protected AclUser solicitanteReporte;
    protected AclUser tecnicoReporte;
    protected Date desdeReporte;
    protected Date hastaReporte;
    protected List<AclUser> solicitantes;
    protected List<AclUser> tecnicos;
    protected GeDepartamento depEdificaciones;
    protected GeDepartamento depCatastro;
    
    @PostConstruct
    public void initView() {
        try {
            if (usuario!=null && usuario.esLogueado()) {
                solicitud= new SolicitudCorreccionPredio();
                solicitudRegistrada= Boolean.FALSE;
                solicitante=(AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{usuario.getName_user()});
                if (usuario.getEsDirector()) {
                    solicitudes= new SolicitudCorreccionPredioLazy();
                }else{
                    solicitudes= new SolicitudCorreccionPredioLazy(solicitante,1L);
                }
                
                // REPORTE
                depEdificaciones=(GeDepartamento)manager.find(GeDepartamento.class, 1L);
                depCatastro=(GeDepartamento)manager.find(GeDepartamento.class, 2L);
                tecnicos= new ArrayList<>();
                /*depCatastro.getAclRolCollection().stream().forEach((aclRolCollection) -> {
                    aclRolCollection.getAclUserCollection().stream().filter((aclUserCollection) -> (!tecnicos.contains(aclUserCollection))).forEach((aclUserCollection) -> {
                        tecnicos.add(aclUserCollection);
                    });
                });*/
                for (AclRol rol : depCatastro.getAclRolCollection()) {
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
                
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void buscarTramite() {
        try {
            if (numTramite != null) {
                tramite = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{numTramite});
                if (tramite == null) {
                    Faces.messageWarning(null, "Advertencia", "El tramite que desea buscar, no existe");
                }
                if (tramite != null && !tramite.getEstado().equalsIgnoreCase("pendiente")) {
                    tramite=null;
                    Faces.messageWarning(null, "Advertencia", "El tramite que desea buscar debe estar pendiente");
                }
                
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un # Tramite valido");
            }

        } catch (Exception e) {
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void buscarPredio() {
        try {
            if (numPredio != null) {
                predio = (CatPredio) manager.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{numPredio});
                if (predio == null) {
                    Faces.messageWarning(null, "Advertencia", "El Predio que desea buscar, no existe");
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un # Predio valido");
            }

        } catch (Exception e) {
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void validarSolicitud(){
        try {
            if (solicitud.getDetalleSolicitud()!=null && tramite!=null && predio!=null) {
                // ABRIR DIALOGO
                JsfUti.update("formConfirmacion");
                JsfUti.executeJS("PF('cofirmacion').show();");
            } else{
                Faces.messageWarning(null, "Advertencia", "Tramite, Predio y Detalle de solicitud son campos obligatorios.");
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void registraSolicitud(){
        try {
            if (solicitud.getDetalleSolicitud()!=null && tramite!=null && predio!=null) {
                //Consultar Director Catastro. Se consulta por el Departamento.
                GeDepartamento departamento= manager.find(GeDepartamento.class, 2L);
                for (AclRol rol : departamento.getAclRolCollection()) {
                    if (rol.getIsDirector()&&rol.getEstado()) {
                        for (AclUser user : rol.getAclUserCollection()) {
                            solicitud.setDirectorCatastro(user);
                            break;
                        }
                        break;
                    }
                }
                //Se registra como solicitante el Usuario de la session
                
                solicitud.setSolicitante(solicitante);
                solicitud.setFechaSolicitud(new Date());
                solicitud.setTramite(tramite);
                solicitud.setPredio(predio);
                solicitud.setAccion(new BigInteger("0"));
                solicitud=(SolicitudCorreccionPredio) manager.persist(solicitud);
                // Registrar Observacion en HT
                if (solicitud!=null) {
                    solicitudRegistrada= Boolean.TRUE;
                    Observaciones observacion= new Observaciones();
                    observacion.setIdTramite(tramite);
                    observacion.setObservacion("N. PREDIO:"+predio.getNumPredio()+". DETALLE:"+solicitud.getDetalleSolicitud());
                    observacion.setTarea("SOLICITUD CORRECCION PREDIO");
                    observacion.setUserCre(usuario.getName_user());
                    observacion.setFecCre(new Date());
                    observacion.setEstado(Boolean.TRUE);
                    manager.persist(observacion);
                    Faces.messageInfo(null, "Mensaje", "Registro Exitoso.");
                    JsfUti.update("formSolCorrPredio:tbVSolicitudes");
                    JsfUti.executeJS("PF('cofirmacion').hide();");
                }
            }else{
                Faces.messageWarning(null, "Advertencia", "Tramite, Predio y Detalle de solicitud son campos obligatorios.");
            }
        } catch (Exception e) {
            Faces.messageError(null, "Error", "Error al grabar datos.");
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void nuevaSolicitud(){
        JsfUti.redirectFaces("/vistaprocesos/edificaciones/solicitudCorreccionPredio.xhtml");
    }
    
    public void generarReporte(){
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(true);
            ss.setNombreReporte("solicitudCorreccionPrediosEdificaciones");
            ss.setNombreSubCarpeta("reportesEdificaciones");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("ESTADO", this.estadoReporte);
            ss.agregarParametro("SOLICITANTE", this.solicitanteReporte!=null?this.solicitanteReporte.getId():null);
            ss.agregarParametro("TECNICO", this.tecnicoReporte!=null?this.tecnicoReporte.getId():null);
            ss.agregarParametro("DESDE", this.desdeReporte!=null?this.desdeReporte:(Date) manager.find(Querys.getFechaSolicitudMenor));
            ss.agregarParametro("HASTA", this.hastaReporte!=null?Utils.sumarRestarDiasFecha(this.hastaReporte,1):Utils.sumarRestarDiasFecha((Date) manager.find(Querys.getFechaSolicitudMayor),1));
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            
        } catch (Exception e) {
            Logger.getLogger(SolicitudCorreccionPredioEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void verDetalleSolicitud(SolicitudCorreccionPredio s){
        this.solicitudView=s;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public SolicitudCorreccionPredio getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudCorreccionPredio solicitud) {
        this.solicitud = solicitud;
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

    public Boolean getSolicitudRegistrada() {
        return solicitudRegistrada;
    }

    public void setSolicitudRegistrada(Boolean solicitudRegistrada) {
        this.solicitudRegistrada = solicitudRegistrada;
    }

    public SolicitudCorreccionPredio getSolicitudView() {
        return solicitudView;
    }

    public void setSolicitudView(SolicitudCorreccionPredio solicitudView) {
        this.solicitudView = solicitudView;
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
