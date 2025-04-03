/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegFicha;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
public class RegFichaLazy extends BaseLazyDataModel<RegFicha> {

    private Collection list;
    private String valor;
    private Long tipoFicha;
    private int tipo = 0;
    private Object value;

    public RegFichaLazy() {
        super(RegFicha.class, "fechaApe", "DESC");
    }

    public RegFichaLazy(Long tipoFicha) {
        super(RegFicha.class, "numFicha", "DESC");
        this.tipoFicha = tipoFicha;
    }

    public RegFichaLazy(String field, Object value, int tipo) {
        super(RegFicha.class, field, "DESC");
        this.tipo = tipo;
        this.value = value;
    }

    public RegFichaLazy(String valor, int tipo) {
        super(RegFicha.class, "fechaApe", "DESC");
        this.valor = valor;
        this.tipo = tipo;
    }

    public RegFichaLazy(Collection collection) {
        super(RegFicha.class, "numFicha", "ASC");
        list = collection;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("numFicha")) {
            if(Utils.validateNumberPattern(filters.get("numFicha").toString().trim()))
                crit.add(Restrictions.eq("numFicha", new Long(filters.get("numFicha").toString().trim())));
        }
        if (filters.containsKey("parroquia.descripcion")) {
            crit.createCriteria("parroquia").add(Restrictions.ilike("descripcion", "%" + filters.get("parroquia.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("predio.numPredio")) {
            if(Utils.validateNumberPattern(filters.get("predio.numPredio").toString().trim()))
                crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("predio.numPredio").toString().trim())));
        }
        if (filters.containsKey("tipoPredio")) {
            crit.add(Restrictions.ilike("tipoPredio", "%" + filters.get("tipoPredio").toString().trim() + "%"));
        }
        if (filters.containsKey("linderos")) {
            crit.add(Restrictions.ilike("linderos", "%" + filters.get("linderos").toString().trim() + "%"));
        }

        if (tipoFicha != null) {
            crit.createCriteria("tipo").add(Restrictions.eq("id", tipoFicha));
        }

        if (tipo != 0) {
            if (tipo == 1) {
                crit.add(Restrictions.ilike("linderos", "%" + valor.trim() + "%"));
            } else if (tipo == 2) {
                crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(valor.trim())));
            } else if (tipo == 6) {
                crit.add(Restrictions.eq("numFicha", Long.parseLong(value.toString())));
            }
        }

        if (list != null) {
            if (!list.isEmpty()) {
                crit.add(Restrictions.in("id", list));
            }
        }
    }
}
