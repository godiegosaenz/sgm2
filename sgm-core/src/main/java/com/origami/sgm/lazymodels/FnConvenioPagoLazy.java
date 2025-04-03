/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnConvenioPago;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dairon Freddy
 */
public class FnConvenioPagoLazy extends BaseLazyDataModel<FnConvenioPago> {

    private Short estado;
    private Short[] estados;
    private Criteria ente;
    private Boolean convenioAgua;

    public FnConvenioPagoLazy() {
        super(FnConvenioPago.class, "id", "DESC");
    }

    public FnConvenioPagoLazy(short estado) {
        super(FnConvenioPago.class, "id", "DESC");
        this.estado = estado;
    }

    public FnConvenioPagoLazy(Short[] estados) {
        super(FnConvenioPago.class, "id", "DESC");
        this.estados = estados;
    }

    public FnConvenioPagoLazy(Short[] estados, Boolean agua) {
        super(FnConvenioPago.class, "id", "DESC");
        this.estados = estados;
        this.convenioAgua = agua;
    }

    public FnConvenioPagoLazy(Boolean agua, SortOrder order) {

        super(FnConvenioPago.class, "id", order.equals(SortOrder.ASCENDING) ? "ASC" : "DESC");

        this.convenioAgua = agua;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        ente = crit.createCriteria("contribuyente");
//
//        for (Map.Entry<String, Object> entry : filters.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            System.out.println("KEY: " + key + "  VALUE: " + value);
//        }
//        if (filters.containsKey("contribuyente.ciRuc")) {
//
//            System.out.println("Entro al filtro");
//            crit.createCriteria("contribuyente").add(Restrictions.ilike("ciRuc", "%" + filters.get("contribuyente.ciRuc").toString().trim() + "%"));
//        }


        /* if (filters.containsKey("id")) {
            ente.add(Restrictions.ilike("id", "%" + filters.get("id").toString().trim() + "%"));
        }*/
        if (filters.containsKey("contribuyente.ciRuc")) {
            ente.add(Restrictions.ilike("ciRuc", "%" + filters.get("contribuyente.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("nombresCompletos")) {
            Criterion c1 = Restrictions.ilike("nombres", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c2 = Restrictions.ilike("apellidos", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c3 = Restrictions.ilike("razonSocial", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c4 = Restrictions.ilike("nombreComercial", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            ente.add(Restrictions.or(c1, c2, c3, c4));
        }
        if (filters.containsKey("apellidonombrecomercial")) {
            Criterion c1 = Restrictions.ilike("apellidos", "%" + filters.get("apellidonombrecomercial").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c2 = Restrictions.ilike("nombreComercial", "%" + filters.get("apellidonombrecomercial").toString().trim().replaceAll(" ", "%") + "%");
            ente.add(Restrictions.or(c1, c2));
        }

        if (estados != null) {
            crit.add(Restrictions.in("estado", estados));
        }

        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public Short[] getEstados() {
        return estados;
    }

    public void setEstados(Short[] estados) {
        this.estados = estados;
    }

}
