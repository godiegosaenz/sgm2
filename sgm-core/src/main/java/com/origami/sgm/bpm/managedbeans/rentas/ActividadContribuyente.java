/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.RenActividadContribuyente;
import com.origami.sgm.entities.RenActividadContribuyenteTelefono;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author xndysxnchez
 */
@Named(value = "actividadContribuyentes")
@ViewScoped
public class ActividadContribuyente implements Serializable {

    @Inject
    protected UserSession sess;
    @Inject
    private Entitymanager manager;
    private RenActividadContribuyente declaracionContribuyente;
    private CatEnte contribuyente, representanteLegal, contador;
    private Boolean calificacionArtesanal;
    private List<RenActividadContribuyenteTelefono> actividadContribuyenteTelefonos;
    private RenActividadContribuyenteTelefono telefono;
    private List<EnteTelefono> listTlfs;
    private EnteTelefono telefonoEnte;
    private String tlfnNew, tlfnNewContribuyente;

    @PostConstruct
    public void initView() {
        try {
            contribuyente = new CatEnte();
            representanteLegal = new CatEnte();
            contador = new CatEnte();
            actividadContribuyenteTelefonos = new ArrayList<>();
            declaracionContribuyente = new RenActividadContribuyente();
            declaracionContribuyente.setUsuarioIngreso(sess.getName_user());
            declaracionContribuyente.setFechaIngreso(new Date());
            listTlfs = new ArrayList<>();
        } catch (Exception e) {
            Logger.getLogger(ActividadContribuyente.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void buscarEnte() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dialogEnte", options, null);
    }

    public String onFlowProcess(FlowEvent event) {
        if (event.getNewStep().equals("actividades")) {
            if (declaracionContribuyente != null) {
                if (declaracionContribuyente.getContribuyente() != null) {
                    if (declaracionContribuyente.getContribuyente().getId() != null) {
                        Faces.messageWarning(null, "Debe los Datos del Contribuyente", "");
                        Faces.update("growl");
                        return event.getOldStep();
                    }

                }
            }
        }

        return event.getNewStep();
    }

    public void agregarTlfn() {
        if (tlfnNew != null) {
            tlfnNew = tlfnNew.trim();
            Boolean flag = true;
            for (RenActividadContribuyenteTelefono t : actividadContribuyenteTelefonos) {
                if (t.getTelefono().equals(tlfnNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validateNumberPattern(tlfnNew)) {
                    if (PhoneUtils.getValidNumber(tlfnNew, SisVars.region)) {
                        telefono = new RenActividadContribuyenteTelefono();
                        telefono.setTelefono(tlfnNew);
                        actividadContribuyenteTelefonos.add(telefono);
                        tlfnNew = "";
                    } else {
                        JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                    }
                } else {
                    JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }
    
    

    public void eliminarTlfn(RenActividadContribuyenteTelefono tlfn) {
        if (tlfn.getId() != null) {
            actividadContribuyenteTelefonos.remove(tlfn);
        } else {
            int ind = -1;
            int cont = 0;
            for (RenActividadContribuyenteTelefono te : actividadContribuyenteTelefonos) {
                if (te.getTelefono().equals(tlfn.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                actividadContribuyenteTelefonos.remove(ind);
            }
        }
    }

    public void agregarTlfnEnte() {
        if (contribuyente != null) {
            listTlfs = contribuyente.getEnteTelefonoCollection();
        }

        if (tlfnNewContribuyente != null) {
            tlfnNewContribuyente = tlfnNewContribuyente.trim();
            Boolean flag = true;
            if (listTlfs != null) {
                for (EnteTelefono t : listTlfs) {
                    if (t.getTelefono().equals(tlfnNewContribuyente)) {
                        flag = false;
                    }
                }
                if (flag) {
                    if (Utils.validateNumberPattern(tlfnNewContribuyente)) {
                        if (PhoneUtils.getValidNumber(tlfnNewContribuyente, SisVars.region)) {
                            telefonoEnte = new EnteTelefono();
                            telefonoEnte.setTelefono(tlfnNew);
                            telefonoEnte = (EnteTelefono) manager.persist(telefonoEnte);
                            listTlfs.add(telefonoEnte);
                            tlfnNew = "";
                        } else {
                            JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                        }
                    } else {
                        JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                    }
                } else {
                    JsfUti.messageInfo(null, Messages.elementoRepetido, "");
                }
            }

        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }

    public void eliminarTlfnEnte(EnteTelefono tlfn) {
        if (tlfn.getId() != null) {
            listTlfs.remove(tlfn);
            manager.delete(tlfn);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteTelefono te : listTlfs) {
                if (te.getTelefono().equals(tlfn.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                listTlfs.remove(ind);
            }
        }
    }

    public void seleccionarContribuyente(SelectEvent event) {
        this.contribuyente = (CatEnte) EntityBeanCopy.clone(event.getObject());
    }

    public void seleccionarRepresentanteLegal(SelectEvent event) {
        this.representanteLegal = (CatEnte) EntityBeanCopy.clone(event.getObject());
    }

    public void seleccionarContador(SelectEvent event) {
        this.contador = (CatEnte) EntityBeanCopy.clone(event.getObject());
    }

    public RenActividadContribuyente getDeclaracionContribuyente() {
        return declaracionContribuyente;
    }

    public void setDeclaracionContribuyente(RenActividadContribuyente declaracionContribuyente) {
        this.declaracionContribuyente = declaracionContribuyente;
    }

    public CatEnte getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(CatEnte contribuyente) {
        this.contribuyente = contribuyente;
    }

    public CatEnte getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(CatEnte representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnte getContador() {
        return contador;
    }

    public void setContador(CatEnte contador) {
        this.contador = contador;
    }

    public Boolean getCalificacionArtesanal() {
        return calificacionArtesanal;
    }

    public void setCalificacionArtesanal(Boolean calificacionArtesanal) {
        this.calificacionArtesanal = calificacionArtesanal;
    }

    public List<RenActividadContribuyenteTelefono> getActividadContribuyenteTelefonos() {
        return actividadContribuyenteTelefonos;
    }

    public void setActividadContribuyenteTelefonos(List<RenActividadContribuyenteTelefono> actividadContribuyenteTelefonos) {
        this.actividadContribuyenteTelefonos = actividadContribuyenteTelefonos;
    }

    public String getTlfnNew() {
        return tlfnNew;
    }

    public void setTlfnNew(String tlfnNew) {
        this.tlfnNew = tlfnNew;
    }

    public RenActividadContribuyenteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(RenActividadContribuyenteTelefono telefono) {
        this.telefono = telefono;
    }

    public String getTlfnNewContribuyente() {
        return tlfnNewContribuyente;
    }

    public void setTlfnNewContribuyente(String tlfnNewContribuyente) {
        this.tlfnNewContribuyente = tlfnNewContribuyente;
    }

}
