/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.managedbeans.ordenes;

import com.origami.sgm.entities.OrdenDet;
import com.origami.censocat.models.FotosModel;
import com.origami.censocat.restful.EstadoMovil;
import com.origami.censocat.restful.JsonUtils;
import com.origami.censocat.service.catastro.PredioEjb;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioObraInterna;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.BeansException;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class RevisionPredioCenso implements Serializable {

    private static final Logger LOG = Logger.getLogger(RevisionPredioCenso.class.getName());

    @Inject
    protected ServletSession ss;
    @Inject
    protected UserSession session;
    @Inject
    protected PredioEjb preds;
    @javax.inject.Inject
    protected CatastroServices catastroServices;

    protected CatPredio predioCenso;
    protected CatPredio predioAct;
    protected CatEscritura escritura;
    protected List<FotosModel> fotosModel;

    protected OrdenDet ordenDet;
    protected JsonUtils jsUtils;
    protected Integer index = 0;
    protected FotosModel fotoModel;
    
    protected CatPredioEdificacion bloq;
    protected CatPredioObraInterna obra;

    @PostConstruct
    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                if (ss.getParametros() == null) {
                    Faces.redirectFaces2("/faces/vistaprocesos/catastro/ordenes/revision.xhtml");
                    return;
                }
                if (ss.getParametros().get("idDetOrden") != null) {
                    
                    if (ordenDet != null) {
                        if (ordenDet.getPredio() != null) {
                            Long idPredio = ordenDet.getPredio().getId();
                            ordenDet.setPredio(null);
                            predioAct = catastroServices.getPredioId(idPredio);
                        } else {
                            predioAct = catastroServices.getPredioNumPredio(ordenDet.getNumPredio());
                        }
                        escritura = Utils.get(predioAct.getCatEscrituraCollection(), 0);
                        jsUtils = new JsonUtils();
                        predioCenso = jsUtils.jsonToObject(ordenDet.getDatoAct(), CatPredio.class);
                        predioCenso.setId(predioAct.getId());
                        if (ordenDet.getOrden().getObservaciones() != null) {
                            fotosModel = Arrays.asList(jsUtils.jsonToObject(ordenDet.getOrden().getObservaciones(), FotosModel[].class));
                        }
                    } else {
                        Faces.redirectFaces2("/faces/vistaprocesos/catastro/ordenes/revision.xhtml");
                    }
                } else {
                    Faces.redirectFaces2("/faces/vistaprocesos/catastro/ordenes/revision.xhtml");
                }
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "Init View", e);
        }
    }

    public void tabChange(TabChangeEvent event) {
        AccordionPanel tv = (AccordionPanel) event.getComponent();
        this.setIndex(Integer.valueOf(tv.getActiveIndex()));
    }

    public void procesarDatos() {
        try {
            System.out.println("Procesando...");
//            String[] ignoreProperties = new String[]{"numPredio", "id", "clave_cat", "instCreacion",
//                "predialant", "catPredios6.predio", "catPredios4.predio"};
//            BeanUtils.copyProperties(predioCenso, predioAct, ignoreProperties);
            Boolean status = Boolean.FALSE;
            if (status) {
                ordenDet.setPredio(predioAct);
                ordenDet.setFecAct(new Date());
                ordenDet.setUsrAct(session.getName_user());
                ordenDet.setEstadoDet(EstadoMovil.FINALIZADO);
                //ots.guardarDetOrden(ordenDet);
                Faces.messageInfo(null, "Información", "Detalle de la orden fue procesada correctamente.");
                Faces.redirectFaces("/faces/vistaprocesos/catastro/ordenes/revision.xhtml");
            } else {
                Faces.messageError(null, "Error", "Ocurrio un error al procesar el detalle de la orden.");
            }
        } catch (BeansException e) {
            LOG.log(Level.SEVERE, "procesarDatos", e);
        }
    }

    public void rechazar() {
        try {
            ordenDet.setEstadoDet(EstadoMovil.PENDIENTE);
            //ots.guardarDetOrden(ordenDet);
            ordenDet.getOrden().setEstadoOt(EstadoMovil.PENDIENTE);
            //ots.guardarOrden(ordenDet.getOrden());
            Faces.messageInfo(null, "Información", "Detalle de la orden # " + ordenDet.getOrden().getNumOrden() + " rechazada correctamente.");
            Faces.redirectFaces("/faces/vistaprocesos/catastro/ordenes/revision.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "rechazar", e);
            Faces.messageError(null, "Error", "Ocurrio un error al rechazar el detalle de la orden.");
        }
    }

    public List<FotosModel> fotos(List<String> fts) {
        if (Utils.isEmpty(fts)) {
            System.out.println("No hay ids de fotos...");
            return null;
        }
        List<FotosModel> model = new ArrayList<>();
        for (String foto : fts) {
            FotosModel get = fotosModel.get(fotosModel.indexOf(new FotosModel(foto)));
            if (get != null) {
                model.add(get);
            }
        }
        return model;
    }

//<editor-fold defaultstate="collapsed" desc="Getter and Setter">
    public CatPredio getPredioCenso() {
        return predioCenso;
    }

    public void setPredioCenso(CatPredio predioCenso) {
        this.predioCenso = predioCenso;
    }

    public CatPredio getPredioAct() {
        return predioAct;
    }

    public void setPredioAct(CatPredio predioAct) {
        this.predioAct = predioAct;
    }

    public OrdenDet getOrdenDet() {
        return ordenDet;
    }

    public void setOrdenDet(OrdenDet ordenDet) {
        this.ordenDet = ordenDet;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public List<FotosModel> getFotosModel() {
        return fotosModel;
    }

    public void setFotosModel(List<FotosModel> fotosModel) {
        this.fotosModel = fotosModel;
    }

    public FotosModel getFotoModel() {
        return fotoModel;
    }

    public void setFotoModel(FotosModel fotoModel) {
        this.fotoModel = fotoModel;
    }
    
    public CatPredioEdificacion getBloq() {
        return bloq;
    }

    public void setBloq(CatPredioEdificacion bloq) {
        this.bloq = bloq;
    }

    public CatPredioObraInterna getObra() {
        return obra;
    }

    public void setObra(CatPredioObraInterna obra) {
        this.obra = obra;
    }
//</editor-fold>

}
