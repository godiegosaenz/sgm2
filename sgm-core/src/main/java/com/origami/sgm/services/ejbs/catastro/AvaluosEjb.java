/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysAvaluos;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.AvalBandaImpositiva;
import com.origami.sgm.entities.AvalValorSuelo;
import com.origami.sgm.entities.AvalCoeficientesSuelo;
import com.origami.sgm.entities.AvalDetCobroImpuestoPredios;
import com.origami.sgm.entities.AvalEdadZonaConst;
import com.origami.sgm.entities.AvalImpuestoPredios;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.historic.ComparativoEmision;
import com.origami.sgm.entities.historic.NovedadValoracionPredial;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.predio.models.AvaluosModel;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.models.avaluos.DatosAvaluos;
import com.origami.sgm.services.interfaces.models.avaluos.DatosBaseCalculo;
import com.origami.sgm.services.interfaces.models.avaluos.DatosNovedad;
import com.origami.sgm.services.interfaces.models.avaluos.DatosNumericos;
import com.origami.sgm.services.interfaces.models.avaluos.ResultadoAvaluos;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.math3.stat.StatUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton
@Lock(LockType.READ)
@Interceptors(value = {HibernateEjbInterceptor.class})
public class AvaluosEjb implements AvaluosServices {

    private static final Logger logx = Logger.getLogger(AvaluosEjb.class.getName());
    @Inject
    private Entitymanager manager;
    @Inject
    private SeqGenMan sec;
    @Inject
    private RecaudacionesService recaudacion;

    //<editor-fold defaultstate="collapsed" desc="AVLUOS SAMBORONDOM">
    @Override
    @Asynchronous
    @AccessTimeout(-1)
    public Future<ResultadoAvaluos> getEmisionGeneral(String user, Integer periodo, Integer version) {
        ResultadoAvaluos datos = null;
        ValoracionPredial vp = null;
        List<DatosAvaluos> lav;
        List<DatosNovedad> lnov;
        List<ValoracionPredial> lvp;
        Query q;
        try {
            Session sess = HiberUtil.getSession();
            q = sess.createSQLQuery(QuerysAvaluos.getPrediosBase).setResultTransformer(Transformers.aliasToBean(DatosBaseCalculo.class));
            List<DatosBaseCalculo> lbc = q.list();
            if (lbc != null) {
                lav = new ArrayList<>();
                lnov = new ArrayList<>();
                lvp = new ArrayList<>();
                for (DatosBaseCalculo b : lbc) {
                    if (b.getAreasolar() != null && b.getAreasolar().compareTo(BigDecimal.ZERO) <= 0) {
                        this.registrarNovedad(b.getNumpredio(), version, periodo, "Sin area de solar");
                        continue;
                    }
                    if (b.getFrente1() != null && b.getFrente1().compareTo(BigDecimal.ZERO) <= 0) {
                        this.registrarNovedad(b.getNumpredio(), version, periodo, "Sin frente");
                        continue;
                    }//alicuota, subsector, area solar, frente, prototivo
                    vp = new ValoracionPredial();
                    vp.setPeriodo(periodo);
                    vp.setFecCre(new Date());
                    vp.setUsrCre(user);
                    vp.setNumVersion(version);
                    vp.setNumPredio(b.getNumpredio().longValue());
                    vp.setClaveMz(b.getClavecat());
                    vp.setAreaSolar(b.getAreasolar());
                    vp.setFrenteSolar(b.getFrente1());
                    vp.setCantServicios(b.getServicios());
                    vp.setSubsector(b.getSubsector().intValue());
                    vp.setAreaConstruccion(b.getAreaconstruccion());
                    vp.setValorM2Subsector(b.getValorm2());
                    vp.setMatriz(Boolean.FALSE);
                    if (b.getAvaluoedificacion() != null) {
                        vp.setAvaluoEdificacion(b.getAvaluoedificacion().setScale(2, RoundingMode.HALF_UP));
                    } else {
                        vp.setAvaluoEdificacion(BigDecimal.ZERO);
                    }
                    vp.setAreaSolarTipo(this.getMediana(QuerysAvaluos.getAreaTipo, new Object[]{b.getClavecat()}));
                    //vp.setFrenteSolarTipo(this.getValorOperacion(QuerysAvaluos.getTotalFrenteTipo, new Object[]{b.getClavecat()}).get().getValor());
                    vp.setFrenteSolarTipo(this.getMediana(QuerysAvaluos.getTotalFrenteTipo, new Object[]{b.getClavecat()}));
                    if (vp.getFrenteSolarTipo().compareTo(BigDecimal.ZERO) == 0) {
                        return null;
                    }
//                    vp.setAlfa(new BigDecimal(this.getCoeficiente("AB", vp.getAreaSolarTipo(), "${AREA}").get().getFactorResultante()));
//                    vp.setBeta(new BigDecimal(this.getCoeficiente("AB", vp.getAreaSolar(), "${AREA}").get().getFactorResultante()));
                    BigDecimal x = BigDecimal.ONE;
                    if (vp.getFrenteSolar().compareTo(vp.getFrenteSolarTipo()) > 0) {
                        if (vp.getFrenteSolar().compareTo(new BigDecimal("30")) <= 0) {
                            x = new BigDecimal("0.25");
                        } else if ((vp.getFrenteSolar().compareTo(new BigDecimal("30")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("200")) <= 0)) {
                            x = new BigDecimal("0.2585").subtract(vp.getFrenteSolar().multiply(new BigDecimal("0.0003")));
                        } else if ((vp.getFrenteSolar().compareTo(new BigDecimal("200")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("480")) <= 0)) {
                            x = new BigDecimal("0.215").subtract(vp.getFrenteSolar().multiply(new BigDecimal("0.000009")));
                        } else {
                            x = new BigDecimal("0.17");
                        }
                    } else {
                        if (vp.getFrenteSolarTipo().compareTo(new BigDecimal("30")) <= 0) {
                            x = new BigDecimal("0.25");
                        } else if ((vp.getFrenteSolarTipo().compareTo(new BigDecimal("30")) > 0) && (vp.getFrenteSolarTipo().compareTo(new BigDecimal("200")) <= 0)) {
                            x = new BigDecimal("0.2585").subtract(vp.getFrenteSolarTipo().multiply(new BigDecimal("0.0003")));
                        } else if ((vp.getFrenteSolarTipo().compareTo(new BigDecimal("200")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("480")) <= 0)) {
                            x = new BigDecimal("0.215").subtract(vp.getFrenteSolarTipo().multiply(new BigDecimal("0.000009")));
                        } else {
                            x = new BigDecimal("0.17");
                        }
                    }
                    vp.setY(x);
                    Double fx = (Math.pow(vp.getAreaSolarTipo().doubleValue(), vp.getBeta().doubleValue())) / (Math.pow(vp.getAreaSolar().doubleValue(), vp.getAlfa().doubleValue()));
                    if ((fx < 1.2)) {
                        vp.setFactExt(BigDecimal.valueOf(fx).setScale(4, RoundingMode.CEILING));
                    } else {
                        vp.setFactExt(BigDecimal.valueOf(1.2));
                    }
                    Double ff = Math.pow((vp.getFrenteSolar().divide(vp.getFrenteSolarTipo(), RoundingMode.HALF_UP)).setScale(4, RoundingMode.CEILING).doubleValue(), vp.getY().doubleValue());//Math.pow((vp.getFrenteSolar().divide(vp.getFrenteSolarTipo(), RoundingMode.HALF_UP)).doubleValue(), vp.getY().doubleValue());
                    System.out.println("num_predio " + b.getNumpredio() + " - " + ff + " - " + x);
                    vp.setFactFrente(BigDecimal.valueOf(ff).setScale(4, RoundingMode.CEILING));
                    if (vp.getAreaSolar().compareTo(new BigDecimal("3000")) > 0) {
                        vp.setFactGeometrico(new BigDecimal("0.8"));
                    } else {
                        vp.setFactGeometrico(vp.getFactExt().multiply(vp.getFactFrente()));
                    }
                    if (vp.getCantServicios() != null && vp.getCantServicios() > 0) {
                        switch (vp.getCantServicios()) {
                            case 7:
                                vp.setFactServicio(new BigDecimal("1"));
                                break;
                            case 6:
                                vp.setFactServicio(new BigDecimal("0.98"));
                                break;
                            case 5:
                                vp.setFactServicio(new BigDecimal("0.95"));
                                break;
                            case 4:
                                vp.setFactServicio(new BigDecimal("0.90"));
                                break;
                            case 3:
                                vp.setFactServicio(new BigDecimal("0.85"));
                                break;
                            default:
                                vp.setFactServicio(new BigDecimal("0.80"));
                        }
                    }
                    //vp.setFactServicio(new BigDecimal(this.getCoeficiente("CSP", new BigDecimal(vp.getCantServicios()), null).get().getFactorResultante()));
                    BigDecimal kx = vp.getFactGeometrico().multiply(vp.getFactServicio()).setScale(4, RoundingMode.CEILING);
                    if (kx.compareTo(BigDecimal.ZERO) > 0) {
                        if (kx.compareTo(new BigDecimal("1.2")) > 0) {
                            vp.setFactCorrelacion(new BigDecimal("1.2"));
                        } else if (kx.compareTo(new BigDecimal("0.8")) >= 0 && kx.compareTo(new BigDecimal("1.2")) <= 0) {
                            vp.setFactCorrelacion(kx);
                        } else {
                            vp.setFactCorrelacion(new BigDecimal("0.8"));
                        }
                    } else {
                        vp.setFactCorrelacion(new BigDecimal("0.8"));
                    }
                    vp.setAvaluoSolar(vp.getAreaSolar().multiply(vp.getValorM2Subsector().multiply(vp.getFactCorrelacion())).setScale(2, RoundingMode.HALF_UP));
                    vp.setAvaluoMunicipal(vp.getAvaluoSolar().add(vp.getAvaluoEdificacion()).setScale(2, RoundingMode.HALF_UP));
                    vp = (ValoracionPredial) manager.persist(vp);
                    if (vp.getId() != null) {
                        lvp.add(vp);
                    }
                }
                //this.actualizarCalculos(version, periodo, user);
            }
        } catch (HibernateException e) {
            logx.log(Level.SEVERE, e.getCause().getMessage(), e);
        } catch (Exception ex) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new AsyncResult<>(datos);
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<DatosNumericos> getValorOperacion(String query, Object[] parameters) {
        DatosNumericos dn = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    q.setParameter(i, parameters[i]);
                }
                dn = new DatosNumericos(new BigDecimal(q.uniqueResult() + ""));
            }
        } catch (HibernateException e) {
            logx.log(Level.SEVERE, e.getCause().getMessage(), e);
        }
        return new AsyncResult<>(dn);
    }

    public Boolean registrarNovedad(BigInteger numpredio, Integer version, Integer periodo, String novedad) {
        try {
            return manager.persist(new NovedadValoracionPredial(version, periodo, numpredio, novedad)) != null;
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return Boolean.FALSE;
    }

    @Asynchronous
    @AccessTimeout(-1)
    public void actualizarCalculos(Integer version, Integer periodo, String user) {
        try {
            try {
                List<Object> parametros = new ArrayList<>();
                parametros.add(periodo);
                parametros.add(version);
                //manager.executeFunction(""+SchemasConfig.HISTORICO+".emision_general", parametros, Boolean.FALSE);
            } catch (Exception e) {
                System.out.println("Error al ejecutar la funcion SQL --> " + SchemasConfig.HISTORICO + ".emision_general " + e.getMessage());
            }
            List<ValoracionPredial> v = manager.findAll(QuerysAvaluos.getAvaluoPredialGral, new String[]{"periodo", "version"}, new Object[]{periodo, version});
            int x = 0;
            //System.out.println("valor total " + v.size());
            for (ValoracionPredial d : v) {
                d.setUsrCre(user);
                this.registrarLiquidacion(d, d.getUsrCre()).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            logx.log(Level.SEVERE, null, e);
        }
    }

    @Override
    @Asynchronous
    @AccessTimeout(-1)
    public Future<Boolean> registrarLiquidacion(ValoracionPredial v, String user) {
        try {
            if (v == null) {
                logx.log(Level.INFO, "Valoracion predial es Nulo...");
                new AsyncResult<>(Boolean.FALSE);
            }
            if (v.getIdPredio() == null) {
                logx.log(Level.INFO, "Valoracion predial es Nulo...");
                new AsyncResult<>(Boolean.FALSE);
            }
            BigDecimal totalPago = BigDecimal.ZERO;
            RenLiquidacion liq = new RenLiquidacion();
            CatPredio predio = manager.find(CatPredio.class, v.getIdPredio());
            liq.setAnio(v.getPeriodo());
            RenTipoLiquidacion tipo = manager.find(RenTipoLiquidacion.class, 13L);
            liq.setPredio(predio);
            liq.setAreaTotal(v.getAreaSolar());
            liq.setAvaluoSolar(v.getAvaluoSolar());
            liq.setAvaluoConstruccion(v.getAvaluoEdificacion());
            liq.setAvaluoMunicipal(v.getAvaluoMunicipal());
            liq.setTipoLiquidacion(tipo);
            liq.setEstadoCoactiva(1);
            liq.setEstadoLiquidacion(manager.find(RenEstadoLiquidacion.class, 2L));
            liq.setFechaIngreso(new Date());
            liq.setNombreComprador(predio.getNombrePropietarios());
            liq.setNumComprobante(BigInteger.ZERO);
            liq.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(v.getPeriodo(), tipo.getId()));
            liq.setIdLiquidacion(tipo.getPrefijo() + "-" + Utils.completarCadenaConCeros(liq.getNumLiquidacion().toString(), 6));
            liq.setBandaImpositiva(v.getBandaImpositiva());
            liq.setEstaExonerado(Boolean.FALSE);
            liq.setCoactiva(Boolean.FALSE);
            liq.setUsuarioIngreso(v.getUsrCre());
            RenLiquidacion c = (RenLiquidacion) manager.persist(liq);
            if (c != null) {
                v.setLiquidacion(c.getId());

                predio.setAvaluoSolar(v.getAvaluoSolar());
                predio.setAvaluoConstruccion(v.getAvaluoEdificacion());
                predio.setAvaluoMunicipal(v.getAvaluoMunicipal());
                manager.persist(predio);

                if (v.getIpAct() != null) {
                    RenDetLiquidacion dt = new RenDetLiquidacion(v.getIpAct(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 1L})).getId());//PREDIOS URBANOS
                    manager.persist(dt);
                    totalPago = totalPago.add(v.getIpAct());
                }
                if (v.getEmisionAct() != null) {
                    RenDetLiquidacion em = new RenDetLiquidacion(v.getEmisionAct(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 10L})).getId());
                    manager.persist(em);
                    totalPago = totalPago.add(v.getEmisionAct());
                }
                if (v.getRbAct() != null) {
                    RenDetLiquidacion rb = new RenDetLiquidacion(v.getRbAct(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 11L})).getId());
                    manager.persist(rb);
                    totalPago = totalPago.add(v.getRbAct());
                }
                if (v.getTasaMant() != null) {
                    RenDetLiquidacion tm = new RenDetLiquidacion(v.getTasaMant(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 17L})).getId());
                    manager.persist(tm);
                    totalPago = totalPago.add(v.getTasaMant());
                }
                try {
                    if (v.getAreaSolar() != null && v.getAreaSolar().compareTo(new BigDecimal("3000")) <= 0) {
                        if (v.getAreaConstruccion() == null || v.getAreaConstruccion().compareTo(BigDecimal.ZERO) == 0) {
                            if (v.getSneAct() != null && v.getNumeroMatriz() == null && v.getCantServicios() > 4
                                    && (v.getAvaluoEdificacion() == null || v.getAvaluoEdificacion().compareTo(BigDecimal.ZERO) <= 0) && predio.getSector() > 2
                                    && predio.getSector() < 9 && v.getSneAct().compareTo(BigDecimal.ZERO) > 0) {
                                RenDetLiquidacion sne = new RenDetLiquidacion(v.getSneAct(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 9L})).getId());
                                manager.persist(sne);
                                totalPago = totalPago.add(v.getSneAct());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al setear sne_act " + e.getMessage());
                }
                HashMap<String, Object> params = new HashMap();
                params.put("idPredio", v.getIdPredio());
                params.put("anio", v.getPeriodo());
                BigDecimal mejoras = (BigDecimal) recaudacion.grabarMejora(params);
                if (mejoras != null) {
                    v.setRmjAct(mejoras);
                    totalPago = totalPago.add(mejoras);
                }
                manager.persist(v);
                try {
                    c.setTotalPago(totalPago);
                    c.setSaldo(totalPago);
                    manager.persist(c);
                } catch (Exception e) {
                    System.out.println("error al setear el pagoTotal " + e.getMessage());
                }
            }
            //}
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return new AsyncResult<>(Boolean.FALSE);
    }

    @Override
    @Asynchronous
    @AccessTimeout(-1)
    public Future<Boolean> registrarLiquidacion(CatPredio cp, Integer periodo, String user) {
        try {
            Map<String, Object> paramt;
            RenDetLiquidacion detLiquidacion;
            BigDecimal totalPago = BigDecimal.ZERO;
            BigDecimal totalRubro = BigDecimal.ZERO;
            RenLiquidacion liq = new RenLiquidacion();
            CatPredio predio = manager.find(CatPredio.class, cp.getId());
            liq.setAnio(periodo);
            RenTipoLiquidacion tipo = manager.find(RenTipoLiquidacion.class, 13L);
            liq.setPredio(predio);
            liq.setAreaTotal(cp.getAreaSolar());
            liq.setAvaluoSolar(cp.getAvaluoSolar());
            liq.setAvaluoConstruccion(cp.getAvaluoConstruccion());
            liq.setAvaluoMunicipal(cp.getAvaluoMunicipal());
            liq.setTipoLiquidacion(tipo);
            liq.setEstadoCoactiva(1);
            liq.setEstadoLiquidacion(manager.find(RenEstadoLiquidacion.class, 2L));
            liq.setFechaIngreso(new Date());
            liq.setNombreComprador(predio.getNombrePropietarios());
            liq.setNumComprobante(BigInteger.ZERO);
            liq.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(periodo, tipo.getId()));
            liq.setIdLiquidacion(tipo.getPrefijo() + "-" + Utils.completarCadenaConCeros(liq.getNumLiquidacion().toString(), 6));
            //liq.setBandaImpositiva(v.getBandaImpositiva());
            liq.setEstaExonerado(Boolean.FALSE);
            liq.setCoactiva(Boolean.FALSE);
            liq.setUsuarioIngreso(user);
            RenLiquidacion c = (RenLiquidacion) manager.persist(liq);
            if (c != null) {
                //IMPUESTO URBANO
                totalRubro = predio.getAvaluoMunicipal().multiply(new BigDecimal("1")).divide(new BigDecimal("1000"));
                RenDetLiquidacion dt = new RenDetLiquidacion(totalRubro, c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 1L})).getId());//PREDIOS URBANOS
                manager.persist(dt);
                totalPago = totalPago.add(totalRubro);
                //SERVICIOS ADMINISTRATIVOS
                totalRubro = new BigDecimal("5");
                RenDetLiquidacion em = new RenDetLiquidacion(totalRubro, c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 2L})).getId());
                manager.persist(em);
                totalPago = totalPago.add(totalRubro);
                //BOMBEROS
                totalRubro = BigDecimal.ZERO;
                totalRubro = cp.getAvaluoMunicipal().multiply(new BigDecimal("0.15")).divide(new BigDecimal("1000"));
                RenDetLiquidacion bomb = new RenDetLiquidacion(totalRubro, c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 6L})).getId());
                manager.persist(bomb);
                totalPago = totalPago.add(totalRubro);
                //CEM-PARQUES Y PLAZAS
                totalRubro = BigDecimal.ZERO;
                detLiquidacion = new RenDetLiquidacion();
                paramt = new HashMap<>();
                paramt.put("idPredio", predio.getId());
                paramt.put("anio", periodo);
                paramt.put("idDetLiquidacion", 640L);
                System.out.println("paramt" + paramt);
                detLiquidacion = (RenDetLiquidacion) manager.findObjectByParameter(Querys.emisionExistenteBajaCEM, paramt);
                if (detLiquidacion != null) {
                    if (detLiquidacion.getId() != null) {
                        if (detLiquidacion.getValor() != null) {
                            //detalle.setValor();
                            RenDetLiquidacion cemParques = new RenDetLiquidacion(detLiquidacion.getValor(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 6L})).getId());
                            manager.persist(cemParques);
                            totalPago = totalPago.add(detLiquidacion.getValor());
                        }
                    }
                }

                ///CEM RUBROS ALCANTrillado 
                totalRubro = BigDecimal.ZERO;
                detLiquidacion = new RenDetLiquidacion();
                paramt = new HashMap<>();
                paramt.put("idPredio", predio.getId());
                paramt.put("anio", periodo);
                paramt.put("idDetLiquidacion", 641L);
                detLiquidacion = (RenDetLiquidacion) manager.findObjectByParameter(Querys.emisionExistenteBajaCEM, paramt);
                if (detLiquidacion != null) {
                    if (detLiquidacion.getId() != null) {
                        if (detLiquidacion.getValor() != null) {
                            //detalle.setValor();
                            RenDetLiquidacion cemAlcantarillado = new RenDetLiquidacion(detLiquidacion.getValor(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 6L})).getId());
                            manager.persist(cemAlcantarillado);
                            totalPago = totalPago.add(detLiquidacion.getValor());
                        }
                    }
                }
//IMP. A LOS INMUEBLES NO EDIFICADOS
                totalRubro = BigDecimal.ZERO;
                detLiquidacion = new RenDetLiquidacion();
                paramt = new HashMap<>();
                paramt.put("idPredio", predio.getId());
                paramt.put("anio", periodo);
                paramt.put("idDetLiquidacion", 4L);
                detLiquidacion = (RenDetLiquidacion) manager.findObjectByParameter(Querys.emisionExistenteBajaCEM, paramt);
                if (detLiquidacion != null) {
                    if (detLiquidacion.getId() != null) {
                        if (detLiquidacion.getValor() != null) {
                            //detalle.setValor();
                            RenDetLiquidacion sne = new RenDetLiquidacion(detLiquidacion.getValor(), c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 6L})).getId());
                            manager.persist(sne);
                            totalPago = totalPago.add(detLiquidacion.getValor());
                        }
                    }
                }
//
//                try {
//                    if (cp.getAreaSolar() != null && cp.getAreaSolar().compareTo(new BigDecimal("3000")) <= 0) {
//                        if (cp.getAreaDeclaradaConst() == null || cp.getAreaDeclaradaConst().compareTo(BigDecimal.ZERO) == 0) {
//                            //IMP. A LOS INMUEBLES NO EDIFICADOS
//                            totalRubro = BigDecimal.ZERO;
//                            totalRubro = cp.getAvaluoMunicipal().multiply(new BigDecimal("2")).divide(new BigDecimal("1000"));
//                            RenDetLiquidacion rb = new RenDetLiquidacion(totalRubro, c, ((RenRubrosLiquidacion) manager.find(QuerysFinanciero.getRubrosLiquidacionTipoLiqCodRubro, new String[]{"tipo", "rubro"}, new Object[]{tipo.getId(), 3L})).getId());
//                            manager.persist(rb);
//                            totalPago = totalPago.add(totalRubro);
//
//                        }
//                    }
//                } catch (Exception e) {
//                    System.out.println("Error al setear sne_act " + e.getMessage());
//                }

                try {
                    c.setTotalPago(totalPago);
                    c.setSaldo(totalPago);
                    manager.persist(c);
                } catch (Exception e) {
                    System.out.println("error al setear el pagoTotal " + e.getMessage());
                }
            }
            //}
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return new AsyncResult<>(Boolean.FALSE);
    }

    @Override
    public Object getNumVersion() {
        return manager.getNativeQuery("select nextval('" + SchemasConfig.HISTORICO + ".num_version')");
    }

    @Override
    public Future<ValoracionPredial> getEmisionPredial(String user, Integer periodo, BigInteger numPredio, Boolean normal) {
        ResultadoAvaluos datos = null;
        ValoracionPredial h = null;
        Query q;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            CatPredio cp = (CatPredio) manager.find(QuerysAvaluos.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{numPredio});
            if (cp != null) {
                //genera los calculos del predio
                if (cp.getBloque() == 0 && cp.getPiso() == 0 && cp.getUnidad() == 0) {
                    //CALCULO DEL PREDIO NORMAL
                    ValoracionPredial dc = getDatosPredioBase(periodo, numPredio, user);
                    //manager.getNativeQuery("select "+SchemasConfig.HISTORICO+".emision_individual(" + periodo + "," + dc.getNumVersion() + "," + numPredio + ")");
                    if (dc != null) {
                        System.out.println("Version ValoracionPredial: " + dc.getNumVersion() + " Predio " + dc.getNumPredio());
                    } else {
                        dc = new ValoracionPredial();
                        dc.setNumVersion(1);
                        dc.setNumPredio(numPredio.longValue());
                        dc.setNumPredio(cp.getId());
                        System.out.println("Version ValoracionPredial: " + dc);
                    }
                    if (normal) {
                        try {
                            Object x = sess.createSQLQuery("select " + SchemasConfig.HISTORICO + ".emision_individual(:n_anio,:n_codigo,:n_predio);").setParameter("n_anio", periodo).setParameter("n_codigo", Integer.parseInt(dc.getNumVersion() + "")).setParameter("n_predio", dc.getNumPredio()).uniqueResult();
                        } catch (HibernateException | NumberFormatException hibernateException) {
                        }
                    } else {
                        try {
                            Object x = sess.createSQLQuery("select " + SchemasConfig.HISTORICO + ".emision_individual_2(:n_anio,:n_codigo,:n_predio);").setParameter("n_anio", periodo).setParameter("n_codigo", Integer.parseInt(dc.getNumVersion() + "")).setParameter("n_predio", dc.getNumPredio()).uniqueResult();
                        } catch (HibernateException | NumberFormatException hibernateException) {
                        }
                    }
                    HiberUtil.flushAndCommit();
                    if (dc.getId() != null) {
                        sess.refresh(dc);
                    }
                    if (dc.getSneAct() != null && dc.getSneAct().compareTo(BigDecimal.ZERO) > 0) {
                        if ((dc.getAreaSolar() != null && dc.getAreaSolar().compareTo(new BigDecimal("3000")) > 0) && (cp.getSector() > 2 && cp.getSector() < 9) && (cp.getSubsector() != null && cp.getSubsector().getServicios() >= 4)) {
                            dc.setSneAct(BigDecimal.ZERO);
                            manager.persist(dc);
                        }
                    }
                    return new AsyncResult<>(dc);
                } else {
                    //BUSQUEDA DEL PREDIO MATRIZ A PARTIR DEL CODIGO DEL HIJO
                    CatPredio pm = (CatPredio) manager.find(QuerysAvaluos.getPredioCodCatastral, new String[]{"sector", "mz", "cdla", "mzdiv", "solar", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9"},
                            new Object[]{cp.getSector(),
                                cp.getMz(), cp.getCdla(), cp.getMzdiv(), cp.getSolar(), cp.getDiv1(), cp.getDiv2(), cp.getDiv3(), cp.getDiv4(), cp.getDiv5(), cp.getDiv6(), cp.getDiv7(), cp.getDiv8(), cp.getDiv9()});
                    if (pm == null) {
                        BigInteger codMatriz = (BigInteger) manager.getNativeQuery(QuerysAvaluos.getEnlacePh, new Object[]{numPredio});
                        if (codMatriz != null) {
                            pm = (CatPredio) manager.find(QuerysAvaluos.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{codMatriz});
                        }
                    }
                    if (pm != null) {
                        if (cp.getAlicuotaUtil().compareTo(BigDecimal.ZERO) > 0) {
                            ValoracionPredial vpm = getDatosPredioBase(periodo, pm.getNumPredio(), user);
                            if (vpm != null) {
                                vpm.setMatriz(Boolean.TRUE);
                                h = new ValoracionPredial();
                                h.setNumPredio(numPredio.longValue());
                                h.setNumVersion(vpm.getNumVersion());
                                h.setPeriodo(periodo);
                                h.setNumeroMatriz(new BigInteger(vpm.getNumPredio() + ""));
                                h.setAlicuota(cp.getAlicuotaUtil().divide(new BigDecimal("100")));
                                h.setCantServicios(cp.getSubsector().getServicios().intValue());
                                h.setSubsector(cp.getSubsector().getSector().intValue());
                                h.setValorM2Subsector(cp.getSubsector().getValorM2());
                                h.setAreaSolar(pm.getAreaSolar().multiply(cp.getAlicuotaUtil().divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP));
                                h.setAvaluoSolar(vpm.getAvaluoSolar().multiply(cp.getAlicuotaUtil().divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP));
                                if (vpm.getAreaConstruccion() != null) {
                                    h.setAreaConstruccion(vpm.getAreaConstruccion().multiply(cp.getAlicuotaUtil().divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP));
                                }
                                try {
                                    h.setAvaluoEdificacion(vpm.getAvaluoEdificacion().multiply(cp.getAlicuotaUtil().divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP));
                                } catch (Exception e) {
                                }
                                try {
                                    h.setAvaluoMunicipal(h.getAvaluoSolar().add(h.getAvaluoEdificacion()).setScale(2, RoundingMode.HALF_UP));
                                } catch (Exception e) {
                                }
                                h.setIdPredio(cp.getId());
                                h.setMatriz(Boolean.FALSE);
                                h.setClaveMz(cp.getClaveCat());
                                //h.setContribuyente(cp.getNombrePropietarios());
                                h.setFecCre(new Date());
                                h.setUsrCre(user);
                                h.setCodigoCatastral(cp.getCodigoPredialCompletoFormatoG());
                                h = (ValoracionPredial) sess.merge(h);
                                if (h.getId() != null) {
                                    sess.persist(vpm);
                                    sess.flush();
                                    HiberUtil.flushAndCommit();
                                    this.actualizarDatosVersion(h, normal);
                                    sess.refresh(h);
                                    HiberUtil.flushAndCommit();
                                    if (h.getSneAct() != null && h.getSneAct().compareTo(BigDecimal.ZERO) > 0) {
                                        if ((h.getAreaSolar() != null && h.getAreaSolar().compareTo(new BigDecimal("3000")) > 0) && (cp.getSector() > 2 && cp.getSector() < 9) && (cp.getSubsector() != null && cp.getSubsector().getServicios() >= 4)) {
                                            h.setSneAct(BigDecimal.ZERO);
                                            manager.persist(h);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return new AsyncResult<>(h);
    }

    @Override
    public Boolean actualizarDatosVersion(ValoracionPredial h, Boolean normal) {
        try {
            if (h != null && h.getId() != null) {
                HiberUtil.requireTransaction();
                Session sess = HiberUtil.getSession();
                if (normal) {
                    Object x = sess.createSQLQuery("select " + SchemasConfig.HISTORICO + ".emision_individual(:n_anio,:n_codigo,:n_predio);").setParameter("n_anio", h.getPeriodo()).setParameter("n_codigo", Integer.parseInt(h.getNumVersion() + "")).setParameter("n_predio", h.getNumPredio()).uniqueResult();
                } else {
                    Object x = sess.createSQLQuery("select " + SchemasConfig.HISTORICO + ".emision_individual_2(:n_anio,:n_codigo,:n_predio);").setParameter("n_anio", h.getPeriodo()).setParameter("n_codigo", Integer.parseInt(h.getNumVersion() + "")).setParameter("n_predio", h.getNumPredio()).uniqueResult();
                }

                HiberUtil.flushAndCommit();
                return true;
            }
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return false;

    }

    @Override
    public ValoracionPredial getDatosPredioBase(Integer periodo, BigInteger numPredio, String user) {
        ValoracionPredial vp = null;
        try {
            Query q = null;
            Session sess = HiberUtil.getSession();
            Integer version = Integer.parseInt(this.getNumVersion().toString());
            CatPredio cp = (CatPredio) manager.find(QuerysAvaluos.getPredioByNumPredioBase, new String[]{"numPredio"}, new Object[]{numPredio});
            if (cp != null) {
                q = sess.createSQLQuery(QuerysAvaluos.getPredioBase).setResultTransformer(Transformers.aliasToBean(DatosBaseCalculo.class));
                q.setParameter(0, numPredio);
                DatosBaseCalculo b = (DatosBaseCalculo) q.uniqueResult();
                if (b != null) {
                    if (b.getAreasolar() != null && b.getAreasolar().compareTo(BigDecimal.ZERO) == 0) {
                        this.registrarNovedad(b.getNumpredio(), version, periodo, "Sin area de solar");
                        return null;
                    }
                    if (b.getFrente1() != null && b.getFrente1().compareTo(BigDecimal.ZERO) == 0) {
                        this.registrarNovedad(b.getNumpredio(), version, periodo, "Sin frente");
                        return null;
                    }//alicuota, subsector, area solar, frente, prototivo
                    vp = new ValoracionPredial();
                    vp.setPeriodo(periodo);
                    vp.setFecCre(new Date());
                    vp.setUsrCre(user);
                    vp.setNumVersion(version);
                    vp.setIdPredio(cp.getId());
                    vp.setNumPredio(b.getNumpredio().longValue());
                    vp.setClaveMz(b.getClavecat());
                    vp.setCodigoCatastral(cp.getCodigoPredialCompletoFormatoG());
                    vp.setAreaSolar(b.getAreasolar());
                    vp.setFrenteSolar(b.getFrente1());
                    vp.setCantServicios(b.getServicios());
                    vp.setSubsector(b.getSubsector().intValue());
                    vp.setAreaConstruccion(b.getAreaconstruccion());
                    vp.setValorM2Subsector(b.getValorm2());
                    vp.setMatriz(Boolean.FALSE);
                    if (b.getAvaluoedificacion() != null) {
                        vp.setAvaluoEdificacion(b.getAvaluoedificacion());
                    } else {
                        vp.setAvaluoEdificacion(BigDecimal.ZERO);
                    }
                    vp.setAreaSolarTipo(this.getMediana(QuerysAvaluos.getAreaTipo, new Object[]{b.getClavecat()}).setScale(2, RoundingMode.HALF_UP));
                    vp.setFrenteSolarTipo(this.getMediana(QuerysAvaluos.getTotalFrenteTipo, new Object[]{b.getClavecat()}).setScale(2, RoundingMode.HALF_UP));
                    if (vp.getFrenteSolarTipo().compareTo(BigDecimal.ZERO) == 0) {
                        return null;
                    }
//                    vp.setAlfa(new BigDecimal(this.getCoeficiente("AB", vp.getAreaSolarTipo(), "${AREA}").get().getFactorResultante()).setScale(2, RoundingMode.HALF_UP));
                    //vp.setBeta(new BigDecimal(this.getCoeficiente("AB", vp.getAreaSolar(), "${AREA}").get().getFactorResultante()).setScale(2, RoundingMode.HALF_UP));
                    BigDecimal x = BigDecimal.ONE;
                    if (vp.getFrenteSolar().compareTo(vp.getFrenteSolarTipo()) > 0) {
                        if (vp.getFrenteSolar().compareTo(new BigDecimal("30")) <= 0) {
                            x = new BigDecimal("0.25");
                        } else if ((vp.getFrenteSolar().compareTo(new BigDecimal("30")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("200")) <= 0)) {
                            x = new BigDecimal("0.2585").subtract(vp.getFrenteSolar().multiply(new BigDecimal("0.0003")));
                        } else if ((vp.getFrenteSolar().compareTo(new BigDecimal("200")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("480")) <= 0)) {
                            x = new BigDecimal("0.215").subtract(vp.getFrenteSolar().multiply(new BigDecimal("0.000009")));
                        } else {
                            x = new BigDecimal("0.17");
                        }
                    } else {
                        if (vp.getFrenteSolarTipo().compareTo(new BigDecimal("30")) <= 0) {
                            x = new BigDecimal("0.25");
                        } else if ((vp.getFrenteSolarTipo().compareTo(new BigDecimal("30")) > 0) && (vp.getFrenteSolarTipo().compareTo(new BigDecimal("200")) <= 0)) {
                            x = new BigDecimal("0.2585").subtract(vp.getFrenteSolarTipo().multiply(new BigDecimal("0.0003")));
                        } else if ((vp.getFrenteSolarTipo().compareTo(new BigDecimal("200")) > 0) && (vp.getFrenteSolar().compareTo(new BigDecimal("480")) <= 0)) {
                            x = new BigDecimal("0.215").subtract(vp.getFrenteSolarTipo().multiply(new BigDecimal("0.000009")));
                        } else {
                            x = new BigDecimal("0.17");
                        }
                    }
                    vp.setY(x);
                    Double fx = (Math.pow(vp.getAreaSolarTipo().doubleValue(), vp.getBeta().doubleValue())) / (Math.pow(vp.getAreaSolar().doubleValue(), vp.getAlfa().doubleValue()));
                    if ((fx < 1.2)) {
                        vp.setFactExt(BigDecimal.valueOf(fx).setScale(2, RoundingMode.HALF_UP));
                    } else {
                        vp.setFactExt(BigDecimal.valueOf(1.2));
                    }
                    Double ff = Math.pow((vp.getFrenteSolar().divide(vp.getFrenteSolarTipo(), RoundingMode.HALF_UP)).doubleValue(), vp.getY().doubleValue());
                    vp.setFactFrente(BigDecimal.valueOf(ff));
                    if (vp.getAreaSolar().compareTo(new BigDecimal("3000")) > 0) {
                        vp.setFactGeometrico(new BigDecimal("0.8"));
                    } else {
                        vp.setFactGeometrico(vp.getFactExt().multiply(vp.getFactFrente()).setScale(2, RoundingMode.HALF_UP));
                    }
//                    System.out.println("num_predio " + b.getNumpredio() + " - " + b.getAreasolar() + " - " + b.getFrente1() + " - " + b.getServicios() + " - " + b.getSubsector() + " - " + b.getValorm2() + " - "
//                            + b.getAreaconstruccion() + " - " + b.getAvaluoedificacion() + " - " + vp.getAlfa() + " - " + vp.getBeta() + " - " + vp.getY() + vp.getFactGeometrico() + " - " + vp.getFactFrente() + " - " + vp.getFactExt());
                    if (vp.getCantServicios() != null && vp.getCantServicios() > 0) {
                        switch (vp.getCantServicios()) {
                            case 7:
                                vp.setFactServicio(new BigDecimal("1"));
                                break;
                            case 6:
                                vp.setFactServicio(new BigDecimal("0.98"));
                                break;
                            case 5:
                                vp.setFactServicio(new BigDecimal("0.95"));
                                break;
                            case 4:
                                vp.setFactServicio(new BigDecimal("0.90"));
                                break;
                            case 3:
                                vp.setFactServicio(new BigDecimal("0.85"));
                                break;
                            default:
                                vp.setFactServicio(new BigDecimal("0.80"));
                        }
                    }
                    BigDecimal kx = vp.getFactGeometrico().multiply(vp.getFactServicio()).setScale(2, RoundingMode.CEILING);
                    if (kx.compareTo(BigDecimal.ZERO) > 0) {
                        if (kx.compareTo(new BigDecimal("1.2")) > 0) {
                            vp.setFactCorrelacion(new BigDecimal("1.2"));
                        } else if (kx.compareTo(new BigDecimal("0.8")) >= 0 && kx.compareTo(new BigDecimal("1.2")) <= 0) {
                            vp.setFactCorrelacion(kx.setScale(2, RoundingMode.HALF_UP));
                        } else {
                            vp.setFactCorrelacion(new BigDecimal("0.8"));
                        }
                    } else {
                        vp.setFactCorrelacion(new BigDecimal("0.8"));
                    }
                    vp.setAvaluoSolar(vp.getAreaSolar().multiply(vp.getValorM2Subsector().multiply(vp.getFactCorrelacion())).setScale(2, RoundingMode.HALF_UP));
                    vp.setAvaluoMunicipal(vp.getAvaluoSolar().add(vp.getAvaluoEdificacion()).setScale(2, RoundingMode.HALF_UP));
                    vp = (ValoracionPredial) manager.persist(vp);
                }
            }
        } catch (HibernateException e) {
            logx.log(Level.SEVERE, null, e);

        } catch (Exception ex) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Error ex) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vp;
    }

    public BigDecimal getMediana(String query, Object[] parameters) {
        BigDecimal c = null;
        try {
            Session sess = HiberUtil.getSession();
            Query q = sess.createSQLQuery(query);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    q.setParameter(i, parameters[i]);
                }
                List<BigDecimal> lbd = q.list();
                double[] x = new double[q.list().size()];
                for (int i = 0; i < lbd.size(); i++) {
                    x[i] = lbd.get(i).doubleValue();
                }
                c = BigDecimal.valueOf(StatUtils.percentile(x, 50));
            }
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return c;
    }

    public Boolean actualizarDatosValoracion(Integer periodo, Integer version, BigInteger numPredio) {
        try {
            return manager.getNativeQuery("select " + SchemasConfig.HISTORICO + ".emision_individual(" + periodo + "," + version + "," + numPredio + ")") != null;
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public List<ValoracionPredial> prediosRecalculados() {
        List<ValoracionPredial> lp = new ArrayList<>();
        ValoracionPredial vp = null;
        try {
            List<Object[]> p = manager.getSqlQuery(QuerysAvaluos.getLiquidacionesxRecalcular);
            for (Object[] o : p) {
                RenLiquidacion rl = manager.find(RenLiquidacion.class, Long.parseLong(o[1] + ""));
                if (rl != null) {
                    vp = this.getEmisionPredial("admin", 2017, new BigInteger(o[0].toString()), true).get();
                    if (vp != null) {
                        RenDetLiquidacion ip = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 2L});
                        RenDetLiquidacion tm = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 636L});
                        //System.out.println("Version " + vp.getNumVersion() + "  - Predio " + vp.getNumPredio() + " :::  IpAnt " + ip.getValor() + " -- " + vp.getIpAct() + " " + tm.getValor() + " -- " + vp.getTasaMant());
                        ip.setValor(vp.getIpAct());
                        tm.setValor(vp.getTasaMant());
                        vp.setLiquidacion(rl.getId());
                        ip.setValor(vp.getIpAct());
                        tm.setValor(vp.getTasaMant());
                        manager.persist(ip);
                        manager.persist(tm);
                        manager.persist(vp);
                        rl.setBandaImpositiva(vp.getBandaImpositiva());
                        rl.setAvaluoMunicipal(vp.getAvaluoMunicipal());
                        rl.setAvaluoConstruccion(vp.getAvaluoEdificacion());
                        rl.setAvaluoSolar(vp.getAvaluoSolar());
                        manager.persist(rl);
                        lp.add(vp);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return lp;
    }

    @Override
    public ValoracionPredial recalcular(String usuario, Long liquidacion, Boolean normal) {
        ValoracionPredial x = null;
        BigDecimal total = BigDecimal.ZERO;
        try {
            RenLiquidacion rl = manager.find(RenLiquidacion.class, liquidacion);
            if (rl != null && rl.getTipoLiquidacion().getId().equals(13L) && rl.getEstadoLiquidacion().getId().equals(2L) && rl.getAnio().equals(2017)) {
                if (rl.getEstaExonerado()) {
                    return null;
                }
                FnExoneracionLiquidacion ex = (FnExoneracionLiquidacion) manager.find(QuerysFinanciero.getExoneracionLiquidacion, new String[]{"liquidacion"}, new Object[]{rl});
                if (ex != null) {
                    return null;
                }
                FnSolicitudExoneracion exoneracion = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.buscarExoneracionTerceraEdadYDiscapacitadoPorPredio, new String[]{"predio"}, new Object[]{rl.getPredio()});
                if (exoneracion != null) {
                    if (exoneracion.getEstado().equals(1L)) {
                        return null;
                    }
                }
                if (rl.getSaldo().compareTo(rl.getTotalPago()) == 0) {
                    x = this.getEmisionPredial(usuario, rl.getAnio(), rl.getPredio().getNumPredio(), normal).get();
                    if (x != null) {
                        rl.setAvaluoSolar(x.getAvaluoSolar());
                        rl.setAvaluoConstruccion(x.getAvaluoEdificacion());
                        rl.setAvaluoMunicipal(x.getAvaluoMunicipal());
                        rl.setBandaImpositiva(x.getBandaImpositiva());
                        rl.setAreaTotal(x.getAreaSolar());
                        manager.persist(rl);
                        x.setLiquidacion(rl.getId());
                        manager.persist(x);

                        rl.getPredio().setAvaluoSolar(x.getAvaluoSolar());
                        rl.getPredio().setAvaluoConstruccion(x.getAvaluoEdificacion());
                        rl.getPredio().setAvaluoMunicipal(x.getAvaluoMunicipal());
                        manager.persist(rl.getPredio());

                        RenDetLiquidacion ip = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 2L});
                        ip.setValor(x.getIpAct());
                        manager.persist(ip);
                        total = total.add(ip.getValor());

                        RenDetLiquidacion tm = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 636L});
                        tm.setValor(x.getTasaMant());
                        manager.persist(tm);
                        total = total.add(tm.getValor());

                        RenDetLiquidacion rb = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 12L});
                        rb.setValor(x.getRbAct());
                        manager.persist(rb);
                        total = total.add(rb.getValor());

                        RenDetLiquidacion rm = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 7L});
                        if (rm != null) {
                            x.setRmjAct(rm.getValor());
                            total = total.add(rm.getValor());
                        }
                        RenDetLiquidacion sne = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 10L});
                        if (sne != null) {
                            sne.setValor(x.getSneAct());
                            manager.persist(sne);
                            total = total.add(x.getSneAct());
                        } else if (x.getSneAct() != null && x.getSneAct().compareTo(BigDecimal.ZERO) > 0 && (x.getAreaSolar() != null && x.getAreaSolar().compareTo(new BigDecimal("3000")) <= 0)) {
                            System.out.println("area del solar " + x.getAreaSolar());
                            RenDetLiquidacion snen = new RenDetLiquidacion();
                            snen.setLiquidacion(rl);
                            snen.setCantidad(1);
                            snen.setValor(x.getSneAct());
                            snen.setRubro(10L);
                            snen.setDesde(BigInteger.ZERO);
                            snen.setHasta(BigInteger.ZERO);
                            snen.setValorRecaudado(BigDecimal.ZERO);
                            manager.persist(snen);
                            total = total.add(x.getSneAct());
                        }
                        RenDetLiquidacion em = (RenDetLiquidacion) manager.find(QuerysFinanciero.getDetalleDeLiquidacionxRubro, new String[]{"liquidacion", "rubro"}, new Object[]{rl, 11L});
                        if (em != null) {
                            em.setValor(x.getEmisionAct());
                            manager.persist(em);
                            total = total.add(x.getEmisionAct());
                        }
                        try {
                            rl.setTotalPago(total);
                            rl.setSaldo(total);
                            manager.persist(rl);
                            manager.persist(x);
                        } catch (Exception e) {
                            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return x;
    }

    @Override
    public Future<List<AvaluosModel>> getLiquidacionesPagadas(Long liquidacion) {
        List<AvaluosModel> liqs = null;
        try {
            Query q = null;
            Session sess = HiberUtil.getSession();
            Integer version = Integer.parseInt(this.getNumVersion().toString());
            q = sess.createSQLQuery(QuerysAvaluos.getLiquidacionesPagadas).setResultTransformer(Transformers.aliasToBean(AvaluosModel.class));
            q.setParameter("liquidacion", liquidacion);
            liqs = q.list();
            for (AvaluosModel d : liqs) {
                ValoracionPredial p = this.getEmisionPredial("admin", version, d.getNumpredio(), Boolean.TRUE).get();
                if (p != null) {
                    ComparativoEmision ce = new ComparativoEmision();
                    ce.setFecCre(new Date());
                    ce.setEstado(Boolean.TRUE);
                    ce.setNumPredio(d.getNumpredio());
                    ce.setIdPredio(d.getIdpredio());
                    ce.setNumVersion(new Integer(p.getNumVersion() + ""));
                    ce.setLiquidacion(d.getLiquidacion());
                    ce.setAreaTotal(d.getAreatotal());
                    ce.setAvaluoSolar(d.getAvaluosolar());
                    ce.setAvaluoConstruccion(d.getAvaluoconstruccion());
                    ce.setAvaluoMunicipal(d.getAvaluomunicipal());
                    ce.setIpLiq(d.getIpliq());
                    ce.setEmisionLiq(d.getEmisionliq());
                    ce.setBomberosLiq(d.getBomberosliq());
                    ce.setMejorasLiq(d.getMejorasliq());
                    ce.setTasaMantLiq(d.getTasamantliq());
                    ce.setSolNedifLiq(d.getSolnedifliq());
                    ce.setDescuentoLiq(d.getDescuentoliq());

                    ce.setAreaCalc(p.getAreaSolar());
                    ce.setAvalSolCalc(p.getAvaluoSolar());
                    ce.setAvalEdifCalc(p.getAvaluoEdificacion());
                    ce.setAvalMunCalc(p.getAvaluoMunicipal());
                    ce.setIpCalc(p.getIpAct());
                    ce.setEmisionCalc(p.getEmisionAct());
                    ce.setBomberosCalc(p.getRbAct());
                    ce.setTasaMantCalc(p.getTasaMant());
                    ce.setSolNedifCalc(p.getSneAct());
                    manager.persist(ce);
                    System.out.println("Predio " + d.getNumpredio() + " - Version " + d.getNumVersion() + " - Liquidacion " + d.getLiquidacion());
                }
            }
        } catch (InterruptedException | NumberFormatException | ExecutionException | HibernateException e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return new AsyncResult<>(liqs);
    }
    //</editor-fold>

    @Override
    public AvalValorSuelo saveAvaluoCategoriaValorM2(AvalValorSuelo avalValorSuelo) {
        AvalValorSuelo acvs = null;
        try {
            if (avalValorSuelo.getId() != null) {
                acvs = (AvalValorSuelo) manager.persist(avalValorSuelo);
            } else {
                acvs = searchAvaluoCategoriaValorM2(avalValorSuelo);
                if (acvs == null) {
                    acvs = (AvalValorSuelo) manager.persist(avalValorSuelo);
                } else {
                    avalValorSuelo.setId(acvs.getId());
                    acvs = (AvalValorSuelo) manager.persist(avalValorSuelo);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "saveAvaluoCategoriaValorM2", e);
        }
        return acvs;
    }

    public AvalValorSuelo searchAvaluoCategoriaValorM2(AvalValorSuelo avalValorSuelo) {
        AvalValorSuelo acvs = null;
        try {
            return acvs = (AvalValorSuelo) manager.find(Querys.getAvalValorSueloByKey,
                    new String[]{"parroquia", "zona", "sector", "mz", "anioInicio", "anioFin", "solar"}, new Object[]{avalValorSuelo.getParroquia(),
                        avalValorSuelo.getZona(), avalValorSuelo.getSector(), avalValorSuelo.getMz(), avalValorSuelo.getAnioInicio(), avalValorSuelo.getAnioFin(), avalValorSuelo.getSolar()});

        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "searchAvaluoCategoriaValorM2", e);
        }
        return acvs;
    }

    public AvalBandaImpositiva saveOrUpdateAvalBandaImpositiva(AvalBandaImpositiva avalBandaImpositiva) {
        AvalBandaImpositiva abiTemp = null;
        try {
            abiTemp = (AvalBandaImpositiva) manager.persist(avalBandaImpositiva);
            return abiTemp;
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "saveAvalBandaImpositiva", e);
        }
        return abiTemp;
    }

    public Boolean saveAvalCoeficientesSuelo(AvalCoeficientesSuelo coefPadre, List<AvalCoeficientesSuelo> coeficientesFactoriales, Boolean control, Integer anioCoeficientesSolarInicio, Integer anioCoeficientesSolarFin, Integer anioCoeficientesConstruccionInicio, Integer anioCoeficientesConstruccionFin) {
        AvalCoeficientesSuelo coef = null;
        Boolean status = Boolean.FALSE;
        try {
            for (AvalCoeficientesSuelo ac : coeficientesFactoriales) {
                if (ac.getValorCoefInferior() == null) {
                    ac.setValorCoefInferior(new BigDecimal("0.00"));
                }
                if (ac.getValorCoefSuperior() == null) {
                    ac.setValorCoefSuperior(new BigDecimal("0.00"));
                }
                if (control == Boolean.FALSE) {
                    coef = (AvalCoeficientesSuelo) manager.find(Querys.getAvalCoeficientesSueloByCategoriaSolar, new String[]{"categoriaSolar", "anioInicio", "anioFin"}, new Object[]{ac.getCategoriaSolar().getId(), anioCoeficientesConstruccionInicio, anioCoeficientesConstruccionFin});
                    ac.setAnioInicio(anioCoeficientesSolarInicio);
                    ac.setAnioFin(anioCoeficientesSolarFin);

                } else {
                    coef = (AvalCoeficientesSuelo) manager.find(Querys.getAvalCoeficientesSueloByCategoriaConstruccion, new String[]{"categoriaConstruccion", "anioInicio", "anioFin"}, new Object[]{ac.getCategoriaConstruccion().getId(), anioCoeficientesConstruccionInicio, anioCoeficientesConstruccionFin});
                    ac.setAnioInicio(anioCoeficientesConstruccionInicio);
                    ac.setAnioFin(anioCoeficientesConstruccionFin);
                }
                if (coef != null) {
                    if (coef.getId() != null) {
                        ac.setId(coef.getId());
                    }
                }
                manager.persist(ac);
            }
            return status = Boolean.TRUE;
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "saveAvalCoeficientesSuelo", e);
        }
        return status;
    }

    @Override
    //para guardar las edades de las zonas del canton  :v 
    public Boolean saveAvalEdadZonaConst(AvalEdadZonaConst avalEdadZonaConst) {
        Boolean status = Boolean.FALSE;
        try {
            AvalEdadZonaConst aezc = (AvalEdadZonaConst) manager.persist(avalEdadZonaConst);
            if (aezc != null) {
                status = Boolean.TRUE;
            } else {
                status = Boolean.FALSE;
            }
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "saveAvalEdadZonaConst", e);
        }
        return status;
    }

    @Override
    public Object generateAvaluo(List<CatPredio> prediosSeleccionados, Integer anioValorizarInicio, Integer anioValorizarFin, Boolean control, String usuario) {
        Object dep = null;
        try {
            if (control == Boolean.FALSE) {
                for (CatPredio cp : prediosSeleccionados) {
                    dep = manager.ejecutarFuncionAvaluosEmisionPredial(SchemasConfig.APP1 + ".avaluar_predio", cp.getId(), anioValorizarInicio, anioValorizarFin, usuario);
                }
            } else {
                dep = manager.ejecutarFuncionAvaluosEmisionPredial(SchemasConfig.APP1 + ".avaluar_predio", 0L, anioValorizarInicio, anioValorizarFin, usuario);
            }
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "generateAvaluo", e);

        }
        return dep;
    }

    @Override
    public Object generateEmisionPredial(List<CatPredio> prediosSeleccionados, Integer anioValorizarInicio, Integer anioValorizarFin, Boolean control, String usuario) {
        Object dep = null;
        try {
            if (control == Boolean.FALSE) {
                for (CatPredio cp : prediosSeleccionados) {
                    dep = manager.ejecutarFuncionAvaluosEmisionPredial(SchemasConfig.APP1 + ".emision_predial_ren_liquidacion", cp.getId(), anioValorizarInicio, anioValorizarFin, usuario);
                }
            } else {
                dep = manager.ejecutarFuncionAvaluosEmisionPredial(SchemasConfig.APP1 + ".emision_predial_ren_liquidacion", 0L, anioValorizarInicio, anioValorizarFin, usuario);
            }
        } catch (Exception e) {
            Logger.getLogger(AvaluosEjb.class.getName()).log(Level.SEVERE, "generateEmisionPredial", e);

        }
        return dep;
    }

    @Override
    public AvalImpuestoPredios saveAvalImpuestoPredios(AvalImpuestoPredios avalImpuestoPredios) {
        AvalImpuestoPredios aip = null;
        try {
            if (avalImpuestoPredios.getId() != null) {
                aip = (AvalImpuestoPredios) manager.persist(avalImpuestoPredios);
            } else {
                aip = (AvalImpuestoPredios) manager.persist(avalImpuestoPredios);
            }

        } catch (Exception e) {
        }
        return aip;
    }

    @Override
    public AvalDetCobroImpuestoPredios saveAvalDetCobroImpuestoPredios(List<AvalDetCobroImpuestoPredios> cobroImpuestoPrediosList) {
        AvalDetCobroImpuestoPredios cobroImpuestoPredios = null;
        try {
            for (AvalDetCobroImpuestoPredios adcip : cobroImpuestoPrediosList) {
                if (adcip.getId() != null) {
                    cobroImpuestoPredios = (AvalDetCobroImpuestoPredios) manager.persist(adcip);
                } else {
                    cobroImpuestoPredios = (AvalDetCobroImpuestoPredios) manager.persist(adcip);
                }
            }
        } catch (Exception e) {
        }
        return cobroImpuestoPredios;
    }

    @Override
    public AvalImpuestoPredios saveAvalImpuestoPrediosAndDetCobro(AvalImpuestoPredios avalImpuestoPredios, List<AvalDetCobroImpuestoPredios> cobroImpuestoPrediosList) {
        AvalImpuestoPredios aip = null;
        try {
            if (avalImpuestoPredios.getId() != null) {
                aip = (AvalImpuestoPredios) manager.persist(avalImpuestoPredios);
                if (aip != null) {
                    for (AvalDetCobroImpuestoPredios adcip : cobroImpuestoPrediosList) {
                        adcip.setIdAvalImpuestoPredio(aip);
                        if (adcip.getId() != null) {
                            manager.persist(adcip);
                        } else {
                            manager.persist(adcip);
                        }
                    }
                } else {
                    return aip;
                }

            } else {
                aip = (AvalImpuestoPredios) manager.persist(avalImpuestoPredios);
                if (aip != null) {
                    for (AvalDetCobroImpuestoPredios adcip : cobroImpuestoPrediosList) {
                        adcip.setIdAvalImpuestoPredio(aip);
                        if (adcip.getId() != null) {
                            manager.persist(adcip);
                        } else {
                            manager.persist(adcip);
                        }
                    }
                } else {
                    return aip;
                }

            }

        } catch (Exception e) {
        }
        return aip;
    }

}
