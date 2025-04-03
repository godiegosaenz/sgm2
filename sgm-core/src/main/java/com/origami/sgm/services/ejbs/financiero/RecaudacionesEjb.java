/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.financiero;

import com.origami.sgm.entities.CarteraVencida;
import com.origami.sgm.entities.TitulosPredio;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.CatPredioRuralModel;
import com.origami.sgm.bpm.models.Cobros;
import com.origami.sgm.bpm.models.ModelCarteraVencida;
import com.origami.sgm.bpm.models.ModelCarteraVencidaParroquia;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CoaAbogado;
import com.origami.sgm.entities.CoaEstadoJuicio;
import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.CtlgDescuentoEmision;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MejDetRubroMejoras;
import com.origami.sgm.entities.MejPagoRubroMejora;
import com.origami.sgm.entities.RecActasEspecies;
import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.entities.RecEspecies;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenIntereses;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenPagoDetalle;
import com.origami.sgm.entities.RenPagoRubro;
import com.origami.sgm.entities.RenParametrosInteresMulta;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.bancos.ConsolidacionBanco;
import com.origami.sgm.entities.models.ParteRecaudaciones;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.financiero.RemisionInteresServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import util.GroovyUtil;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author HenryPilco
 */
@Singleton(name = "RecaudacionesService")
@Interceptors(value = {HibernateEjbInterceptor.class})
@Lock(LockType.READ)
@ApplicationScoped
public class RecaudacionesEjb implements RecaudacionesService {

    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    private SeqGenMan sec;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    @javax.inject.Inject
    private RemisionInteresServices interesServices;

    @Override
    public HistoricoTramites getHistoricoTramiteByNumTramite(Long tramite) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("id", tramite);
        try {
            return manager.findObjectByParameter(HistoricoTramites.class, paramt);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public AclUser getAclUserByRol(Long idRol) {
        try {
            AclRol rol = manager.find(AclRol.class, idRol);
            List<AclUser> list = (List<AclUser>) rol.getAclUserCollection();
            return list.get(0);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     *
     * @param user
     * @param flag TRUE: NOMBRE COMPLETO, FALSE: NOMBRE DE USUARIO
     * @return
     */
    @Override
    public String getNameUserAssigne(Long user, Boolean flag) {
        String name = "";
        try {
            if (user == null) {
                System.out.println("Id de usuario es nullo metodo getNameUserAssigne");
                return "";
            }
            AclUser u = manager.find(AclUser.class, user);
            if (u != null) {
                if (flag) {
                    if (u.getEnte() != null) {
                        if (u.getEnte().getTituloProf() == null) {
                            name = u.getEnte().getNombres() + " " + u.getEnte().getApellidos();
                        } else {
                            name = u.getEnte().getTituloProf() + " " + u.getEnte().getNombres() + " " + u.getEnte().getApellidos();
                        }
                    }
                } else {
                    name = u.getUsuario();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return "";
        }
        return name;
    }

    @Override
    public List<AclUser> getUsuariosByRolId(Long id) {
        List<AclUser> list;
        try {
            AclRol rol = manager.find(AclRol.class, id);
            list = (List<AclUser>) rol.getAclUserCollection();
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public String getNameUserByRol(Long idRol) {
        AclUser user = new AclUser();
        try {
            AclRol rol = manager.find(AclRol.class, idRol);
            if (Utils.isNotEmpty((List<?>) rol.getAclUserCollection())) {
                for (AclUser u : rol.getAclUserCollection()) {
                    user = u;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return this.getNameUserAssigne(user.getId(), true);
    }

    @Override
    public List<RecEspecies> getEspeciesActivas() {
        List<RecEspecies> list;
        try {
            list = manager.findAll(Querys.getEspeciesActivas);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public List<RecActasEspeciesDet> getActasEspeciesActivas() {
        List<RecActasEspeciesDet> list;
        try {
            list = manager.findAll(Querys.getActasEspeciesActivas);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public String saveActaEspecies(RecActasEspecies acta) {
        try {
            Collection<RecActasEspeciesDet> list = acta.getRecActasEspeciesDetCollection();
            acta.setRecActasEspeciesDetCollection(null);
            // 104 ES EL ID DEL ROL JEFE DE TESORERIA
            if (this.getUsuariosByRolId(104L) != null) {
                acta.setTesorero(this.getUsuariosByRolId(104L).get(0).getId());
                acta = sec.maxNumeroEspecie(acta);
                for (RecActasEspeciesDet det : list) {
                    det.setActa(acta);
                    manager.persist(det);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return acta.getNumActa() + "-" + acta.getAnio();
    }

    @Override
    public String reAsignarActaEspecies(List<RecActasEspeciesDet> list, Long idAclUser, String nameUser, BigDecimal valor) {
        RecActasEspecies acta = new RecActasEspecies();
        Calendar cal = Calendar.getInstance();
        RecActasEspeciesDet temp;
        try {

            acta.setAnio(cal.get(Calendar.YEAR));
            acta.setFechaIngreso(cal.getTime());
            acta.setTotal(valor);
            acta.setUsuarioIngreso(nameUser);
            acta.setUsuarioAsignado(idAclUser);
            acta.setTesorero(this.getUsuariosByRolId(104L).get(0).getId());
            acta = sec.maxNumeroEspecie(acta);

            for (RecActasEspeciesDet de : list) {
                temp = new RecActasEspeciesDet();
                temp.setActa(acta);
                temp.setCantidad(de.getDisponibles());
                temp.setDesde(de.getUltimoVendido() + 1L);
                temp.setDisponibles(de.getDisponibles());
                temp.setEspecie(de.getEspecie());
                temp.setHasta(de.getHasta());
                temp.setValorUni(de.getValorUni());
                temp.setValorTotal(de.getValorUni().multiply(BigDecimal.valueOf(de.getDisponibles())));
                manager.persist(temp);
                de.setEstado("C");
                manager.update(de);
            }

        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return acta.getNumActa() + "-" + acta.getAnio();
    }

    @Override
    public List<CoaAbogado> getListAbogadosJuicios() {
        return manager.findAllEntCopy(Querys.getAbogadosJuicios);
    }

    @Override
    public List<CoaEstadoJuicio> getListEstadosJuicios() {
        return manager.findAllEntCopy(Querys.getEstadosJuicios);
    }

    @Override
    public List<RenLiquidacion> getPagoAnualByPredioPendientesCoactiva(Long idPredio, Long idEstado) {
        Calendar cal = Calendar.getInstance();
        try {
            if (idEstado == null) {
                return manager.findAll(Querys.getLiquidacionesActivasByPredio, new String[]{"tipo", "anio", "idPredio"}, new Object[]{13L, cal.get(Calendar.YEAR), idPredio});
            } else {
                return manager.findAll(Querys.getLiquidacionNoPagadaByPredio, new String[]{"tipo", "estado", "anio", "idPredio"}, new Object[]{13L, idEstado, cal.get(Calendar.YEAR), idPredio});
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<RenLiquidacion> getEmisionesCoactivaAntigua(CatPredio predio) {
        try {
            Calendar fecha = Calendar.getInstance();
            return manager.findAll(Querys.getLiquidacionParaJuiciosAntiguos, new String[]{"predio", "anio"}, new Object[]{predio, fecha.get(Calendar.YEAR)});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<CatPredioPropietario> getPropietariosActivosPredio(Long idPredio) {
        try {
            return manager.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{idPredio});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public Boolean consultaJuicioByNumeroYanio(Integer numero, Integer anio) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("numeroJuicio", numero);
        paramt.put("anioJuicio", anio);
        try {
            Object juicio = manager.findObjectByParameter(CoaJuicio.class, paramt);
            return juicio != null;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return true;
        }
    }

    @Override
    public Boolean guardarJuicioCoactivoAntiguo(CoaJuicio juicio, List<RenLiquidacion> list) {
        CoaJuicioPredio det;
        try {
            juicio = (CoaJuicio) manager.persist(juicio);
            for (RenLiquidacion l : list) {
                det = new CoaJuicioPredio();
                det.setJuicio(juicio);
                det.setAbogadoJuicio(juicio.getAbogadoJuicio());
                det.setLiquidacion(l);
                manager.persist(det);
            }

            for (RenLiquidacion l : list) {
                l.setCoactiva(Boolean.TRUE);
                //l.setEstadoCoactiva(3);
                manager.update(l);
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public HistoricoTramites saveJuicioCoactivoNuevo(HistoricoTramites ht, CoaJuicio juicio, List<RenLiquidacion> list) {
        CoaJuicioPredio det;
        try {
            GeTipoTramite tipo = (GeTipoTramite) manager.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, "juicioCoactiva"});
            ht.setTipoTramite(tipo);
            ht.setEstado("Pendiente");
            ht.setFecha(new Date());
            ht.setTipoTramiteNombre(tipo.getDescripcion());
            ht.setId(sec.getSecuenciasTram("SGM"));
            ht = (HistoricoTramites) manager.persist(ht);

            juicio.setTramite(ht);
            juicio = (CoaJuicio) manager.persist(juicio);
            for (RenLiquidacion l : list) {
                det = new CoaJuicioPredio();
                det.setJuicio(juicio);
                //det.setAnioDeuda(l.getAnio());
                //det.setPredio(l.getPredio());
                //det.setValorDeuda(l.getTotalPago());
                det.setAbogadoJuicio(juicio.getAbogadoJuicio());
                det.setLiquidacion(l);
                manager.persist(det);
            }
            ht.setCoaJuicio(juicio);

            /*EN ESTADO INICIAL LAS EMISIONES AUN NO ESTAN EN COACTIVA*/
 /*for (RenLiquidacion l : list) {
             l.setCoactiva(Boolean.TRUE);
             l.setEstadoCoactiva(2);
             manager.update(l);
             }*/
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return ht;
    }

    @Override
    public Boolean updateEmisionesJuicio(Long idJuicio) {
        List<RenLiquidacion> list;
        try {
            list = manager.findAll(Querys.getLiquidacionesCoactivaByJuicio, new String[]{"idJuicio"}, new Object[]{idJuicio});
            if (list != null && !list.isEmpty()) {
                for (RenLiquidacion l : list) {
                    l.setCoactiva(Boolean.TRUE);
                    l.setEstadoCoactiva(2);
                    manager.update(l);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public List<RenLiquidacion> getLiquidacionesCoactivaByJuicio(Long idJuicio) {
        try {
            return manager.findAll(Querys.getLiquidacionesCoactivaByJuicio, new String[]{"idJuicio"}, new Object[]{idJuicio});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public RecActasEspeciesDet getActaByEspecieYUser(Long especie, Long user) {
        try {
            return (RecActasEspeciesDet) manager.findUnique(Querys.getActaByEspecieUser, new String[]{"idEspecie", "idUser"}, new Object[]{especie, user});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public RenLiquidacion grabarEmisionPredial(CatPredio predio, Long anio, AclUser usuario) {
        Map<String, Object> paramt = new HashMap<>();
        RenTipoLiquidacion tipoLiquidacion;
        RenLiquidacion emision = new RenLiquidacion();
        BigDecimal total = BigDecimal.ZERO;
        List<RenDetLiquidacion> detalleLiquidacion;

        GroovyUtil gutil;
        MatFormulaTramite formula;

        paramt.put("id", 13L);
        try {
            tipoLiquidacion = (RenTipoLiquidacion) manager.findObjectByParameter(RenTipoLiquidacion.class, paramt);

            //DATOS LIQUIDACION
            emision.setTipoLiquidacion(tipoLiquidacion);
            emision.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            emision.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(new Integer(anio + ""), tipoLiquidacion.getId()));
            emision.setIdLiquidacion(tipoLiquidacion.getPrefijo() + "-" + Utils.completarCadenaConCeros(emision.getNumLiquidacion().toString(), 6));
            emision.setNumComprobante(BigInteger.ZERO);
            emision.setUsuarioIngreso(usuario.getUsuario());
            emision.setFechaIngreso(new Date());
            emision.setAnio(new Integer(anio + ""));
            emision.setComprador((predio.getCatPredioPropietarioCollection() != null && !predio.getCatPredioPropietarioCollection().isEmpty()) ? ((List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection()).get(0).getEnte() : null);
            emision.setCoactiva(Boolean.FALSE);
            emision.setEstadoCoactiva(1);
            //emision.setEstado(true);
            //detalleLiquidacion= obtenerDetalleLiquidacion(predio, anio);
            formula = (MatFormulaTramite) manager.find(MatFormulaTramite.class, 33L);
            gutil = new GroovyUtil(formula.getFormula());
            gutil.setProperty("predio", predio);
            gutil.setProperty("anio", anio);
            detalleLiquidacion = (List<RenDetLiquidacion>) gutil.getExpression("getValorEmision", new Object[]{});

            //CALCULO DEL TOTAL A CANCELAR
            if (detalleLiquidacion != null && !detalleLiquidacion.isEmpty()) {
                for (RenDetLiquidacion dl : detalleLiquidacion) {
                    total = total.add(dl.getValor());
                }
                emision.setTotalPago(total);
                emision.setSaldo(total);

                //VERIFICAR SI SE DEBE GRABAR LA EMISION
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    //DATOS PREDIO
                    emision.setPredio(predio);
                    emision.setAvaluoConstruccion(predio.getAvaluoConstruccion());
                    emision.setAvaluoSolar(predio.getAvaluoSolar());
                    emision.setAvaluoMunicipal(predio.getAvaluoMunicipal());
                    emision.setAreaTotal(predio.getCatPredioS4() == null ? null : predio.getCatPredioS4().getAreaCalculada()); //AREA TOTAL DE LA LIQUIDACION GUARDA EL AREA DE SOLAR DEL PREDIO
                    emision = (RenLiquidacion) manager.persist(emision);

                    //DETALLE DE LIQUIDACION
                    if (!detalleLiquidacion.isEmpty()) {
                        for (RenDetLiquidacion dl : detalleLiquidacion) {
                            dl.setLiquidacion(emision);
                            manager.persist(dl);
                        }
                    }
                    emision.setRenDetLiquidacionCollection(detalleLiquidacion);
                }
                emision.setTotalPago(total);
                emision.setSaldo(total);
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return emision;
    }

//    @Lock(LockType.READ)
    @Override
    public RenLiquidacion realizarDescuentoRecargaInteresPredial(RenLiquidacion emision, Date fechaPago) {
        Boolean aplicaRemision = interesServices.aplicaRemision(emision);
        Map<String, Object> paramt;
        CtlgDescuentoEmision descuento;
        RenRubrosLiquidacion rubroLiquidacion;
        Date fecha = new Date();
        String fechaParaPh = "15/02/2018";
        if (fechaPago != null) {
            fecha = fechaPago;
        }

        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        String fechaSistema = formateador.format(fecha);

        Date fechaDate1;
        try {
            fechaDate1 = formateador.parse(fechaSistema);
            Date fechaDate2 = formateador.parse(fechaParaPh);
            if (emision.getPredio() != null) {
                if (emision.getPredio().getPropiedadHorizontal() && fechaDate1.before(fechaDate2)) {
                    fecha = formateador.parse("01/01/2018");
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        Integer dia = Utils.getDateValues("D", fecha);
        Integer mes = Utils.getDateValues("M", fecha);
        Integer anio = Utils.getDateValues("Y", fecha);
        BigDecimal valorImpuesto = BigDecimal.ZERO;
        try {
            //UNA EMISION SIEMPRE TIENE EL RUBRO DE IMPUESTO PREDIAL 
            for (RenDetLiquidacion rubro : emision.getRenDetLiquidacionCollection()) {
                rubroLiquidacion = manager.find(RenRubrosLiquidacion.class, rubro.getRubro());
                if (rubroLiquidacion != null) {
                    if (rubroLiquidacion.getCodigoRubro().equals(1L)) {
                        valorImpuesto = rubro.getValor();
                        break;
                    }

                }
            }
            //SE REALIZA UNA SOLO VEZ EL RECARGO O EL DESCUENTO
            emision.setRecargo(new BigDecimal("0.00"));
            emision.setDescuento(new BigDecimal("0.00"));
            if (emision.getRenPagoCollection() == null || emision.getRenPagoCollection().isEmpty()) {
                paramt = new HashMap<>();
                if (mes + 1 < 7 && anio.equals(emision.getAnio())) {
                    //SE REALIZA DECUENTO - DEACUERDO AL MES Y QUINCENA ANTES DEL MES DE JULIO
                    paramt.put("mes", mes + 1);
                    paramt.put("quincena", dia > 15 ? 2 : 1);
                    descuento = (CtlgDescuentoEmision) manager.findObjectByParameter(Querys.descuentoFecha, paramt);
                    emision.setDescuento(valorImpuesto.multiply(descuento.getPorcentaje()).divide(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
                } else {
                    // SE REALIZA RECARGO - DESPUES DE JUNIO 10% DEL IMPUESTO
                    if (!aplicaRemision) {
                        emision.setRecargo(valorImpuesto.multiply(new BigDecimal("10")).divide(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            }
            //INTERES EMISION PREDIAL AÑO VENCIDO
            emision.setInteres(new BigDecimal("0.00"));
//
//            if (emision.getAnio() < anio) {
//                if (emision.getRenPagoCollection() == null || emision.getRenPagoCollection().isEmpty()) {// CONSULTAR CON UN LSTADO
//                    Calendar fechaInteres = Calendar.getInstance();
//                    fechaInteres.set(emision.getAnio() + 1, Calendar.JANUARY, 1, 0, 0, 0);
//                    emision.setInteres(this.generarInteres(emision.getSaldo(), emision.getAnio()));
//                } else {
//                    //CONSULTAR ULTIMO PAGO - SI EL ULTIMO PAGO FUE REALIZADO EN EL MISMO ANIO DE EMISION LA FECHA DE INTERES TB DESDE EL PRIMER DIA DE LA EMISION VENCDA
//
//                    //emision.setInteres(this.generarInteres(emision.getSaldo(), ((List<RenPago>) emision.getRenPagoCollection()).get(emision.getRenPagoCollection().size() - 1).getFechaPago(), fechaPago));
//                    emision.setInteres(new BigDecimal("0.00"));
//                }
//            }
            if (!aplicaRemision) {
                if (emision.getAnio() < anio) {
                    if (emision.getRenPagoCollection() == null || emision.getRenPagoCollection().isEmpty()) {// CONSULTAR CON UN LSTADO
                        Calendar fechaInteres = Calendar.getInstance();
                        fechaInteres.set(emision.getAnio() + 1, Calendar.JANUARY, 1, 0, 0, 0);
                        emision.setInteres(this.generarInteres(emision.getSaldo(), emision.getAnio()));
                    } else {
                        //CONSULTAR ULTIMO PAGO - SI EL ULTIMO PAGO FUE REALIZADO EN EL MISMO ANIO DE EMISION LA FECHA DE INTERES TB DESDE EL PRIMER DIA DE LA EMISION VENCDA
                        //emision.setInteres(this.generarInteres(emision.getSaldo(), ((List<RenPago>) emision.getRenPagoCollection()).get(emision.getRenPagoCollection().size() - 1).getFechaPago(), fechaPago));
                        emision.setInteres(new BigDecimal("0.00"));
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        //return emision;
        return emision;
    }

    @Override
    public RenLiquidacion realizarPagosCoactiva(List<RenLiquidacion> emisiones, String cajero) {
        //SOLO CUANDO SE PAGA VARIAS EMISIONES
        Map<String, Object> map = new HashMap<>();
        Collection<RenDetLiquidacion> detalles = new ArrayList<>();
        RenLiquidacion temporal = new RenLiquidacion();
        RenLiquidacion liquidacion = new RenLiquidacion();
        RenDetLiquidacion detalle;
        Boolean coactiva = false;
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (RenLiquidacion li : emisiones) {
                if (li.getEstadoCoactiva() == 2) {
                    li = this.realizarDescuentoRecargaInteresPredial(li, null);
                    li.calcularPago();
                    coactiva = true;
                    temporal = li;
                    total = total.add(li.getValorCoactiva());
                }
            }
            if (coactiva) {
                map.put("tipoLiquidacion", new RenTipoLiquidacion(49L)); // TIPO LIQUIDACION COACTIVA
                map.put("codigoRubro", 1L);
                RenRubrosLiquidacion rubro = manager.findObjectByParameter(RenRubrosLiquidacion.class, map);
                liquidacion.setComprador(temporal.getComprador());
                liquidacion.setNombreComprador(temporal.getNombreComprador());
                liquidacion.setTotalPago(total);
                liquidacion.setAnio(Calendar.getInstance().get(Calendar.YEAR));
                liquidacion.setPredio(temporal.getPredio());
                liquidacion.setSaldo(total);
                liquidacion.setTipoLiquidacion(rubro.getTipoLiquidacion());
                liquidacion.setFechaIngreso(new Date());
                liquidacion.setUsuarioIngreso(cajero);
                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                liquidacion.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(liquidacion.getAnio(), rubro.getTipoLiquidacion().getId()));
                liquidacion.setIdLiquidacion(rubro.getTipoLiquidacion().getPrefijo() + "-" + Utils.completarCadenaConCeros(liquidacion.getNumLiquidacion().toString(), 6));
                liquidacion.setNumComprobante(BigInteger.ZERO);
                liquidacion.setCoactiva(Boolean.FALSE);
                liquidacion.setEstadoCoactiva(1);
                liquidacion = (RenLiquidacion) manager.persist(liquidacion);
                for (RenLiquidacion li : emisiones) {
                    if (li.getEstadoCoactiva() == 2) {
                        detalle = new RenDetLiquidacion();
                        detalle.setCantidad(li.getAnio());
                        detalle.setLiquidacion(liquidacion);
                        detalle.setRubro(rubro.getId());
                        detalle.setValor(li.getValorCoactiva());
                        detalle = (RenDetLiquidacion) manager.persist(detalle);
                        detalles.add(detalle);
                    }
                }
                liquidacion.setRenDetLiquidacionCollection(detalles);
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return liquidacion;
    }

    @Override
    public RenLiquidacion realizarUnPagoCoactiva(RenLiquidacion liquidacion, BigDecimal total, String cajero) {
        //SOLO CUANDO SE PAGA UNA SOLA EMISION
        Map<String, Object> map = new HashMap<>();
        RenLiquidacion nueva = new RenLiquidacion();
        RenDetLiquidacion detalle;
        Collection<RenDetLiquidacion> detalles = new ArrayList<>();
        try {
            map.put("tipoLiquidacion", new RenTipoLiquidacion(49L)); // TIPO LIQUIDACION COACTIVA
            map.put("codigoRubro", 1L);
            RenRubrosLiquidacion rubro = manager.findObjectByParameter(RenRubrosLiquidacion.class, map);
            nueva.setComprador(liquidacion.getComprador());
            nueva.setNombreComprador(liquidacion.getNombreComprador());
            System.out.println(liquidacion.getNombreComprador());
            nueva.setTotalPago(total);
            nueva.setTipoLiquidacion(rubro.getTipoLiquidacion());
            nueva.setAnio(Calendar.getInstance().get(Calendar.YEAR));
            nueva.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            nueva.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(nueva.getAnio(), rubro.getTipoLiquidacion().getId()));
            nueva.setIdLiquidacion(rubro.getTipoLiquidacion().getPrefijo() + "-" + Utils.completarCadenaConCeros(nueva.getNumLiquidacion().toString(), 6));
            nueva.setNumComprobante(BigInteger.ZERO);
            nueva.setPredio(liquidacion.getPredio());
            nueva.setSaldo(total);
            nueva.setFechaIngreso(new Date());
            nueva.setUsuarioIngreso(cajero);
            nueva.setCoactiva(Boolean.FALSE);
            nueva.setEstadoCoactiva(1);
            if (liquidacion.getValorCoactiva().compareTo(total) > 0) {
                nueva.setObservacion("ABONO");
            }
            nueva = (RenLiquidacion) manager.persist(nueva);
            detalle = new RenDetLiquidacion();
            detalle.setCantidad(liquidacion.getAnio());
            detalle.setLiquidacion(nueva);
            detalle.setRubro(rubro.getId());
            detalle.setValor(total);
            detalle = (RenDetLiquidacion) manager.persist(detalle);
            detalles.add(detalle);
            nueva.setRenDetLiquidacionCollection(detalles);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return nueva;
    }

    @Lock(LockType.WRITE)
    @Override
    public RenPago realizarPago(RenLiquidacion liquidacion, RenPago pago, AclUser cajero, Boolean isSac) {
        Long numComprobante;
        List<ConsolidacionBanco> listCB;
        // COMPROBANTE DEL SGM
        numComprobante = sec.getNumComprobante();

        List<RenPagoDetalle> detallePago;
        BigDecimal valorLiquidacion;
        RenPagoRubro pagoRubro;
        BigDecimal valorRecaudacion;
        MejPagoRubroMejora pagoRubroMejora;
        BigDecimal valorRecaudacionMejora;
        Map<String, Object> parametros;
        try {
            detallePago = (List<RenPagoDetalle>) pago.getRenPagoDetalles();
            pago.setFechaPago(new Date());
            pago.setEstado(true);
            pago.setNumComprobante(numComprobante);
            pago.setLiquidacion(liquidacion);
            pago.setCajero(cajero);
            pago.setDescuento(liquidacion.getDescuento());
            pago.setRecargo(liquidacion.getRecargo());
            pago.setContribuyente(liquidacion.getComprador());
            pago.setNombreContribuyente(liquidacion.getNombreComprador());
            pago.setInteres(liquidacion.getInteres());
            pago = (RenPago) manager.persist(pago);
            //ACTUALIZACION DE TABLA CONSOLIDACIONBANCO
            for (RenPagoDetalle det : detallePago) {
                det.setPago(pago);
                manager.persist(det);
                if (det.getTipoPago() == 5) {
                    if (liquidacion.getTipoLiquidacion().getId() == 13L && liquidacion.getPredio() != null) {
                        parametros = new HashMap<>();
                        parametros.put("numPredio", liquidacion.getPredio().getNumPredio());
                        parametros.put("anio", liquidacion.getAnio());
                        listCB = manager.findObjectByParameterList(ConsolidacionBanco.class, parametros);
                        if (listCB != null && !listCB.isEmpty()) {
                            for (ConsolidacionBanco cb : listCB) {
                                cb.setEstado("P");
                                cb.setNumComprobante(new BigInteger(numComprobante + ""));
                                manager.persist(cb);
                            }
                        }
                    }
                }
            }
            liquidacion.setNumComprobante(new BigInteger(pago.getNumComprobante() + ""));
            liquidacion.setSaldo(liquidacion.getSaldo().subtract(pago.getValor().subtract(pago.getInteres()).subtract(pago.getRecargo()).add(pago.getDescuento())));
            if (liquidacion.getSaldo().compareTo(BigDecimal.ZERO) < 1) {
                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(1L));
                /*Anyelo*/
                if (liquidacion.getEstadoCoactiva() != null && liquidacion.getEstadoCoactiva() == 2) {
                    liquidacion.setEstadoCoactiva(3);
                }
            }
            //VALOR DE LIQUIDACION
            valorLiquidacion = pago.getValor().subtract(pago.getInteres()).subtract(pago.getRecargo()).add(pago.getDescuento());
            for (RenDetLiquidacion rubro : liquidacion.getRenDetLiquidacionCollection()) {
                //RUBROS QUE ESTAN PENDIENTES DE RECAUDAR
                if (rubro != null) {
                    if (rubro.getValor() != null) {
                        pagoRubro = new RenPagoRubro();
                        //REGISTRO VALOR RECAUDADO POR RUBRO
                        pagoRubro.setPago(pago);
                        pagoRubro.setRubro(new RenRubrosLiquidacion(rubro.getRubro()));
                        pagoRubro.setValor(rubro.getValor());
                        pagoRubro = (RenPagoRubro) manager.persist(pagoRubro);

                    }

                }

            }
            manager.persist(liquidacion);

            // OBSERVACION DEL SALDO DE UNA LIQUIDACION
            if (liquidacion.getEstadoLiquidacion().getId() == 2L) {
                pago.setObservacion("Saldo: " + liquidacion.getTotalPago().subtract(pago.getValor()) + ".");
            }

            //
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return pago;
    }

    @Override
    public RenLiquidacion editarLiquidacion(RenLiquidacion liquidacion) {
        liquidacion = (RenLiquidacion) manager.persist(liquidacion);
        return liquidacion;
    }

    @Override
    public RenLiquidacion grabarLiquidacion(RenLiquidacion liquidacion) {
        List<RenDetLiquidacion> detLiquidacion = new ArrayList<>();
        List<RenDetLiquidacion> detLiquidacionPersist = new ArrayList<>();
        RenDetLiquidacion detalle;
        liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
        liquidacion.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(liquidacion.getAnio(), liquidacion.getTipoLiquidacion().getId()));
        liquidacion.setIdLiquidacion(liquidacion.getAnio() + "-" + Utils.completarCadenaConCeros(liquidacion.getNumLiquidacion().toString(), 6) + "-" + liquidacion.getTipoLiquidacion().getPrefijo());
        liquidacion.setNumComprobante(BigInteger.ZERO);
        liquidacion.setEstadoCoactiva(1);
        for (RenRubrosLiquidacion r : liquidacion.getTipoLiquidacion().getRenRubrosLiquidacionCollection()) {
            detalle = new RenDetLiquidacion();
            detalle.setRubro(r.getId());
            detalle.setValor(r.getValor());
            if (r.getCantidad() != null) {
                detalle.setCantidad(r.getCantidad());
            }
            if (r.getActa() != null) {
                detalle.setRecActasEspeciesDet(r.getActa().getId());
                detalle.setDesde(BigInteger.valueOf(r.getActa().getDesdeTemp()));
                detalle.setHasta(BigInteger.valueOf(r.getActa().getHastaTemp()));
                r.getActa().setUltimoVendido(r.getActa().getHastaTemp());
                if (r.getActa().getDisponibles() == 0) {
                    r.getActa().setEstado("C");
                }
                manager.update(r.getActa());
            }
            detLiquidacion.add(detalle);
        }
        liquidacion = (RenLiquidacion) manager.persist(liquidacion);
        for (RenDetLiquidacion d : detLiquidacion) {
            d.setLiquidacion(liquidacion);
            d = (RenDetLiquidacion) manager.persist(d);
            detLiquidacionPersist.add(d);
        }
        liquidacion.setRenDetLiquidacionCollection(detLiquidacionPersist);
        return liquidacion;
    }

    @Override
    public RenLiquidacion grabaLiquidacionRubro(RenLiquidacion liquidacion, RenDetLiquidacion detalle) {
        List<RenDetLiquidacion> detLiquidacion = new ArrayList<>();
        try {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            liquidacion.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(liquidacion.getAnio(), liquidacion.getTipoLiquidacion().getId()));
            liquidacion.setIdLiquidacion(liquidacion.getTipoLiquidacion().getPrefijo() + "-" + Utils.completarCadenaConCeros(liquidacion.getNumLiquidacion().toString(), 6));
            liquidacion.setNumComprobante(BigInteger.ZERO);
            liquidacion.setEstadoCoactiva(1);
            liquidacion = (RenLiquidacion) manager.persist(liquidacion);
            detalle.setLiquidacion(liquidacion);
            detalle = (RenDetLiquidacion) manager.persist(detalle);
            detLiquidacion.add(detalle);
            liquidacion.setRenDetLiquidacionCollection(detLiquidacion);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return liquidacion;
    }

    @Override
    public Boolean actualizaEmisionesCoactiva(Long idLiquidacion) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("liquidacion", new RenLiquidacion(idLiquidacion));
            RenDetLiquidacion detalle = manager.findObjectByParameter(RenDetLiquidacion.class, map);
            List<RenLiquidacion> list = manager.findAll(Querys.getEmisionesEnCoactiva, new String[]{"idPredio", "anio"}, new Object[]{detalle.getLiquidacion().getPredio(), detalle.getHasta().intValue()});
            System.out.println("// lista: " + list);
            if (list != null) {
                for (RenLiquidacion liq : list) {
                    liq.setEstadoCoactiva(3);
                    manager.update(liq);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public Object grabarMejora(Map<String, Object> parametros) {
        return manager.executeFunction("mejoras.generar_mejora", parametros, Boolean.FALSE);
    }

    @Override
    public Object grabarMejora(List<Object> parametros) {
        return manager.executeFunction("mejoras.generar_mejora", parametros, Boolean.FALSE);
    }

    /**
     * predio anio
     *
     * @param parametros
     */
    @Override
    public Long grabarEmision(List<Object> parametros) {
        return Long.parseLong(manager.executeFunction(" sgm_app.emision_predial", parametros, Boolean.FALSE) + "");
    }

    /**
     * predio anio usuario
     *
     * @param parametros
     * @return
     */
    @Override
    public BigInteger grabarEmisionRural(List<Object> parametros) {
        return (BigInteger) manager.executeFunction(" sgm_app.emision_predial_rural", parametros, Boolean.FALSE);
    }

    @Override
    public void grabarEmisionGlobal(Map<String, Object> parametros) {
        manager.executeFunction(" sgm_app.emision_anual_predial", parametros, Boolean.FALSE);
    }

    @Override
    public void grabarEmisionRuralGlobal(Map<String, Object> parametros) {
        manager.executeFunction(" sgm_app.emision_anual_predial_rural", parametros, Boolean.FALSE);
    }

    @Override
    public List<Cobros> getCobros(RenPago pago) {
        if (pago == null) {
            return null;
        }

        List<Cobros> cobros = new ArrayList<>();

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("pago", pago);
            List<RenPagoDetalle> pagos = manager.findObjectByParameterList(RenPagoDetalle.class, map);
            if (pagos == null) {
                return null;
            } else {
                Cobros c;
                for (RenPagoDetalle p : pagos) {
                    if (p.getTipoPago() > 0) {
                        Long tipopago = p.getTipoPago();
                        switch (tipopago.intValue()) {
                            case 2: // Tarjeta de credito
                                c = new Cobros(pago.getNumComprobante().longValue(),
                                        p.getTcBanco() == null ? "TARJETA DE CREDITO" : p.getTcBanco().getDescripcion(),
                                        p.getTcNumTarjeta(), p.getTcBaucher(), pago.getValor(), tipopago.intValue(), p.getTcTitular());
                                cobros.add(c);
                                break;
                            case 3:// Nota de Credito
                                c = new Cobros(pago.getNumComprobante().longValue(), "NOTA DE CREDITO",
                                        null, p.getNcNumCredito(), p.getValor(), tipopago.intValue(), "");
                                cobros.add(c);
                                break;
                            case 4:// Cheque                          
                                c = new Cobros(pago.getNumComprobante().longValue(),
                                        p.getChBanco() == null ? "CHEQUE" : p.getChBanco().getDescripcion(),
                                        p.getChNumCuenta(), p.getChNumCheque(), p.getValor(), tipopago.intValue(), "");
                                cobros.add(c);

                                break;
                            case 5:// Transferencia

                                c = new Cobros(pago.getNumComprobante().longValue(),
                                        p.getTrBanco() == null ? "TRANSFERENCIA" : p.getTrBanco().getDescripcion(), null, p.getTrNumTransferencia(),
                                        p.getValor(), tipopago.intValue(), Utils.dateFormatPattern("dd/MM/yyyy HH:mm", p.getTrFecha()));
                                cobros.add(c);

                                break;
                            case 6:// Dinero electronico

                                break;
                            default: // Efectivo

                                break;
                        }
                    }
                }
            }
            return cobros;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<Cobros> getComprobante(RenLiquidacion liquidacion) {
        List<Cobros> cs = new ArrayList<>();
        List<Object[]> c = manager.getManyColumnsResults(QuerysFinanciero.getComprobante, new String[]{"liquidacion"}, new Object[]{liquidacion});
        if (c != null) {
            for (Object[] c1 : c) {
                Cobros cobros = new Cobros((Long) c1[1], (String) c1[0], BigDecimal.ZERO, ((c1[2] == null) ? null : new BigInteger(c1[2].toString())));
                cs.add(cobros);
//                System.out.println(cobros);
            }
            return cs;
        } else {
            return null;
        }
    }

    @Override
    public List<RenLiquidacion> getEmisionesByPredio(CatPredio p, Map<String, Object> parametros) {
        List<RenLiquidacion> emisiones;
        try {
            parametros.put("predio", p);
            emisiones = manager.findNamedQuery(Querys.emisionByPredio, parametros);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return emisiones;
    }

    @Override
    public List<RenLiquidacion> getEmisionesByPredioRustico(CatPredioRustico pr, Map<String, Object> parametros) {
        List<RenLiquidacion> emisiones;
        try {
            parametros.put("predioRustico", pr);
            emisiones = manager.findNamedQuery(Querys.emisionByPredioRural, parametros);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return emisiones;
    }

    @Override
    public List<RenLiquidacion> getEmisionesByPredioRustico2017(EmisionesRuralesExcel rural2017, Map<String, Object> parametros) {
        List<RenLiquidacion> emisiones;
        try {
            System.out.println("RecaudacionesEjb" + rural2017);
            parametros.put("ruralExcel", rural2017);
            System.out.println("parametros" + parametros);
            emisiones = manager.findNamedQuery(Querys.emisionByPredioFox2017, parametros);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return emisiones;
    }

    @Override
    public List<RenLiquidacion> getEmisionesByAME(String claveAME, Integer anio) {
        List<RenLiquidacion> emisiones;
        try {
            emisiones = manager.findAll(Querys.emisionByPredioAME, new String[]{"claveAME", "anio"}, new Object[]{claveAME, anio});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return emisiones;
    }

    @Override
    public BigDecimal getTotalEmisionesByPredio(CatPredio p, Map<String, Object> parametros) {
        BigDecimal total;
        try {
            parametros.put("predio", p);
            total = (BigDecimal) manager.findObjectByParameter(Querys.totalEmisionByPredio, parametros);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return total != null ? total : new BigDecimal("0.00");
    }

//    @Lock(LockType.READ)
    @Override
    public BigDecimal generarInteres(BigDecimal valor, Integer anio) {
        RenIntereses interes;
        BigDecimal interesValor = new BigDecimal("0.00");
        try {
            interes = (RenIntereses) manager.find(QuerysFinanciero.getInteresByAnio, new String[]{"anio"}, new Object[]{anio});
            if (interes != null) {
                interes.setPorcentaje(interes.getPorcentaje().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                interesValor = valor.multiply(interes.getPorcentaje()).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return interesValor;
    }

    @Override
    public CtlgSalario grabraSalario(CtlgSalario salario) {
        CtlgSalario i;
        try {
            i = (CtlgSalario) manager.persist(salario);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return i;
    }

    @Override
    public RenIntereses grabraInteres(RenIntereses interes) {
        RenIntereses i;
        try {
            i = (RenIntereses) manager.persist(interes);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return i;
    }

    @Override
    public List<ParteRecaudaciones> parteRecaudacioneses(Date fechaParte, BigDecimal total) {
        List<ParteRecaudaciones> resultParte;
        List<com.origami.sgm.entities.ParteRecaudaciones> parte;
        try {
            List temp = manager.findAll(QuerysFinanciero.getSumRubroByCtaPres,
                    new String[]{"fecha", "anio"}, new Object[]{fechaParte, Utils.getAnio(fechaParte)});
            // BOMBEROS
            List<ParteRecaudaciones> resultBomb = manager.getSqlQueryParametros(ParteRecaudaciones.class,
                    QuerysFinanciero.getSumBomberos, new String[]{"fecha"}, new Object[]{fechaParte});
            //Parte de Fondos ajenos
            List<ParteRecaudaciones> resultFondosAj = manager.getSqlQueryParametros(ParteRecaudaciones.class,
                    QuerysFinanciero.getSumFondAjenos, new String[]{"fecha"}, new Object[]{fechaParte});

            parte = manager.findAllObjectOrder(com.origami.sgm.entities.ParteRecaudaciones.class, new String[]{"id", "orden"}, true);
//            //RECORREMOS LA LISTA DE LA CONSULTA PARA AGREGAR EL RESULTADO DE LA CONSULTA AL PARTE GENERAL
            for (Iterator it = temp.iterator(); it.hasNext();) {
                Object[] p = (Object[]) it.next();
                com.origami.sgm.entities.ParteRecaudaciones r = (com.origami.sgm.entities.ParteRecaudaciones) p[0];
                r.setValor(new BigDecimal(p[1].toString()));
                // buscamos en el parte 
                int indexParteFinal = parte.indexOf(r);
                parte.set(indexParteFinal, r);
            }
            resultParte = new ArrayList<>();
            int count = 0;
            if (parte != null && !parte.isEmpty()) {
                for (com.origami.sgm.entities.ParteRecaudaciones udPt : parte) {
                    if (udPt.getTipo() == 1) {
                        String q = null;
                        BigDecimal valor = BigDecimal.ZERO;
                        if (null != udPt.getCodigo()) {
                            switch (udPt.getCodigo()) {
                                case "000000000000000":
                                    valor = (BigDecimal) manager.find(QuerysFinanciero.getDescuento, new String[]{"fecha"}, new Object[]{fechaParte});
                                    udPt.setValor((valor == null ? BigDecimal.ZERO : valor));
                                    break;
                                case "170301000000000":
                                    try {
                                        BigDecimal trub = (BigDecimal) manager.find(QuerysFinanciero.getMoraMulta, new String[]{"fecha"}, new Object[]{fechaParte});
                                        udPt.setValor((trub == null ? BigDecimal.ZERO : trub).add((udPt.getValor() == null ? BigDecimal.ZERO : udPt.getValor())));
                                        // Interes de y recargo
                                        Object[] v2 = (Object[]) manager.find(QuerysFinanciero.getInteresRecargo, new String[]{"fecha"}, new Object[]{fechaParte});
                                        valor = (new BigDecimal(v2[0].toString())).add(new BigDecimal(v2[1].toString()));
                                        udPt.setValor((valor == null ? BigDecimal.ZERO : valor).add((udPt.getValor() == null ? BigDecimal.ZERO : udPt.getValor())));
                                    } catch (Exception e) {
                                        Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "", e);
                                    }
                                    break;
                                case "110201050000000": // años anteriores de Predios Urbanos
                                    BigDecimal trub = (BigDecimal) manager.find(QuerysFinanciero.getPredioUrbAnioAnt, new String[]{"anio", "fecha"}, new Object[]{Utils.getAnio(fechaParte), fechaParte});
                                    udPt.setValor(trub);
                                    break;
                                case "110202030000000": // PREDIOS RUSTICOS AÑOS ANTERIORES
                                    BigDecimal valorRusAnt = (BigDecimal) manager.find(QuerysFinanciero.getPredioRusAnioAnt, new String[]{"anio", "fecha"}, new Object[]{Utils.getAnio(fechaParte), fechaParte});
                                    udPt.setValor(valorRusAnt);
                                    break;
                            }
                        }
                        int index = 1;
                        // Solo para este registro se retrocede 3 indices para restar el valor de años enteriores
                        if (udPt.getId() == 10L) {
                            index = 3;
                        }
                        // Si el rubro es de anios anteriores se hace la resta del valor al anio actual
                        if (udPt.getAnioAnterior() && !udPt.getId().equals(10L)) {
                            com.origami.sgm.entities.ParteRecaudaciones anioAnt = parte.get(parte.indexOf(udPt) - index);
                            anioAnt.setValor(anioAnt.getValor().subtract(udPt.getValor()));
                            findPadre(parte, udPt, false);
                        }
                        if (udPt.getPadre() != null) {
                            if (!"000000000000000".equals(udPt.getCodigo())) {
                                findPadre(parte, udPt, true);
                            } else {
                                findPadre(parte, udPt, false);
                            }
                        }
                    }
                }
                for (com.origami.sgm.entities.ParteRecaudaciones parte2 : parte) {
                    if (parte2.getValor().compareTo(BigDecimal.ZERO) != 0) {
                        resultParte.add(new ParteRecaudaciones(BigInteger.valueOf(parte2.getId()), parte2.getCodigo(), parte2.getDescripcion(),
                                parte2.getCtaTransaccion(), parte2.getOrden(), parte2.getPadre(), parte2.getValor(), parte2.getTipo()));
                        count++;
                    }
                }
            }
            if (resultBomb != null && !resultBomb.isEmpty()) {
                for (ParteRecaudaciones b : resultBomb) {
                    System.out.println("Valor Bomberos: " + b.getValorAcumulado());
                }
                resultParte.addAll(resultBomb);
            }
            if (resultFondosAj != null && !resultFondosAj.isEmpty()) {
                resultParte.addAll(resultFondosAj);
            }

        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "Generar Parte", e);
            return null;
        }
        return resultParte;
    }

    private void findPadre(List<com.origami.sgm.entities.ParteRecaudaciones> parte, com.origami.sgm.entities.ParteRecaudaciones rowParte, Boolean agregar) {
        Boolean tienePadre = true;
        Integer idPadre = rowParte.getPadre();
        BigDecimal v = (rowParte.getValor() == null) ? BigDecimal.ZERO : rowParte.getValor();
        int iterCount = 0;
        while (tienePadre) {
            int index = parte.indexOf(new com.origami.sgm.entities.ParteRecaudaciones(Long.valueOf(idPadre)));
            com.origami.sgm.entities.ParteRecaudaciones findPadre = parte.get(index);
            if (findPadre.getId().intValue() == idPadre) {
                if (agregar) {
                    findPadre.setValor(findPadre.getValor().add(v));
                } else {
                    findPadre.setValor(findPadre.getValor().subtract(v));
                }
                parte.set(index, findPadre);
                tienePadre = findPadre.getPadre() != null;
                idPadre = findPadre.getPadre();
            }
        }
    }

    @Override
    public List<CatPredio> getListPrediosByPropietario(Long idEnte) {
        Map<String, Object> map = new HashMap<>();
        List<CatPredio> predios = new ArrayList<>();
        try {
            map.put("idEnte", idEnte);
            predios = manager.findNamedQuery(Querys.getPrediosByPropietario, map);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "Generar Parte", e);
        }
        return predios;
    }

    @Override
    public List<CatPredio> getListPrediosByCodigoPredial(CatPredioModel model) {
        Map<String, Object> map = new HashMap<>();
        List<CatPredio> predios = new ArrayList<>();
        try {
            map.put("sector", model.getSector());
            map.put("mz", model.getMz());
            map.put("cdla", model.getCdla());
            map.put("mzdiv", model.getMzDiv());
            map.put("solar", model.getSolar());
            map.put("div1", model.getDiv1());
            map.put("div2", model.getDiv2());
            map.put("div3", model.getDiv3());
            map.put("div4", model.getDiv4());
            map.put("div5", model.getDiv5());
            map.put("div6", model.getDiv6());
            map.put("div7", model.getDiv7());
            map.put("div8", model.getDiv8());
            map.put("div9", model.getDiv9());
            map.put("phv", model.getPhv());
            map.put("phh", model.getPhh());
            map.put("estado", "A");
            predios = manager.findObjectByParameterList(CatPredio.class, map);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return predios;
    }

    @Override
    public RenPago reversarPago(RenPago pago) {
        try {
            BigDecimal valorRecaudadoEmision = pago.getValor().add(pago.getDescuento()).subtract(pago.getInteres()).subtract(pago.getRecargo());
            pago.getLiquidacion().setSaldo(pago.getLiquidacion().getSaldo().add(valorRecaudadoEmision));
            for (RenDetLiquidacion l : pago.getLiquidacion().getRenDetLiquidacionCollection()) {
                for (RenPagoRubro rp : pago.getRenPagoRubros()) {
                    if (l.getRubro().equals(rp.getRubro().getId())) {
                        l.setValorRecaudado(l.getValorRecaudado().subtract(rp.getValor()));
                        manager.persist(l);
                        //REVERSO DE MEJORAS
                        if (l.getRubro() == 7L && l.getMejDetRubroMejorasCollection() != null && !l.getMejDetRubroMejorasCollection().isEmpty()) {
                            for (MejDetRubroMejoras rm : l.getMejDetRubroMejorasCollection()) {
                                for (MejPagoRubroMejora prm : rp.getMejPagoRubroMejoras()) {
                                    if (rm.getUbicacionObra().getId().equals(prm.getUbicacionObra().getId())) {
                                        rm.setSaldo(rm.getSaldo().add(prm.getValor()));
                                        manager.persist(rm);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
            pago.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            manager.persist(pago.getLiquidacion());
            pago.setEstado(Boolean.FALSE);
            pago.setFechaAnulacion(new Date());
            pago = (RenPago) manager.persist(pago);
            //CONSULTAR TODOS LOS PAGOS ACTIVOS DE LA LIQUIDACION DEL PAGO ANULADO
            List<RenPago> pagosAct = obtenerPagos(pago.getLiquidacion(), true);
            //SI NO TE DEVUELDE NADA SE APLICA LA INACTIVACION DE RENLIQUIDACION (DE LAS PERMITIDAS) - INACTIVAR LIQUIDACION ESTADo 3
            if (Utils.isEmpty(pagosAct) && pago.getLiquidacion().getTipoLiquidacion().getPermiteAnulacion()) {
                if (revertirActaEspecies(pago.getLiquidacion().getRenDetLiquidacionCollection())) {
                    pago.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
                    manager.persist(pago.getLiquidacion());
                }
            }
            //SI ES TIPO ESPECIE O CARPETAS ECT VERIFICAR SI ES LA ULTIMA VENDIDA Y REVERSAR EL INVENTARIO, 
            //CASO CONTRARIO SE PASA TITULOS DE CREDITOS
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "Anulacion Pago", e);
            return null;
        }
        return pago;
    }

    @Override
    public RenPago ultimoPago(RenLiquidacion liquidacion) {
        RenPago p;
        try {
            p = (RenPago) manager.find(QuerysFinanciero.ultimoPagoByLiquidacion, new String[]{"liquidacion"}, new Object[]{liquidacion});
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "Generar Parte", e);
            return null;
        }
        return p;
    }

    @Override
    public List<RenParametrosInteresMulta> getListParametrosInteresMulta(RenLiquidacion liquidacion) {
        Map<String, Object> parametros = new HashMap<>();
        Date fecha = new Date();
        Integer dia = Utils.getDateValues("D", fecha);
        Integer mes = Utils.getDateValues("M", fecha);
        Integer anio = Utils.getDateValues("Y", fecha);
        try {
            parametros.put("tipoLiquidacion", liquidacion.getTipoLiquidacion());
            if (liquidacion.getAnio() < anio) {
                return manager.findObjectByParameterList(RenParametrosInteresMulta.class, parametros);
            } else {
                parametros.put("dia", dia);
                parametros.put("mes", mes + 1);
            }
            return manager.findNamedQuery(Querys.getParametrosInteresMulta, parametros);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, "Generar Parte", e);
            return null;
        }
    }

    @Override
    public BigDecimal generarMultas(RenLiquidacion liquidacion, RenParametrosInteresMulta interesMulta) {
        BigDecimal multa;
        try {
            Date fecha = new Date();
            Integer mes = Utils.getDateValues("M", fecha);
            Integer anio = Utils.getDateValues("Y", fecha);
            Integer cantidadMeses;
            cantidadMeses = 12 * (anio - liquidacion.getAnio()) + (mes + 2 - interesMulta.getMes().intValue());// +2 : 1 por la funcion que retorna el mes y +1 por la multa corre desde el mes establecido
            multa = (liquidacion.getSaldo().multiply(interesMulta.getPorcentaje())).multiply(new BigDecimal(cantidadMeses + "")).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return multa;
    }

    @Override
    public BigDecimal valorRecaudarCoactiva(BigDecimal valorTotal) {
        BigDecimal valorCoactiva;
        try {
            valorCoactiva = valorTotal.divide(new BigDecimal("11"), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return valorCoactiva;
    }

    @Override
    public CoaAbogado grabarAbogado(CoaAbogado abogado) {
        CoaAbogado a;
        try {
            a = (CoaAbogado) manager.persist(abogado);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return a;
    }

    private List<RenPago> obtenerPagos(RenLiquidacion liquidacion, boolean estado) {
        try {
            Map<String, Object> paramts = new HashMap<>();
            paramts.put("estado", estado);
            paramts.put("liquidacion", liquidacion);
            return manager.findObjectByParameterList(RenPago.class, paramts);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private RecActasEspeciesDet getActaDetById(Long idActasEspeciesDet) {
        return manager.find(RecActasEspeciesDet.class, idActasEspeciesDet);
    }

    private Boolean revertirActaEspecies(Collection<RenDetLiquidacion> detalle) {
        Boolean actasReversadas = true;
        List<RecActasEspeciesDet> actas = new ArrayList<>();
        for (RenDetLiquidacion d : detalle) {
            if (d.getRecActasEspeciesDet() != null) {
                RecActasEspeciesDet acta = getActaDetById(d.getRecActasEspeciesDet());
                if (acta.getUltimoVendido().equals(d.getHasta().longValue())) {
                    acta.setDisponibles(acta.getDisponibles() + d.getCantidad());
                    acta.setUltimoVendido(d.getDesde().subtract(BigInteger.ONE).longValue());
                    actas.add(acta);
                } else {
                    actasReversadas = false;
                    break;
                }
            }
        }
        if (actasReversadas && actas.size() > 0) {
            manager.saveList(actas);
        }
        return actasReversadas;
    }

    @Override
    public boolean ultimaEspecie(RenLiquidacion liquidacion) {
        if (Utils.isNotEmpty((List<?>) liquidacion.getRenDetLiquidacionCollection())) {
            for (RenDetLiquidacion d : liquidacion.getRenDetLiquidacionCollection()) {
                if (d.getRecActasEspeciesDet() != null) {
                    RecActasEspeciesDet acta = getActaDetById(d.getRecActasEspeciesDet());
                    if (!acta.getUltimoVendido().equals(d.getHasta().longValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Boolean verificarPagoBanco(CatPredio predio) {
        try {
            if (predio == null) {
                return Boolean.FALSE;
            }
            int anio = 0;
            List<ConsolidacionBanco> cbs = manager.findAll(QuerysFinanciero.verificarPagoBanco, new String[]{"numPredio", "anio", "anioFin"}, new Object[]{new BigInteger(predio.getNumPredio().toString()), Utils.getAnio(new Date()) - 1, Utils.getAnio(new Date())});
            return (cbs != null && !cbs.isEmpty());
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public List<CatPredio> getListPrediosByPredioModel(CatPredioModel model) {
        List<CatPredio> predios = new ArrayList<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            switch (model.getTipoConsultaUrbano().intValue()) {
                case 1://NUMERO PREDIAL
                    if (model.getNumPredio() != null && model.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", model.getNumPredio());
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (model.getPropietarioUrbano() != null) {
                        for (CatPredioPropietario pp : model.getPropietarioUrbano().getEnte().getCatPredioPropietarioCollection()) {
                            predios.add(pp.getPredio());
                        }
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (model.getProvincia() > 0 || model.getCanton() > 0 || model.getParroquiaShort() > 0
                            || model.getZona() > 0 || model.getSector() > 0 || model.getMz() > 0 || model.getSolar() > 0
                            || model.getBloque() > 0 || model.getPiso() > 0 || model.getUnidad() > 0) {
                        parametros.put("estado", "A");
                        if (model.getSector() > 0) {
                            parametros.put("provincia", model.getProvincia());
                        }
                        if (model.getMz() > 0) {
                            parametros.put("canton", model.getCanton());
                        }
                        if (model.getCdla() > 0) {
                            parametros.put("parroquia", model.getParroquiaShort());
                        }
                        if (model.getMzDiv() > 0) {
                            parametros.put("zona", model.getZona());
                        }
                        if (model.getSolar() > 0) {
                            parametros.put("sector", model.getSector());
                        }
                        if (model.getDiv1() > 0) {
                            parametros.put("mz", model.getMz());
                        }
                        if (model.getDiv2() > 0) {
                            parametros.put("solar", model.getSolar());
                        }
                        if (model.getDiv3() > 0) {
                            parametros.put("bloque", model.getBloque());
                        }
                        if (model.getDiv4() > 0) {
                            parametros.put("piso", model.getPiso());
                        }
                        if (model.getDiv5() > 0) {
                            parametros.put("unidad", model.getUnidad());
                        }
                    }
                    break;
                case 4://UBICACION
                    if (model.getCiudadela() != null || model.getMzUrb() != null || model.getSlUrb() != null) {
                        if (model.getCiudadela() != null) {
                            parametros.put("ciudadela", model.getCiudadela());
                        }
                        if (model.getMzUrb() != null) {
                            parametros.put("urbMz", model.getMzUrb());
                        }
                        if (model.getSlUrb() != null) {
                            parametros.put("urbSolarnew", model.getSlUrb());
                        }
                    }
                    break;
                case 5://CODIGO ANTERIOR
                    if (model.getPredialAnt() != null) {
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("predialant", model.getPredialAnt());
                    }
                    break;
                default:
                    if (model.getClaveCat() != null) {
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("claveCat", model.getClaveCat());
                    }
                    break;
            }
            if (model.getTipoConsultaUrbano() != 2L) {
                predios = manager.findObjectByParameterList(CatPredio.class, parametros);
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return predios;
    }

    @Override
    public List<CatPredioRustico> getListPrediosRuralesByPredioModel(CatPredioModel model) {
        List<CatPredioRustico> predios = new ArrayList<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            switch (model.getTipoConsultaUrbano().intValue()) {
                case 1://CODIGO PREDIAL.
                    if (model.getRegCatastral() != null || model.getIdPredial() != null || model.getParroquia() != null) {
                        if (model.getRegCatastral() != null) {
                            parametros.put("regCatastral", model.getRegCatastral().trim());
                        }
                        if (model.getIdPredial() != null) {
                            parametros.put("idPredial", model.getIdPredial());
                        }
                        if (model.getParroquia() != null) {
                            parametros.put("parroquia", model.getParroquia());
                        }
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (model.getContribuyenteConsultaRural() != null) {
                        if (model.getContribuyenteConsultaRural().getCatPredioRusticos() != null && !model.getContribuyenteConsultaRural().getCatPredioRusticos().isEmpty()) {
                            if (model.getContribuyenteConsultaRural().getCatPredioRusticos().size() == 1) {
                                parametros.put("regCatastral", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getRegCatastral());
                                parametros.put("idPredial", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getIdPredial());
                                parametros.put("parroquia", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getParroquia());
                            } else {
                                predios.addAll(model.getContribuyenteConsultaRural().getCatPredioRusticos());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            if (predios.isEmpty()) {
                predios = manager.findObjectByParameterList(CatPredioRustico.class, parametros);
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return predios;
    }

    @Override
    public FnSolicitudExoneracion aplicarSolicitud(FnSolicitudExoneracion solicitudExoneracion) {
        try {
            switch (solicitudExoneracion.getExoneracionTipo().getId().intValue()) {
                case 1://"TRANSFERENCIA DE DOMINIO (2 AÑOS)"

                    break;
                case 2://"TRANSFERENCIA DE DOMINIO(3 AÑOS)"

                    break;
                case 3://"TRANSFERENCIA DE DOMINIO (5 AÑOS)"

                    break;
                case 4://"CASO FORTUITO"

                    break;
                case 5://"REAVALUO DE PREDIOS"

                    break;
                case 6://"ESTACIONAMIENTO DE VEHICULOS"

                    break;
                case 7://"ZONAS NO URBANIZADAS"

                    break;
                case 8://"PROPIEDAD FISCO Y ENTIDADES PUBLICAS"

                    break;
                case 9://"INSTITUCIONES BENEFICAS O EDUCATIVAS"

                    break;
                case 10://"PERT A NAC EXT O ORG INTER DE FUNC P "

                    break;
                case 11://"TEMPLOS CULTO RELIGIOSO CONVENIO CASA PARROQUIAL "

                    break;
                case 12://"ASOC Y MUTUALISTA DE AHORRO Y CREDITO"

                    break;
                case 13://"COOPERATIVAS DE VIVIENDA"

                    break;
                case 14://"AVALUO COMERCIAL < 25 * S M V"

                    break;
                case 15://"25 S M V < AVALUO COMERCIAL < 59 SMV"

                    break;
                case 16://"AMPARADOS POR PATRIMONIO FAMILIAR"

                    break;
                case 17://"LEY DEL ANCIANO"

                    break;
                case 18://"LEY DEL ANCIANO PORCENTAJE"

                    break;
                case 19://"LEY DEL CIEGO"

                    break;
                case 20://"PRESTAMOS IESS BEV MUTUALISTAS"

                    break;
                case 21://"VIVIENDAS POPULARES"

                    break;
                case 22://"HOTELES"

                    break;
                case 23://"VIVIENDAS"

                    break;
                case 24://"INDUSTRIAS"

                    break;
                case 25://"EDIF INTERES HIST ARTIST CULTURA"

                    break;
                case 26://"EDIFICIO EN REPARACION"

                    break;
                case 27://"PATRIMONIO CULTURAL DE LA NACION (50%)"

                    break;
                case 28://"PATRIMONION CULTURAL DE LA NACION (100%)"

                    break;
                case 29://"20-40 % VALOR CAPITAL DE LA DEUDA - HIPOTECA"

                    break;
                case 30://"EDIF APARCAM DE VEHICULOS"

                    break;
                case 31://"PRESCRIPCION DE TITULOS"

                    break;
                case 32://"VIVIENDAS"

                    break;
                case 33://"BAJA DE TITULOS"

                    break;
                case 34://"EXONERACION AREA PUBLICA"

                    break;
                case 35://"SEGURO SOCIAL - HIPOTECA"

                    break;
                case 36://"INSTITUTO ECUATORIANO DE SEGURIDAD SOCIAL"

                    break;
                case 37://"LEY DEL DISCAPACITADO"

                    break;
                case 38://"RELIQUIDACION DE IMPUESTOS"

                    break;
                case 39://"RELIQUIDACION POR AREAS"

                    break;
                case 40://"BAJAS DE TITULOS"

                    break;
                case 41://"EMISION DE TITULOS"

                    break;
                case 42://"POR PAGO INDEBIDO"

                    break;
                case 43://"LEY ORGANICA PARA EL CIERRE DE LA CRISIS BANCARIA 1999"

                    break;
                case 44://"LEY ORGANICA DE DISCAPACIDADES"

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return solicitudExoneracion;
    }

    public List<RenLiquidacion> getRenLiquidacionByPredioRangoAnios(CatPredio predio, Long desde, Long hasta, Long tipoLiquidacion) {
        List<RenLiquidacion> emisiones = new ArrayList<>();
        //REALIZAR LA CONSULTA TIPO LIQUIDACION 13/7
        return emisiones;
    }

    @Override
    public Long cantidadPagosByLiquidacion(RenLiquidacion liquidacion) {
        Map<String, Object> parametros = new HashMap<>();
        try {
            parametros.put("liquidacion", liquidacion);
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return (Long) manager.findObjectByParameter(QuerysFinanciero.pagosByLiquidacion, parametros);

    }

    @Override
    public List<CatPredioRuralModel> getListPrediosRuralModelByPredioModel(CatPredioModel model) {
        List<CatPredioRuralModel> predios = new ArrayList<>();
        List<CatPredioRustico> prediosRurales = new ArrayList<>();
//        List<EmisionesRuralesExcel> prediosRurales2017 = new ArrayList<>();
        CatPredioRuralModel pr;
        Map<String, Object> parametros = new HashMap<>();
        try {
            switch (model.getTipoConsultaRural().intValue()) {
                case 1://CODIGO PREDIAL.
                    if (model.getRegCatastral() != null || model.getIdPredial() != null || model.getParroquia() != null) {
                        if (model.getRegCatastral() != null) {
                            parametros.put("regCatastral", model.getRegCatastral().trim());
                        }
//                        if (model.getIdPredial() != null) {
//                            parametros.put("idPredial", model.getIdPredial());
//                        }
//                        if (model.getParroquia() != null) {
//                            parametros.put("parroquia", model.getParroquia());
//                        }
                    }
                    prediosRurales = manager.findObjectByParameterList(CatPredioRustico.class, parametros);
                    break;
                case 2://CONTRIBUYENTE
                    if (model.getContribuyenteConsultaRural() != null) {
                        if (model.getContribuyenteConsultaRural().getCatPredioRusticos() != null && !model.getContribuyenteConsultaRural().getCatPredioRusticos().isEmpty()) {
                            if (model.getContribuyenteConsultaRural().getCatPredioRusticos().size() == 1) {
                                parametros.put("regCatastral", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getRegCatastral());
                                parametros.put("idPredial", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getIdPredial());
                                parametros.put("parroquia", model.getContribuyenteConsultaRural().getCatPredioRusticos().get(0).getParroquia());
                                prediosRurales = manager.findObjectByParameterList(CatPredioRustico.class, parametros);
                            } else {
                                prediosRurales.addAll(model.getContribuyenteConsultaRural().getCatPredioRusticos());
                            }
                        }
                    }
                    break;
                case 3://2017
//                    if (model.getCodigoPredialRural2017() != null) {
//                        parametros.put("codigoCatastral", model.getCodigoPredialRural2017().trim());
//                    }
//                    prediosRurales2017 = manager.findObjectByParameterList(EmisionesRuralesExcel.class, parametros);
                    break;
                default:
                    break;
            }
            if (prediosRurales != null && !prediosRurales.isEmpty()) {
                for (CatPredioRustico p : prediosRurales) {
                    pr = new CatPredioRuralModel(p, null);
                    predios.add(pr);
                }
            }
//            if (prediosRurales2017 != null && !prediosRurales2017.isEmpty()) {
//                for (EmisionesRuralesExcel p : prediosRurales2017) {
//                    pr = new CatPredioRuralModel(null, p);
//                    predios.add(pr);
//                }
//            }
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return predios;
    }

    @Override
    public List<ModelCarteraVencidaParroquia> getCarteraModelParroquias(List<CatParroquia> parroquias) {
        try {
            List<ModelCarteraVencidaParroquia> carteraVencidas = new ArrayList<>();
            for (CatParroquia parroquia : parroquias) {
                List<ModelCarteraVencida> cv1 = getCarteraModel(parroquia);
                if (Utils.isNotEmpty(cv1)) {
                    ModelCarteraVencidaParroquia mcvp = new ModelCarteraVencidaParroquia();
                    mcvp.setParroquia(parroquia.getDescripcion());
                    mcvp.setCodParroquia(parroquia.getCodNac());
                    for (ModelCarteraVencida c : cv1) {
                        mcvp.setTotalCartera((mcvp.getTotalCartera() == null ? BigDecimal.ZERO : mcvp.getTotalCartera()).add(c.getTotalCartera() == null ? BigDecimal.ZERO : c.getTotalCartera()));
                        mcvp.setTotalCobrado((mcvp.getTotalCobrado() == null ? BigDecimal.ZERO : mcvp.getTotalCobrado()).add(c.getTotalCobrado() == null ? BigDecimal.ZERO : c.getTotalCobrado()));
                        mcvp.setTotalEmitido((mcvp.getTotalEmitido() == null ? BigDecimal.ZERO : mcvp.getTotalEmitido()).add(c.getTotalEmitido() == null ? BigDecimal.ZERO : c.getTotalEmitido()));
                    }
                    mcvp.setCarteraVencidas(cv1);
                    carteraVencidas.add(mcvp);
                }
            }
            if (!carteraVencidas.isEmpty()) {
                return carteraVencidas;
            }
        } catch (HibernateException e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return null;
    }

    @Override
    public List<ModelCarteraVencida> getCarteraModel(CatParroquia parroquia) {
        List<ModelCarteraVencida> carteraVencidas = new ArrayList<>();
        try {
            StringBuilder cartera = new StringBuilder("SELECT COALESCE(SUM(r.total_Pago), 0) total, r.anio, r.tipo_liquidacion, cp.parroquia FROM ")
                    .append(SchemasConfig.FINANCIERO).append(".ren_liquidacion r")
                    .append(" INNER JOIN ").append(SchemasConfig.APP1).append(".cat_predio cp ON cp.id=r.predio")
                    .append(" WHERE r.estado_liquidacion=2 AND cp.estado='A' GROUP BY r.anio, r.tipo_liquidacion, cp.parroquia ");

            StringBuilder pagos = new StringBuilder("SELECT COALESCE(SUM(r.total_Pago), 0) total, r.anio, r.tipo_liquidacion, cp.parroquia FROM ")
                    .append(SchemasConfig.FINANCIERO).append(".ren_liquidacion r ")
                    .append(" INNER JOIN ").append(SchemasConfig.FINANCIERO).append(".ren_pago p ON p.liquidacion=r.id")
                    .append(" INNER JOIN ").append(SchemasConfig.APP1).append(".cat_predio cp ON cp.id=r.predio")
                    .append(" WHERE r.estado_liquidacion=1 AND cp.estado='A' AND p.estado GROUP BY r.anio, r.tipo_liquidacion, cp.parroquia ");

            StringBuilder emision = new StringBuilder("SELECT COALESCE(SUM(r.total_Pago), 0) total, r.anio, r.tipo_liquidacion, cp.parroquia FROM ")
                    .append(SchemasConfig.FINANCIERO).append(".ren_liquidacion r")
                    .append(" INNER JOIN ").append(SchemasConfig.APP1).append(".cat_predio cp ON cp.id=r.predio")
                    .append(" WHERE r.estado_Liquidacion IN (1,2) AND cp.estado='A' GROUP BY r.anio, r.tipo_liquidacion, cp.parroquia ");
            if (parroquia.getCodNac() != null) {
                StringBuilder sb = new StringBuilder("SELECT p.descripcion parroquia, p.cod_nac \"codParroquia\", l.anio")
                        .append(", cv.total  \"totalCartera\"")
                        .append(", pg.total \"totalCobrado\"")
                        .append(", emis.total \"totalEmitido\" ")
                        .append(" FROM ").append(SchemasConfig.APP1).append(".cat_parroquia p")
                        .append(", ").append(SchemasConfig.FINANCIERO).append(".ren_liquidacion l ")
                        .append(" LEFT OUTER JOIN (").append(cartera).append(") cv ON (cv.anio=l.anio AND cv.tipo_liquidacion=l.tipo_liquidacion AND cv.parroquia=:pq1)")
                        .append(" LEFT OUTER JOIN (").append(pagos).append(") pg ON (pg.anio=l.anio AND pg.tipo_liquidacion=l.tipo_liquidacion AND pg.parroquia=:pq2)")
                        .append(" LEFT OUTER JOIN (").append(emision).append(") emis ON (emis.anio=l.anio AND emis.tipo_liquidacion=l.tipo_liquidacion AND emis.parroquia=:pq3)")
                        .append(" WHERE p.id = :parroquia AND l.anio < :anio ")
                        .append("AND l.tipo_liquidacion=13 ")
                        .append(" GROUP BY p.descripcion,p.cod_nac,l.anio,cv.total,pg.total,emis.total")
                        .append(" ORDER BY p.descripcion, l.anio DESC");
                Session sess = HiberUtil.getSession();
//                System.out.println(sb.toString());
                SQLQuery sq1 = sess.createSQLQuery(sb.toString());
                sq1.setResultTransformer(Transformers.aliasToBean(ModelCarteraVencida.class));
                sq1.setLong("parroquia", parroquia.getId());
                sq1.setShort("pq1", parroquia.getCodNac());
                sq1.setShort("pq2", parroquia.getCodNac());
                sq1.setShort("pq3", parroquia.getCodNac());
                sq1.setShort("anio", Utils.getAnio(new Date()).shortValue());

                List<ModelCarteraVencida> mc = sq1.list();
                if (Utils.isNotEmpty(mc)) {
                    carteraVencidas.addAll(mc);
                }
            }
            if (!carteraVencidas.isEmpty()) {
                return carteraVencidas;
            }
        } catch (HibernateException e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return null;
    }

    @Override
    public List<RenPago> getPagosByPredioTipoLiquidacionAnio(CatPredio predio, CatPredioRuralModel predioRural, RenTipoLiquidacion tipo, Integer desde, Integer hasta) {
        List<RenPago> pagos = null;
        try {
            if (predio != null) {
                pagos = manager.findAll(QuerysFinanciero.pagosByPredioTipoLiquidacionAnio, new String[]{"tipoLiquidacion", "predio", "desde", "hasta"}, new Object[]{new RenTipoLiquidacion(13L), predio, desde, hasta});
            }
            if (predioRural != null) {
                if (predioRural.getPredioRustico() != null) {
                    pagos = manager.findAll(QuerysFinanciero.pagosByPredioUrbanoTipoLiquidacionAnio, new String[]{"tipoLiquidacion", "predioRural", "desde", "hasta"}, new Object[]{new RenTipoLiquidacion(7L), predioRural.getPredioRustico(), desde, hasta});
                }
                if (predioRural.getPredioRusctico2017() != null) {
                    pagos = manager.findAll(QuerysFinanciero.pagosByPredioUrbano2017TipoLiquidacionAnio, new String[]{"tipoLiquidacion", "predioSig", "desde", "hasta"}, new Object[]{new RenTipoLiquidacion(13L), predioRural.getPredioRusctico2017(), desde, hasta});
                }
            }
            return pagos;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<RenPago> getPagosByPredioTipoLiquidacionAnioPagada(CatPredio predio, CatPredioRuralModel predioRural, RenTipoLiquidacion tipo, Integer desde, Integer hasta) {
        List<RenPago> pagos = null;
        try {
            if (predio != null) {
                pagos = manager.findAll(QuerysFinanciero.pagosByPredioTipoLiquidacionAnioPagado, new String[]{"tipoLiquidacion", "predio", "desde", "hasta"}, new Object[]{new RenTipoLiquidacion(13L), predio, desde, hasta});
            }
            return pagos;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<CarteraVencida> getCarteraVencidaAME(String claveCatastralAnterior) {
        List<CarteraVencida> carteraVencidaList = null;
        try {
            if (claveCatastralAnterior != null) {
                carteraVencidaList = manager.findAll(QuerysFinanciero.getCarteraVencidaAME, new String[]{"claveCatastralAnterior"}, new Object[]{claveCatastralAnterior});
            }
            return carteraVencidaList;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<RenLiquidacion> updateLiquidacionesByIdTituloLiquidacion(List<String> idTitulosLiquidacion, CatPredio catPredio, Boolean segundaVuelta) {
        List<RenLiquidacion> liquidacionesAfectadas = null;
        List<RenLiquidacion> liquidacionesPrincipales = null;
        try {
            RenLiquidacion liquidacion = null;
            liquidacionesAfectadas = new ArrayList();
            liquidacionesPrincipales = (List<RenLiquidacion>) manager.findAll(QuerysFinanciero.getRenLiquidacionByPredio, new String[]{"predio"}, new Object[]{catPredio});
            if (liquidacionesPrincipales != null && !liquidacionesPrincipales.isEmpty()) {
                for (RenLiquidacion rl : liquidacionesPrincipales) {
                    if (rl.getPredio() != null && rl.getAnio() < 2018) {
                        if (!segundaVuelta) {
                            liquidacionesAfectadas.add(rl);
                            rl.setPredioHistorico(rl.getPredio());
                            rl.setPredio(null);
                        }

                    }
                    manager.persist(rl);
                }
            }
            for (String numTitulo : idTitulosLiquidacion) {
                liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionIdTitulo, new String[]{"numLiquidacion"}, new Object[]{numTitulo});
                if (liquidacion != null) {
                    liquidacionesAfectadas.add(liquidacion);
                    ///   liquidacion.setPredioHistorico(liquidacion.getPredio());
                    liquidacion.setPredio(catPredio);
                    manager.persist(liquidacion);
                }
            }

            return liquidacionesAfectadas;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public List<TitulosPredio> getTitulosPredioAME(String claveCatastralAnterior) {
        List<TitulosPredio> titulosPrediosList = null;
        try {
            if (claveCatastralAnterior != null) {
                titulosPrediosList = manager.findAll(QuerysFinanciero.getTitulosPrediosAME, new String[]{"claveCatastralAnterior"}, new Object[]{claveCatastralAnterior});
            }
            return titulosPrediosList;
        } catch (Exception e) {
            Logger.getLogger(RecaudacionesEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public void emisionUrbana(Long idPredio, Long anio, Long idUsuario, BigDecimal avaluoSolar, BigDecimal avaluoConstruccion, BigDecimal avaluoMunicipal, BigDecimal cemParquesPlazas, BigDecimal cemAlcantarillado) {
        String parametrosText = "";
        parametrosText = idPredio + "," + anio + "," + anio + "," + 3L + "," + avaluoSolar + "," + avaluoConstruccion + "," + avaluoMunicipal + "," + cemParquesPlazas + "," + cemAlcantarillado;
        manager.executeFunction("sgm_financiero.emision_urbana", parametrosText, Boolean.FALSE);
    }

}
