/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.censocat.restful.JsonUtils;
import com.origami.config.MainConfig;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FinanPrestamoPredio;
import com.origami.sgm.entities.historic.Predio;
import com.origami.sgm.predio.models.FichaEdificacionesModel;
import com.origami.sgm.entities.predio.models.FichaModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class Historico implements Serializable {

    protected MainConfig mainConfig;

    private static final long serialVersionUID = 8799656478674716638L;
    private Long val = null;
    private Predio historico;
    @Inject
    private UserSession sess;
    @Inject
    private Entitymanager manager;
    private CatPredio pant, pact;
    private List<CatPredioPropietario> propietarioAnt;
    private List<CatPredioPropietario> propietarioAct;
    private List<CatEscritura> escrituraAct, escrituraAnt;
    private CatEscritura escriAct, escriAnt;
    private List<CtlgItem> instalacionesEspecialesAnt, instalacionesEspecialesAct;
    private FichaEdificacionesModel fichaAnt, fichaAct;
    private ArrayList<FichaModel> fpredsAnt, fpredsAct;
    private FinanPrestamoPredio prestamoPredioAnt, prestamoPredioAct;
    private static final Logger log = Logger.getLogger(Historico.class.getName());

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    private void iniView() {
        try {
            if (sess != null) {
                if (val != null) {
                    this.mainConfig = new MainConfig();
                    instalacionesEspecialesAnt = new ArrayList<>();
                    instalacionesEspecialesAct = new ArrayList<>();
                    historico = manager.find(Predio.class, val);
                    if (historico != null) {
                        JsonUtils js = new JsonUtils();
                        if (historico.getFichaAnt() != null) {//@JsonIgnore
                            pant = js.jsonToObject(historico.getFichaAnt(), CatPredio.class);
                        }
                        if (historico.getFichaAct() != null) {
                            pact = js.jsonToObject(historico.getFichaAct(), CatPredio.class);
                        }
                        loadPropietario();
                        listarViaseInstalacionesEspeciales();
                        loadEscrituras();
                        loadprestamo();
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Historico.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void loadPropietario() {
        CatPredioPropietario propietario;
        propietarioAnt = new ArrayList<CatPredioPropietario>();
        propietarioAct = new ArrayList<CatPredioPropietario>();
        if (pant != null) {
            if (pant.getCatPredioPropietarioCollection() != null && !pant.getCatPredioPropietarioCollection().isEmpty()) {
                for (CatPredioPropietario p : pant.getCatPredioPropietarioCollection()) {
                    propietario = new CatPredioPropietario();
                    if (p.getId() != null) {
                        if (p.getTipo() == null) {
                            propietario = (CatPredioPropietario) manager.find(CatPredioPropietario.class, p.getId());
                            if (propietario != null) {
                                propietarioAnt.add(propietario);
                            }
                        } else {
                            propietarioAnt.add(p);
                        }
                    }
                    if (p.getId() != null && p.getTipo() != null && p.getEnte().getCiRuc() != null) {
                        propietarioAnt.add(p);
                    }
                }
            }
        }
        if (pact != null) {
            if (pact.getCatPredioPropietarioCollection() != null && !pact.getCatPredioPropietarioCollection().isEmpty()) {
                for (CatPredioPropietario p : pact.getCatPredioPropietarioCollection()) {
                    propietario = new CatPredioPropietario();
                    if (p.getId() != null) {
                        if (p.getTipo() == null) {
                            propietario = (CatPredioPropietario) manager.find(CatPredioPropietario.class, p.getId());
                            if (propietario != null) {
                                propietarioAct.add(propietario);
                            }
                        } else {
                            propietarioAct.add(p);
                        }
                    }
                    if (p.getId() != null && p.getTipo().getId() != null && p.getEnte().getCiRuc() != null) {
                        propietarioAct.add(p);
                    }

                }
            }
        }
    }

    public void loadprestamo() {
        prestamoPredioAnt = new FinanPrestamoPredio();
        prestamoPredioAct = new FinanPrestamoPredio();
        if (pant != null) {
            if (this.pant.getId() != null) {
                prestamoPredioAnt = (FinanPrestamoPredio) manager.find(Querys.getFinanciamientoPredio, new String[]{"predio"}, new Object[]{pant.getId()});
            }
        }
        if (pact != null) {
            if (this.pact.getId() != null) {
                prestamoPredioAct = (FinanPrestamoPredio) manager.find(Querys.getFinanciamientoPredio, new String[]{"predio"}, new Object[]{pact.getId()});
            }
        }
    }

    public List<CtlgItem> getListado(String argumento) {
        HiberUtil.newTransaction();
        return manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
    }

    protected void listarViaseInstalacionesEspeciales() {
        Collection<CtlgItem> xinstalacionesAnt, xinstalacionesAct;
        if (pant != null) {
            if (pant.getCatPredioS6() != null) {
                xinstalacionesAnt = pant.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial();
                if (xinstalacionesAnt != null && !xinstalacionesAnt.isEmpty()) {
                    for (CtlgItem v : xinstalacionesAnt) {
                        instalacionesEspecialesAnt.add(v);
                    }
                }
            }
        }
        if (pact != null) {
            if (pact.getCatPredioS6() != null) {
                xinstalacionesAct = pact.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial();
                if (xinstalacionesAct != null && !xinstalacionesAct.isEmpty()) {
                    for (CtlgItem v : xinstalacionesAct) {
                        instalacionesEspecialesAct.add(v);
                    }
                }
            }
        }
    }

    public void loadEscrituras() {
        escrituraAnt = new ArrayList<>();
        escrituraAct = new ArrayList<>();

        if (pant != null && pant.getId() != null) {
            Object idant = manager.find(Querys.getIdEscrituraAnt, new String[]{"predio", "predio1"}, new Object[]{pant.getId(), pant.getId()});
            if (pant.getCatEscrituraCollection() != null) {
                escrituraAnt = (List<CatEscritura>) pant.getCatEscrituraCollection();
                if (idant != null) {
                    escriAnt = escrituraAnt.get(escrituraAnt.indexOf(new CatEscritura(Long.valueOf(idant.toString()))));
                }
            }
        }
        if (pact != null && pact.getId() != null) {
            Object idactual = manager.find(Querys.getIdEscrituraActual, new String[]{"predio"}, new Object[]{pact.getId()});
            if (pact.getCatEscrituraCollection() != null) {
                escrituraAct = (List<CatEscritura>) pact.getCatEscrituraCollection();
                if (idactual != null) {
                    escriAct = escrituraAct.get(escrituraAct.indexOf(new CatEscritura(Long.valueOf(idactual.toString()))));
                }

            }
        }

    }

    public String completarCeros(Number n, Integer numCaracteres) {
        try {
            return Utils.completarCadenaConCeros(n.toString(), numCaracteres);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Completar con ceros", e);
        }
        return "";
    }

    public List<CatEscritura> getEscrituraAct() {
        return escrituraAct;
    }

    public void setEscrituraAct(List<CatEscritura> escrituraAct) {
        this.escrituraAct = escrituraAct;
    }

    public List<CatEscritura> getEscrituraAnt() {
        return escrituraAnt;
    }

    public void setEscrituraAnt(List<CatEscritura> escrituraAnt) {
        this.escrituraAnt = escrituraAnt;
    }

    public Long getVal() {
        return val;
    }

    public void setVal(Long val) {
        this.val = val;
    }

    public Predio getHistorico() {
        return historico;
    }

    public void setHistorico(Predio historico) {
        this.historico = historico;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public CatPredio getPant() {
        return pant;
    }

    public void setPant(CatPredio pant) {
        this.pant = pant;
    }

    public CatPredio getPact() {
        return pact;
    }

    public void setPact(CatPredio pact) {
        this.pact = pact;
    }

    public List<CatPredioPropietario> getPropietarioAnt() {
        return propietarioAnt;
    }

    public void setPropietarioAnt(List<CatPredioPropietario> propietarioAnt) {
        this.propietarioAnt = propietarioAnt;
    }

    public List<CatPredioPropietario> getPropietarioAct() {
        return propietarioAct;
    }

    public void setPropietarioAct(List<CatPredioPropietario> propietarioAct) {
        this.propietarioAct = propietarioAct;
    }

    public List<CtlgItem> getInstalacionesEspecialesAnt() {
        return instalacionesEspecialesAnt;
    }

    public void setInstalacionesEspecialesAnt(List<CtlgItem> instalacionesEspecialesAnt) {
        this.instalacionesEspecialesAnt = instalacionesEspecialesAnt;
    }

    public List<CtlgItem> getInstalacionesEspecialesAct() {
        return instalacionesEspecialesAct;
    }

    public void setInstalacionesEspecialesAct(List<CtlgItem> instalacionesEspecialesAct) {
        this.instalacionesEspecialesAct = instalacionesEspecialesAct;
    }

    public FichaEdificacionesModel getFichaAnt() {
        return fichaAnt;
    }

    public void setFichaAnt(FichaEdificacionesModel fichaAnt) {
        this.fichaAnt = fichaAnt;
    }

    public FichaEdificacionesModel getFichaAct() {
        return fichaAct;
    }

    public void setFichaAct(FichaEdificacionesModel fichaAct) {
        this.fichaAct = fichaAct;
    }

    public List<FichaModel> getFpredsAnt() {
        return fpredsAnt;
    }

    public void setFpredsAnt(ArrayList<FichaModel> fpredsAnt) {
        this.fpredsAnt = fpredsAnt;
    }

    public List<FichaModel> getFpredsAct() {
        return fpredsAct;
    }

    public void setFpredsAct(ArrayList<FichaModel> fpredsAct) {
        this.fpredsAct = fpredsAct;
    }

    public FinanPrestamoPredio getPrestamoPredioAnt() {
        return prestamoPredioAnt;
    }

    public void setPrestamoPredioAnt(FinanPrestamoPredio prestamoPredioAnt) {
        this.prestamoPredioAnt = prestamoPredioAnt;
    }

    public FinanPrestamoPredio getPrestamoPredioAct() {
        return prestamoPredioAct;
    }

    public void setPrestamoPredioAct(FinanPrestamoPredio prestamoPredioAct) {
        this.prestamoPredioAct = prestamoPredioAct;
    }

    public CatEscritura getEscriAct() {
        return escriAct;
    }

    public void setEscriAct(CatEscritura escriAct) {
        this.escriAct = escriAct;
    }

    public CatEscritura getEscriAnt() {
        return escriAnt;
    }

    public void setEscriAnt(CatEscritura escriAnt) {
        this.escriAnt = escriAnt;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public void setMainConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

}
