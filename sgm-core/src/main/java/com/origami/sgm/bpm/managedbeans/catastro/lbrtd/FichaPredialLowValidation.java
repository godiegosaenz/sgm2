/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro.lbrtd;

import com.google.gson.GsonBuilder;
import com.origami.sgm.bpm.managedbeans.catastro.FichaPredial;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.predio.models.FichaModel;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.Faces;
import util.HibernateProxyTypeAdapter;
import util.Utils;

/**
 *
 * @author Fernando
 */
@Alternative
@Named(value = "fichaPredial")
@ViewScoped
public class FichaPredialLowValidation extends FichaPredial {

    @Override
    public void load() {
        cargarQueryPar();
        this.cargarDatos();
        Logger.getLogger(FichaPredialLowValidation.class.getName()).info("### Usando alternatives f !!! ");
    }

    
    


    @Override
    public Boolean validateTipoConjuntoCiudadela(CatPredio cp) {
        Logger.getLogger(FichaPredialLowValidation.class.getName()).info("### Usando alternatives f !!!");
        return true; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean validateEdifEspecif(CatPredioEdificacion cpe) {
        return true;
    }
    
}
