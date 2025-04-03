/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.transactionalcore.entitymanager;

import com.origami.sgm.acl.AccessLevelRequest;
import com.origami.sgm.acl.RespuestaAcceso;
import com.origami.sgm.acl.entity.AclUrl;
import com.origami.sgm.acl.service.AclCache;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatNacionalidad;
import com.origami.sgm.entities.CatPais;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.ejbs.PersistenceState;
import com.origami.sgm.services.interfaces.SeqGenMan;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import util.EntityBeanCopy;
import util.HiberUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton(name = "manager")
@Lock(LockType.READ)
@Interceptors(value = {HibernateEjbInterceptor.class})
@ApplicationScoped
public class TransactionManager implements Entitymanager {

    private static final Logger LOG = Logger.getLogger(TransactionManager.class.getName());

    @javax.inject.Inject
    private SeqGenMan secuencia;
    @javax.inject.Inject
    private AclCache cacheServ;
    @Inject
    private PersistenceState pstate;

    @Override
    public void evict(Object obj) {
        HiberUtil.getSession().evict(obj);
    }

    @Override
    public <T> T find(Class<T> entity, Object id) {
        T ob = null;
        try {
            Session sess = HiberUtil.getSession();
            ob = (T) sess.get(entity, (Serializable) id);
            Hibernate.initialize(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> List<T> findAll(Class<T> entity) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            result = (List) cq.list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public Object find(String query, String[] par, Object[] val) {
        //LOG.log(Level.INFO, " class: {0}", this.getClass().getTypeName());
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query).setMaxResults(1);
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
            //HiberUtil.unproxy(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T extends Object> T findNoProxy(Class<T> entity, Object id) {
        T ob = null;
        try {
            Session sess = HiberUtil.getSession();
            ob = (T) sess.get(entity, (Serializable) id);
            Hibernate.initialize(ob);
            ob = (T) EntityBeanCopy.clone(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public Object findNoProxy(String query, String[] par, Object[] val) {
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query).setMaxResults(1);
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
            ob = EntityBeanCopy.clone(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public Object findUnique(String query, String[] par, Object[] val) {
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query).setMaxResults(1);
            q.setMaxResults(1);
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public List findMax(String query, String[] par, Object[] val, Integer max) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            if (max != null) {
                q.setMaxResults(max);
            }
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            result = (List) q.list();
            result.size();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public List findFirstAndMaxResult(String query, String[] par, Object[] val, Integer first, Integer max) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            if (max != null) {
                q.setMaxResults(max);
            }
            if (first != null) {
                q.setFirstResult(first);
            }
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            result = (List) q.list();
            result.size();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public List findAll(String query) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            l = (List) q.list();
            l.size();
            Hibernate.initialize(l);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    @Override
    public List findAll(String query, String[] par, Object[] val) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            l = (List) q.list();
            l.size();
            Hibernate.initialize(l);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    @Override
    public <T> List<T> findAllEntCopy(Class<T> entity) {
        List<T> list = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            list = (List) EntityBeanCopy.clone(cq.list());
            list.size();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    @Override
    public List findAllEntCopy(String query, String[] par, Object[] val) {
        List list = null;
        try {
            Session sess = HiberUtil.getSession();
            sess.enableFilter("activos");
            sess.enableFilter("activosString");
            Query q = sess.createQuery(query);
            for (int i = 0; i < par.length; i++) {
                q.setParameter(par[i], val[i]);
            }
            list = (List) EntityBeanCopy.clone(q.list());
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    @Override
    public List findAllEntCopy(String query) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            l = (List) EntityBeanCopy.clone(q.list());
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    @Override
    public Object persist(Object o) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return o;
        }
        Object ob = null;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            ob = sess.merge(o); // RETORNA EL OBJETO PERSISTIDO
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public Object saveAll(Object entity) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return entity;
        }
        Object ob = null;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            ob = sess.save(entity); // RETORNA EL ID DEL OBJETO
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public boolean delete(Object o) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return true;
        }
        boolean flag;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            sess.delete(o);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean update(Object o) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return true;
        }
        boolean flag;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            sess.update(o);
            sess.flush();
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean executeNativeQuery(String query, Object[] val) {
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            for (int i = 0; i < val.length; i++) {
                q.setParameter(i, val[i]);
            }
            q.executeUpdate();
            sess.flush();
            return true;
        } catch (HibernateException e) {
            System.out.println("error al ejecutar la funcion + " + query + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public <T> List<T> nativeQuery(String query, Object[] val, Class<T> entity) {
        List<T> obj = new LinkedList<>();
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            for (int i = 0; i < val.length; i++) {
                q.setParameter(i, val[i]);
            }
            System.out.println("Query: " + q.getQueryString());
            q.setResultTransformer(Transformers.aliasToBean(entity));
            List<T> result = q.list();

            return result;

        } catch (HibernateException e) {
            System.out.println("error al ejecutar la funcion + " + query + " - " + e.getMessage());

        }
        return obj;
    }

    @Override
    public Object getNativeQuery(String query) {
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
        } catch (HibernateException e) {
            System.out.println("error al ejecutar la funcion + " + query + " - " + e.getMessage());
        }
        return ob;
    }

    @Override
    public Object getNativeQuery(String query, Object[] val) {
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            for (int i = 0; i < val.length; i++) {
                q.setParameter(i, val[i]);
            }
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public List getSqlQuery(String query) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            result = q.list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> getSqlQueryParametros(Class<T> clase, String query) {
        List<T> result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            result = q.setResultTransformer(Transformers.aliasToBean(clase)).list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> getSqlQueryParametros(Class<T> clase, String query, String[] params, Object[] values) {
        List<T> result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(params[i], values[i]);
            }
            result = q.setResultTransformer(Transformers.aliasToBean(clase)).list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public List<Object[]> getManyColumnsResults(String query, String[] params, Object[] values) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(params[i], values[i]);
            }
            l = q.list();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    @Override
    public List<Object[]> getManyColumnsResults(String query) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            l = q.list();
            Hibernate.initialize(l);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    @Override
    public Criteria getCriteriaQuery(Class entity, String alias) {
        Criteria cq = null;
        try {
            Session sess = HiberUtil.getSession();
            if (alias != null) {
                cq = sess.createCriteria(entity, alias);
            } else {
                cq = sess.createCriteria(entity);
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return cq;
    }

    @Override
    public Session getSession() {
        Session sess = null;
        try {
            sess = HiberUtil.getSession();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return sess;
    }

    @Override
    public Transaction requiredTransaction() {
        try {
            return HiberUtil.requireTransaction();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List findAllByEntities(String query, String[] entitiesNames, Object[] entities) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            for (int i = 0; i < entitiesNames.length; i++) {
                q.setEntity(entitiesNames[i], entities[i]);
            }
            result = q.list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public boolean saveList(List entities) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return true;
        }
        try {
            //int x = 0;
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            for (Object entitie : entities) {
                sess.merge(entitie);
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteList(List entities) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return true;
        }
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            for (Object entitie : entities) {
                sess.delete(entitie);
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public Object find(String query) {
        Object result = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query);
            result = q.uniqueResult();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> findAllOrdered(Class<T> entity, String[] order, Boolean[] prior) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            for (int i = 0; i < order.length; i++) {
                if (prior[i] == true) {
                    cq.addOrder(Order.asc(order[i]));
                } else {
                    cq.addOrder(Order.desc(order[i]));
                }
            }
            result = (List) cq.list();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> findAllOrdEntCopy(Class<T> entity, String[] order, Boolean[] prior) {
        List<T> list = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            for (int i = 0; i < order.length; i++) {
                if (prior[i] == true) {
                    cq.addOrder(Order.asc(order[i]));
                } else {
                    cq.addOrder(Order.desc(order[i]));
                }
            }
            list = (List) EntityBeanCopy.clone(cq.list());
            list.size();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

//    @Override
//    public <T> T findEntity(String entity, Long id) {
//        T ob = null;
//        try {
//            Session sess = HiberUtil.getSession();
//            ob = (T) sess.get(ReflexionEntity.entityClass(entity), (Serializable) id);
//            Hibernate.initialize(ob);
//        } catch (HibernateException e) {
//            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return ob;
//    }

    @Override
    public <T> T findObjectByParameter(Class entity, Map<String, Object> paramt) {
        T ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            cq.add(Restrictions.allEq(paramt));
            ob = (T) cq.uniqueResult();
            Hibernate.initialize(ob);
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> List<T> findObjectByParameterList(Class entity, Map<String, Object> paramt) {
        List<T> ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            cq.add(Restrictions.allEq(paramt));
            ob = (List<T>) cq.list();
            ob.size();
            Hibernate.initialize(ob);
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> List<T> findAllObjectOrder(Class entity, String[] order, Boolean ascDes) {
        List<T> list = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            for (String ord : order) {
                if (ascDes == null || !ascDes) {
                    cq.addOrder(Order.desc(ord));
                } else {
                    cq.addOrder(Order.asc(ord));
                }
            }
            list = cq.list();
            list.size();
            Hibernate.initialize(list);
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    @Override
    public <T> List<T> findObjectByParameterOrderList(Class entity, Map<String, Object> paramt, String[] order, Boolean ascDes) {
        List<T> ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            cq.add(Restrictions.allEq(paramt));
            for (String ord : order) {
                if (ascDes == null || !ascDes) {
                    cq.addOrder(Order.desc(ord));
                } else {
                    cq.addOrder(Order.asc(ord));
                }
            }
            ob = (List<T>) cq.list();
            ob.size();
            Hibernate.initialize(ob);
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public Object findObjectByParameter(String query, Map<String, Object> paramt) {
        Object ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(query).setMaxResults(1);
            for (Map.Entry<String, Object> entrySet : paramt.entrySet()) {
                q.setParameter(entrySet.getKey(), entrySet.getValue());
            }
            ob = (Object) q.uniqueResult();
            Hibernate.initialize(ob);
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public List findIn(Class entity, Map<String, Object> paramt, Map<String, Object> paramtIn) {
        List l = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            if (paramt != null && !paramt.isEmpty()) {
                cq.add(Restrictions.allEq(paramt));
            }
            if (paramtIn != null && !paramtIn.isEmpty()) {
                for (Map.Entry<String, Object> entrySet : paramtIn.entrySet()) {
                    String key = entrySet.getKey();
                    if (key.contains(".")) {
                        String[] s = key.split("\\.");
                        int index = 1;
                        Criteria c = null;
                        for (String s1 : s) {
                            if (s.length > index) {
                                if (index > 1) {
                                    c = c.createCriteria(s1);
                                } else {
                                    c = cq.createCriteria(s1);
                                }
                                index++;
                            } else {
                                c.add(Restrictions.in(s1, getType(entrySet.getValue())));
                            }
                        }
                    } else {
                        cq.add(Restrictions.in(entrySet.getKey(), getType(entrySet.getValue())));
                    }
                }
            }
            l = (List) cq.list();
            l.size();
            Hibernate.initialize(l);

        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return l;
    }

    private Object[] getType(Object value) {
        try {
            if (value instanceof Collection || value instanceof List) {
                return ((Collection) value).toArray();
            } else if (value instanceof Set) {
                return ((Set) value).toArray();
            } else if (value instanceof Object[]) {
                return (Object[]) value;
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public Object executeFunction(String function, Map<String, Object> paramt, Boolean tipoVoid) {
        if (!Boolean.TRUE.equals(pstate.getTransactionEnabled())) {
            return null;
        }
        Object o = null;
        try {
            String parametros = "";
            if (paramt != null) {
                for (Map.Entry<String, Object> entrySet : paramt.entrySet()) {
                    parametros = parametros + entrySet.getValue() + ",";
                }
                parametros = parametros.substring(0, parametros.length() - 1);
            }
            //System.out.println("/*** PARAMETROS: " + parametros);
            this.requiredTransaction();
            Session sess = HiberUtil.getSession();
            //sess.beginTransaction();
            if (tipoVoid) {
                sess.createSQLQuery("SELECT " + function + "(" + parametros + ");");
            } else {
                o = sess.createSQLQuery("SELECT " + function + "(" + parametros + ");").uniqueResult();
            }
            //sess.getTransaction().commit();
            //sess.flush();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public Object ejecutarFuncionAvaluosEmisionPredial(String function, Long predioId, Integer anioInio, Integer anioFin, String usuario) {
        Object o = null;
        try {
            HiberUtil.newTransaction();
            int idPredio = Math.toIntExact(predioId);
            //System.out.println("/*** PARAMETROS: " + parametros);
            this.requiredTransaction();
            Session sess = HiberUtil.getSession();
            sess.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    CallableStatement callableStatement;
                    callableStatement = connection.prepareCall("{call " + function + " (?, ?, ?, ?)}");
                    callableStatement.setInt(1, idPredio);
                    callableStatement.setString(2, usuario);
                    callableStatement.setInt(3, anioInio);
                    callableStatement.setInt(4, anioFin);
                    callableStatement.executeUpdate();
                }
            });
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public Object ejecutarFuncionCleanAvaluos(String usuario, Long predioId) {
        Object o = null;
        try {
            HiberUtil.newTransaction();
            this.requiredTransaction();
            int idPredio = Math.toIntExact(predioId);
            Session sess = HiberUtil.getSession();
            sess.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    CallableStatement callableStatement;
                    callableStatement = connection.prepareCall("{call sgm_app.clean_avaluos ( ?, ?) }");
                    callableStatement.setString(1, usuario);
                    callableStatement.setInt(2, idPredio);
                    callableStatement.executeUpdate();
                }
            });
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public Object executeFunction(String function, List<Object> paramt, Boolean tipoVoid) {
        Object o = null;
        try {
            String parametros = "";
            if (paramt != null) {
                for (Object obj : paramt) {
                    parametros = parametros + obj.toString() + ",";
                }
                parametros = parametros.substring(0, parametros.length() - 1);
            }
            //System.out.println("/*** PARAMETROS: " + parametros);
            this.requiredTransaction();
            Session sess = HiberUtil.getSession();
            //sess.beginTransaction();
            if (tipoVoid) {
                sess.createSQLQuery("SELECT " + function + "(" + parametros + ");");
            } else {
                o = sess.createSQLQuery("SELECT " + function + "(" + parametros + ");").uniqueResult();
            }
            //sess.getTransaction().commit();
            //sess.flush();
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public List findNamedQuery(String namedQuery, Map<String, Object> paramt) {
        List ob = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createQuery(namedQuery);
            for (Map.Entry<String, Object> entrySet : paramt.entrySet()) {
                q.setParameter(entrySet.getKey(), entrySet.getValue());
            }
            ob = q.list();
            ob.size();
            Hibernate.initialize(ob);
            //HiberUtil.unproxy(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> T findObjectNamedQuery(String namedQuery, Map<String, Object> paramt) {
        T ob = null;
        try {
            Session sess1 = HiberUtil.getSession();
            Query q1 = sess1.getNamedQuery(namedQuery);
            for (Map.Entry<String, Object> entrySet : paramt.entrySet()) {
                q1.setParameter(entrySet.getKey(), entrySet.getValue());
            }
            ob = (T) q1.uniqueResult();
            Hibernate.initialize(ob);
            //HiberUtil.unproxy(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> List<T> findListNamedQuery(String namedQuery, Map<String, Object> paramt) {
        List<T> ob = null;
        try {
            Session sess1 = HiberUtil.getSession();
            Query q1 = sess1.getNamedQuery(namedQuery);
            for (Map.Entry<String, Object> entrySet : paramt.entrySet()) {
                q1.setParameter(entrySet.getKey(), entrySet.getValue());
            }
            ob = (List<T>) q1.list();
            Hibernate.initialize(ob);
            //HiberUtil.unproxy(ob);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ob;
    }

    @Override
    public <T> List<T> findFirstAndMaxResult(Class<T> entity, Integer first, Integer max) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
            Criteria cq = sess.createCriteria(entity);
            if (max != null) {
                cq.setMaxResults(max);
            }
            if (first != null) {
                cq.setFirstResult(first);
            }
            result = (List) cq.list();
            result.size();
            Hibernate.initialize(result);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> getPaginatedResult(String query, int first, int pageSize, String[] params, Object[] values) {
        List result = null;
        try {
            Session sess = HiberUtil.getSession();
//            Criteria cq = sess.createCriteria(entity);
//            cq.setFirstResult((pageNumber - 1) * pageSize);
//            cq.setMaxResults(pageSize);
            Query q = sess.createQuery(query);
            q.setFirstResult(first);
            q.setMaxResults(pageSize);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(params[i], values[i]);
            }
            result = q.list();
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public <T> List<T> getNativeQuery(Class<T> entity, String query, String[] params, Object[] values) {
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query).addEntity(entity);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(params[i], values[i]);
                }
            }
            q.setCacheable(Boolean.TRUE);
            q.scroll(ScrollMode.FORWARD_ONLY);
            return q.list();
        } catch (HibernateException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public CatEnte guardarEnteCorreosTlfns(CatEnte ente) {
        try {

            if (ente.getNacionalidad() == null) {
                ente.setNacionalidad(new CatNacionalidad(1L));
                ente.setPais(new CatPais(1l));
            } else {
                ente.setNacionalidad(new CatNacionalidad(2L));
            }
            if (ente.getDiscapacidad() == null) {
                ente.setDiscapacidad(new CtlgItem(203l));
            }
            if (ente.getPorcentaje() == null) {
                ente.setPorcentaje(BigDecimal.ZERO);
            }
            ente = (CatEnte) persist(ente);
            if (!ente.getEnteCorreoCollection().isEmpty()) {
                for (EnteCorreo c : ente.getEnteCorreoCollection()) {
                    c.setEnte(ente);
                    persist(c);
                }
            }
            if (!ente.getEnteTelefonoCollection().isEmpty()) {
                for (EnteTelefono t : ente.getEnteTelefonoCollection()) {
                    t.setEnte(ente);
                    persist(t);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ente;
    }

    @Override
    public CatEnte guardarEnteCorreosTlfnos(CatEnte ente) {
        try {
            Boolean nuevo = false;
            if (ente.getNacionalidad() == null) {
                ente.setNacionalidad(new CatNacionalidad(1L));
                ente.setPais(new CatPais(1l));
            } else {
                ente.setNacionalidad(new CatNacionalidad(2L));
            }
            if (ente.getDiscapacidad() == null) {
                ente.setDiscapacidad(new CtlgItem(203l));
            }
            if (ente.getPorcentaje() == null) {
                ente.setPorcentaje(BigDecimal.ZERO);
            }
            if (!ente.getEsPersona()) {
                if (ente.getCiRuc() == null || ente.getCiRuc().equals("")) {
                    ente.setExcepcionales(Boolean.TRUE);
                    ente = secuencia.guardarOActualizarEnte(ente);
                    nuevo = true;
                } else {
                    if (ente.getId() == null) {
                        ente = (CatEnte) persist(ente);
                        nuevo = true;
                    } else {
                        persist(ente);
                    }
                }
            } else {
                if (ente.getId() == null) {
                    ente = (CatEnte) persist(ente);
                    nuevo = true;
                } else {
                    persist(ente);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return ente;
    }

    @Override
    public Boolean editarEnteCorreosTlfns(CatEnte ente) {
        try {
            ente = (CatEnte) persist(ente);
            if (ente == null) {
                return false;
            }
            if (!ente.getEnteCorreoCollection().isEmpty()) {
                for (EnteCorreo c : ente.getEnteCorreoCollection()) {
                    c.setEnte(ente);
                    c = (EnteCorreo) persist(c);
                }
            }
            if (!ente.getEnteTelefonoCollection().isEmpty()) {
                for (EnteTelefono t : ente.getEnteTelefonoCollection()) {
                    t.setEnte(ente);
                    t = (EnteTelefono) persist(t);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public List<AclUser> getTecnicosByRol(List<Long> roles) {
        List<AclUser> lu = null;
        List<AclUser> temp;
        HashSet l = new HashSet<>();
        try {
            for (Long r1 : roles) {
                temp = findAll(Querys.getAclUserTecnicosByRol, new String[]{"idRol"}, new Object[]{r1});
                l.addAll(temp);
            }
            lu = new ArrayList<>(l);
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return lu;
    }

    @Override
    public List<AclRol> getRoles_url(AclUrl urlEnt) {
        Session sess = HiberUtil.getSession();

        Criteria crit = sess.createCriteria(AclRol.class);
        crit.createAlias("urlHasRolColl", "uhr1");
        crit.add(Restrictions.eq("uhr1.url", urlEnt));

        return crit.list();
    }

    @Override
    public List<AclRol> getRoles_url(Long idUrl) {
        Session sess = HiberUtil.getSession();
        AclUrl urlEnt = (AclUrl) sess.load(AclUrl.class, idUrl);
        return this.getRoles_url(urlEnt);
    }

    @Override
    public RespuestaAcceso checkAccessLevel(AccessLevelRequest alreq) {
        RespuestaAcceso respuesta = new RespuestaAcceso();
        // si el acl esta deshabilitado, dar acceso
        if (!Boolean.TRUE.equals(cacheServ.getEnabled())) {
            // acceso libre:
            respuesta.setAccessGranted(true);
            return respuesta;
        }
        // si es Super User, dar accesos:
        if (alreq.getEsSuperUser()) {
            respuesta.setAccessGranted(true);
            return respuesta;
        }
        // consulta cache-transaccional:
        Boolean ok = cacheServ.checkAccess(alreq.getUrlAcceso(), alreq.getIdUser(), alreq.getRolesIds());
        respuesta.setAccessGranted(ok);
        return respuesta;
    }

    public Object getNativeQueryPropietarios(String query) {
        LOG.info("metodo sin implementer...");
        return null;
    }

    @Override
    public Object executeFunction(String function, String parametros, Boolean tipoVoid) {
        Object o = null;
        try {
            this.requiredTransaction();
            Session sess = HiberUtil.getSession();
            if (tipoVoid) {
                sess.createSQLQuery("SELECT " + function + "(" + parametros + ");");
            } else {
                o = sess.createSQLQuery("SELECT " + function + "(" + parametros + ");").uniqueResult();
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

}
