/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.catastro.FusionDivisionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author dfcalderio
 */
@Named(value = "fusionarPredioView")
@ViewScoped
public class FusionarPredio extends PredioUtil implements Serializable {

    @javax.inject.Inject
    private FusionDivisionServices fusionDivisionEjb;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private CatastroServices catastroService;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private ServletSession ss;

    private List<CatPredio> prediosEnManzana;
    private List<CatPredio> predioFiltrados;
    private List<CatPredio> prediosSeleccionados;
    private Collection<CatPredioEdificacion> edificaciones;
    private CatPredio predio;
    private Long idPredio;
    private Short nuevoCodigo;

//<editor-fold defaultstate="collapsed" desc="LOAD">
    @PostConstruct
    public void init() {
        nuevoCodigo = (short) 1;
        try {
            if (sess != null) {
                prediosSeleccionados = new ArrayList<>();
                prediosEnManzana = new ArrayList<>();
                edificaciones = new ArrayList<>();
                if (ss.getParametros().get("idPredio") != null) {
                    idPredio = Long.parseLong(ss.getParametros().get("idPredio").toString());
                    predio = catastroService.getPredioId(idPredio);
                    if (predio != null) {
                        String[] param = {"parroquia", "zona", "sector", "mz"};
                        Object[] values = {predio.getParroquia(), predio.getZona(), predio.getSector(), predio.getMz()};
                        prediosEnManzana = manager.findAllEntCopy(Querys.getLisPrediosByManzana, param, values);

                        for (int i = 0; i < prediosEnManzana.size(); i++) {
                            if (i == 0) {
                                nuevoCodigo = prediosEnManzana.get(i).getSolar();
                            } else {
                                if (nuevoCodigo < prediosEnManzana.get(i).getSolar()) {
                                    nuevoCodigo = prediosEnManzana.get(i).getSolar();
                                }
                            }
                        }
                        List<CatPredio> tempPredio = new ArrayList<>();
                        for (CatPredio p : prediosEnManzana) {
                            if (p.getEstado().equals("A") && !p.equals(predio)) {
                                tempPredio.add(p);
                            }
                        }
                        prediosEnManzana = tempPredio;
                        if (prediosEnManzana.isEmpty()) {
                            nuevoCodigo = predio.getSolar();
                        }

                        ++nuevoCodigo;
                        this.setUsr(sess.getName_user());
//                        List<FichaModel> modelFicha = catastroService.getDatosFicha(idPredio);
//
//                        GsonBuilder builder = new GsonBuilder();
//                        builder.excludeFieldsWithoutExposeAnnotation()
//                                .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
//                                .excludeFieldsWithModifiers(Modifier.STATIC)
//                                .serializeNulls().setPrettyPrinting();
//                        Gson gson2 = builder.create();
//                        System.out.println("Print model: " + gson2.toJson(modelFicha));
                    } else {
                        JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
                    }
                } else {
                    JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
                }
            }

        } catch (NumberFormatException e) {
            Logger.getLogger(FusionarPredio.class.getName()).log(Level.SEVERE, null, e);
        }
    }
//</editor-fold>

    public void fusionar() {

        CatPredio nuevo = fusionDivisionEjb.clonarPredioFusion(predio, prediosSeleccionados, nuevoCodigo, true);

        if (nuevo.getId() != null) {
            ss.agregarParametro("numPredio", nuevo.getNumPredio());
            ss.agregarParametro("idPredio", nuevo.getId());
            ss.agregarParametro("edit", true);
            this.saveHistoric(predio, "UNIFICACIÒN DE PREDIOS", null, null, null, null, Boolean.FALSE);
            Faces.redirectFaces("/faces/vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml");
        }

    }

    public BigDecimal areaTotalEdificacion(CatPredio p) {
        BigDecimal area = BigDecimal.ZERO;
        edificaciones = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{p.getId()});
        for (CatPredioEdificacion e : edificaciones) {
            if (e.getAreaConsCenso() != null) {
                area = area.add(e.getAreaConsCenso());
            }
        }
        return area;
    }

    public Integer cantidadEdificaciones(CatPredio p) {

        Collection<CatPredioEdificacion> eds = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{p.getId()});
        return eds.size();
    }

    public BigDecimal areaTotalTerrenoNueva() {
        BigDecimal areaTotal = BigDecimal.ZERO;
        for (CatPredio p : prediosSeleccionados) {
            if (p.getAreaSolar() != null) {
                areaTotal = areaTotal.add(p.getAreaSolar());
            }
        }

        return areaTotal;
    }

    public BigDecimal areaTotalConstruccionNueva() {
        BigDecimal areaTotal = BigDecimal.ZERO;
        for (CatPredio p : prediosSeleccionados) {
            Collection<CatPredioEdificacion> eds = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{p.getId()});
            for (CatPredioEdificacion e : eds) {
                if (e.getAreaConsCenso() != null) {
                    areaTotal = areaTotal.add(e.getAreaConsCenso());
                }
            }

        }
        return areaTotal;
    }

    public BigDecimal areaTotalEdificacionInicial() {
        BigDecimal area = BigDecimal.ZERO;
        if (ss.getParametros().get("idPredio") != null) {
            Collection<CatPredioEdificacion> eds = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{predio.getId()});
            if (Utils.isNotEmpty(eds)) {
                for (CatPredioEdificacion e : eds) {
                    if (e.getAreaConsCenso() != null) {
                        area = area.add(e.getAreaConsCenso());
                    }
                }
            }
        }
        return area;
    }

//<editor-fold defaultstate="collapsed" desc="Getter and Setter">
    public String onFlowProcess(FlowEvent event) {

        return event.getNewStep();
    }

    public void onRowSelect(SelectEvent event) {

    }

    public void onRowUnselect(UnselectEvent event) {

    }

    public Short getNuevoCodigo() {
        return nuevoCodigo;
    }

    public void setNuevoCodigo(Short nuevoCodigo) {
        this.nuevoCodigo = nuevoCodigo;
    }

    public Collection<CatPredioEdificacion> getEdificaciones() {
        return edificaciones;
    }

    public void setEdificaciones(Collection<CatPredioEdificacion> edificaciones) {
        this.edificaciones = edificaciones;
    }

    public List<CatPredio> getPrediosEnManzana() {
        return prediosEnManzana;
    }

    public void setPrediosEnManzana(List<CatPredio> prediosEnManzana) {
        this.prediosEnManzana = prediosEnManzana;
    }

    public List<CatPredio> getPredioFiltrados() {
        return predioFiltrados;
    }

    public void setPredioFiltrados(List<CatPredio> predioFiltrados) {
        this.predioFiltrados = predioFiltrados;
    }

    public List<CatPredio> getPrediosSeleccionados() {
        return prediosSeleccionados;
    }

    public void setPrediosSeleccionados(List<CatPredio> prediosSeleccionados) {
        this.prediosSeleccionados = prediosSeleccionados;
    }

    public CatastroServices getCatastroService() {
        return catastroService;
    }

    public void setCatastroService(CatastroServices catastroService) {
        this.catastroService = catastroService;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public FusionDivisionServices getFusionDivisionEjb() {
        return fusionDivisionEjb;
    }

    public void setFusionDivisionEjb(FusionDivisionServices fusionDivisionEjb) {
        this.fusionDivisionEjb = fusionDivisionEjb;
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }
//</editor-fold>

}
