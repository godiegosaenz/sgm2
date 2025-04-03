/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.runtime.ProcessInstance;
import org.primefaces.event.SelectEvent;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class SolicitudServicio extends BpmManageBeanBaseRoot implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of solicitudServicio
     */
    private static final Logger LOG = Logger.getLogger(SolicitudServicio.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    @javax.inject.Inject
    private CatastroServices catas;
    @Inject
    private ServletSession ss;

    private String observ;
    private Boolean entidad = false;
    private Boolean hayEnte = false;
    private boolean audiencia = false;
    private boolean registrada = false;

    private CatEnte enteSolicitante = new CatEnte();
    private CatEnte enteEntidad;
    private EnteCorreo correo = new EnteCorreo();
    private EnteTelefono telefono = new EnteTelefono();
    private SvSolicitudServicios solicitud;
    private HistoricoTramites ht = new HistoricoTramites();
    private VuCatalogo tipoServicio = new VuCatalogo();
    private VuCatalogo lugarAudiencia = new VuCatalogo();
    private CatParroquia parroquia = new CatParroquia();
    private CatCiudadela cdla = new CatCiudadela();

    protected List<EnteTelefono> eliminarTelefono;
    protected List<EnteCorreo> eliminarCorreo;

    protected HashMap<String, Object> paramt;
    private Object valido;

    @PostConstruct
    public void initView() {
        solicitud = new SvSolicitudServicios();
        tipoServicio = service.getVuCatalogoById(42L);
        parroquia = service.getPropiedadHorizontalServices().getCatParroquia(1L);
        lugarAudiencia = service.getVuCatalogoById(43L);
        solicitud.setFechaInconveniente(new Date());
        eliminarCorreo = new ArrayList<>();
        eliminarTelefono = new ArrayList<>();

    }

    public void buscarEnte() {
        try {
            if (enteSolicitante.getCiRuc() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Número de Cédula o Ruc.", "");
                return;
            }
            if (!Utils.validateNumberPattern(enteSolicitante.getCiRuc())) {
                JsfUti.messageInfo(null, "Debe solo números.", "");
                return;
            }
            if (enteSolicitante.getEsPersona()) {
                if (!Utils.validateCCRuc(enteSolicitante.getCiRuc())) {
                    JsfUti.messageInfo(null, "Número de Documento es Invalido.", "");
                    return;
                }
            } else {
//                if (enteSolicitante.getCiRuc().trim().length() != 13) {
//                    JsfUti.messageInfo(null, "Número de Ruc invalido.", "");
//                    return;
//                }
            }
            final Boolean esPersona = enteSolicitante.getEsPersona();
            Map<String, Object> par = new HashMap<>();
            par.put("ciRuc", enteSolicitante.getCiRuc());
            par.put("esPersona", esPersona);
            CatEnte temp = (CatEnte) service.getPropiedadHorizontalServices().getCatEnteByParemt(par);
            if (temp != null) {
                enteSolicitante = temp;
                hayEnte = true;
                entidad = !temp.getEsPersona();
                JsfUti.update("frmSolicitud");
            } else {
                this.formEntes(null, false, !entidad, enteSolicitante.getCiRuc());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void procesarEnte(SelectEvent event) {
        enteSolicitante = (CatEnte) event.getObject();
    }

    public void actualizarFormulario() {
        enteSolicitante = new CatEnte();
        enteEntidad = new CatEnte();
        solicitud = new SvSolicitudServicios();
        hayEnte = false;
        if (entidad) {
            enteSolicitante.setEsPersona(false);
        } else {
            enteSolicitante.setEsPersona(true);
        }

        solicitud.setFechaInconveniente(new Date());
    }

    public void actualizarPanel() {
        audiencia = solicitud.getTipoServicio().getId().compareTo(783L) == 0;
    }

    public List<CatParroquia> getParroquias() {
        return catas.getProrroquias(null);
    }

    public void actualizarCiudadelas() {
        if (parroquia != null) {
            getCiudadelas();
        }
    }

    public List<CatCiudadela> getCiudadelas() {
        try {
            if (parroquia != null) {
                return service.getPropiedadHorizontalServices().getFichaServices().getCiudadelasByParroquia(parroquia.getId());
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void mostrar() {
        try {
            this.formEntes(null, false, !entidad, null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void mostrarEdicion() {
        try {
            this.formEntes(enteSolicitante.getId(), false, !entidad, null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void refrect() {
        JsfUti.redirectFaces("/vistaprocesos/alcaldia/solicitudServicio/solicitudServicio.xhtml");

    }

    public void validar() {
        if (enteSolicitante == null) {
            JsfUti.messageInfo(null, "Solicitante.", "Debe ingresar el solicitante.");
            return;
        }
        if (solicitud.getDescripcionInconveniente() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar la descripcion del hecho.", "");
            return;
        }
        if (entidad && solicitud.getRepresentante() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar el Remitente.", "");
            return;
        }
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");
    }

    public void registrarSolicitud() {
        if (observ == null) {
            JsfUti.messageInfo(null, "Debe Ingresar las Observaciones.", "");
            return;
        }
        paramt = new HashMap<>();
        AclUser secretaria = null;
        GeTipoTramite tipoTramite = service.getPropiedadHorizontalServices().getPermiso().getGeTipoTramiteByActivitiKey("solicitudServicio");
        if (tipoTramite == null) {
            JsfUti.messageInfo(null, "Error", "Comuniquese con sistemas, proceso no disponible.");
            return;
        }
        if (Utils.isEmpty(tipoTramite.getParametrosDisparadorCollection())) {
            JsfUti.messageInfo(null, "Error", "No se encontraron los parametros del flujo comuniquese con sistemas.");
            return;
        }
        try {
            if (!audiencia) {
                solicitud.setParroquia(parroquia);
                solicitud.setCdla(cdla);
            }
            for (ParametrosDisparador pd : tipoTramite.getParametrosDisparadorCollection()) {
                if (audiencia) {
                    paramt.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                    if (pd.getVarResp().equalsIgnoreCase("secretariaAlcaldia")) {
                        secretaria = pd.getResponsable();
                    }
                } else {
                    if (pd.getVarResp().contains("_audiencia")) {
                        paramt.put(pd.getVarResp().replace("_audiencia", ""), pd.getResponsable().getUsuario());
                        if (pd.getVarResp().equalsIgnoreCase("secretariaAlcaldia_audiencia")) {
                            secretaria = pd.getResponsable();
                        }
                    }
                }
            }
            paramt.put("from", SisVars.correo);
            if (secretaria.getEnte() != null && Utils.isNotEmpty(secretaria.getEnte().getEnteCorreoCollection())) {
                paramt.put("to", secretaria.getEnte().getEnteCorreoCollection().get(0).getEmail());
            } else {
                paramt.put("to", "no_tiene_correo@hotmail.com");
            }
            paramt.put("asistenteAlcaldia", session.getName_user());
            paramt.put("carpeta", tipoTramite.getCarpeta());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<>());
            paramt.put("prioridad", 50);
            paramt.put("descripcion", tipoTramite.getDescripcion());// NOMBRE DEL TRAMITE
            paramt.put("audiencia", audiencia);
            HistoricoTramites hts = new HistoricoTramites();

            hts.setCorreos(getCorreos(enteSolicitante.getEnteCorreoCollection()));
            if (!enteSolicitante.getEnteTelefonoCollection().isEmpty()) {
                hts.setTelefonos(enteSolicitante.getEnteTelefonoCollection().get(0).getTelefono());
            }

            hts.setSolicitante(enteSolicitante);
            hts.setEstado("Pendiente");
            hts.setFecha(new Date());
            hts.setTipoTramite(tipoTramite);
            hts.setTipoTramiteNombre(tipoTramite.getDescripcion());
            if (enteSolicitante.getEsPersona() == true) {
                hts.setNombrePropietario(getNombrePropietario(enteSolicitante));
            } else {
                hts.setNombrePropietario(enteSolicitante.getRazonSocial());
            }

            hts.setUserCreador(session.getUserId());
            hts.setId(service.getPropiedadHorizontalServices().getPermiso().generarIdTramite());
            System.out.println("Ente Asiganado a la solicitud " + hts.getSolicitante().getId());
            hts = service.getNormasConstruccion().guardarHistoricoTranites(hts);

            if (hts != null) {
                ht = hts;
                paramt.put("tramite", hts.getId());
                ProcessInstance pro = this.startProcessByDefinitionKey(tipoTramite.getActivitykey(), paramt);
                if (pro != null) {
                    hts.setCarpetaRep(hts.getId() + "-" + pro.getId());
                    hts.setIdProceso(pro.getId());
                    hts.setIdProcesoTemp(pro.getId());
                    service.getPropiedadHorizontalServices().getPermiso().actualizarHistoricoTramites(hts);
                    solicitud.setTramite(hts);
                    System.out.println("Ente asigado a la Solicitud " + enteSolicitante.getId() + " Trámite " + ht.getId());
                    solicitud.setEnteSolicitante(enteSolicitante);
                    solicitud.setStatus("Proceso");
                    solicitud.setSolicitudInterna(true);
                    solicitud.setAsignado(false);
                    solicitud.setFechaCreacion(new Date());
                    solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, hts, session.getName_user(), observ, "Requisitos Solicitud");
                    if (solicitud != null) {
                        System.out.println("Ente guardado " + enteSolicitante.getId() + " en el Trámite " + ht.getId());
                        registrada = true;
                    }
                } else {
//                    hts.setId(null);
//                    service.getPropiedadHorizontalServices().getPermiso().actualizarHistoricoTramites(hts);
                }
                parroquia = solicitud.getParroquia();
                cdla = solicitud.getCdla();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Inicio de Proceso", e);
        }
        JsfUti.executeJS("PF('obs').hide()");
        JsfUti.update("frmSolicitud");
        JsfUti.update("frmSolicitud:enviarSol");
        JsfUti.update("frmSolicitud:ImpSol");
        JsfUti.update("frmSolicitud:BanTar");
        JsfUti.update("@all");
    }

    public void imprimirT() {
        ss.instanciarParametros();
        ss.agregarParametro("P_TITULO", "Número de Trámite");
        ss.agregarParametro("P_SUBTITULO", "S.S. " + ht.getTipoTramite().getDescripcion());
        ss.agregarParametro("P_NUMERO_TRAMITE", ht.getId().toString());
        ss.agregarParametro("NOM_SOLICITANTE", ht.getNombrePropietario());
        ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
        ss.agregarParametro("DESCRIPCION", solicitud.getTipoServicio().getNombre());
        ss.setNombreReporte("plantilla1");
        ss.setTieneDatasource(false);
        JsfUti.update("@all");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public SolicitudServicio() {
    }

    public CatEnte getEnteSolicitante() {
        return enteSolicitante;
    }

    public void setEnteSolicitante(CatEnte enteSolicitante) {
        this.enteSolicitante = enteSolicitante;
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

    public Boolean getEntidad() {
        return entidad;
    }

    public void setEntidad(Boolean entidad) {
        this.entidad = entidad;
    }

    public Boolean getHayEnte() {
        return hayEnte;
    }

    public void setHayEnte(Boolean hayEnte) {
        this.hayEnte = hayEnte;
    }

    public SvSolicitudServicios getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SvSolicitudServicios solicitud) {
        this.solicitud = solicitud;
    }

    public VuCatalogo getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(VuCatalogo tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public boolean isAudiencia() {
        return audiencia;
    }

    public void setAudiencia(boolean audiencia) {
        this.audiencia = audiencia;
    }

    public VuCatalogo getLugarAudiencia() {
        return lugarAudiencia;
    }

    public void setLugarAudiencia(VuCatalogo lugarAudiencia) {
        this.lugarAudiencia = lugarAudiencia;
    }

    public List<EnteTelefono> getEliminarTelefono() {
        return eliminarTelefono;
    }

    public void setEliminarTelefono(List<EnteTelefono> eliminarTelefono) {
        this.eliminarTelefono = eliminarTelefono;
    }

    public List<EnteCorreo> getEliminarCorreo() {
        return eliminarCorreo;
    }

    public void setEliminarCorreo(List<EnteCorreo> eliminarCorreo) {
        this.eliminarCorreo = eliminarCorreo;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public boolean isRegistrada() {
        return registrada;
    }

    public void setRegistrada(boolean registrada) {
        this.registrada = registrada;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public CatCiudadela getCdla() {
        return cdla;
    }

    public void setCdla(CatCiudadela cdla) {
        this.cdla = cdla;
    }

    public CatEnte getEnteEntidad() {
        return enteEntidad;
    }

    public void setEnteEntidad(CatEnte enteEntidad) {
        this.enteEntidad = enteEntidad;
    }

}
