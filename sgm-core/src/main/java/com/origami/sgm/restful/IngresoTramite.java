/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.bpm.models.DatosTramite;
import com.origami.sgm.bpm.models.DatosTramiteDet;
import com.origami.sgm.bpm.models.ModelPermisoFunc;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.entities.PeTipoPermisoAdicionales;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenClaseLocal;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.entities.VuItems;
import com.origami.sgm.restful.models.ExtLog;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.http.HttpStatus;
import util.JsonUtil;

/**
 *
 * @author Angel Navarro
 */
@Path(value = "ingresoTramite")
@Produces(value = {"application/Json", "text/xml"})
public class IngresoTramite implements Serializable {

    private static final long serialVersionUID = 1l;
    private static final Logger LOG = Logger.getLogger(IngresoTramite.class.getName());

    @javax.inject.Inject
    private PropiedadHorizontalServices phs;
    @javax.inject.Inject
    private RentasServices rentasEjb;

    @javax.inject.Inject
    protected BpmBaseEngine engine;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private Entitymanager acl;
    private HashMap<String, Object> paramt;

    @POST
    @Path(value = "/registrar")
    public Response registrarTramite(String datos) {
        DatosTramite tramite;
        JsonUtil jsUtil = new JsonUtil();
        HistoricoTramites ht = null;

        try {
            tramite = (DatosTramite) jsUtil.fromJson(datos, DatosTramite.class);
            if (tramite != null) {
                GeTipoTramite tipoTramite = phs.getPermiso().getGeTipoTramiteById(tramite.getTipoTramite());
                if (tipoTramite == null) {
                    return Response.status(HttpStatus.SC_NOT_FOUND).entity(new ExtLog(2, "Error", "No se encontro Tipo de Tramite")).build();
                }
                ht = new HistoricoTramites();
                ht.setCorreos(tramite.getCorreo());
                ht.setTelefonos(tramite.getTelefono());
                ht.setTipoTramite(tipoTramite);
                ht.setTipoTramiteNombre(tramite.getNombreTramite());
                CatEnte solicitante;
                if (tramite.getSolicitante() != null) {
                    solicitante = manager.find(CatEnte.class, tramite.getSolicitante().longValue());
                } else if (tramite.getCi() != null) {
                    solicitante = phs.getPermiso().getCatEnteByCiRuc(tramite.getCi());
                } else { // Cuando Solicitante es nullo
                    return Response.ok(new ExtLog(2, "Error", "Falta Ingresar el Solicitante")).build();
                }
                ht.setNombrePropietario(solicitante.getNombreCompleto());
                ht.setSolicitante(solicitante);
                ht.setFecha(new Date());
                ht.setEstado("Pendiente");
                if (tramite.getNumPredio() != null) {
                    ht.setNumPredio(BigInteger.valueOf(tramite.getNumPredio()));
                }
                if (tramite.getCdla() != null) {
                    ht.setUrbanizacion(new CatCiudadela(tramite.getCdla()));
                }
                ht.setMz(tramite.getMzUrb());
                ht.setSolar(tramite.getSlUrb());
                paramt = new HashMap<>();
                if (tipoTramite.getParametrosDisparadorCollection() != null) {
                    for (ParametrosDisparador pd : tipoTramite.getParametrosDisparadorCollection()) {
                        if (pd.getVarInterfaz() != null && pd.getVarInterfaz().trim().length() > 0) {
                            paramt.put(pd.getVarInterfaz(), pd.getInterfaz());
                        }
                        if (pd.getResponsable() != null) {
                            paramt.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                        }
                    }
                }
                paramt.put("tdocs", true);
                paramt.put("reasignar", 2);
                if (tipoTramite.getDisparador() != null) {
                    paramt.put("carpeta", tipoTramite.getDisparador().getCarpeta());
                } else {
                    paramt.put("carpeta", tipoTramite.getCarpeta());
                }
                paramt.put("listaArchivos", null);
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("prioridad", 50);
                paramt.put("iniciar", false);
                if (tipoTramite.getId() == 50L) {
                    paramt.put("tiene_archivos", true);
                }

                ht.setUserCreador(22L);
                ht = (HistoricoTramites) manager.persist(ht);
                if (ht != null) {
                    subTramite(ht, tramite);
                    ht.setId(phs.getPermiso().generarIdTramite());
                    paramt.put("descripcion", tramite.getNombreTramite());
                    paramt.put("tramite", ht.getId());
                    try {
                        ProcessInstance pro = null;
                        if (tipoTramite.getDisparador() != null) {
                            pro = engine.startProcessByDefinitionKey(tipoTramite.getDisparador().getDescripcion(), paramt);
                        } else {
                            pro = engine.startProcessByDefinitionKey(tipoTramite.getActivitykey(), paramt);
                        }
                        ht.setCarpetaRep(ht.getId() + "-" + pro.getId());
                        ht.setIdProcesoTemp(pro.getId());
                        if (tipoTramite.getId() == 50L || tipoTramite.getId() == 20L) {
                            ht.setIdProceso(pro.getId());
                        }
                        if (tramite.getDetalle() == null) {
                            CatPredio cp = phs.getFichaServices().getPredioByNum(tramite.getNumPredio());
                            nuevoDet(ht, cp);
                        } else {
                            for (DatosTramiteDet d : tramite.getDetalle()) {
                                CatPredio cp = phs.getFichaServices().getPredioByNum(d.getPredios());
                                nuevoDet(ht, cp);
                            }
                        }
                        manager.persist(ht);
                        phs.guardarObservaciones(ht, "ventanilla", "Ingreso Ventanilla Publica: " + ht.getId(), "Ingreso Requisitos");
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    return Response.ok(new ExtLog(1, "Informacion Guardada", ("Tramite Ingresado Correctamente: " + ht.getId()))).build();

                } else {
                    return Response.status(HttpStatus.SC_METHOD_FAILURE).entity(new ExtLog(2, "Error", "Ocurrio un error al ingresar Tramite")).build();
                }
            } else {
                return Response.status(HttpStatus.SC_NO_CONTENT).entity(new ExtLog(2, "Error", "Ocurrio un error al ingresar Tramite")).build();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            return Response.status(HttpStatus.SC_METHOD_FAILURE).entity(new ExtLog(2, "Error", e.getMessage())).build();
        }
    }

    /**
     * Se envia crear un nuevo registro en la tabla HistoricoTramiteDet
     *
     * @param ht
     * @param predio
     * @return
     */
    private void nuevoDet(HistoricoTramites ht, CatPredio predio) {
        HistoricoTramiteDet dt = new HistoricoTramiteDet();
        dt.setTramite(ht);
        dt.setPredio(predio);
        dt.setFecCre(new Date());
        dt.setEstado(true);
        manager.persist(dt);
    }

    /**
     * Verificamos si titne un subTipo el tramite
     *
     * @param ht
     * @param tipotramite
     * @param numPredio
     * @param subTipo
     */
    private void subTramite(HistoricoTramites ht, DatosTramite d) {
        try {
            switch (d.getTipoTramite().intValue()) {
                case 2: // Permiso de Construccion
                    if (d.getSubTramite() == null) {
                        return;
                    }
                    PeTipoPermiso stp = (PeTipoPermiso) manager.find(PeTipoPermiso.class, d.getSubTramite());
                    if (stp != null) {
                        ht.setTipoTramiteNombre(stp.getDescripcion());
                    }
                    break;
                case 14: // Otros Tramites
                    if (d.getSubTramite() == null) {
                        return;
                    }
                    OtrosTramites ot = manager.find(OtrosTramites.class, d.getSubTramite());
                    ht.setSubTipoTramite(ot);
                    ht.setTipoTramiteNombre(ot.getTipoTramite());
                    break;
                case 6: // Permisos adicionales
                    if (d.getSubTramite() == null) {
                        return;
                    }
                    PeTipoPermisoAdicionales tpa = manager.find(PeTipoPermisoAdicionales.class, d.getSubTramite());
                    PePermisosAdicionales pa = new PePermisosAdicionales();
                    pa.setTipoPermisoAdicional(tpa);
                    pa.setPredio(phs.getFichaServices().getPredioByNum(d.getNumPredio()));
                    pa.setFechaEmision(new Date());
                    pa.setCartaAdosamiento(false);
                    pa.setNumTramite(ht.getId());
                    ht.setTipoTramiteNombre(tpa.getDescripcion());
                    manager.persist(pa);
                    break;
                case 20: // Tramites de Alcladia
                    if (d.getSubTramite() == null) {
                        return;
                    }
                    VuItems tipo = manager.find(VuItems.class, d.getSubTramite());
                    SvSolicitudServicios solicitud = new SvSolicitudServicios();
                    AclUser secretaria = phs.getPermiso().getAclUserById(18L);
                    AclUser asistente = phs.getPermiso().getAclUserById(197L);
                    Boolean audiencia = tipo.getId().compareTo(783L) == 0;
                    if (audiencia) {
                        if (d.getLugarAdu() != null && !d.getLugarAdu().equals(0L)) {
                            if (d.getLugarAdu().compareTo(784L) == 0) {
                                secretaria = phs.getPermiso().getAclUserById(280L);
                                asistente = phs.getPermiso().getAclUserById(280L);
                            }

                            solicitud.setLugarAudiencia(new VuItems(d.getLugarAdu()));
                        }
                    } else {
                        if (d.getCdla() != null && !d.getCdla().equals(0L)) {
                            CatCiudadela cd = manager.find(CatCiudadela.class, d.getCdla());
                            solicitud.setCdla(cd);
                            solicitud.setParroquia(cd.getCodParroquia());
                        }
                    }
                    solicitud.setTramite(ht);
                    solicitud.setEnteSolicitante(ht.getSolicitante());
                    solicitud.setStatus("Proceso");
                    solicitud.setSolicitudInterna(false);
                    solicitud.setAsignado(false);
                    solicitud.setFechaCreacion(new Date());
                    solicitud.setDescripcionInconveniente(d.getObservacion());
                    solicitud.setTipoServicio(tipo);
                    solicitud.setRepresentante(d.getDescSolicitante());
                    solicitud.setFechaInconveniente(d.getFecha());
                    paramt.put("asistenteAlcaldia", asistente.getUsuario());
                    paramt.put("secretariaAlcaldia", secretaria.getUsuario());
                    paramt.put("to", ht.getCorreos());
                    paramt.put("audiencia", audiencia);
                    manager.persist(solicitud);
                    break;
                case 50: // Permisos de Funcionamiento(Locales Comerciales)

                    RenPermisosFuncionamientoLocalComercial permFunc = new RenPermisosFuncionamientoLocalComercial(null, ht);
                    ModelPermisoFunc model = d.getPermisoFunc();

                    permFunc.setEsPublico(true);
                    permFunc.setFechaEmision(new Date());
                    permFunc.setLocalComercial(new RenLocalComercial(model.getIdLocal()));
                    permFunc.setUsuarioIngreso("ventanilla");
                    permFunc.setPrimeraVez(model.getPrimeraVez());
                    permFunc.setActivos(model.getActivos());
                    permFunc.setPatente(model.getPatente());
                    permFunc.setRotulos(model.getRotulos());
                    permFunc.setTasaHabilitacion(model.getTasaHabilitacion());
                    permFunc.setTurismo(model.getTurismo());
                    permFunc.setClaseLocal(new RenClaseLocal(model.getClaseLocal()));
                    if (model.getAfiliacionCamara() != null && model.getAfiliacionCamara() > 0) {
                        RenAfiliacionCamaraProduccion ac = new RenAfiliacionCamaraProduccion();
                        ac.setId(model.getAfiliacionCamara());
                        permFunc.setAfiliacionCamara(ac);
                    }
                    if (model.getContador() != null) {
                        permFunc.setContador(new CatEnte(model.getContador()));
                    }
                    permFunc.setHt(ht);
                    permFunc.setMtrsRotulo(model.getMtrsRotulo());
                    permFunc.setTipoRotulo(model.getTipoRotulo());
                    permFunc.setTipo(ht.getSolicitante().getEsPersona() ? 1 : 2);
//                    permFunc.setNumDeclaracion();
                    permFunc.setNumTrabajadores(model.getNumTrabajadores());
                    permFunc = (RenPermisosFuncionamientoLocalComercial) manager.persist(permFunc);
//                    permFunc = rentasEjb.guadarPermisoFuncionamiento(permFunc, ht.getSolicitante(), 22L, activos, balance);
                    if (permFunc != null) {
                        // Registro de activos
                        RenActivosLocalComercial activos = null;
                        if (model.getActivos()) {
                            activos = new RenActivosLocalComercial();
                            activos.setFechaIngreso(new Date());
                            activos.setUsuarioIngreso("ventanilla");
                            activos.setEstado(Boolean.TRUE);
                            activos.setActivoTotal(model.getActivoTotal());
                            activos.setAnioBalance(model.getAnioBalance());
                            activos.setPasivoContingente(model.getPasivoContingente());
                            activos.setPasivoTotal(model.getPasivoTotal());
                            activos.setPorcentajeIngreso(model.getPorcentajeIngreso());
                            activos.setPermiso(permFunc);
                            activos.setLocalComercial(permFunc.getLocalComercial());
                            manager.persist(activos);
                        }
                        // Registro de Patente
                        RenBalanceLocalComercial balance = null;
                        if (model.getPatente()) {
                            balance = new RenBalanceLocalComercial();
                            balance.setEstado(Boolean.TRUE);
                            balance.setCapital(model.getCapital());
                            balance.setAnioBalance(model.getAnioBalance());
                            balance.setPermiso(permFunc);
                            balance.setLocalComercial(permFunc.getLocalComercial());
                            manager.persist(balance);
                        }

                        paramt.put("task_def", d.getNombreTramite());
                        if (permFunc.getClaseLocal().getId() != 1l) {
                            paramt.put("es_bombero", true);
                        } else {
                            paramt.put("es_bombero", false);
                        }

                        paramt.put("es_turismo", model.getTurismo());
                        paramt.put("aprobado", false);
                        paramt.put("primera_vez", model.getPrimeraVez());
                    }

                    break;
                default:
                    break;
            }
            manager.update(ht);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

}
