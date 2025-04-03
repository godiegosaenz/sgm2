/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.transactionalcore.entitymanager;

import com.origami.sgm.acl.AccessLevelRequest;
import com.origami.sgm.acl.RespuestaAcceso;
import com.origami.sgm.acl.entity.AclUrl;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface Entitymanager {

    public void evict(Object obj);

    public <T extends Object> T find(Class<T> entity, Object id);

    public <T extends Object> List<T> findAll(Class<T> entity);

    public Object find(String query, String[] par, Object[] val);

    public Object findUnique(String query, String[] par, Object[] val);

    public List findMax(String query, String[] par, Object[] val, Integer max);

    public List findAll(String query);

    public <T extends Object> T findNoProxy(Class<T> entity, Object id);

    public Object findNoProxy(String query, String[] par, Object[] val);

    public List findAll(String query, String[] par, Object[] val);

    public <T> List<T> findAllEntCopy(Class<T> entity);

    public List findAllEntCopy(String query, String[] par, Object[] val);

    public List findAllEntCopy(String query);

    public Object persist(Object o);

    public Object saveAll(Object entity);

    public boolean delete(Object o);

    public boolean update(Object o);

    public boolean executeNativeQuery(String query, Object[] val);

    public <T> List<T> nativeQuery(String query, Object[] val, Class<T> entity);

    public Object getNativeQuery(String query);

    public Object getNativeQuery(String query, Object[] val);

    public List getSqlQuery(String query);

    public <T> List<T> getNativeQuery(Class<T> entity, String query, String[] params, Object[] values);

    public <T> List<T> getSqlQueryParametros(Class<T> clase, String query);

    public <T> List<T> getSqlQueryParametros(Class<T> clase, String query, String[] params, Object[] values);

    public List<Object[]> getManyColumnsResults(String query, String[] params, Object[] values);

    public List<Object[]> getManyColumnsResults(String query);

    public Criteria getCriteriaQuery(Class entity, String alias);

    public Session getSession();

    public Transaction requiredTransaction();

    public List findAllByEntities(String query, String[] entitiesNames, Object[] entities);

    public boolean saveList(List entities);

    public boolean deleteList(List entities);

    public Object find(String query);

    public List findFirstAndMaxResult(String query, String[] par, Object[] val, Integer first, Integer max);

    public <T> List<T> findAllOrdered(Class<T> entity, String[] order, Boolean[] prior);

    public <T> List<T> findAllOrdEntCopy(Class<T> entity, String[] order, Boolean[] prior);

    /**
     * Busca la Entiti en el paquete "com.origami.sgm.entities" si no existe
     * retorna nulo, caso contrario la entity si existe en la base de datos
     *
     * @param <T>
     * @param entity Nombre de Entity
     * @param id Id de la tabla.
     * @return Entity.
     */
///    public <T> T findEntity(String entity, Long id);

    public <T> T findObjectByParameter(Class entity, Map<String, Object> paramt);

    /**
     * Por default el order es desc
     *
     * @param <T>
     * @param entity Entiti class
     * @param order Nombre de Los campos a ordenar
     * @param ascDes True ordena Asc, False o Null Desc.
     * @return
     */
    public <T> List<T> findAllObjectOrder(Class entity, String[] order, Boolean ascDes);

    public <T> List<T> findObjectByParameterList(Class entity, Map<String, Object> paramt);

    public <T> List<T> findObjectByParameterOrderList(Class entity, Map<String, Object> paramt, String[] order, Boolean ascDes);

    public Object findObjectByParameter(String query, Map<String, Object> paramt);

    /**
     *
     * @param entity Clase de Entiti
     * @param paramt
     * @param paramtIn el key es nombre de la propiedad y el valor el es la
     * lsita para el in.
     * @return
     */
    public List findIn(Class entity, Map<String, Object> paramt, Map<String, Object> paramtIn);

    public Object executeFunction(String function, Map<String, Object> paramt, Boolean tipoVoid);

    public Object executeFunction(String function, List<Object> paramt, Boolean tipoVoid);

    public List findNamedQuery(String namedQuery, Map<String, Object> paramt);

    /**
     * Realiza la busqueda por los nameQuerys que se encuentran definidos en los
     * entitis
     *
     * @param <T>
     * @param namedQuery Nonmbre del Name Query que se encuentra en la entiti.
     * @param paramt Mapa de parametros que recibe la consulta.
     * @return
     */
    public <T> T findObjectNamedQuery(String namedQuery, Map<String, Object> paramt);

    public <T> List<T> findListNamedQuery(String namedQuery, Map<String, Object> paramt);

    public <T> List<T> findFirstAndMaxResult(Class<T> entity, Integer first, Integer max);

    public <T> List<T> getPaginatedResult(String query, int first, int pageSize, String[] params, Object[] values);

    public CatEnte guardarEnteCorreosTlfns(CatEnte ente);

    public CatEnte guardarEnteCorreosTlfnos(CatEnte ente);

    public Boolean editarEnteCorreosTlfns(CatEnte ente);

    public List<AclUser> getTecnicosByRol(List<Long> roles);

    public List<AclRol> getRoles_url(AclUrl urlEnt);

    public List<AclRol> getRoles_url(Long idUrl);

    public RespuestaAcceso checkAccessLevel(AccessLevelRequest alreq);

    public Object getNativeQueryPropietarios(String query);

    public Object ejecutarFuncionAvaluosEmisionPredial(String function, Long predioId, Integer anioInio, Integer anioFin, String usuario);

    public Object ejecutarFuncionCleanAvaluos(String user, Long predioId);
    
    public Object executeFunction(String function, String parametros, Boolean tipoVoid);
}
