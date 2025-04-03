/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegCertificado;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class RegCertificadoLazy extends BaseLazyDataModel<RegCertificado> {

    public RegCertificadoLazy() {
        super(RegCertificado.class, "fechaEmision", "DESC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("tipoCertificado.nombreCertificado")) {
            crit.createCriteria("tipoCertificado").add(Restrictions.ilike("nombreCertificado", "%" + filters.get("tipoCertificado.nombreCertificado").toString().trim() + "%"));
        }
        if (filters.containsKey("numTramite")) {
            crit.add(Restrictions.eq("numTramite", new BigInteger(filters.get("numTramite").toString().trim())));
        }
        if (filters.containsKey("numCertificado")) {
            crit.add(Restrictions.eq("numCertificado", new BigInteger(filters.get("numCertificado").toString().trim())));
        }
        if (filters.containsKey("secuencia")) {
            crit.add(Restrictions.eq("secuencia", Integer.getInteger(filters.get("secuencia").toString().trim())));
        }
        if (filters.containsKey("beneficiario")) {
            crit.add(Restrictions.ilike("beneficiario", "%" + filters.get("beneficiario").toString().trim() + "%"));
        }
    }

}
