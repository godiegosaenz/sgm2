/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro.excepciones;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatExcepciones;
import com.origami.sgm.entities.CatExcepcionesDet;
import com.origami.sgm.entities.CatExcepcionesParam;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class ExcepcionesCat extends Busquedas implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession sess;
    private String observaciones;
    private List<GeTipoTramite> tipoTramites;
    private List<CatPredio> predios;
    private Boolean mostrar = false;
    private HashMap<String, Object> params;
    private CatExcepcionesParam cep;
    private CatExcepciones exc;
    private ArrayList<CatExcepcionesDet> det;
    private static final Logger LOG = Logger.getLogger(ExcepcionesCat.class.getName());

    @PostConstruct
    protected void load() {
        try {
            params = new HashMap<>();
            cep = (CatExcepcionesParam) manager.find(Querys.getCatExcParams, new String[]{"prefijo"}, new Object[]{"AEP-01"});
            if (cep != null) {
                String[] pf = cep.getTipoTramPref().split("-");
                List<String> lpf = new ArrayList<>();
                lpf = Arrays.asList(pf);
                if (pf.length > 0) {
                    params.put("abreviatura", lpf);
                }
                tipoTramites = manager.findIn(GeTipoTramite.class, null, params);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void selecc(CatExcepciones ex) {
        if (ex != null) {
            exc = ex;
            exc.setFecAct(new Date());
            exc.setUsrCre(sess.getName_user());
        } else {
            exc = new CatExcepciones();
            exc.setEstado(true);
            exc.setFecCre(new Date());
            exc.setUsrCre(sess.getName_user());
            det = new ArrayList<>();
        }
    }

    public void validarSeleccion() {
        try {
            switch (exc.getTipoTramite().getAbreviatura().toUpperCase()) {
                case "FP":
                    break;
                case "DP":
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void mostrarPredios() {
        this.mostrarDialog("/resources/dialog/predios");
    }

    public void seleccionarObject(SelectEvent event) {
        try {
            predios = (List<CatPredio>) event.getObject();
            mostrar = predios != null && !predios.isEmpty();
            Faces.update("frmSolExo:panel");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<GeTipoTramite> getTipoTramites() {
        return tipoTramites;
    }

    public void setTipoTramites(List<GeTipoTramite> tipoTramites) {
        this.tipoTramites = tipoTramites;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public CatExcepciones getExc() {
        return exc;
    }

    public void setExc(CatExcepciones exc) {
        this.exc = exc;
    }

    public ArrayList<CatExcepcionesDet> getDet() {
        return det;
    }

    public void setDet(ArrayList<CatExcepcionesDet> det) {
        this.det = det;
    }

}
