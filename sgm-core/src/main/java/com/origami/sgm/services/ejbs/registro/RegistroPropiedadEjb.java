/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.models.ActosPorLiquidaciones;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.bpm.models.DatoMercantilContrato;
import com.origami.sgm.bpm.models.DatoMercantilSocietario;
import com.origami.sgm.bpm.models.DatoPublicoRegistroPropiedad;
import com.origami.sgm.bpm.models.DatosTramite;
import com.origami.sgm.bpm.models.LiquidacionRegistroAntiguaModel;
import com.origami.sgm.bpm.models.ReporteTramitesRp;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CatTransferenciaDetalle;
import com.origami.sgm.entities.CatTransferenciaDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegCertificado;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoBienCaracteristica;
import com.origami.sgm.entities.RegTipoCertificado;
import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpEntradaSalidaDocs;
import com.origami.sgm.entities.RegpIntervinientes;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RegpLiquidacionDetalles;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.entities.UserConTareas;
import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.entities.VuItems;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.activiti.engine.history.HistoricVariableInstance;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import util.Utils;
import utils.ejb.interfaces.DatabaseLocal;

/**
 *
 * @author Anyelo
 * @author Angel Navarro
 */
@Stateless(name = "registroPropiedad")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class RegistroPropiedadEjb implements RegistroPropiedadServices {

    @javax.inject.Inject
    private Entitymanager manager;

    //@javax.inject.Inject
    //private ConsultasSqlLocal con;
    @javax.inject.Inject
    private SeqGenMan sec;

    @javax.inject.Inject
    private DatabaseLocal ds;

    @javax.inject.Inject
    protected BpmBaseEngine engine;

    @javax.inject.Inject
    protected RentasServices rentasService;

    /**
     * Se guarda la liquidacion generada en el Regitro de la Propiedad, se
     * almacena en las tablas de HistoricoTramites,
     * RegpLiquidacionDerechosAranceles, RegpLiquidacionDetalles,
     * RegpIntervinientes, ademas hace una consulta del numero de liquidacion de
     * la base SAC del municipio y se guarda la liquidacion en el SAC
     *
     * @param liq RegpLiquidacionDerechosAranceles
     * @param user nombre de usuario
     * @param financiero flag true para cuando este listo recaudaciones
     * @return
     */
    @Override
    public RegpLiquidacionDerechosAranceles guardarLiquidacionRegistro(RegpLiquidacionDerechosAranceles liq, String user, Boolean financiero) {
        Calendar cal = Calendar.getInstance();
        try {
            RenTipoLiquidacion tipo = manager.find(RenTipoLiquidacion.class, 177L); // 177L ID REN_TIPO_LIQUIDACION REGISTRO PROPIEDAD
            HistoricoTramites ht = liq.getHistoricTramite();
            List<RegpIntervinientes> listInterv = (List<RegpIntervinientes>) liq.getRegpIntervinientesCollection();
            List<RegpLiquidacionDetalles> actosPorPagar = (List<RegpLiquidacionDetalles>) liq.getRegpLiquidacionDetallesCollection();
            if (!liq.getIsExoneracion()) {
                //CUANDO CONSULTABA AL SAC
                //Long numeroLiquidacion = con.getNumeroLiquidacionPorTitulo("REGISTRO_PROPIEDAD");
                //ht.setNumLiquidacion(BigInteger.valueOf(numeroLiquidacion));
                //ACTUALMENTE LAS SECUENCIAS DE LAS LIQUIDACIONES SE CONSULTAN SOLO DE SGM
                ht.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(cal.get(Calendar.YEAR), tipo.getId()));
            }
            liq.setRegpIntervinientesCollection(null);
            liq.setRegpLiquidacionDetallesCollection(null);
            // Linea agregada por la modificacion de la tabla historico_tramites donde se cambio el nombre del pk
            // y ahora se hacen las consultas por la columna id siendo el nuevo numero de tramite
            ht.setId(sec.getSecuenciasTram("SGM"));
            ht = (HistoricoTramites) manager.persist(ht);
            liq.setHistoricTramite(ht);
            liq = sec.saveRegpLiqDerAranc(liq);

            for (RegpIntervinientes i : listInterv) {
                i.setLiquidacion(liq);
            }
            manager.saveList(listInterv);

            for (RegpLiquidacionDetalles d : actosPorPagar) {
                d.setLiquidacion(liq);
            }
            manager.saveList(actosPorPagar);

            if (!liq.getIsExoneracion()) {
                liq.setNameUser(user);
                List<ActosPorLiquidaciones> actosAgrupados = this.getActosAgrupadosPorLiquidacionId(liq.getId());
                //CUANDO SE GUARDABA LA LIQUIDACION EN EL SAC
                //con.grabarLiquidacionRegistroPropiedad(liq, actosAgrupados, user);
                //ACTUALMENTE SE GUARDAN LAS LIQUIDACIONES SOLO EN SGM
                this.grabaLiquidacionNuevaSgm(liq, actosAgrupados, listInterv, tipo, cal.get(Calendar.YEAR));
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return liq;
    }

    /**
     * Edita una liquidacion generada en el registro de la propiedad, pero solo
     * sino ha sido cancelada.
     *
     * @param liq
     * @param user
     * @param financiero
     * @return
     */
    @Override
    public Boolean editarLiquidacionRegistro(RegpLiquidacionDerechosAranceles liq, String user, Boolean financiero) {
        try {
            Calendar cal = Calendar.getInstance();
            List<RegpIntervinientes> listInterv = (List<RegpIntervinientes>) liq.getRegpIntervinientesCollection();
            List<RegpLiquidacionDetalles> actosPorPagar = (List<RegpLiquidacionDetalles>) liq.getRegpLiquidacionDetallesCollection();
            liq.setRegpIntervinientesCollection(null);
            liq.setRegpLiquidacionDetallesCollection(null);
            manager.update(liq);
            for (RegpIntervinientes i : listInterv) {
                i.setLiquidacion(liq);
            }
            manager.saveList(listInterv);
            for (RegpLiquidacionDetalles d : actosPorPagar) {
                d.setLiquidacion(liq);
            }
            manager.saveList(actosPorPagar);
            HistoricoTramites ht = liq.getHistoricTramite();
            RenTipoLiquidacion tipo = manager.find(RenTipoLiquidacion.class, 177L); // 177L ID REN_TIPO_LIQUIDACION REGISTRO PROPIEDAD
            // SOLO SE DEBE EDITAR LAS LIQUIDACIONES EN SGM
            if (ht.getNumLiquidacion() != null) {
                //con.inactivarLiquidacionRegistro("186", ht.getNumLiquidacion().toString());
                RenLiquidacion ren = (RenLiquidacion) manager.findUnique(Querys.getLiquidacionNoPagadaByTramite, new String[]{"idTramite", "tipoLiq"}, new Object[]{ht.getIdTramite(), tipo.getId()});
                if (ren != null) {
                    ren.setEstadoLiquidacion(new RenEstadoLiquidacion(3L)); // ESTADO INACTIVO
                    manager.update(ren);
                }
            }
            if (liq.getIsExoneracion()) {
                ht.setNumLiquidacion(null);
                manager.update(ht);
            } else {
                //Long numeroLiquidacion = con.getNumeroLiquidacionPorTitulo("REGISTRO_PROPIEDAD");
                //ht.setNumLiquidacion(BigInteger.valueOf(numeroLiquidacion));
                ht.setNumLiquidacion(sec.getMaxSecuenciaTipoLiquidacion(cal.get(Calendar.YEAR), tipo.getId()));
                manager.update(ht);
                liq.setHistoricTramite(ht);
                List<ActosPorLiquidaciones> actosAgrupados = this.getActosAgrupadosPorLiquidacionId(liq.getId());
                //con.grabarLiquidacionRegistroPropiedad(liq, actosAgrupados, user);
                liq.setNameUser(user);
                this.grabaLiquidacionNuevaSgm(liq, actosAgrupados, listInterv, tipo, cal.get(Calendar.YEAR));
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public RegpLiquidacionDerechosAranceles guardarLiquidacionAntigua(RegpLiquidacionDerechosAranceles liq, HistoricoTramites ht) {
        List<RegpIntervinientes> listInterv = (List<RegpIntervinientes>) liq.getRegpIntervinientesCollection();
        List<RegpLiquidacionDetalles> actosPorPagar = (List<RegpLiquidacionDetalles>) liq.getRegpLiquidacionDetallesCollection();
        liq.setRegpIntervinientesCollection(null);
        liq.setRegpLiquidacionDetallesCollection(null);
        try {
            // Linea agregada por la modificacion de la tabla historico_tramites donde se cambio el nombre del pk
            // y ahora se hacen las consultas por la columna id siendo el nuevo numero de tramite
            if (ht.getId() == null) {
                ht.setId(sec.getSecuenciasTram("SGM"));
            }
            ht = (HistoricoTramites) manager.persist(ht);
            liq.setHistoricTramite(ht);
            liq = (RegpLiquidacionDerechosAranceles) manager.persist(liq);
            for (RegpIntervinientes i : listInterv) {
                i.setLiquidacion(liq);
            }
            manager.saveList(listInterv);
            for (RegpLiquidacionDetalles d : actosPorPagar) {
                d.setLiquidacion(liq);
            }
            manager.saveList(actosPorPagar);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return liq;
    }

    @Override
    public Boolean grabaLiquidacionNuevaSgm(RegpLiquidacionDerechosAranceles liq, List<ActosPorLiquidaciones> actosAgrupados, List<RegpIntervinientes> listInterv,
            RenTipoLiquidacion tipo, Integer anio) {
        RenLiquidacion ren;
        RenDetLiquidacion det;
        try {
            ren = new RenLiquidacion();
            ren.setTramite(liq.getHistoricTramite());
            ren.setNumComprobante(BigInteger.ZERO);
            ren.setNumLiquidacion(liq.getHistoricTramite().getNumLiquidacion());
            ren.setIdLiquidacion(tipo.getPrefijo().concat("-").concat(Utils.completarCadenaConCeros(ren.getNumLiquidacion().toString(), 6)));
            ren.setTipoLiquidacion(tipo);
            ren.setTotalPago(liq.getValorActos()); // VALOR SIN TASA CATASTRO
            ren.setUsuarioIngreso(liq.getNameUser());
            ren.setFechaIngreso(new Date());
            ren.setObservacion("");
            if (liq.getInfAdicional() != null) {
                ren.setObservacion(liq.getInfAdicional());
            }
            for (RegpIntervinientes re : listInterv) {
                if (re.getEsBeneficiario()) {
                    if (re.getEnte() != null) {
                        ren.setComprador(re.getEnte());
                    } else {
                        ren.setNombreComprador(re.getNombres());
                    }
                    break;
                }
            }
            ren.setSaldo(liq.getValorActos()); // VALOR SIN TASA CATASTRO
            ren.setEstadoLiquidacion(new RenEstadoLiquidacion(2L)); // ESTADO NO PAGADO
            ren.setCoactiva(Boolean.FALSE);
            ren.setAnio(anio);
            ren = (RenLiquidacion) manager.persist(ren);
            for (ActosPorLiquidaciones re : actosAgrupados) {
                det = new RenDetLiquidacion();
                det.setLiquidacion(ren);
                det.setRubro(re.getRubro().longValue());
                det.setCantidad(re.getCantidad().intValue());
                det.setValor(re.getValor());
                manager.persist(det);
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Se verifica el estado de pago de la liquidacion generada en el registro,
     * si el estado de pago no es "P" verifica en la base SQLServer del SAC el
     * numero de comprobante y actualiza los datos en nuestra base si es el caso
     * METODO DEPRECADO
     *
     * @param liq RegpLiquidacionDerechosAranceles
     * @param numLiquidacion numero de liquidacion
     * @return
     */
    @Override
    public Boolean comprobarEstadoPago(RegpLiquidacionDerechosAranceles liq, String numLiquidacion) {
        //METODO DEPRECADO
        Boolean flag = false;
        try {
            if (liq.getIsExoneracion()) {
                flag = true;
            } else if (liq.getEstadoPago().equalsIgnoreCase("P")) {
                flag = true;
            } else {
                String id;
                if (liq.getIsRegistroPropiedad()) {
                    id = "186";
                } else {
                    id = "189";
                }
                /*Integer comprobante = con.getNumComprobanteLiquidacion(id, numLiquidacion);
                 if (comprobante == 0) {
                 flag = false;
                 } else {
                 liq.setNumeroComprobante(BigInteger.valueOf(comprobante));
                 liq.setEstadoPago("P");
                 manager.update(liq);
                 flag = true;
                 }*/
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return flag;
    }

    /**
     * Se consulta el estado de pago de una liquidacion en las tablas del SAC y
     * devuelve el numero del comprobante con que se hizo el pago si es el caso
     * METODO DEPRECADO
     *
     * @param idLiq
     * @return
     */
    @Override
    public Integer getEstadoPagoByLiquidacion(Long idLiq) {
        //METODO DEPRECADO
        RegpLiquidacionDerechosAranceles liq;
        try {
            liq = manager.find(RegpLiquidacionDerechosAranceles.class, idLiq);
            if (liq.getIsExoneracion()) {
                return 4;
            } else if (liq.getEstadoPago().equalsIgnoreCase("P")) {
                return 1;
            } else {
                BigInteger num;
                if (liq.getHistoricTramite().getNumLiquidacion() == null) {
                    num = BigInteger.ZERO;
                } else {
                    num = liq.getHistoricTramite().getNumLiquidacion();
                }
                String id;
                if (liq.getIsRegistroPropiedad()) {
                    id = "186";
                } else {
                    id = "189";
                }
//                Integer comprobante = con.getNumComprobanteLiquidacion(id, num.toString());
//                if (comprobante == null) {
//                    return 5;
//                } else if (comprobante == 0) {
//                    return 2;
//                } else {
//                    liq.setNumeroComprobante(BigInteger.valueOf(comprobante));
//                    liq.setEstadoPago("P");
//                    manager.update(liq);
//                    return 1;
//                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 5;
        }
        return 2;
    }

    /**
     * Consulta el estado de pago de la liquidacion, devuelve un numero que
     * representa el estado detallado en los comentarios
     *
     * @param idLiq
     * @return 1 : PAGADO - CANCELADO / 2 : PEDIENTE DE PAGO / 3 : INACTIVO / 4
     * : EXONERADO / 5 : ERROR
     */
    @Override
    public Integer getEstadoPagoLiquidacionSgm(Long idLiq) {
        RegpLiquidacionDerechosAranceles liq;
        RenLiquidacion ren;
        try {
            liq = manager.find(RegpLiquidacionDerechosAranceles.class, idLiq);
            if (liq.getIsExoneracion()) {
                return 4;
            } else if (liq.getEstadoPago().equalsIgnoreCase("P")) {
                return 1;
            } else {
                ren = (RenLiquidacion) manager.findUnique(Querys.getLiquidacionByTramiteActivo, new String[]{"idTramite", "tipoLiq", "estadoLiq"}, new Object[]{liq.getHistoricTramite().getIdTramite(), 177L, 1L});
                if (ren != null) {
                    liq.setNumeroComprobante(ren.getNumComprobante());
                    liq.setEstadoPago("P");
                    manager.update(liq);
                    return 1;
                }
                ren = (RenLiquidacion) manager.findUnique(Querys.getLiquidacionByTramiteActivo, new String[]{"idTramite", "tipoLiq", "estadoLiq"}, new Object[]{liq.getHistoricTramite().getIdTramite(), 177L, 2L});
                if (ren != null) {
                    return 2;
                }
                ren = (RenLiquidacion) manager.findUnique(Querys.getLiquidacionByTramite, new String[]{"idTramite", "tipoLiq"}, new Object[]{liq.getHistoricTramite().getIdTramite(), 177L});
                if (ren != null) {
                    if (ren.getEstadoLiquidacion().getId() == 3L || ren.getEstadoLiquidacion().getId() == 4L) {
                        return 3;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 5;
        }
        return 5;
    }

    /**
     * Guarda un nuevo registro en la tabla VuItems
     *
     * @param item
     * @return
     */
    @Override
    public VuItems saveVuItmenRegistro(VuItems item) {
        try {
            if (item.getId() == null) {
                VuCatalogo cat = (VuCatalogo) manager.find(Querys.getVuCatalogoByNombre, new String[]{"nombre"}, new Object[]{"Uso_doc_registro_propiedad"});
                item.setCatalogo(cat);
                item = (VuItems) manager.persist(item);
            } else {
                manager.update(item);
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return item;
    }

    /**
     * Retorna una lista de tipo VuItems que se consulta por tipo de catalogo
     * que clasifica a los items
     *
     * @return
     */
    @Override
    public List<VuItems> getUsosDocumentos() {
        try {
            return (List<VuItems>) manager.findAllEntCopy(Querys.getVuItemsByNombre, new String[]{"nombre"}, new Object[]{"Uso_doc_registro_propiedad"});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Retorna el primer elemento de la lista de usuarios con el rol
     * "supervisor_Registro_Propiedad"
     *
     * @return
     */
    @Override
    public AclUser getUsuarioSupervisor() {
        AclUser user;
        try {
            AclRol rol = manager.find(AclRol.class, 181L);
            List<AclUser> list = (List<AclUser>) rol.getAclUserCollection();
            user = list.get(0);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return user;
    }

    /**
     * Retorna el primer elemento de la lista de usuarios con el rol
     * "Director_Registro_Propiedad"
     *
     * @return
     */
    @Override
    public AclUser getUsuarioDirector() {
        AclUser user;
        try {
            AclRol rol = manager.find(AclRol.class, 79L);
            List<AclUser> list = (List<AclUser>) rol.getAclUserCollection();
            user = list.get(0);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return user;
    }

    /**
     * Retorna el primer elemento de la lista de usuarios con el rol
     * "Digitalizador_Registro_Propiedad"
     *
     * @return
     */
    @Override
    public AclUser getUsuarioDigitalizador() {
        AclUser user;
        try {
            AclRol rol = manager.find(AclRol.class, 101L);
            List<AclUser> list = (List<AclUser>) rol.getAclUserCollection();
            user = list.get(0);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return user;
    }

    /**
     * Retorna los nombre de usuario concatenados con comas de los liquidadores
     * del registro de la propiedad
     *
     * @return
     */
    @Override
    public String getLiquidadoresRegistro() {
        String cadena = "";
        try {
            AclRol rol = manager.find(AclRol.class, 102L);
            List<AclUser> list = (List<AclUser>) rol.getAclUserCollection();
            for (AclUser user : list) {
                cadena = cadena + "," + user.getUsuario();
            }
            cadena = cadena.substring(1);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return cadena;
    }

    /**
     * Retorna una lista de usuarios asociados al rol del cual se hace la
     * consulta, para mediante hibernate hacer el get de la coleccion
     *
     * @param id
     * @return
     */
    @Override
    public List<AclUser> getUsuariosByRolId(Long id) {
        List<AclUser> list;
        try {
            AclRol rol = manager.find(AclRol.class, id);
            list = (List<AclUser>) rol.getAclUserCollection();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna el nombre de usuario encargado de la revision registral, este
     * usuario es asignado por la menor cantidad de tareas por realizar que se
     * actualizan con el parametro enviado
     *
     * @param cantidad
     * @return
     */
    @Override
    public String getTecnicoRegistro(Integer cantidad) {
        String result;
        try {
            UserConTareas u = sec.getUserConMenosTareas(80L, cantidad);
            result = u.getUsername();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return result;
    }

    /**
     * Retorna el nombre de usuario encargado de la revision legal, este usuario
     * es asignado por la menor cantidad de tareas por realizar que se
     * actualizan con el parametro enviado
     *
     * @param cantidad
     * @return
     */
    @Override
    public String getAbogadoRegistro(Integer cantidad) {
        String result;
        try {
            UserConTareas u = sec.getUserConMenosTareas(100L, cantidad);
            result = u.getUsername();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return result;
    }

    /**
     * Retorna el nombre de usuario de un tecnico al que le toca ser asignado un
     * tramite en el registro de la propiedad
     *
     * @param cantidad
     * @return
     */
    @Override
    public String getTecnicoCatastroRegistro(Integer cantidad) {
        String result;
        try {
            UserConTareas u = sec.getUserConMenosTareas(66L, cantidad);
            result = u.getUsername();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return result;
    }

    /**
     * Retorna un string que contiene la url o direccion donde debe ser
     * redirigido el usuario dependiendo de la tarea
     *
     * @param id
     * @return
     */
    @Override
    public String getUrlByTarea(Long id) {
        String value;
        try {
            RegpCertificadosInscripciones temp = manager.find(RegpCertificadosInscripciones.class, id);
            value = temp.getActo().getTipoTarea().getUrl();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return value;
    }

    /**
     * Retorna el HistoricoTramite que corresponde a la tarea del proceso que se
     * esta realizando
     *
     * @param id
     * @return
     */
    @Override
    public HistoricoTramites getHistoricoTramiteById(Long id) {
        HistoricoTramites ht;
        try {
            // ya no se consulta por el pk, sino por el campo id pero solo para la tabla HistoricoTramites
            /* t = manager.find(HistoricoTramites.class, id); */
            ht = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{id});
            if (ht != null) {
                List<Observaciones> list = manager.findAll(Querys.getObservacionesActivasHistoricoTramites, new String[]{"idTramite", "estado"}, new Object[]{ht.getIdTramite(), Boolean.TRUE});
                if (list != null) {
                    ht.setObservacionesCollection(list);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return ht;
    }

    /**
     * Actualiza la tabla UserConTareas cuando el usuario completa la tarea, se
     * restan la cantidad de actos dentro del tramite que le fue asignado
     *
     * @param user
     * @param cantidad
     * @return
     */
    @Override
    public Boolean updateUserConTareas(String user, Integer cantidad) {
        UserConTareas u;
        try {
            u = (UserConTareas) manager.find(Querys.getUserTareasByUser, new String[]{"user"}, new Object[]{user});
            BigInteger temp = u.getPeso().subtract(BigInteger.valueOf(cantidad));
            u.setPeso(temp);
            return manager.update(u);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    /**
     * Actualiza las tablas de historicotramites y
     * regpliquidacionderechosaranceles para guardar los datos de inicio de
     * tramite, ademas se crean los registros de las observaciones del tramite y
     * el detalle de un ingreso de documentos
     *
     * @param liq
     * @param nameUser
     * @param observacion
     */
    @Override
    public void iniciarTramiteRegistro(RegpLiquidacionDerechosAranceles liq, String nameUser, String observacion) {
        try {
            HistoricoTramites ht = liq.getHistoricTramite();
            manager.update(ht);
            manager.update(liq);

            Observaciones ob = new Observaciones();
            ob.setIdTramite(ht);
            ob.setFecCre(new Date());
            ob.setUserCre(nameUser);
            ob.setEstado(true);
            ob.setTarea("Inicio de Tramite");
            ob.setObservacion(observacion);
            ob.setIdTramite(ht);

            RegpEntradaSalidaDocs docs = new RegpEntradaSalidaDocs();
            docs.setCliente(ht.getSolicitante());
            docs.setFecha(new Date());
            docs.setLiquidacion(liq);
            docs.setUsuario(nameUser);
            docs.setObservacion("Ingreso de Documentos - Inicio de Tramite");
            docs.setLiquidacion(liq);

            manager.persist(ob);
            manager.persist(docs);

        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Devuelve el numero de tareas pendientes por usuario en una tabla propia
     * del registro de la propiedad
     *
     * @param id
     * @return
     */
    @Override
    public Integer getCantidadTareasByIdLiquidacion(Long id) {
        BigInteger cantidad;
        try {
            cantidad = (BigInteger) manager.getNativeQuery("select count(*) from flow.regp_certificados_inscripciones where liquidacion = " + id + " and estado = true");
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return cantidad.intValue();
    }

    /**
     * Retorna una lista de tipo RegpCertificadosInscripciones que representa la
     * lista de tareas generada por cada liquidacion
     *
     * @param id
     * @param tipo
     * @return
     */
    @Override
    public List<RegpCertificadosInscripciones> getListaCertfByLiquidacion(Long id, String tipo) {
        List<RegpCertificadosInscripciones> list;
        try {
            list = manager.findAll(Querys.getTareasByLiquidacion, new String[]{"idLiq", "tipo"}, new Object[]{id, tipo});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una lista de tipo RegpCertificadosInscripciones que representa la
     * lista de tareas generada por cada liquidacion
     *
     * @param id
     * @param tipo
     * @return
     */
    @Override
    public List<RegpCertificadosInscripciones> getListaTareasDinardap(Long id, String tipo) {
        List<RegpCertificadosInscripciones> list;
        try {
            list = manager.findAll(Querys.getTareasDinardap, new String[]{"idTarea", "tipo"}, new Object[]{id, tipo});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Guarda una lista de tipo RegpCertificadosInscripciones que se relaciona
     * derectamente con una liquidacion y representa la lista de tareas por
     * tramite
     *
     * @param id
     * @param cant
     * @param tipo
     * @return
     */
    @Override
    public List<RegpCertificadosInscripciones> saveListCertfByLiq(Long id, Integer cant, String tipo) {
        List<RegpCertificadosInscripciones> list;
        RegpCertificadosInscripciones temp;
        try {
            RegpLiquidacionDerechosAranceles liq = manager.find(RegpLiquidacionDerechosAranceles.class, id);
            for (int i = 0; i < cant; i++) {
                temp = new RegpCertificadosInscripciones();
                temp.setFecha(new Date());
                temp.setLiquidacion(liq);
                temp.setTipoTarea(tipo);
                manager.persist(temp);
            }

            list = manager.findAll(Querys.getTareasByLiquidacion, new String[]{"idLiq", "tipo"}, new Object[]{id, tipo});

        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una lista de tipo RegpCertificadosInscripciones que representa la
     * lista de tareas generada por cada liquidacion
     *
     * @param id
     * @return
     */
    @Override
    public List<RegpCertificadosInscripciones> getListTareasByLiquidacion(Long id) {
        List<RegpCertificadosInscripciones> list = new ArrayList<>();
        RegpCertificadosInscripciones temp;
        try {
            RegpLiquidacionDerechosAranceles liq = manager.find(RegpLiquidacionDerechosAranceles.class, id);
            for (RegpLiquidacionDetalles det : liq.getRegpLiquidacionDetallesCollection()) {
                // YA NO SE CREAN LAS SUBTAREAS POR LA CANTIDAD DE ACTOS 
                // POR QUE SE HACEN MUCHOS REGISTRO BASURA EN LA BASE
                // for (int i = 0; i < det.getCantidad(); i++) { }
                if (det.getActo().getTipoActo().getId() == 1L) {
                    temp = new RegpCertificadosInscripciones();
                    temp.setActo(det.getActo());
                    temp.setFecha(new Date());
                    temp.setLiquidacion(liq);
                    temp.setTipoTarea("I");
                    temp.setObservacion(det.getActo().getNombre());
                    temp = (RegpCertificadosInscripciones) manager.persist(temp);
                    list.add(temp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una lista de tipo RegpCertificadosInscripciones que representa la
     * lista de tareas generada por cada liquidacion
     *
     * @param id
     * @return
     */
    @Override
    public List<RegpCertificadosInscripciones> getListTareasCertfByLiquidacion(Long id) {
        List<RegpCertificadosInscripciones> list = new ArrayList<>();
        RegpCertificadosInscripciones temp;
        try {
            RegpLiquidacionDerechosAranceles liq = manager.find(RegpLiquidacionDerechosAranceles.class, id);
            for (RegpLiquidacionDetalles det : liq.getRegpLiquidacionDetallesCollection()) {
                for (int i = 0; i < det.getCantidad(); i++) {
                    if (det.getActo().getTipoActo().getId() == 2L) {
                        temp = new RegpCertificadosInscripciones();
                        temp.setActo(det.getActo());
                        temp.setFecha(new Date());
                        temp.setLiquidacion(liq);
                        temp.setTipoTarea("CE");
                        temp = (RegpCertificadosInscripciones) manager.persist(temp);
                        list.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Crea las tareas que se envia a Catastro
     *
     * @param id
     * @return List<> RegpCertificadosInscripciones
     */
    @Override
    public List<RegpCertificadosInscripciones> guardarTareasCatastroByLiquidacion(Long id) {
        List<RegpCertificadosInscripciones> list = new ArrayList<>();
        RegpCertificadosInscripciones temp;
        try {
            RegpLiquidacionDerechosAranceles liq = manager.find(RegpLiquidacionDerechosAranceles.class, id);
            for (RegpLiquidacionDetalles det : liq.getRegpLiquidacionDetallesCollection()) {
                if (det.getActo().getRealizaTransferencia()) {
                    for (int i = 0; i < det.getCantidad(); i++) {
                        temp = new RegpCertificadosInscripciones();
                        temp.setActo(det.getActo());
                        temp.setFecha(new Date());
                        temp.setLiquidacion(liq);
                        temp.setTipoTarea("C");
                        temp = (RegpCertificadosInscripciones) manager.persist(temp);
                        list.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public Integer getCantidadTareasTransferenciaDominio(String taskId, Long numSeguimiento) {
        Long temp;
        try {
            temp = (Long) manager.find(Querys.getCantRegFichaByMovTramiteTaskID, new String[]{"numTramite", "taskId"}, new Object[]{numSeguimiento, taskId});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 0;
        }
        return temp.intValue();
    }

    @Override
    public List<RegpCertificadosInscripciones> saveTareasCatastroTransferencia(Long numSeguimiento) {
        List<RegpCertificadosInscripciones> list;
        List<RegMovimientoFicha> fichas;
        RegpCertificadosInscripciones temp;
        try {
            HistoricoTramites ht = this.getHistoricoTramiteById(numSeguimiento);
            list = manager.findAll(Querys.getTareasByLiquidacion, new String[]{"idLiq", "tipo"}, new Object[]{ht.getRegpLiquidacionDerechosAranceles().getId(), "C"});
            if (list.isEmpty()) {
                list = new ArrayList<>();
                fichas = manager.findAll(Querys.getRegMovsFichaByMovTramiteTaskID, new String[]{"numTramite"}, new Object[]{numSeguimiento});
                for (RegMovimientoFicha f : fichas) {
                    temp = new RegpCertificadosInscripciones();
                    temp.setTipoTarea("C");
                    temp.setFecha(new Date());
                    temp.setNumFicha(f.getFicha().getNumFicha());
                    temp.setIdMovimiento(f.getMovimiento().getId());
                    temp.setObservacion("Ficha Registral: " + f.getFicha().getNumFicha() + ", No Repertorio: " + f.getMovimiento().getNumRepertorio());
                    temp.setLiquidacion(ht.getRegpLiquidacionDerechosAranceles());
                    temp = (RegpCertificadosInscripciones) manager.persist(temp);
                    list.add(temp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public List<RegpCertificadosInscripciones> saveTareasFinalScann(Long numSeguimiento) {
        List<RegpCertificadosInscripciones> list;
        List<RegMovimiento> movs;
        RegpCertificadosInscripciones temp;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            HistoricoTramites ht = this.getHistoricoTramiteById(numSeguimiento);
            list = manager.findAll(Querys.getTareasByLiquidacion, new String[]{"idLiq", "tipo"}, new Object[]{ht.getRegpLiquidacionDerechosAranceles().getId(), "EF"});
            if (list.isEmpty()) {
                list = new ArrayList<>();
                movs = manager.findAll(Querys.getRegMovimientoByTramite, new String[]{"tramite"}, new Object[]{ht.getId()});
                for (RegMovimiento m : movs) {
                    temp = new RegpCertificadosInscripciones();
                    temp.setTipoTarea("EF");
                    temp.setFecha(new Date());
                    temp.setObservacion("Libro:" + m.getLibro().getNombre() + "/Insc.:" + m.getNumInscripcion() + "/Rep.:" + m.getNumRepertorio() + "/Tomo:" + m.getNumTomo() + "/F.Ins.:" + sdf.format(m.getFechaInscripcion()));
                    temp.setIdMovimiento(m.getId());
                    temp.setLiquidacion(ht.getRegpLiquidacionDerechosAranceles());
                    temp = (RegpCertificadosInscripciones) manager.persist(temp);
                    list.add(temp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Realiza la consulta para generar el anexo uno, que forma parte de los
     * reportes diarios que se envian a la Dinardap, en el anexo uno se detallan
     * las inscripciones de tipo Propiedad del Registro de la Propiedad
     *
     * @param fechaInicio
     * @return
     */
    @Override
    public List<DatoPublicoRegistroPropiedad> consultaDinardapAnexoUno(String fechaInicio) {
        //public List<DatoPublicoRegistroPropiedad> consultaDinardapAnexoUno(String fechaInicio, String fechaFin) {
        List<DatoPublicoRegistroPropiedad> list;
        String query = "select rei.tipo_interv as tipopersona, rei.nombre AS apellidos, rei.nombre AS nombres, "
                + "rei.ced_ruc AS ci, rp.papel as tipocompareciente, ac.nombre as tipocontrato, "
                + "m.num_inscripcion as numinscripcion, m.fecha_inscripcion as fechainsripcion, "
                + "fi.codigo_predial as clavecatastral, coalesce('','') as descripcionbien, li.nombre as libro, "
                + "coalesce('Guayas','') as provincia, fi.tipo_predio as zona, fi.linderos as lindero, "
                + "parr.descripcion as parroquia, coalesce('Samborondon','') as canton, coalesce('','') as cuantia, "
                + "coalesce('','') as unidad, m.id as identificador, coalesce('','') as numjuicio, m.estado as estado, "
                + "coalesce('RP.Samborondon','') as ubicaciondato, m.fecha_inscripcion as ultimamodificacion, "
                + "enj.nombre as notaria, cton.nombre as cantonnotaria, m.fecha_oto as fechaescritura, "
                + "m.valor_uuid as valoruuid "
                + "from  sgm_app.reg_movimiento as m "
                + "left join  sgm_app.reg_movimiento_cliente as mc on (mc.movimiento=m.id) "
                + "left join  sgm_app.reg_ente_interviniente as rei on (rei.id=mc.ente_interv) "
                + "left join  sgm_app.reg_cat_papel as rp on (rp.id=mc.papel) "
                + "left join  sgm_app.reg_movimiento_ficha as mf on (mf.movimiento=m.id) "
                + "left join  sgm_app.reg_acto as ac on (ac.id=m.acto) "
                + "left join  sgm_app.reg_libro as li on (li.id=m.libro) "
                + "left join  sgm_app.reg_ficha as fi on (fi.id=mf.ficha) "
                + "left join  sgm_app.cat_parroquia as parr on (parr.id=fi.parroquia) "
                + "left join  sgm_app.reg_ente_judiciales as enj on (enj.id=m.ente_judicial) "
                + "left join  sgm_app.cat_canton as cton on (cton.id=m.codigo_can) "
                + "where "
                + "(m.libro <> 52 AND m.libro <> 55 AND m.libro <> 25 AND m.libro <> 63 AND m.libro <> 12 AND "
                + "m.libro <> 14 AND m.libro <> 56 AND m.libro <> 61) and (m.acto <> 752 and m.acto <> 415 and "
                + "m.acto <> 767 and m.acto <> 796 and m.acto <> 907 and m.acto <> 879 and m.acto <> 929 and "
                + "m.acto <> 1537 and m.acto <> 749 and m.acto <> 738 and m.acto <> 712 and m.acto <> 739 and "
                + "m.acto <> 902 and m.acto <> 523 and m.acto <> 649 and m.acto <> 863) and m.estado = 'AC' and "
                + "to_date(to_char(m.fecha_inscripcion, 'dd/MM/yyyy'), 'dd/MM/yyyy') = to_date('" + fechaInicio + "', 'dd/MM/yyyy');";
        //+ "m.acto <> 902 and m.acto <> 523 and m.acto <> 649 and m.acto <> 863) and m.fecha_inscripcion "
        //+ "between to_timestamp('" + fechaInicio + "', 'dd/MM/yyyy') and to_timestamp('" + fechaFin + "', 'dd/MM/yyyy');";

        try {
            Session s = manager.getSession();
            Query q = s.createSQLQuery(query);
            list = q.setResultTransformer(Transformers.aliasToBean(DatoPublicoRegistroPropiedad.class)).list();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Realiza la consulta para generar el anexo dos, que forma parte de los
     * reportes diarios que se envian a la Dinardap, en el anexo dos se detallan
     * las inscripciones de tipo Mercantil en el Registro de la Propiedad
     *
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    @Override
    public List<DatoMercantilContrato> consultaDinardapAnexoDos(String fechaInicio, String fechaFin) {
        List<DatoMercantilContrato> list;
        String query = "select rei.tipo_interv as tipopersona, rei.nombre AS apellidos, rei.nombre AS nombres, "
                + "rei.ced_ruc AS ci, rp.papel as tipocompareciente, ac.nombre tipocontrato, "
                + "m.fecha_inscripcion as fechainsripcion, m.num_inscripcion as numinscripcion, "
                + "rei.nombre as representante, m.fecha_inscripcion as fechacancelacion, rtf.nombre as tipobien, "
                + "rf.num_ficha as chasis, coalesce('','') as motor, coalesce('','') as marca, "
                + "coalesce('','') as modelo, coalesce('','') as aniofabrica, coalesce('','') as placa, "
                + "coalesce('RP.Samborondon','') as ubicaciondato, m.fecha_inscripcion as ultimamodificacion, "
                + "m.id as identificador, enj.nombre as notaria, cton.nombre as cantonnotaria, "
                + "m.fecha_oto as fechaescritura, m.estado as estado, m.valor_uuid as valoruuid "
                + "from  sgm_app.reg_acto as ac "
                + "left join  sgm_app.reg_movimiento m on(m.acto=ac.id) "
                + "left join  sgm_app.reg_movimiento_cliente as mc on (mc.movimiento=m.id) "
                + "left join  sgm_app.reg_ente_interviniente as rei on (rei.id=mc.ente_interv) "
                + "left join  sgm_app.reg_cat_papel as rp on (rp.id=mc.papel) "
                + "left join  sgm_app.reg_movimiento_ficha as mf on (mf.movimiento=m.id) "
                + "left join  sgm_app.reg_ficha as rf on (rf.id=mf.ficha) "
                + "left join  sgm_app.reg_tipo_ficha as rtf on (rtf.id=rf.tipo) "
                + "left join  sgm_app.reg_ente_judiciales as enj on (enj.id=m.ente_judicial) "
                + "left join  sgm_app.cat_canton as cton on (cton.id=m.codigo_can)"
                + "where ac.anexo_dos_mercantil_contrato=true and "
                + "m.fecha_inscripcion between to_timestamp('" + fechaInicio + "', 'dd/MM/yyyy') and "
                + "to_timestamp('" + fechaFin + "', 'dd/MM/yyyy') order by m.fecha_inscripcion asc, apellidos";
        try {
            Session s = manager.getSession();
            Query q = s.createSQLQuery(query);
            list = q.setResultTransformer(Transformers.aliasToBean(DatoMercantilContrato.class)).list();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Realiza la consulta para generar el anexo tres, que forma parte de los
     * reportes diarios que se envian a la Dinardap, en el anexo tres se
     * detallan las inscripciones de sociedades en el Registro Mercantil del
     * Registro de la Propiedad
     *
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    @Override
    public List<DatoMercantilSocietario> consultaDinardapAnexoTres(String fechaInicio, String fechaFin) {
        List<DatoMercantilSocietario> list;
        String query = "select  rei.tipo_interv as tipopersona, rei.nombre AS nombrecompania, "
                + "rei.ced_ruc AS ci, coalesce('','') as especie, m.fecha_inscripcion as fechainscripcion, "
                + "reir.nombre as apellidoscompareciente, reir.nombre as nombrescompareciente, "
                + "reir.ced_ruc	as cicompareciente, car.nombre as cargo, car.nombre as tipocompareciente, "
                + "case ac.nombre='Resolucion' when TRUE THEN coalesce('Resolucion','') else coalesce('','') "
                + "end as disposicion, "
                + "case rp.id=39 when TRUE THEN rei.nombre else coalesce('','') end as autoridad, "
                + "m.fecha_inscripcion as fechadisposicion, coalesce('','') as numdisposicion, "
                + "m.fecha_oto as fechaescritura, enj.nombre as notaria, cton.nombre as cantonnotaria, "
                + "ac.nombre as tipotramite, coalesce('RP.Samborondon','') as ubicaciondato, "
                + "m.fecha_oto as ultimamodificacion, m.id as identificador, m.estado as estado, "
                + "m.valor_uuid as valoruuid "
                + "from  sgm_app.reg_acto as ac "
                + "left join  sgm_app.reg_movimiento m on(m.acto=ac.id) "
                + "left join  sgm_app.reg_movimiento_cliente as mc on (mc.movimiento=m.id) "
                + "left join  sgm_app.reg_ente_interviniente as rei on (rei.id=mc.ente_interv) "
                + "left join  sgm_app.reg_movimiento_representante as rep on (rep.movimiento=m.id) "
                + "left join  sgm_app.reg_ente_interviniente as reir on (reir.id=rep.ente_interv) "
                + "left join  sgm_app.ctlg_cargo car on (car.id=rep.cargo) "
                + "left join  sgm_app.reg_ente_judiciales as enj on (enj.id=m.ente_judicial) "
                + "left join  sgm_app.cat_canton as cton on (cton.id=m.codigo_can) "
                + "left join  sgm_app.reg_cat_papel as rp on (rp.id=mc.papel) "
                + "where ac.anexo_tres_mercatil_soc_nombramientos=true and m.libro=52 and "
                + "m.fecha_inscripcion between to_timestamp('" + fechaInicio + "', 'dd/MM/yyyy') "
                + "and to_timestamp('" + fechaFin + "', 'dd/MM/yyyy') order by m.fecha_inscripcion asc;";
        try {
            Session s = manager.getSession();
            Query q = s.createSQLQuery(query);
            list = q.setResultTransformer(Transformers.aliasToBean(DatoMercantilSocietario.class)).list();
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna la Lista de RegFicha asociadas al Movimiento del cual se envia
     * como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegFicha> getRegFichaByIdRegMov(Long id) {
        List<RegFicha> fichas;
        try {
            fichas = manager.findAll(Querys.getRegFichasByMovimientoId, new String[]{"idmov"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return fichas;
    }

    /**
     * Retorna una lista de tipo RegMovimientoReferencia que representa los
     * movimientos de referencia por cada inscripcion
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoReferencia> getRegMovRefByIdRegMov(Long id) {
        List<RegMovimientoReferencia> referencias;
        try {
            referencias = manager.findAll(Querys.getRegMovimientoReferenciaByIdMov, new String[]{"idmov"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return referencias;
    }

    /**
     * Retorna la Lista de RegMovimientoCapital asociadas al Movimiento del cual
     * se envia como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoCapital> getRegMovCapitalByIdMov(Long id) {
        List<RegMovimientoCapital> list;
        try {
            list = manager.findAll(Querys.getRegMovimientoCapitalByMovimiento, new String[]{"movid"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Devuelve una lista relacionada con un movimiento o inscripcion del
     * registro de la propiedad y representa la lista de capitales o valores por
     * cada socio en una sociedad
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoCapital> getRegMovCapitalByIdMovCopy(Long id) {
        List<RegMovimientoCapital> list = new ArrayList<>();
        List<RegMovimientoCapital> temp;
        try {
            temp = manager.findAll(Querys.getRegMovimientoCapitalByMovimiento, new String[]{"movid"}, new Object[]{id});
            if (temp != null && !temp.isEmpty()) {
                for (RegMovimientoCapital mc : temp) {
                    list.add(mc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna la Lista de RegMovimientoCliente asociadas al Movimiento del cual
     * se envia como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoCliente> getRegMovClienteByIdMov(Long id) {
        List<RegMovimientoCliente> list;
        try {
            list = manager.findAll(Querys.getRegMovimientoClienteByMovimiento, new String[]{"movid"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Devuelve una lista de tipo RegMovimientoCliente que representa la lista
     * de intervientes por cada inscripcion o movimiento
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoCliente> getRegMovClienteByIdMovCopy(Long id) {
        List<RegMovimientoCliente> list = new ArrayList<>();
        List<RegMovimientoCliente> temp;
        try {
            temp = manager.findAll(Querys.getRegMovimientoClienteByMovimiento, new String[]{"movid"}, new Object[]{id});
            if (temp != null && !temp.isEmpty()) {
                for (RegMovimientoCliente mc : temp) {
                    list.add(mc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna la Lista de RegMovimientoRepresentante asociadas al Movimiento
     * del cual se envia como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoRepresentante> getRegMovRepresentByIdMov(Long id) {
        List<RegMovimientoRepresentante> list = new ArrayList<>();
        try {
            list = manager.findAll(Querys.getRegRegMovimientoRepresentanteByMovimiento, new String[]{"movid"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    /**
     * Retorna una lista que representa todos los representantes de una sociedad
     * en una inscripcion de tipo mercantil
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoRepresentante> getRegMovRepresentByIdMovCopy(Long id) {
        List<RegMovimientoRepresentante> list = new ArrayList<>();
        List<RegMovimientoRepresentante> temp;
        try {
            temp = manager.findAll(Querys.getRegRegMovimientoRepresentanteByMovimiento, new String[]{"movid"}, new Object[]{id});
            if (temp != null && !temp.isEmpty()) {
                for (RegMovimientoRepresentante mc : temp) {
                    list.add(mc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna la Lista de RegMovimientoSocios asociadas al Movimiento del cual
     * se envia como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoSocios> getRegMovSociosByIdMov(Long id) {
        List<RegMovimientoSocios> list;
        try {
            list = manager.findAll(Querys.getRegMovimientoSociosByMovimiento, new String[]{"movid"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Devuelve todos los socios que representan a una sociedad en un movimiento
     * o inscripcion de tipo mercantil
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoSocios> getRegMovSociosByIdMovCopy(Long id) {
        List<RegMovimientoSocios> list = new ArrayList<>();
        List<RegMovimientoSocios> temp;
        try {
            temp = manager.findAll(Querys.getRegMovimientoSociosByMovimiento, new String[]{"movid"}, new Object[]{id});
            if (temp != null && !temp.isEmpty()) {
                for (RegMovimientoSocios mc : temp) {
                    list.add(mc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna la Lista de RegMovimientoFicha asociadas a la Ficha del cual se
     * envia como parametro el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoFicha> getRegMovByIdFicha(Long id) {
        List<RegMovimientoFicha> list;
        try {
            list = manager.findAll(Querys.getRegMovimientoFichaByFicha, new String[]{"idFicha"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una coleccion de los primary key de la tabla reg_movimiento que
     * esten relacionados al id del interviniente
     *
     * @param id
     * @return
     */
    @Override
    public Collection getListIdMovsByInterv(Long id) {
        Collection values;
        try {
            values = manager.findAll(Querys.getListIdMovsByIdInterv, new String[]{"codigo"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return values;
    }

    @Override
    public Collection getListIdMovsByCedRucInterv(String documento) {
        Collection values;
        try {
            values = manager.findAll(Querys.getListIdMovsByCedRucInterv, new String[]{"codigo"}, new Object[]{documento});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return values;
    }

    /**
     * Retorna una coleccion de los primary key de la tabla reg_ficha que esten
     * relacionados a los movimientos que se involucra el id del interviniente
     * que se envia como parametro
     *
     * @param id
     * @return
     */
    @Override
    public Collection getListIdFichasByInterv(Long id) {
        Collection<BigInteger> values;
        Collection list = new ArrayList();
        try {
            values = manager.getSqlQuery(Querys.getListIdFichasByIdInterv + id);
            if (values != null) {
                for (BigInteger temp : values) {
                    list.add(temp.longValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public Collection getListIdFichasByDocInterv(String documento) {
        Collection<BigInteger> values;
        Collection list = new ArrayList();
        try {
            values = manager.getSqlQuery("select distinct mf.ficha from  sgm_app.reg_movimiento_ficha mf "
                    + "inner join  sgm_app.reg_movimiento_cliente mc on(mc.movimiento = mf.movimiento) "
                    + "inner join  sgm_app.reg_ente_interviniente en on(en.id = mc.ente_interv) "
                    + "where en.ced_ruc = '" + documento + "'");
            if (values != null) {
                for (BigInteger temp : values) {
                    list.add(temp.longValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Guarda en la base propia y en la base Sac los rubros que son los
     * conceptos de cobro en un recibo por parte del municipio
     *
     * @param a entity RegpActosIngreso
     * @return
     */
    @Override
    public Boolean guardarActoLiquidacion(RegpActosIngreso a) {
        RenRubrosLiquidacion rubro = new RenRubrosLiquidacion();
        Map<String, Object> par;
        //String codTitulo;
        Long tipoLiq;
        if (a.getTipoActo().getId() == 1L || a.getTipoActo().getId() == 2L) {
            //codTitulo = "186";
            tipoLiq = 177L;
        } else {
            //codTitulo = "189";
            tipoLiq = 179L;
        }
        try {
            //par.put("estado", true);
            //par.put("activitykey", "transferenciaDominio");
            //GeTipoTramite tipo = manager.findObjectByParameter(GeTipoTramite.class, par);
            par = new HashMap<>();
            par.put("tipoLiquidacion", new RenTipoLiquidacion(tipoLiq));
            par.put("codigoRubro", 1L);
            RenRubrosLiquidacion temp = manager.findObjectByParameter(RenRubrosLiquidacion.class, par);
            rubro.setEstado(Boolean.TRUE);
            rubro.setValor(BigDecimal.ZERO);
            rubro.setTipoLiquidacion(temp.getTipoLiquidacion());
            rubro.setCtaContable(temp.getCtaContable());
            rubro.setCtaOrden(temp.getCtaOrden());
            rubro.setPrioridad(1L);
            rubro.setTipoValor(new RenTipoValor(1L));
            rubro.setRubroDelMunicipio(Boolean.FALSE);
            rubro.setDescripcion(a.getNombre());
            rubro.setCtaPresupuesto(temp.getCtaPresupuesto());
            rubro = rentasService.guardarRubroNuevo(rubro, temp.getTipoLiquidacion());
            a.setCodigoRubroSac(rubro.getCodigoRubro().intValue());
            a.setRubroLiquidacion(rubro);
            manager.persist(a);
            /*if (tipo.getFlagOne()) {
                
             } else {
             Integer codrubro = con.guardarRubroLiquidacionSac(codTitulo, a.getNombre(), a.getUserCre());
             if (codrubro != null) {
             a.setCodigoRubroSac(codrubro + 1);
             manager.persist(a);
             }
             }*/
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Actualiza el rubro por titulo que corresponde al rubro que se referencia
     * en el sistema, solo actualiza el campo nombre en la base del sac si es
     * que fue modificado el nombre
     *
     * @param a
     * @param flag
     * @return
     */
    @Override
    public Boolean actualizarActoLiquidacion(RegpActosIngreso a, Boolean flag) {
        //Map<String, Object> par = new HashMap<>();
        try {
            RenRubrosLiquidacion temp = a.getRubroLiquidacion();
            temp.setDescripcion(a.getNombre());
            manager.update(temp);
            manager.update(a);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Busca todos los Tipos de Dominio en la tabla CatTiposDominio
     *
     * @return Lista de CatTiposDominio
     */
    @Override
    public List<CatTiposDominio> getCatTiposDominioList() {
        return manager.findAll(CatTiposDominio.class);
    }

    /**
     * Actuliza el registro de la tabla CatPredio
     *
     * @param predio Entity CatPredio
     */
    @Override
    public void updateCatPredio(CatPredio predio) {
        manager.update(predio);
    }

    /**
     * Envia a guardar los nuevos registro creados en CatEscritura,
     * PropietariosPredioHist, PropietariosPredioDetalleHist,
     * CatPredioPropietario y tambien envia a eliminar la lista de
     * CatPredioPropietario si es que la lista no esta vacio los propieatrios
     * anteriores
     *
     * @param escrituraNew Entity CatEscritura a guardar
     * @param hist Entity PropietariosPredioHist a guardar
     * @param histProp Lista de Entity PropietariosPredioDetalleHist a guardar
     * @param propietariosNew Lista de entity CatPredioPropietario a guardar
     * @param propietarios Lista de entity CatPredioPropietario a eliminar
     * @return
     
    @Override
    public CatEscritura guardarCambioPropietario(CatEscritura escrituraNew, PropietariosPredioHist hist, List<PropietariosPredioDetalleHist> histProp, List<CatPredioPropietario> propietariosNew, List<CatPredioPropietario> propietarios) {
//        try {
        guardarPropietariosPredioHist(hist, histProp);
//            eliminarPropietarios(propietarios);
        manager.saveList(propietariosNew);
        escrituraNew = (CatEscritura) manager.persist(escrituraNew);
        Hibernate.initialize(escrituraNew);
//        } catch (Exception ex) {
//            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return escrituraNew;
    }*/

    /**
     * Envia a guardar el nuevo registro creado en PropietariosPredioHist, y
     * tambien la lista de detalles de PropietariosPredioDetalleHist
     *
     * @param hist Entity PropietariosPredioHist a guardar
     * @param histProp Lista de PropietariosPredioDetalleHist e guardar
     
    @Override
    public void guardarPropietariosPredioHist(PropietariosPredioHist hist, List<PropietariosPredioDetalleHist> histProp) {
        hist.setFecha(new Date());
        hist = (PropietariosPredioHist) manager.persist(hist);
        for (PropietariosPredioDetalleHist propietario : histProp) {
            propietario.setPropietariosPredioDetalle(hist);
            manager.persist(propietario);
        }
    }*/

    /**
     * Recibe el numero del tramite y realiza un conteo en CatEscritura de
     * escriruras impresas y retorna el numero incrementado en uno
     *
     * @param anioTramite
     * @return El numero de reporte Impreso
     */
    @Lock
    @Override
    public Integer getNumerosReporteImpresosByAnioEscritura(Long anioTramite) {
        Object obj;
        obj = sec.getSequences("Select count(ce.anio) from CatEscritura as ce where ce.anio=:anioTramite", new String[]{"anioTramite"}, new Object[]{anioTramite});
        return new Integer(obj.toString());
    }

    /**
     * Recibe una lista de CatPredioPropietario si no esta vacia elimina la
     * lista de CatPredioPropietario
     *
     * @param propietarios Lista CatPredioPropietario a eliminar
     */
    @Override
    public void eliminarPropietarios(List<CatPredioPropietario> propietarios) {
        if (!propietarios.isEmpty()) {
            for (CatPredioPropietario p : propietarios) {
                manager.delete(p);
            }
        }
    }

    /**
     *
     * @return Lista RegTipoBien
     */
    @Override
    public List<RegTipoBien> getRegTipoBienList() {
        return manager.findAll(RegTipoBien.class);
    }

    /**
     * Optiene la Lista de RegTipoBienCaracteristica pasadas en RegTipoBien,
     * Despues envia a persistir la entity RegTipoBien, por ultimo se verifica
     * que la lista de RegTipoBienCaracteristica esta vacia o no, si esta vacia
     * no realiza niguna accion caso contrario envia a persistir la lista de
     * RegTipoBienCaracteristica
     *
     * @param tipoBien Entity RegTipoBien a guardar
     * @return La entity RegTipoBien Persistida
     */
    @Override
    public RegTipoBien guardarRegTipoBienAndRegTipoBienCaracteristica(RegTipoBien tipoBien) {
        List<RegTipoBienCaracteristica> list = (List<RegTipoBienCaracteristica>) tipoBien.getRegTipoBienCaracteristicaCollection();
        tipoBien.setRegTipoBienCaracteristicaCollection(null);
        tipoBien = (RegTipoBien) manager.persist(tipoBien);
        if (!list.isEmpty()) {
            for (RegTipoBienCaracteristica l : list) {
                l.setTipoBien(tipoBien);
                manager.persist(l);
            }
        }
        return tipoBien;
    }

    /**
     * Obtiene la Lista de RegTipoBienCaracteristica
     *
     * @param tipoBien Entity RegTipoBien a actualizar
     * @return True si realizo la modificacion corrctamente caso contrario false
     */
    @Override
    public Boolean updateRegTipoBienAndRegTipoBienCaracteristica(RegTipoBien tipoBien) {
        List<RegTipoBienCaracteristica> list = (List<RegTipoBienCaracteristica>) tipoBien.getRegTipoBienCaracteristicaCollection();
        tipoBien.setRegTipoBienCaracteristicaCollection(null);
        Boolean ok = manager.update(tipoBien);
        if (!list.isEmpty()) {
            for (RegTipoBienCaracteristica l : list) {
                if (l.getId() == null) {
                    l.setTipoBien(tipoBien);
                    manager.persist(l);
                } else {
                    manager.update(l);
                }
            }
        }
        return ok;
    }

    /**
     * Obtiene todos los registros de Tenga la tabla RegCapital
     *
     * @return Lista RegCapital
     */
    @Override
    public List<RegCapital> getRegCapitalList() {
        return manager.findAll(RegCapital.class);
    }

    /**
     * Recibe la entity RegCapital y lo envia a persistir
     *
     * @param capital Entity RegCapital a guardar
     * @return RegCapital
     */
    @Override
    public RegCapital guardarRegCapital(RegCapital capital) {
        return (RegCapital) manager.persist(capital);
    }

    /**
     * Envia a actualizar la entity RegCapital
     *
     * @param capital Entity RegCapital actualizar
     * @return True si se actualizo correctamente caso contrario false
     */
    @Override
    public Boolean updateRegCapital(RegCapital capital) {
        return manager.update(capital);
    }

    /**
     * Carga la liquidacion hecha en el sistema anterior smbWorkflow, y la
     * guarda en la nueva base, el numero que retorna se refiere a los
     * diferentes estados de la transaccion que se detallan a continuacion:
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    public RegpLiquidacionDerechosAranceles cargarLiquidacionAnterior(Long id, String user) {
        PreparedStatement ps;
        ResultSet rs;
        LiquidacionRegistroAntiguaModel model = new LiquidacionRegistroAntiguaModel();
        RegpLiquidacionDerechosAranceles liq = new RegpLiquidacionDerechosAranceles();
        List<RegpLiquidacionDetalles> listDet = new ArrayList<>();
        List<RegpIntervinientes> listInterv = new ArrayList<>();
        try {
            Connection conn = ds.getDbOldDataSource().getConnection();
            if (conn != null) {
                /**
                 * Busca los Valores de HistoricoTramites por el id que es
                 * numero de seguimiento del tramite
                 */
                ps = conn.prepareStatement(Querys.getHistoricoTramiteAntiguo);
                ps.setLong(1, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    model.setCi(rs.getString(1));
                    model.setNombres(rs.getString(2));
                    model.setApellidos(rs.getString(3));
                    model.setIdTramite(rs.getLong(4));
                    model.setFecha(rs.getDate(5));
                    model.setNombrePropietario(rs.getString(6));
                    model.setCorreos(rs.getString(7));
                    model.setTelefonos(rs.getString(8));
                    model.setNumLiquidacion(rs.getLong(9));
                    model.setUsuario(rs.getString(10));
                }
                /**
                 * Se busca el solicitante del tramite, si no existe se crea
                 */
                CatEnte ente = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{model.getCi()});
                if (ente == null) {
                    ente = new CatEnte();
                    ente.setCiRuc(model.getCi());
                    ente.setNombres(model.getNombres());
                    ente.setApellidos(model.getApellidos());
                    ente.setUserCre(user);
                    ente.setFechaCre(new Date());
                    ente = (CatEnte) manager.persist(ente);
                }
                /**
                 * Se busca el usuario que creo el tramite por nombre de usuario
                 */
                AclUser us = (AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{model.getUsuario()});
                /**
                 * Aqui se crea el HistoricoTramite con los datos de la base
                 * antigua
                 */
                GeTipoTramite tt = manager.find(GeTipoTramite.class, 5L);
                HistoricoTramites ht = new HistoricoTramites();
                ht.setSolicitante(ente);
                ht.setEstado("Pendiente");
                ht.setTipoTramite(tt);
                ht.setFecha(model.getFecha());
                ht.setNombrePropietario(model.getNombrePropietario());
                ht.setTipoTramiteNombre(tt.getDescripcion());
                //ht.setCorreos(model.getCorreos()); TRAMITE MIGRADO NO DEBE ENVIAR CORREO A USUARIO
                ht.setCorreos(SisVars.correoClienteGenerico);
                ht.setTelefonos(model.getTelefonos());
                if (us != null) {
                    ht.setUserCreador(us.getId());
                }
                ht.setId(id);
                ht.setObservacion("Tramite migrado de la base anterior, por el usuario: " + user + ", fecha: " + new Date());
                //ht = (HistoricoTramites) manager.persist(ht);

                /**
                 * Busca los valores de la RegLiquidacionDerechosAranceles
                 */
                ps = conn.prepareStatement(Querys.getLiquidacionBaseAnterior);
                ps.setLong(1, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    model.setId(rs.getLong(1)); // id de regp_liquidacion_derechos_aranceles
                    model.setTasaCatastro(rs.getBigDecimal(2));
                    model.setInfAdicional(rs.getString(3));
                    model.setNumTramiteRp(BigInteger.valueOf(rs.getLong(4)));
                    model.setParentescoSolicitante(rs.getString(5));
                    model.setCantidadTasasCatastro(BigInteger.valueOf(rs.getLong(6)));
                    model.setTotalPagar(rs.getBigDecimal(7));
                    model.setIsExoneracion(rs.getBoolean(8));
                }

                liq = new RegpLiquidacionDerechosAranceles();
                liq.setFecha(model.getFecha());
                liq.setTasaCatastro(model.getTasaCatastro());
                liq.setInfAdicional(model.getInfAdicional());
                if (us != null) {
                    liq.setUserCreador(us.getId());
                }
                liq.setEstado(3);
                liq.setEstadoPago("P");
                liq.setHistoricTramite(ht);
                liq.setIsRegistroPropiedad(Boolean.TRUE);
                liq.setNumTramiteRp(model.getNumTramiteRp());
                liq.setParentescoSolicitante(model.getParentescoSolicitante());
                liq.setCantidadTasasCatastro(model.getCantidadTasasCatastro());
                liq.setTotalPagar(model.getTotalPagar());
                liq.setIsExoneracion(model.getIsExoneracion());
                liq.setInscripcion(Boolean.FALSE);
                liq.setCertificado(Boolean.FALSE);
                liq.setValorActos(liq.getTotalPagar().subtract(liq.getTasaCatastro()));
                //liq = (RegpLiquidacionDerechosAranceles) manager.persist(liq);

                /**
                 * Busca y guarda los detalles de la liquidacion
                 */
                RegpLiquidacionDetalles det;
                ps = conn.prepareStatement(Querys.getDetallesLiquidacionAnterior);
                ps.setLong(1, model.getId());//id de la liquidacion anterior
                rs = ps.executeQuery();
                while (rs.next()) {
                    model.setTipoActo(rs.getLong(1));//pk reg_acto
                    model.setNumValor(rs.getBigDecimal(2));
                    model.setValorPagar(rs.getBigDecimal(3));
                    model.setCuantia(rs.getBigDecimal(4));
                    model.setAvaluo(rs.getBigDecimal(5));
                    model.setObservacion(rs.getString(6));
                    //busca el acto por id, para eso tienen los mismos id tanto en la base anterior
                    //como en la nueva base
                    RegpActosIngreso acto = manager.find(RegpActosIngreso.class, model.getTipoActo());
                    det = new RegpLiquidacionDetalles();
                    det.setActo(acto);
                    det.setLiquidacion(liq);
                    det.setAvaluo(model.getAvaluo());
                    det.setCuantia(model.getCuantia());
                    det.setCantidad(model.getNumValor().intValue());
                    det.setValorUnitario(BigDecimal.ZERO);
                    det.setDescuento(BigDecimal.ZERO);
                    det.setValorTotal(model.getValorPagar());
                    det.setObservacion(model.getObservacion());
                    //manager.persist(det);
                    listDet.add(det);
                }
                liq.setRegpLiquidacionDetallesCollection(listDet);
                /**
                 * Busca y guarda los intervinientes en la liquidacion
                 */
                RegpIntervinientes inte;
                ps = conn.prepareStatement(Querys.getIntervinientesLiquidacion);
                ps.setLong(1, model.getId());//id de la liquidacion anterior

                rs = ps.executeQuery();
                while (rs.next()) {
                    model.setPapel(rs.getLong(1));//id de reg_cat_papel debe ser el mismo en las dos bases
                    model.setEsBeneficiario(rs.getBoolean(2));
                    model.setIsPersona(rs.getBoolean(3));
                    model.setDocumento(rs.getString(4));
                    model.setFirstName(rs.getString(5));
                    model.setSecondName(rs.getString(6));
                    //busca el ente por numero de cedula, para crear los intervinientes del tramite
                    CatEnte temp = null;
                    if (model.getDocumento() != null) {
                        temp = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{model.getDocumento()});
                    }
                    if (temp == null) {
                        temp = new CatEnte();
                        temp.setCiRuc(model.getDocumento());
                        temp.setEsPersona(model.getIsPersona());
                        if (model.getIsPersona()) {
                            temp.setNombres(model.getFirstName());
                            temp.setApellidos(model.getSecondName());
                        } else {
                            temp.setRazonSocial(model.getFirstName());
                        }
                        temp = (CatEnte) manager.persist(temp);
                    }
                    inte = new RegpIntervinientes();
                    inte.setEnte(temp);
                    inte.setEsBeneficiario(model.getEsBeneficiario());
                    inte.setLiquidacion(liq);
                    //busca el reg_cat_papel por el id, debe ser el mismo en ambas bases
                    RegCatPapel papel = manager.find(RegCatPapel.class, model.getPapel());
                    inte.setPapel(papel);
                    if (model.getIsPersona()) {
                        inte.setNombres(model.getFirstName() + " " + model.getSecondName());
                    } else {
                        inte.setNombres(model.getFirstName());
                    }
                    //manager.persist(inte);
                    listInterv.add(inte);
                }
                liq.setRegpIntervinientesCollection(listInterv);
                ht.setNumTramiteXDepartamento(liq.getNumTramiteRp());
                liq.setHistoricTramite(ht);
                //manager.update(ht);
                ps.close();
                conn.close();
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return liq;
    }

    /**
     * Devuelve una lista de tipo modelo de datos ActosPorLiquidaciones que
     * representa los actos que se cobraron en una liquidacion
     *
     * @param liquidacion
     * @return
     */
    @Override
    public List<ActosPorLiquidaciones> getActosAgrupadosPorLiquidacionId(Long liquidacion) {
        return manager.getSqlQueryParametros(ActosPorLiquidaciones.class, Querys.getActosIngresadosAgrupadosPorLiquidacion, new String[]{"liquidacion"}, new Object[]{liquidacion});
    }

    /**
     * Consulta todas las listas relacionadas con un movimiento, esto ayuda al
     * momento de hacer las consultas para la visualizacion de un movimientos
     *
     * @param idMov
     * @return
     */
    @Override
    public ConsultaMovimientoModel getConsultaMovimiento(Long idMov) {
        ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();
        List<RegMovimientoCliente> listMovsClientes;
        List<RegFicha> listFichas;
        List<RegMovimientoReferencia> listMovsRef;
        //List<RegMovimientoCapital> listMovsCapital;
        //List<RegMovimientoRepresentante> listMovsRepresentantes;
        //List<RegMovimientoSocios> listMovsSocios;
        try {
            listFichas = this.getRegFichaByIdRegMov(idMov);
            if (listFichas != null) {
                modelo.setFichas(listFichas);
            }
            listMovsRef = this.getRegMovRefByIdRegMov(idMov);
            if (listMovsRef != null) {
                modelo.setListMovRef(listMovsRef);
            }
            listMovsClientes = this.getRegMovClienteByIdMov(idMov);
            if (listMovsClientes != null) {
                modelo.setListMovCli(listMovsClientes);
            }
            /*listMovsCapital = this.getRegMovCapitalByIdMov(idMov);
             if (listMovsCapital != null) {
             modelo.setListMovCap(listMovsCapital);
             }*/
            /*listMovsRepresentantes = this.getRegMovRepresentByIdMov(idMov);
             if (listMovsRepresentantes != null) {
             modelo.setLisMovRep(listMovsRepresentantes);
             }*/

            /*listMovsSocios = this.getRegMovSociosByIdMov(idMov);
             if (listMovsSocios != null) {
             modelo.setListMovSoc(listMovsSocios);
             }*/
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return modelo;
    }

    /**
     * Devuelve toda la lista de la tabla RegTipoCertificado
     *
     * @return
     */
    @Override
    public List<RegTipoCertificado> getListTipoCertificado() {
        List<RegTipoCertificado> list;
        try {
            list = manager.findAllEntCopy(Querys.getListTipoCertificados);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Devuelve un objeto de tipo RegCertificado que representa un certificado
     * hecho por tarea y se consulta para saber si ya fue realizado o no
     *
     * @param id
     * @return
     */
    @Override
    public RegCertificado getCertificadoByIdTarea(Long id) {
        RegCertificado certificado;
        try {
            certificado = (RegCertificado) manager.find(Querys.getCertificadoByTarea, new String[]{"codigo"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return certificado;
    }

    /**
     * Devuelve el numero de secuencia de un certificado por tarea, ya que por
     * cada tarea puede haber mas de un certificado
     *
     * @param numTramite
     * @return
     */
    @Override
    public Integer getMaxNumeroIndiceCertificado(BigInteger numTramite) {
        Integer indice;
        try {
            Object object = manager.find(Querys.getMaxNumCertificadoByTramite, new String[]{"tramite"}, new Object[]{numTramite});
            if (object == null) {
                indice = 1;
            } else {
                indice = (Integer) object;
                indice++;
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 1;
        }
        return indice;
    }

    @Override
    public Integer getMaxNumeroIndiceCertificadoSine(Integer anio) {
        Integer indice;
        try {
            Object object = manager.find(Querys.getMaxNumCertificadoSine, new String[]{"anio"}, new Object[]{anio.toString()});
            if (object == null) {
                indice = 1;
            } else {
                indice = (Integer) object;
                indice++;
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 1;
        }
        return indice;
    }

    /**
     * Devuelve un modelo de datos DatosTramite que representa un tramite hecho
     * en la base de datos smbcatas
     *
     * @param numTramite
     * @return
     */
    @Override
    public DatosTramite getTramiteAnterior(Long numTramite) {
        DatosTramite ht = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = ds.getDbOldDataSource().getConnection();
            if (conn != null) {
                /**
                 * Busca los Valores de HistoricoTramites por el id que es
                 * numero de seguimiento del tramite
                 */
                ps = conn.prepareStatement(Querys.getHistoricoTramitexEstado);
                ps.setLong(1, numTramite);
                rs = ps.executeQuery();
                if (rs != null) {
                    ht = new DatosTramite();
                    while (rs.next()) {
                        ht.setnTramite(numTramite);
                        if (rs.getLong(1) != 0) {
                            ht.setId(rs.getLong(1));
                        }
                        if (rs.getString(2) != null) {
                            ht.setCi(rs.getString(2));
                        }
                        if (rs.getString(3) != null) {
                            ht.setNombres(rs.getString(3));
                        }
                        if (rs.getString(4) != null) {
                            ht.setApellidos(rs.getString(4));
                        }
                        if (rs.getLong(5) != 0) {
                            ht.setNumPredio(rs.getLong(5));
                        }
                        ht.setFecha(rs.getDate(6));
                        if (rs.getString(7) != null) {
                            ht.setPropietario(rs.getString(7));
                        }
                        if (rs.getString(9) != null) {
                            ht.setUsrCreador(rs.getString(9));
                        }
                        if (rs.getString(10) != null) {
                            ht.setDescSolicitante(rs.getString(10));
                        }
                        ht.setPersona(rs.getBoolean(11));
                        if (rs.getString(12) != null) {
                            ht.setCorreo(rs.getString(12));
                        }
                        if (rs.getString(13) != null) {
                            ht.setTelefono(rs.getString(13));
                        }
                        ht.setEstado(rs.getString(14));
                        if (rs.getLong(15) != 0) {
                            ht.setTipoTramite(rs.getLong(15));
                        }
                    }
                }
                ps.close();
                conn.close();
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return ht;
    }

    /**
     * Devuelve el nombre del papel que ocupa un interviniente en un movimiento
     * o inscripcion determinada
     *
     * @param mov
     * @param inter
     * @return
     */
    @Override
    public String getPapelByMovimientoInterviniente(Long mov, Long inter) {
        String papel;
        try {
            papel = (String) manager.find(Querys.getPapelByMovimientoInterviniente, new String[]{"mov", "inter"}, new Object[]{mov, inter});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return papel;
    }

    @Override
    public String getPapelByMovAndDocumentoInterv(Long mov, String doc) {
        String papel;
        try {
            papel = (String) manager.find(Querys.getPapelByMovYDocInterv, new String[]{"mov", "doc"}, new Object[]{mov, doc});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return papel;
    }

    /**
     * Devuelve la fecha menor de la tabla reg_movimiento
     *
     * @return
     */
    @Override
    public Date getFechaInscripcionMenor() {
        Date fecha;
        try {
            fecha = (Date) manager.find(Querys.getFechaInscripcionMenor);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return fecha;
    }

    /**
     * Devuelve la fecha mayor de la tabla reg_movimiento
     *
     * @return
     */
    @Override
    public Date getFechaInscripcionMayor() {
        Date fecha;
        try {
            fecha = (Date) manager.find(Querys.getFechaInscripcionMayor);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return fecha;
    }

    /**
     * Con los parametros recibidos crea una un nuevo objecto de Observaciones y
     * los envia a persitir.
     *
     * @param ht HistoricoTramites
     * @param nameUser Nombre del Usuario Creador.
     * @param observaciones Observaciones realizadas.
     * @param taskDefinitionKey Nombre de la Tarea.
     * @return entity Observaciones.
     */
    @Override
    public Observaciones guardarObservaciones(HistoricoTramites ht, String nameUser, String observaciones, String taskDefinitionKey) {
        try {
            Observaciones observ = new Observaciones();
            observ.setEstado(true);
            observ.setFecCre(new Date());
            observ.setTarea(taskDefinitionKey);
            observ.setIdTramite(ht);
            observ.setUserCre(nameUser);
            observ.setObservacion(observaciones);
            return (Observaciones) manager.persist(observ);
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<CtlgItem> lisCtlgItems(String catalogo) {
        return manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{catalogo});
    }

    @Override
    public Boolean saveTransferenciaDominio(CatPredio predio, List<CatPredioPropietario> delete, List<CatPredioPropietario> news) {
        try {
            //manager.update(predio);
            if (!news.isEmpty()) {
                for (CatPredioPropietario p : news) {
                    p.setPredio(predio);
                    manager.persist(p);
                }
            }
            if (!delete.isEmpty()) {
                for (CatPredioPropietario p : delete) {
                    manager.delete(p);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public CatTransferenciaDominio registrarTransferencia(CatTransferenciaDominio td, List<CatPredioPropietario> list) {
        CatTransferenciaDetalle det;
        try {
            td = (CatTransferenciaDominio) manager.persist(td);
            for (CatPredioPropietario pp : list) {
                det = new CatTransferenciaDetalle();
                det.setTransferencia(td);
                det.setEnte(pp.getEnte());
                manager.persist(det);
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return td;
    }

    @Override
    public RegpLiquidacionDerechosAranceles getLiquidacionByNumYFecha(BigInteger numTramite, String fecha) {
        RegpLiquidacionDerechosAranceles rlda;
        try {
            rlda = (RegpLiquidacionDerechosAranceles) manager.find(Querys.getRegpLiquidacionByFechaYTramite, new String[]{"numTramite", "fechaLiq"}, new Object[]{numTramite, fecha});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return rlda;
    }

    @Override
    public List<ReporteTramitesRp> tramitesAsignadosRegistro(String fecha, Integer opcion) {
        PreparedStatement ps;
        ResultSet rs;
        List<ReporteTramitesRp> list = new ArrayList<>();
        ReporteTramitesRp rt;
        String query, consulta;
        HistoricVariableInstance var;
        if (opcion == 1) {
            consulta = Querys.getTramitesAsignadosCertificadosRp;
        } else {
            consulta = Querys.getTramitesAsignadosContratosRp;
        }
        try {
            Connection conn = ds.getDataSource().getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(consulta);
                ps.setString(1, fecha);
                rs = ps.executeQuery();
                while (rs.next()) {
                    rt = new ReporteTramitesRp();
                    rt.setIdprocess(rs.getString(1));
                    rt.setBeneficiario(rs.getString(2));
                    rt.setNumtramite(rs.getLong(3));
                    rt.setNumseguimiento(rs.getLong(4));
                    rt.setFecha(rs.getDate(5));
                    query = "select va.* from act_hi_varinst as va where va.execution_id_='" + rt.getIdprocess()
                            + "' and va.name_='tecnico'";
                    var = engine.getProcessEngine().getHistoryService().createNativeHistoricVariableInstanceQuery().sql(query).singleResult();
                    if (var != null) {
                        rt.setAssigne((String) var.getValue());
                        if (rt.getAssigne().isEmpty()) {
                            query = "select va.* from act_hi_varinst as va "
                                    + "left join act_hi_procinst as hp on (va.execution_id_=hp.proc_inst_id_) "
                                    + "where va.name_='tecnico' and hp.super_process_instance_id_='" + rt.getIdprocess() + "'";
                            var = engine.getProcessEngine().getHistoryService().createNativeHistoricVariableInstanceQuery().sql(query).singleResult();
                            if (var != null) {
                                rt.setAssigne((String) var.getValue());
                            }
                        }
                        //rt.setNombre(this.getNombresByUser(rt.getAssigne()));
                    }
                    if (opcion == 2) {
                        query = "select va.* from act_hi_varinst as va where va.execution_id_='" + rt.getIdprocess()
                                + "' and va.name_='abogado'";
                        var = engine.getProcessEngine().getHistoryService().createNativeHistoricVariableInstanceQuery().sql(query).singleResult();
                        if (var != null) {
                            rt.setUserlegal((String) var.getValue());
                            if (rt.getUserlegal().isEmpty()) {
                                query = "select va.* from act_hi_varinst as va "
                                        + "left join act_hi_procinst as hp on (va.execution_id_=hp.proc_inst_id_) "
                                        + "where va.name_='abogado' and hp.super_process_instance_id_='" + rt.getIdprocess() + "'";
                                var = engine.getProcessEngine().getHistoryService().createNativeHistoricVariableInstanceQuery().sql(query).singleResult();
                                if (var != null) {
                                    rt.setUserlegal((String) var.getValue());
                                }
                            }
                            //rt.setNombrelegal(this.getNombresByUser(rt.getUserlegal()));
                        }
                    }
                    list.add(rt);
                }
                conn.close();
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    public String getNombresByUser(String user) {
        String nombres = "";
        try {
            if (user != null && !user.isEmpty()) {
                AclUser u = (AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{user});
                nombres = u.getEnte().getNombres() + " " + u.getEnte().getApellidos();
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return nombres.toUpperCase();
    }

    @Override
    public Boolean restarTareasTransferenciaUser(String user, Integer cant) {
        try {
            UserConTareas u = (UserConTareas) manager.findUnique(Querys.getUserTareasByUser, new String[]{"user"}, new Object[]{user});
            if (u != null) {
                u.setPeso(u.getPeso().subtract(BigInteger.valueOf(cant)));
                manager.update(u);
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    @Override
    public CatEnte saveEnteSinCedRuc(CatEnte ente) {
        CatEnte newEnte;
        EnteTelefono et;
        EnteCorreo ec;
        try {
            newEnte = sec.guardarOActualizarEnte(ente);
            if (ente.getCorreo() != null) {
                ec = new EnteCorreo();
                ec.setEmail(ente.getCorreo());
                ec.setEnte(newEnte);
                ec = (EnteCorreo) manager.persist(ec);
                newEnte.getEnteCorreoCollection().add(ec);
            }
            if (ente.getTelefono() != null) {
                et = new EnteTelefono();
                et.setTelefono(ente.getTelefono());
                et.setEnte(newEnte);
                et = (EnteTelefono) manager.persist(et);
                newEnte.getEnteTelefonoCollection().add(et);
            }
            //newEnte = manager.find(CatEnte.class, newEnte.getId());
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return newEnte;
    }

    @Override
    public Integer getCantidadCertificadosRealizados(String desde, String hasta) {
        Long cantidad;
        try {
            Object o = manager.find(Querys.getCantidadCertificadosRealizados, new String[]{"desde", "hasta"}, new Object[]{desde, hasta});
            if (o == null) {
                cantidad = 0L;
            } else {
                cantidad = (Long) o;
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 0;
        }
        return cantidad.intValue();
    }

    @Override
    public Integer getCantSolicitantes(Integer tipo, String desde, String hasta) {
        String q1 = "", q2 = "";
        BigInteger cant1;
        BigInteger cant2;
        try {
            switch (tipo) {
                case 1: // TODOS LOS CERTIFICADOS
                    q1 = Querys.getCantSolicitantesCertLiq;
                    q2 = Querys.getCantSolicitantesCertSine;
                    break;
                case 2: // INSCRIPCIONES PROPIEDAD
                    q1 = Querys.getCantSolInscPropiedad;
                    q2 = Querys.getCantSolInscPropiedadSine;
                    break;
                case 3: // INSCRIPCIONES DE GRAVAMENES
                    q1 = Querys.getCantSolInscGravamen;
                    q2 = Querys.getCantSolInscGravamenSine;
                    break;
                case 4: // INSCRIPCIONES DE RESOLUCION
                    q1 = Querys.getCantSolInscResolucion;
                    q2 = Querys.getCantSolInscResolucionSine;
                    break;
            }
            Object ob1 = manager.getNativeQuery(q1, new Object[]{desde, hasta});
            if (ob1 == null) {
                cant1 = BigInteger.ZERO;
            } else {
                cant1 = (BigInteger) ob1;
            }
            Object ob2 = manager.getNativeQuery(q2, new Object[]{desde, hasta});
            if (ob2 == null) {
                cant2 = BigInteger.ZERO;
            } else {
                cant2 = (BigInteger) ob2;
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 0;
        }
        return cant1.add(cant2).intValue();
    }

    @Override
    public Integer getCantInscripcionesByTramite(Long tramite) {
        Long cant;
        try {
            Object ob = manager.find(Querys.getCantRegMovimientoByTramite, new String[]{"tramite"}, new Object[]{tramite});
            if (ob == null) {
                cant = 0L;
            } else {
                cant = (Long) ob;
            }
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return 0;
        }
        return cant.intValue();
    }

    @Override
    public RegMovimiento getRegMovimientoById(Long id) {
        RegMovimiento mov;
        try {
            mov = (RegMovimiento) manager.find(Querys.getRegMovimientoById, new String[]{"movId"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(RegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return mov;
    }

}
