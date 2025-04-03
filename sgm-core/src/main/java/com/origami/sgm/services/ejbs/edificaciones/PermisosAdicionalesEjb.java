/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermisosAdicionales;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeUnidadMedida;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisosAdicionalesServices;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.activiti.engine.runtime.ProcessInstance;

/**
 *
 * @author Joao Sanga
 */
@Stateless(name = "permisosAdicionalesServices")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class PermisosAdicionalesEjb implements PermisosAdicionalesServices {

    @javax.inject.Inject
    private Entitymanager services;

    @Override
    public CatEnte obtenerCatEntePorQuery(String query, String[] parametros, Object[] valores) {
        CatEnte ente;
        try {
            ente = (CatEnte) services.find(query, parametros, valores);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ente;
    }

    @Override
    public HistoricoTramites iniciarProceso(HistoricoTramites ht, Observaciones obs, ProcessInstance p) {
        try {
            ht.setIdProcesoTemp(p.getId());
            ht.setCarpetaRep(ht.getId() + "-" + p.getId());
            obs.setIdTramite(ht);
            services.update(ht);
            services.persist(obs);
        } catch (Exception e) {
            Logger.getLogger(PermisosAdicionalesEjb.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
            return null;
        }
        return ht;
    }

    @Override
    public List<CatEdfProp> getMaterialesPorNombreCategoria(String nomCategoria) {
        List<CatEdfProp> list = null;
        try {
            list = services.findAllEntCopy(Querys.getCatEdfPropListByNom, new String[]{"nombreCateg"}, new Object[]{nomCategoria});
        } catch (Exception e) {
            Logger.getLogger(PermisosAdicionalesEjb.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public List<PeUnidadMedida> getUnidadesMedida() {
        List<PeUnidadMedida> list;
        list = (List<PeUnidadMedida>) services.findAll(Querys.getPeUnidadMedidaList, new String[]{}, new Object[]{});
        return list;
    }

    @Override
    public Boolean guardarPermisoAdicional(PePermisosAdicionales pAdicional, HistoricoReporteTramite hrt, HistoricoTramiteDet htd, List<HistoricoTramiteDet> htdList, List<HistoricoReporteTramite> hrtList, List<PeDetallePermisosAdicionales> pdpaList, Observaciones obs, CatPredio datosPredio, List<CatPredioPropietario> lisPropietarios, HistoricoTramites ht) {
        Boolean b;
        try {
            b = true;

            services.update(ht);
            services.persist(obs);
            /*
             for(PeDetallePermisosAdicionales temp : pdpaList){
             if(temp.getId()==null){
             temp.setIdPermisosAdicionales(pAdicional);
             services.persist(temp);
             }else{
             services.update(temp);
             }
             }*/

            //services.persist(hrt);

            services.persist(htd);

            services.update(datosPredio);

            for (CatPredioPropietario cpp : lisPropietarios) {
                if (cpp.getId() == null) {
                    services.persist(cpp);
                } else {
                    services.update(cpp);
                }
            }
            if (hrtList != null) {
                for (HistoricoReporteTramite hrtTemp : hrtList) {
                    hrtTemp.setEstado(false);
                    services.update(hrtTemp);
                }
            }
            if (htdList != null) {
                for (HistoricoTramiteDet htdTemp : htdList) {
                    htdTemp.setEstado(false);
                    services.update(htdTemp);
                }
            }
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public PePermisosAdicionales getPePermisoAdicionalesById(Long idHtd) {
        return (PePermisosAdicionales) services.find(PePermisosAdicionales.class, idHtd);
    }

    @Override
    public boolean actualizarPermisoAdicionales(PePermisosAdicionales permisoAdicional, List<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesList, List<CatPredioPropietario> lisPropietarios, HistoricoTramiteDet htd, HistoricoTramites ht, CatPredio datosPredio) {
        try {
            if (ht != null) {
                services.update(ht);
            }
            if (htd != null) {
                services.update(htd);
            }
            services.update(datosPredio);
            services.update(permisoAdicional);
            
            for (CatPredioPropietario cpp : lisPropietarios) {
                if (cpp.getId() == null) {
                    services.persist(cpp);
                } else {
                    services.update(cpp);
                }
            }
            
            for(PeDetallePermisosAdicionales temp : peDetallePermisosAdicionalesList){
                if(temp.getId() == null)
                    services.persist(temp);            
            }
            
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al actualizar Permiso Adicionales.", e);
            return false;
        }
    }
    private static final Logger LOG = Logger.getLogger(PermisosAdicionalesEjb.class.getName());

}
