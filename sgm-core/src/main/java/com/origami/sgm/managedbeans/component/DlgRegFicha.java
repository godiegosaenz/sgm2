/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.lazymodels.RegFichaLazy;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Angel Navarro
 * @Date 28/04/2016
 */
@Named
@ViewScoped
public class DlgRegFicha implements Serializable {

    private RegFichaLazy fichas;

    /**
     * Creates a new instance of DlgRegFicha
     */
    public DlgRegFicha() {
    }

    @PostConstruct
    public void initView() {
        Long l = null;
        this.fichas = new RegFichaLazy("fechaInscripcion", null, 0);
    }

    public void selectFicha(RegFicha ficha) {
        RequestContext.getCurrentInstance().closeDialog(ficha);
    }

    public RegFichaLazy getFichas() {
        return fichas;
    }

    public void setFichas(RegFichaLazy fichas) {
        this.fichas = fichas;
    }

}
