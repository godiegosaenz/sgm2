package com.origami.sgm.bpm.managedbeans.agenda;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DatosAgenda;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.Colores;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.TipoAviso;
import com.origami.sgm.entities.TipoEvento;
import com.origami.sgm.lazymodels.AgendaDetLazy;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.agenda.AgendaServ;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class AgendaTrabajo extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private AgendaServ serv;
    @javax.inject.Inject
    private SeqGenMan seq;
    private DefaultScheduleModel calendario;
    private List<TipoEvento> eventos;
    private List<AclUser> involucrados, invSel, directores;
    private Agenda agenda;
    private AgendaDet detAgenda;
    private List<AgendaDet> det;
    private TipoAviso tipoAviso;
    private DefaultScheduleEvent evento;
    private SimpleDateFormat sdf;
    private CatEnte responsable;
    private boolean habilitado = false;
    private HistoricoTramites tramite;
    private AclUser us, directorSelec;
    private DatosAgenda datos, cDatos;
    private HashMap<String, Object> params;
    private List<Attachment> adjuntos;
    private AgendaDetLazy misEventos;
    private List<Colores> colores;
    private Colores color;
    private boolean director = false, docs = false;
    private String emails = "";
    private MsgFormatoNotificacion msg;
    private int intervalos;
    @javax.inject.Inject
    private DatoSeguroServices datoSeguro;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void init() {
        if (sess != null) {
            us = manager.find(AclUser.class, sess.getUserId());
            if (us.getEnte() != null) {
                director = us.getUserIsDirector();
                directores = manager.findAll(Querys.getUsersEntesDirectores);
                params = new HashMap<>();
                responsable = us.getEnte();
                agenda = new Agenda();
                tipoAviso = new TipoAviso();
                evento = new DefaultScheduleEvent();
                calendario = new DefaultScheduleModel();
                eventos = manager.findAllEntCopy(TipoEvento.class);
                involucrados = manager.findAll(Querys.getUsersEntes);
                habilitado = true;
                tramite = new HistoricoTramites();
                colores = manager.findAll(Colores.class);
                this.cargarCalendario();
                misEventos = new AgendaDetLazy(us.getEnte().getId());
                msg = (MsgFormatoNotificacion) manager.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{2L});
                //datoSeguro.getDatos("0908911456", true);
            } else {
                habilitado = false;
            }
        }
    }

    public void cargarCalendario() {
        try {
            List<AgendaDet> ldet = manager.findAll(Querys.getAgendaDetxInvolucrado, new String[]{"involucrado"}, new Object[]{us.getEnte().getId()});
            for (AgendaDet d : ldet) {
                evento = new DefaultScheduleEvent();
                if (d.getAgenda().getDescripcion() != null) {
                    evento.setTitle(d.getAgenda().getTipo().getDescripcion() + " - " + d.getAgenda().getDescripcion().toUpperCase());
                    evento.setData(d.getAgenda().getDescripcion().toUpperCase());
                } else {
                    evento.setData("");
                    evento.setTitle(d.getAgenda().getTipo().getDescripcion());
                }
                evento.setStartDate(d.getAgenda().getFInicio());
                if (d.getAgenda().getFFin() != null) {
                    evento.setEndDate(d.getAgenda().getFFin());
                } else {
                    evento.setEndDate(d.getAgenda().getFInicio());
                }
                if (d.getAgenda().getFinalizado() != null) {
                    evento.setStyleClass(".custom .ui-state-highlight { background: green-grad !important;}");
                } else {
                    evento.setStyleClass(d.getAgenda().getColor());
                }
                evento.setId(d.getAgenda().getId().toString());
                calendario.addEvent(evento);
                evento.setId(d.getAgenda().getId().toString());
                calendario.updateEvent(evento);
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaTrabajo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agendar() {
        try {
            if (evento.getId() == null) {
                emails = "";

                if (evento.getStartDate().getTime() >= (new Date()).getTime()) {
                    det = new ArrayList<>();
                    datos = new DatosAgenda();
                    agenda.setColor(color.getEstilo());
                    agenda.setFInicio(evento.getStartDate());
                    agenda.setFFin(evento.getEndDate());
                    agenda.setFecCre(new Date());
                    agenda.setEstado(Boolean.TRUE);
                    agenda.setResponsable(responsable);
                    tramite.setId(seq.getSecuenciasTram("SGM"));
                    tramite.setFecha(new Date());
                    tramite.setEstado("Pendiente");
                    GeTipoTramite tipo = (GeTipoTramite) manager.find(Querys.getTipoTramitexAbreviatura, new String[]{"abreviatura"}, new Object[]{"AGT"});
                    tramite.setTipoTramite(tipo);
                    tramite.setTipoTramiteNombre(tipo.getDescripcion().toUpperCase());
                    if (invSel != null) {
                        for (AclUser u : invSel) {
                            detAgenda = new AgendaDet();
                            detAgenda.setAsistencia(true);
                            detAgenda.setInvolucrado(u.getEnte());
                            detAgenda.setFecCre(new Date());
                            detAgenda.setEstado(true);
                            detAgenda.setUsuario(u.getUsuario());
                            det.add(detAgenda);
                            if (u.getEnte() != null) {
                                if (u.getEnte().getEnteCorreoCollection() != null && !u.getEnte().getEnteCorreoCollection().isEmpty()) {
                                    emails = emails + u.getEnte().getEnteCorreoCollection().get(0).getEmail() + ",";
                                }
                            }
                        }
                    }
                    detAgenda = new AgendaDet();
                    detAgenda.setAsistencia(true);
                    detAgenda.setInvolucrado(responsable);
                    detAgenda.setFecCre(new Date());
                    detAgenda.setEstado(true);
                    detAgenda.setUsuario(us.getUsuario());
                    det.add(detAgenda);
                    tramite.setSolicitante(responsable);
                    tramite.setNombrePropietario(responsable.getApellidos() + " " + responsable.getNombres());
                    agenda.setPrioridad(50);
                    datos.setAgenda(agenda);
                    datos.setAvisos(tipoAviso);
                    datos.setDetAgenda(det);
                    datos.setTramite(tramite);
                    if (responsable.getEnteCorreoCollection() != null && !responsable.getEnteCorreoCollection().isEmpty()) {
                        emails = emails + responsable.getEnteCorreoCollection().get(0).getEmail();
                        //System.out.println("emails " + emails);
                    }
                    params.put("to", emails);
                    params.put("from", SisVars.correo);
                    params.put("subject", agenda.getTipo().getDescripcion());
                    params.put("message", msg.getHeader() + "<br/>" + agenda.getDescripcion() + "<br/>" + msg.getFooter());
                    cDatos = serv.guardarAgenda(datos);
                    if (cDatos.getModelAgenda() != null && !cDatos.getModelAgenda().isEmpty()) {
                        if (this.getFiles() != null) {
                            if (this.getFiles().isEmpty()) {
                                params.put("tdocs", false);
                                docs = false;
                            } else {
                                params.put("tdocs", true);
                                docs = true;
                            }
                        } else {
                            params.put("tdocs", false);
                            docs = false;
                        }
                        params.put("responsable", us.getUsuario());

                        if (!director) {
                            params.put("aprobacion", true);
                            if (directorSelec != null) {
                                params.put("director", directorSelec.getUsuario());
                            } else {
                                Faces.messageWarning(null, "Advertencia", "Debe seleccionar el director respectivo");
                                return;
                            }
                        } else {
                            params.put("aprobacion", false);
                        }
                        params.put("tramite", tramite.getId());
                        params.put("agenda", cDatos.getAgenda().getId());
                        params.put("carpeta", tipo.getCarpeta());
                        params.put("listaArchivos", this.getFiles());
                        params.put("listaArchivosFinal", new ArrayList<>());
                        params.put("involucrados", cDatos.getModelAgenda());
                        params.put("fecha", this.getFecha(agenda, tipoAviso));
                        params.put("prioridad", 50);
                        ProcessInstance pi = startProcessByDefinitionKey(tipo.getActivitykey(), params);
                        if (pi != null) {
                            if (docs) {
                                cDatos.getTramite().setCarpetaRep(cDatos.getTramite().getId() + "-" + pi.getId());
                            }
                            cDatos.getTramite().setIdProceso(pi.getId());
                            manager.persist(cDatos.getTramite());
                            cDatos.getAgenda().setIdProceso(pi.getId());
                            manager.persist(cDatos.getAgenda());
                            evento.setId(cDatos.getAgenda().getId().toString());
                            calendario.addEvent(new DefaultScheduleEvent(agenda.getDescripcion().toUpperCase(), agenda.getFInicio(), agenda.getFInicio()));
                            Faces.messageInfo(null, "Nota", "Agendado");
                            Faces.executeJS("PF('dlgEvento').hide()");
                        }
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia", "Las fechas ingresadas no son validas");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaTrabajo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        evento = (DefaultScheduleEvent) selectEvent.getObject();
        if (evento != null) {
            agenda = manager.find(Agenda.class, Long.parseLong(evento.getId()));
            adjuntos = this.getProcessInstanceAttachmentsFiles(agenda.getIdProceso());
            if (!agenda.getTipoAvisoList().isEmpty()) {
                tipoAviso = agenda.getTipoAvisoList().get(0);
            }
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        evento = new DefaultScheduleEvent();
        agenda = new Agenda();
        tipoAviso = new TipoAviso();
        tipoAviso.setNotificacion(true);
    }

    protected Date getFecha(Agenda a, TipoAviso ta) {
        Calendar cl = Calendar.getInstance();
        try {
            cl.setTime(a.getFInicio());

            if (ta != null && ta.getAlerta() && intervalos > 0) {
                switch (intervalos) {
                    case 1:
                        cl.set(Calendar.HOUR, cl.get(Calendar.HOUR) - intervalos);
                        break;
                    case 2:
                        cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) - intervalos);
                        break;
                    case 3:
                        cl.set(Calendar.HOUR, cl.get(Calendar.HOUR) - 2);
                        break;
                }
            } else {
                cl.set(Calendar.MINUTE, cl.get(Calendar.MINUTE) - 10);
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaTrabajo.class.getName()).log(Level.SEVERE, null, e);
        }
        return cl.getTime();
    }

    public DefaultScheduleModel getCalendario() {
        return calendario;
    }

    public void setCalendario(DefaultScheduleModel calendario) {
        this.calendario = calendario;
    }

    public ScheduleEvent getEvento() {
        return evento;
    }

    public void setEvento(DefaultScheduleEvent evento) {
        this.evento = evento;
    }

    public List<TipoEvento> getEventos() {
        return eventos;
    }

    public void setEventos(List<TipoEvento> eventos) {
        this.eventos = eventos;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public TipoAviso getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(TipoAviso tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public List<AclUser> getInvolucrados() {
        return involucrados;
    }

    public void setInvolucrados(List<AclUser> involucrados) {
        this.involucrados = involucrados;
    }

    public List<AclUser> getInvSel() {
        return invSel;
    }

    public void setInvSel(List<AclUser> invSel) {
        this.invSel = invSel;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public CatEnte getResponsable() {
        return responsable;
    }

    public void setResponsable(CatEnte responsable) {
        this.responsable = responsable;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public List<Attachment> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Attachment> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public AgendaDetLazy getMisEventos() {
        return misEventos;
    }

    public void setMisEventos(AgendaDetLazy misEventos) {
        this.misEventos = misEventos;
    }

    public List<Colores> getColores() {
        return colores;
    }

    public void setColores(List<Colores> colores) {
        this.colores = colores;
    }

    public Colores getColor() {
        return color;
    }

    public void setColor(Colores color) {
        this.color = color;
    }

    public boolean getDirector() {
        return director;
    }

    public void setDirector(boolean director) {
        this.director = director;
    }

    public List<AclUser> getDirectores() {
        return directores;
    }

    public void setDirectores(List<AclUser> directores) {
        this.directores = directores;
    }

    public AclUser getDirectorSelec() {
        return directorSelec;
    }

    public void setDirectorSelec(AclUser directorSelec) {
        this.directorSelec = directorSelec;
    }

    public int getIntervalos() {
        return intervalos;
    }

    public void setIntervalos(int intervalos) {
        this.intervalos = intervalos;
    }

}
