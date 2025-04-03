/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatTransferenciaDominio;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
public class TransferenciaDominioLazy extends BaseLazyDataModel<CatTransferenciaDominio> {

    public TransferenciaDominioLazy() {
        super(CatTransferenciaDominio.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("predio.numPredio")) {
            if (Utils.validateNumberPattern(filters.get("predio.numPredio").toString().trim())) {
                crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("predio.numPredio").toString().trim())));
            }
        }
        if (filters.containsKey("numTramite")) {
            if (Utils.validateNumberPattern(filters.get("numTramite").toString().trim())) {
                crit.add(Restrictions.eq("numTramite", Long.parseLong(filters.get("numTramite").toString().trim())));
            }
        }
    }
}
