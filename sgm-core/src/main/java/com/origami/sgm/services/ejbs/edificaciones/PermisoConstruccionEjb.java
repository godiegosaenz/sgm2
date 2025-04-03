/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.Hibernate;
import util.GroovyUtil;
import util.Utils;

/**
 * Metodos Para el procesos PermisoCosntruccion
 *
 * @author Angel Navarro
 */
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Stateless(name = "permisoConstruccionServices")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class PermisoConstruccionEjb implements PermisoConstruccionServices {

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    @javax.inject.Inject
    private SeqGenMan secuenciaReporte;

    @javax.inject.Inject
    private FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    protected Entitymanager manager;

    @javax.inject.Inject
    protected DatoSeguroServices datoSeguroServices;

    /**
     * Obtiene el id para la tabla HistoricoTramites
     *
     * @return Id
     */
    @Override
    public Long generarIdTramite() {
        return secuenciaReporte.getSecuenciasTram("SGM");
    }

    /**
     * Busca en la tabla GeTipoTramite por el id
     *
     * @param id de GeTipoTramite
     * @return GeTipoTramite
     */
    @Override
    public GeTipoTramite getGeTipoTramiteById(Long id) {
        return (GeTipoTramite) services.find(GeTipoTramite.class, id);
    }

    /**
     * Busca en PeTipoPermiso que tengan los cÃ³digos 'AR','CN','RN','RM','RP'
     *
     * @return Lista PeTipoPermiso
     */
    @Override
    public List<PeTipoPermiso> getPeTipoPermisoByCodigo() {
        return services.findAll(Querys.getPeTipoPermisoCodigo, new String[]{}, new Object[]{});
    }

    /**
     * Busca todos los registros de la tablas CatEdfCategProp orednado por el
     * campo guiOrden asc.
     *
     * @return Lista CatEdfCategProp
     */
    @Override
    public List<CatEdfCategProp> getCatEdfCategPropList() {
        return manager.findAllObjectOrder(CatEdfCategProp.class, new String[]{"guiOrden"}, Boolean.TRUE);
    }

    /**
     * Busca el la tabla CatPredio por el cÃ³digo predial
     *
     * @param zona
     * @param sector Sector
     * @param mz Manzana
     * @param cdla Ciudadela
     * @param mzdiv DivisiÃ³n de Manzana
     * @param solar Solar
     * @param div1 DivisiÃ³n 1
     * @param div2 DivisiÃ³n 2
     * @param div3 DivisiÃ³n 3
     * @param div4 DivisiÃ³n 4
     * @param div5 DivisiÃ³n 5
     * @param div6 DivisiÃ³n 6
     * @param div7 DivisiÃ³n 7
     * @param div8 DivisiÃ³n 8
     * @param div9 DivisiÃ³n 9
     * @param phh Propiedad Hizontal
     * @param phv Propiedad Vertical
     * @return CatPredio
     */
    @Override
    public CatPredio getCatPredioByCodigoPredio(Short zona, short sector, short mz, short solar) {
        return (CatPredio) services.find(Querys.getPredioByCodCat,
                new String[]{"zonap", "sectorp", "mzp", "solarp"}, new Object[]{zona, sector, mz, solar});
    }

    /**
     * Busca en la tabla CatEnte por el número de cÃ©dula y si es persona
     * natural o jurÃ­dica
     *
     * @param ciRuc CÃ©dula O RUC
     * @param esPersona si es persona true, Persona JurÃ­dica false
     * @return CatEnte
     */
    @Override
    public CatEnte getCatEnteByCiRucByEsPersona(String ciRuc, boolean esPersona) {
        return (CatEnte) services.find(Querys.getPersonaByCi, new String[]{"ciRuc", "persona"}, new Object[]{ciRuc, esPersona});
    }

    /**
     * Busca en la tabla CatEdfProp por el id de CatEdfCategProp y retorna una
     * lista CatEdfProp
     *
     * @param id Entity CatEdfCategProp
     * @return Lista CatEdfProp
     */
    @Override
    public List<CatEdfProp> getCatEdfPropList(Long id) {
        return services.findAll(Querys.getCatEdfPropList, new String[]{"idCateg"}, new Object[]{id});
    }

    /**
     * Verifica si el id es nulo para enviar a guardar caso contario se
     * actualiza
     *
     * @param listaPropietarios Lista de CatPredioPropietario a Guardar o
     * actualizar
     */
    @Override
    public void guardarOActualizarCatPredioPropietario(List<CatPredioPropietario> listaPropietarios) {
        if (listaPropietarios.size() > 0) {
            for (CatPredioPropietario listCat1 : listaPropietarios) {
                if (listCat1.getId() == null) {
                    services.persist(listCat1);
                } else {
                    services.update(listCat1);
                }
            }
        }
    }

    /**
     * Busca el tabla PeTipoPermiso por el nombre
     *
     * @param tipoTramiteNombre Nombre del PeTipoPermiso
     * @return PeTipoPermiso
     */
    @Override
    public PeTipoPermiso getPeTipoPermisoByDesc(String tipoTramiteNombre) {
        return (PeTipoPermiso) services.find(Querys.getTipoPermisoDes, new String[]{"des"}, new Object[]{tipoTramiteNombre});
    }

    /**
     * Obtiene todo los registro que tenga la PeTipoPermiso
     *
     * @return Lista PeTipoPermiso
     */
    @Override
    public List<PeTipoPermiso> getPeTipoPermisoList() {
        return services.findAll(PeTipoPermiso.class);
    }

    /**
     * consulta PePermiso la secuencia del reporte por aÃ±o
     *
     * @param anio AÃ±o del tramite
     * @return La Secuencia del reporte
     */
    @Override
    public BigInteger getSecuenciaNumReporte(Short anio) {
        Object ob = secuenciaReporte.getSequences(Querys.getNumerosReportes, new String[]{"anioPermiso"}, new Object[]{anio});
        BigInteger n = new BigInteger((ob == null) ? "0" : ob.toString());
        return n;
    }

    /**
     * Genera la tasa de liquidaciÃ³n, obtiene los valores de de revisiÃ³n e
     * inspecciÃ³n, se realiza el calculo de impuesto y despues envia a guardar
     * PePermiso
     *
     * @param permisoNuevo PePermiso
     * @param formulas Formulas para realizar los calculos de liquidacion.
     * @return PePermiso
     */
    @Override
    public PePermiso guardarPePermiso(PePermiso permisoNuevo, MatFormulaTramite formulas) {
        GroovyUtil groovyUtil = new GroovyUtil(formulas.getFormula());
        try {
            groovyUtil.setProperty("tipoPermiso", permisoNuevo.getTipoPermiso());
            groovyUtil.setProperty("permiso", permisoNuevo);
            permisoNuevo.setAvaluoLiquidacion(((BigDecimal) groovyUtil.getExpression("getAvaluoLiquidacion", new Object[]{})));
            // GENERAR LIQUIDACION
            String esquinero;
            if (permisoNuevo.getIdPredio().getPhh() > 0 && permisoNuevo.getIdPredio().getPhv() > 0) {
                CatPredio predio = manager.find(CatPredio.class, permisoNuevo.getIdPredio().getPredioRaiz().longValue());
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{predio});
            } else {
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{permisoNuevo.getIdPredio()});
            }

            permisoNuevo.setLineaF((((BigDecimal) groovyUtil.getExpression("generarLiquidacionLineaFabrica", new Object[]{permisoNuevo.getLineaFabrica(), esquinero}))));

            List<PeTipoPermiso> ptp = getPeTipoPermisoList();
            for (PeTipoPermiso ptp1 : ptp) {
                /*OBTENCION DE REVISION, INSPECCION*/
                if ("IN".equals(ptp1.getCodigo())) {
                    permisoNuevo.setInspeccion(ptp1.getValor());
                }
                if ("RV".equals(ptp1.getCodigo())) {
                    permisoNuevo.setRevision(ptp1.getValor());
                }
                if ("ND".equals(ptp1.getCodigo())) {
                    permisoNuevo.setNoAdeudar(ptp1.getValor());
                }
                /*OBTENCION DE IMPUESTO*/
                if ("IM".equals(ptp1.getCodigo())) {
                    if (permisoNuevo.getAvaluoLiquidacion() != null) {
                        groovyUtil.setProperty("permiso", permisoNuevo);
                        permisoNuevo.setImpuesto((BigDecimal) groovyUtil.getExpression("getImpuesto", new Object[]{ptp1.getValor()}));
                    }
                }
            }
            return secuenciaReporte.getSequences(permisoNuevo);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public PePermiso guardarPePermisoInspeccion(PePermiso permisoNuevo, MatFormulaTramite formulas) {
        GroovyUtil groovyUtil = new GroovyUtil(formulas.getFormula());
        try {
            groovyUtil.setProperty("tipoPermiso", permisoNuevo.getTipoPermiso());
            groovyUtil.setProperty("permiso", permisoNuevo);
            permisoNuevo.setAvaluoLiquidacion(((BigDecimal) groovyUtil.getExpression("getAvaluoLiquidacion", new Object[]{})));
            // GENERAR LIQUIDACION
            String esquinero;
            if (permisoNuevo.getIdPredio().getPhh() > 0 && permisoNuevo.getIdPredio().getPhv() > 0) {
                CatPredio predio = manager.find(CatPredio.class, permisoNuevo.getIdPredio().getPredioRaiz().longValue());
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{predio});
            } else {
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{permisoNuevo.getIdPredio()});
            }

            permisoNuevo.setLineaF((((BigDecimal) groovyUtil.getExpression("generarLiquidacionLineaFabrica", new Object[]{permisoNuevo.getLineaFabrica(), esquinero}))));

            List<PeTipoPermiso> ptp = getPeTipoPermisoList();
            for (PeTipoPermiso ptp1 : ptp) {
                /*OBTENCION DE REVISION, INSPECCION*/
                if ("IN".equals(ptp1.getCodigo())) {
                    permisoNuevo.setInspeccion(ptp1.getValor());
                }
                if ("RV".equals(ptp1.getCodigo())) {
                    permisoNuevo.setRevision(ptp1.getValor());
                }
                if ("ND".equals(ptp1.getCodigo())) {
                    permisoNuevo.setNoAdeudar(ptp1.getValor());
                }
                /*OBTENCION DE IMPUESTO*/
                if ("IM".equals(ptp1.getCodigo())) {
                    if (permisoNuevo.getAvaluoLiquidacion() != null) {
                        groovyUtil.setProperty("permiso", permisoNuevo);
                        permisoNuevo.setImpuesto((BigDecimal) groovyUtil.getExpression("getImpuesto", new Object[]{ptp1.getValor()}));
                    }
                }
            }
            return (PePermiso) services.persist(permisoNuevo);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Envia a guardar PePermisoCabEdificacion y por cada registro busca los
     * detalles PeDetallePermiso de viene en la en la collection de
     * PePermisoCabEdificacion y las ve guardando
     *
     * @param p PePermiso
     * @param DetallesEdific PePermisoCabEdificacion
     * @param permisoSelect PePermisoCabEdificacion contiene la la Collection de
     * PeDetallePermiso
     */
    @Override
    public void guardarPePermisoCabEdificacionAndPeDetallePermiso(PePermiso p, List<PePermisoCabEdificacion> DetallesEdific, PePermisoCabEdificacion permisoSelect) {
        try {
            Map<String, Object> map;
            PePermisoCabEdificacion d1;
            for (PePermisoCabEdificacion de : DetallesEdific) {
                de.setIdPermiso(p);
                de.setEstado(Boolean.TRUE);
                map = new HashMap<>();
                map.put("idPermiso", p);
                map.put("numEdificacion", de.getNumEdificacion());
                PePermisoCabEdificacion tempEd = manager.findObjectByParameter(PePermisoCabEdificacion.class, map);
                if (tempEd != null) {
                    de.setId(tempEd.getId());
                    manager.persist(de);
                    d1 = tempEd;
                } else {
                    d1 = (PePermisoCabEdificacion) services.persist(de);
                }
                permisoSelect = de;
                for (PeDetallePermiso pdp : permisoSelect.getPeDetallePermisoCollection()) {
                    map = new HashMap<>();
                    map.put("idPermisoEdificacion", d1);
                    map.put("idCatEdfProp", pdp.getIdCatEdfProp());
                    PeDetallePermiso temp = manager.findObjectByParameter(PeDetallePermiso.class, map);
                    pdp.setEstado(Boolean.TRUE);
                    if (temp != null) {
                        pdp.setId(temp.getId());
                        manager.persist(pdp);
                        pdp = temp;
                    } else {
                        pdp.setIdPermisoEdificacion(d1);
                        pdp = (PeDetallePermiso) services.persist(pdp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Busca el la tabla AclUser por el User
     *
     * @param userDireccion Nombre de User
     * @return AclUser
     */
    @Override
    public AclUser getAclUserByUser(String userDireccion) {
        return (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{userDireccion});
    }

    /**
     * Envia actualiza la tabla PePermiso
     *
     * @param permisoNuevo PePermiso
     * @return True si fue actualizada Caso contrario false
     */
    @Override
    public boolean actualizarPePermiso(PePermiso permisoNuevo) {
        return services.update(permisoNuevo);
    }

    /**
     * Busca en HistoricoTramites por el campo id
     *
     * @param id Campo id de la tabla HistoricoTramites
     * @return HistoricoTramites
     */
    @Override
    public HistoricoTramites getHistoricoTramiteById(Long id) {
        return (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{id});
    }

    /**
     * Enivia a peristir la entity HistoricoTramiteDet
     *
     * @param htd entity HistoricoTramiteDet
     * @return HistoricoTramiteDet persistida
     */
    @Override
    public HistoricoTramiteDet guardarHistoricoTramiteDet(HistoricoTramiteDet htd) {
        return (HistoricoTramiteDet) services.persist(htd);
    }

    /**
     * Obtiene la lista de parametros por el id de GeTipoTramite
     *
     * @param tipoTramite id de GeTipoTramite
     * @return Lista ParametrosDisparador
     */
    @Override
    public List<ParametrosDisparador> getParametroDisparadorByTipoTramite(Long tipoTramite) {
        List<ParametrosDisparador> list = services.findAll(Querys.getParametroDisparadorByTipoTramite, new String[]{"tipoTramite"}, new Object[]{tipoTramite});
        return list;
    }

    /**
     * Envia actualizar HistoricoTramites
     *
     * @param ht HistoricoTramites
     */
    @Override
    public void actualizarHistoricoTramites(HistoricoTramites ht) {
        services.update(ht);
    }

    /**
     * Consulta en la table RT_LIQUIDACION
     *
     * @param tipoTramite Id de GeTipoTramite
     * @return Estado de la liquidación P,A,I
     */
    @Override
    public String consultaPagoLiquidacion(int tipoTramite, HistoricoTramites ht) {
        String pagado = null;
        try {
            if (pagado == null) {
                if (ht != null) {
                    if (ht.getRenLiquidacionCollection() != null && ht.getRenLiquidacionCollection().size() > 0) {
                        OUTER:
                        for (RenLiquidacion l : ht.getRenLiquidacionCollection()) {
                            if (l.getEstadoLiquidacion() != null) {
                                switch (l.getEstadoLiquidacion().getId().intValue()) {
                                    case 1:
                                        pagado = "P";
                                        break OUTER;
                                    case 2:
                                        pagado = "A";
                                        break OUTER;
                                    case 5:
                                        pagado = "P";
                                        break OUTER;
                                    default:
                                        pagado = "I";
                                        break OUTER;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return pagado;
    }

    @Override
    public Boolean consultaPagoLiquidacion(HistoricoTramites ht) {
        String pagado = null;
        RenLiquidacion liquidacion = null;
        try {
            if (ht.getTipoTramite().getId() == 6 || ht.getTipoTramite().getId() == 14 || ht.getTipoTramite().getId() == 43) {
                liquidacion = buscarNumCompSubTipo(ht);
            } else {
                liquidacion = (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{ht.getTipoTramite().getRenTipoLiquidacion(), ht.getNumLiquidacion()});
            }

            if (liquidacion != null) {
                if (liquidacion.getEstadoLiquidacion().getId() == 1) {
                    return true;
                } else {
                    return false;
                }
            }
            /*
             switch (tipoTramite) {
             case 2://16 es el tipoTramite que significa DatosProcesoReportePermiso construccion en la base sac tabla RT_TITULOS_REPORTES
             pagado = consultaPagoLiquidacionNumComprobante(16, numLiquidacion.longValue());
             break;
             case 4:
             pagado = consultaPagoLiquidacionNumComprobante(27, numLiquidacion.longValue());
             break;
             case 6:
             pagado = consultaPagoLiquidacionNumComprobante(29, numLiquidacion.longValue());
             break;
             case 7:
             pagado = consultaPagoLiquidacionNumComprobante(120, numLiquidacion.longValue());
             break;
             case 8:
             pagado = consultaPagoLiquidacionNumComprobante(121, numLiquidacion.longValue());
             break;
             case 9:
             pagado = consultaPagoLiquidacionNumComprobante(119, numLiquidacion.longValue());
             break;
             case 15:
             pagado = consultaPagoLiquidacionNumComprobante(122, numLiquidacion.longValue());
             break;
             case 16:
             pagado = consultaPagoLiquidacionNumComprobante(13, numLiquidacion.longValue());
             break;
             case 17:
             pagado = consultaPagoLiquidacionNumComprobante(186, numLiquidacion.longValue());
             break;
             case 36:
             pagado = consultaPagoLiquidacionNumComprobante(13, numLiquidacion.longValue());
             break;
             }*/
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return false;
    }

    public RenLiquidacion buscarNumCompSubTipo(HistoricoTramites ht) {
        Long cod_titulo;
        OtrosTramites oTramite = ht.getSubTipoTramite();
        String prefijo, nombreTitulo, nombreAplicacion;

        if (ht.getTipoTramite().getId() == 6) {
            PePermisosAdicionales ppa = (PePermisosAdicionales) services.find(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
            if (ppa.getTipoPermisoAdicional().getId() == 1) {
                return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{35L, ht.getNumLiquidacion()});
            }
            if (ppa.getTipoPermisoAdicional().getId() == 2) {
                return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{191L, ht.getNumLiquidacion()});
            }
            if (ppa.getTipoPermisoAdicional().getId() == 3) {
                return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{248L, ht.getNumLiquidacion()});
            }
            if (ppa.getTipoPermisoAdicional().getId() == 4) {
                return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{249L, ht.getNumLiquidacion()});
            }
            if (ppa.getTipoPermisoAdicional().getId() == 5) {
                return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{250L, ht.getNumLiquidacion()});
            }
        }
        if (ht.getTipoTramite().getId() == 14) {
            prefijo = oTramite.getPrefijo();
            nombreTitulo = oTramite.getTituloReporte();
            nombreAplicacion = oTramite.getCodigoAplicacion();

            if (prefijo == null || prefijo == "") {
                prefijo = "SOB";
            }
            if (nombreTitulo == null || nombreTitulo == "") {
                nombreTitulo = "OTRAS CONSTRUCCIONES";
            }

            cod_titulo = (Long) services.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
            if (cod_titulo == null) {
                cod_titulo = 25L;
            }

            // No tengas miedo de esta línea. Es más sencillo de lo que parece
            return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{((RenTipoLiquidacion) services.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo})).getId(), ht.getNumLiquidacion()});
        }
        if (ht.getTipoTramite().getId() == 43) {
            prefijo = oTramite.getPrefijo();
            nombreTitulo = oTramite.getTituloReporte();
            nombreAplicacion = oTramite.getCodigoAplicacion();

            if (prefijo == null || prefijo == "") {
                prefijo = "SOB";
            }
            if (nombreTitulo == null || nombreTitulo == "") {
                nombreTitulo = "OTRAS CONSTRUCCIONES";
            }

            cod_titulo = (Long) services.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
            if (cod_titulo == null) {
                cod_titulo = 25L;
            }

            // No tengas miedo de esta línea. Es más sencillo de lo que parece
            return (RenLiquidacion) services.find(QuerysFinanciero.getRenLiquidacionByTipoYNumLiquidacion, new String[]{"tipoLiq", "numLiq"}, new Object[]{((RenTipoLiquidacion) services.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo})).getId(), ht.getNumLiquidacion()});
        }

        return null;
    }

    /**
     * Envia a Guardar en la tabla Observaciones
     *
     * @param obs Observaciones
     * @return Observaciones
     */
    @Override
    public Observaciones guardarObservacion(Observaciones obs) {
        Observaciones o;
        try {
            o = (Observaciones) services.persist(obs);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            o = null;
        }
        return o;
    }

    /**
     * realiza los cambios en CatPredioPropietario, primero envia actualizar o
     * guardar la lista de propietarios, luego le cambia de estado a los
     * propietarios de son eliminados.
     * <p/>
     * Se genera los valores para el avaluo de liquidacion, impuesto, impeción,
     * revision, no adeudar y la linea de fabrica, despues envia a guadar en
     * PePermisoCabEdificacion las edificaciones del predio que son nuevas y
     * actualiza las que existen, seguido guardar o actualiza las
     * especificaciones de cada una de la edificaciones en la tabla
     * PeDetallePermiso.
     * <p/>
     * Despues envia a eliminar las edificaciones que son eliminadas y las
     * especificaciones de cada edificación, por ultimo actualiza PePermiso.
     *
     * @param listaPropietariosEliminar lista CatPredioPropietario a eliminar
     * @param listaPropietarios CatPredioPropietario a guardar a actualizar
     * @param permisoNuevo PePermiso
     * @param DetallesEdific PePermisoCabEdificacion a Guardar o actualizar,
     * tambien contiene la coleccion de PeDetallePermiso de cada una de
     * edificaciones del predio.
     * @param detallesEdificEliminar PePermisoCabEdificacion a eliminar
     * @param peDetallePermisoEliminar PeDetallePermiso a eliminar
     * @param formulas Formulas de calculo de Permiso Construccion.
     * @return PePermiso
     */
    @Override
    public PePermiso modificarLiquidacion(List<CatPredioPropietario> listaPropietariosEliminar, List<CatPredioPropietario> listaPropietarios,
            PePermiso permisoNuevo, List<PePermisoCabEdificacion> DetallesEdific, List<PePermisoCabEdificacion> detallesEdificEliminar,
            List<PeDetallePermiso> peDetallePermisoEliminar, MatFormulaTramite formulas) {

        GroovyUtil groovyUtil = new GroovyUtil(formulas.getFormula());
        try {
            CatPredioPropietario propietarioNuevo;
            for (CatPredioPropietario cpp : listaPropietarios) {
                if (cpp.getId() != null) {
                    services.update(cpp);
                } else {
                    propietarioNuevo = (CatPredioPropietario) services.persist(cpp);
                    permisoNuevo.setPropietarioPersona(propietarioNuevo.getEnte());
                }
            }

            if (!listaPropietariosEliminar.isEmpty()) {
                for (CatPredioPropietario listCat1 : listaPropietariosEliminar) {
                    services.update(listCat1);
                }
            }

            groovyUtil.setProperty("tipoPermiso", permisoNuevo.getTipoPermiso());
            groovyUtil.setProperty("permiso", permisoNuevo);
            permisoNuevo.setAvaluoLiquidacion((BigDecimal) groovyUtil.getExpression("getAvaluoLiquidacion", new Object[]{}));
            permisoNuevo.setImpuesto(((BigDecimal) groovyUtil.getExpression("getImpuesto", new Object[]{getTipoPermiso("IM").getValor()})));

            permisoNuevo.setEstado("A");
            permisoNuevo.setInspeccion(getTipoPermiso("IN").getValor());
            permisoNuevo.setRevision(getTipoPermiso("RV").getValor());
            permisoNuevo.setNoAdeudar(getTipoPermiso("ND").getValor());

            /*GENERACION DE LIQUIDACION*/
            String esquinero;
            if (permisoNuevo.getIdPredio().getPhh() > 0 && permisoNuevo.getIdPredio().getPhv() > 0) {
                CatPredio predio = manager.find(CatPredio.class, permisoNuevo.getIdPredio().getPredioRaiz().longValue());
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{predio});
            } else {
                esquinero = (String) groovyUtil.getExpression("isEsquinero", new Object[]{permisoNuevo.getIdPredio()});
            }
            permisoNuevo.setLineaF((((BigDecimal) groovyUtil.getExpression("generarLiquidacionLineaFabrica", new Object[]{permisoNuevo.getLineaFabrica(), esquinero}))));

            for (PePermisoCabEdificacion edificaciones : DetallesEdific) {
                List<PeDetallePermiso> list = new ArrayList<>();
                for (PeDetallePermiso d : (List<PeDetallePermiso>) edificaciones.getPeDetallePermisoCollection()) {
                    list.add(d);
                }

                PePermisoCabEdificacion edif;
                if (edificaciones.getId() == null) {
                    edificaciones.setIdPermiso(permisoNuevo);
                    edificaciones.setEstado(Boolean.TRUE);
                    edif = (PePermisoCabEdificacion) services.persist(edificaciones);
                } else {
                    services.update(edificaciones);
                    edif = edificaciones;
                }
                if (list.size() > 0) {
                    for (PeDetallePermiso detalleEdificaciones : list) {
                        if (detalleEdificaciones.getId() == null) {
                            detalleEdificaciones.setIdPermisoEdificacion(edif);
                            detalleEdificaciones.setEstado(Boolean.TRUE);
                            services.persist(detalleEdificaciones);
                        } else {
                            services.update(detalleEdificaciones);
                        }
                    }
                }
            }

            if (!peDetallePermisoEliminar.isEmpty()) {
                for (PeDetallePermiso pdp : peDetallePermisoEliminar) {
                    pdp.setEstado(Boolean.FALSE);
                    services.update(pdp);
                }
            }

            if (!detallesEdificEliminar.isEmpty()) {
                for (PePermisoCabEdificacion ppce : detallesEdificEliminar) {
                    ppce.setEstado(Boolean.FALSE);
                    services.update(ppce);
                }
            }
//            permisoNuevo.setPeDetallePermisoCollection(null);
            permisoNuevo.setPePermisoCabEdificacionCollection(null);
            actualizarPePermiso(permisoNuevo);
            permisoNuevo = (PePermiso) services.find(PePermiso.class, permisoNuevo.getId());
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return permisoNuevo;
    }

    /**
     * Busca en la tabla PeTipoPermiso por el codigo Ejemplo "IM"
     *
     * @param codigo Codigo de la columna codigo
     * @return PeTipoPermiso
     */
    @Override
    public PeTipoPermiso getTipoPermiso(String codigo) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("codigo", codigo);
        return manager.findObjectByParameter(PeTipoPermiso.class, paramt);
    }

    /**
     * Realiza el calculo del valor de LiquidaciÃ³n y envia a actualizar
     * HistoricoTramites
     *
     * @param avaluoLiquidacion El valor de la liquidacion.
     * @param ht HistoricoTramites
     */
    @Override
    public void actualizarHistoricoTramitesAndValorLiquidacion(BigDecimal avaluoLiquidacion, HistoricoTramites ht) {
        ht.setValorLiquidacion(avaluoLiquidacion);
        actualizarHistoricoTramites(ht);
    }

    /**
     * Busca en la tabla por el número de tramite
     *
     * @param numTramite Número de Tramite
     * @return PePermiso
     */
    @Override
    public PePermiso getPePermisoByNumTramite(String numTramite) {
        return (PePermiso) services.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{new BigInteger(numTramite)});
    }

    /**
     * Consulta en HistoricoReporteTramites por el nombre de tarea y por el id
     * del proceso y cambia el estado a false y envia a actualizar
     * HistoricoReporteTramites
     *
     * @param nombreTarea Nombre de tarea
     * @param idPreoceso Id de Proceso
     */
    @Override
    public void actualizarHistoricoReporteTramitesByTaskDef(String nombreTarea, String idPreoceso) {
        HistoricoReporteTramite hrt = (HistoricoReporteTramite) services.find(Querys.getReporteByNombreTareaSinEstado, new String[]{"nombreTarea", "idProceso"}, new Object[]{nombreTarea, idPreoceso});
        if (hrt != null) {
            hrt.setEstado(false);
            services.update(hrt);
        }
    }

    /**
     * Busca en la tabla CatPredio por el código predio Y el estado
     *
     * @param provincia
     * @param canton
     * @param mz
     * @param zona
     * @param parroquia
     * @param estado
     * @param lote
     * @param bloque
     * @param unidad
     * @param piso
     * @return CatPredio
     */
    @Override
    public CatPredio getCatPredioByCodigoPredio(short provincia, short canton, short parroquia, short zona, short sector, short mz, short lote, short bloque, short piso, short unidad, String estado) {
        return (CatPredio) services.find(Querys.getPredioByCodCatByEstado,
                new String[]{"provincia", "canton", "parroquia", "zona", "sector", "mz", "solar", "bloque", "piso", "unidad", "estado"},
                new Object[]{provincia, canton, parroquia, zona, sector, mz, lote, bloque, piso, unidad, estado});
    }

    /**
     * Busca en la Tabla PePermiso por el id de la tabla
     *
     * @param idPermiso id de la tabla PePermiso
     * @return PePermiso
     */
    @Override
    public PePermiso getPePermisoById(Long idPermiso) {
        PePermiso p = (PePermiso) services.find(PePermiso.class, idPermiso);
        Hibernate.initialize(p.getTipoPermiso());
        Hibernate.initialize(p.getTramite());
        return p;
    }

    @Override
    public HistoricoReporteTramite getHistoricoTramiteDet(String idProceso, Boolean estado) {
        Map paramt = new HashMap<>();
        paramt.put("proceso", idProceso);
        paramt.put("estado", estado);
        List<HistoricoReporteTramite> list = manager.findObjectByParameterList(HistoricoReporteTramite.class, paramt);
        if (!list.isEmpty()) {
            return list.get((list.size() - 1));
        }
//        return (HistoricoReporteTramite) services.find(Querys.getReporteByNombreTarea, new String[]{"nombreTarea", "idProceso", "estado"}, new Object[]{tarea, idProceso, estado});
        return null;
    }

    @Override
    public HistoricoTramiteDet getHistoricoTramiteDetByTramite(Long idTramite) {
        return (HistoricoTramiteDet) services.find(Querys.getHistoricoTramiteDetByTramite, new String[]{"numTramite"}, new Object[]{idTramite});
    }

    @Override
    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Map paramts) {
        return (MsgFormatoNotificacion) manager.findObjectByParameter(MsgFormatoNotificacion.class, paramts);
    }

    @Override
    public FichaIngresoNuevoServices getFichaServices() {
        return fichaServices;
    }

    /**
     * Busca en la tabla MatFormulaTramite por el id de GeTipoTramite
     *
     * @param l id de GeTipoTramite
     * @return MatFormulaTramite
     */
    @Override
    public MatFormulaTramite getMatFormulaTramite(Long l) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("estado", true);
        paramt.put("tipoTramite.id", l);
        return manager.findObjectByParameter(MatFormulaTramite.class, paramt);
    }

    /**
     * Busca en AclUser por el id.
     *
     * @param id Id de la tabla.
     * @return AclUser.
     */
    @Override
    public AclUser getAclUserById(Long id) {
        return manager.find(AclUser.class, id);
    }

    /**
     * Obtiene CatPredio Por el Id.
     *
     * @param id id de la tabla CatPredio
     * @return CatPredio
     */
    @Override
    public CatPredio getCatPredioById(Long id) {
        return manager.find(CatPredio.class, id);
    }

    @Override
    public void actualizarHistoricoReporteTramites(Collection<HistoricoReporteTramite> historicoReporteTramiteCollection) {
        try {
            for (HistoricoReporteTramite d : historicoReporteTramiteCollection) {
                d.setEstado(false);
                manager.update(d);
            }
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public PePermiso getPePermisoByIdPredio(Long id) {
        try {
            Map<String, Object> paramt = new HashMap<>();
            paramt.put("idPredio.id", id);
            return manager.findObjectByParameter(PePermiso.class, paramt);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<CatEscritura> getCatEscrituraByPredioList(Long id) {
        try {
            Map paramt = new HashMap<>();
            paramt.put("predio.id", id);
            return manager.findObjectByParameterList(CatEscritura.class, paramt);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public NormasConstruccionServices getNormasConstruccion() {
        return normasServices;
    }

    @Override
    public Observaciones guardarActualizarObservacionProp(List<CatPredioPropietario> listaPropietariosEliminar, List<CatPredioPropietario> listaPropietarios, HistoricoTramites histTramite, String name_user, String observacion, String nameTask) {
        Observaciones o = null;
        try {
            if (Utils.isNotEmpty(listaPropietariosEliminar)) {
                guardarOActualizarCatPredioPropietario(listaPropietariosEliminar);
            }
            if (Utils.isNotEmpty(listaPropietarios)) {
                guardarOActualizarCatPredioPropietario(listaPropietarios);
            }
            o = new Observaciones();
            o.setEstado(true);
            o.setFecCre(new Date());
            o.setTarea(nameTask);
            o.setIdTramite(histTramite);
            o.setUserCre(name_user);
            o.setObservacion(observacion);
            o = guardarObservacion(o);
        } catch (Exception e) {
            Logger.getLogger(PermisoConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    /**
     * Busca en la tabla {@link CatPredio} por el id de la ciudadela, Manzana y
     * Solar.
     *
     * @param id Id de entity {@link CatCiudadela}
     * @param mzUrb Manzana
     * @param solarUrb Solar
     * @return Entity {@link CatCiudadela} si existe caso contrario null.
     */
    @Override
    public CatPredio getCatPredioByCiudadelaMzSolar(Long id, String mzUrb, String solarUrb) {
        return (CatPredio) manager.find(Querys.getCatPredioByCdlByMzBySolar, new String[]{"mz", "solar", "cdla"}, new Object[]{mzUrb.toUpperCase(), solarUrb.toUpperCase(), id});
    }

    @Override
    public Integer existeHistoricoTrámites(Long id, String mzUrb, String solarUrb) {
//        BigInteger idciudadela = BigInteger.valueOf(id);
        return (Integer) manager.getNativeQuery(Querys.existeHistoricoTramite, new Object[]{mzUrb, solarUrb, BigInteger.valueOf(id), "Pendiente"});
    }

    @Override
    public CatEnte getCatEnteByCiRuc(String ciRuc) {
        return (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciRuc});
    }

    @Override
    public GeTipoTramite getGeTipoTramiteByActivitiKey(String solicitudServicio) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("activitykey", solicitudServicio);
        return manager.findObjectByParameter(GeTipoTramite.class, paramt);
    }

    @Override
    public CatEnte buscarEnte(CatEnte ente, Boolean buscarDatoSeg) {
        if (buscarDatoSeg) {
            CatEnte temp = getCatEnteByCiRuc(ente.getCiRuc());
            if (temp == null) {
                DatoSeguro datos = datoSeguroServices.getDatos(ente.getCiRuc(), !ente.getEsPersona(), 0);
                if (datos != null) {
                    temp = datoSeguroServices.getEnteFromDatoSeguro(datos);
                    if (temp.getTipoDocumento() == null) {
                        temp.setTipoDocumento(ente.getTipoDocumento());
                    }
                    if (temp.getEstadoCivil() == null) {
                        temp.setEstadoCivil(ente.getEstadoCivil());
                    }
                    if (temp.getDiscapacidad() == null) {
                        temp.setDiscapacidad(ente.getDiscapacidad());
                    }
                    return temp;
                } else {
                    return null;
                }
            } else {
                return temp;
            }
        } else {
            return getCatEnteByCiRuc(ente.getCiRuc());
        }
    }

}
