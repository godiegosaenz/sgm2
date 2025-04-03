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
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteCorreo;
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
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class RentasPruebasView extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RentasPruebasView.class.getName());
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    
    private HistoricoTramites ht;
    private List<HistoricoReporteTramite> hrts;
    private String nombreReporte;
    private Boolean aprobar = false;
    private Boolean esReporte;
    private HashMap<String, Object> params;
    private Observaciones obs;
    private GeTipoTramite tipoTramite;
    private Boolean pagoRealizado;
    private Object numLiquidacion;
    private RenLiquidacion liquidacion;
    private RenEstadoLiquidacion estado;
    private List<RenRubrosLiquidacion> rubros;
    private List<RenDetLiquidacion> rubrosAGuardar;
    private RenTipoLiquidacion tipoLiq;
    
    @PostConstruct
    public void init() {

        try {
            if (uSession != null && uSession.getTaskID() != null) {
                params = new HashMap();
                obs = new Observaciones();
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht == null) {
                    return;
                }
                liquidacion = new RenLiquidacion();
                estado = (RenEstadoLiquidacion) acl.find(RenEstadoLiquidacion.class, new Long(2));
                tipoTramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());

                pagoRealizado = !(tipoTramite.getId().intValue() == 7 || tipoTramite.getId().intValue() == 8 || tipoTramite.getId().intValue() == 9
                        || tipoTramite.getId().intValue() == 15 || tipoTramite.getId().intValue() == 17 || (ht.getId().compareTo(new Long("1450")) == 1));

                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                esReporte = hrts != null && !hrts.isEmpty();
                if(ht.getTipoTramite().getRenTipoLiquidacion()!=null){
                    tipoLiq = (RenTipoLiquidacion) acl.find(RenTipoLiquidacion.class, Long.parseLong(ht.getTipoTramite().getRenTipoLiquidacion()+""));

                    if(tipoLiq != null){
                        liquidacion.setTipoLiquidacion(tipoLiq);
                        rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    }
                }else{
                    buscarTipoLiqSubtramite();
                }
                llenarValoresRubros();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RentasPruebasView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void buscarTipoLiqSubtramite(){
        Long cod_titulo;
        OtrosTramites oTramite = ht.getSubTipoTramite();
        String prefijo, nombreTitulo, nombreAplicacion;

        switch(Integer.parseInt(tipoTramite.getId()+"")){
            case 6:
                PePermisosAdicionales ppa = (PePermisosAdicionales)acl.find(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                if(ppa == null){
                    JsfUti.messageInfo(null, "Info", "Hubo un problema con el trámite. Comuníquise con sistemas");
                    return;
                }
                if(ppa.getTipoPermisoAdicional().getId() == 1){
                    tipoLiq = (RenTipoLiquidacion)acl.find(RenTipoLiquidacion.class, 35L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if(ppa.getTipoPermisoAdicional().getId() == 2){
                    tipoLiq = (RenTipoLiquidacion)acl.find(RenTipoLiquidacion.class, 191L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if(ppa.getTipoPermisoAdicional().getId() == 3){
                    tipoLiq = (RenTipoLiquidacion)acl.find(RenTipoLiquidacion.class, 248L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if(ppa.getTipoPermisoAdicional().getId() == 4){
                    tipoLiq = (RenTipoLiquidacion)acl.find(RenTipoLiquidacion.class, 249L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }
                if(ppa.getTipoPermisoAdicional().getId() == 5){
                    tipoLiq = (RenTipoLiquidacion)acl.find(RenTipoLiquidacion.class, 250L);
                    rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                    return;
                }   
                break;
            case 14:
                
                prefijo= oTramite.getPrefijo();
                nombreTitulo = oTramite.getTituloReporte();
                nombreAplicacion = oTramite.getCodigoAplicacion();

                if(prefijo == null || prefijo == "")
                    prefijo = "SOB";
                if(nombreTitulo == null || nombreTitulo == "")
                    nombreTitulo = "OTRAS CONSTRUCCIONES";

                cod_titulo = (Long) acl.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
                if(cod_titulo == null)
                    cod_titulo = 25L;
                tipoLiq = (RenTipoLiquidacion)acl.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo});
                rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                break;
            case 43:
                prefijo= oTramite.getPrefijo();
                nombreTitulo = oTramite.getTituloReporte();
                nombreAplicacion = oTramite.getCodigoAplicacion();

                if(prefijo == null || prefijo == "")
                    prefijo = "SOB";
                if(nombreTitulo == null || nombreTitulo == "")
                    nombreTitulo = "OTRAS CONSTRUCCIONES";

                cod_titulo = (Long) acl.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{prefijo, nombreTitulo});
                if(cod_titulo == null)
                    cod_titulo = 25L;
                tipoLiq = (RenTipoLiquidacion)acl.find(QuerysFinanciero.getRenTipoLiquidacionByCodTitReporte, new String[]{"codtitrep"}, new Object[]{cod_titulo});
                rubros = (List<RenRubrosLiquidacion>) tipoLiq.getRenRubrosLiquidacionCollection();
                break;
        }
    }
    
    public void agregarRubro(RenRubrosLiquidacion rubro){
        if(!rubros.contains(rubro))
            rubros.add(rubro);
        else
            JsfUti.messageInfo(null, "Info", "Se agregó el rubro correctamente");
    }
    
    public void mostrarObservaciones(boolean aprobado) {
        aprobar = aprobado;
        JsfUti.update("frmObs");
        JsfUti.executeJS("PF('obs').show();");
    }
    
    public void completarTarea() {
        AclUser userCreador = new AclUser();
        EnteCorreo correoTecn = null;
        //RenDetLiquidacion detalle;
        //List<RenDetLiquidacion> detalleList = new ArrayList<>();
        try {
            Object tecnicoUser = getVariables(ht.getIdProcesoTemp(), "revisor");
            AclUser tecnico = permisoServices.getAclUserByUser(tecnicoUser.toString());
            if (tecnico.getEnte() != null && !tecnico.getEnte().getEnteCorreoCollection().isEmpty()) {
                correoTecn = tecnico.getEnte().getEnteCorreoCollection().get(0);
            }

            List<ParametrosDisparador> p = permisoServices.getParametroDisparadorByTipoTramite(ht.getTipoTramite().getId());
            for (ParametrosDisparador p1 : p) {
                if ("digitalizador".equals(p1.getVarResp())) {
                    userCreador = (AclUser) EntityBeanCopy.clone(acl.find(AclUser.class, p1.getResponsable().getId()));
                    params.put("digitalizador", userCreador.getUsuario());
                }
            }

            if (!obs.getObservacion().equals("")) {

                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea("Aprobación de Reporte");
                params.put("aprobado", aprobar);

                acl.persist(obs);
                BigDecimal liquidacion = null;
                liquidacion = ht.getValorLiquidacion();
                if (ht.getTipoTramite() != null) {
                    MsgFormatoNotificacion ms = new MsgFormatoNotificacion();

                    String mensaje = "<h2>Acercarse a cancelar </h2><br/> <h3> "
                            + "Valor a Cancelar :  $ " + liquidacion + " <br/>"
                            + "	  <br/> ";
                    Map pa = new HashMap<>();
                    pa.put("tipo.id", 2L);
                    ms = permisoServices.getMsgFormatoNotificacionByTipo(pa);
                    
                    this.liquidacion.setComprador(ht.getSolicitante());
                    this.liquidacion.setObservacion(ht.getNombrePropietario());
                    this.liquidacion.setTipoLiquidacion(tipoLiq);
                    this.liquidacion.setAnio(Utils.getAnio(new Date()));
                    this.liquidacion = servicesRentas.guardarLiquidacionYRubros(this.liquidacion, rubrosAGuardar, tipoLiq, null, null, ht, null);
                    
                    if (this.liquidacion.getNumLiquidacion()!= null) {
                        //ht.setNumLiquidacion(new BigInteger(this.liquidacion.getNumLiquidacion().toString()));
                        if (aprobar) {
                            params.put("to", this.getCorreosByCatEnte(ht.getSolicitante()));
                            params.put("subject", "La Liquidacion de " + ht.getTipoTramite().getDescripcion() + " del tramite # " + ht.getId() + " esta realizada");
                            params.put("message", ms.getHeader() + mensaje + ms.getFooter());
                        }
                    } else {
                        if (!aprobar) {

                            params.put("to", correoTecn.getEmail());
                            params.put("subject", "EL trámite " + ht.getId() + " esta mal generado");
                            params.put("message", (ms.getHeader() + "<br/><br/>"
                                    + "<h1/> Liquidación mal Generada."
                                    + "<br/><br/> "
                                    + obs.getObservacion()
                                    + "<br/><br/> "
                                    + ms.getFooter()));
                        }
                    }
                    //permisoServices.actualizarHistoricoTramites(ht);
                }
                this.completeTask(this.getTaskId(), params);
                this.continuar();

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
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

            if(ht.getNumPredio()!=null)
                predio = (CatPredio)acl.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
            

            switch (ht.getTipoTramite().getId().intValue()) {
                case 2:
                    
                    PePermiso permis = (PePermiso)acl.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{ht});
                    if(permis==null)
                        return;
                    for(RenRubrosLiquidacion temp : rubros){
                        if(temp.getPrioridad()==1){
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getImpuesto(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(permis.getImpuesto());
                        }
                        if(temp.getPrioridad()==2){
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getInspeccion(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(permis.getInspeccion());
                        }
                        if(temp.getPrioridad()==3){
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getRevision(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(permis.getRevision());
                        }
                        if(temp.getPrioridad()==4){
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getNoAdeudar(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(permis.getNoAdeudar());
                        }
                        if(temp.getPrioridad()==5){
                            rubrosAGuardar.add(new RenDetLiquidacion(permis.getLineaF(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(permis.getLineaF());
                        }
                    }

                    /*
                    numLiquidacion = permisoServices.grabaRTLiquidacionSac(16L, ht.getId(), "PERMISO_CONSTRUCCION",
                            permis.getIdPredio().getId(), c2.get(Calendar.YEAR), permis.getNumReporte().longValue(),
                            new Timestamp(permis.getFechaEmision().getTime()), ht.getValorLiquidacion(), "PER", permis.getUsuarioCreador().getUsuario(), false);
                    Long numLiq = Long.valueOf(numLiquidacion.toString());
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(16L, permis.getImpuesto(), usuarioCreador, "PER-" + this.generadorCeroALaIzquierda(numLiq), numLiq, c2.get(Calendar.YEAR), 1);
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(16L, permis.getInspeccion(), usuarioCreador, "PER-" + this.generadorCeroALaIzquierda(numLiq), numLiq, c2.get(Calendar.YEAR), 2);
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(16L, permis.getRevision(), usuarioCreador, "PER-" + this.generadorCeroALaIzquierda(numLiq), numLiq, c2.get(Calendar.YEAR), 3);
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(16L, permis.getNoAdeudar(), usuarioCreador, "PER-" + this.generadorCeroALaIzquierda(numLiq), numLiq, c2.get(Calendar.YEAR), 4);
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(16L, permis.getLineaF(), usuarioCreador, "PER-" + this.generadorCeroALaIzquierda(numLiq), numLiq, c2.get(Calendar.YEAR), 5);
                    liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));*/
                    break;
                case 9:
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                    
                    if(proceso!=null)
                        c2.setTime(proceso.getFecCre());
                    for(RenRubrosLiquidacion temp : rubros){
                        if(temp.getPrioridad()==1){
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    /*
                    numLiquidacion = permisoServices.grabaRTLiquidacionSac(119L, ht.getId(), "PROPIEDAD HORIZONTAL",
                            proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                            new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "PRH", usuarioCreador, false);
                    liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));*/
                    break;

                case 4: // Inspección Final
                    
                    if (ht.getPeInspeccionFinal() != null) {
                        inspeccion = ht.getPeInspeccionFinal();
                    } else {
                        System.out.println("Error al generar la liquidación");
                        return;
                    }
                    
                    for(RenRubrosLiquidacion temp : rubros){
                        if(temp.getPrioridad().compareTo(1L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getImpuesto(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(inspeccion.getImpuesto());
                        }
                        if(temp.getPrioridad().compareTo(2L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getInspeccion(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(inspeccion.getInspeccion());
                        }
                        if(temp.getPrioridad().compareTo(3L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getNoAdeudar(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(inspeccion.getNoAdeudar());
                        }
                        if(temp.getPrioridad().compareTo(4L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(inspeccion.getRevicion(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(inspeccion.getRevicion());
                        }
                        if(temp.getPrioridad().compareTo(5L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(new BigDecimal(0.00), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(new BigDecimal(0.00));
                        }
                    }
                        /*
                        numLiquidacion = permisoServices.grabaRTLiquidacionSac(27L, ht.getId(), "INSPECCION INICIAL-FINAL",
                                proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                                new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "INS", usuarioCreador, false);
                        if (inspeccion != null) {
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(27L, inspeccion.getImpuesto(), usuarioCreador, "INS-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(27L, inspeccion.getInspeccion(), usuarioCreador, "INS-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 2);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(27L, inspeccion.getNoAdeudar(), usuarioCreador, "INS-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 4);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(27L, inspeccion.getRevicion(), usuarioCreador, "INS-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 3);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(27L, new BigDecimal(0.00), usuarioCreador, "INS-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 5);
                        }*/
                    
                    //liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));

                    break;

                case 6: //Permisos Adicionales
                    
                    PePermisosAdicionales ppa;
                    ppa = (PePermisosAdicionales) acl.find(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                    if (ppa == null) {
                        System.out.println("Error al generar la liquidación");
                        return;
                    }
                    for(RenRubrosLiquidacion temp : rubros){
                        if(temp.getPrioridad().compareTo(1L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getImpMunicipalAreaedif(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ppa.getImpMunicipalAreaedif());
                        }
                        if(temp.getPrioridad().compareTo(2L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getInspeccion(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(ppa.getInspeccion());
                        }
                        if(temp.getPrioridad().compareTo(3L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getNoAdeudarMun(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(ppa.getNoAdeudarMun());
                        }
                        if(temp.getPrioridad().compareTo(4L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getRevisionAprobPlanos(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(ppa.getRevisionAprobPlanos());
                        }
                        if(temp.getPrioridad().compareTo(5L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ppa.getLineaFabricaCalculada(), temp.getId(), temp.getDescripcion())); 
                            temp.setValor(ppa.getLineaFabricaCalculada());
                        }
                    }
                        /*
                        numLiquidacion = permisoServices.grabaRTLiquidacionSac(29L, ht.getId(), "OBRA_MENOR",
                                proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                                new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "OBM", usuarioCreador, false);

                        if (ppa != null) {
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(29L, ppa.getImpMunicipalAreaedif(), usuarioCreador, "OBM-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(29L, ppa.getInspeccion(), usuarioCreador, "OBM-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 2);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(29L, ppa.getNoAdeudarMun(), usuarioCreador, "OBM-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 4);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(29L, ppa.getRevisionAprobPlanos(), usuarioCreador, "OBM-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 3);
                            permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(29L, ppa.getLineaFabricaCalculada(), usuarioCreador, "OBM-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 5);
                        }*/
                    
                    //liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));

                    break;

                case 7: // División de Predio
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                    
                    if (proceso != null) {
                        for(RenRubrosLiquidacion temp : rubros){
                            if(temp.getPrioridad().compareTo(1L)==0){
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                        /*
                        numLiquidacion = permisoServices.grabaRTLiquidacionSac(120L, ht.getId(), "DIVISION DE SOLAR",
                                proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                                new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "DSL", usuarioCreador, false);

                        permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(120L, ht.getValorLiquidacion(), usuarioCreador, "DSL-" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);
                        */
                    }
                    //liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));

                    break;
                case 8://FUSION DE PREDIOS
                    
                    for(RenRubrosLiquidacion temp : rubros){
                            if(temp.getPrioridad().compareTo(1L)==0){
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                    /*
                    numLiquidacion = permisoServices.grabaRTLiquidacionSac(121L, ht.getId(), "FUSION DE SOLAR",
                            proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                            new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "FNS", usuarioCreador, false);
                    liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));
                    */
                    break;
                case 36://RESELLADO DE PLANOS
                    
                    for(RenRubrosLiquidacion temp : rubros){
                        if(temp.getPrioridad().compareTo(1L)==0){
                            rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                            temp.setValor(ht.getValorLiquidacion());
                        }
                    }
                    /*
                    numLiquidacion = permisoServices.grabaRTLiquidacionSac(13L, ht.getId(), "RESELLADO DE PLANO",
                            proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                            new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "RPL", usuarioCreador, false);
                    liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));
                    */
                    break;

                case 14: // Otros Trámites
                    if(ht.getValorLiquidacion()!=null){
                        proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                        numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                        for(RenRubrosLiquidacion temp : rubros){
                            if(temp.getPrioridad().compareTo(1L)==0){
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                        /*
                        numLiquidacion = permisoServices.grabaRTLiquidacionSac(122L, ht.getId(), "LINEA DE FABRICA",
                                proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                                new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "LNF", usuarioCreador, false);
                        permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(122L, ht.getValorLiquidacion(), usuarioCreador, "LNF  -" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);
                        liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));
                        */
                    }
                    break;

                case 43: // Otros Trámites Solo liquidación
                    if(ht.getValorLiquidacion()!=null){
                        proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                        numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                        for(RenRubrosLiquidacion temp : rubros){
                            if(temp.getPrioridad().compareTo(1L)==0){
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                        /*
                        numLiquidacion = permisoServices.grabaRTLiquidacionSac(122L, ht.getId(), "LINEA DE FABRICA",
                                proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                                new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "LNF", usuarioCreador, false);
                        permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(122L, ht.getValorLiquidacion(), usuarioCreador, "LNF  -" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);
                        liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));
                        */
                    }
                    break;

                case 44: // Otros Trámites liquidación y certificado
                    proceso = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());
                    numReporte = permisoServices.getHistoricoTramiteDet(ht.getIdProceso(), Boolean.TRUE);
                    for(RenRubrosLiquidacion temp : rubros){
                            if(temp.getPrioridad().compareTo(1L)==0){
                                rubrosAGuardar.add(new RenDetLiquidacion(ht.getValorLiquidacion(), temp.getId(), temp.getDescripcion()));
                                temp.setValor(ht.getValorLiquidacion());
                            }
                        }
                    /*
                    numLiquidacion = permisoServices.grabaRTLiquidacionSac(122L, ht.getId(), "LINEA DE FABRICA",
                            proceso.getPredio().getId(), c2.get(Calendar.YEAR), (numReporte.getSecuencia() != null ? numReporte.getSecuencia().longValue() : null),
                            new Timestamp(proceso.getFecCre().getTime()), ht.getValorLiquidacion(), "LNF", usuarioCreador, false);
                    permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(122L, ht.getValorLiquidacion(), usuarioCreador, "LNF -" + this.generadorCeroALaIzquierda(Long.valueOf(numLiquidacion.toString())), Long.valueOf(numLiquidacion.toString()), c2.get(Calendar.YEAR), 1);

                    liquidacion.setNumLiquidacion(BigInteger.valueOf(Long.valueOf(numLiquidacion+"")));
                            */
                    break;
            }

            //servicesRentas.guardarLiquidacionYRubros(liquidacion, rubrosAGuardar);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
        

    }
    
    public void eliminarRubro(RenRubrosLiquidacion rubro){
        this.rubros.remove(rubro);
    }

    public String generadorCeroALaIzquierda(Long n) {
        int cont = 0;
        Long num = n;
        String salida = "";
        while (num > 0) {
            num = num / 10;
            cont++;
        }
        for (int i = 0; i < 6 - cont; i++) {
            salida = salida + "0";
        }
        salida = salida + n;
        return salida;
    }

    public List<RenRubrosLiquidacion> getRubros() {
        return rubros;
    }

    public void setRubros(List<RenRubrosLiquidacion> rubros) {
        this.rubros = rubros;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Boolean getPagoRealizado() {
        return pagoRealizado;
    }

    public void setPagoRealizado(Boolean pagoRealizado) {
        this.pagoRealizado = pagoRealizado;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public List<RenDetLiquidacion> getRubrosAGuardar() {
        return rubrosAGuardar;
    }

    public void setRubrosAGuardar(List<RenDetLiquidacion> rubrosAGuardar) {
        this.rubrosAGuardar = rubrosAGuardar;
    }

}
