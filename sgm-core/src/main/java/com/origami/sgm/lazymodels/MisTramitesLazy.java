/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.HistoricoTramites;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class MisTramitesLazy extends BaseLazyDataModel<HistoricoTramites> {

    private String interviniente;
    private Criteria ct = null;

    public MisTramitesLazy() {
        super(HistoricoTramites.class, "id");
    }

    public MisTramitesLazy(String interviniente) {
        super(HistoricoTramites.class);
        this.interviniente = interviniente;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (interviniente != null) {
            ct = crit.createCriteria("observacionesCollection");
            ct.add(Restrictions.eq("userCre", interviniente));
            ct.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString())));
        }
        if (filters.containsKey("estado")) {
            crit.add(Restrictions.ilike("estado", "%" + filters.get("estado").toString().trim() + "%"));
        }
        if (filters.containsKey("nombrePropietario")) {
            crit.add(Restrictions.ilike("nombrePropietario", "%" + filters.get("nombrePropietario").toString().trim() + "%"));
        }
//        if(filters.containsKey("tipoTramite.descripcion")) {
//            crit.createCriteria("tipoTramite").add(Restrictions.ilike("descripcion", "%"+ filters.get("tipoTramite.descripcion").toString().trim() +"%" ));
//        }
    }
}
