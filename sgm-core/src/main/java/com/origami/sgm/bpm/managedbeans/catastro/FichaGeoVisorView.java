/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Fernando
 */
@Named("fichaGeoVisorView")
@ViewScoped
public class FichaGeoVisorView extends FichaGeoVisor implements Serializable{
    
    @Inject
    private FichaPredial fichaPredial;

    public String getCodigoPredio() {
        return fichaPredial.getPredio().getClaveCat();
    }

    
    
    
}
