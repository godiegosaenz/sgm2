/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalComercial;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author Angel Navarro
 * @date 05/08/2016
 */
public class LiquidacionesLazy extends BaseLazyDataModel<RenLiquidacion> {

    private int tipo = 0;
    private Long estado;
    private Boolean vigencia = null;
    private RenLocalComercial l;

    /**
     *
     * @param defaultSorted
     * @param defaultSortOrder
     * @param tipo 1 para Locales Comerciales, 2 para Plusvalias y Alcabalas, o
     * para todos
     */
    public LiquidacionesLazy(String defaultSorted, String defaultSortOrder, int tipo) {
        super(RenLiquidacion.class, defaultSorted, defaultSortOrder);
        this.tipo = tipo;
    }

    /**
     *
     * @param tipo 1 para Locales Comerciales, 2 para Plusvalias y Alcabalas, o
     * para todos
     */
    public LiquidacionesLazy(int tipo) {
        super(RenLiquidacion.class, "fechaIngreso", "DESC");
        this.tipo = tipo;
    }

    public LiquidacionesLazy(int tipo, Boolean vigencia) {
        super(RenLiquidacion.class, "fechaIngreso", "DESC");
        this.tipo = tipo;
        this.vigencia = vigencia;
    }

    /**
     *
     * @param tipo 1 para Locales Comerciales, 2 para Plusvalias y Alcabalas, o
     * para todos
     * @param estadoLiquidacion
     */
    public LiquidacionesLazy(int tipo, Long estadoLiquidacion) {
        super(RenLiquidacion.class, "fechaIngreso", "DESC");
        this.tipo = tipo;
        this.estado = estadoLiquidacion;
    }

    public LiquidacionesLazy(int tipo, RenLocalComercial local) {
        super(RenLiquidacion.class, "fechaIngreso", "DESC");
        this.tipo = tipo;
        this.l = local;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        //Criteria localComercial = crit.createCriteria("localComercial", JoinType.LEFT_OUTER_JOIN);
        Criteria comprador = crit.createCriteria("comprador", JoinType.LEFT_OUTER_JOIN);
        Criteria vendedor = crit.createCriteria("vendedor", JoinType.LEFT_OUTER_JOIN);
        Criteria predio = crit.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
        Criteria tipoLiquidacion = crit.createCriteria("tipoLiquidacion");
        Criteria estadoLiquidacion = crit.createCriteria("estadoLiquidacion");

        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString())));
        }
        if (filters.containsKey("tipoLiquidacion.nombreTitulo")) {
            tipoLiquidacion.add(Restrictions.ilike("nombreTitulo", "%" + filters.get("tipoLiquidacion.nombreTitulo").toString() + "%"));
        }
        if (filters.containsKey("idLiquidacion")) {
            crit.add(Restrictions.ilike("idLiquidacion", "%" + filters.get("idLiquidacion").toString() + "%"));
        }
        if (filters.containsKey("numLiquidacion")) {
            crit.add(Restrictions.eq("numLiquidacion", new BigInteger(filters.get("numLiquidacion").toString())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", Integer.parseInt(filters.get("anio").toString())));
        }
        if (filters.containsKey("comprador.ciRuc")) {
            comprador.add(Restrictions.ilike("ciRuc", "%" + filters.get("comprador.ciRuc").toString() + "%"));
        }
        if (filters.containsKey("comprador.nombreCompleto")) {
            Criterion apellidos = Restrictions.ilike("apellidos", "%" + filters.get("comprador.nombreCompleto").toString() + "%");
            Criterion razonSocial = Restrictions.ilike("razonSocial", "%" + filters.get("comprador.nombreCompleto").toString() + "%");
            LogicalExpression orExp = Restrictions.or(apellidos, razonSocial);
            comprador.add(orExp);
        }
        if (filters.containsKey("comprador.nombres")) {
            comprador.add(Restrictions.ilike("nombres", "%" + filters.get("comprador.nombres").toString() + "%"));
        }
        if (filters.containsKey("comprador.apellidos")) {
            comprador.add(Restrictions.ilike("apellidos", "%" + filters.get("comprador.apellidos").toString() + "%"));
        }
        if (filters.containsKey("comprador.razonSocial")) {
            comprador.add(Restrictions.ilike("razonSocial", "%" + filters.get("comprador.razonSocial").toString() + "%"));
        }

        if (filters.containsKey("vendedor.ciRuc")) {
            vendedor.add(Restrictions.ilike("ciRuc", "%" + filters.get("vendedor.ciRuc").toString() + "%"));
        }
        if (filters.containsKey("vendedor.nombreCompleto")) {
            Criterion apellidos = Restrictions.ilike("apellidos", "%" + filters.get("vendedor.nombreCompleto").toString() + "%");
            Criterion razonSocial = Restrictions.ilike("razonSocial", "%" + filters.get("vendedor.nombreCompleto").toString() + "%");
            LogicalExpression orExp = Restrictions.or(apellidos, razonSocial);
            vendedor.add(orExp);
        }
        if (filters.containsKey("vendedor.nombres")) {
            vendedor.add(Restrictions.ilike("nombres", "%" + filters.get("vendedor.nombres").toString() + "%"));
        }
        if (filters.containsKey("vendedor.apellidos")) {
            vendedor.add(Restrictions.ilike("apellidos", "%" + filters.get("vendedor.apellidos").toString() + "%"));
        }
        if (filters.containsKey("vendedor.razonSocial")) {
            vendedor.add(Restrictions.ilike("razonSocial", "%" + filters.get("vendedor.razonSocial").toString() + "%"));
        }

        if (filters.containsKey("predio.numPredio")) {
            predio.add(Restrictions.ilike("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
        }
        if (filters.containsKey("estadoLiquidacion.descripcion")) {
            estadoLiquidacion.add(Restrictions.ilike("descripcion", "%" + filters.get("estadoLiquidacion.descripcion").toString() + "%"));
        }

        if (estado != null) {
            estadoLiquidacion.add(Restrictions.in("id", new Object[]{estado}));
        } else {
            estadoLiquidacion.add(Restrictions.in("id", new Object[]{1l, 2l}));
        }
        switch (tipo) {
            case 1: // LC
                tipoLiquidacion.add(Restrictions.in("codigoTituloReporte", new Object[]{11l, 14l, 15l, 98l, 53l, 102l, 206L}));
                break;
            case 2: // PLU Y ALC
                tipoLiquidacion.add(Restrictions.in("codigoTituloReporte", new Object[]{93l, 95l}));
                break;
            default:

                break;
        }
        if (vigencia != null) {
            if (vigencia) {
                crit.add(Restrictions.sqlRestriction("CAST({alias}.fecha_ingreso AS DATE) <= (select CAST(now() AS DATE) - CAST((SELECT vigencia FROM " + SchemasConfig.FINANCIERO + ".ren_valores_plusvalia where liquidacion = {alias}.id) || ' month' AS interval)) AND {alias}.estado_liquidacion = 2"));
            } else {
                crit.add(Restrictions.sqlRestriction("(select CAST(now() AS DATE) - CAST((SELECT vigencia FROM " + SchemasConfig.FINANCIERO + ".ren_valores_plusvalia where liquidacion = {alias}.id) || ' month' AS interval)) < CAST({alias}.fecha_ingreso AS DATE) AND {alias}.estado_liquidacion = 2"));
            }
        }
        if (this.l != null) {
            crit.add(Restrictions.eq("localComercial", l));
        }
    }

}
