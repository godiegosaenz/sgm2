/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.reingreso;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DatosTramite;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeRequisitosTipoTramite;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeTipoPermisoAdicionales;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import com.origami.sgm.services.interfaces.reingreso.ReingresoTramService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class ReingresoTramites extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    private HistoricoTramites tramite;
    @javax.inject.Inject
    private RegistroPropiedadServices reg;
    @javax.inject.Inject
    private ReingresoTramService reingreso;
    @Inject
    private ServletSession servletSession;
    @Inject
    private UserSession sess;
    private Long numTramite;
    private CatEnte solicitante;
    private EnteCorreo correo;
    private EnteTelefono telefono;
    private boolean res = false, telfs = false, mails = false, mostrarRequisitos = false, esPermisoConst = false, mostrarPredio = false, otros = false, otrosReq = false, pa = false;
    private List<GeTipoTramite> tipoTramites;
    private CatPredio predio;
    private CatEnteLazy solicitantes;
    private List<CatPredio> predios;
    private List<GeRequisitosTramite> requisitos;
    private List<OtrosTramites> otrosTramites;
    private String tipoConst;
    private HashMap<String, Object> params;
    private Observaciones obs = null;
    protected CatPredioLazy prediosLazy;
    private List<Attachment> adjuntos;
    private PeTipoPermisoAdicionales tipoPermiso;
    List<PeTipoPermisoAdicionales> permisosAdicionales;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void load() {
        if (sess != null) {
            tipoTramites = manager.findAllEntCopy(Querys.getGeTipoTramitesByState, new String[]{"estado"}, new Object[]{true});
            solicitantes = new CatEnteLazy();
            predios = new ArrayList<>();
            params = new HashMap();
            obs = new Observaciones();
            correo = new EnteCorreo();
            telefono = new EnteTelefono();
            tipoPermiso = null;
            prediosLazy = new CatPredioLazy("A");
            otrosTramites = manager.findAll(Querys.getOtrosTramites);
            permisosAdicionales = manager.findAllEntCopy(PeTipoPermisoAdicionales.class);
        }
    }

    public void buscar() {
        try {
            if (numTramite != null && numTramite > 0) {
                DatosTramite dt = reg.getTramiteAnterior(numTramite);
                if (dt != null && dt.getId() != null) {//existe en wf
                    if (dt.getCi() != null) {
                        tramite = new HistoricoTramites();
                        if (dt.getTipoTramite() != null) {
                            GeTipoTramite tt = manager.find(GeTipoTramite.class, dt.getTipoTramite());
                            if (tt != null) {
                                tramite.setTipoTramite(tt);
                            }
                        }
                        res = true;
                        solicitante = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{dt.getCi()});
                        if (solicitante == null) {
                            solicitante = new CatEnte();
                            solicitante.setFechaCre(new Date());
                            correo = new EnteCorreo();
                            telefono = new EnteTelefono();
                        } else {
                            tramite.setSolicitante(solicitante);
                            if (solicitante.getEsPersona()) {
                                tramite.setNombrePropietario(solicitante.getApellidos().toUpperCase() + " " + solicitante.getNombres().toUpperCase());
                            } else {
                                tramite.setNombrePropietario(solicitante.getRazonSocial().toUpperCase());
                            }
                            if (solicitante.getEnteCorreoCollection().isEmpty()) {
                                mails = false;
                                correo = new EnteCorreo();
                            } else {
                                mails = true;
                            }
                            if (solicitante.getEnteTelefonoCollection().isEmpty()) {
                                telfs = false;
                                telefono = new EnteTelefono();
                            } else {
                                telfs = true;
                            }
                        }
                    } else {
                        res = true;
                        tramite = new HistoricoTramites();
                        solicitante = new CatEnte();
                        solicitante.setFechaCre(new Date());
                        correo = new EnteCorreo();
                        telefono = new EnteTelefono();
                        Faces.messageWarning(null, "Advertencia", "El tramite, no tiene un solicitante registrado, por favor registrelo");
                    }
                    if (dt.getNumPredio() != null) {
                        predio = (CatPredio) manager.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{dt.getNumPredio()});
                        mostrarPredio = predio != null;
                    } else {
                        mostrarPredio = true;
                        predio = new CatPredio();
                    }
                } else {
                    tramite = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{numTramite});
                    if (tramite != null) {
                        if (tramite.getEstado() != null && !tramite.getEstado().equalsIgnoreCase("finalizado")) {
                            res = true;
                            adjuntos = this.getProcessInstanceAllAttachments(numTramite);
                            this.listarRequisitos();
                            Faces.messageInfo(null, "Nota", "Este tramite se encuentra en estado " + tramite.getEstado());
                            solicitante = tramite.getSolicitante();
                            if (tramite.getNumPredio() != null) {
                                predio = (CatPredio) manager.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});
                                mostrarPredio = predio != null;
                            } else {
                                mostrarPredio = true;
                                predio = new CatPredio();
                            }
                        } else {
                            res = false;
                            Faces.messageWarning(null, "Advertencia", "Este tramite se encuentra finalizado no es posible re-ingresarlo ");
                        }
                    } else {
                        res = false;
                        Faces.messageWarning(null, "Advertencia", "El tramite ingresado no existe");
                    }
                }
            } else {
                res = false;
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un No. Tramite valido.");
            }
        } catch (Exception e) {
            Logger.getLogger(ReingresoTramites.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void seleccionar() {
        if (solicitante == null) {
            mails = false;
            telfs = false;
            Faces.messageWarning(null, "Advertencia", "El tramite, no tiene un solicitante registrado, por favor registrelo");
        } else {
            mails = solicitante.getEnteCorreoCollection() != null;
            telfs = solicitante.getEnteTelefonoCollection() != null;
            for (CatPredioPropietario var : solicitante.getCatPredioPropietarioCollection()) {
                if (var.getPredio() != null && var.getEstado().equals("A") && var.getPredio().getEstado().equals("A")) {
                    predios.add(var.getPredio());

                }
            }
        }
    }

    public void nuevoSolicitane() {
        Faces.redirectFacesNewTab("/faces/generic/entefaces.xhtml");
    }

    public void listarRequisitos() {
        try {
            if (tramite.getTipoTramite() != null) {
                if (tramite.getTipoTramite().getAbreviatura() != null) {
                    if (tramite.getTipoTramite().getAbreviatura().equalsIgnoreCase("ote")) {
                        otros = !otrosTramites.isEmpty();
                        mostrarRequisitos = otros;
                        tipoPermiso = null;
                    } else if (tramite.getTipoTramite().getAbreviatura().equalsIgnoreCase("pa")) {
                        otros = false;
                        pa = true;
                        mostrarRequisitos = pa;
                        tramite.setSubTipoTramite(null);
                    } else {
                        otros = false;
                        otrosReq = false;
                        mostrarRequisitos = true;
                    }
                }
                requisitos = this.getRequisitos(tramite.getTipoTramite(), true, tramite.getTipoTramite().getActivitykey());
                mostrarRequisitos = true;
                esPermisoConst = tramite.getTipoTramite().getId() == 2L;
            } else {
                otros = false;
                pa = false;
                mostrarRequisitos = false;
                esPermisoConst = false;
                tipoPermiso = null;
                Faces.messageWarning(null, "Advertencia", "Debe seleccionar el tipo de tramite respectivo");
            }
        } catch (Exception e) {
            Logger.getLogger(ReingresoTramites.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<GeRequisitosTipoTramite> getTipoConstruccion() {
        return manager.findAll(Querys.getListRequisitosTipoTramitesByTipTra, new String[]{"tipo"}, new Object[]{tramite.getTipoTramite().getId()});
    }

    public void reingresarTramite() {
        try {
            boolean disparador = false;
            //if (!this.getFiles().isEmpty() && tramite.getTipoTramite() != null && solicitante != null && solicitante.getCiRuc() != null) {
            if (tramite.getTipoTramite() != null && solicitante != null && solicitante.getCiRuc() != null) {
                //MODIFICADO 17-01-2017 Henry Pilco
                //tramite.setFecha(new Date());
                //Fin Modificacion
                if (predio != null && predio.getId() != null) {
                    tramite.setNumPredio(predio.getNumPredio());
                }
                if (solicitante.getEnteCorreoCollection().isEmpty() && correo.getEmail() == null) {
                    Faces.messageWarning(null, "Advertencia", "Debe ingresar al menos un email");
                    return;
                }
                if (solicitante.getEnteTelefonoCollection().isEmpty() && telefono.getTelefono() == null) {
                    Faces.messageWarning(null, "Advertencia", "Debe ingresar al menos un telefono");
                    return;
                }
                obs.setUserCre(sess.getName());
                tramite.setUserCreador(sess.getUserId());
                tramite.setId(numTramite);
                if (tramite.getSubTipoTramite() != null) {
                    tramite.setTipoTramiteNombre(tramite.getSubTipoTramite().getTipoTramite().toUpperCase());
                } else if (!esPermisoConst) {
                    tramite.setTipoTramiteNombre(tramite.getTipoTramite().getDescripcion().toUpperCase());
                } else if (tipoPermiso != null) {
                    tramite.setTipoTramiteNombre(tipoPermiso.getDescripcion().toUpperCase());
                }
                HistoricoTramites temp = reingreso.reingresarTramite(tramite, solicitante, correo, telefono, obs);
                if (temp != null) {
                    if (tramite.getTipoTramite().getDisparador() != null) {
                        disparador = true;
                        params.put("carpeta", tramite.getTipoTramite().getDisparador().getCarpeta());
                        for (ParametrosDisparador pd : tramite.getTipoTramite().getParametrosDisparadorCollection()) {
                            params.put(pd.getVarInterfaz(), pd.getInterfaz());
                            if (pd.getResponsable() != null) {
                                params.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                            }
                        }
                    } else {
                        params.put("carpeta", tramite.getTipoTramite().getCarpeta());
                    }
                    if (this.getFiles().isEmpty()) {
                        params.put("tdocs", false);
                    } else {
                        params.put("tdocs", true);
                    }
                    params.put("reasignar", 2);
                    params.put("listaArchivos", this.getFiles());
                    params.put("listaArchivosFinal", new ArrayList<>());
                    params.put("prioridad", 50);
                    params.put("iniciar", false);
                    params.put("descripcion", tramite.getTipoTramite().getDescripcion());
                    params.put("tramite", temp.getId());
                    ProcessInstance p = null;
                    if (disparador == true) {
                        p = this.startProcessByDefinitionKey(tramite.getTipoTramite().getDisparador().getDescripcion(), params);
                        temp.setIdProcesoTemp(p.getId());
                    } else {
                        p = this.startProcessByDefinitionKey(tramite.getTipoTramite().getDescripcion(), params);
                        temp.setIdProceso(p.getId());
                    }
                    if (p != null) {
                        temp.setCarpetaRep(temp.getId() + "-" + p.getId());
                        manager.persist(temp);
                        servletSession.instanciarParametros();
                        servletSession.agregarParametro("P_TITULO", tramite.getTipoTramite().getDescripcion());
                        servletSession.agregarParametro("P_SUBTITULO", "");
                        servletSession.agregarParametro("P_NUMERO_TRAMITE", temp.getId().toString());
                        servletSession.agregarParametro("NOM_SOLICITANTE", temp.getNombrePropietario());
                        //servletSession.agregarParametro("DIRECCION", predio.getCiudadela().getNombre() + " MZ: " + predio.getUrbMz() + " SL: " + predio.getSolar());
                        servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                        servletSession.agregarParametro("DESCRIPCION", tramite.getTipoTramite().getDescripcion());
                        servletSession.setNombreReporte("plantilla1");
                        servletSession.setTieneDatasource(false);
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                        this.continuar();
                    } else {
                        Faces.messageError(null, "Error", "No se ha podido re-Ingresar el tramite, por favor verifique que los campos del formulario se ecuentren ingresados correctamente");
                    }
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar los requisitos del tramite");
            }
        } catch (Exception e) {
            Logger.getLogger(ReingresoTramites.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public EnteCorreo getCorreo() {
        return correo;
    }

    public void setCorreo(EnteCorreo correo) {
        this.correo = correo;
    }

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public boolean getRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public List<GeTipoTramite> getTipoTramites() {
        return tipoTramites;
    }

    public void setTipoTramites(List<GeTipoTramite> tipoTramites) {
        this.tipoTramites = tipoTramites;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public boolean getTelfs() {
        return telfs;
    }

    public void setTelfs(boolean telfs) {
        this.telfs = telfs;
    }

    public boolean getMails() {
        return mails;
    }

    public void setMails(boolean mails) {
        this.mails = mails;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public boolean getEsPermisoConst() {
        return esPermisoConst;
    }

    public void setEsPermisoConst(boolean esPermisoConst) {
        this.esPermisoConst = esPermisoConst;
    }

    public String getTipoConst() {
        return tipoConst;
    }

    public void setTipoConst(String tipoConst) {
        this.tipoConst = tipoConst;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public boolean getMostrarPredio() {
        return mostrarPredio;
    }

    public void setMostrarPredio(boolean mostrarPredio) {
        this.mostrarPredio = mostrarPredio;
    }

    public CatPredioLazy getPrediosLazy() {
        return prediosLazy;
    }

    public void setPrediosLazy(CatPredioLazy prediosLazy) {
        this.prediosLazy = prediosLazy;
    }

    public boolean getOtros() {
        return otros;
    }

    public void setOtros(boolean otros) {
        this.otros = otros;
    }

    public List<OtrosTramites> getOtrosTramites() {
        return otrosTramites;
    }

    public void setOtrosTramites(List<OtrosTramites> otrosTramites) {
        this.otrosTramites = otrosTramites;
    }

    public boolean getOtrosReq() {
        return otrosReq;
    }

    public void setOtrosReq(boolean otrosReq) {
        this.otrosReq = otrosReq;
    }

    public List<Attachment> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Attachment> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public List<PeTipoPermisoAdicionales> getPermisosAdicionales() {
        return permisosAdicionales;
    }

    public void setPermisosAdicionales(List<PeTipoPermisoAdicionales> permisosAdicionales) {
        this.permisosAdicionales = permisosAdicionales;
    }

    public boolean getPa() {
        return pa;
    }

    public void setPa(boolean pa) {
        this.pa = pa;
    }

    public PeTipoPermisoAdicionales getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(PeTipoPermisoAdicionales tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

}
