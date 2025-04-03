/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import util.JsfUti;

/**
 *
 * @author supergold
 */
@Named
@ViewScoped
public class AprobarDocTecnico extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    private HistoricoTramites tramite;
    private Observaciones observ;
    private MsgFormatoNotificacion msg;
    private AclUser usr;
    private CatPredio predio;
    private HashMap<String, Object> paramt;
    private StreamedContent file;
    private DefaultStreamedContent download;

    @PostConstruct
    public void initView() {
        if (sess != null && sess.getTaskID() != null) {
            this.setTaskId(sess.getTaskID());
            tramite = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString()));
            if (tramite.getNumPredio() != null) {
                predio = (CatPredio) serv.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});
            }
            if (tramite != null) {
                this.setCorreosAdjuntos(getCorreosByCatEnte(tramite.getSolicitante()));
            }
            msg = (MsgFormatoNotificacion) serv.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{1L});
            usr = (AclUser) serv.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariable(sess.getTaskID(), "digitalizador")});
            observ = new Observaciones();
            paramt = new HashMap<>();
        } else {
            this.continuar();
        }
    }

    /**
     * Completa la tarea.
     */
    public void completarTarea() {
        paramt.put("revision", true);
        paramt.put("prioridad", 50);
        paramt.put("tecnico", sess.getName_user());
        this.completeTask(this.getTaskId(), paramt);
        this.continuar();
    }

    /**
     * De acuerdo al caso, se aprueba, desaprueba o cancela el documento.
     *
     * @param x
     */
    public void aprobacion(int x) {
        try {
            observ.setEstado(Boolean.TRUE);
            observ.setFecCre(new Date());
            observ.setIdTramite(tramite);
            observ.setUserCre(sess.getName_user());
            paramt.put("from", SisVars.correo);
            observ.setTarea(this.getTaskDataByTaskID().getName());
            tramite.setCorreccion(0L);
            switch (x) {
                case 0:
                    if (usr != null && usr.getEnte() != null) {
                        paramt.put("to", this.getCorreosByCatEnte(usr.getEnte()));
                        paramt.put("subject", "Cambiar el tipo de tramite ");
                        paramt.put("message", msg.getHeader() + "<br/>" + observ.getObservacion() + "<br/>" + msg.getFooter());
                    }
                    paramt.put("actualizarTramite", false);
                    break;
                case 1:
                    paramt.put("to", this.getCorreosAdjuntos());
                    paramt.put("actualizarTramite", true);
                    paramt.put("tramite", tramite.getId());
                    paramt.put("carpeta", tramite.getTipoTramite().getCarpeta());
                    paramt.put("listaArchivos", this.getVariable(this.getTaskId(), "listaArchivosFinal"));
                    paramt.put("listaArchivosFinal", new ArrayList());
                    paramt.put("idproceso", tramite.getTipoTramite().getActivitykey());
                    paramt.put("director", this.getVariable(this.getTaskId(), "asignador"));
                    paramt.put("tecnico", this.getTaskDataByTaskID().getAssignee());
                    paramt.put("digitalizador", this.getVariable(this.getTaskId(), "digitalizador"));
                    List<Long> renta = new ArrayList<>();
                    renta.add(98L); // Jefe Renta 98
                    List<AclUser> usersRenta = acl.getTecnicosByRol(renta);
                    for (AclUser u : usersRenta) {
                        if (u.getSisEnabled() && u.getUserIsDirector()) {
                            paramt.put("rentas", u.getUsuario());
                        }
                    }
                    break;
                case 2:
                    paramt.put("actualizarTramite", false);
                    paramt.put("to", this.getCorreosAdjuntos());
                    paramt.put("subject", "Documentos faltantes Tramite No. " + tramite.getId());
                    paramt.put("message", msg.getHeader() + observ.getObservacion() + msg.getFooter());
                    tramite.setCorreccion(1L);
                    break;
            }
            paramt.put("aprobado", x);
            paramt.put("prioridad", 50);
            serv.persist(tramite);
            if (serv.persist(observ) != null) {
                this.completeTask(this.getTaskId(), paramt);
                this.continuar();
            } else {
                JsfUti.messageWarning(null, "Advertencia", "La accion no se pudo realizar");
            }
        } catch (Exception e) {
            Logger.getLogger(AprobarDocTecnico.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void enviarCorrecciones() {
        paramt.put("revision", false);
    }

    /**
     * Muestra el documento seleccionado en otra pestaña.
     *
     * @param url
     */
    public void verDocumento(String url) {
        JsfUti.redirectNewTab(url);
    }

    /**
     * Descarga el archivo seleccionado.
     *
     * @param url
     * @param name
     * @throws FileNotFoundException
     */
    public void descargarArchivo(String url, String name) throws FileNotFoundException {
        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(url);
        file = new DefaultStreamedContent(stream);
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObserv() {
        return observ;
    }

    public void setObserv(Observaciones observ) {
        this.observ = observ;
    }

    public HashMap<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(HashMap<String, Object> paramt) {
        this.paramt = paramt;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public DefaultStreamedContent getDownload() {
        return download;
    }

    public void setDownload(DefaultStreamedContent download) {
        this.download = download;
    }

    public MsgFormatoNotificacion getMsg() {
        return msg;
    }

    public void setMsg(MsgFormatoNotificacion msg) {
        this.msg = msg;
    }

    public AclUser getUsr() {
        return usr;
    }

    public void setUsr(AclUser usr) {
        this.usr = usr;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

}
