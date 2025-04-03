/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author JOAO ISRAEL SANGA CHAVARRÍA
 */
@Named
@ViewScoped
public class AprobacionReporte extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AprobacionReporte.class.getName());

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    private HistoricoTramites ht;
    private List<HistoricoReporteTramite> hrts;
    private String nombreReporte;
    private Boolean aprobar = false;
    private Boolean esReporte;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private CatEnte ente;
    private Object numLiquidacion;
    private GeTipoTramite tipoTramite;
    private Boolean pagoRealizado;
    private RenLiquidacion liquidacion;
    private RenEstadoLiquidacion estado;
    private RenTipoLiquidacion tipoLiq;
    private List<RenRubrosLiquidacion> rubros;
    private List<RenDetLiquidacion> rubrosAGuardar;
    private CmMultas multa;
    private BigDecimal valorLiquidacionTemp;
    private AclUser tecnico;

    @PostConstruct
    public void init() {

        try {
            if (uSession.esLogueado() && uSession.getTaskID() != null) {
                liquidacion = new RenLiquidacion();
                params = new HashMap();
                obs = new Observaciones();
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht == null) {
                    return;
                }

                valorLiquidacionTemp = ht.getValorLiquidacion();

                if (ht.getMultasCollection() != null && !ht.getMultasCollection().isEmpty()) {
                    multa = (CmMultas) acl.find(QuerysFinanciero.getLastMultaByTramite, new String[]{"tramite"}, new Object[]{ht});
                    ht.setTipoTramite((GeTipoTramite) acl.find(GeTipoTramite.class, 56L));
                    ht.setTipoTramiteNombre(ht.getTipoTramite().getDescripcion());
                    if (ht.getTipoTramite().getRenTipoLiquidacion() != null) {
                        tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, Long.parseLong(ht.getTipoTramite().getRenTipoLiquidacion() + ""));

                        if (tipoLiq != null) {
                            liquidacion.setTipoLiquidacion(tipoLiq);
                            rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                        }
                    }
                    llenarValoresRubros();
                    return;
                }
                if (ht.getFnSolicitudExoneracions() != null && ht.getFnSolicitudExoneracions().size() > 0) {
                    JsfUti.redirectFaces("/vistaprocesos/financiero/aplicarExoneracion.xhtml");
                    return;
                }
                if (ht.getPermisoDeFuncionamientoLC() != null) {
                    JsfUti.redirectFaces("/vistaprocesos/permisosFuncionamiento/generarPermisosFuncionamiento.xhtml");
                    return;
                }

                tipoTramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());

                pagoRealizado = !(tipoTramite.getId().intValue() == 7 || tipoTramite.getId().intValue() == 8 || tipoTramite.getId().intValue() == 9
                        || tipoTramite.getId().intValue() == 15 || tipoTramite.getId().intValue() == 17 || (ht.getId().compareTo(new Long("1450")) == 1));

                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                esReporte = hrts != null && !hrts.isEmpty();

                // INCLUIDO PARA PRUEBAS
                if (ht.getTipoTramite().getRenTipoLiquidacion() != null) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, Long.parseLong(ht.getTipoTramite().getRenTipoLiquidacion() + ""));

                    if (tipoLiq != null) {
                        liquidacion.setTipoLiquidacion(tipoLiq);
                        rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    }
                } else {
                    buscarTipoLiqSubtramite();
                }
                llenarValoresRubros();

                // INCLUIDO PARA PRUEBAS
            } else {
                this.continuar();
            }
            //liquidacion = new RenLiquidacion();            
            //estado = (RenEstadoLiquidacion) acl.find(RenEstadoLiquidacion.class, new Long(2));
            //liquidacion.setEstado(true);
            //transaccionesList = acl.findAll(QuerysFinanciero.getRenTransacciones, new String[]{}, new Object[]{});
            //asignarTipoLiquidacionEdificaciones();
        } catch (Exception e) {
            Logger.getLogger(AprobacionReporte.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarTipoLiqSubtramite() {
        Long cod_titulo;
        OtrosTramites oTramite = ht.getSubTipoTramite();
        String prefijo, nombreTitulo, nombreAplicacion;

        switch (Integer.parseInt(tipoTramite.getId() + "")) {
            case 6:
                PePermisosAdicionales ppa = (PePermisosAdicionales) acl.find(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                if (ppa == null) {
                    JsfUti.messageInfo(null, "Info", "Hubo un problema con el trámite. Comuníquise con sistemas");
                    return;
                }
                if (ppa.getTipoPermisoAdicional().getId() == 1) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, 35L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if (ppa.getTipoPermisoAdicional().getId() == 2) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, 191L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if (ppa.getTipoPermisoAdicional().getId() == 3) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, 248L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if (ppa.getTipoPermisoAdicional().getId() == 4) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, 249L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if (ppa.getTipoPermisoAdicional().getId() == 5) {
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, 250L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                break;
            case 14:

                prefijo = oTramite.getPrefijo();
                nombreTitulo = oTramite.getTituloReporte();
                nombreAplicacion = oTramite.getCodigoAplicacion();

                if (prefijo == null || prefijo == "") {
                    prefijo = "SOB";
                }
                if (nombreTitulo == null || nombreTitulo == "") {
                    nombreTitulo = "OTRAS CONSTRUCCIONES";
                }

                cod_titulo = (Long) acl.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
                if (cod_titulo == null) {
                    cod_titulo = 25L;
                }
                tipoLiq = (RenTipoLiquidacion) acl.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo});
                rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                break;
            case 43:
                prefijo = oTramite.getPrefijo();
                nombreTitulo = oTramite.getTituloReporte();
                nombreAplicacion = oTramite.getCodigoAplicacion();

                if (prefijo == null || prefijo == "") {
                    prefijo = "SOB";
                }
                if (nombreTitulo == null || nombreTitulo == "") {
                    nombreTitulo = "OTRAS CONSTRUCCIONES";
                }

                cod_titulo = (Long) acl.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
                if (cod_titulo == null) {
                    cod_titulo = 25L;
                }
                tipoLiq = (RenTipoLiquidacion) acl.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo});
                rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                break;
        }
    }

    public void llenarValoresRubros() {

        String usuarioCreador = session.getName_user();
        Calendar c2 = Calendar.getInstance();
        HistoricoTramiteDet proceso;
        HistoricoReporteTramite numReporte;
        PeInspeccionFinal inspeccion = null;
        rubrosAGuardar = new ArrayList();
        CatPredio predio = null;

        try {

            liquidacion.setFechaIngreso(new Date());
            liquidacion.setTotalPago(ht.getValorLiquidacion());
            liquidacion.setSaldo(ht.getValorLiquidacion());
            liquidacion.setTramite(ht);
            liquidacion.setUsuarioIngreso(uSession.getName_user());
            liquidacion.setEstadoLiquidacion(estado);

            if (ht.getNumPredio() != null) {
                predio = (CatPredio) acl.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
            }
            liquidacion.setPredio(predio);

            switch (ht.getTipoTramite().getId().intValue()) {
                case 2:

                    PePermiso permis = (PePermiso) acl.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{ht});
                    if (permis == null) {
                        return;
                    }
                    liquidacion.setNumReporte(permis.getNumReporte() + "");
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad() == 1) {
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getImpuesto(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getImpuesto());
                        }
                        if (temp.getPrioridad() == 2) {
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getInspeccion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getInspeccion());
                        }
                        if (temp.getPrioridad() == 3) {
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getRevision(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getRevision());
                        }
                        if (temp.getPrioridad() == 4) {
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getNoAdeudar(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getNoAdeudar());
                        }
                        if (temp.getPrioridad() == 5) {
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getLineaF(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getLineaF());
                        }
                    }
                    break;
                case 9:
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                    numReporte = permisoServices.getHistoricoTramiteDet((ht.getIdProceso() == null ? ht.getIdProcesoTemp() : ht.getIdProceso()), Boolean.TRUE);

                    if (proceso != null) {
                        c2.setTime(proceso.getFecCre());
                    }
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad() == 1) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    break;

                case 4: // Inspección Final

                    if (ht.getPeInspeccionFinal() != null) {
                        inspeccion = ht.getPeInspeccionFinal();
                    } else {
                        System.out.println("Error al generar la liquidación");
                        return;
                    }

                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getImpuesto(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(inspeccion.getImpuesto());
                        }
                        if (temp.getPrioridad().compareTo(2L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getInspeccion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(inspeccion.getInspeccion());
                        }
                        if (temp.getPrioridad().compareTo(3L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getNoAdeudar(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(inspeccion.getNoAdeudar());
                        }
                        if (temp.getPrioridad().compareTo(4L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getRevicion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(inspeccion.getRevicion());
                        }
                        if (temp.getPrioridad().compareTo(5L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(new BigDecimal(0.00), temp.getId(), temp.getDescripcion()));
                            temp.setValor(new BigDecimal(0.00));
                        }
                    }
                    liquidacion.setNumReporte(inspeccion.getNumReporte() + "");
                    break;

                case 6: //Permisos Adicionales

                    PePermisosAdicionales ppa;
                    ppa = (PePermisosAdicionales) acl.find(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                    if (ppa == null) {
                        System.out.println("Error al generar la liquidación");
                        return;
                    }
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getImpMunicipalAreaedif(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getImpMunicipalAreaedif());
                        }
                        if (temp.getPrioridad().compareTo(2L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getInspeccion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getInspeccion());
                        }
                        if (temp.getPrioridad().compareTo(3L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getNoAdeudarMun(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getNoAdeudarMun());
                        }
                        if (temp.getPrioridad().compareTo(4L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getRevisionAprobPlanos(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getRevisionAprobPlanos());
                        }
                        if (temp.getPrioridad().compareTo(5L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getLineaFabricaCalculada(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getLineaFabricaCalculada());
                        }
                    }
                    liquidacion.setNumReporte(ppa.getNumReporte() + "");
                    break;

                case 7: // División de Predio
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());

                    if (proceso != null) {
                        for (RenRubrosLiquidacion temp : rubros) {
                            if (temp.getPrioridad().compareTo(1L) == 0) {
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                    }
                    liquidacion.setNumReporte(ht.getNumTramiteXDepartamento() + "");
                    break;
                case 8://FUSION DE PREDIOS
                    numReporte = permisoServices.getHistoricoTramiteDet((ht.getIdProceso() == null ? ht.getIdProcesoTemp() : ht.getIdProceso()), Boolean.TRUE);
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    break;
                case 36://RESELLADO DE PLANOS
                    numReporte = permisoServices.getHistoricoTramiteDet((ht.getIdProceso() == null ? ht.getIdProcesoTemp() : ht.getIdProceso()), Boolean.TRUE);

                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    break;

                case 14: // Otros Trámites
                    if (ht.getValorLiquidacion() != null) {
                        proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                        numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                        for (RenRubrosLiquidacion temp : rubros) {
                            if (temp.getPrioridad().compareTo(1L) == 0) {
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                        liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    }
                    break;

                case 43: // Otros Trámites Solo liquidación
                    if (ht.getValorLiquidacion() != null) {
                        proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                        numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                        for (RenRubrosLiquidacion temp : rubros) {
                            if (temp.getPrioridad().compareTo(1L) == 0) {
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                        liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    }
                    break;

                case 44: // Otros Trámites liquidación y certificado
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                    numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    liquidacion.setNumReporte(numReporte.getSecuencia() + "");
                    break;

                case 56:
                    for (RenRubrosLiquidacion temp : rubros) {
                        if (temp.getPrioridad().compareTo(1L) == 0) {
                            if (multa != null) {
                                rubrosAGuardar.add(new RenDetLiquidacion(multa.getValor(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(multa.getValor());
                                ht.setValorLiquidacion(multa.getValor());
                                liquidacion.setTotalPago(multa.getValor());
                                liquidacion.setSaldo(multa.getValor());
                            } else {
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                    }

                    break;
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void mostrarObservaciones(boolean aprobado) {
        aprobar = aprobado;
        JsfUti.executeJS("PF('obs').show();");
        JsfUti.update("frmObs");
    }

    public void guardarRubros(List<RenDetLiquidacion> detalles) {
        try {
            servicesRentas.guardarRubrosPorLiquidacion(detalles);
            JsfUti.messageInfo(null, "Info", "Rubros guardados correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completarTarea() {
        AclUser userCreador = new AclUser();
        try {
            if (getVariables(ht.getIdProcesoTemp(), "revisor") != null) {
                Object tecnicoUser = getVariables(ht.getIdProcesoTemp(), "revisor");
                tecnico = permisoServices.getAclUserByUser(tecnicoUser.toString());
            }

            List<ParametrosDisparador> p = permisoServices.getParametroDisparadorByTipoTramite(ht.getTipoTramite().getId());
            if (p != null) {
                for (ParametrosDisparador p1 : p) {
                    if ("digitalizador".equals(p1.getVarResp())) {
                        userCreador = (AclUser) EntityBeanCopy.clone(acl.find(AclUser.class, p1.getResponsable().getId()));
                        params.put("digitalizador", userCreador.getUsuario());
                    }
                }
            }

            if (!obs.getObservacion().equals("")) {

                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea("Aprobación de Reporte");
                params.put("aprobado", aprobar);

                servicesDP.guardarObservacion(obs);
                BigDecimal liquidacion = null;

                if (multa != null) {
                    ht.setValorLiquidacion(valorLiquidacionTemp);
                }

                liquidacion = ht.getValorLiquidacion();
                if (ht.getTipoTramite() != null) {
                    MsgFormatoNotificacion ms = new MsgFormatoNotificacion();

                    String mensaje = "<h2>Acercarse a cancelar </h2><br/> <h3> "
                            + "Valor a Cancelar :  $ " + liquidacion + " <br/>"
                            + "	  <br/>";
                    Map pa = new HashMap<>();
                    pa.put("tipo.id", 2L);
                    ms = permisoServices.getMsgFormatoNotificacionByTipo(pa);


                    if (numLiquidacion == null || multa != null) {
                        this.liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                        this.liquidacion.setEstadoCoactiva(1);
                        this.liquidacion.setComprador(ht.getSolicitante());
                        //this.liquidacion.setObservacion(ht.getNombrePropietario());
                        this.liquidacion.setTipoLiquidacion(tipoLiq);
                        this.liquidacion.setAnio(Utils.getAnio(new Date()));
                        this.liquidacion = servicesRentas.guardarLiquidacionYRubros(this.liquidacion, rubrosAGuardar, tipoLiq, null, null, ht, numLiquidacion == null ? null : numLiquidacion.toString());
                        //this.liquidacion = servicesRentas.guardarLiquidacionYRubros(this.liquidacion, rubrosAGuardar, tipoLiq, null, null, ht, numLiquidacion.toString());

                        ht.setNumLiquidacion(numLiquidacion != null ? new BigInteger(numLiquidacion.toString()) : this.liquidacion.getNumLiquidacion());
                        numLiquidacion = this.liquidacion.getNumLiquidacion();
                        if (aprobar) {
                            params.put("to", this.getCorreosByCatEnte(ht.getSolicitante()));
                            params.put("subject", "La Liquidacion de " + ht.getTipoTramite().getDescripcion() + " del tramite # " + ht.getId() + " esta realizada");
                            params.put("message", ms.getHeader() + mensaje + ms.getFooter());
                        }
                    } else {
                        if (!aprobar) {
                            if (tecnico == null) {
                                params.put("to", "");
                            } else {
                                params.put("to", tecnico.getEnte() == null ? "" : tecnico.getEnte().getEmails());
                            }
                            params.put("subject", "EL trámite " + ht.getId() + " esta mal generada");
                            params.put("message", (ms.getHeader() + "<br/><br/>"
                                    + "<h1/> Liquidación mal Generada."
                                    + "<br/><br/> "
                                    + obs.getObservacion()
                                    + "<br/><br/> "
                                    + ms.getFooter()));
                        }
                    }
                    permisoServices.actualizarHistoricoTramites(ht);
                }
                if (numLiquidacion == null) {
                    JsfUti.messageError(null, "Error.", "Error de conexión con Sistema SAC.");
                    return;
                }
                this.completeTask(this.getTaskId(), params);
                this.continuar();

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void verDocumento(HistoricoReporteTramite doc) {
        this.showDocuments(doc.getUrl(), "pdf");
    }

    public void descargarDocumento(HistoricoReporteTramite doc) {
        this.descargarDocumento(doc.getUrl());
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public Boolean getAprobar() {
        return aprobar;
    }

    public void setAprobar(Boolean aprobar) {
        this.aprobar = aprobar;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public Boolean getEsReporte() {
        return esReporte;
    }

    public void setEsReporte(Boolean esReporte) {
        this.esReporte = esReporte;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public List<RenRubrosLiquidacion> getRubros() {
        return rubros;
    }

    public void setRubros(List<RenRubrosLiquidacion> rubros) {
        this.rubros = rubros;
    }

    public List<RenDetLiquidacion> getRubrosAGuardar() {
        return rubrosAGuardar;
    }

    public void setRubrosAGuardar(List<RenDetLiquidacion> rubrosAGuardar) {
        this.rubrosAGuardar = rubrosAGuardar;
    }

    public CmMultas getMulta() {
        return multa;
    }

    public void setMulta(CmMultas multa) {
        this.multa = multa;
    }

}
