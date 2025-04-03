/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.DatosTramite;
import com.origami.sgm.bpm.models.DatosTramiteDet;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.restful.models.DatosConsultaPredios;
import com.origami.sgm.restful.models.DatosRenLiquidacion;
import com.origami.sgm.restful.models.RenLocalComercialModel;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.activiti.engine.history.HistoricTaskInstance;
import util.EntityBeanCopy;
import util.JsonUtil;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@RequestScoped
@Path(value = "consultas/")
@Produces({"application/Json", "text/xml"})
public class EstatusTramite implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.inject.Inject
    private Entitymanager services;
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    protected BpmBaseEngine engine;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @javax.inject.Inject
    private RentasServices rentas;
    static final Logger log = Logger.getLogger(EstatusTramite.class.getName());

    @GET
    @Path(value = "tramites/dni/{dni}/tram/{tram}")
    public Collection<DatosTramite> getTramites(@PathParam(value = "dni") String dni, @PathParam(value = "tram") Long numTramite) {
        Collection<DatosTramite> ldt = null;
        List<HistoricoTramites> hts = new ArrayList<>();
        try {
            if (dni != null && !dni.isEmpty() && !dni.contentEquals("0000000000")) {
                CatEnte ent = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{dni});
                if (ent != null) {
                    hts = (List<HistoricoTramites>) services.findAll(Querys.getHistoricoTramitesByEnte, new String[]{"persona"}, new Object[]{ent});
                }
            } else {
                HistoricoTramites thts = permisoServices.getHistoricoTramiteById(numTramite);
                if (thts != null) {
                    hts.add(thts);
                }
            }
            if (!hts.isEmpty()) {
                ldt = new ArrayList<>();
                for (HistoricoTramites ht : hts) {
                    ldt.add(this.getDatos(ht));
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return ldt;
    }

    @GET
    @Path(value = "predio/dni/{dni}")
    public List<CatPredioModel> getDatosPredio(@PathParam(value = "dni") String ds) {
        CatPredioModel cp;
        DatosConsultaPredios dc;
        JsonUtil json = new JsonUtil();
        List<CatPredioModel> lcp = null;
        try {
            if (ds != null) {
                List<CatPredio> predios = manager.findAll(Querys.getPrediosByNumCi, new String[]{"ciRuc"}, new Object[]{ds});
                if (predios != null) {
                    lcp = new ArrayList<>();
                    for (CatPredio pp : predios) {
                        cp = this.getDatosPredio(pp, null);
                        lcp.add(cp);
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return lcp;
    }

    @GET
    @Path(value = "datosLiqPredio/predio/{predio}")
    public List<DatosRenLiquidacion> getDatalosLiquidacion(@PathParam(value = "predio") Long numPredio) {
        List<DatosRenLiquidacion> liqs = null;
        HashMap paramt;
        try {
            CatPredio pp = (CatPredio) manager.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{numPredio});
            if (pp != null) {
                paramt = new HashMap<>();
                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                paramt.put("predio", pp);
                paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                List<RenLiquidacion> emisionesPrediales = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
//                this.calculoTotalPago(emisionesPrediales);
                liqs = this.getDatosLiquidacion(emisionesPrediales);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return liqs;
    }

    @GET
    @Path(value = "localesComercial/propietario/{propietario}")
    public List<RenLocalComercialModel> getLocalesComercial(@PathParam(value = "propietario") String ciRuc) {
        try {
            List<RenLocalComercialModel> locales = new ArrayList<>();
            Map<String, Object> paramt = new HashMap<>();
            CatEnte prop = permisoServices.getCatEnteByCiRuc(ciRuc);
            if (prop != null) {
                paramt.put("propietario", prop);
                paramt.put("estadoLocalComercial", BigInteger.valueOf(1));
                List<RenLocalComercial> list = manager.findObjectByParameterList(RenLocalComercial.class, paramt);
                if (list != null) {
                    for (RenLocalComercial lc : list) {
                        RenLocalComercialModel model = new RenLocalComercialModel(lc.getId());
                        model.setArea(lc.getArea());
                        if (lc.getCategoria() != null) {
                            model.setCategoria(lc.getCategoria().getDescripcion());
                        }
                        model.setContabilidad(lc.getContabilidad());
                        model.setInicioActividad(lc.getInicioActividad());
                        model.setMatriz(lc.getMatriz());
                        model.setNombreLocal(lc.getNombreLocal());
                        model.setNumLocal(lc.getNumLocal());
                        model.setNumPredio(lc.getNumPredio());
                        if (lc.getRazonSocial() != null) {
                            model.setRazonSocial(lc.getRazonSocial().getNombreCompleto());
                        }
                        if (lc.getRenLiquidacionCollection() != null) {
                            List<RenLiquidacion> l = new ArrayList<>();
                            for (RenLiquidacion rl : lc.getRenLiquidacionCollection()) {
                                if (Utils.getAnio(rl.getFechaIngreso()) == (Utils.getAnio(new Date()) - 1)) {
                                    l.add(rl);
                                }
                            }
                            model.setRenLiquidacionCollection(getDatosLiquidacion(l));
                        }
                        if (lc.getTipoLocal() != null) {
                            model.setTipoLocal(lc.getTipoLocal().getDescripcion());
                        }
                        if (lc.getRenActividadComercialCollection() != null) {
                            for (RenActividadComercial ac : lc.getRenActividadComercialCollection()) {
                                if (rentas.actividadTuristica(ac, lc.getTipoLocal())) {
                                    model.setTurismo(true);
                                    break;
                                }
                            }
                        }
                        if (lc.getUbicacion() != null) {
                            model.setUbicacion(lc.getUbicacion().getDescripcion());
                        }
                        model.setActividad(lc.getActividades());
                        locales.add(model);
                    }
                    return locales;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "getLocalesComercial()", e);
        }
        return null;
    }

    @GET
    @Path(value = "existePermisos/local/{idLocal}")
    public Boolean existePermisos(@PathParam(value = "idLocal") Long idLocal) {
        try {
            if (idLocal == null) {
                return null;
            }
            RenLocalComercial local = manager.find(RenLocalComercial.class, idLocal);
            return rentas.existePermiso(local);
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            return false;
        }
    }

    protected void calculoTotalPago(List<RenLiquidacion> listado) {
        Boolean flag = true;
        for (RenLiquidacion e : listado) {
            if (e.getEstadoLiquidacion().getId().compareTo(2L) == 0) {
                try {
                    //CALCULO DE DESCUENTO-RECARGO-INTERES
                    e = recaudacion.realizarDescuentoRecargaInteresPredial(e, null);
                    e.calcularPagoConCoactiva();
                } catch (Exception ex) {
                    Logger.getLogger(EstatusTramite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected DatosTramite getDatos(HistoricoTramites ht) {
        DatosTramite dt = null;
        try {
            if (ht != null) {
                dt = new DatosTramite();
                dt.setId(ht.getIdTramite());
                if (ht.getId() != null) {
                    dt.setIdProceso(ht.getId().toString());
                } else {
                    dt.setIdProceso(ht.getIdProceso());
                }
                dt.setnTramite(ht.getId());
                dt.setNombreTramite(ht.getTipoTramiteNombre());
                dt.setEstado(ht.getEstado());
                dt.setFecha(ht.getFecha());
                if (ht.getNumPredio() != null) {
                    dt.setNumPredio(ht.getNumPredio().longValue());
                }
                dt.setTipoTramite(ht.getTipoTramite().getId());
                dt.setObservacion(ht.getObservacion());
                dt.setSlUrb(ht.getSolar());
                dt.setMzUrb(ht.getMz());
                if (ht.getSolicitante() != null) {
                    dt.setCi(ht.getSolicitante().getCiRuc());
                    dt.setPersona(ht.getSolicitante().getEsPersona());
                    if (ht.getSolicitante().getEsPersona()) {
                        dt.setApellidos(ht.getSolicitante().getApellidos());
                        dt.setNombres(ht.getSolicitante().getNombres());
                    } else {
                        dt.setDescSolicitante(ht.getSolicitante().getRazonSocial());
                    }
                }
                dt.setDetalle(this.getTareas(ht));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return dt;
    }

    protected List<DatosTramiteDet> getTareas(HistoricoTramites hs) {
        List<DatosTramiteDet> ldt = null;
        DatosTramiteDet d = null;
        try {
            List<HistoricTaskInstance> tareas = null;
            if (hs.getIdProcesoTemp() != null || hs.getIdProceso() != null) {
                if (hs.getIdProcesoTemp() != null) {
                    tareas = engine.getTaskByProcessInstanceIdMain(hs.getIdProcesoTemp());
                } else {
                    tareas = engine.getTaskByProcessInstanceIdMain(hs.getIdProceso());
                }
                if (tareas != null) {
                    ldt = new ArrayList<>();
                    for (HistoricTaskInstance t : tareas) {
                        d = new DatosTramiteDet(Long.parseLong(t.getId()), t.getName(), t.getAssignee(), t.getCreateTime(), t.getEndTime());
                        ldt.add(d);
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return ldt;
    }

    protected CatPredioModel getDatosPredio(CatPredio p, List<RenLiquidacion> liq) {
        CatPredioModel cp = null;
        try {
            cp = new CatPredioModel();
            cp.setNumPredio(p.getNumPredio());
            cp.setSector(p.getSector());
            cp.setCdla(p.getCdla());
            cp.setDiv1(p.getDiv1());
            cp.setDiv2(p.getDiv2());
            cp.setDiv3(p.getDiv3());
            cp.setDiv4(p.getDiv4());
            cp.setDiv5(p.getDiv5());
            cp.setDiv6(p.getDiv6());
            cp.setDiv7(p.getDiv7());
            cp.setDiv8(p.getDiv8());
            cp.setDiv9(p.getDiv9());
            cp.setPhv(p.getPhv());
            cp.setPhh(p.getPhh());
            cp.setMz(p.getMz());
            cp.setMzDiv(p.getMzdiv());
            cp.setMzUrb(p.getUrbMz());
            if (p.getCiudadela() != null) {
                cp.setUrbanizacion(p.getCiudadela().getNombre());
                cp.setCiudadela((CatCiudadela) EntityBeanCopy.clone(p.getCiudadela()));
            }

            cp.setProvincia(p.getProvincia());
            cp.setCanton(p.getCanton());
            cp.setParroquiaShort(p.getParroquia());
            cp.setZona(p.getZona());
            cp.setSector(p.getSector());
            cp.setMz(p.getMz());
            cp.setSolar(p.getSolar());
            cp.setBloque(p.getBloque());
            cp.setPiso(p.getPiso());
            cp.setUnidad(p.getUnidad());
            cp.setCodigoPredial(p.getClaveCat());
            cp.setPredialAnt(p.getPredialant());

            cp.setDatosLiquidacion(this.getDatosLiquidacion(liq));
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return cp;
    }

    protected List<DatosRenLiquidacion> getDatosLiquidacion(List<RenLiquidacion> liq) {
        List<DatosRenLiquidacion> ldl = null;
        try {
            if (liq != null) {
                ldl = new ArrayList<>();
                for (RenLiquidacion l : liq) {
                    DatosRenLiquidacion d = new DatosRenLiquidacion();
                    d.setId(l.getId());
                    if (l.getEstadoLiquidacion().getId().compareTo(2L) == 0) {
                        try {
                            //CALCULO DE DESCUENTO-RECARGO-INTERES
                            l = recaudacion.realizarDescuentoRecargaInteresPredial(l, null);
                            l.calcularPagoConCoactiva();
                        } catch (Exception ex) {
                            Logger.getLogger(EstatusTramite.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    d.setIdLiquidacion(l.getIdLiquidacion());
                    d.setFechaIngreso(l.getFechaIngreso());
                    d.setNumLiquidacion(l.getNumLiquidacion());
                    if (l.getComprador() != null) {
                        d.setVendedor(l.getComprador().getNombreCompleto());
                    } else {
                        d.setVendedor(l.getNombreComprador());
                    }
                    d.setAnio(l.getAnio());
                    if (l.getPredio() != null) {
                        if (l.getPredio().getNumPredio() != null) {
                            d.setPredio(l.getPredio().getNumPredio().longValue());
                        }
                    }

                    d.setAvaluoMunicipal(l.getAvaluoMunicipal());
                    d.setTotalPago(l.getTotalPago());
                    d.setDescuento(l.getDescuento());
                    d.setRecargo(l.getRecargo());
                    d.setInteres(l.getInteres());
                    d.setValorCoactiva(l.getValorCoactiva());
                    d.setPagoFinal(l.getPagoFinal());
                    ldl.add(d);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return ldl;
    }
}
