package com.origami.sgm.services.ejbs.rentas;

import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AvalBandaImpositiva;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracionPredios;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MejDetRubroMejoras;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenClaseLocal;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenDetallePlusvalia;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenRangosValores;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenSecuenciaNumLiquidicacion;
import com.origami.sgm.entities.RenSolicitudesLiquidacion;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoLocalComercial;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.entities.RenTurismo;
import com.origami.sgm.entities.RenTurismoDetalleHoteles;
import com.origami.sgm.entities.RenTurismoServicios;
import com.origami.sgm.entities.RenValoresPlusvalia;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.entities.models.HabitacionTurismo;
import com.origami.sgm.entities.models.LiquidacionesPermisosFuncionamiento;
import com.origami.sgm.entities.models.SolicitudExoneracionEnte;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Stateless(name = "rentasEjb")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class RentasEjb implements RentasServices {

    private static final Logger LOG = Logger.getLogger(RentasEjb.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private SeqGenMan seq;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;

    @javax.inject.Inject
    private CatastroServices catas;

    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    @javax.inject.Inject
    private AvaluosServices avaluos;

    @Override
    public PermisoConstruccionServices permisoServices() {
        return permisoService;
    }

    @Override
    public DatoSeguroServices getDatoSeguro() {
        return datoSeguroSeguro;
    }

    /**
     * Envia a persistir la entiti RenEntidadBancaria si el id es nulo caso
     * contrario envia a realizar update.
     *
     * @param banco entiti RenEntidadBancaria
     * @return Entiti RenEntidadBancaria persistida
     */
    @Override
    public RenEntidadBancaria guardarBanco(RenEntidadBancaria banco) {
        if (banco.getId() == null) {
            banco.setFechaIngreso(new Date());
            return (RenEntidadBancaria) manager.persist(banco);
        } else {
            manager.update(banco);
            return banco;
        }

    }

    /**
     * B
     *
     * @param descripcion
     * @return
     */
    @Override
    public Long existeRenEntidadBancaria(String descripcion) {
        return (Long) manager.findNoProxy(QuerysFinanciero.getIdEntidadBanco, new String[]{"descripcion"}, new Object[]{descripcion.toUpperCase()});
    }

    @Override
    public List<RenEntidadBancaria> getBancos(Long tipo) {
        Map<String, Object> par = new HashMap<>();
        par.put("tipo.id", tipo);
        return manager.findObjectByParameterOrderList(RenEntidadBancaria.class, par, new String[]{"descripcion"}, Boolean.TRUE);
    }

    @Override
    public Boolean guardarRubrosPorLiquidacion(List<RenDetLiquidacion> list) {
        Boolean b;
        try {
            b = true;
            for (RenDetLiquidacion varTemp : list) {
                manager.persist(varTemp);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            b = false;
        }
        return b;
    }

    @Override
    public Boolean guardarRubrosPorTipoLiquidacion(List<RenRubrosLiquidacion> list) {
        Boolean b;
        try {
            b = true;
            for (RenRubrosLiquidacion varTemp : list) {
                manager.persist(varTemp);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            b = false;
        }
        return b;
    }

    @Override
    public RenLiquidacion guardarLiquidacion(RenLiquidacion liquidacion, List<RenRubrosLiquidacion> rubrosLiquidacion, String prefijo, RenValoresPlusvalia valores) {
        try {
            final Long tipoLiquidacion = liquidacion.getTipoLiquidacion().getId();
            liquidacion.setSaldo(liquidacion.getTotalPago());
            liquidacion = (RenLiquidacion) manager.persist(liquidacion);
            if (liquidacion != null) {
                for (RenRubrosLiquidacion rl : rubrosLiquidacion) {
                    if (rl.getCobrar()) {
                        RenDetLiquidacion ruL = new RenDetLiquidacion();
                        ruL.setLiquidacion(liquidacion);
                        ruL.setRubro(rl.getId());
                        ruL.setEstado(true);
                        if (rl.getId() == 343l && rl.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
                            ruL.setValor(rl.getValor());
                        } else {
                            ruL.setValor(rl.getValorTotal());
                        }
                        ruL.setValorRecaudado(BigDecimal.ZERO);
                        manager.persist(ruL);
                    }
                }
                if (valores != null) {
                    valores.setLiquidacion(liquidacion);
                    List<RenDetallePlusvalia> detallePlusvalias = (List<RenDetallePlusvalia>) valores.getRenDetallePlusvaliaCollection();
                    valores.setRenDetallePlusvaliaCollection(null);
                    valores = (RenValoresPlusvalia) manager.persist(valores);
//                    manager.saveList(detallePlusvalias);
                    if (detallePlusvalias != null) {
                        for (RenDetallePlusvalia detallePlusvalia : detallePlusvalias) {
                            detallePlusvalia.setValoresPlusvalia(valores);
                            manager.persist(detallePlusvalia);
                        }
                    }
                }
            }

            return liquidacion;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Registrar Liquidacion", e);
            return null;
        }
    }

    @Override
    public RenTipoLiquidacion getTipoLiquidacion(Long tipoRubros) {
        return manager.find(RenTipoLiquidacion.class, tipoRubros);
    }

    @Override
    public RenEstadoLiquidacion getEstadoLiquidacionByDesc(Long activo) {
        Map<String, Object> par = new HashMap<>();
        par.put("id", activo);
        return manager.findObjectByParameter(RenEstadoLiquidacion.class, par);
    }

    @Override
    public List<RenTipoLiquidacion> getTiposLiquidacionList(Long tipo) {
        Map<String, Object> par = new HashMap<>();
        par.put("id", tipo);
        return manager.findObjectByParameterOrderList(RenTipoLiquidacion.class, par, new String[]{"nombreTitulo"}, Boolean.TRUE);
    }

    @Override
    public List<RenTipoLiquidacion> getTiposLiquidacionList() {
        return manager.findAllObjectOrder(RenTipoLiquidacion.class, new String[]{"nombreTitulo"}, Boolean.TRUE);
    }

    @Override
    public RenDesvalorizacion getDesvalorizacionAnio(Integer anio) {
        Map<String, Object> par = new HashMap<>();
        par.put("anio", anio);
        return manager.findObjectByParameter(RenDesvalorizacion.class, par);
    }

    @Override
    public RenDesvalorizacion guardarDesvalorizacion(RenDesvalorizacion desvalorizacion) {
        return (RenDesvalorizacion) manager.persist(desvalorizacion);
    }

    @Override
    public List<RenRubrosLiquidacion> getRubrosPorLiquidacion(Long idTipo) {
        Map<String, Object> par = new HashMap<>();
        par.put("tipoLiquidacion.id", idTipo);
        par.put("estado", true);
        return manager.findObjectByParameterOrderList(RenRubrosLiquidacion.class, par, new String[]{"prioridad"}, Boolean.TRUE);
    }

    @Override
    public MatFormulaTramite getMatFormulaByPrefijo(String prefijo) {
        Map<String, Object> par = new HashMap<>();
        par.put("prefijo", prefijo);
        par.put("estado", true);
        return manager.findObjectByParameter(MatFormulaTramite.class, par);
    }

    @Override
    public List<RenTipoLiquidacion> gettiposLiquidacionByCodTitRep(int tipo) {
        switch (tipo) {
            case 1:
                return manager.findAll(QuerysFinanciero.getRenTipoLiquidacionByCodTitRepPA);
            case 2:
                return manager.findAll(QuerysFinanciero.getRenTipoLiquidacionByCodTitRepLC);
            case 3:
                return manager.findAll(QuerysFinanciero.getRenTipoLiquidacionByCodTitRepEdf);
            default:
                return manager.findAll("SELECT r FROM RenTipoLiquidacion r WHERE r.transaccionPadre = 225");
            // SELECT r FROM RenTipoLiquidacion r WHERE r.codigoTituloReporte IN (13, 16, 27, 28, 29, 119, 120, 121, 122, 188) ORDER BY r.nombreTransaccion ASC
        }
//        return null;
    }

    @Override
    public RenLiquidacion guardarLiquidacionYRubros(RenLiquidacion liq, List<RenDetLiquidacion> rubros, RenTipoLiquidacion tipoLiq, RenActivosLocalComercial activos, RenBalanceLocalComercial balance, HistoricoTramites ht, String numLiquidacion) {
        RenDetLiquidacion detalle;
        Calendar c2 = Calendar.getInstance();
        RenLiquidacion liquidacion = null;
        try {

            liquidacion = (RenLiquidacion) manager.persist(liq);

            if (numLiquidacion == null || numLiquidacion == "") {
                liquidacion.setNumLiquidacion(seq.getMaxSecuenciaTipoLiquidacion(c2.get(Calendar.YEAR), tipoLiq.getId()));

            } else {
                liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.parseLong(numLiquidacion)));
            }
            liquidacion.setIdLiquidacion(tipoLiq.getPrefijo().concat("-").concat(Utils.completarCadenaConCeros(liquidacion.getNumLiquidacion().toString(), 6)));
            liquidacion = (RenLiquidacion) manager.persist(liquidacion);

            if (ht != null) {
                ht.setNumLiquidacion(liquidacion.getNumLiquidacion());
                manager.persist(ht);
            }
            if (liquidacion != null) {
                for (RenDetLiquidacion temp : rubros) {
                    if (temp.getRubro() != null) {
                        temp.setLiquidacion(liquidacion);
                        temp.setEstado(true);
                        temp.setValorRecaudado(BigDecimal.ZERO);
                        manager.persist(temp);
                    }
                }
                if (activos != null) {
                    activos.setEstado(Boolean.TRUE);
                    activos.setNumLiquidacion(BigInteger.valueOf(liquidacion.getId()));
                    manager.persist(activos);
                }
                if (balance != null) {
                    balance.setEstado(Boolean.TRUE);
                    balance.setNumLiquidacion(BigInteger.valueOf(liquidacion.getId()));
                    manager.persist(balance);
                }
            }

        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "Guardar Liquidacion Locales Comerciales", e);
        }
        return liquidacion;
    }

    @Override
    public FnExoneracionClase guadModExoneracion(FnExoneracionClase exoneracion) {
        try {
            final List<FnExoneracionTipo> list = (List<FnExoneracionTipo>) exoneracion.getFnExoneracionTipoCollection();
            exoneracion.setFnExoneracionTipoCollection(null);
            if (exoneracion.getId() == null) {
                exoneracion = (FnExoneracionClase) manager.persist(exoneracion);
            } else {
                manager.update(exoneracion);
            }
            if (Utils.isNotEmpty(list)) {
                exoneracion.setFnExoneracionTipoCollection(new ArrayList<FnExoneracionTipo>());
                for (FnExoneracionTipo l : list) {
                    if (l.getId() == null) {
                        l.setExoneracionClase(exoneracion);
                        exoneracion.getFnExoneracionTipoCollection().add((FnExoneracionTipo) manager.persist(l));
                    } else {
                        manager.update(l);
                        exoneracion.getFnExoneracionTipoCollection().add(l);
                    }
                }
            }
            exoneracion.setFnExoneracionTipoCollection(list);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
        return exoneracion;
    }

    @Override
    public List<FnExoneracionClase> FnExoneraciones(Boolean b, int tipo) {
        Map<String, Object> in = new HashMap<>();
        Map<String, Object> par = new HashMap<>();
        par.put("estado", b);
        List<Long> ids;
        switch (tipo) {
            case 1:
                ids = new ArrayList<>();
                ids.add(1L);
                ids.add(2L);
                ids.add(3L);
                ids.add(4L);
                ids.add(5L);
                ids.add(6L);
                ids.add(7L);
                ids.add(8L);
                ids.add(9L);
                ids.add(10L);
                in.put("id", ids);
                break;
            case 2:
                ids = new ArrayList<>();
                ids.add(3L);
                ids.add(6L);
                ids.add(7L);
                ids.add(8L);
                ids.add(5L);
                in.put("id", ids);
                break;
            default:

                break;
        }
        return manager.findIn(FnExoneracionClase.class, par, in);
    }

    @Override
    public List<FnExoneracionTipo> FnExoneracionesTipo(Boolean b, Long idExon) {
        Map<String, Object> par = new HashMap<>();
        par.put("exoneracionClase.id", idExon);
        par.put("estado", b);
        return manager.findObjectByParameterOrderList(FnExoneracionTipo.class, par, new String[]{"id"}, Boolean.TRUE);
    }

    @Override
    public List<RenTipoValor> tipoValorList(Set<Long> ids) {
        Map<String, Object> par = new HashMap<>();
        par.put("id", ids);
        return manager.findIn(RenTipoValor.class, null, par);
    }

    @Override
    public FnSolicitudExoneracion registraSolicitudExoneracion(FnSolicitudExoneracion SolExo, Long user, List<CatPredio> predios, String obs) {
        GeTipoTramite tipoTramite = buscarTipoTramiteDep(8L, "EXN");

        HistoricoTramites ht = new HistoricoTramites();
        ht.setFecha(new Date());
        ht.setEstado("Pendiente");
        if (SolExo.getSolicitante() != null) {
            ht.setSolicitante(SolExo.getSolicitante());
            ht.setNombrePropietario(SolExo.getSolicitante().getNombreCompleto());
        }
        ht.setTipoTramite(tipoTramite);
        ht.setTipoTramiteNombre(tipoTramite.getDescripcion());
        ht.setUserCreador(user);
        final Long id = seq.getSecuenciasTram("SGM");
        ht.setId(id);
        ht = (HistoricoTramites) manager.persist(ht);

        try {
            if (ht != null) {
                SolExo.setTramite(ht);

                HistoricoTramiteDet det;
                if (predios != null) {
                    for (CatPredio predio : predios) {
                        det = new HistoricoTramiteDet();
                        det.setTramite(ht);
                        det.setEstado(Boolean.TRUE);
                        det.setPredio(predio);
                        det.setFecCre(new Date());
                        manager.persist(det);
                    }
                }
                Observaciones o = new Observaciones();
                o.setEstado(Boolean.TRUE);
                o.setFecCre(new Date());
                o.setIdTramite(ht);
                o.setObservacion(obs);
                o.setTarea("Registrar Solicitud");
                o.setUserCre(permisoServices().getAclUserById(user).getUsuario());
                o = (Observaciones) manager.persist(o);

                SolExo = (FnSolicitudExoneracion) manager.persist(SolExo);
                return SolExo;
            }
            return null;
        } catch (Exception e) {
            LOG.log(Level.OFF, "Registra Solicitud de Exoneracion", e);
            return null;
        }
    }

    @Override
    public FnSolicitudExoneracion registraSolicitudExoneracionRust(FnSolicitudExoneracion SolExo, Long user, List<CatPredioRustico> prediosRusticos, String obs) {
        GeTipoTramite tipoTramite = buscarTipoTramiteDep(8L, "EXN");

        HistoricoTramites ht = new HistoricoTramites();
        ht.setFecha(new Date());
        ht.setEstado("Pendiente");
        if (SolExo.getSolicitante() != null) {
            ht.setSolicitante(SolExo.getSolicitante());
            ht.setNombrePropietario(SolExo.getSolicitante().getNombreCompleto());
        }
        ht.setTipoTramite(tipoTramite);
        ht.setTipoTramiteNombre(tipoTramite.getDescripcion());
        ht.setUserCreador(user);
        final Long id = seq.getSecuenciasTram("SGM");
        ht.setId(id);
        ht = (HistoricoTramites) manager.persist(ht);

        try {
            if (ht != null) {
                SolExo.setTramite(ht);

                HistoricoTramiteDet det;
                if (prediosRusticos != null) {
                    for (CatPredioRustico predio : prediosRusticos) {
                        det = new HistoricoTramiteDet();
                        det.setTramite(ht);
                        det.setEstado(Boolean.TRUE);
                        det.setPredioRustico(predio);
                        det.setFecCre(new Date());
                        manager.persist(det);
                    }
                }
                Observaciones o = new Observaciones();
                o.setEstado(Boolean.TRUE);
                o.setFecCre(new Date());
                o.setIdTramite(ht);
                o.setObservacion(obs);
                o.setTarea("Registrar Solicitud");
                o.setUserCre(permisoServices().getAclUserById(user).getUsuario());
                o = (Observaciones) manager.persist(o);

                SolExo = (FnSolicitudExoneracion) manager.persist(SolExo);
                return SolExo;
            }
            return null;
        } catch (Exception e) {
            LOG.log(Level.OFF, "Registra Solicitud de Exoneracion", e);
            return null;
        }
    }

    @Override
    public GeTipoTramite buscarTipoTramiteDep(Long idDep, String abr) {
        GeDepartamento dep = manager.find(GeDepartamento.class, idDep);
        return buscarTipoTramite(dep, abr);
    }

    @Override
    public GeTipoTramite buscarTipoTramite(GeDepartamento dep, String abr) {
        if (dep == null) {
            throw new RuntimeException("Departamento es nulo");
        }
        for (GeTipoTramite col : dep.getGeTipoTramiteCollection()) {
            if (col.getEstado() && col.getAbreviatura().equalsIgnoreCase(abr)) {
                return col;
            }
        }
        return null;
    }

    @Override
    public GeTipoTramite geTipoTramiteByAbr(String abreviatura) {
        Map<String, Object> par = new HashMap<>();
        par.put("abreviatura", abreviatura);
        par.put("estado", true);
        return manager.findObjectByParameter(GeTipoTramite.class, par);
    }

    @Override
    public Boolean actualizarsolicitudExoneracion(FnSolicitudExoneracion SolExo) {
        try {
            if (SolExo.getTramite() != null) {
                SolExo.getTramite().setCarpetaRep(SolExo.getTramite().getId() + "-" + SolExo.getTramite().getIdProcesoTemp());
//                manager.persist(SolExo.getTramite());
                manager.persist(SolExo); // error
                return true;
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "Actualizar Solicitud Ex.", e);
            return false;
        }
        return false;
    }

    @Override
    public FnResolucion guardarResolucion(FnResolucion resolucion) {
        return (FnResolucion) manager.persist(resolucion);
    }

    @Override
    public List<FnSolicitudExoneracion> verficarSolicitudExoneracion(FnExoneracionTipo idTipoExoneracion, Integer anioInicio, Integer anioFin, CatPredio predio) {
        try {
            return manager.findAll(QuerysFinanciero.existeSolicitudPredio,
                    new String[]{"predio", "exoneracionTipo", "anioInicio", "anioFin", "estado"},
                    new Object[]{predio, idTipoExoneracion, anioInicio, anioFin, 2L});
        } catch (Exception e) {
            LOG.log(Level.OFF, "VERIFICAR EXISTENCIA DE SOLICITUD", e);
        }
        return null;
    }

    @Override
    public CatEnte consultarEnte(String cedula, boolean compania, String UserCreacion) {
        CatEnte ente = permisoServices().getCatEnteByCiRuc(cedula);
//        CatEnte ente = permisoServices().getCatEnteByCiRucByEsPersona(cedula, !compania);
        try {
            DatoSeguro d = getDatoSeguro().getDatos(cedula, compania, 0);
            if (d != null) {
                if (ente == null) {
                    ente = new CatEnte();
                }
                getDatoSeguro().llenarEnte(d, ente, (ente.getId() == null || ente.getCiRuc() == null));
            }
            if (ente != null) {
                if (ente.getId() != null) {
                    ente.setUserMod(UserCreacion);
                    ente.setFechaMod(new Date());
                    manager.update(ente);
                } else {
                    ente.setFechaCre(new Date());
                    ente.setUserCre(UserCreacion);
                    ente = (CatEnte) manager.persist(ente);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, cedula, e);
            return null;
        }

        return ente;
    }

    @Override
    public FnSolicitudExoneracion registarDatoSolicitudExoneracion(FnSolicitudExoneracion solicitud, List<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionesExoneracion, List<FnSolicitudExoneracionPredios> prediosSolicitud) {
        try {
            if (tipoLiquidacionesExoneracion != null && !tipoLiquidacionesExoneracion.isEmpty()) {
                for (FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionesExoneracion1 : tipoLiquidacionesExoneracion) {
                    tipoLiquidacionesExoneracion1.setSolicitudExoneracion(solicitud);
                    manager.persist(tipoLiquidacionesExoneracion1);
                }
            }
            if (prediosSolicitud != null && !prediosSolicitud.isEmpty()) {
                for (FnSolicitudExoneracionPredios prediosSolicitud1 : prediosSolicitud) {
                    prediosSolicitud1.setSolicitudExoneracion(solicitud);
                    manager.persist(prediosSolicitud1);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "DATOS SOLICITUD EXONERACION", e);
        }
        return solicitud;
    }

    @Override
    public List<FnExoneracionLiquidacion> aplicarExoneracion(HistoricoTramites ht, FnSolicitudExoneracion sol, String username) {
        FnSolicitudExoneracion solicitud = null;
        List<CatPredio> predios = new ArrayList();
        List<CatPredioRustico> prediosRusticos = new ArrayList();
        FnExoneracionLiquidacion tablaIntermedia;
        List<FnExoneracionLiquidacion> exoneraciones = new ArrayList();
        CtlgSalario salario;
        CatPredio predioRaiz;
        BigDecimal salarioMax = BigDecimal.ZERO, diferenciaSalarioTemp;
        GroovyUtil gutil;
        List<RenDetLiquidacion> detalleLiquidacion;
        MatFormulaTramite formula;
        FnExoneracionClase exoneracionClase;
        FnExoneracionTipo exoneracionTipo;
        CatPredio predio = null;
        //Boolean b; --HENRY
        RenLiquidacion liquidacion = null, liquidacion2 = null;
        RenDetLiquidacion nuevoRubro;
        RenRangosValores valorRango;
        BigDecimal total, diferencia;
        BigDecimal totalTemp = BigDecimal.ZERO;
        Map<BigInteger, SolicitudExoneracionEnte> map = new HashMap();
        CatEnte solicitante;
        String mensajeExoneracion;
        MejDetRubroMejoras rubroMejora, mejoraEncontrada;

        //DEBE USARSE CUANDO SE REALIZA UNA EMISION NO EN EXONERACIONES
        formula = (MatFormulaTramite) manager.find(MatFormulaTramite.class, 33L);
        gutil = new GroovyUtil(formula.getFormula());

        BigDecimal totalPagoExneracion;
        BigDecimal valorCalculado;
        BigDecimal valor = new BigDecimal("0.00");
        Map<String, Object> parametros;
        List<FnSolicitudExoneracionPredios> prediosSolicitud;
        List<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionesSolicitud;

        if (ht == null) {
            solicitud = sol;
            if (sol.getPredio() != null) {
                predios.add(sol.getPredio());
            }
            if (sol.getPredioRustico() != null) {
                prediosRusticos.add(sol.getPredioRustico());
            }
        } else if (ht.getIdTramite() == null) {
            if (ht.getFnSolicitudExoneracions() != null) {
                solicitud = manager.findNoProxy(FnSolicitudExoneracion.class, ht.getFnSolicitudExoneracions().get(0).getId());
            }
        } else {
            solicitud = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByTramite, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
        }
        if (solicitud == null) {
            return null;
        }

        Integer anioDif = solicitud.getAnioFin() - solicitud.getAnioInicio();
        Integer anioInicio = solicitud.getAnioInicio();
        mensajeExoneracion = "Tiene una exoneración de: " + solicitud.getExoneracionTipo().getDescripcion().toUpperCase() + "\nNúmero de resolución: " + solicitud.getNumResolucionSac();

        BigDecimal avaluoCalculo = null;
        RenLiquidacion liquidacionDadaBaja;
        RenLiquidacion liquidacionExonerada;
        try {
            //b = true; --HENRY
            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
            salarioMax = salario.getValor().multiply(new BigDecimal(500));
            if (ht != null) {
                for (HistoricoTramiteDet temp : ht.getHistoricoTramiteDetCollection()) {
                    if (temp.getPredio() != null) {
                        predios.add(temp.getPredio());
                    }
                    if (temp.getPredioRustico() != null) {
                        prediosRusticos.add(temp.getPredioRustico());
                    }
                }
            }
            exoneracionTipo = solicitud.getExoneracionTipo();
            exoneracionClase = exoneracionTipo.getExoneracionClase();
            solicitante = solicitud.getSolicitante();
            System.out.println("exoneracionClase.getId().intValue() " + exoneracionClase.getId().intValue());
            ValoracionPredial valoracion;
            parametros = new HashMap<>();
            parametros.put("solicitudExoneracion", solicitud);
            prediosSolicitud = manager.findObjectByParameterList(FnSolicitudExoneracionPredios.class, parametros);
            if ((prediosSolicitud == null || prediosSolicitud.isEmpty()) && solicitud.getPredio() != null) {
                FnSolicitudExoneracionPredios ps = new FnSolicitudExoneracionPredios();
                ps.setPredio(solicitud.getPredio());
                ps.setSolicitudExoneracion(solicitud);
                manager.persist(ps);
                prediosSolicitud = manager.findObjectByParameterList(FnSolicitudExoneracionPredios.class, parametros);
            }
            switch (exoneracionClase.getId().intValue()) {
                case 1: //Rebaja del recargo por solar no edificado. SOLO AFECTA EL VALOR DEL RUBRO 9. EN EL SAC GUARDA ESE VALOR DEL RUBRO EN LA COLUMNA DE EXONERACION DE PU_CATASTRO E INSERTA UN REGISTRO EN LA TABLA PU_PREDIOS_DADOS_DE_BAJA
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NO NECESITA PARAMETRO DE VALORES (NO LOS CONSIDERA)
                     */
                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            if (ps.getPredio() != null) {
                                if (ps.getPredio() != null) {
                                    if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                        liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                        liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                        liquidacionExonerada.setTramite(ht);
                                        totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, null, null, Boolean.FALSE, exoneracionTipo);
                                        liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                        liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                        liquidacionExonerada.setSaldo(totalPagoExneracion);
                                        liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                        liquidacionExonerada.setValorExoneracion(liquidacion.getTotalPago().subtract(totalPagoExneracion));
                                        liquidacionExonerada.setExoneracionDescripcion("EXONERACIÒN: " + solicitud.getExoneracionTipo().getDescripcion());
                                        manager.persist(liquidacionExonerada);
                                        exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 2: //No considera el avaluo de la Construccion
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NO NECESITA PARAMETRO DE VALORES (NO LOS CONSIDERA)
                     */
                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            if (ps.getPredio() != null) {
                                if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                    liquidacionExonerada.setTramite(ht);
                                    totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, liquidacion.getAvaluoSolar(), null, Boolean.TRUE, exoneracionTipo);
                                    liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                    liquidacionExonerada.setBandaImpositiva(liquidacion.getAvaluoSolar());
                                    liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                    liquidacionExonerada.setSaldo(totalPagoExneracion);
                                    liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                    manager.persist(liquidacionExonerada);
                                    exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                }
                            }
                        }
                    }
                    break;
                case 3: //Exoneración definitiva de los impuestos, solo se considera las tasas
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NO NECESITA PARAMETRO DE VALORES (SI LOS INGRESA SI LOS CONSIDERA, SI NO REBAJA EL TOTAL 100%)
                     */
                    System.out.println("exoneracionTipo.getId().intValue() " + exoneracionTipo.getId().intValue());
                    switch (exoneracionTipo.getId().intValue()) {

                        case 17:
                            List<CatPredio> prediosExoneracion = new ArrayList();
                            for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                prediosExoneracion.add(ps.getPredio());
                            }
                            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio,
                                    new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
                            salarioMax = salario.getValor().multiply(new BigDecimal(500));
                            prediosExoneracion = exoneracionA500Remuneraciones(salarioMax, prediosExoneracion);
                            for (CatPredio ps : prediosExoneracion) {
                                //for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                    //SIEMPRE LAS EMISIONES
                                    if (ps != null) {
                                        if (validarAplicarExoneracion(ps, null, null, i, 13L, exoneracionTipo.getId())) {
                                            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps, i, 13L, 2L);
                                            liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                            liquidacionExonerada.setTramite(ht);
                                            ///50% PARA LA LEY DEL DISCAPACITADO
                                            if (exoneracionTipo.getId().intValue() == 44L) {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal().divide(new BigDecimal("2")));
                                            } else {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal());
                                            }
                                            totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, ps.getAvaluoMunicipal(), null, Boolean.TRUE, exoneracionTipo);
                                            liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                            liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                            liquidacionExonerada.setSaldo(totalPagoExneracion);
                                            liquidacionExonerada.setValorExoneracion(liquidacion.getTotalPago().subtract(totalPagoExneracion));
                                            liquidacionExonerada.setExoneracionDescripcion("EXONERACIÒN: " + solicitud.getExoneracionTipo().getDescripcion());
                                            liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                            manager.persist(liquidacionExonerada);
                                            exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                            ps.setAvaluoMunicipal(ps.getAvaluoSolar().add(ps.getAvaluoConstruccion()));
                                            manager.persist(ps);
                                        }
                                    }
                                }
                            }
                            break;
                        case 44:
                            prediosExoneracion = new ArrayList();
                            for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                prediosExoneracion.add(ps.getPredio());
                            }
                            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio,
                                    new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
                            salarioMax = salario.getValor().multiply(new BigDecimal(500));
                            prediosExoneracion = exoneracionA500Remuneraciones(salarioMax, prediosExoneracion);
                            for (CatPredio ps : prediosExoneracion) {
                                //for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                    //SIEMPRE LAS EMISIONES
                                    if (ps != null) {
                                        if (validarAplicarExoneracion(ps, null, null, i, 13L, exoneracionTipo.getId())) {
                                            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps, i, 13L, 2L);
                                            liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                            liquidacionExonerada.setTramite(ht);
                                            ///50% PARA LA LEY DEL DISCAPACITADO
                                            if (exoneracionTipo.getId().intValue() == 44L) {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal().divide(new BigDecimal("2")));
                                            } else {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal());
                                            }
                                            totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, ps.getAvaluoMunicipal(), null, Boolean.TRUE, exoneracionTipo);
                                            liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                            liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                            liquidacionExonerada.setSaldo(totalPagoExneracion);
                                            liquidacionExonerada.setValorExoneracion(liquidacion.getTotalPago().subtract(totalPagoExneracion));
                                            liquidacionExonerada.setExoneracionDescripcion("EXONERACIÒN: " + solicitud.getExoneracionTipo().getDescripcion());
                                            liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                            manager.persist(liquidacionExonerada);
                                            exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                            ps.setAvaluoMunicipal(ps.getAvaluoSolar().add(ps.getAvaluoConstruccion()));
                                            manager.persist(ps);
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                    //SIEMPRE LAS EMISIONES
                                    if (ps.getPredio() != null) {
                                        if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{i});
                                            salarioMax = salario != null ? salario.getValor().multiply(new BigDecimal(500)) : new BigDecimal("0.00");
                                            avaluoCalculo = this.obtenerValorParaCalculoExoneracion(solicitud, liquidacion.getAvaluoMunicipal(), salarioMax);
                                            liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                            liquidacionExonerada.setTramite(ht);
                                            totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, avaluoCalculo, null, Boolean.TRUE, exoneracionTipo);
                                            liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                            liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                            liquidacionExonerada.setSaldo(totalPagoExneracion);
                                            liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                            manager.persist(liquidacionExonerada);
                                            exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                        }
                                    }
                                }
                            }
                            break;
                    }

                    break;
                case 4:
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NO NECESITA PARAMETRO DE VALORES (NO LOS CONSIDERA)
                     SE DEBE REALIZAR UNA NUEVA EMISION; SE PROCESA CON LOS DATOS DEL NUEVO AVALUO
                     */
                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            //SIEMPRE LAS EMISIONES
                            if (ps.getPredio() != null) {
                                if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.FALSE, liquidacion.getEstadoLiquidacion().getId());
                                    valoracion = avaluos.getEmisionPredial(username, i, ps.getPredio().getNumPredio(), Boolean.TRUE).get();
                                    avaluos.registrarLiquidacion(valoracion, username).get();
                                    liquidacionExonerada = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    liquidacionExonerada.setTramite(ht);
                                    liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                    liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                    manager.persist(liquidacionExonerada);
                                    exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                }
                            }
                        }
                    }
                    break;
                case 5: //Porcentaje sobre valor de la Hipoteca
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NECESITA PARAMETRO DE VALORES (SI NO LO INGRESA SE EXONERA 100%)                    
                     */
                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            //SIEMPRE LAS EMISIONES
                            if (ps.getPredio() != null) {
                                if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{i});
                                    salarioMax = salario != null ? salario.getValor().multiply(new BigDecimal(500)) : new BigDecimal("0.00");
                                    avaluoCalculo = this.obtenerValorParaCalculoExoneracion(solicitud, liquidacion.getAvaluoMunicipal(), salarioMax);

                                    liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                    liquidacionExonerada.setTramite(ht);
                                    totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, avaluoCalculo, null, Boolean.TRUE, exoneracionTipo);
                                    if (solicitud.getTipoValor().getId().intValue() == 7) {
                                        liquidacionExonerada.setValorHipoteca(solicitud.getValor());
                                    } else {
                                        liquidacionExonerada.setValorHipoteca(liquidacion.getAvaluoMunicipal().multiply(solicitud.getValor()).divide(new BigDecimal("100")));
                                    }
                                    liquidacionExonerada.setBandaImpositiva(avaluoCalculo);
                                    liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                    liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                    liquidacionExonerada.setSaldo(totalPagoExneracion);
                                    ///VALORES PARA REPORTES
                                    liquidacionExonerada.setValorExoneracion(liquidacion.getTotalPago().subtract(totalPagoExneracion));
                                    liquidacionExonerada.setExoneracionDescripcion("EXONERACIÒN: " + solicitud.getExoneracionTipo().getDescripcion());
                                    liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                    manager.persist(liquidacionExonerada);
                                    exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                }
                            }
                        }
                    }
                    break;
                case 6: //Porcentaje sobre valor de la base imponible ACTUALIZADO EL 25/04/2012 LMS
                    /*HENRY : TODOS LOS TIPOS QUE PERTENECEN A ESTA CLASE REALIZAN EL MISMO PROCESO
                     NECESITA PARAMETRO DE VALORES (SI NO LO INGRESA SE EXONERA 100%)                    
                     */
                    tipoLiquidacionesSolicitud = manager.findObjectByParameterList(FnSolicitudTipoLiquidacionExoneracion.class, parametros);
                    switch (exoneracionTipo.getId().intValue()) {
                        case 48:
                            System.out.println("case 48:");
                            for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                    //SIEMPRE LAS EMISIONES
                                    if (ps.getPredio() != null) {
                                        if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                            if (liquidacion != null) {
                                                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(4L));
                                                manager.persist(liquidacion);
                                                valoracion = avaluos.getEmisionPredial(username, i, ps.getPredio().getNumPredio(), Boolean.TRUE).get();
                                                avaluos.registrarLiquidacion(valoracion, username).get();
                                                liquidacionExonerada = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                                liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                                liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                                manager.persist(liquidacionExonerada);
                                                exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        default:

                            System.out.println("PREDIOS DATOS:  " + exoneracionTipo.getId().intValue());
                            System.out.println("EXO TIPO DATOS:  " + exoneracionTipo.getId().intValue());

                            List<CatPredio> prediosExoneracion = new ArrayList();
                            for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                prediosExoneracion.add(ps.getPredio());
                            }
                            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio,
                                    new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
                            salarioMax = salario.getValor().multiply(new BigDecimal(500));
                            prediosExoneracion = exoneracionA500Remuneraciones(salarioMax, prediosExoneracion);
                            for (CatPredio ps : prediosExoneracion) {

                                //for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                                for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                    //SIEMPRE LAS EMISIONES
                                    if (ps != null) {
                                        if (validarAplicarExoneracion(ps, null, null, i, 13L, exoneracionTipo.getId())) {
                                            System.out.println("solicitud " + solicitud.getValor());
                                            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps, i, 13L, 2L);
//                                            salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{i});
//
//                                            salarioMax = salario != null ? salario.getValor().multiply(new BigDecimal(500)) : new BigDecimal("0.00");
//                                            System.out.println("salarioMax" + salarioMax);
                                            //  avaluoCalculo = this.obtenerValorParaCalculoExoneracion(solicitud, ps.getAvaluoMunicipal(), salarioMax);
                                            liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                            liquidacionExonerada.setTramite(ht);
                                            ///50% PARA LA LEY DEL DISCAPACITADO
                                            if (exoneracionTipo.getId().intValue() == 37L) {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal().divide(new BigDecimal("2")));
                                            } else {
                                                liquidacionExonerada.setBandaImpositiva(ps.getAvaluoMunicipal());
                                            }
                                            ///SE LO UTILIZA PARA GUARDAR TEMPORALMENTE EL AVALUO DE LA PROPIEDAD setAvaluoObraComplement
                                            System.out.println("ps.getAvaluoMunicipal() " + ps.getAvaluoMunicipal());
                                            System.out.println("ps.getAvaluoObraComplement() " + ps.getAvaluoObraComplement());
                                            totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, ps.getAvaluoMunicipal(), null, Boolean.TRUE, exoneracionTipo, solicitud, ps.getAvaluoObraComplement());
                                            liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                            liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                            liquidacionExonerada.setSaldo(totalPagoExneracion);
                                            liquidacionExonerada.setValorExoneracion(liquidacion.getTotalPago().subtract(totalPagoExneracion));
                                            liquidacionExonerada.setExoneracionDescripcion("EXONERACIÒN: " + solicitud.getExoneracionTipo().getDescripcion());
                                            liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                            manager.persist(liquidacionExonerada);
                                            exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                            ps.setAvaluoMunicipal(ps.getAvaluoSolar().add(ps.getAvaluoConstruccion()));
                                            manager.persist(ps);
                                        }
                                    }

                                    //LIQUIDACIONES ADICIONALES
                                    if (tipoLiquidacionesSolicitud != null && !tipoLiquidacionesSolicitud.isEmpty()) {
                                        for (FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionesSolicitud1 : tipoLiquidacionesSolicitud) {
                                            if (validarAplicarExoneracion(ps, null, null, i, tipoLiquidacionesSolicitud1.getTipoLiquidacion().getId(), exoneracionTipo.getId())) {
                                                liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps, i, tipoLiquidacionesSolicitud1.getTipoLiquidacion().getId(), 2L);
                                                salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{i});
                                                salarioMax = salario != null ? salario.getValor().multiply(new BigDecimal(500)) : new BigDecimal("0.00");
                                                for (RenDetLiquidacion d : liquidacion.getRenDetLiquidacionCollection()) {
                                                    if (d.getRubro().equals(tipoLiquidacionesSolicitud1.getRubro().getId())) {
                                                        valor = d.getValor();
                                                        break;
                                                    }
                                                }
                                                valorCalculado = this.obtenerValorParaCalculoExoneracion(solicitud, valor, salarioMax);
                                                liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                                liquidacionExonerada.setTramite(ht);
                                                totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, valorCalculado, tipoLiquidacionesSolicitud1, Boolean.TRUE, null);
                                                liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                                liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                                liquidacionExonerada.setSaldo(totalPagoExneracion);
                                                liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                                manager.persist(liquidacionExonerada);
                                                exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                            }
                                        }
                                    }
                                }
                                //}
                            }
                            break;
                    }
                    break;
                case 7: //Prescripcion de titulos o dar de baja / Ley Organica pàra eñ cierre de ña crisis bancaria

                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            //SIEMPRE LAS EMISIONES
                            if (ps.getPredio() != null) {
                                liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                            }
                            if (ps.getPredioRural() != null) {
                                liquidacion = this.getLiquidacionPorPredioRuralAnioTipoEstado(ps.getPredioRural(), i, 7L, 2L);
                            }
//                            if(ps.getPredioRural2017()!=null)
//                                liquidacion=this.getLiquidacionPorPredioRural2017AnioTipoEstado(ps.getPredioRural2017(), i, 7L, 2L);
                            if (liquidacion != null) {
                                this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.FALSE, liquidacion.getEstadoLiquidacion().getId());
                                exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, null, solicitud, username));
                            }
                        }
                    }
                    break;
                case 8: //Prescripcion de titulos o dar de baja / Ley Organica pàra eñ cierre de ña crisis bancaria

                    for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                        for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                            //SIEMPRE LAS EMISIONES
                            if (ps.getPredio() != null) {
                                if (validarAplicarExoneracion(ps.getPredio(), null, null, i, 13L, exoneracionTipo.getId())) {
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{i});
                                    salarioMax = salario != null ? salario.getValor().multiply(new BigDecimal(500)) : new BigDecimal("0.00");
                                    avaluoCalculo = this.obtenerValorParaCalculoExoneracion(solicitud, liquidacion.getAvaluoMunicipal(), salarioMax);
                                    liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, liquidacion.getEstadoLiquidacion().getId());
                                    liquidacionExonerada.setTramite(ht);
                                    totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, avaluoCalculo, null, Boolean.TRUE, exoneracionTipo);
                                    liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                    liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                    liquidacionExonerada.setSaldo(totalPagoExneracion);
                                    liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                    manager.persist(liquidacionExonerada);
                                    exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                }
                            }

                        }
                    }
                    break;
                case 9: //Prescripcion de titulos o dar de baja / Ley Organica pàra eñ cierre de ña crisis bancaria

                    if (exoneracionTipo.getId() == 39L) {
                        for (CatPredio predioTemp : predios) {
                            clase8_and_tipo38_or_clase4_or_clase9_tipo39(predioTemp, anioInicio, anioDif, ht, solicitud, exoneraciones, username, null);
                        }
                    }
                    ////BAJA DE TITULOS
                    if (exoneracionTipo.getId() == 40L) {
                        for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                            for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                //SIEMPRE LAS EMISIONES
                                if (ps.getPredio() != null) {
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                }
                                if (ps.getPredioRural() != null) {
                                    liquidacion = this.getLiquidacionPorPredioRuralAnioTipoEstado(ps.getPredioRural(), i, 7L, 2L);
                                }
//                                if(ps.getPredioRural2017()!=null)
//                                    liquidacion=this.getLiquidacionPorPredioRural2017AnioTipoEstado(ps.getPredioRural2017(), i, 7L, 2L);
                                if (liquidacion != null) {
                                    this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.FALSE, liquidacion.getEstadoLiquidacion().getId());//this.clonarRenLiquidacion(liquidacion, solicitud,Boolean.FALSE);
                                    exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, null, solicitud, username));
                                }

                            }
                        }
                    }
                    ////EMISION PREDIAL 
                    if (exoneracionTipo.getId() == 41L) {
                        for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                            for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                //SIEMPRE LAS EMISIONES
                                if (ps.getPredio() != null) {

                                    //GENERA UNA NUEVA EMISION 
                                    liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                    if (liquidacion == null) {
                                        List<CatPredio> catPredioToAval = new ArrayList();
                                        catPredioToAval.add(ps.getPredio());
                                        //avaluos.generateEmisionPredial(catPredioToAval, i, i, false, username);
                                        avaluos.registrarLiquidacion(ps.getPredio(), i, username);
                                        liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(ps.getPredio(), i, 13L, 2L);
                                        exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(null, liquidacion, solicitud, username));

                                    }
                                }

                            }
                        }
                    }
                    break;
                case 10: // REACTIVA UNA EMISIÓN
                    //PAGO INDEBIDO
                    if (exoneracionTipo.getId() == 42L) {
                        for (FnSolicitudExoneracionPredios ps : prediosSolicitud) {
                            for (int i = solicitud.getAnioInicio(); i <= solicitud.getAnioFin(); i++) {
                                if (ps.getPredio() != null) {
                                    liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstadoOrderDate, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{ps.getPredio(), i, 13L, 1L});
                                    if (liquidacion != null) {
                                        liquidacionExonerada = this.clonarRenLiquidacion(liquidacion, solicitud, Boolean.TRUE, 2L);
                                        liquidacionExonerada.setTramite(ht);
                                        totalPagoExneracion = this.generarDetalleRenLiquidacionObtenerTotal(liquidacion, liquidacionExonerada, null, null, Boolean.FALSE, null);
                                        liquidacionExonerada.setEstaExonerado(Boolean.TRUE);
                                        liquidacionExonerada.setTotalPago(totalPagoExneracion);
                                        liquidacionExonerada.setSaldo(totalPagoExneracion);
                                        liquidacionExonerada.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                                        manager.persist(liquidacionExonerada);
                                        exoneraciones.add(this.registrarRelacionLiquidacionDadaBajaExoneracion(liquidacion, liquidacionExonerada, solicitud, username));
                                    }
                                }
                            }
                        }
                    }
                    break;
            }

            solicitud.setEstado((FnEstadoExoneracion) manager.find(FnEstadoExoneracion.class, 1L));
            manager.persist(solicitud);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }
        return exoneraciones;
    }

    public List<CatPredio> exoneracionA500Remuneraciones(BigDecimal salarioMax, List<CatPredio> prediosUrbanos) {
        List<CatPredio> prediosUrbanosExoneracion = new ArrayList();
        BigDecimal totalesAvaluos = BigDecimal.ZERO;
        for (CatPredio prediosUrbano : prediosUrbanos) {
            totalesAvaluos = totalesAvaluos.add(prediosUrbano.getAvaluoMunicipal());
        }
        int res = totalesAvaluos.compareTo(salarioMax);
        if (res == 1) {
            prediosUrbanosExoneracion = prediosQuePuedenSerExonerados(prediosUrbanos, salarioMax);
            return prediosUrbanosExoneracion;
        } else {

            for (CatPredio prediosUrbano : prediosUrbanos) {
                ///SE LO UTILIZA PARA GUARDAR TEMPORALMENTE EL AVALUO DE LA PROPIEDAD

                prediosUrbano.setAvaluoObraComplement(prediosUrbano.getAvaluoMunicipal());
                prediosUrbano.setAvaluoMunicipal(BigDecimal.ZERO);
                System.out.println("prediosUrbano.setAvaluoMunicipal " + prediosUrbano.getAvaluoMunicipal());
                System.out.println("prediosUrbano.setAvaluoObraComplement " + prediosUrbano.getAvaluoObraComplement());
                prediosUrbanosExoneracion.add(prediosUrbano);
            }
        }

        return prediosUrbanosExoneracion;
    }

    public List<CatPredio> prediosQuePuedenSerExonerados(List<CatPredio> prediosSeleccionados, BigDecimal salarioMax) {
        List<CatPredio> prediosUrbanosAExonerar = new ArrayList<>();
        BigDecimal totalesAvaluos = BigDecimal.ZERO;
        Collections.sort(prediosSeleccionados, (CatPredio p1, CatPredio p2)
                -> new Integer(p1.getAvaluoMunicipal().toBigInteger().intValue())
                        .compareTo(p2.getAvaluoMunicipal().toBigInteger().intValue()));
        prediosUrbanosAExonerar = new ArrayList();
        for (CatPredio cp : prediosSeleccionados) {
            if (cp.getAvaluoMunicipal().compareTo(salarioMax) == 1) {
                cp.setAvaluoMunicipal(cp.getAvaluoMunicipal().subtract(salarioMax));
                prediosUrbanosAExonerar.add(cp);
                break;
            } else {
                salarioMax = salarioMax.subtract(cp.getAvaluoMunicipal());
                cp.setAvaluoObraComplement(cp.getAvaluoMunicipal());
                cp.setAvaluoMunicipal(BigDecimal.ZERO);
                System.out.println("prediosUrbano.setAvaluoMunicipal " + cp.getAvaluoMunicipal());
                System.out.println("prediosUrbano.setAvaluoObraComplement " + cp.getAvaluoObraComplement());
                prediosUrbanosAExonerar.add(cp);
            }

        }
        return prediosUrbanosAExonerar;
    }

    public BigDecimal generarDetalleRenLiquidacionObtenerTotal(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, BigDecimal valorCalculado, FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionExoneracion, Boolean realizarCalculo, FnExoneracionTipo tipoExoneracion) {
        BigDecimal totalPagoExoneracion = new BigDecimal("0.00");
        //BigDecimal totalAvaluo = new BigDecimal("0.00");
        RenDetLiquidacion detalleExonerado;
        MejDetRubroMejoras mejoraExonerada;

        System.out.println("valorCalculado: " + valorCalculado);
        try {
            for (RenDetLiquidacion dl : liquidacionOriginal.getRenDetLiquidacionCollection()) {
                detalleExonerado = new RenDetLiquidacion();
                detalleExonerado.setLiquidacion(liquidacionPosterior);
                detalleExonerado.setRubro(dl.getRubro());
                detalleExonerado.setValor(dl.getValor());

                if (tipoExoneracion != null && (tipoExoneracion.getId() == 8L || tipoExoneracion.getId() == 9L
                        || tipoExoneracion.getId() == 10L || tipoExoneracion.getId() == 11L || tipoExoneracion.getId() == 12L
                        || tipoExoneracion.getId() == 14L || tipoExoneracion.getId() == 16L || tipoExoneracion.getId() == 17L
                        || tipoExoneracion.getId() == 25L || tipoExoneracion.getId() == 28L
                        || tipoExoneracion.getId() == 36L || tipoExoneracion.getId() == 44L || tipoExoneracion.getId() == 29L
                        || tipoExoneracion.getId() == 35L
                        || tipoExoneracion.getId() == 15L || tipoExoneracion.getId() == 18L || tipoExoneracion.getId() == 19L
                        || tipoExoneracion.getId() == 27L || tipoExoneracion.getId() == 37L
                        || tipoExoneracion.getId() == 38L || tipoExoneracion.getId() == 39L)) {
                    if (dl.getRubro() == 10L) {
                        detalleExonerado.setValor(valorCalculado.multiply(new BigDecimal(0.002)));
                    }
                }

                //PARA EMISIONES URBANAS  BANDA IMPOSITIVA SAN VICENTE 1 x 1000
                if (realizarCalculo && (dl.getRubro() == 2L)) {

                    if (valorCalculado.compareTo(BigDecimal.ZERO) == 0) {
                        detalleExonerado.setValor(BigDecimal.ZERO);
                    } else {
                        detalleExonerado.setValor(getValorImpuesto(valorCalculado, liquidacionOriginal.getAnio()));
                    }
                }

                if (tipoExoneracion != null && (tipoExoneracion.getId() == 37L || tipoExoneracion.getId() == 44L)) {
                    ////COBRO DEL 50% DEL IMPUESTO PREDIAL URBANO SOBRE LA DISCAPACIDAD
                    if (realizarCalculo && (dl.getRubro() == 2L)) {
                        if (valorCalculado.compareTo(BigDecimal.ZERO) == 0) {
                            detalleExonerado.setValor(dl.getValor().divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            detalleExonerado.setValor(getValorImpuesto(valorCalculado, liquidacionOriginal.getAnio()));
                            detalleExonerado.setValor(detalleExonerado.getValor().divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP));
                        }

                    }
                }

                if (tipoExoneracion != null && (tipoExoneracion.getId() == 6L || tipoExoneracion.getId() == 30L
                        || tipoExoneracion.getId() == 32L)) {
                    if (dl.getRubro() == 4L) {
                        detalleExonerado.setValor(new BigDecimal(0.00));
                    }
                }
                if (tipoLiquidacionExoneracion != null && tipoLiquidacionExoneracion.getRubro().getId().equals(dl.getRubro())) {
                    detalleExonerado.setValor(valorCalculado);
                }

                detalleExonerado = (RenDetLiquidacion) manager.persist(detalleExonerado);

                totalPagoExoneracion = totalPagoExoneracion.add(detalleExonerado.getValor());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
        }
        return totalPagoExoneracion;
    }

    public BigDecimal generarDetalleRenLiquidacionObtenerTotal(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, BigDecimal valorCalculado, FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionExoneracion, Boolean realizarCalculo, FnExoneracionTipo tipoExoneracion, FnSolicitudExoneracion solicitudExoneracion, BigDecimal valorLeyAncianoPorcentaje) {
        BigDecimal totalPagoExoneracion = new BigDecimal("0.00");
        //BigDecimal totalAvaluo = new BigDecimal("0.00");
        RenDetLiquidacion detalleExonerado;
        MejDetRubroMejoras mejoraExonerada;
        System.out.println("valorCalculado: " + valorCalculado);
        try {
            for (RenDetLiquidacion dl : liquidacionOriginal.getRenDetLiquidacionCollection()) {
                detalleExonerado = new RenDetLiquidacion();
                detalleExonerado.setLiquidacion(liquidacionPosterior);
                detalleExonerado.setRubro(dl.getRubro());
                detalleExonerado.setValor(dl.getValor());
                //PARA EMISIONES URBANAS - RURALES  BANDA IMPOSITIVA SAN VICENTE 1 x 1000
                if (realizarCalculo && (dl.getRubro() == 2L || dl.getRubro() == 18L || dl.getRubro() == 17L)) {
                    if (solicitudExoneracion.getValor() == null) {
                        solicitudExoneracion.setValor(new BigDecimal("50"));
                    }
                    ///LEY DEL ANCIANO PORCENTAJE
                    if (tipoExoneracion.getId() == 18L) {
                        if (valorCalculado == null) {
                            valorCalculado = valorLeyAncianoPorcentaje;
                        }
                        if (valorCalculado.equals(BigDecimal.ZERO)) {
                            valorCalculado = valorLeyAncianoPorcentaje;
                        }
                        detalleExonerado.setValor(getValorImpuesto(valorCalculado, liquidacionOriginal.getAnio()));
                        detalleExonerado.setValor(detalleExonerado.getValor().multiply(solicitudExoneracion.getValor()).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                    } else {
                        detalleExonerado.setValor(valorCalculado.multiply(liquidacionOriginal.getPredio().getBaseImponible()).divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP));
                    }

                }
                if (tipoExoneracion != null && (tipoExoneracion.getId() == 8L || tipoExoneracion.getId() == 9L
                        || tipoExoneracion.getId() == 10L || tipoExoneracion.getId() == 11L || tipoExoneracion.getId() == 12L
                        || tipoExoneracion.getId() == 14L || tipoExoneracion.getId() == 16L || tipoExoneracion.getId() == 17L
                        || tipoExoneracion.getId() == 25L || tipoExoneracion.getId() == 28L
                        || tipoExoneracion.getId() == 36L || tipoExoneracion.getId() == 44L || tipoExoneracion.getId() == 29L
                        || tipoExoneracion.getId() == 35L
                        || tipoExoneracion.getId() == 15L || tipoExoneracion.getId() == 18L || tipoExoneracion.getId() == 19L
                        || tipoExoneracion.getId() == 27L || tipoExoneracion.getId() == 37L
                        || tipoExoneracion.getId() == 38L || tipoExoneracion.getId() == 39L)) {
                    if (dl.getRubro() == 10L) {
                        detalleExonerado.setValor(valorCalculado.multiply(new BigDecimal(0.002)));
                    }
                }

                if (tipoExoneracion != null && (tipoExoneracion.getId() == 37L || tipoExoneracion.getId() == 44L)) {
                    ////COBRO DEL 50% DEL IMPUESTO PREDIAL URBANO SOBRE LA DISCAPACIDAD
                    if (realizarCalculo && (dl.getRubro() == 2L)) {
                        if (valorCalculado.compareTo(BigDecimal.ZERO) == 0) {
                            detalleExonerado.setValor(dl.getValor().divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            detalleExonerado.setValor(getValorImpuesto(valorCalculado, liquidacionOriginal.getAnio()));
                            detalleExonerado.setValor(detalleExonerado.getValor().divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP));
                        }

                    }
                }

                if (tipoExoneracion != null && (tipoExoneracion.getId() == 43L)) {
                    detalleExonerado.setValor(new BigDecimal(0.00));
                }
                if (tipoExoneracion != null && (tipoExoneracion.getId() == 6L || tipoExoneracion.getId() == 30L
                        || tipoExoneracion.getId() == 32L)) {
                    if (dl.getRubro() == 4L) {
                        detalleExonerado.setValor(new BigDecimal(0.00));
                    }
                }
                if (tipoLiquidacionExoneracion != null && tipoLiquidacionExoneracion.getRubro().getId().equals(dl.getRubro())) {
                    detalleExonerado.setValor(valorCalculado);
                }
                detalleExonerado = (RenDetLiquidacion) manager.persist(detalleExonerado);

                totalPagoExoneracion = totalPagoExoneracion.add(detalleExonerado.getValor());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
        }
        return totalPagoExoneracion;
    }

    public Boolean validarAplicarExoneracion(CatPredio predio, CatPredioRustico predioRustico, EmisionesRuralesExcel predioRustico2017, Integer anio, Long tipoLiquidacion) {
        RenLiquidacion liquidacionDadaBaja = null;
        RenLiquidacion liquidacion = null;
        if (predio != null) {
            //liquidacionDadaBaja = this.getLiquidacionPorPredioAnioTipoEstado(predio, anio, tipoLiquidacion, 4L);
            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(predio, anio, tipoLiquidacion, 2L);
        }
        if (predioRustico != null) {
            liquidacionDadaBaja = this.getLiquidacionPorPredioRuralAnioTipoEstado(predioRustico, anio, tipoLiquidacion, 4L);
            liquidacion = this.getLiquidacionPorPredioRuralAnioTipoEstado(predioRustico, anio, tipoLiquidacion, 2L);
        }
        if (predioRustico2017 != null) {
            liquidacionDadaBaja = this.getLiquidacionPorPredioRural2017AnioTipoEstado(predioRustico2017, anio, tipoLiquidacion, 4L);
            liquidacion = this.getLiquidacionPorPredioRural2017AnioTipoEstado(predioRustico2017, anio, tipoLiquidacion, 2L);
        }
        return liquidacionDadaBaja == null && liquidacion != null;
    }

    public RenLiquidacion getLiquidacionPorPredioAnioTipoEstado(CatPredio predio, Integer anio, Long tipoLiquidacion, Long estado) {
        return (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predio, anio, tipoLiquidacion, estado});
    }

    public RenLiquidacion getLiquidacionPorPredioRuralAnioTipoEstado(CatPredioRustico predioRustico, Integer anio, Long tipoLiquidacion, Long estado) {
        return (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesRuralByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predioRustico, anio, tipoLiquidacion, estado});
    }

    public RenLiquidacion getLiquidacionPorPredioRural2017AnioTipoEstado(EmisionesRuralesExcel predioRustico2017, Integer anio, Long tipoLiquidacion, Long estado) {
        return (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesRural2017ByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predioRustico2017, anio, tipoLiquidacion, estado});
    }

    public List<RenDetLiquidacion> getValorEmision(CatPredio predio, Integer anio, RenTipoLiquidacion tipoLiquidacion) {
        try {
            Map<String, Object> paramt;

            List<RenDetLiquidacion> detalleLiquidacion = new ArrayList();

            RenDetLiquidacion detalle;

            RenDetLiquidacion detLiquidacion;
            for (RenRubrosLiquidacion rubros : tipoLiquidacion.getRenRubrosLiquidacionCollection()) {

                detalle = new RenDetLiquidacion();
                detalle.setRubro(rubros.getId());
                detalle.setEstado(true);

                switch (rubros.getCodigoRubro().intValue()) {

                    case 1: //IMPUESTO PREDIAL
                        detalle.setValor(predio.getAvaluoMunicipal().multiply(new BigDecimal("1")).divide(new BigDecimal("1000")));
                        break;
                    case 2: //SERVICIOS ADMINISTRATIVOS
                        detalle.setValor(new BigDecimal("5"));
                        break;

                    case 3: //IMP. A LOS INMUEBLES NO EDIFICADOS
                        if (predio.getAvaluoConstruccion().compareTo(BigDecimal.ZERO) == 0) {
                            System.out.println("SOLAR VACIO");
                            detalle.setValor(predio.getAvaluoMunicipal().multiply(new BigDecimal("2")).divide(new BigDecimal("1000")));
                        }
                        break;

                    case 6: //BOMBEROS
                        detalle.setValor(predio.getAvaluoMunicipal().multiply(new BigDecimal("0.15")).divide(new BigDecimal("1000")));
                        break;

                    case 8: //CEM-PARQUES Y PLAZAS
                        detLiquidacion = new RenDetLiquidacion();
                        paramt = new HashMap<>();
                        paramt.put("idPredio", predio.getId());
                        paramt.put("anio", anio);
                        paramt.put("idDetLiquidacion", rubros.getId());
                        detLiquidacion = (RenDetLiquidacion) manager.findObjectByParameter(Querys.emisionExistenteBaja, paramt);
                        if (detLiquidacion != null) {
                            if (detLiquidacion.getId() != null) {
                                if (detLiquidacion.getValor() != null) {
                                    detalle.setValor(detLiquidacion.getValor());
                                }
                            }
                        }
                        break;

                    case 9: //CEM-ALCANTARILLADOS Y VIAS
                        detLiquidacion = new RenDetLiquidacion();
                        paramt = new HashMap<>();
                        paramt.put("idPredio", predio.getId());
                        paramt.put("anio", anio);
                        paramt.put("idDetLiquidacion", rubros.getId());
                        detLiquidacion = (RenDetLiquidacion) manager.findObjectByParameter(Querys.emisionExistenteBaja, paramt);
                        if (detLiquidacion != null) {
                            if (detLiquidacion.getId() != null) {
                                if (detLiquidacion.getValor() != null) {
                                    detalle.setValor(detLiquidacion.getValor());
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
                if (detalle.getValor() != null && detalle.getValor().compareTo(BigDecimal.ZERO) > 0) {
                    detalleLiquidacion.add(detalle);
                }
            }
            return detalleLiquidacion;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getValorEmision", e);
        }
        return null;
    }

    public RenLiquidacion getExonerarcionBomberos(RenLiquidacion liquidacion) {
        try {
            RenDetLiquidacion detalle;
            int index = 0;
            for (RenDetLiquidacion rubros : liquidacion.getRenDetLiquidacionCollection()) {
                detalle = new RenDetLiquidacion();
                detalle.setRubro(rubros.getId());
                detalle.setEstado(true);
                switch (rubros.getCodigoRubro().intValue()) {
                    case 6: //BOMBEROS
                        liquidacion.getRenDetLiquidacionCollection().remove(index);
                        detalle.setValor(new BigDecimal("0"));
                        liquidacion.getRenDetLiquidacionCollection().add(detalle);
                        break;
                    default:
                        break;
                }
                index = index + 1;
            }
            return liquidacion;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getExonerarcionBomberos", e);
        }
        return null;
    }

    /**
     * Calcula el Valor con el cual se genera La exoneracion dependiendo del
     * Tipo y de SI necesita Validacion de 500 Remuneraciones
     *
     * @param solicitudExoneracion
     * @param valor
     * @param valorLimite
     * @return
     */
    public BigDecimal obtenerValorParaCalculoExoneracion(FnSolicitudExoneracion solicitudExoneracion, BigDecimal valor, BigDecimal valorLimite) {
        BigDecimal valorCalculado = new BigDecimal("0.00");
        BigDecimal valorPorcentaje;
        try {
            if (solicitudExoneracion.getTipoValor() != null) {
                switch (solicitudExoneracion.getTipoValor().getId().intValue()) {
                    case 7://POR VALOR
                        if (solicitudExoneracion != null) {
                            if (solicitudExoneracion.getExoneracionTipo().getValidaRemuneracion() && solicitudExoneracion.getValor().compareTo(valorLimite) > 0) {
                                valorCalculado = valor.subtract(valorLimite);
                            } else {
                                valorCalculado = valor.subtract(solicitudExoneracion.getValor());
                            }
                        }

                        break;

                    case 10://POR PORCENTAJE
                        valorPorcentaje = valor.multiply(solicitudExoneracion.getValor()).divide(new BigDecimal("100"));
                        if (solicitudExoneracion.getExoneracionTipo().getValidaRemuneracion() && valorPorcentaje.compareTo(valorLimite) > 0) {
                            valorCalculado = valor.subtract(valorLimite);
                        } else {
                            valorCalculado = valor.subtract(valorPorcentaje);
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "CALCULO DE AVALUO PARA EXONERACION", e);
        }
        return valorCalculado.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : valorCalculado;
    }

    /**
     *
     * @param liquidacionOriginal
     * @param liquidacionPosterior
     * @param valorCalculado
     * @param tipoLiquidacionExoneracion
     * @param realizarCalculo
     * @return
     */
    public BigDecimal generarDetalleRenLiquidacionObtenerTotal(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, BigDecimal valorCalculado, FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionExoneracion, Boolean realizarCalculo) {
        BigDecimal totalPagoExoneracion = new BigDecimal("0.00");
        RenDetLiquidacion detalleExonerado;
        MejDetRubroMejoras mejoraExonerada;
        try {
            for (RenDetLiquidacion dl : liquidacionOriginal.getRenDetLiquidacionCollection()) {
                detalleExonerado = new RenDetLiquidacion();
                detalleExonerado.setLiquidacion(liquidacionPosterior);
                detalleExonerado.setRubro(dl.getRubro());
                detalleExonerado.setValor(dl.getValor());
                //PARA EMISIONES URBANAS - RURALES
                if (realizarCalculo && (dl.getRubro() == 2L || dl.getRubro() == 18L)) {
                    detalleExonerado.setValor(valorCalculado.multiply(liquidacionPosterior.getBandaImpositiva()).divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP));
                }
                /*
                 if (dl.getRubro() == 10L ) {
                 detalleExonerado.setValor(valorCalculado.multiply(new BigDecimal(0.002)));
                 }
                 */
                if (tipoLiquidacionExoneracion != null && tipoLiquidacionExoneracion.getRubro().getId().equals(dl.getRubro())) {
                    detalleExonerado.setValor(valorCalculado);
                }
                detalleExonerado = (RenDetLiquidacion) manager.persist(detalleExonerado);

                //MEJORAS
                if (detalleExonerado.getRubro() == 7L && dl.getMejDetRubroMejorasCollection() != null && !dl.getMejDetRubroMejorasCollection().isEmpty()) {
                    for (MejDetRubroMejoras rm : dl.getMejDetRubroMejorasCollection()) {
                        mejoraExonerada = new MejDetRubroMejoras();
                        mejoraExonerada.setRubroMejora(detalleExonerado);
                        mejoraExonerada.setFechaIngreso(new Date());
                        mejoraExonerada.setValor(rm.getValor());
                        if (realizarCalculo) {
                            mejoraExonerada.setSaldo(rm.getSaldo());
                        } else {
                            mejoraExonerada.setSaldo(rm.getValor());
                        }
                        mejoraExonerada.setEstado(Boolean.TRUE);
                        mejoraExonerada.setUbicacionObra(rm.getUbicacionObra());
                        manager.persist(mejoraExonerada);
                    }
                }
                totalPagoExoneracion = totalPagoExoneracion.add(detalleExonerado.getValor());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
        }
        return totalPagoExoneracion;
    }

    //ESTE ERA EL PRIMERITO D:
//    public FnExoneracionLiquidacion registrarRelacionLiquidacionDadaBajaExoneracion(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, FnSolicitudExoneracion solicitud, String usuario) {
//        FnExoneracionLiquidacion exoneracionLiquidacion = new FnExoneracionLiquidacion();
//        try {
//            exoneracionLiquidacion.setFechaIngreso(new Date());
//            exoneracionLiquidacion.setLiquidacionOriginal(liquidacionOriginal);
//            exoneracionLiquidacion.setLiquidacionPosterior(liquidacionPosterior);
//            exoneracionLiquidacion.setExoneracion(solicitud);
//            exoneracionLiquidacion.setUsuarioIngreso(usuario);
//            exoneracionLiquidacion.setEstado(Boolean.TRUE);
//            exoneracionLiquidacion.setEsUrbano(Boolean.TRUE);
//            exoneracionLiquidacion = (FnExoneracionLiquidacion) manager.persist(exoneracionLiquidacion);
//        } catch (Exception e) {
//            exoneracionLiquidacion = null;
//            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
//        }
//        return exoneracionLiquidacion;
//    }
    public FnExoneracionLiquidacion registrarRelacionLiquidacionDadaBajaExoneracion(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, FnSolicitudExoneracion solicitud, String usuario) {
        FnExoneracionLiquidacion exoneracionLiquidacion = new FnExoneracionLiquidacion();
        List<CoaJuicioPredio> juicioPredios;
        CoaJuicioPredio jp;
        try {
            exoneracionLiquidacion.setFechaIngreso(new Date());
            exoneracionLiquidacion.setLiquidacionOriginal(liquidacionOriginal);
            exoneracionLiquidacion.setLiquidacionPosterior(liquidacionPosterior);
            exoneracionLiquidacion.setExoneracion(solicitud);
            exoneracionLiquidacion.setUsuarioIngreso(usuario);
            exoneracionLiquidacion.setEstado(Boolean.TRUE);
            exoneracionLiquidacion.setEsUrbano(Boolean.TRUE);
            exoneracionLiquidacion = (FnExoneracionLiquidacion) manager.persist(exoneracionLiquidacion);
            if (liquidacionOriginal != null && liquidacionPosterior != null) {
                juicioPredios = manager.findAll(Querys.getJuicioDetallesByLiquidacion, new String[]{"liquidacion"}, new Object[]{liquidacionOriginal});
                if (juicioPredios != null && !juicioPredios.isEmpty()) {
                    for (CoaJuicioPredio juicioPredio : juicioPredios) {
                        if (juicioPredio.getLiquidacion().getId().equals(liquidacionOriginal.getId())) {
                            jp = new CoaJuicioPredio();
                            jp.setAbogadoJuicio(juicioPredio.getAbogadoJuicio());
                            jp.setAnioDesde(juicioPredio.getAnioDesde());
                            jp.setAnioDeuda(juicioPredio.getAnioDeuda());
                            jp.setAnioHasta(juicioPredio.getAnioHasta());
                            jp.setEstado(Boolean.TRUE);
                            jp.setJuicio(juicioPredio.getJuicio());
                            jp.setLiquidacion(liquidacionPosterior);
                            jp.setObservacion(juicioPredio.getObservacion());
                            jp.setPredio(juicioPredio.getPredio());
                            jp.setValorDeuda(liquidacionPosterior.getTotalPago());
                            manager.persist(jp);
                            juicioPredio.setEstado(Boolean.FALSE);
                            manager.persist(juicioPredio);
                            if (juicioPredio.getJuicio().getTotalDeuda() != null) {
                                juicioPredio.getJuicio().setTotalDeuda(juicioPredio.getJuicio().getTotalDeuda().subtract(liquidacionOriginal.getTotalPago()).add(liquidacionPosterior.getTotalPago()));
                                manager.persist(juicioPredio.getJuicio());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exoneracionLiquidacion = null;
            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
        }
        return exoneracionLiquidacion;
    }

    private FnExoneracionLiquidacion agregarExoneracionLiquidacion(FnSolicitudExoneracion solc, RenLiquidacion liquidAnt, RenLiquidacion liquidPost, String user, Boolean esUrbano) {
        FnExoneracionLiquidacion exLiquid = new FnExoneracionLiquidacion();
        exLiquid.setFechaIngreso(new Date());
        exLiquid.setLiquidacionOriginal(liquidAnt);
        exLiquid.setLiquidacionPosterior(liquidPost);
        exLiquid.setExoneracion(solc);
        exLiquid.setUsuarioIngreso(user);
        exLiquid.setEstado(Boolean.TRUE);
        exLiquid.setEsUrbano(esUrbano);
        return (FnExoneracionLiquidacion) manager.persist(exLiquid);
    }

    private BigDecimal generarRubrosLiquidacion(Collection<RenDetLiquidacion> detalleLiquidacion, RenLiquidacion nuevaLiquid, RenLiquidacion liquidacion, BigDecimal diferencia) {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (RenDetLiquidacion temp : detalleLiquidacion) {
                RenDetLiquidacion nuevoRubro = new RenDetLiquidacion();
                nuevoRubro.setEstado(true);
                nuevoRubro.setLiquidacion(nuevaLiquid);
                nuevoRubro.setRubro(temp.getRubro());
                nuevoRubro.setValorRecaudado(BigDecimal.ZERO);
                if (temp.getRubro() == 2L || temp.getRubro() == 10L) { // RUBROS 1 Y 9 DEL SAC, 2 y 10 de SGM
                    if (temp.getRubro() == 2L) {
                        if (diferencia.compareTo(BigDecimal.ZERO) <= 0) {
                            nuevoRubro.setValor(BigDecimal.ZERO);
                        } else {
                            nuevoRubro.setValor(diferencia.multiply(liquidacion.getBandaImpositiva().divide(new BigDecimal(1000))));
                        }
                    }
                    if (temp.getRubro() == 10L) {
                        if (diferencia.compareTo(BigDecimal.ZERO) <= 0) {
                            nuevoRubro.setValor(BigDecimal.ZERO);
                        } else {
                            nuevoRubro.setValor(diferencia.multiply(new BigDecimal(0.002)));
                        }
                    }
                } else {
                    nuevoRubro.setValor(temp.getValor());
                }
                nuevoRubro = (RenDetLiquidacion) manager.persist(nuevoRubro);
                if (nuevoRubro.getRubro() == 7L) {
                    generarNuevoRubroMejora(temp, nuevoRubro);
                }
                total = total.add(nuevoRubro.getValor());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generar Rubros.", e);
        }
        return total;
    }

    public BigDecimal generarDetalleRenLiquidacionObtenerTotal(RenLiquidacion liquidacionOriginal, RenLiquidacion liquidacionPosterior, BigDecimal valorCalculado, FnSolicitudTipoLiquidacionExoneracion tipoLiquidacionExoneracion) {
        BigDecimal totalPagoExoneracion = new BigDecimal("0.00");
        RenDetLiquidacion detalleExonerado;
        MejDetRubroMejoras mejoraExonerada;
        try {
            for (RenDetLiquidacion dl : liquidacionOriginal.getRenDetLiquidacionCollection()) {
                detalleExonerado = new RenDetLiquidacion();
                detalleExonerado.setLiquidacion(liquidacionPosterior);
                detalleExonerado.setRubro(dl.getRubro());
                detalleExonerado.setValor(dl.getValor());
                //PARA EMISIONES URBANAS - RURALES
                if (dl.getRubro() == 2L || dl.getRubro() == 18L) {
                    detalleExonerado.setValor(valorCalculado.multiply(liquidacionPosterior.getBandaImpositiva()).divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP));
                }
                /*
                 if (dl.getRubro() == 10L ) {
                 detalleExonerado.setValor(valorCalculado.multiply(new BigDecimal(0.002)));
                 }
                 */
                if (tipoLiquidacionExoneracion != null && tipoLiquidacionExoneracion.getRubro().getId().equals(dl.getRubro())) {
                    detalleExonerado.setValor(valorCalculado);
                }
                detalleExonerado = (RenDetLiquidacion) manager.persist(detalleExonerado);

                //MEJORAS
                if (detalleExonerado.getRubro() == 7L && dl.getMejDetRubroMejorasCollection() != null && !dl.getMejDetRubroMejorasCollection().isEmpty()) {
                    for (MejDetRubroMejoras rm : dl.getMejDetRubroMejorasCollection()) {
                        mejoraExonerada = new MejDetRubroMejoras();
                        mejoraExonerada.setRubroMejora(detalleExonerado);
                        mejoraExonerada.setFechaIngreso(new Date());
                        mejoraExonerada.setValor(rm.getValor());
                        mejoraExonerada.setSaldo(rm.getSaldo());
                        mejoraExonerada.setEstado(Boolean.TRUE);
                        mejoraExonerada.setUbicacionObra(rm.getUbicacionObra());
                        manager.persist(mejoraExonerada);
                    }
                }
                totalPagoExoneracion = totalPagoExoneracion.add(detalleExonerado.getValor());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "GENERACION DETALLE EXONERACION", e);
        }
        return totalPagoExoneracion;
    }

    /**
     * La liquidacion Original pasa a estado 4, se crea una nueva liquidacion en
     * caso de ser necesario en base a la original en estado 2 y registra un
     * RenSolicitudesLiquidacion
     *
     * @param liquidacion
     * @param solicitud
     * @param generarPosterior
     * @param estadoLiquidacion
     * @return
     */
    public RenLiquidacion clonarRenLiquidacion(RenLiquidacion liquidacion, FnSolicitudExoneracion solicitud, Boolean generarPosterior, Long estadoLiquidacion) {
        RenLiquidacion liquidacion2 = null;

        try {
            liquidacion.setEstadoLiquidacion((RenEstadoLiquidacion) manager.find(RenEstadoLiquidacion.class, 4L));
            manager.persist(liquidacion);
            if (generarPosterior) {
                liquidacion2 = (RenLiquidacion) EntityBeanCopy.clone(liquidacion);
                liquidacion2.setId(null);
                liquidacion2.setComprador(liquidacion.getComprador());
                liquidacion2.setTramite(liquidacion.getTramite());
                liquidacion2.setTipoLiquidacion(liquidacion.getTipoLiquidacion());
                liquidacion2.setPredio(liquidacion.getPredio());
                liquidacion2.setPredioRustico(liquidacion.getPredioRustico());
                liquidacion2.setRuralExcel(liquidacion.getRuralExcel());
                liquidacion2.setLocalComercial(liquidacion.getLocalComercial());
                liquidacion2.setRenValoresPlusvalia(liquidacion.getRenValoresPlusvalia());
                liquidacion2.setVendedor(liquidacion.getVendedor());
                //liquidacion2.setUsuario(liquidacion.getUsuario());
                liquidacion2.setEstadoLiquidacion(new RenEstadoLiquidacion(estadoLiquidacion));
                liquidacion2.setAvaluoConstruccion(liquidacion.getAvaluoConstruccion());
                liquidacion2.setAvaluoMunicipal(liquidacion.getAvaluoMunicipal());
                liquidacion2.setAvaluoSolar(liquidacion.getAvaluoSolar());
                liquidacion2.setAreaTotal(liquidacion.getAreaTotal());
                liquidacion2.setFechaIngreso(new Date());
                liquidacion2.setEstaExonerado(Boolean.TRUE);
                liquidacion2.setBombero(Boolean.FALSE);
                liquidacion2.setNombreComprador(liquidacion.getNombreComprador());
                liquidacion2.setCoactiva(liquidacion.getCoactiva());
                liquidacion2.setEstadoCoactiva(liquidacion.getEstadoCoactiva());
                //System.out.println("liquidacion2.getRenDetLiquidacionCollection() " + liquidacion2.getRenDetLiquidacionCollection().size());
                //liquidacion2 = getExonerarcionBomberos(liquidacion);
                liquidacion2 = (RenLiquidacion) manager.persist(liquidacion2);
            }
            if (solicitud != null) {
                RenSolicitudesLiquidacion solsLiq = new RenSolicitudesLiquidacion();
                solsLiq.setEstado(Boolean.TRUE);
                solsLiq.setSolExoneracion(solicitud);
                solsLiq.setLiquidacion(liquidacion);
                manager.persist(solsLiq);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }

        return liquidacion2;
    }

    private MejDetRubroMejoras generarNuevoRubroMejora(RenDetLiquidacion temp, RenDetLiquidacion nuevoRubro) {
        try {
            MejDetRubroMejoras mejoraAnt = (MejDetRubroMejoras) manager.find(QuerysFinanciero.getUbicacionByDetLiq, new String[]{"detLiq"}, new Object[]{temp});
            if (mejoraAnt != null) {
                MejDetRubroMejoras rubroMejora = new MejDetRubroMejoras();
                rubroMejora.setRubroMejora(nuevoRubro);
                rubroMejora.setFechaIngreso(new Date());
                rubroMejora.setValor(nuevoRubro.getValor());
                rubroMejora.setSaldo(nuevoRubro.getValor());
                rubroMejora.setEstado(Boolean.TRUE);
                rubroMejora.setUbicacionObra(mejoraAnt.getUbicacionObra());
                manager.persist(rubroMejora);
            }
            return mejoraAnt;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generar Rubro Mejora.", e);
        }
        return null;
    }

    public void clase7_or_clase9_tipo40_or_clase8_tipo43(CatPredio predio, Integer anioInicio, Integer anioDif, HistoricoTramites ht, FnSolicitudExoneracion solicitud, List<FnExoneracionLiquidacion> exoneraciones, String username, CatPredioRustico rustico) {
        FnExoneracionLiquidacion tablaIntermedia;
        RenLiquidacion liquidacion = null, liquidacion2;

        for (int i = 0; i <= anioDif; i++) {
            liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predio, anioInicio, new Long(13L), new Long(2L)});
            liquidacion2 = (RenLiquidacion) manager.find(RenLiquidacion.class, 797057L);
            if (liquidacion != null && liquidacion2 != null) {
                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(4L));
                manager.persist(liquidacion);

                tablaIntermedia = new FnExoneracionLiquidacion();
                tablaIntermedia.setFechaIngreso(new Date());
                tablaIntermedia.setLiquidacionOriginal(liquidacion);
                tablaIntermedia.setLiquidacionPosterior(liquidacion2);
                tablaIntermedia.setExoneracion(solicitud);
                tablaIntermedia.setUsuarioIngreso(username);
                tablaIntermedia.setEstado(Boolean.TRUE);
                tablaIntermedia.setEsUrbano(Boolean.TRUE);
                tablaIntermedia = (FnExoneracionLiquidacion) manager.persist(tablaIntermedia);
                exoneraciones.add(tablaIntermedia);
            }
        }
    }

    public void clase8_and_tipo38_or_clase4_or_clase9_tipo39(CatPredio predio, Integer anioInicio, Integer anioDif, HistoricoTramites ht, FnSolicitudExoneracion solicitud, List<FnExoneracionLiquidacion> exoneraciones, String username, CatPredioRustico rustico) {
        GroovyUtil gutil;
        List<RenDetLiquidacion> detalleLiquidacion;
        MatFormulaTramite formula;
        formula = (MatFormulaTramite) manager.find(MatFormulaTramite.class, 33L);
        gutil = new GroovyUtil(formula.getFormula());
        RenLiquidacion liquidacion, liquidacion2 = null;
        RenDetLiquidacion nuevoRubro;
        BigDecimal total = BigDecimal.ZERO;
        CatPredio predioRaiz;
        FnExoneracionLiquidacion tablaIntermedia;
        MejDetRubroMejoras mejoraEncontrada, rubroMejora;

        for (int i = 0; i <= anioDif; i++) {
            total = BigDecimal.ZERO;
            liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predio, anioInicio, new Long(13L), new Long(4L)});
            if (liquidacion == null) {
                liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predio, anioInicio, new Long(13L), new Long(2L)});
                if (liquidacion != null) {
                    liquidacion2 = this.clonarRenLiquidacion(liquidacion, solicitud);
                    liquidacion2.setTramite(ht);
                    liquidacion2.setObservacion("Tiene una exoneración de: " + solicitud.getExoneracionTipo().getDescripcion().toUpperCase()
                            + "\nNúmero de resolución: " + solicitud.getNumResolucionSac());
                    if (anioInicio >= 2014) {
                        // EJECUTA UN MÉTODO QUE TODAVÍA NO ESTÁ CREADO
                        // CEM.dbo.LM_INGRESO_EGRESOS_RUBROS_MEJORAS
                        // HENRY ME DIJO QUE LA FUNCIÓN NO HACE NADA
                    }

                    for (RenDetLiquidacion temp : liquidacion.getRenDetLiquidacionCollection()) {
                        nuevoRubro = new RenDetLiquidacion();
                        nuevoRubro.setEstado(true);
                        nuevoRubro.setLiquidacion(liquidacion2);
                        nuevoRubro.setRubro(temp.getRubro());
                        nuevoRubro.setValor(temp.getValor());
                        nuevoRubro.setValorRecaudado(BigDecimal.ZERO);
                        nuevoRubro = (RenDetLiquidacion) manager.persist(nuevoRubro);
                        if (nuevoRubro.getRubro() == 7L) {
                            mejoraEncontrada = (MejDetRubroMejoras) manager.find(QuerysFinanciero.getUbicacionByDetLiq, new String[]{"detLiq"}, new Object[]{temp});
                            if (mejoraEncontrada != null) {
                                rubroMejora = new MejDetRubroMejoras();
                                rubroMejora.setRubroMejora(nuevoRubro);
                                rubroMejora.setFechaIngreso(new Date());
                                rubroMejora.setValor(nuevoRubro.getValor());
                                rubroMejora.setSaldo(nuevoRubro.getValor());
                                rubroMejora.setEstado(Boolean.TRUE);
                                rubroMejora.setUbicacionObra(mejoraEncontrada.getUbicacionObra());
                                manager.persist(rubroMejora);
                            }
                        }

                        total = total.add(nuevoRubro.getValor());
                    }
                    liquidacion2.setEstaExonerado(Boolean.TRUE);
                    liquidacion2.setTotalPago(total);
                    liquidacion2.setSaldo(total);
                    manager.persist(liquidacion2);

                    tablaIntermedia = new FnExoneracionLiquidacion();
                    tablaIntermedia.setFechaIngreso(new Date());
                    tablaIntermedia.setLiquidacionOriginal(liquidacion);
                    tablaIntermedia.setLiquidacionPosterior(liquidacion2);
                    tablaIntermedia.setExoneracion(solicitud);
                    tablaIntermedia.setUsuarioIngreso(username);
                    tablaIntermedia.setEstado(Boolean.TRUE);
                    tablaIntermedia.setEsUrbano(Boolean.TRUE);
                    tablaIntermedia = (FnExoneracionLiquidacion) manager.persist(tablaIntermedia);
                    exoneraciones.add(tablaIntermedia);
                }
            }
            anioInicio++;

        }

        // PREDIO RUSTICO - EMISION (FALTA)
        if (rustico != null) {
            for (int i = 0; i <= anioDif; i++) {
                total = BigDecimal.ZERO;
                liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{rustico, anioInicio, new Long(7L), new Long(4L)});

                if (liquidacion == null) {
                    liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{rustico, anioInicio, new Long(7L), new Long(2L)});
                    if (liquidacion != null) {
                        liquidacion2 = this.clonarRenLiquidacion(liquidacion, solicitud);
                        liquidacion2.setTramite(ht);

                        for (RenDetLiquidacion temp : liquidacion.getRenDetLiquidacionCollection()) {
                            nuevoRubro = new RenDetLiquidacion();
                            nuevoRubro.setEstado(true);
                            nuevoRubro.setLiquidacion(liquidacion2);
                            nuevoRubro.setRubro(temp.getRubro());
                            nuevoRubro.setValorRecaudado(BigDecimal.ZERO);

                            if (temp.getRubro() == 18L) { // RUBROS 1 DEL SAC, 18 de SGM
                                nuevoRubro.setValor(BigDecimal.ZERO);
                            } else {
                                total = total.add(temp.getValor());
                                nuevoRubro.setValor(temp.getValor());
                            }
                            manager.persist(nuevoRubro);
                        }
                        liquidacion2.setEstaExonerado(Boolean.TRUE);
                        liquidacion2.setTotalPago(total);
                        liquidacion2.setSaldo(total);
                        manager.persist(liquidacion2);

                        tablaIntermedia = new FnExoneracionLiquidacion();
                        tablaIntermedia.setFechaIngreso(new Date());
                        tablaIntermedia.setLiquidacionOriginal(liquidacion);
                        tablaIntermedia.setLiquidacionPosterior(liquidacion2);
                        tablaIntermedia.setExoneracion(solicitud);
                        tablaIntermedia.setUsuarioIngreso(username);
                        tablaIntermedia.setEstado(Boolean.TRUE);
                        tablaIntermedia.setEsUrbano(Boolean.FALSE);
                        tablaIntermedia = (FnExoneracionLiquidacion) manager.persist(tablaIntermedia);
                        exoneraciones.add(tablaIntermedia);
                    }
                }

                anioInicio++;

            }
        }
    }

    public RenLiquidacion clonarRenLiquidacion(RenLiquidacion liquidacion, FnSolicitudExoneracion solicitud) {
        RenLiquidacion liquidacion2;
        RenEstadoLiquidacion estado;

        try {
            estado = liquidacion.getEstadoLiquidacion();
            liquidacion.setEstadoLiquidacion((RenEstadoLiquidacion) manager.find(RenEstadoLiquidacion.class, 4L));
            manager.persist(liquidacion);
            liquidacion2 = (RenLiquidacion) EntityBeanCopy.clone(liquidacion);
            liquidacion2.setId(null);
            liquidacion2.setComprador(liquidacion.getComprador());
            liquidacion2.setTramite(liquidacion.getTramite());
            liquidacion2.setTipoLiquidacion(liquidacion.getTipoLiquidacion());
            liquidacion2.setPredio(liquidacion.getPredio());
            liquidacion2.setLocalComercial(liquidacion.getLocalComercial());
            liquidacion2.setRenValoresPlusvalia(liquidacion.getRenValoresPlusvalia());
            liquidacion2.setVendedor(liquidacion.getVendedor());
            liquidacion2.setPredioRustico(liquidacion.getPredioRustico());
            //liquidacion2.setUsuario(liquidacion.getUsuario());
            liquidacion2.setEstadoLiquidacion(estado);
            liquidacion2.setAvaluoConstruccion(liquidacion.getAvaluoConstruccion());
            liquidacion2.setAvaluoMunicipal(liquidacion.getAvaluoMunicipal());
            liquidacion2.setAvaluoSolar(liquidacion.getAvaluoSolar());
            liquidacion2.setAreaTotal(liquidacion.getAreaTotal());
            liquidacion2.setFechaIngreso(new Date());
            liquidacion2.setEstaExonerado(Boolean.TRUE);
            liquidacion2.setBombero(Boolean.FALSE);
            liquidacion2.setNombreComprador(liquidacion.getNombreComprador());
            liquidacion2 = (RenLiquidacion) manager.persist(liquidacion2);

            if (solicitud != null) {
                RenSolicitudesLiquidacion solsLiq = new RenSolicitudesLiquidacion();
                solsLiq.setEstado(Boolean.TRUE);
                solsLiq.setSolExoneracion(solicitud);
                solsLiq.setLiquidacion(liquidacion);
                manager.persist(solsLiq);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }

        return liquidacion2;
    }

    public Boolean validarAplicarExoneracion(CatPredio predio, CatPredioRustico predioRustico, EmisionesRuralesExcel predioRustico2017, Integer anio, Long tipoLiquidacion, Long tipoExoneracion) {
        RenLiquidacion liquidacionDadaBaja = null;
        RenLiquidacion liquidacion = null;
        Boolean validador = Boolean.FALSE;
        if (predio != null) {
            //liquidacionDadaBaja = this.getLiquidacionPorPredioAnioTipoEstado(predio, anio, tipoLiquidacion, 4L);
            System.out.println("liquidacionDadaBaja" + liquidacionDadaBaja);
            liquidacion = this.getLiquidacionPorPredioAnioTipoEstado(predio, anio, tipoLiquidacion, 2L);
            System.out.println("liquidacion" + liquidacion);
        }
        if (predioRustico != null) {
            liquidacionDadaBaja = this.getLiquidacionPorPredioRuralAnioTipoEstado(predioRustico, anio, tipoLiquidacion, 4L);
            liquidacion = this.getLiquidacionPorPredioRuralAnioTipoEstado(predioRustico, anio, tipoLiquidacion, 2L);
        }
        if (predioRustico2017 != null) {
            liquidacionDadaBaja = this.getLiquidacionPorPredioRural2017AnioTipoEstado(predioRustico2017, anio, tipoLiquidacion, 4L);
            liquidacion = this.getLiquidacionPorPredioRural2017AnioTipoEstado(predioRustico2017, anio, tipoLiquidacion, 2L);
        }
        if (liquidacionDadaBaja != null && liquidacionDadaBaja.getExoneracionLiquidacionCollection() != null && !liquidacionDadaBaja.getExoneracionLiquidacionCollection().isEmpty()) {
            for (FnExoneracionLiquidacion exL : liquidacionDadaBaja.getExoneracionLiquidacionCollection()) {
                if (exL.getExoneracion() != null && exL.getExoneracion().getExoneracionTipo() != null && exL.getExoneracion().getExoneracionTipo().getId().equals(tipoExoneracion)) {
                    validador = Boolean.TRUE;
                    break;
                }
            }
            if (!validador) {
                liquidacionDadaBaja = null;
            }
        }
        return liquidacionDadaBaja == null && liquidacion != null;
    }

    public BigDecimal buscarValorOtrasLiquidacionesDelPropietario(CatEnte propietario) {
        CatPredio predio;
        List<BigDecimal> valores;
        BigDecimal valorTotal = BigDecimal.ZERO;

        try {
            for (CatPredioPropietario temp : propietario.getCatPredioPropietarioCollection()) {
                predio = temp.getPredio();
                valores = (List) manager.find(QuerysFinanciero.getRenLiquidacionesByPredioYTipoLiquidacion, new String[]{"predio", "idTipoLiquidacion"}, new Object[]{predio, new Long(13L)});
                for (BigDecimal valor : valores) {
                    valorTotal = valorTotal.add(valor);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }

        return valorTotal;
    }

    @Override
    public Boolean rechazarExoneracion(HistoricoTramites ht) {
        Boolean b;
        try {
            b = true;
            if (ht == null) {
                return false;
            }
            FnSolicitudExoneracion solicitud = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getNumSolicitudExoneracion, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
            if (solicitud == null) {
                return false;
            }
            solicitud.setEstado((FnEstadoExoneracion) manager.find(FnEstadoExoneracion.class, 4L));
        } catch (Exception e) {
            b = false;
            LOG.log(Level.SEVERE, "", e);
        }

        return b;
    }

    @Override
    public Boolean corregirExoneracion(HistoricoTramites ht) {
        Boolean b;
        try {
            b = true;
            if (ht == null) {
                return false;
            }
            FnSolicitudExoneracion solicitud = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getNumSolicitudExoneracion, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
            if (solicitud == null) {
                return false;
            }
            solicitud.setEstado((FnEstadoExoneracion) manager.find(FnEstadoExoneracion.class, 1L));
        } catch (Exception e) {
            b = false;
            LOG.log(Level.SEVERE, "", e);
        }

        return b;
    }

    @Override
    public FnSolicitudExoneracion getSolicitudExoneracion(HistoricoTramites ht) {
        Map<String, Object> par = new HashMap<>();
        par.put("tramite", ht);
        return manager.findObjectByParameter(FnSolicitudExoneracion.class, par);
    }

    @Override
    public Observaciones guardarObservacionCraerResolucion(FnSolicitudExoneracion sol, FnResolucion res, Observaciones obs) {
        try {
            if (res != null) {
//                res.setSolicitudExoneracion(sol);
                res = (FnResolucion) manager.persist(res);
                sol.setResolucion(res);
            }
            manager.persist(sol);
            obs = (Observaciones) manager.persist(obs);
            return obs;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ACTUALIZAR SOLICITUD Y RESOLUCION", e);
        }
        return null;
    }

    @Override
    public List<RenLiquidacion> getPagosPredio(CatPredio predio, FnSolicitudExoneracion sol, Boolean esUrbano) {
        List<RenLiquidacion> result = new ArrayList<>();
        try {
            Map<String, Object> par = new HashMap<>();
            par.put("predio", predio);
//                par.put("estado", 1L);
            par.put("anioInicio", sol.getAnioInicio());
            par.put("anioFin", sol.getAnioFin());
            par.put("codigoTitulo", 13L);
            List<RenLiquidacion> l = manager.findListNamedQuery("RenLiquidacion.findPagoPredio", par);
            for (RenLiquidacion r : l) {
                if (r.getRenPagoCollection() != null && r.getRenPagoCollection().size() > 0) {
                    fuera:
                    for (RenPago p : r.getRenPagoCollection()) {
                        if (p.getEstado()) {
                            result.add(r);
                            break fuera;
                        }
                    }
                } else if (r.getEstadoLiquidacion() != null) {
                    if (r.getEstadoLiquidacion().getId().compareTo(1L) == 0) {
                        result.add(r);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            LOG.log(Level.OFF, "", e);
        }
        return null;
    }

    @Override
    public List<FnSolicitudExoneracion> verficarSolicitudExoneracionRust(FnExoneracionTipo exoneracionTipo, Integer anioInicio, Integer anioFin, CatPredioRustico predioRustico) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("predioRustico", predioRustico);
            map.put("exoneracionTipo", exoneracionTipo);
            map.put("anioInicio", anioInicio);
            map.put("anioFin", anioFin);
            map.put("estado", 2L);
            return manager.findListNamedQuery("FnSolicitudExoneracion.findSolicitudRust", map);
        } catch (Exception e) {
            LOG.log(Level.OFF, "", e);
        }
        return null;
    }

    @Override
    public List<RenLiquidacion> getPagosPredioRusticos(CatPredioRustico predioRustico, FnSolicitudExoneracion sol) {
        List<RenLiquidacion> result = new ArrayList<>();
        try {
            Map<String, Object> par = new HashMap<>();
            par.put("predio", predioRustico);
//                par.put("estado", 1L);
            par.put("anioInicio", sol.getAnioInicio());
            par.put("anioFin", sol.getAnioFin());
            par.put("codigoTitulo", 1L);
            List<RenLiquidacion> l = manager.findListNamedQuery("RenLiquidacion.findPagoPredioRustico", par);
            for (RenLiquidacion r : l) {
                if (r.getRenPagoCollection() != null) {
                    fuera:
                    for (RenPago p : r.getRenPagoCollection()) {
                        if (p.getEstado()) {
                            result.add(r);
                            break fuera;
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            LOG.log(Level.OFF, "Consultar Pago Predios Rusticos", e);
        }
        return null;
    }

    @Override
    public List<CatPredio> getPredios(CatEnte comprador) {
        Map<String, Object> map = new HashMap<>();
        map.put("ente", comprador);
        return manager.findListNamedQuery("CatPredioPropietario.findPredioByEnte", map);
    }

    @Override
    public Boolean generarNumLiquidacion(RenLiquidacion liquidacion, String prefijo) {
        final Long tipoLiquidacion = liquidacion.getTipoLiquidacion().getId();
        try {
            liquidacion.setNumLiquidacion(seq.getMaxSecuenciaTipoLiquidacion(Utils.getAnio(new Date()), tipoLiquidacion));
            if (liquidacion.getNumLiquidacion() != null) {
                liquidacion.setIdLiquidacion(prefijo.concat("-").concat(Utils.completarCadenaConCeros(liquidacion.getNumLiquidacion().toString(), 6)));
            }
            liquidacion.setAnio(Utils.getAnio(new Date()));
            manager.update(liquidacion);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, prefijo, e);
            return false;
        }

    }

    @Override
    public Boolean editarRenTipoLiquidacion(RenTipoLiquidacion tipoLiq) {
        try {
            if (tipoLiq.getCodigoTituloReporte() == null && tipoLiq.getNombreTitulo() != null && tipoLiq.getPrefijo() != null) {
                tipoLiq.setCodigoTituloReporte(this.generarCodTitRep());
            }
            manager.update(tipoLiq);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return false;
        }
    }

    @Override
    public Long generarCodTitRep() {
        Long value = (Long) manager.find(QuerysFinanciero.getLastCodigoTituloReporte, new String[]{}, new Object[]{});
        if (value != null) {
            return value + 1;
        } else {
            return null;
        }
    }

    @Override
    public CtlgSalario getSalarioBasico(Integer anio) {
        return (CtlgSalario) manager.findNoProxy(QuerysFinanciero.getSalarioByAnio, new String[]{"anio"}, new Object[]{BigInteger.valueOf(anio)});
    }

    @Override
    public RenTasaTurismo guardarTasaTurismo(RenTasaTurismo tasa) {
        try {
            if (tasa.getId() == null) {
                return (RenTasaTurismo) manager.persist(tasa);
            } else {
                manager.persist(tasa);
                return tasa;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "guardarTasaTurismo", e);
            return null;
        }
    }

    @Override
    public Boolean eliminarRubro(RenRubrosLiquidacion rubro, RenTipoLiquidacion tipoLiquidacion) {
        try {
            rubro.setTipoLiquidacion(null);
            rubro.setCodigoRubro(null);
            manager.persist(rubro);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return false;
        }
    }

    @Override
    public RenRubrosLiquidacion guardarRubroNuevo(RenRubrosLiquidacion rubro, RenTipoLiquidacion tipoLiquidacion) {
        try {
            if (tipoLiquidacion != null) {
                Long lastCodRubro = (Long) manager.find(QuerysFinanciero.getLastCodigoRubro, new String[]{"tipoLiq"}, new Object[]{tipoLiquidacion.getId()});
                if (lastCodRubro == null) {
                    lastCodRubro = 1L;
                } else {
                    lastCodRubro = lastCodRubro + 1L;
                }
                rubro.setCodigoRubro(lastCodRubro);
            }
            rubro.setTipoLiquidacion(tipoLiquidacion);
            System.out.println(rubro.getTipoValor());
            rubro = (RenRubrosLiquidacion) manager.persist(rubro);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }
        return rubro;
    }

    @Override
    public Long obtenerNumeroCondonacion() {
        Long lastNumero;
        try {
            lastNumero = (Long) manager.find(QuerysFinanciero.getLastNumeroSolicitudCondonacion, new String[]{}, new Object[]{});
            if (lastNumero == null || lastNumero == 0) {
                lastNumero = 1L;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }

        return lastNumero;
    }

    @Override
    public RenLocalComercial guardarLocalComercial(RenLocalComercial local, RenActivosLocalComercial activosLocal) {
        RenLocalComercial localC;
        Boolean confirm = true;
        try {
            Collection<RenLocalCantidadAccesorios> cantidadAccesorios = local.getCantidadAccesoriosCollection();
            localC = local;
            localC = (RenLocalComercial) manager.persist(localC);
            /*if (localC.getActivosLocalComercialCollection() == null || localC.getActivosLocalComercialCollection().isEmpty()) {
             confirm = true;
             } else {
             if (localC.getActivosLocalComercialCollection() != null && !localC.getActivosLocalComercialCollection().isEmpty()) {
             for (RenActivosLocalComercial temp : localC.getActivosLocalComercialCollection()) {
             if (temp.getAnioBalance().equals(activosLocal.getAnioBalance())) {
             temp.setActivoContingente(activosLocal.getActivoContingente());
             temp.setActivoTotal(activosLocal.getActivoTotal());
             temp.setEstado(true);
             temp.setFechaIngreso(new Date());
             temp.setPasivoContingente(activosLocal.getPasivoContingente());
             temp.setPasivoTotal(activosLocal.getPasivoTotal());
             temp.setPorcentajeIngreso(activosLocal.getPorcentajeIngreso());
             manager.update(temp);
             confirm = false;
             break;
             }
             }
             }
             }*/
            if (confirm) {
                if (activosLocal != null) {
                    activosLocal.setLocalComercial(localC);
                    manager.persist(activosLocal);
                }
            }

            localC = (RenLocalComercial) manager.persist(localC);

            if (cantidadAccesorios != null) {
                for (RenLocalCantidadAccesorios temp : cantidadAccesorios) {
                    temp.setLocalComercial(localC);
                    manager.persist(temp);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            localC = null;
        }

        return localC;
    }

//    @Override
//    public HistoricoTramites guardarMultaFlujo(Observaciones obs, CmMultas multa, HistoricoTramites ht){
//        try{
//            ht = (HistoricoTramites)manager.persist(ht);
//            multa.setTramite(ht);
//            obs.setIdTramite(ht);
//            manager.persist(obs);
//            manager.persist(multa);
//            return ht;
//        }catch(Exception e){
//            LOG.log(Level.SEVERE, "", e);
//            return null;
//        }
//    }
    @Override
    public List<RenLocalCantidadAccesorios> getAcesorios(RenLocalComercial localSel) {
        Map m = new HashMap<>();
        m.put("localComercial", localSel);
        return (List<RenLocalCantidadAccesorios>) manager.findObjectByParameterList(RenLocalCantidadAccesorios.class, m);
    }

    @Override
    public List<RenTasaTurismo> getTasasTurismo(RenLocalComercial localSel) {
        List<RenTasaTurismo> result = new ArrayList<>();
        for (RenActividadComercial col : localSel.getRenActividadComercialCollection()) {
            Map m = new HashMap<>();
            m.put("actividad", col);
            m.put("categoria", localSel.getCategoria());
            RenTasaTurismo t = (RenTasaTurismo) manager.findObjectByParameter(RenTasaTurismo.class, m);
            if (t != null) {
                result.add(t);
            }
        }
        return (List<RenTasaTurismo>) EntityBeanCopy.clone(result);
    }

    @Override
    public List<RenActividadComercial> getActividadesLocal(RenLocalComercial loc) {
        Map m = new HashMap<>();
        m.put("localComercial", loc);
        return (List<RenActividadComercial>) EntityBeanCopy.clone(manager.findObjectByParameterList(RenActividadComercial.class, m));
    }

    @Override
    public List<FnExoneracionTipo> FnExoneracionesTipoByAplica(List aplica) {
        Map<String, Object> par = new HashMap<>();
        par.put("estado", true);
        Map<String, Object> in = new HashMap<>();
        in.put("aplica", aplica);
        return manager.findIn(FnExoneracionTipo.class, par, in);
    }

    @Override
    public List<FnSolicitudExoneracion> getSolicitudesAutomaticas(Integer anio, List<FnExoneracionTipo> tipos) {
        Map<String, Object> par = new HashMap<>();
        par.put("estado", new FnEstadoExoneracion(Long.parseLong("1")));
        par.put("anio", anio);
        List<FnSolicitudExoneracion> resultConsu = manager.findListNamedQuery("FnSolicitudExoneracion.findAllSolicitudesAuto", par);
        List<FnSolicitudExoneracion> result = new ArrayList<>();
        /*
         for (FnSolicitudExoneracion ob : resultConsu) {
         if (ob.getSolicitante() != null && ob.getPredio() != null) {
         if (verificarSolicitanteSolicutud(ob)) {
         if (ob.getFnSolicitudExoneracionAutomaticaCollection() == null
         && ob.getFnSolicitudExoneracionAutomaticaCollection().isEmpty()) {
         result.add(ob);
         } else {
         FnSolicitudExoneracionAutomatica a = Utils.get(ob.getFnSolicitudExoneracionAutomaticaCollection(), 0);
         if (!Objects.equals(a.getAnio(), Utils.getAnio(new Date()))) {
         if (ob.getExoneracionTipo().getExoneracionClase().getId().equals(2L)) {
         if (ob.getFnSolicitudExoneracionAutomaticaCollection().size() > 0) {
         result.add(ob);
         }
         } else {
         result.add(ob);
         }
         }
         }
         }
         }
         }*/
        if (result.size() == 0) {
            return null;
        }
        return result;
    }

    @Override
    public Boolean verificarSolicitanteSolicutud(FnSolicitudExoneracion solicitudExoneracion) {
        try {
            Long idEntePredio = null;
            if (solicitudExoneracion.getPredio() != null) {
                idEntePredio = (Long) manager.find(QuerysFinanciero.verificarPropietarioPredios,
                        new String[]{"estado", "predio", "ente"},
                        new Object[]{"A", solicitudExoneracion.getPredio(), solicitudExoneracion.getSolicitante()});
            } else {
                Map<String, Object> par = new HashMap<>();
                par.put("estado", true);
                par.put("tramite", solicitudExoneracion.getTramite());
                List<HistoricoTramiteDet> prediosSolicitud = manager.findObjectByParameterList(HistoricoTramiteDet.class, par);
                if (prediosSolicitud != null) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("estado", true);
                    p.put("predio", solicitudExoneracion.getTramite());
                    for (HistoricoTramiteDet ps : prediosSolicitud) {

                    }
                }
            }
            return solicitudExoneracion.getSolicitante().getId().equals(idEntePredio);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    /*@Override
     public Boolean generarExoneracionAuto(FnSolicitudExoneracion solicitud, String userSession) {
     try {
     FnSolicitudExoneracionAutomatica automatica = new FnSolicitudExoneracionAutomatica();
     automatica.setAnio(Utils.getAnio(new Date()));
     automatica.setEstado(new FnEstadoExoneracion(2L));
     automatica.setFechaIngreso(new Date());
     automatica.setSolicitudExoneracion(solicitud);
     automatica.setUsuarioIngreso(userSession);
     return manager.persist(automatica) != null;
     } catch (Exception e) {
     LOG.log(Level.SEVERE, "ERROR AL GENERAR NUEVA SOLICITUD...", e);
     return false;
     }
     }*/
    @Override
    public RenLocalComercial getLocalById(RenLiquidacion liquidacion) {
        Map<String, Object> par = new HashMap<>();
        par.put("liquidacion", liquidacion.getId());
        return manager.findObjectNamedQuery("RenLocalComercial.findAllByLiquidacion", par);
    }

    @Override
    public RenActivosLocalComercial getActivosLocal(RenLiquidacion liquidacion) {
        Map<String, Object> par = new HashMap<>();
        par.put("numLiquidacion", BigInteger.valueOf(liquidacion.getId()));
        par.put("estado", true);
        return manager.findObjectByParameter(RenActivosLocalComercial.class, par);
    }

    @Override
    public RenBalanceLocalComercial getBalanceLocal(RenLiquidacion liquidacion) {
        Map<String, Object> par = new HashMap<>();
        par.put("numLiquidacion", BigInteger.valueOf(liquidacion.getId()));
        par.put("estado", true);
        return manager.findObjectByParameter(RenBalanceLocalComercial.class, par);
    }

    @Override
    public List<RenClaseLocal> getClasesLocal() {
        Map<String, Object> par = new HashMap<>();
        par.put("estado", true);
        return manager.findObjectByParameterOrderList(RenClaseLocal.class, par, new String[]{"descripcion"}, true);
    }

    @Override
    public List<RenAfiliacionCamaraProduccion> getAfiliacionCamara() {
        Map<String, Object> par = new HashMap<>();
        par.put("estado", true);
        return manager.findObjectByParameterOrderList(RenAfiliacionCamaraProduccion.class, par, new String[]{"descripcion"}, true);
    }

    @Override
    public List<GeRequisitosTramite> requisitosTramite(boolean b) {
        try {
            Map<String, Object> par = new HashMap<>();
            par.put("estado", b);
            par.put("abreviatura", "PRF");
            GeTipoTramite tramite = manager.findObjectByParameter(GeTipoTramite.class, par);
            if (tramite == null) {
                LOG.log(Level.INFO, "No existe Tramite Con esa Abreviatura.");
                return null;
            }
            if (tramite.getGeRequisitosTramiteCollection() == null) {
                LOG.log(Level.INFO, "Tramite no tiene requisitos.");
                return null;
            }
            tramite.getGeRequisitosTramiteCollection().size();
            return (List<GeRequisitosTramite>) EntityBeanCopy.clone(tramite.getGeRequisitosTramiteCollection());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Consultar Requisitos", e);
        }
        return null;
    }

    @Override
    public List<RenLiquidacion> getLiquidacionesLocal(RenLocalComercial local, Integer anio) {
        String q = "SELECT l FROM RenLiquidacion l WHERE l.estadoLiquidacion.id IN (1, 2) AND l.localComercial = :local AND TO_CHAR(l.fechaIngreso, 'YYYY') = :anioIngreso";
        String q2 = "SELECT l FROM RenLiquidacion l WHERE l.estadoLiquidacion.id IN (1, 2) AND l.localComercial = :local ORDER BY l.anio ASC";
        if (local.getId() != null) {
            return (List<RenLiquidacion>) manager.findAll(q2, new String[]{"local"}, new Object[]{local});
        }
        return null;
    }

    @Override
    public boolean actividadTuristica(RenActividadComercial actl, RenTipoLocalComercial categoria) {
        RenLocalCategoria ct = getCategiriaByDescripcion(categoria.getDescripcion());
        Map<String, Object> par = new HashMap<>();
        par.put("actividad", actl);
        par.put("categoria", ct);
        par.put("estado", true);
        RenTasaTurismo tur = manager.findObjectByParameter(RenTasaTurismo.class, par);
        return tur != null;
    }

    @Override
    public RenPermisosFuncionamientoLocalComercial guadarPermisoFuncionamiento(RenPermisosFuncionamientoLocalComercial permisosFuncionamiento, CatEnte contribuyente, Long isUser, RenActivosLocalComercial activos, RenBalanceLocalComercial balance) {
        try {
            Map<String, Object> par = new HashMap<>();
            par.put("abreviatura", "PRF");
            par.put("estado", true);
            GeTipoTramite tramite = manager.findObjectByParameter(GeTipoTramite.class, par);

            HistoricoTramites ht = new HistoricoTramites();
            if (tramite != null) {
                if (!contribuyente.getEnteCorreoCollection().isEmpty()) {
                    String correos = null;
                    for (EnteCorreo c : contribuyente.getEnteCorreoCollection()) {
                        if (correos == null) {
                            correos = c.getEmail();
                        } else {
                            correos = correos + "," + c.getEmail();
                        }
                    }
                    ht.setCorreos(correos);
                }
                if (!contribuyente.getEnteTelefonoCollection().isEmpty()) {
                    ht.setTelefonos(contribuyente.getEnteTelefonoCollection().get(0).getTelefono());
                }
                ht.setSolicitante(contribuyente);
                ht.setEstado("Pendiente");
                ht.setFecha(new Date());
                ht.setTipoTramite(tramite);
                ht.setTipoTramiteNombre(tramite.getDescripcion());
                ht.setNombrePropietario(contribuyente.getNombreCompleto());

                ht.setNumPredio(permisosFuncionamiento.getLocalComercial().getNumPredio());
                String nomLoc = null;
                if (permisosFuncionamiento.getLocalComercial() != null && permisosFuncionamiento.getLocalComercial().getNombreLocal() != null) {
                    nomLoc = (permisosFuncionamiento.getLocalComercial().getNombreLocal().length() <= 19)
                            ? permisosFuncionamiento.getLocalComercial().getNombreLocal() : permisosFuncionamiento.getLocalComercial().getNombreLocal().substring(0, 19);
                }
                ht.setMz(nomLoc);
                ht.setSolar(permisosFuncionamiento.getLocalComercial().getNumLocal());
                ht.setUserCreador(isUser);
                ht.setId(permisoService.generarIdTramite());
                ht = (HistoricoTramites) manager.persist(ht);
                if (ht != null && ht.getIdTramite() != null) {
                    permisosFuncionamiento.setHt(ht);
                    permisosFuncionamiento = (RenPermisosFuncionamientoLocalComercial) manager.persist(permisosFuncionamiento);
//                    manager.update(permisosFuncionamiento);

                    if (activos != null) {
                        activos.setPermiso(permisosFuncionamiento);
                        activos.setLocalComercial(permisosFuncionamiento.getLocalComercial());
                        activos = (RenActivosLocalComercial) manager.persist(activos);
                    }

                    if (balance != null) {
                        balance.setPermiso(permisosFuncionamiento);
                        balance.setLocalComercial(permisosFuncionamiento.getLocalComercial());
                        balance = (RenBalanceLocalComercial) manager.persist(balance);
                    }
                }
                return permisosFuncionamiento;
            } else {
                throw new RuntimeException("No se encuentra ge_tipo_tramite con abreviatura 'PRF'");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Guardar Permiso Funcinamiento", e);
        }
        return null;
    }

    @Override
    public Boolean updateHtAndPermisoFunc(HistoricoTramites hts, RenPermisosFuncionamientoLocalComercial permisosFuncionamiento, Observaciones o) {
        try {
            HistoricoTramites ht = permisosFuncionamiento.getHt();
            manager.persist(ht);
            //manager.persist(permisosFuncionamiento.getLocalComercial());
            manager.persist(permisosFuncionamiento);
            o.setIdTramite(ht);
            return manager.persist(o) != null;
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
            return false;
        }
    }

    @Override
    public RenRubrosLiquidacion getRubroById(Long idRubro) {
        return manager.find(RenRubrosLiquidacion.class, idRubro);
    }

    @Override
    public HistoricoTramites guardarMultaFlujoInit(Observaciones obs, CmMultas multa, HistoricoTramites ht) {
        try {
            ht = (HistoricoTramites) manager.persist(ht);
            multa.setTramite(ht);
            obs.setIdTramite(ht);
            manager.persist(obs);
            manager.persist(multa);
            return ht;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }
    }

    @Override
    public RenTurismo guardarTurismo(RenTurismo turismo, Observaciones obs, List<HabitacionTurismo> detalleHabitaciones) {
        try {
            turismo = (RenTurismo) manager.persist(turismo);
            manager.persist(obs);
            for (HabitacionTurismo temp : detalleHabitaciones) {
                RenTurismoDetalleHoteles det = temp.getDetalle();
                det.setTurismo(turismo);
                det.setTipoHabitacion(manager.find(RenTurismoServicios.class, temp.getIdTurismoHabitacion()));
                det.setEstado(Boolean.TRUE);
                manager.persist(det);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            return null;
        }
        return turismo;
    }

    public List<RenTasaTurismo> getTasasActividad(RenLocalComercial localSel) {
        List<RenTasaTurismo> result = new ArrayList<RenTasaTurismo>();
        for (RenActividadComercial col : localSel.getRenActividadComercialCollection()) {
            Map m = new HashMap<>();
            m.put("actividad", col);
            m.put("categoria", localSel.getCategoria());
            RenTasaTurismo t = (RenTasaTurismo) manager.findObjectByParameter(RenTasaTurismo.class, m);
            if (t != null) {
                result.add(t);
            }
        }
        return (List<RenTasaTurismo>) EntityBeanCopy.clone(result);
    }

    @Override
    public RenTurismoServicios guardarTurismoServicios(RenTurismoServicios servicios) {
        if (servicios.getId() == null) {
            servicios.setEstado(Boolean.TRUE);
            servicios.setFechaIngreso(new Date());
            return (RenTurismoServicios) manager.persist(servicios);
        } else {
            manager.persist(servicios);
            return servicios;
        }

    }

    @Override
    public List<RenLiquidacion> guardarPermisosFuncionamiento(RenPermisosFuncionamientoLocalComercial permiso, LiquidacionesPermisosFuncionamiento actAnual, LiquidacionesPermisosFuncionamiento tasaHab, LiquidacionesPermisosFuncionamiento rotulos, LiquidacionesPermisosFuncionamiento turismo, LiquidacionesPermisosFuncionamiento patente, String usuarioCreador) {
        List<RenLiquidacion> result = new ArrayList<>();
        Object numLiquidacion = null;

        try {
            if (permiso.getActivos()) {
                actAnual.getLiquidacion().setSaldo(actAnual.getLiquidacion().getTotalPago());
                actAnual.getLiquidacion().setEstadoCoactiva(1);
                actAnual.getLiquidacion().setCoactiva(Boolean.FALSE);
                actAnual.getLiquidacion().setTramite(permiso.getHt());
                actAnual.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                if (actAnual.getLiquidacion().getEstadoLiquidacion() == null) {
                    actAnual.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2l));
                }
                RenLiquidacion act = this.guardarLiquidacionYRubros(actAnual.getLiquidacion(), actAnual.getDetalle(),
                        actAnual.getTiposLiquidacion(), actAnual.getActivos(), null, null, null);
                if (act != null) {
                    // GENERAR SECUENCIA DE ACTIVOS
                    if (numLiquidacion != null) {
//                        seq.
                        act.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                        act.setIdLiquidacion(act.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
                    }
                    result.add(act);
                }
            }
            if (permiso.getPatente()) {
                patente.getLiquidacion().setSaldo(patente.getLiquidacion().getTotalPago());
                patente.getLiquidacion().setEstadoCoactiva(1);
                patente.getLiquidacion().setCoactiva(Boolean.FALSE);
                patente.getLiquidacion().setTramite(permiso.getHt());
                patente.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                if (patente.getLiquidacion().getEstadoLiquidacion() == null) {
                    patente.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2l));
                }
                RenLiquidacion pat = this.guardarLiquidacionYRubros(patente.getLiquidacion(), patente.getDetalle(),
                        patente.getTiposLiquidacion(), null, patente.getBalance(), null, null);
                if (pat != null) {
                    // GENERAR SECUENCIA DE TURISMO
                    if (numLiquidacion != null) {
                        pat.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                        pat.setIdLiquidacion(pat.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
                    }
                    result.add(pat);
                }

            }
            if (permiso.getRotulos()) {
                rotulos.getLiquidacion().setSaldo(rotulos.getLiquidacion().getTotalPago());
                rotulos.getLiquidacion().setEstadoCoactiva(1);
                rotulos.getLiquidacion().setCoactiva(Boolean.FALSE);
                rotulos.getLiquidacion().setTramite(permiso.getHt());
                rotulos.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                if (rotulos.getLiquidacion().getEstadoLiquidacion() == null) {
                    rotulos.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2l));
                }
                RenLiquidacion rot = this.guardarLiquidacionYRubros(rotulos.getLiquidacion(), rotulos.getDetalle(),
                        rotulos.getTiposLiquidacion(), null, null, null, null);
                if (rot != null) {
                    // GENERAR SECUENCIA DE ROTULOS
                    if (numLiquidacion != null) {
                        rot.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                        rot.setIdLiquidacion(rot.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
                    }
                    result.add(rot);
                }
            }
            if (permiso.getTasaHabilitacion()) {
                tasaHab.getLiquidacion().setSaldo(tasaHab.getLiquidacion().getTotalPago());
                tasaHab.getLiquidacion().setEstadoCoactiva(1);
                tasaHab.getLiquidacion().setCoactiva(Boolean.FALSE);
                tasaHab.getLiquidacion().setTramite(permiso.getHt());
                tasaHab.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                if (tasaHab.getLiquidacion().getEstadoLiquidacion() == null) {
                    tasaHab.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2l));
                }

                RenLiquidacion tashab = this.guardarLiquidacionYRubros(tasaHab.getLiquidacion(), tasaHab.getDetalle(),
                        tasaHab.getTiposLiquidacion(), null, null, null, null);
                if (tashab != null) {
                    //GENERAR SECUENCIA DE TASA DE HABILITACION 
                    if (numLiquidacion != null) {
                        tashab.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                        tashab.setIdLiquidacion(tashab.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
                    }
                    result.add(tashab);
                }
            }
            if (permiso.getTurismo()) {
                turismo.getLiquidacion().setSaldo(turismo.getLiquidacion().getTotalPago());
                turismo.getLiquidacion().setEstadoCoactiva(1);
                turismo.getLiquidacion().setCoactiva(Boolean.FALSE);
                turismo.getLiquidacion().setTramite(permiso.getHt());
                turismo.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                if (turismo.getLiquidacion().getEstadoLiquidacion() == null) {
                    turismo.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2l));
                }
                RenLiquidacion tur = this.guardarLiquidacionYRubros(turismo.getLiquidacion(), turismo.getDetalle(),
                        turismo.getTiposLiquidacion(), null, null, null, null);
                if (tur != null) {
                    // GENERAR SECUENCIA DE TURISMO
                    if (numLiquidacion != null) {
                        tur.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                        tur.setIdLiquidacion(tur.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
                    }
                    result.add(tur);
                }
            }
            return result;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al Procesar Liquidaciones de Permiso de Funcionamiento", e);
            return null;
        }
    }

    @Override
    public List<RenLocalUbicacion> getUbicaciones() {
        Map m = new HashMap<>();
        m.put("estado", true);
        return manager.findObjectByParameterOrderList(RenLocalUbicacion.class, m, new String[]{"descripcion"}, Boolean.TRUE);
    }

    @Override
    public List<RenLocalCategoria> getCategorias() {
        Map m = new HashMap<>();
        m.put("estado", true);
        return manager.findObjectByParameterOrderList(RenLocalCategoria.class, m, new String[]{"descripcion"}, Boolean.TRUE);
    }

    @Override
    public List<RenTipoLocalComercial> getTipoLocals() {
        Map m = new HashMap<>();
        m.put("estado", true);
        return manager.findObjectByParameterOrderList(RenTipoLocalComercial.class, m, new String[]{"descripcion"}, Boolean.TRUE);
    }

    @Override
    public RenLocalCategoria getCategiriaByDescripcion(String descripcion) {
        String q = "SELECT ct FROM RenLocalCategoria ct where ct.descripcion = :descripcion";
        return (RenLocalCategoria) manager.find(q, new String[]{"descripcion"}, new Object[]{descripcion});
    }

    public BigDecimal getValorImpuesto(BigDecimal avaluo, Integer anio) {
        String q = "SELECT ct FROM AvalBandaImpositiva ct where ct.estado = 'A' and"
                + " ct.anioInicio=:anio AND " + avaluo + " BETWEEN ct.desdeUs and ct.hastaUs";
        AvalBandaImpositiva banda = (AvalBandaImpositiva) manager.find(q, new String[]{"anio"}, new Object[]{anio});
        System.out.println("banda: " + banda.toString());
        BigDecimal r = ((avaluo.subtract(banda.getDesdeUs())).multiply(banda.getMultiploImpuestoPredial())).divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);
        System.out.println("getValorImpuesto: " + r.add(new BigDecimal(banda.getFracionBasica())));
        return r.add(new BigDecimal(banda.getFracionBasica()));
    }

    @Override
    public RenActividadComercial guardarActividad(RenActividadComercial actividad) {
        return (RenActividadComercial) manager.persist(actividad);
    }

    public String generadorCeroALaIzquierda(Long n) {
        int cont = 0;
        Long num = n;
        String salida = "";
        while (num > 0) {
            num = num / 10;
            cont++;
        }
        for (int i = 0; i < 8 - cont; i++) {
            salida = salida + "0";
        }
        salida = salida + n;
        return salida;
    }

    @Override
    public Boolean existePermiso(RenLocalComercial local) {
        try {
            if (local.getId() != null) {
                Map<String, Object> paramt = new HashMap<>();
                paramt.put("localComercial", local);
                paramt.put("fechaEmision", new Date());
                List<RenPermisosFuncionamientoLocalComercial> p = manager.findObjectByParameterList(RenPermisosFuncionamientoLocalComercial.class, paramt);
                if (p != null && !p.isEmpty()) {
                    return true;
                } else {
                    List<RenLiquidacion> existeLiquidacion = getLiquidacionesLocal(local, Utils.getAnio(new Date()));
                    return existeLiquidacion != null && !existeLiquidacion.isEmpty();
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "existePermiso(RenLocalComercial local)", e);
            return false;
        }
    }

    @Override
    public Integer existeLiquidacion(RenLiquidacion liquidacion) {
//        QuerysFinanciero.existeLiquidacion;
        Object temp = manager.getNativeQuery(QuerysFinanciero.existeLiquidacion,
                new Object[]{liquidacion.getTipoLiquidacion(), liquidacion.getAnio(), liquidacion.getLocalComercial()});
        if (temp == null) {
            return 3;
        } else {
            if (Long.valueOf(temp.toString()).equals(1L)) {
                return 1;
            } else if (Long.valueOf(temp.toString()).equals(2L)) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    @Override
    public boolean inactivarLiquidaciones(List<RenLiquidacion> liquidaciones) {
        try {
            if (Utils.isNotEmpty(liquidaciones)) {
                for (RenLiquidacion liquidacion : liquidaciones) {
                    liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
                    manager.persist(liquidacion);
                }
                return true;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "inactivarLiquidaciones", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean eliminarTitulo(RenTipoLiquidacion tipoLiq) {
        try {
            Collection<RenRubrosLiquidacion> renRubrosLiquidacionCollection = tipoLiq.getRenRubrosLiquidacionCollection();
            tipoLiq.setRenRubrosLiquidacionCollection(null);
            if (renRubrosLiquidacionCollection != null) {
                if (manager.deleteList((List) renRubrosLiquidacionCollection)) {
                    System.out.println("Elimnado >>>> ");
                }
            }
            Collection<RenSecuenciaNumLiquidicacion> renNumLiquidacionCollection = tipoLiq.getRenNumLiquidacionCollection();
            tipoLiq.setRenNumLiquidacionCollection(null);
            if (renNumLiquidacionCollection != null) {
                if (manager.deleteList((List) renNumLiquidacionCollection));
            }
            return manager.delete(tipoLiq);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "inactivarLiquidaciones", e);
        }
        return false;
    }

    @Override
    public List<CatPredio> getPrediosByEnte(CatEnte solicitante) {
        try {
            List<CatPredio> listTemp = new ArrayList<>();
            Map<String, Object> paramt = new HashMap<>();
            paramt.put("ente", solicitante);
            paramt.put("estado", "A");
            List<CatPredioPropietario> pps = manager.findObjectByParameterList(CatPredioPropietario.class, paramt);
            if (Utils.isNotEmpty(pps)) {
                for (CatPredioPropietario pp : pps) {
                    if (!listTemp.contains(pp.getPredio())) {
                        listTemp.add(pp.getPredio());
                    }
                }
                return listTemp;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Obtener predio de solicitante", e);
        }
        return null;
    }

    public BigDecimal interesCalculado(RenLiquidacion emision, Date hasta) {
        //BigDecimal interes = this.interesAcumulado(emision, hasta);
        BigDecimal div = new BigDecimal("100");
        BigDecimal interesValor = div.multiply(emision.getTotalPago()).divide(div).setScale(2, RoundingMode.HALF_UP);
        return interesValor;
    }

}
