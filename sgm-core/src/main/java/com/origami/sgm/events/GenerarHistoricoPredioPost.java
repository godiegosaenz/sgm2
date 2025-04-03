package com.origami.sgm.events;

import com.origami.sgm.entities.CatPredio;

/**
 * 
 *
 * @author Fernando
 */
public class GenerarHistoricoPredioPost {

    private String jsonAnt;
    private String jsonPost;
    private String observacion;
    private String user;
    private CatPredio predio;
    private CatPredio predioPost;
    private Boolean updatePredio = Boolean.FALSE;

    public GenerarHistoricoPredioPost() {
    }

    /**
     * 
     * @param jsonAnt Json Anterior
     * @param jsonPost Json Actual
     * @param observacion Observacion
     * @param user Usuario 
     * @param predioPost Predio Actual 
     */
    public GenerarHistoricoPredioPost(String jsonAnt, String jsonPost, String observacion, String user, CatPredio predioPost) {
        this.jsonAnt = jsonAnt;
        this.jsonPost = jsonPost;
        this.observacion = observacion;
        this.predioPost = predioPost;
        this.user = user;
    }

    public GenerarHistoricoPredioPost(String observacion, String user, CatPredio predio, CatPredio predioPost) {
        this.observacion = observacion;
        this.user = user;
        this.predio = predio;
        this.predioPost = predioPost;
    }

    public String getJsonAnt() {
        return jsonAnt;
    }

    public void setJsonAnt(String jsonAnt) {
        this.jsonAnt = jsonAnt;
    }

    public String getJsonPost() {
        return jsonPost;
    }

    public void setJsonPost(String jsonPost) {
        this.jsonPost = jsonPost;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getUpdatePredio() {
        return updatePredio;
    }

    public void setUpdatePredio(Boolean updatePredio) {
        this.updatePredio = updatePredio;
    }

    public CatPredio getPredioPost() {
        return predioPost;
    }

    public void setPredioPost(CatPredio predioPost) {
        this.predioPost = predioPost;
    }

}
