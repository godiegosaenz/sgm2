/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.listener;

import com.origami.censocat.restful.JsonUtils;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.models.EstadosPredio;
import com.origami.sgm.events.EliminacionPredioPost;
import com.origami.sgm.events.GenerarHistoricoPredioPost;
import com.origami.sgm.events.HistoricoPredioPost;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import util.Faces;

/**
 *
 * @author Angel Navarro
 */
@ApplicationScoped
@Singleton
@Lock(LockType.READ)
@Interceptors(HibernateEjbInterceptor.class)
public class ListenerEvent {

    private static final Logger LOG = Logger.getLogger(ListenerEvent.class.getName());

    @Inject
    protected Entitymanager manager;
    @Inject
    protected CatastroServices catastroServices;
//    @Inject
//    private GeoProcesosService geoService;

    public CatPredio getPredioByCodCat(String clavecat) {
        Map<String, Object> pm = new HashMap<>();
        pm.put("claveCat", clavecat);
        return manager.findObjectByParameter(CatPredio.class, pm);
    }

    public void deshabilitarPredio(@Observes EliminacionPredioPost deshabilitar) {
        if (deshabilitar.getCodPredio() != null) {
            CatPredio p = getPredioByCodCat(deshabilitar.getCodPredio());
            if (p != null) {
                p.setEstado(EstadosPredio.INACTIVO);
                manager.persist(p);
            }

        } else if (deshabilitar.getNumPredio() != null) {
            CatPredio p = catastroServices.getPredioNumPredio(deshabilitar.getNumPredio());
            p.setEstado(EstadosPredio.INACTIVO);
            manager.persist(p);
        } else if (deshabilitar.getPredio() != null) {
            deshabilitar.getPredio().setEstado(EstadosPredio.INACTIVO);
            manager.persist(deshabilitar.getPredio());
        }
        if (deshabilitar.getHabilitar()) {
            deshabilitar.getPredio().setEstado(EstadosPredio.ACTIVO);
            manager.persist(deshabilitar.getPredio());
        }
        if (deshabilitar.getHabilitar()) {
            System.out.println("Activación");
            Faces.messageInfo(null, "Activación ", " predio " + deshabilitar.getCodPredio() + " activado satisfactoriamente.");
        } else {
            Faces.messageInfo(null, "Eliminación ", " predio " + deshabilitar.getCodPredio() + " eliminado satisfactoriamente.");
        }

    }

    public void HistoricoPredio(@Observes HistoricoPredioPost historico) {
        if (historico.getCodPredio() != null) {
            CatPredio p = getPredioByCodCat(historico.getCodPredio());
            p.setEstado(EstadosPredio.HISTORICO);
            manager.persist(p);
        } else if (historico.getNumPredio() != null) {
            CatPredio p = catastroServices.getPredioNumPredio(historico.getNumPredio());
            p.setEstado(EstadosPredio.HISTORICO);
            manager.persist(p);
        } else if (historico.getPredio() != null) {
            historico.getPredio().setEstado(EstadosPredio.HISTORICO);
            manager.persist(historico.getPredio());
        }
    }

    public void guardarHistoricoPredio(@Observes GenerarHistoricoPredioPost historico) {
        try {
            if (historico.getPredioPost() != null) {
                manager.persist(historico.getPredioPost());
                historico.setPredioPost(this.limpiarCollection(historico.getPredioPost()));
            }
            if (historico.getPredioPost().getTipoConjunto() != null) {
                historico.getPredioPost().getTipoConjunto().setCatCiudadelaCollection(null); // No cargar la collection de Ciudadelas.
            }
            if (historico.getPredioPost().getEscrituraLinderos() != null && historico.getPredioPost().getEscrituraLinderos().getCanton() != null) {
                historico.getPredioPost().getEscrituraLinderos().getCanton().setCatParroquiaCollection(null);// no cargar la collection y parroquias.
            }
            if (historico.getPredio() != null && historico.getPredioPost() != null) {
                JsonUtils js = new JsonUtils();
                historico.setPredio(this.limpiarCollection(historico.getPredio()));
                catastroServices.guardarHistoricoPredio(historico.getPredioPost().getNumPredio().longValue(), js.generarJson(historico.getPredio()), js.generarJson(historico.getPredioPost()),
                        historico.getUser(), historico.getObservacion(), null, null, null, null, null);
            } else {
                catastroServices.guardarHistoricoPredio(historico.getPredioPost().getNumPredio().longValue(), historico.getJsonAnt(), historico.getJsonPost(),
                        historico.getUser(), historico.getObservacion(), null, null, null, null, null);
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public CatPredio limpiarCollection(CatPredio p) {
        if (p != null) {
            if (p.getTipoConjunto() != null) {
                p.getTipoConjunto().setCatCiudadelaCollection(null); // No cargar la collection de Ciudadelas.
            }
            if (p.getEscrituraLinderos() != null && p.getEscrituraLinderos().getCanton() != null) {
                p.getEscrituraLinderos().getCanton().setCatParroquiaCollection(null);// no cargar la collection y parroquias.
            }

            if (p.getCatEscrituraCollection() != null) {
                List<CatEscritura> escTemp = new ArrayList<>();
                p.getCatEscrituraCollection().stream().map((ce) -> {
                    if (ce.getCanton() != null && ce.getCanton().getCatParroquiaCollection() != null) {
                        ce.getCanton().setCatParroquiaCollection(null);
                    }
                    return ce;
                }).forEachOrdered((ce) -> {
                    escTemp.add(ce);
                });
                p.setCatEscrituraCollection(escTemp);// no cargar la collection y parroquias.
            }
        }
        return p;
    }

//    public void cessionPostEvent(@Observes CesionPredioPost post) {
//        try {
//            
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//    }
}
