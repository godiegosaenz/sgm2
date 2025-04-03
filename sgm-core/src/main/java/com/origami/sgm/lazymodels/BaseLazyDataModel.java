/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.util.EjbsCaller;
import util.Utils;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author CarlosLoorVargas
 * @param <T> Entity Class
 */
public class BaseLazyDataModel<T extends Object> extends LazyDataModel<T> {

    private Entitymanager manager;
    private Class<T> entity;
    private int rowCount = 0;
    private String defaultSorted = "id", defaultSorted2, defaultSorted3, defaultSorted4;
    private String defaultSortOrder = "ASC";
    private String defaultSortOrder2 = "ASC";
    private String defaultSortOrder3 = "ASC";
    private String defaultSortOrder4 = "ASC";
    private Criteria orderCrit;
    private String orderField;
    private String colunmEstado;
    private Object valueEstado;

    private String[] filterss;
    private Object[] filtersValue;

    public BaseLazyDataModel() {
        manager = EjbsCaller.getTransactionManager();
    }

    public BaseLazyDataModel(Class<T> entity) {
        this.entity = entity;
        manager = EjbsCaller.getTransactionManager();
    }

    public BaseLazyDataModel(Class<T> entity, String defaultSorted) {
        this.entity = entity;
        this.defaultSorted = defaultSorted;
        manager = EjbsCaller.getTransactionManager();
    }

    public BaseLazyDataModel(Class<T> entity, String defaultSorted, String defaultSortOrder) {
        this.entity = entity;
        this.defaultSorted = defaultSorted;
        this.defaultSortOrder = defaultSortOrder;
        manager = EjbsCaller.getTransactionManager();
    }

    public BaseLazyDataModel(Class<T> entity, String defaultSorted, String defaultSortOrder,
            String defaultSorted2, String defaultSortOrder2, String defaultSorted3,
            String defaultSortOrder3, String defaultSorted4, String defaultSortOrder4) {
        this.entity = entity;
        this.defaultSorted = defaultSorted;
        this.defaultSortOrder = defaultSortOrder;

        this.defaultSorted2 = defaultSorted2;
        this.defaultSortOrder2 = defaultSortOrder2;

        this.defaultSorted3 = defaultSorted3;
        this.defaultSortOrder3 = defaultSortOrder3;

        this.defaultSorted4 = defaultSorted4;
        this.defaultSortOrder4 = defaultSortOrder4;

        manager = EjbsCaller.getTransactionManager();
    }

    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters == null || filters.isEmpty()) {
                return;
            }
            if (colunmEstado != null && valueEstado != null) {
                crit.add(Restrictions.eq(colunmEstado, valueEstado));
            }
            filters.entrySet().forEach(new Consumer<Map.Entry<String, Object>>() {
                @Override
                public void accept(Map.Entry<String, Object> entry) {
                    Criteria c = null;
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    Class type = (Class) Utils.getTypeObject(BaseLazyDataModel.this.entity, key);
                    if (type != null) {
                        if (key.contains(".")) {
                            String[] split = key.split("\\.");
                            int index = 0;
                            for (String sp : split) {
                                if (index == 0) {
                                    c = crit.createCriteria(sp);
                                } else if (index < (split.length - 1)) {
                                    c = c.createCriteria(sp);
                                } else {
                                    key = sp;
                                }
                                index++;
                            }
                        } else {
                            c = crit;
                        }
                        if (type.equals(String.class)) {
                            c.add(Restrictions.ilike(key, "%" + value.toString() + "%"));
                        } else if (type.equals(Character.class)) {
                            c.add(Restrictions.ilike(key, "%" + value.toString() + "%"));
                        } else {
                            Class clazz = (Class) type;
                            Object obj = null;
                            try {
                                if (clazz.isPrimitive()) {
                                    if (type.equals(long.class)) {
                                        type = Long.class;
                                        obj = Long.valueOf("0");
                                    } else if (type.equals(short.class)) {
                                        type = Short.class;
                                        obj = Short.valueOf("0");
                                    } else if (type.equals(int.class)) {
                                        type = Integer.class;
                                        obj = Integer.valueOf("0");
                                    } else if (type.equals(boolean.class)) {
                                        type = Boolean.class;
                                        obj = Boolean.valueOf("false");
                                    } else if (type.equals(double.class)) {
                                        type = Double.class;
                                        obj = Double.valueOf("0");
                                    }
                                } else {
                                    obj = clazz.newInstance();
                                }
                            } catch (InstantiationException | IllegalAccessException ex) {
                                Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Boolean isNumber = Boolean.FALSE;
                            if (obj != null) {
                                isNumber = (obj instanceof Number);
                            }
                            if (isNumber) {
                                if (NumberUtils.isNumber(value.toString())) {
                                    c.add(Restrictions.eq(key, Utils.instanceConsString((Class) type, value.toString().trim())));
                                }
                            } else {
                                c.add(Restrictions.eq(key, Utils.instanceConsString((Class) type, value.toString().trim())));
                            }
                        }
                    }
                }
            });
            if (this.filterss != null) {
                for (int i = 0; i < this.filterss.length; i++) {
                    crit.add(Restrictions.eq(this.filterss[i], this.filtersValue[i]));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List result = null;
        Criteria cq, dcq;
        try {
            cq = manager.getSession().createCriteria(this.getEntity(), "entity");
            this.criteriaFilterSetup(cq, filters);
            cq.setProjection(Projections.projectionList().add(Projections.rowCount()));
            dcq = manager.getSession().createCriteria(this.getEntity(), "entity1");

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
            Hibernate.initialize(result);
        } catch (Exception ex) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public T getRowData(Object key) {
        T ob = null;
        try {
            ob = manager.find(entity, key);
        } catch (Exception e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public T getRowData(String rowKey) {
        T ob = null;
        Object x = rowKey;
        try {
            if (NumberUtils.isNumber(rowKey)) {
                ob = manager.find(entity, Long.parseLong(rowKey));
            } else {
                ob = manager.find(entity, rowKey);
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
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
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void criteriaPageSetup(Criteria crit, int first, int pageSize) {
        try {
            crit.setFirstResult(first);
            crit.setMaxResults(pageSize);
        } catch (Exception e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void criteriaSortSetup(Criteria crit, String field, SortOrder order) {
        try {
            if (field == null) {
                crit.addOrder((defaultSortOrder.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted) : Order.desc(defaultSorted));
//                if (this.defaultSortOrder2 != null && this.defaultSortOrder2 != null) {
//                    crit.addOrder((defaultSortOrder2.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted2) : Order.desc(defaultSorted2));
//                }
//                if (this.defaultSortOrder3 != null && this.defaultSortOrder3 != null) {
//                    crit.addOrder((defaultSortOrder3.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted3) : Order.desc(defaultSorted3));
//                }
//                if (this.defaultSortOrder4 != null && this.defaultSortOrder4 != null) {
//                    crit.addOrder((defaultSortOrder4.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted4) : Order.desc(defaultSorted4));
//                }
            } else {
                if (order.equals(SortOrder.ASCENDING)) {
                    crit.addOrder(Order.asc(field));
                } else {
                    crit.addOrder(Order.desc(field));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }
//<editor-fold defaultstate="collapsed" desc="Getter and Setter">

    public Class<T> getEntity() {
        return entity;
    }

    public void setEntity(Class<T> entity) {
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

    public String getColunmEstado() {
        return colunmEstado;
    }

    public void setColunmEstado(String colunmEstado) {
        this.colunmEstado = colunmEstado;
    }

    public Object getValueEstado() {
        return valueEstado;
    }

    public void setValueEstado(Object valueEstado) {
        this.valueEstado = valueEstado;
    }

    public String[] getFilterss() {
        return filterss;
    }

    /**
     * Nombre de los campos a filtrar
     *
     * @param filterss nombre de los campos a filtrar
     * <br/>
     * <p>
     * Ejemplo: "numPredio", "claveCat". solo los campos de la entity no soporta
     * filtros de campos relacionados.</p>
     */
    public void setFilterss(String[] filterss) {
        this.filterss = filterss;
    }

    public Object[] getFiltersValue() {
        return filtersValue;
    }

    /**
     * Valor de los filtros ingresados <code>filterss</code>
     *
     * @param filtersValue
     */
    public void setFiltersValue(Object[] filtersValue) {
        this.filtersValue = filtersValue;
    }
//</editor-fold>
}
