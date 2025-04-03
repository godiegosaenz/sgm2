/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioObraInterna;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author OrigamiSolutions
 */
public class CatPredioObraInternaLazy extends BaseLazyDataModel<CatPredioObraInterna>{
    private Boolean estado = false;
    private static final Logger log = Logger.getLogger(CatPredioObraInternaLazy.class.getName());
    
    public CatPredioObraInternaLazy(Boolean estado){
        super(CatPredioObraInterna.class);
        this.estado = estado;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters.containsKey("predio.numPredio")) {
                if (NumberUtils.isNumber(filters.get("predio.numPredio").toString())) {
                    crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
                }
            }
            if (filters.containsKey("CatPredioObraInterna.tipo")) {
                crit.createCriteria("CatPredioObraInterna").add(Restrictions.ilike("tipo", "%" + filters.get("CatPredioObraInterna.tipo").toString().trim() + "%"));
            }
            if (filters.containsKey("CatPredioObraInterna.material")) {
                crit.createCriteria("CatPredioObraInterna").add(Restrictions.ilike("material", "%" + filters.get("CatPredioObraInterna.material").toString().trim() + "%"));
            }
            if (filters.containsKey("CatPredioObraInterna.cantidad")) {
                crit.createCriteria("CatPredioObraInterna").add(Restrictions.ilike("cantidad", "%" + filters.get("CatPredioObraInterna.cantidad").toString().trim() + "%"));
            }
            if (filters.containsKey("CatPredioObraInterna.conservacion")) {
                crit.createCriteria("CatPredioObraInterna").add(Restrictions.ilike("conservacion", "%" + filters.get("CatPredioObraInterna.conservacion").toString().trim() + "%"));
            }
            if (filters.containsKey("CatPredioObraInterna.edad")) {
                crit.createCriteria("CatPredioObraInterna").add(Restrictions.ilike("edad", "%" + filters.get("CatPredioObraInterna.edad").toString().trim() + "%"));
            }            
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }    
}
