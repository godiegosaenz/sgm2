/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.trigger;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeRequisitosTipoTramite;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeTipoPermisoAdicionales;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
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
import util.Archivo;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CambiarTipoTramite extends BpmManageBeanBaseRoot implements Serializable {

    public static long serialVersionUID = 1L;

    private HistoricoTramites tramite;
    private GeTipoTramite tipo;
    private List<GeTipoTramite> tipos;
    private HashMap<String, Object> params;
    private Observaciones obs = null;
    private List<GeRequisitosTramite> requisitos;
    @javax.inject.Inject
    private Entitymanager serv;;
    @Inject
    private UserSession sess;
    private HashMap<String, Object> parametros = new HashMap<>();
    private String tipoConst;
    private Boolean esPermisoConst = false, reasignar = false;
    private PeTipoPermisoAdicionales tipoPermisoAdc;


    @PostConstruct
    public void init() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                tramite = new HistoricoTramites();
//                tipo = new GeTipoTramite();

                params = new HashMap();
                obs = new Observaciones();
                this.setTaskId(sess.getTaskID());
                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.valueOf(this.getVariable(sess.getTaskID(), "tramite").toString())});
                tipos = serv.findAllEntCopy(Querys.getGeTipoTramiteById, new String[]{"id"}, new Object[]{1L});
            }
        } catch (Exception e) {
            Logger.getLogger(CambiarTipoTramite.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void listarRequisitos() {
        List<GeRequisitosTramite> list = new ArrayList();
        requisitos = null;
        if (tipo != null) {
            esPermisoConst = tipo.getId() == 2;
            requisitos = (List<GeRequisitosTramite>) tipo.getGeRequisitosTramiteCollection();
            try {
                for (GeRequisitosTramite temp : requisitos) {
                    if (temp.getEstado() != null) {
                        if (temp.getEstado().equals("A")) {
                            list.add(temp);
                        }
                    }
                }
                requisitos = list;
            } catch (Exception e) {
                Logger.getLogger(CambiarTipoTramite.class.getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    public void cambiarTipo() {
        try {
            if (this.getFiles() != null && tipo != null) {
                tramite.setTipoTramite(tipo);
                if(esPermisoConst){
                    tramite.setTipoTramiteNombre(tipoConst);
                }else{
                    tramite.setTipoTramiteNombre(tipo.getDescripcion());
                }
                
                if(tramite.getSubTipoTramite()!=null){//OTROS TRAMITES EDIFICACIONES
                    String nombreTramite=tramite.getTipoTramiteNombre()+" - "+tramite.getSubTipoTramite().getTipoTramite();
                    tramite.setTipoTramiteNombre(nombreTramite.length()<=200?nombreTramite:nombreTramite.substring(0, 199));
                }
                
                if (serv.update(tramite) == true) {
                    if(tramite.getTipoTramite().getId().compareTo(6L) == 0){
                        PePermisosAdicionales adic = (PePermisosAdicionales) serv.find(Querys.getPermisoAdicional, new String[]{"numTramite"}, new Object[]{tramite.getId()});
                        if(adic != null){
                            adic.setTipoPermisoAdicional(tipoPermisoAdc);
                        }
                        
                    }
                    obs.setFecCre(new Date());
                    obs.setIdTramite(tramite);
                    obs.setIdProceso(new BigInteger(this.getTaskDataByTaskID().getProcessInstanceId()));
                    obs.setTarea(this.getTaskDataByTaskID().getName());
                    obs.setUserCre(this.getTaskDataByTaskID().getAssignee());
                    serv.persist(obs);
                    this.parametros.put("carpeta", tramite.getCarpetaRep());
                    this.parametros.put("listaArchivos", this.getFiles());
                    this.parametros.put("listaArchivosFinal", new ArrayList<Archivo>());
                    this.parametros.put("descripcion", tipo.getDescripcion()+(tramite.getSubTipoTramite()==null?"":" - "+tramite.getSubTipoTramite().getTipoTramite()));
                    if(this.reasignar){
                        this.parametros.put("reasignar", 2);
                    }else{
                        this.parametros.put("reasignar", 1);
                    }
                    this.completeTask(this.getTaskId(), parametros);
                    this.continuar();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(CambiarTipoTramite.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<GeRequisitosTipoTramite> getTipoConstruccion() {
        return serv.findAll(Querys.getListRequisitosTipoTramitesByTipTra, new String[]{"tipo"}, new Object[]{tipo.getId()});
    }

    public List<PeTipoPermisoAdicionales> getPermisosList() {
        return serv.findAllOrdered(PeTipoPermisoAdicionales.class, new String[]{"descripcion"}, new Boolean[]{true});
    }
    
    public List<OtrosTramites> getOtrosTramites() {
        return serv.findAll(Querys.getOtrosTramites);
    }
    
    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public GeTipoTramite getTipo() {
        return tipo;
    }

    public void setTipo(GeTipoTramite tipo) {
        this.tipo = tipo;
    }

    public List<GeTipoTramite> getTipos() {
        return tipos;
    }

    public void setTipos(List<GeTipoTramite> tipos) {
        this.tipos = tipos;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public String getTipoConst() {
        return tipoConst;
    }

    public void setTipoConst(String tipoConst) {
        this.tipoConst = tipoConst;
    }

    public Boolean getEsPermisoConst() {
        return esPermisoConst;
    }

    public void setEsPermisoConst(Boolean esPermisoConst) {
        this.esPermisoConst = esPermisoConst;
    }

    public Boolean getReasignar() {
        return reasignar;
    }

    public void setReasignar(Boolean reasignar) {
        this.reasignar = reasignar;
    }

    public PeTipoPermisoAdicionales getTipoPermisoAdc() {
        return tipoPermisoAdc;
    }

    public void setTipoPermisoAdc(PeTipoPermisoAdicionales tipoPermisoAdc) {
        this.tipoPermisoAdc = tipoPermisoAdc;
    }

}
