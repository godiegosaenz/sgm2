/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.HistoricoTramites;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import util.Utils;

/**
 *
 * @author Anyelo
 */
public class HistoricoTramitesLazy extends BaseLazyDataModel<HistoricoTramites> {

    private BigInteger tipo;
    private Collection tiposTramite;
    private Criteria curb;
    private Criteria ctipTram;
    
    public HistoricoTramitesLazy() {
        super(HistoricoTramites.class, "fecha", "DESC");
    }

    public HistoricoTramitesLazy(BigInteger tipoTramite) {
        super(HistoricoTramites.class, "fecha", "DESC");
        tipo = tipoTramite;
    }
    
    public HistoricoTramitesLazy(Collection tiposTramite) {
        super(HistoricoTramites.class, "id", "DESC");
        this.tiposTramite=tiposTramite;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        curb = crit.createCriteria("urbanizacion", JoinType.LEFT_OUTER_JOIN);
        ctipTram = crit.createCriteria("tipoTramite");
        if (filters.containsKey("id")) {
            if(Utils.validateNumberPattern(filters.get("id").toString().trim()))
                crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("estado")) {
            crit.add(Restrictions.ilike("estado", "%" + filters.get("estado").toString().trim() + "%"));
        }
        if (filters.containsKey("nombrePropietario")) {
            crit.add(Restrictions.ilike("nombrePropietario", "%" + filters.get("nombrePropietario").toString().trim() + "%"));
        }
        if (tipo != null) {
            crit.add(Restrictions.eq("tipoTramite", tipo));
        }
        if (this.tiposTramite != null && !this.tiposTramite.isEmpty()) {
            crit.add(Restrictions.isNotNull("id"));
            crit.add(Restrictions.in("tipoTramite", this.tiposTramite));
        }
        if (filters.containsKey("urbanizacion.nombre")) {
            curb.add(Restrictions.ilike("nombre", "%" + filters.get("urbanizacion.nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("tipoTramite.descripcion")) {
            ctipTram.add(Restrictions.ilike("descripcion", "%" + filters.get("tipoTramite.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("urbsolar")) {
            crit.add(Restrictions.ilike("urbsolar", "%" + filters.get("urbsolar").toString().trim() + "%"));
        }
        if (filters.containsKey("urbmz")) {
            crit.add(Restrictions.ilike("urbmz", "%" + filters.get("urbmz").toString().trim() + "%"));
        }
    }
}
