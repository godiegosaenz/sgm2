/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Angel
 * Navarro
 * @Date 06/06/2016
 */
@Named
@ViewScoped
public class PrediosDLG implements Serializable {

    private CatPredioLazy predios;
    private List<CatCiudadela> ciudadelas;
    private boolean filtro;
    private List<CatPredio> predioSelect;

    private String parroquia;
    private String zona;
    private String sector;
    private String mz;

    @javax.inject.Inject
    private Entitymanager serv;

//    @PostConstruct
    public void initView() {
        if (parroquia == null && zona == null && sector == null && mz == null) {
            predios = new CatPredioLazy("A");
        } else {
            CatPredioModel model = new CatPredioModel();
            model.setParroquiaShort(Short.valueOf(parroquia));
            model.setZona(Short.valueOf(zona));
            model.setSector(Short.valueOf(sector));
            model.setMz(Short.valueOf(mz));
            predios = new CatPredioLazy("A");
            predios.setModel(model);
        }
        ciudadelas = serv.findAllOrdered(CatCiudadela.class, new String[]{"nombre"}, new Boolean[]{true});
        filtro = false;
    }

    public SelectItem[] getLisUrbanizaciones() {
        if (ciudadelas != null && !ciudadelas.isEmpty()) {
            int cantRegis = ciudadelas.size();
            SelectItem[] options = new SelectItem[cantRegis + 1];
            options[0] = new SelectItem("", "Seleccione");
            for (int i = 0; i < cantRegis; i++) {
                options[i + 1] = new SelectItem(ciudadelas.get(i).getNombre(), ciudadelas.get(i).getNombre());
            }
            return options;
        }
        return null;
    }

    public void seleccionarPredios() {
        RequestContext.getCurrentInstance().closeDialog(predioSelect);
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public boolean isFiltro() {
        return filtro;
    }

    public void setFiltro(boolean filtro) {
        this.filtro = filtro;
    }

    public List<CatPredio> getPredioSelect() {
        return predioSelect;
    }

    public void setPredioSelect(List<CatPredio> predioSelect) {
        this.predioSelect = predioSelect;
    }

    public String getParroquia() {
        return parroquia;
    }

    public void setParroquia(String parroquia) {
        this.parroquia = parroquia;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public PrediosDLG() {
    }

}
