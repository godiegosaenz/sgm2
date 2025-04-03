/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatProvincia;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import util.EntityBeanCopy;

/**
 *
 * @author Angel Navarro
 */
@Named
@Singleton
@Interceptors(value = {HibernateEjbInterceptor.class})
@ApplicationScoped
public class ServiceLists {

    private static final Logger LOG = Logger.getLogger(ServiceLists.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private CatastroServices catas;

    public List<RenLocalUbicacion> getLocalUbicacions() {
        return (List<RenLocalUbicacion>) EntityBeanCopy.clone(manager.findAllOrdered(RenLocalUbicacion.class, new String[]{"descripcion"}, new Boolean[]{true}));
    }

    public List<RenActividadComercial> getActividadComercials() {
        return (List<RenActividadComercial>) EntityBeanCopy.clone(manager.findAllOrdered(RenActividadComercial.class, new String[]{"descripcion"}, new Boolean[]{true}));
    }

    public List<RenLocalCategoria> getLocalCategorias() {
        return (List<RenLocalCategoria>) EntityBeanCopy.clone(manager.findAllOrdered(RenLocalCategoria.class, new String[]{"descripcion"}, new Boolean[]{true}));
    }

    //**********************************//
    //********Listas de Catastro********//
    //**********************************//
    public List<CatCiudadela> getCiudadelas() {
        return (List<CatCiudadela>) EntityBeanCopy.clone(manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE));
    }

    public List<CatParroquia> getParroquiasRurales() {
        return (List<CatParroquia>) manager.findAllEntCopy(Querys.parroquiasRurales);
    }

    // Exoneraciones, Descuentos y Recargos
    /**
     *
     * @return Retorna todos los FnExoneracionClase sin proxis
     */
    public List<FnExoneracionClase> getClases() {
        return (List<FnExoneracionClase>) manager.findAllEntCopy(QuerysFinanciero.getClaseExoneracionByState);
    }

    /**
     * Listado de Retorna el Listado de FnExoneracionTipo sin proxis
     *
     * @param clase FnExoneracionClase
     * @return retorna la lista FnExoneracionTipo si existen caso contrario
     * null.
     */
    public List<FnExoneracionTipo> getTipos(FnExoneracionClase clase) {
        return (List<FnExoneracionTipo>) manager.findAllEntCopy(QuerysFinanciero.getTipoExoneracionTipoByClaseAndState, new String[]{"clase"}, new Object[]{clase});
    }

    /**
     * Si el prefijo es nulo devualve todos los RenTipoValor
     *
     * @param prefijo
     * @return
     */
    public List<RenTipoValor> getTipoValores(String prefijo) {
        if (!prefijo.isEmpty()) {
            return (List<RenTipoValor>) manager.findAllEntCopy(QuerysFinanciero.getRenTipoValoresByPrefijo, new String[]{"prefijo"}, new Object[]{prefijo});
        } else {
            List<RenTipoValor> renTipoValors = manager.findAll(QuerysFinanciero.getRenTipoValorList);
            return renTipoValors;
        }
    }

    public List<RenTipoValor> getTipoValoresById(Long id) {
        if (id != null) {
            return (List<RenTipoValor>) manager.findAllEntCopy(QuerysFinanciero.getRenTipoValoresById, new String[]{"id"}, new Object[]{id});
        }else{
            return null;
        }
    }

    public List<CtlgItem> getListado(String argumento) {
        return (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
    }

    public CtlgItem listadoItemsCultivos(CtlgItem tipo) {
        if (tipo.getHijo() != null) {
            return (CtlgItem) EntityBeanCopy.clone(manager.find(Querys.getCtlgItemaByCultivosHijos, new String[]{"hijo"}, new Object[]{tipo.getHijo().longValue()}));
        }
        return null;
    }

    public List<CatCanton> getCantones() {
        return manager.findAllEntCopy(CatCanton.class);
    }

    public List<CatProvincia> getProvincias() {
        return manager.findAllEntCopy(CatProvincia.class);
    }

    public CatProvincia obtenerCatProvinciaxCodNac(Integer codnac) {
        if (codnac == null) {
            return null;
        }
        CatProvincia provincia = (CatProvincia) EntityBeanCopy.clone(manager.find(Querys.getCatProvinciaxCodNac, new String[]{"codNac"}, new Object[]{codnac.shortValue()}));
        return provincia;
    }

    public List<CatParroquia> getParroquiasxCanton(CatCanton canton) {
        if (canton == null) {
            return manager.findAllEntCopy(CatParroquia.class);
        } else {
            Map<String, Object> mp = new HashMap<>();
            mp.put("idCanton", canton);
            return (List<CatParroquia>) EntityBeanCopy.clone(manager.findObjectByParameterList(CatParroquia.class, mp));
        }
    }

    public CatCanton obtenerCatCatonxCodNac(String codnac) {
        CatCanton canton = (CatCanton) EntityBeanCopy.clone(manager.find(Querys.getCatCatonxCodNac, new String[]{"codNac"}, new Object[]{Short.valueOf(codnac)}));
        return canton;
    }

    public List<CatCanton> getCantones(CatProvincia prov) {
        if (prov == null) {
            return getCantones();
        } else {
            Map<String, Object> mp = new HashMap<>();
            mp.put("idProvincia", prov);
            return (List<CatCanton>) EntityBeanCopy.clone(manager.findObjectByParameterList(CatCanton.class, mp));
        }

    }

    public List<CatTipoConjunto> getTiposConjunto() {
        return manager.findAllEntCopy(CatTipoConjunto.class);
    }

    public List<CatCiudadela> getCiudadelas(CatTipoConjunto tipoConjunto) {
        try {
            if (tipoConjunto != null) {
                return catas.getCiudadelasByTipoConjunto(tipoConjunto);
            } else {
                return catas.getCiudadelasByTipoConjunto(null);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getCiudadelas", e);
        }
        return null;
    }

//    public List<CatPredioPropietario> getPropietarios() {
//        return catas.propiedadHorizontal().getPermiso().;
//    }
}
