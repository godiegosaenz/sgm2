/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PePermiso;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
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
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class RevTramDir extends BpmManageBeanBaseRoot implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RevTramDir.class.getName());

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    private HistoricoTramites ht;
    private List<HistoricoReporteTramite> hrts;
    private HistoricoReporteTramite hrt;
    private HashMap<String, Object> datosIniciales;
    private String nombreReporte;
    private Boolean aprobar = false;
    private Boolean isSac = false;
    private Boolean esReporte;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private PePermiso permiso = new PePermiso();
    private CatEnte ente;
    private Object numLiquidacion;
    private PePermiso permis;
    private GeTipoTramite tipoTramite;
    private Boolean pagoRealizado;
    private String mensaje;
    private Boolean mostrarBoton = false;
    private Boolean mostarMensaja = true;
    private String pagado = null;
    private MsgFormatoNotificacion formatoMsg;
    protected GeDepartamento departamento;

    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                params = new HashMap();
                obs = new Observaciones();
                datosIniciales = new HashMap<>();
                this.setTaskId(uSession.getTaskID());
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht == null) {
                    return;
                }
                if(this.getvariableByExecutionId(uSession.getTaskID(), "taskdef")!=null)
                    nombreReporte = this.getvariableByExecutionId(uSession.getTaskID(), "taskdef").toString();
                tipoTramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());

                tipoTramite = ht.getTipoTramite();
                if (tipoTramite.getLlegaCatastro() == true) {
                    if (tipoTramite.getId().intValue() == 7 || tipoTramite.getId().intValue() == 8 || tipoTramite.getId().intValue() == 9 || tipoTramite.getId().intValue() == 36
                            || tipoTramite.getId().intValue() == 15 || tipoTramite.getId().intValue() == 17 || (ht.getId().compareTo(new Long("1450")) == 1)) {
                        if (ht.getNumLiquidacion() != null) {
                            pagado = permisoServices.consultaPagoLiquidacion(tipoTramite.getId().intValue(), ht);
                        }
                        if (pagado == null) {
                            mostarMensaja = false;
                        } else {
                            if ("P".equalsIgnoreCase(pagado)) {
                                mostrarBoton = true;
                                mensaje = "El Usuario ya Cancelo la Tasa Dar clic en el boton 'Tasa Cancelada' para continuar con las siguientes Tareas";
                            } else {
                                mostrarBoton = false;
                                mensaje = "Hasta el Momento el Usuario aun NO CANCELA su Tasa de Liquidación, al momento que el Usuario Cancela su Tasa de Liquidación se mostrara un Boton 'Tasa Cancelada' ";
                            }
                        }
                    } else {
                        mensaje = "El Usuario ya Cancelo la Tasa Dar clic en el boton 'Tasa Cancelada'";
                    }
                }
                //hrts = servicesDP.obtenerHistoricoReporteTramiteListPorQuery(Querys.getReporteByNombreTareaSinEstado, new String[]{"nombreTarea", "idProceso"}, new Object[]{nombreReporte, this.ht.getIdProceso()});
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                if (hrts != null && !hrts.isEmpty()) {
                    esReporte = true;
                    for (HistoricoReporteTramite hrtTemp : hrts) {
                        if (hrtTemp.getEstado()) {
                            hrt = hrtTemp;
                        }
                    }
                } else {
                    esReporte = false;
                }
                // CONSULTA EL DEPARTAMENTO RENTA QUE TIENE EL ID 12
                departamento=(GeDepartamento)services.find(GeDepartamento.class, 12L);
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RevTramDir.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void mostrarObservaciones(int estatus) {
        String userTecnico;
        AclUser tecnicoUser;
        String msg="";
        
        switch (estatus) {
            case 1:
                aprobar = true;
                params.put("renta", "renta");
                // SE ASIGNA LA TAREA AL USUARIO CON ROL JEFE RENTA ID ROL: 98
                if (departamento!=null && departamento.getAclRolCollection()!=null && !departamento.getAclRolCollection().isEmpty()) {
                    for (AclRol rol : departamento.getAclRolCollection()) {
                        if (rol.getId().equals(98L) && rol.getAclUserCollection()!=null && !rol.getAclUserCollection().isEmpty()) {
                            for (AclUser user : rol.getAclUserCollection()) {
                                params.put("renta", user.getUsuario());
                                break;
                            }
                            break;
                        }
                    }
                }
                break;
            case 2:
                aprobar = false;
                break;
            case 3:
                if (this.getVariable(this.getTaskId(), "urlTec") != null) {
                    aprobar = false;
                } else {
                    JsfUti.messageInfo(null, "Advertencia", "Para continuar con el tramite, se debe asociar la tarea del tecnico");
                    return;
                }
                break;
        }
        ht.setLiquidacionAprobada(aprobar);
        params.put("descripcion", ht.getTipoTramiteNombre());
        params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
        params.put("prioridad", 50);
        params.put("aprobado", aprobar);
        if(!aprobar){
            msg = formatoMsg.getHeader() + "Se rechazaron documentos. Revise su bandeja de tareas el trámite: "+ht.getId() + formatoMsg.getFooter();
            userTecnico = (String) getvariableByExecutionId(uSession.getTaskID(), "tecnico");
            tecnicoUser = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{userTecnico});
            params.put("to", getCorreosByCatEnte(tecnicoUser.getEnte()));
            params.put("message", msg);
            params.put("subject", "Trámite: "+ht.getId());
        }
        params.put("estatus", estatus);
        params.put("listaArchivos", this.getFiles());
        params.put("listaArchivosFinal", new ArrayList<>());
        JsfUti.executeJS("PF('obs').show();");
        JsfUti.update("frmObs");
    }

    public void completarTarea() {
        try {
            if (obs.getObservacion() != null) {
                params.put("tramite", ht.getId());
                obs.setEstado(true);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setTarea(this.getTaskDataByTaskID().getName());
                obs.setUserCre(uSession.getName_user());
                obs = permisoServices.guardarObservacion(obs);
                if (obs != null) {
                    params.put("idProcess", uSession.getTaskID());
                    this.completeTask(this.uSession.getTaskID(), params);
                    this.continuar();
                }
            } else {
                JsfUti.messageInfo(null, "Debe Ingresar la Observación de la Tarea.", "");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null) {
            obs.setObservacion(this.getTaskDataByTaskID().getName());
        }
    }

    public void verDocumento(HistoricoReporteTramite doc) {
        this.showDocuments(doc.getUrl(), "pdf");
    }

    public void descargarDocumento(HistoricoReporteTramite doc) {
        this.descargarDocumento(doc.getUrl());
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public Boolean getAprobar() {
        return aprobar;
    }

    public void setAprobar(Boolean aprobar) {
        this.aprobar = aprobar;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public PePermiso getPermiso() {
        return permiso;
    }

    public void setPermiso(PePermiso permiso) {
        this.permiso = permiso;
    }

    public HistoricoReporteTramite getHrt() {
        return hrt;
    }

    public void setHrt(HistoricoReporteTramite hrt) {
        this.hrt = hrt;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public HashMap<String, Object> getDatosIniciales() {
        return datosIniciales;
    }

    public void setDatosIniciales(HashMap<String, Object> datosIniciales) {
        this.datosIniciales = datosIniciales;
    }

    public Boolean getEsReporte() {
        return esReporte;
    }

    public void setEsReporte(Boolean esReporte) {
        this.esReporte = esReporte;
    }

    public Boolean getIsSac() {
        return isSac;
    }

    public void setIsSac(Boolean isSac) {
        this.isSac = isSac;
    }

    public Object getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(Object numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public Boolean getPagoRealizado() {
        return pagoRealizado;
    }

    public void setPagoRealizado(Boolean pagoRealizado) {
        this.pagoRealizado = pagoRealizado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(Boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }

    public Boolean getMostarMensaja() {
        return mostarMensaja;
    }

    public void setMostarMensaja(Boolean mostarMensaja) {
        this.mostarMensaja = mostarMensaja;
    }

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }

}
