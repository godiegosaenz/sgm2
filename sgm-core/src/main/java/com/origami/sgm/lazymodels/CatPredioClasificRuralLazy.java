/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioClasificRural;
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
public class CatPredioClasificRuralLazy extends BaseLazyDataModel<CatPredioClasificRural>{
    private Boolean estado = false;
    private static final Logger log = Logger.getLogger(CatPredioClasificRuralLazy.class.getName());
    
    public CatPredioClasificRuralLazy(Boolean estado) {
        super(CatPredioClasificRural.class);
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
            if (filters.containsKey("catPredioClasificRural.sector_homogeneo")) {
                crit.createCriteria("catPredioClasificRural").add(Restrictions.ilike("sector_homogeneo", "%" + filters.get("catPredioClasificRural.sector_homogeneo").toString().trim() + "%"));
            }
            if (filters.containsKey("catPredioClasificRural.calidad_suelo")) {
                crit.createCriteria("catPredioClasificRural").add(Restrictions.ilike("calidad_suelo", "%" + filters.get("catPredioClasificRural.calidad_suelo").toString().trim() + "%"));
            }
            if (filters.containsKey("catPredioClasificRural.superficie")) {
                crit.createCriteria("catPredioClasificRural").add(Restrictions.ilike("superficie", "%" + filters.get("catPredioClasificRural.superficie").toString().trim() + "%"));
            }
            if (filters.containsKey("catPredioClasificRural.uso_predio")) {
                crit.createCriteria("catPredioClasificRural").add(Restrictions.ilike("uso_predio", "%" + filters.get("catPredioClasificRural.uso_predio").toString().trim() + "%"));
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }
}
