/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.financiero.bancos.models.EnteModel;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.util.EjbsCaller;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author CarlosLoorVargas
 */
public class ConciliacionBancaria extends LazyDataModel<RenLiquidacion> {

    private Entitymanager manager;
    private RecaudacionesService recaudacion;
    private int rowCount = 0;
    private String defaultSorted = "id";
    private String defaultSortOrder = "ASC";
    private Criteria orderCrit;
    private String orderField;
    private RenLiquidacion entity;
    private RenTipoLiquidacion tipoLiquidacion;
    private RenEstadoLiquidacion estadoLiquidacion;
    private Integer periodo;
    private List<CatPredio> exluidos;
    private Short sector;
    private List<RenLiquidacion> resultado;
//    private static final Long serialVersionUID = 1L;

    public ConciliacionBancaria() {
        manager = EjbsCaller.getTransactionManager();
        recaudacion = EjbsCaller.getServiciosFinanciero();
    }

    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            crit.add(Restrictions.eq("tipoLiquidacion", tipoLiquidacion));
            crit.add(Restrictions.eq("estadoLiquidacion", estadoLiquidacion));
            if (filters.containsKey("anio")) {
                crit.add(Restrictions.eq("anio", Integer.valueOf(filters.get("anio").toString())));
            } else {
                crit.add(Restrictions.between("anio", (periodo - 1), periodo));
            }
            crit.add(Restrictions.eq("estaExonerado", Boolean.FALSE));
            crit.createCriteria("predio").add(Restrictions.ge("sector", sector));
            crit.add(Restrictions.not(Restrictions.in("predio", exluidos)));
            resultado = crit.list();
        } catch (NumberFormatException | HibernateException e) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List result = null;
        Criteria cq, dcq;
        try {
            cq = manager.getSession().createCriteria(RenLiquidacion.class, "entity");
            //this.criteriaFilterSetup(cq, filters);
            cq.setProjection(Projections.projectionList().add(Projections.rowCount()));
            dcq = manager.getSession().createCriteria(RenLiquidacion.class, "entity1");
            this.criteriaFilterSetup(dcq, filters);
            if (orderCrit != null) {
                this.criteriaSortSetup(orderCrit, orderField, sortOrder);
            } else {
                this.criteriaSortSetup(dcq, sortField, sortOrder);
            }
            this.criteriaPageSetup(dcq, first, pageSize);
            rowCount = 0;
            rowCount = ((Long) cq.uniqueResult()).intValue();
            this.setRowCount(rowCount);
            result = dcq.list();
            /*for (Iterator it = result.iterator(); it.hasNext();) {
                RenLiquidacion e = (RenLiquidacion) it.next();
                e = recaudacion.realizarDescuentoRecargaInteresPredial(e, new Date());
                e.calcularPagoConCoactiva();
            }*/
            Hibernate.initialize(result);
        } catch (Exception ex) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void procesar() {
        try {
            for (RenLiquidacion e : resultado) {
                e = recaudacion.realizarDescuentoRecargaInteresPredial(e, new Date());
                e.calcularPagoConCoactiva();
            }
        } catch (Exception ex) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public RenLiquidacion getRowData(String key) {
        try {
            return manager.find(RenLiquidacion.class, Long.parseLong(key));
        } catch (NumberFormatException e) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        try {
            if (rowIndex == -1 || getPageSize() == 0) {
                super.setRowIndex(-1);
            } else {
                super.setRowIndex(rowIndex % getPageSize());
            }
        } catch (Exception e) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void criteriaPageSetup(Criteria crit, int first, int pageSize) {
        try {
            crit.setFirstResult(first);
            crit.setMaxResults(pageSize);
        } catch (Exception e) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void criteriaSortSetup(Criteria crit, String field, SortOrder order) {
        try {
            if (field == null) {
                crit.addOrder((defaultSortOrder.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted) : Order.desc(defaultSorted));
            } else {
                if (order.equals(SortOrder.ASCENDING)) {
                    crit.addOrder(Order.asc(field));
                } else {
                    crit.addOrder(Order.desc(field));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ConciliacionBancaria.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RenLiquidacion find() {
        return entity;
    }

    public void setEntity(RenLiquidacion entity) {
        this.entity = entity;
    }

    public String getDefaultSorted() {
        return defaultSorted;
    }

    public void setDefaultSorted(String defaultSorted) {
        this.defaultSorted = defaultSorted;
    }

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

    public Criteria getOrderCrit() {
        return orderCrit;
    }

    public void setOrderCrit(Criteria orderCrit) {
        this.orderCrit = orderCrit;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RenEstadoLiquidacion getEstadoLiquidacion() {
        return estadoLiquidacion;
    }

    public void setEstadoLiquidacion(RenEstadoLiquidacion estadoLiquidacion) {
        this.estadoLiquidacion = estadoLiquidacion;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public List<CatPredio> getExluidos() {
        return exluidos;
    }

    public void setExluidos(List<CatPredio> exluidos) {
        this.exluidos = exluidos;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public List<RenLiquidacion> getResultado() {
        return resultado;
    }

    public void setResultado(List<RenLiquidacion> resultado) {
        this.resultado = resultado;
    }

}
