/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.catastro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.origami.censocat.restful.JsonUtils;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.AvalValorSuelo;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioClasificRural;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CatPredioFusionDivision;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.models.EstadosPredio;
import com.origami.sgm.enums.TipoProceso;
import com.origami.sgm.events.FusionPrediosPost;
import com.origami.sgm.events.ValorarPredioPost;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.ejbs.SeqGenManEjb;
import com.origami.sgm.services.ejbs.censocat.UploadFotoBean;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.catastro.FusionDivisionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import sun.nio.cs.ext.Big5;
import util.EntityBeanCopy;
import util.HiberUtil;
import util.HibernateProxyTypeAdapter;
import util.Utils;

/**
 *
 * @author Xndy
 * Sxnchez :v
 */
@Stateless(name = "fusionDivision")
@Interceptors(value = {HibernateEjbInterceptor.class})
//@Dependent
public class FusionDivisionEjb implements FusionDivisionServices {

    @Inject
    protected Entitymanager manager;

    @Inject
    protected CatastroServices catas;
    @Inject
    protected UploadFotoBean fotoBean;

    @Inject
    protected Event<FusionPrediosPost> event;

    @Inject
    private AvaluosServices avaluosServices;

    @Inject
    private UserSession user;
    @Inject
    protected Event<ValorarPredioPost> eventValoracion;

    @Override
    public void savePredioFusionDivision(CatPredio predioRaiz, CatPredio predioResultante, String tipo) {
        try {
            if (predioRaiz != null) {
                CatPredioFusionDivision pdf = new CatPredioFusionDivision();
                pdf.setPredioRaiz(predioRaiz);
                pdf.setPredioResultante(predioResultante);
                pdf.setTipo(getTipoFraccionPredio(tipo));
                pdf.setFechaIngreso(new Date());
                pdf.setEstado(Boolean.TRUE);
                manager.persist(pdf);
            }

        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "savePredioFusionDivision", e);
        }

    }

    @Override
    public CtlgItem getTipoFraccionPredio(String tipo) {
        try {
            return (CtlgItem) manager.find(Querys.getCtlgItemByCatalogoValor, new String[]{"catalogo", "valor"}, new Object[]{"predio.fusion_division", tipo});
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "getTipoFraccionPredio", e);
        }
        return null;
    }

    @Override
    public ArrayList<CatPredio> saveDivisionPredio(CatPredio predioRaiz, ArrayList<CatPredio> predioDivision) {
        HiberUtil.closeSession();
        AvalValorSuelo suelo = null;
        ArrayList<CatPredio> prediosResultantes = new ArrayList<CatPredio>();
        ArrayList<CatPredio> prediosAvaluo = new ArrayList<CatPredio>();
        try {
            BigDecimal areaSolarDivision = new BigDecimal("0.00");
            BigDecimal areaSolarTotalesDivididas = new BigDecimal("0.00");
            Short solarDivision, unidadDivision = (short) 0;
            for (CatPredio p : predioDivision) {
                solarDivision = p.getSolar();
                areaSolarDivision = p.getAreaSolar();
                unidadDivision = p.getUnidad();
                getValorBaseM2Avaluo(predioRaiz, solarDivision);
                p = this.clonePredioWithOutId(predioRaiz, p);
                p.setId(null);
                p.setAvaluoSolar(new BigDecimal("0.00"));
                p.setAvaluoConstruccion(new BigDecimal("0.00"));
                p.setAvaluoMunicipal(new BigDecimal("0.00"));
                p.setAreaSolar(areaSolarDivision);
                p.setSolar(solarDivision);
                p.setLote(solarDivision);
                p.setUnidad(unidadDivision);
                p.setNumPredio(generarNumPredio());
                p.setPredialant(null);
                p.setUsuarioCreador((AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{user.getName_user()} ));
                //p = this.registrarPredio(p);
                p = this.saveAllDataPredio(p);
                savePredioFusionDivision(predioRaiz, p, "DIVISION");
                this.saveEdificacionesPredio(predioRaiz, p);
                this.saveFotosPredio(predioRaiz, p);
                prediosResultantes.add(p);
                prediosAvaluo.add(p);
                areaSolarTotalesDivididas = areaSolarDivision.add(areaSolarTotalesDivididas);
            }
            if (predioRaiz != null) {
                areaSolarDivision = new BigDecimal("0.00");
                areaSolarTotalesDivididas = new BigDecimal("0.00");
                for (CatPredio p : predioDivision) {
                    areaSolarTotalesDivididas = p.getAreaSolar().add(areaSolarTotalesDivididas);
                }
                predioRaiz.setAreaSolar(predioRaiz.getAreaSolar().subtract(areaSolarTotalesDivididas));
                if (predioRaiz.getAreaSolar().compareTo(BigDecimal.ZERO) == 0) {
                    predioRaiz.setEstado("H");
                }
                manager.persist(predioRaiz);
            }
            prediosAvaluo.add(predioRaiz);
            avaluosServices.generateAvaluo(prediosAvaluo,
                    Utils.getAnio(new Date()), Utils.getAnio(new Date()), false, user.getName_user());
            return prediosResultantes;
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "saveDivisionPredio", e);
        }
        return prediosResultantes;
    }

    @Override
    public CatPredio registrarPredio(CatPredio px) {
        HiberUtil.closeSession();
        CatPredio pred = null;
        try {
            if (px != null) {
                try {
                    px.setClaveCat(claveCatastral(px));
                } catch (Exception e) {
                }
                if (px.getId() == null) {
                    pred = (CatPredio) manager.persist(px);
                } else {
                    manager.persist(px);
                }
                if (px.getCatPredioS6() != null) {
//                    if (px.getCatPredioS6() == null) {
                    px.getCatPredioS6().setPredio(pred);
                    px.getCatPredioS6().setId(null);
                    px.setCatPredioS6((CatPredioS6) manager.persist(px.getCatPredioS6()));
//                    } else {
//                        manager.persist(px.getCatPredioS6());
//                    }
                }
                if (px.getCatPredioS4() != null) {
//                    if (px.getCatPredioS4() == null) {
                    px.getCatPredioS4().setPredio(pred);
                    px.getCatPredioS4().setId(null);
                    px.setCatPredioS4((CatPredioS4) manager.persist(px.getCatPredioS4()));
//                    } else {
//                        manager.persist(px.getCatPredioS4());
//                    }
                }
                if (px.getCatPredioPropietarioCollection() != null) {
                    List<CatPredioPropietario> lp = new ArrayList<>();
                    for (CatPredioPropietario ccp : px.getCatPredioPropietarioCollection()) {
//                        if (ccp == null) {
                        ccp.setPredio(pred);
                        ccp.setId(null);
                        lp.add((CatPredioPropietario) manager.persist(ccp));
//                        } else {
//                            manager.persist(ccp);
//                        }
                    }
                    px.setCatPredioPropietarioCollection(lp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }
        return pred;
    }

    @Override
    public CatPredioEdificacion registrarEdificacion(CatPredioEdificacion edificacion) {
        CatPredioEdificacion edf = null;
        try {
            if (edificacion != null) {

                edf = (CatPredioEdificacion) manager.persist(edificacion);
            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }

        return edf;
    }

    @Override
    public CatPredioEdificacion clonarEdificacion(CatPredioEdificacion edificacion, CatPredio predio, Short nroEdificacion, boolean save) {
        CatPredioEdificacion clon = new CatPredioEdificacion();
        BeanUtils.copyProperties(edificacion, clon);
        clon.setId(null);
        clon.setPredio(predio);
        clon.setNoEdificacion(nroEdificacion);
        if (save) {
            clon = registrarEdificacion(clon);
        }
        return clon;
    }

    @Override
    public CatPredioEdificacionProp registrarEdificacionProp(CatPredioEdificacionProp detalle) {
        CatPredioEdificacionProp det = null;
        try {
            if (detalle != null) {

                det = (CatPredioEdificacionProp) manager.persist(detalle);
            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }
        return det;
    }

    @Override
    public CatPredioEdificacionProp clonarEdificacionProp(CatPredioEdificacionProp detalle, CatPredioEdificacion edificacion, CatEdfProp prop, boolean save) {
        CatPredioEdificacionProp clon = new CatPredioEdificacionProp();
        BeanUtils.copyProperties(detalle, clon);
        clon.setId(null);
        clon.setEdificacion(edificacion);
        clon.setProp(prop);
        //prop.getCatPredioEdificacionPropCollection().add(clon);

        if (save) {
            clon = registrarEdificacionProp(clon);
        }
        return clon;
    }

    @Override
    public CatPredioS4 registrarPredioS4(CatPredioS4 predioS4) {
        CatPredioS4 s4 = null;
        try {
            if (predioS4 != null) {

                s4 = (CatPredioS4) manager.persist(predioS4);
            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }

        return s4;
    }

    @Override
    public CatPredioS4 clonarPredioS4(CatPredioS4 predioS4, CatPredio predio, boolean save) {
        CatPredioS4 clon = new CatPredioS4();

        BeanUtils.copyProperties(predioS4, clon);
        clon.setId(null);
        clon.setPredio(predio);
        predio.setCatPredioS4(clon);
        if (save) {
            clon = registrarPredioS4(clon);
        }

        return clon;
    }

    @Override
    public CatPredioS6 registrarPredioS6(CatPredioS6 predioS6) {
        CatPredioS6 s6 = null;
        try {
            if (predioS6 != null) {

                s6 = (CatPredioS6) manager.persist(predioS6);
            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }

        return s6;
    }

    @Override
    public CatPredioS6 registrarPredioS6(CatPredioS6 predioS6, Collection<CtlgItem> vias, Collection<CtlgItem> instalaciones) {
        CatPredioS6 s6 = null;
        try {
            if (predioS6 != null) {

                for (CtlgItem item : vias) {
                    item.getCatPredioS6Collection().add(s6);
                }
                s6 = (CatPredioS6) manager.persist(predioS6);

            }
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }

        return s6;
    }

    @Override
    public CatPredio clonarPredio(CatPredio predio, Short bloque, Short piso, Short unidad, BigInteger numP) {

        String[] ignoresProperties = new String[]{"catPredioS4", "catPredioS6", "catPredioPropietarioCollection"};
        CatPredio nuevo = new CatPredio();
        BeanUtils.copyProperties(predio, nuevo, ignoresProperties);
        nuevo.setId(null);
        nuevo.setNumPredio(numP);
        nuevo.setNumeroFicha(nuevo.getNumPredio());
        nuevo.setInstCreacion(new Date());
        nuevo.setPredialant(predio.getClaveCat());
        nuevo.setEstado(EstadosPredio.ACTIVO);
        nuevo.setBloque(bloque);
        nuevo.setPiso(piso);
        nuevo.setUnidad(unidad);
        nuevo.setPredioRaiz(new BigInteger(predio.getId().toString()));
        nuevo.setPropiedadHorizontal(Boolean.TRUE);
        nuevo.setAreaDeclaradaConst(null);
        nuevo.setCatPredioPropietarioCollection(new ArrayList<>());
        nuevo.setAlicuotaComponentes(new ArrayList<>());
        nuevo.setObservaciones(null);
        nuevo = this.registrarPredio(nuevo);

        CatPredioS4 predioS4 = catas.getPredioS4ByPredio(predio);
        if (predioS4 != null) {
            this.clonarPredioS4(predioS4, nuevo, true);
        }
        CatPredioS6 predioS6 = catas.getPredioS6ByPredio(predio);
        if (predioS6 != null) {
            this.clonarPredioS6(predioS6, nuevo, predio.getCatPredioS6().getCtlgItemCollection(), predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial(), true);
        }
        String[] param = {"idEdificacion"};
        Object[] values = new Object[2];
        Collection<CatPredioEdificacionProp> detallesEdificacion;
        Collection<CatPredioEdificacion> edificacionesActuales = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{predio.getId()});
        CatPredioEdificacion edificacion;
        short numeroEdificacion = (short) 1;
        for (CatPredioEdificacion cpe : edificacionesActuales) {
            edificacion = this.clonarEdificacion(cpe, nuevo, numeroEdificacion++, true);
            values[0] = cpe.getId();
            detallesEdificacion = manager.findAll(Querys.getDetallesEdificacion, param, values);
            String[] param1 = {"idEdificacion", "idProp"};
            Object[] values1 = new Object[2];
            if (detallesEdificacion != null) {
                for (CatPredioEdificacionProp det : detallesEdificacion) {
                    values1[0] = cpe.getId();
                    values1[1] = det.getId();
                    CatEdfProp prop = (CatEdfProp) manager.find(Querys.getProps, param1, values1);
                    if (prop != null) {
                        this.clonarEdificacionProp(det, edificacion, prop, true);
                    }

                }
            }
        }

        Collection<CatPredioPropietario> duenos = predio.getCatPredioPropietarioCollection();
        for (CatPredioPropietario cpp : duenos) {
            this.clonarPropietario(cpp, nuevo);
        }

        return nuevo;
    }

    @Override
    public CatPredioS6 clonarPredioS6(CatPredioS6 predioS6, CatPredio predio, Collection<CtlgItem> vias, Collection<CtlgItem> instalaciones, boolean save) {
        CatPredioS6 clon = new CatPredioS6();
        BeanUtils.copyProperties(predioS6, clon);
        clon.setId(null);
        clon.setPredio(predio);
        predio.setCatPredioS6(predioS6);
        if (vias != null) {
            for (CtlgItem item : vias) {
                item.getCatPredioS6Collection().add(clon);
            }
        }
        if (instalaciones != null) {
            for (CtlgItem item : instalaciones) {
                item.getCatPredioS6CollectionInstalaciones().add(clon);
            }
        }
        clon.setCtlgItemCollection(vias);
        clon.setCtlgItemCollectionInstalacionEspecial(instalaciones);
        if (save) {
            clon = registrarPredioS6(clon);
        }

        return clon;
    }

    @Override
    public CatPredioPropietario clonarPropietario(CatPredioPropietario propietario, CatPredio predio) {
        CatPredioPropietario clon = new CatPredioPropietario();
        BeanUtils.copyProperties(propietario, clon);
        clon.setId(null);
        clon.setPredio(predio);
        try {
            clon = (CatPredioPropietario) manager.persist(clon);
        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "registrarPredio", e);
        }

        return clon;
    }

    @Override
    public Collection<FotoPredio> cambiarPredio(CatPredio old, CatPredio predio) {
        String[] param = {"idPredio"};
        Object[] value = {old.getId()};
        Collection<FotoPredio> photos = manager.findAll(Querys.getFotosByPredio, param, value);
        Collection<FotoPredio> fotos = new ArrayList<>();
        photos.stream().map((fp) -> {
            FotoPredio clon = new FotoPredio();
            BeanUtils.copyProperties(fp, clon);
            return clon;
        }).map((clon) -> {
            clon.setIdPredio(predio.getId());
            return clon;
        }).map((clon) -> {
            clon.setCodPredio(Long.parseLong(predio.getNumPredio().toString()));
            return clon;
        }).map((clon) -> {
            clon.setNombre(predio.getNumPredio().toString());
            return clon;
        }).map((clon) -> (FotoPredio) manager.persist(clon)).forEachOrdered((f) -> {
            fotos.add(f);
        });

        return fotos;
    }

    @Override
    public CatPredio clonarPredioFusion(CatPredio predio, List<CatPredio> predios, Short codLote, boolean withEvent) {

        List<String> codigosFusionados = new LinkedList<>();
        String claveResultante;
        BigDecimal areaTotal = BigDecimal.ZERO;
        codigosFusionados.add(predio.getClaveCat());
        if (predio.getAreaSolar() != null) {
            areaTotal = predio.getAreaSolar();
        }
        for (CatPredio p : predios) {
            if (p.getAreaSolar() != null) {
                areaTotal = areaTotal.add(p.getAreaSolar());
            }
        }
        CatPredio nuevo = new CatPredio();
        BeanUtils.copyProperties(predio, nuevo, new String[]{"catPredioS4", "catPredioS6", "catPredioPropietarioCollection"});
        nuevo.setId(null);
        nuevo.setNumPredio(this.generarNumPredio());
        nuevo.setNumeroFicha(nuevo.getNumPredio());
        nuevo.setInstCreacion(new Date());
        nuevo.setAreaSolar(areaTotal);
        nuevo.setSolar(codLote);
        nuevo.setLote(codLote);
        nuevo.setPredialant(predio.getClaveCat());
        nuevo.setUsuarioCreador((AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{user.getName_user()} ));
        if (withEvent) {
            nuevo.setEstado(EstadosPredio.ACTIVO);
        }
        //AQUI
        getValorBaseM2Avaluo(nuevo, nuevo.getSolar());
        nuevo = this.registrarPredio(nuevo);
        claveResultante = nuevo.getClaveCat();
        CatPredioS4 predioS4 = catas.getPredioS4ByPredio(predio);
        if (predioS4 != null) {
            this.clonarPredioS4(predioS4, nuevo, true);
        }
        CatPredioS6 predioS6 = catas.getPredioS6ByPredio(predio);
        if (predioS6 != null) {
            this.clonarPredioS6(predioS6, nuevo, predio.getCatPredioS6().getCtlgItemCollection(), predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial(), true);
        }
        String[] param = {"idEdificacion"};
        Object[] values = new Object[2];
        Collection<CatPredioEdificacionProp> detallesEdificacion;
        Collection<CatPredioEdificacion> edificacionesActuales = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{predio.getId()});
        CatPredioEdificacion edificacion;
        short numeroEdificacion = (short) 1;
        for (CatPredioEdificacion cpe : edificacionesActuales) {
            edificacion = this.clonarEdificacion(cpe, nuevo, numeroEdificacion++, true);
            values[0] = cpe.getId();
            detallesEdificacion = manager.findAll(Querys.getDetallesEdificacion, param, values);
            String[] param1 = {"idEdificacion", "idProp"};
            Object[] values1 = new Object[2];
            if (detallesEdificacion != null) {
                for (CatPredioEdificacionProp det : detallesEdificacion) {
                    values1[0] = cpe.getId();
                    values1[1] = det.getId();
                    CatEdfProp prop = (CatEdfProp) manager.find(Querys.getProps, param1, values1);
                    if (prop != null) {
                        this.clonarEdificacionProp(det, edificacion, prop, true);
                    }

                }
            }
        }
        for (CatPredio cp : predios) {
            codigosFusionados.add(cp.getClaveCat());
            Collection<CatPredioEdificacion> edificacionesNuevas = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{cp.getId()});
            for (CatPredioEdificacion cpe : edificacionesNuevas) {
                edificacion = this.clonarEdificacion(cpe, nuevo, numeroEdificacion++, true);
                values[0] = cp.getId();
                values[1] = cpe.getId();
                detallesEdificacion = manager.findAllEntCopy(Querys.getDetallesEdificacion, param, values);
                String[] param1 = {"idEdificacion", "idProp"};
                Object[] values1 = new Object[2];
                for (CatPredioEdificacionProp det : detallesEdificacion) {
                    values1[0] = cpe.getId();
                    values1[1] = det.getId();

                    CatEdfProp prop = (CatEdfProp) manager.find(Querys.getProps, param1, values1);
                    if (prop != null) {
                        this.clonarEdificacionProp(det, edificacion, prop, true);
                    }
                }
            }
        }
        Collection<CatPredioPropietario> duenos = predio.getCatPredioPropietarioCollection();
        for (CatPredioPropietario cpp : duenos) {
            this.clonarPropietario(cpp, nuevo);
        }
        predios.add(predio);
        for (CatPredio pp : predios) {
            this.cambiarPredio(pp, nuevo);
            this.savePredioFusionDivision(pp, nuevo, "FUSION");
            pp.setEstado("H");
            manager.persist(pp);
        }
        if (withEvent) {
            FusionPrediosPost even = new FusionPrediosPost();
            even.setCodPredioFinal(claveResultante);
            even.setCodPrediosFusion(codigosFusionados);
            event.fire(even);
        }
        ArrayList<CatPredio> avaluoPredioFusionado = new ArrayList<>();
        avaluoPredioFusionado.add(nuevo);
        avaluosServices.generateAvaluo(avaluoPredioFusionado,
                Utils.getAnio(new Date()), Utils.getAnio(new Date()), false, user.getName_user());
        return nuevo;
    }

    @Override
    public CatPredio clonarPredio(CatPredio predio, Short codProvincia) {

        CatPredio nuevo = new CatPredio();
        BeanUtils.copyProperties(predio, nuevo);
        nuevo.setId(null);
        nuevo.setNumPredio(this.generarNumPredio());
        nuevo.setNumeroFicha(nuevo.getNumPredio());
        nuevo.setInstCreacion(new Date());
        nuevo.setProvincia(codProvincia);
        nuevo.setPredialant(predio.getClaveCat());
        nuevo.setEstado(EstadosPredio.TEMPORAL);
        nuevo = this.registrarPredio(nuevo);

        CatPredioS4 predioS4 = catas.getPredioS4ByPredio(predio);
        if (predioS4 != null) {
            this.clonarPredioS4(predioS4, nuevo, true);
        }
        CatPredioS6 predioS6 = catas.getPredioS6ByPredio(predio);
        if (predioS6 != null) {
            this.clonarPredioS6(predioS6, nuevo, predio.getCatPredioS6().getCtlgItemCollection(), predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial(), true);
        }
        String[] param = {"idEdificacion"};
        Object[] values = new Object[2];
        Collection<CatPredioEdificacionProp> detallesEdificacion;
        Collection<CatPredioEdificacion> edificacionesActuales = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{predio.getId()});
        CatPredioEdificacion edificacion;
        short numeroEdificacion = (short) 1;
        for (CatPredioEdificacion cpe : edificacionesActuales) {
            edificacion = this.clonarEdificacion(cpe, nuevo, numeroEdificacion++, true);
            values[0] = cpe.getId();
            detallesEdificacion = manager.findAll(Querys.getDetallesEdificacion, param, values);
            String[] param1 = {"idEdificacion", "idProp"};
            Object[] values1 = new Object[2];
            if (detallesEdificacion != null) {
                for (CatPredioEdificacionProp det : detallesEdificacion) {
                    values1[0] = cpe.getId();
                    values1[1] = det.getId();
                    CatEdfProp prop = (CatEdfProp) manager.find(Querys.getProps, param1, values1);
                    if (prop != null) {
                        this.clonarEdificacionProp(det, edificacion, prop, true);
                    }

                }
            }
        }

        Collection<CatPredioPropietario> duenos = predio.getCatPredioPropietarioCollection();
        for (CatPredioPropietario cpp : duenos) {
            this.clonarPropietario(cpp, nuevo);
        }

        this.cambiarPredio(predio, nuevo);

        return nuevo;
    }

    @Override
    public void copiarInfoPredios(CatPredio predio, List<CatPredio> predios) {

    }

    @Override
    public String claveCatastral(CatPredio px) {
        return Utils.completarCadenaConCeros(px.getProvincia().toString(), 2)
                + Utils.completarCadenaConCeros(px.getCanton().toString(), 2)
                + Utils.completarCadenaConCeros(px.getParroquia().toString(), 2)
                + Utils.completarCadenaConCeros(px.getZona().toString(), 2)
                + Utils.completarCadenaConCeros(px.getSector().toString(), 2)
                + Utils.completarCadenaConCeros(px.getMz().toString(), 3)
                + Utils.completarCadenaConCeros(px.getSolar().toString(), 3)
                + Utils.completarCadenaConCeros(px.getBloque().toString(), 3)
                + Utils.completarCadenaConCeros(px.getPiso().toString(), 2)
                + Utils.completarCadenaConCeros(px.getUnidad().toString(), 3);
    }

    @Override
    public BigInteger generarNumPredio() {
        try {

            Object sequence = manager.find(Querys.getMaxCatPredio);
            BigInteger l;
            if (sequence == null) {
                return new BigInteger("1");
            } else {
                l = (BigInteger) sequence;
                l = l.add(new BigInteger("1"));
                return l;
            }

        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public CatPredio clonePredioWithOutId(CatPredio predioRaiz, CatPredio resultante) {
        try {
            Hibernate.initialize(predioRaiz);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = new Gson();

            builder.excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .serializeNulls().setPrettyPrinting();
            gson = builder.create();

            BeanUtils.copyProperties(predioRaiz, resultante);
            //String predioResultJson = gson.toJson(resultante);

            return resultante;

        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "clonePredioWithOutId", e);
        }
        return null;
    }

    @Override
    public CatPredio saveAllDataPredio(CatPredio predio) {
        CatPredio pred = null;
        try {
            try {
                predio.setClaveCat(claveCatastral(predio));
            } catch (Exception e) {
            }
            pred = (CatPredio) manager.persist(predio);
            CatPredioS4 catPredioS4 = null;
            CatPredioS6 catPredioS6 = null;
            Collection<CtlgItem> xvias;
            Collection<CtlgItem> xinstalaciones;
            List<CtlgItem> vias = new ArrayList<>(), instalacionesEspeciales = new ArrayList<>();
            if (predio.getCatPredioS4() != null) {
                catPredioS4 = new CatPredioS4();
                BeanUtils.copyProperties(predio.getCatPredioS4(), catPredioS4);
                catPredioS4.setId(null);
                catPredioS4.setPredio(pred);
                manager.persist(catPredioS4);
            }
            if (predio.getCatPredioS6() != null) {
                catPredioS6 = new CatPredioS6();
                BeanUtils.copyProperties(predio.getCatPredioS6(), catPredioS6);
                catPredioS6.setId(null);
                catPredioS6.setPredio(pred);
//                if (predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial() != null) {
//                    //System.out.println("es nulo? " + predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial().toString());
//
//                    //catPredioS6.setCtlgItemCollectionInstalacionEspecial(predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial());
//                }
//                if (predio.getCatPredioS6().getCtlgItemCollection() != null) {
//                    catPredioS6.setCtlgItemCollection(predio.getCatPredioS6().getCtlgItemCollection());
//                    //System.out.println("es nulo? item " + predio.getCatPredioS6().getCtlgItemCollection().toString());
//                }
                xvias = predio.getCatPredioS6().getCtlgItemCollection();
                xinstalaciones = predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial();
                if (xvias != null && !xvias.isEmpty()) {
                    for (CtlgItem v : xvias) {
                        vias.add(v);
                    }
                } else {
                    catPredioS6.setCtlgItemCollection(vias);
                }
                if (xinstalaciones != null && !xinstalaciones.isEmpty()) {
                    for (CtlgItem v : xinstalaciones) {
                        instalacionesEspeciales.add(v);
                    }
                } else {
                    catPredioS6.setCtlgItemCollectionInstalacionEspecial(instalacionesEspeciales);
                }
                manager.persist(catPredioS6);
            }
            if (predio.getCatPredioPropietarioCollection() != null) {
                CatPredioPropietario cpp;
                for (CatPredioPropietario cppTemp : predio.getCatPredioPropietarioCollection()) {
                    cpp = new CatPredioPropietario();
                    BeanUtils.copyProperties(cppTemp, cpp);
                    cpp.setId(null);
                    cpp.setPredio(pred);
                    manager.persist(cpp);
                }
            }
            if (predio.getCatEscrituraCollection() != null) {
                CatEscritura ce;
                for (CatEscritura ceTemp : predio.getCatEscrituraCollection()) {
                    ce = new CatEscritura();
                    BeanUtils.copyProperties(ceTemp, ce);
                    ce.setIdEscritura(null);
                    ce.setPredio(pred);
                    manager.persist(ce);
                    //System.out.println("es nulo? item " + predio.getCatPredioS6().getCtlgItemCollection().toString());
                }
            }
            return pred;
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "saveAllDataPredio", e);
        }
        return pred;
    }

    @Override
    public void saveEdificacionesPredio(CatPredio predioRaiz, CatPredio predioResultante) {
        try {
            String[] param1 = {"idEdificacion", "idProp"};
            String[] param = {"idEdificacion"};
            Collection<CatPredioEdificacion> edificacionesActuales = catas.getEdificaciones(predioRaiz);
            Collection<CatPredioEdificacionProp> detallesEdificacion;
            CatPredioEdificacion edificacion;
            Object[] values = new Object[2];
            Object[] values1 = new Object[2];
            short numeroEdificacion = (short) 1;
            if (edificacionesActuales != null && !edificacionesActuales.isEmpty()) {
                for (CatPredioEdificacion cpe : edificacionesActuales) {
                    edificacion = this.clonarEdificacion(cpe, predioResultante, numeroEdificacion++, true);
                    //values[0] = predioRaiz.getId();
                    values[0] = cpe.getId();
                    values1[0] = cpe.getId();
                    detallesEdificacion = manager.findAllEntCopy(Querys.getDetallesEdificacion, param, values);
                    if (detallesEdificacion != null && !detallesEdificacion.isEmpty()) {
                        for (CatPredioEdificacionProp det : detallesEdificacion) {
                            values1[1] = det.getId();
                            CatEdfProp prop = (CatEdfProp) manager.find(Querys.getProps, param1, values1);
                            this.clonarEdificacionProp(det, edificacion, prop, true);
                            // this.clonarEdificacionProp(det, edificacion, true);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "saveEdificacionesPredio", e);
        }

    }

    @Override
    public void saveFotosPredio(CatPredio predioRaiz, CatPredio predioResultante) {
        try {
            List<FotoPredio> fotos;
            fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predioRaiz.getId().longValue()});
            if (fotos != null) {
                for (FotoPredio fp : fotos) {
                    fotoBean.setNombre(fp.getNombre());
                    fotoBean.setPredioId(predioResultante.getNumPredio().longValue());
                    fotoBean.setIdPredio(predioResultante.getId());
                    fotoBean.setContentType(fp.getContentType());
                    fotoBean.setFileId(fp.getFileOid());
                    fotoBean.saveFoto();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "saveFotosPredio", e);
        }

    }

    @Override
    public List<CatPredio> dividirPredios(Integer numeroDivisiones, CatPredio predio) {
        try {
            List<CatPredio> predios = new ArrayList<>();
            Integer ultimo;
            Boolean esPH = predio.getBloque() > 0;
            if (esPH) {
                ultimo = catas.getUnidadMaxPredio(predio);
            } else {
                ultimo = catas.getSolarMaxPredio(predio);
                System.out.println("Clave ");
            }
            ultimo++;
            for (int i = 0; i < numeroDivisiones; i++) {
                CatPredio temp = new CatPredio();
                try {
                    EntityBeanCopy.cloneClass(predio, temp);
//                    BeanUtils.copyProperties(predio, temp, new String[]{"id", "numPredio", "predio"});
                    temp.setId(null);
                    temp.setInstCreacion(new Date());
                    temp.setUsuarioCreador(new AclUser(user.getUserId()));
                    temp.setNumPredio(null);
                    temp.setNumeroFicha(null);
                    temp.setPredioRaiz(new BigInteger(predio.getId().toString()));
                    if (esPH) {
                        temp.setUnidad(ultimo.shortValue());
                    } else {
                        temp.setSolar(ultimo.shortValue());
                    }
                    temp.setClaveCat(this.claveCatastral(temp));
                    if (predio.getCatPredioS4() != null) {
                        CatPredioS4 s4 = new CatPredioS4();
                        BeanUtils.copyProperties(catas.getPredioS4ByPredio(predio), s4, new String[]{"id", "predio"});
                        predio.setCatPredioS4(s4);
                    }
                    if (predio.getCatPredioS6() != null) {
                        CatPredioS6 s6 = new CatPredioS6();
                        BeanUtils.copyProperties(catas.getPredioS6ByPredio(predio), s6, new String[]{"id", "predio"});
                        predio.setCatPredioS6(s6);
                    }
                    if (predio.getCatPredioClasificRuralCollection() != null) {
                        List<CatPredioClasificRural> s6 = new ArrayList<>();
                        BeanUtils.copyProperties(predio.getCatPredioClasificRuralCollection(), s6, new String[]{"id", "predio"});
                        predio.setCatPredioClasificRuralCollection(s6);
                    }
                    predios.add(temp);
                    ultimo++;
                } catch (BeansException be) {
                    Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", be);
                }

            }
            return predios;
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
        return null;
    }

    @Override
    public List<CatPredio> registrarPredioGenerados(List<CatPredio> predios) {
        try {
            List<CatPredio> pds = new ArrayList<>();
            for (CatPredio predio : predios) {
                predio = catas.generarNumPredio(predio);
                if (predio != null) {
                    CatPredioS4 catPredioS4 = predio.getCatPredioS4();
                    if (catPredioS4 != null) {
                        catPredioS4.setId(null);
                        catPredioS4.setPredio(predio);
                        catPredioS4 = (CatPredioS4) manager.persist(catPredioS4);
                    }
                    CatPredioS6 catPredioS6 = predio.getCatPredioS6();
                    if (catPredioS6 != null) {
                        catPredioS6.setId(null);
                        catPredioS6.setPredio(predio);
                        catPredioS6 = (CatPredioS6) manager.persist(catPredioS6);
                    }
                    List<CatPredioClasificRural> s6 = predio.getCatPredioClasificRuralCollection();
                    if (s6 != null) {
                        for (CatPredioClasificRural cr : s6) {
                            cr.setId(null);
                            cr.setPredio(predio);
                            cr = (CatPredioClasificRural) manager.persist(cr);
                        }
                    }
                    predio.setEstado(EstadosPredio.PENDIENTE);
                    pds.add(predio);
                } else {
                    return null;
                }
            }
            return pds;
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
        return null;
    }

    @Override
    public void actualizarEstadoPredioTramite(Long idTramite, CatPredio predio, Boolean estado) {
        try {
            Map<String, Object> pm = new HashMap<>();
            pm.put("tramite", new HistoricoTramites(idTramite));
            pm.put("predio", predio);
            HistoricoTramiteDet det = manager.findObjectByParameter(HistoricoTramiteDet.class, pm);
            if (det != null) {
                det.setEstado(estado);
                manager.persist(det);
            }
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
    }

    @Override
    public void actualizarEstadoPredioTramite(Long idTramite, CatPredio predio, Integer estado, TipoProceso tipo) {
        try {
            if (estado == null) {
                estado = 2;
            }
            Map<String, Object> pm = new HashMap<>();
            pm.put("tramite", new HistoricoTramites(idTramite));
            pm.put("predio", predio);
            HistoricoTramiteDet det = manager.findObjectByParameter(HistoricoTramiteDet.class, pm);
            if (det != null) {
                switch (tipo) {
                    case DIVIDIR_PREDIO: {
                        det.setEstado(estado == 1);
                        break;
                    }
                    default: {

                    }
                }

                det.setNumTasa(BigInteger.valueOf(estado));
                manager.persist(det);
            }
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
    }

    @Override
    public void actualizarDetalleTramite(Long idTramite, CatPredio predio, boolean estado, BigInteger numero, TipoProceso tipo) {
        try {
            Map<String, Object> pm = new HashMap<>();
            pm.put("tramite", new HistoricoTramites(idTramite));
            pm.put("predio", predio);
            HistoricoTramiteDet det = manager.findObjectByParameter(HistoricoTramiteDet.class, pm);

            if (det != null) {
                switch (tipo) {
                    case DIVIDIR_PREDIO: {
                        det.setEstado(estado);
                        break;
                    }
                    default: {
                        det.setEstado(estado);
                    }
                }

                det.setNumTasa(numero);
                System.out.println("Detalle estado: " + det.getEstado());
                System.out.println("Detalle num: " + det.getNumTasa().toString());
                manager.persist(det);
            } else {
                System.out.println("Detalle null: ");
            }

        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
    }

    @Override
    public CatastroServices getCatas() {
        return catas;
    }

    @Override
    public List<CatPredioPropietario> getPredioPropietarios(CatPredio predio) {
        try {
            Map<String, Object> pm = new HashMap<>();
            pm.put("estado", "A");
            pm.put("predio", predio);
            return manager.findObjectByParameterList(CatPredioPropietario.class, pm);
        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
        return null;
    }

    @Override
    public void registrarPredios(List<CatPredio> prediosGenerados, CatPredio predioMatriz) {
        try {
//            JsonUtils js = new JsonUtils();
//            CatPredioS4 ms4 = js.jsonToObject(js.generarJson(predioMatriz.getCatPredioS4()),
//                    CatPredioS4.class);
//            CatPredioS6 ms6 = js.jsonToObject(js.generarJson(predioMatriz.getCatPredioS6()),
//                    CatPredioS6.class);
//            CatPredioPropietario[] pps = js.jsonToObject(js.generarJson(predioMatriz.getCatPredioPropietarioCollection()),
//                    CatPredioPropietario[].class);
//            manager.persist(predioMatriz);
//            for (CatPredio prediosGenerado : prediosGenerados) {
//                if (ms4 != null) {
//                    CatPredioS4 s4Temp = catas.getPredioS4ByPredio(prediosGenerado);
//                    if (s4Temp == null) {
//                        CatPredioS4 s4 = ms4;
//                        s4.setId(null);
//                        s4.setPredio(prediosGenerado);
//                        s4 = (CatPredioS4) manager.persist(s4);
//                        prediosGenerado.setCatPredioS4(s4);
//                    }
//                }
//                if (ms6 != null) {
//                    CatPredioS6 s6Temp = catas.getPredioS6ByPredio(prediosGenerado);
//                    if (s6Temp == null) {
//                        CatPredioS6 s6 = ms6;
//                        s6.setId(null);
//                        s6.setPredio(prediosGenerado);
//                        s6 = (CatPredioS6) manager.persist(s6);
//                        prediosGenerado.setCatPredioS6(s6);
//                    }
//                }
//                if (pps != null) {
//                    for (CatPredioPropietario pp : pps) {
//                        Map<String, Object> paramt = new HashMap<>();
//                        paramt.put("predio", prediosGenerado);
//                        paramt.put("ciuCedRuc", pp.getCiuCedRuc());
//                        CatPredioPropietario ppTemp = manager.findObjectByParameter(CatPredioPropietario.class, paramt);
//                        if (ppTemp == null) {
//                            pp.setId(null);
//                            pp.setPredio(prediosGenerado);
//                            pp = (CatPredioPropietario) manager.persist(pp);
//                        }
//                    }
//                }
//                manager.persist(prediosGenerado);
//                eventValoracion.fire(new ValorarPredioPost(prediosGenerado.getClaveCat(), prediosGenerado.getPredialant(), 1));
//            }

            try {
                for (CatPredio prediosGenerado : prediosGenerados) {

                    manager.persist(prediosGenerado);
                    // eventValoracion.fire(new ValorarPredioPost(prediosGenerado.getClaveCat(), prediosGenerado.getPredialant(), 1));
                }
            } catch (Exception e) {
                Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
            }

        } catch (Exception e) {
            Logger.getLogger(CatastroEjb.class.getName()).log(Level.SEVERE, "Generar divissiones", e);
        }
    }

    @Override
    public AvalValorSuelo getValorBaseM2Avaluo(CatPredio s1, Short solar) {
        AvalValorSuelo valorSuelo = null;
        try {
            Integer anioInicio = null, anioFin = null;
            anioFin = (Integer) manager.find(Querys.getAnioFinMax);
            anioInicio = (Integer) manager.find(Querys.getAnioInicioMax);
            if (anioFin != null && anioInicio != null) {

                if (anioInicio >= Utils.getAnio(new Date()) && anioInicio <= Utils.getAnio(new Date())) {

                    valorSuelo = (AvalValorSuelo) manager.find(Querys.getValorM2DivisionFusion,
                            new String[]{"zonap", "sectorp", "mzp", "solarp", "parroquia", "anioInicio", "anioFin"},
                            new Object[]{s1.getZona(), s1.getSector(), s1.getMz(), s1.getSolar(), s1.getParroquia(), anioInicio, anioFin});
                    if (valorSuelo != null) {
                        saveValorM2Division(valorSuelo, solar);
                    }
                    return valorSuelo;
                }
            }

        } catch (Exception e) {
            Logger.getLogger(FusionDivisionEjb.class.getName()).log(Level.SEVERE, "getValorBaseM2Avaluo", e);
        }
        return valorSuelo;
    }

    @Override
    public AvalValorSuelo saveValorM2Division(AvalValorSuelo suelo, Short solar) {
        Hibernate.initialize(suelo);
        AvalValorSuelo sueloClon = new AvalValorSuelo();
        try {
            if (suelo != null) {
                System.out.println("valor" + suelo.getValorM2());
                if (suelo.getId() != null) {
                    BeanUtils.copyProperties(suelo, sueloClon);
                    sueloClon.setId(null);
                    sueloClon.setSolar(solar);
                    avaluosServices.saveAvaluoCategoriaValorM2(sueloClon);
                }

            }
        } catch (Exception e) {
        }

        return sueloClon;
    }
}
