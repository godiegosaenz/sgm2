/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegTipoCertificado;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class RegTipoCertificadoLazy extends BaseLazyDataModel<RegTipoCertificado> {

    private Boolean estado;

    public RegTipoCertificadoLazy() {
        super(RegTipoCertificado.class, "id");
    }

    public RegTipoCertificadoLazy(Boolean estado) {
        super(RegTipoCertificado.class, "id");
        this.estado = estado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("nombreCertificado")) {
            crit.add(Restrictions.ilike("nombreCertificado", "%" + filters.get("nombreCertificado").toString().trim() + "%"));
        }
        if (estado != null) {
            crit.add(Restrictions.eq("estado", estado));
        }
    }

}
