/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.service.catastro;

import com.origami.sgm.entities.OrdenDet;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import util.JsonUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Interceptors(value = {HibernateEjbInterceptor.class})
@ApplicationScoped
public class PredioEjb {

    @Inject
    private Entitymanager manager;
    private CatPredio predio, px;
    private CatPredioPropietario prop;
    private Collection<CatPredioPropietario> props;
    private Collection<CatPredioEdificacion> edif;
    private CatEnte ct;
    private SimpleDateFormat sdf;
    private EnteCorreo email;
    private EnteTelefono telf;
    private CatEscritura esc;
    private CatPredioS6 pserv;
    //private PredioVia via;
    //private Collection<PredioVia> vias;
    private CatPredioS6 cold;
    //private PredioUso puso;
    //private Collection<PredioColindante> colds;
    private CatPredioEdificacion edf;
    private CatPredioEdificacionProp ec;
    private JsonUtil js;
    @Inject
    private CatastroServices catas;

    @Lock(LockType.WRITE)
    public CatPredio guardarPredio(CatPredio p, String ref, boolean nuevo) {
        predio = null;
        CatPredioS4 s4 = null;
        px = null;
        js = new JsonUtil();
        OrdenDet d = null;
        List<OrdenDet> ot = null;
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        HashMap<String, Object> caracts = new HashMap<>();
        try {
            // Implementar metodos de guardados
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return predio;
    }

    public CtlgItem getValor(String nombre, Long Referencia) {
        CtlgItem gral = null;
        gral = manager.find(CtlgItem.class, Referencia);
        if (gral != null) {
            return gral;
        } else {
            gral = (CtlgItem) manager.find(Querys.getCatalogo, new String[]{"nombre", "referencia"}, new Object[]{nombre, Referencia});
        }
        return gral;
    }

    public CatPredioS4 guardarCaracteristica(CatPredio p) {
        try {
            if (p.getCatPredioS4() != null) {

            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public CatPredioEdificacionProp guardarCaracteristica(CatPredioEdificacion ed, Boolean dato, Long referencia, String usuario) {
        CatPredioEdificacionProp e = new CatPredioEdificacionProp();
        e.setEdificacion(ed);
        e.setEstado(Boolean.TRUE);
        e.setPorcentaje(new BigDecimal("100"));
        e.setProp(manager.find(CatEdfProp.class, referencia));
        return e;
    }

    @Lock(LockType.WRITE)
    public boolean guardarPropietario(List<CatEnte> c, CatPredio p, String usuario) {
        try {
            if (p != null && p.getId() != null) {
                CatPredioPropietario pp;
                for (CatEnte x : c) {
                    pp = (CatPredioPropietario) manager.find(Querys.getPropietario, new String[]{"identificacion", "numPredio"}, new Object[]{x.getCiRuc(), p.getNumPredio()});
                    if (pp == null) {
                        pp = new CatPredioPropietario();
                        pp.setPredio(p);
                        pp.setEnte(x);
                        pp.setFecha(new Date());
                        pp.setEstado("A");
                        pp.setUsuario(usuario);
                        manager.persist(pp);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Lock(LockType.WRITE)
    public Boolean quitarPropietario(CatPredioPropietario pp) {
        try {
            pp = (CatPredioPropietario) manager.persist(pp);
            return manager.delete(pp);
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Boolean quitarFoto(FotoPredio f) {
        try {
            f = (FotoPredio) manager.persist(f);
            return manager.delete(f);
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Boolean guardarUsoEdif(List<CtlgItem> usos, CatPredio p) {
        try {
            CatPredioS6 s12;
            manager.persist(p);
            if (p.getCatPredioS6()== null) {
                s12 = new CatPredioS6();
                s12.setPredio(p);
//                s12.setUsosList(usos);
            } else {
                s12 = p.getCatPredioS6();
//                s12.setUsosList(null);
                manager.persist(s12);
//                s12.setUsosList(usos);
            }
            manager.persist(s12);
            return true;
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

//    public List<CtlgItem> getUsoEdificacion(CatPredio p) {
//        List<CtlgItem> usos = new ArrayList<>();
//        for (CatPredioS12 u : p.getPredioUsoEdificacionCollection()) {
//            usos.add(u.getUso());
//        }
//        return usos;
//    }
    @Lock(LockType.WRITE)
    public CatEscritura guardarEscritura(CatEscritura pe) {
        try {
            if (pe.getPredio() != null) {
                return (CatEscritura) manager.persist(pe);
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Lock(LockType.WRITE)
    public CatEscritura guardarEscritura(CatPredio px, CatEscritura pe) {
        try {
            if (pe != null) {
                if (px.getCatEscrituraCollection() != null) {
                    for (CatEscritura ex : px.getCatEscrituraCollection()) {
                        manager.delete(ex);
                    }
                    esc = new CatEscritura();
                    esc.setPredio(px);
                    esc.setEstado("A");
                    
                    return (CatEscritura) manager.persist(esc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Lock(LockType.WRITE)
    public CatPredioS6 guardarServicio(CatPredio p, CatPredioS6 ps) {
        try {
            if (p.getCatPredioS6() != null) {
                pserv = p.getCatPredioS6();
            } else {
                pserv = new CatPredioS6();
                pserv.setPredio(px);
            }
            
            return (CatPredioS6) manager.persist(pserv);
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public CatPredioS6 guardarColindante(CatPredioS6 pc) {
        try {
            if (pc != null && pc.getPredio() != null) {
                return (CatPredioS6) manager.persist(pc);
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
//
//    @Lock(LockType.WRITE)
//    public boolean guardarColindante(CatPredio px, List<Colindante> cols) {
//        try {
//            if (px.getCatPredioS6() != null) {
//                manager.delete(px.getCatPredioS6());
//            }
//            CtlgItem cti = null;
//            for (Colindante cl : cols) {
//                cold = new CatPredioS6();
//                cold.setPredio(px);
//                if (cl.tipo != null) {
//                    cti = getValor("predio.colindante.cardinalidad", cl.tipo.longValue());
//                    if (cti != null) {
//                        cold.setPuntoCardinal(cti);
//                    } else {
//                        break;
//                    }
//                }
//                try {
//                    cold.setDimension(new BigDecimal(cl.dimension));
//                } catch (Exception e) {
//                }
//                manager.persist(cold);
//                return true;
//            }
//        } catch (Exception e) {
//            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return false;
//    }

//    public boolean guardarPredioUsos(CatPredio p, List<Componente> c) {
//        try {
//            if (px.getCatPredioS12() != null) {
//                for (PredioUso pu : px.getPredioUsoCollection()) {
//                    manager.delete(pu);
//                }
//                px.setPredioUsoCollection(null);
//                manager.persist(px);
//            }
//
//            CtlgItem cti = null;
//            for (Componente cl : c) {
//                puso = new PredioUso();
//                puso.setPredio(px);
//                puso.setFecCre(new Date());
//                puso.setEstado(Boolean.TRUE);
//                if (cl.tipo != null) {
//                    cti = getValor("predio.componente_ph", cl.tipo.longValue());
//                    if (cti != null) {
//                        puso.setComponente(cti);
//                    } else {
//                        break;
//                    }
//                }
//                try {
//                    puso.setDescripcion(cl.descripcion);
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//                manager.persist(puso);
//            }
//            return true;
//        } catch (Exception e) {
//            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return false;
//    }
    @Lock(LockType.WRITE)
    public Long getNumPredio() {
        Long x = 0L;
        try {
            x = (Long) manager.getNativeQuery("select max(num_predio+10000) from catastro.predio");
        } catch (Exception e) {
            x = 0L;
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return x;
    }

    @Lock(LockType.WRITE)
    public Long getSigPredioLegacy() {
        Long x = 0L;
        try {
            x = (Long) manager.getNativeQuery("select nextval('legacy.seq_leg_predio')");
        } catch (Exception e) {
            x = 0L;
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return x;
    }

    @Lock(LockType.WRITE)
    public Boolean guardarUso(CatPredioS6 u) {
        try {
            if (u != null) {
                return (CatPredioS6) manager.persist(u) != null;
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Lock(LockType.WRITE)
    public CatEnte guardarContacto(CatPredioPropietario p, String usuario) {
        CatEnte cx = null;
        try {
            
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    @Lock(LockType.WRITE)
    public CatPredioPropietario guardarPropietario(CatEnte c, CatPredio p, String usuario) {
        CatPredioPropietario pp = null;
        try {
            pp = (CatPredioPropietario) manager.find(Querys.getPropietario, new String[]{"identificacion", "numPredio"}, new Object[]{c.getCiRuc(), p.getNumPredio()});
            if (pp != null) {
                return pp;
            } else {
                pp = new CatPredioPropietario();
                pp.setPredio(p);
                pp.setEstado("A");
                pp.setFecha(new Date());
                pp.setEnte(c);
                pp.setUsuario(usuario);
                return (CatPredioPropietario) manager.persist(pp);
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public Boolean quitarEdificacion(CatPredio edfs, CatPredioEdificacion bloques, String usuario, HashMap<String, Object> caracts) {
        try {
            for (CatPredioEdificacion ex : edfs.getCatPredioEdificacionCollection()) {
                this.quitarEdificacion(ex);
            }
            edfs.setCatPredioEdificacionCollection(null);
            manager.persist(edfs);
            
            return true;

        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Boolean quitarEdificacion(CatPredioEdificacion ed) {
        try {
            for (CatPredioEdificacionProp cx : ed.getCatPredioEdificacionPropCollection()) {
                manager.delete(cx);
            }
            manager.delete(ed);
            return true;

        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Boolean quitarEdificacionGeneral(CatPredioEdificacion ed) {
        try {
            ed = (CatPredioEdificacion) manager.persist(ed);
            if (ed.getCatPredioEdificacionPropCollection() != null) {
                for (CatPredioEdificacionProp cx : ed.getCatPredioEdificacionPropCollection()) {
                    cx = (CatPredioEdificacionProp) manager.persist(cx);
                    manager.delete(cx);
                }
            }
            return manager.delete(ed);
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Lock(LockType.WRITE)
    public Boolean removerDetalle(OrdenDet d) {
        try {
            if (d != null) {
                for (FotoPredio fp : d.getFotosPredio()) {
                    fp = (FotoPredio) manager.persist(fp);
                    manager.delete(fp);
                }
                if (d.getPredio() != null) {
                    CatPredio pred = d.getPredio();
//                    for (PredioUso pu : pred.getPredioUsoCollection()) {
//                        pu = manager.persist(pu);
//                        manager.delete(pu);
//                    }
                    for (CatPredioPropietario pp : pred.getCatPredioPropietarioCollection()) {
                        pp = (CatPredioPropietario) manager.persist(pp);
                        manager.delete(pp);
                    }
                    for (CatEscritura pes : pred.getCatEscrituraCollection()) {
                        pes = (CatEscritura) manager.persist(pes);
                        manager.delete(pes);
                    }
                    for (CatPredioEdificacion pe : pred.getCatPredioEdificacionCollection()) {
                        for (CatPredioEdificacionProp escrt : pe.getCatPredioEdificacionPropCollection()) {
                            escrt = (CatPredioEdificacionProp) manager.persist(escrt);
                            manager.delete(escrt);
                        }
                    }
                    d = (OrdenDet) manager.persist(d);
                    manager.delete(d);
                } else {
                    d = (OrdenDet) manager.persist(d);
                    manager.delete(d);
                }
                return true;

            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

//    @Lock(LockType.WRITE)
//    public Boolean guardarPredioUsoEdif(Predio p, List<Integer> usuPredios, String usuario) {
//        PredioUsoEdificacion pue;
//        try {
//            if (p.getPredioUsoEdificacionCollection() == null) {
//                for (Integer usos : usuPredios) {
//                    pue = new PredioUsoEdificacion();
//                    pue.setPredio(predio);
//                    pue.setFecCre(new Date());
//                    pue.setEstado(Boolean.TRUE);
//                    pue.setUsrCre(usuario);
//                    pue.setUso(this.getValor("predio.uso_edificacion", usos.longValue()));
//                    manager.persist(pue);
//                }
//            } else {
//                if (!p.getPredioUsoEdificacionCollection().isEmpty()) {
//                    for (PredioUsoEdificacion pu : p.getPredioUsoEdificacionCollection()) {
//                        for (Integer usos : usuPredios) {
//                            if (!pu.getUso().getId().equals(usos.longValue())) {
//                                pue = new PredioUsoEdificacion();
//                                pue.setPredio(predio);
//                                pue.setFecCre(new Date());
//                                pue.setEstado(Boolean.TRUE);
//                                pue.setUsrCre(usuario);
//                                pue.setUso(this.getValor("predio.uso_edificacion", usos.longValue()));
//                                manager.persist(pue);
//                            }
//                        }
//                    }
//                } else {
//                    for (Integer usos : usuPredios) {
//                        pue = new PredioUsoEdificacion();
//                        pue.setPredio(predio);
//                        pue.setFecCre(new Date());
//                        pue.setEstado(Boolean.TRUE);
//                        pue.setUsrCre(usuario);
//                        pue.setUso(this.getValor("predio.uso_edificacion", usos.longValue()));
//                        manager.persist(pue);
//                    }
//                }
//            }
//            return true;
//
//        } catch (Exception e) {
//            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return false;
//    }
    public Boolean reprocesarPropietarios() {
        try {
            List<OrdenDet> dts = manager.findAll(Querys.getPrediosPendientes);
            for (OrdenDet d : dts) {
                
            }
        } catch (Exception e) {
            Logger.getLogger(PredioEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
}
