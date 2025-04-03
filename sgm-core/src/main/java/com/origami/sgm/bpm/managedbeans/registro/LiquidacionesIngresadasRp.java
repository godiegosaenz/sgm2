/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RegpLiquidacionDetalles;
import com.origami.sgm.lazymodels.RegpLiquidacionesRegistroLazy;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class LiquidacionesIngresadasRp extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private ServletSession servletSession;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected RegpLiquidacionesRegistroLazy liquidacionesLazy;
    protected HistoricoTramites ht = new HistoricoTramites();
    protected RegpLiquidacionDerechosAranceles liq = new RegpLiquidacionDerechosAranceles();
    protected Boolean sonCertificados = true;
    protected Integer cantidad = 0;
    protected Integer estadoPago = 0;
    protected HashMap<String, Object> parametros;
    protected SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");

    protected Boolean showEdit = false;
    protected Boolean showIn = false;
    protected Boolean disable = false;
    protected List<HistoricTaskInstance> tareas = new ArrayList<>();
    protected List<Attachment> listAttach = new ArrayList<>();
    protected List<RegpCertificadosInscripciones> trabajos = new ArrayList<>();

    protected HistoricTaskInstance tareaActual;
    protected List<AclUser> users = new ArrayList<>();
    protected Boolean showUsers = false;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            liquidacionesLazy = new RegpLiquidacionesRegistroLazy();
            this.validaRoles(session.getRoles());
            this.cargarUsuariosRegistro();
        } catch (Exception e) {
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validaRoles(List<Long> list) {
        // 102 id rol liquidador_registro - 9 administrador - 181 supervisor registro
        // 79 id director registro
        for (Long rol : list) {
            if (rol == 102) {
                showEdit = true;
                return;
            }
            if (rol == 181 || rol == 79 || rol == 9) {
                showEdit = true;
                showIn = true;
                return;
            }
        }
    }

    public void cargarUsuariosRegistro() {
        try {
            users = reg.getUsuariosByRolId(80L); // id 80 rol registro_propiedad
            List<AclUser> temp = reg.getUsuariosByRolId(100L); // id 100 rol legal_registro_propiedad
            for (AclUser u : temp) {
                users.add(u);
            }
            users.size();
        } catch (Exception e) {
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void dlgImprimirReport(RegpLiquidacionDerechosAranceles regpLiq) {
        ht = regpLiq.getHistoricTramite();
        liq = regpLiq;
        JsfUti.update("formImprimir");
        JsfUti.executeJS("PF('dlgImprimir').show();");
    }

    public void imprimirTicket() {
        this.cargarDatos(ht.getId());
        JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
    }

    public void cargarDatos(Long id) {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("tramiteRegistroPropiedadMercantil");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("NUMTRAMITE", id);
            servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
        } catch (Exception e) {
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirPreforma() {
        JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/PreformaIngreso?codigo=" + ht.getId());
    }

    public void imprimirAcuerdo() {
        if (liq.getCertificado()) {
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/SolicitudIngreso?codigo=" + ht.getId());
        }
        if (liq.getInscripcion()) {
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/AcuerdoIngreso?codigo=" + ht.getId());
        }
    }

    public void visualizarDatos(RegpLiquidacionDerechosAranceles regpLiq) {
        try {
            ht = reg.getHistoricoTramiteById(regpLiq.getHistoricTramite().getId());
            liq = regpLiq;
            if (ht.getIdProceso() == null) {
                tareas = new ArrayList<>();
                listAttach = new ArrayList<>();
            } else {
                tareas = this.getTaskByProcessInstanceIdMain(ht.getIdProceso());
                if (!tareas.isEmpty()) {
                    if (tareas.get(0).getEndTime() != null) {
                        JsfUti.messageInfo(null, "TRAMITE FINALIZADO", "El tramite " + liq.getNumTramiteRp() + " ha Finaliado su proceso.");
                    }
                }
                listAttach = this.getProcessInstanceAllAttachmentsFiles(ht.getIdProceso());
            }

            //estadoPago = reg.getEstadoPagoByLiquidacion(r.getId());
            estadoPago = reg.getEstadoPagoLiquidacionSgm(liq.getId());

            trabajos = acl.findAll(Querys.getTodasTareasByLiq, new String[]{"idLiq"}, new Object[]{liq.getId()});
            if (trabajos == null) {
                trabajos = new ArrayList<>();
            }
            JsfUti.update("formInformLiq");
            JsfUti.executeJS("PF('dlgVerInfoRp').show();");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String usuariosCandidatos(String idTask) {
        String candidatosAsignados = "";
        if (idTask != null) {
            for (IdentityLink identityLink : this.obtenerIdentityLinkByIdTask(idTask)) {
                candidatosAsignados = candidatosAsignados + identityLink.getUserId() + ", ";
            }
            return candidatosAsignados.substring(1, candidatosAsignados.length() - 2);
        }
        return "";
    }

    public void editarLiquidacion(RegpLiquidacionDerechosAranceles r) {
        if (r.getEstado() == 1) {
            //estadoPago = reg.getEstadoPagoByLiquidacion(r.getId());
            estadoPago = reg.getEstadoPagoLiquidacionSgm(r.getId());
            switch (estadoPago) {
                case 1: // PAGADO
                    JsfUti.messageError(null, "No se puede editar", "Liquidacion ya fue cancelada");
                    break;
                case 2: // POR PAGAR
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/iniciarTramiteRP.xhtml?state=edit&code=" + r.getId());
                    break;
                case 3: // INACTIVO
                    JsfUti.messageError(null, "No se puede editar", "Liquidacion inactiva, consulte a sistemas");
                    break;
                case 4: // EXONERADO
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/iniciarTramiteRP.xhtml?state=edit&code=" + r.getId());
                    break;
                case 5: // ERROR
                    JsfUti.messageError(null, Messages.error, "");
                    break;
            }
        } else {
            JsfUti.messageInfo(null, "El tramite ya fue Ingresado y no se puede Editar. Estado de la liquidacion: " + r.getEstado(), "");
        }
    }

    public void showDlgIngresoTramite(RegpLiquidacionDerechosAranceles regpLiq) {
        if (regpLiq.getEstado() == 1) {
            liq = regpLiq;
            ht = regpLiq.getHistoricTramite();
            //estadoPago = reg.getEstadoPagoByLiquidacion(regpLiq.getId());
            estadoPago = reg.getEstadoPagoLiquidacionSgm(regpLiq.getId());
            if (this.reliquidacion()) {
                switch (estadoPago) {
                    case 1: // PAGADO
                        JsfUti.update("formConfirmIngreso");
                        JsfUti.executeJS("PF('dlgIngreso').show();");
                        break;
                    case 2: // POR PAGAR
                        JsfUti.messageError(null, "No se puede ingresar", "Liquidacion no se ha cancelado");
                        break;
                    case 3: // INACTIVO
                        JsfUti.messageError(null, "No se puede ingresar", "Liquidacion inactiva, consulte a sistemas");
                        break;
                    case 4: // EXONERADO
                        JsfUti.update("formConfirmIngreso");
                        JsfUti.executeJS("PF('dlgIngreso').show();");
                        break;
                    case 5: // ERROR
                        JsfUti.messageError(null, Messages.error, "");
                        break;
                }
            } else {
                JsfUti.messageInfo(null, "No se puede ingresar tramite, la liquidacion es Diferencia de Pago.", "");
            }
        } else {
            JsfUti.messageInfo(null, "El tramite ya fue ingresado. Estado de la liquidacion: " + regpLiq.getEstado(), "");
        }
    }

    public void showDlgReAsignarUser(RegpLiquidacionDerechosAranceles regpLiq) {
        try {
            showUsers = true;
            ht = reg.getHistoricoTramiteById(regpLiq.getHistoricTramite().getId());
            if (ht.getIdProceso() == null) {
                JsfUti.messageError(null, "El tramite no ha sido Ingresado. No hay tarea para re-asignar.", "");
            } else {
                tareas = this.getTaskByProcessInstanceIdMain(ht.getIdProceso());
                if (tareas.size() > 0) {
                    tareaActual = tareas.get(0);
                    if (tareaActual.getEndTime() == null) {
                        if (tareaActual.getAssignee() == null) {
                            showUsers = false;
                            JsfUti.messageError(null, "Esta tarea no se puede re-asignar. Tiene usuarios candidatos.", "");
                        }
                        JsfUti.update("formreasignar");
                        JsfUti.executeJS("PF('dlgReasignar').show();");
                    } else {
                        JsfUti.messageError(null, "No se puede re asignar Tarea. Tramite Finalizado.", "");
                    }
                } else {
                    JsfUti.messageError(null, "No se encontro tarea para reasignar.", "");
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reasignarTarea(AclUser user) {
        try {
            String obs = "TAREA: " + tareaActual.getName() + ", USUARIO ANTERIOR: " + tareaActual.getAssignee() + ", USUARIO ACTUAL: " + user.getUsuario();
            reg.guardarObservaciones(ht, session.getName_user(), obs, "REASIGNACION DE USUARIO");
            this.reasignarTarea(tareaActual.getId(), user.getUsuario());
            Map<String, Object> v = this.engine.getvariables(tareaActual.getProcessInstanceId());
            for (Map.Entry<String, Object> entrySet : v.entrySet()) {
                if (entrySet.getValue() != null && entrySet.getValue().equals(tareaActual.getAssignee())) {
                    this.setVariableByProcessInstance(tareaActual.getProcessInstanceId(), entrySet.getKey(), user.getUsuario());
                    break;
                }
            }
            JsfUti.executeJS("PF('dlgReasignar').hide();");
            JsfUti.update("mainForm:dtLiquidaciones");
            JsfUti.messageInfo(null, "Tarea Re-Asiganada con exito.", "");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void ingresarTramite() {
        try {
            disable = true;
            JsfUti.update("formConfirmIngreso");
            this.llenarVariablesActiviti();
            MsgFormatoNotificacion msg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{2L});
            if (msg != null) {
                String mensaje = msg.getHeader() + "Usted inicio un tramite en el REGISTRO DE LA PROPIEDAD.<br>"
                        + "El numero de tramite es: " + liq.getNumTramiteRp() + ". Fecha de ingreso del tramite: " + ft.format(new Date())
                        + msg.getFooter();

                parametros.put("to", ht.getCorreos());
                parametros.put("subject", ht.getTipoTramiteNombre());
                parametros.put("message", mensaje);

                ProcessInstance p = this.startProcessByDefinitionKey(ht.getTipoTramite().getActivitykey(), parametros);
                if (p != null) {
                    this.setVariableByProcessInstance(p.getId(), "subCarpeta", ht.getId() + "-" + p.getId());
                    ht.setCarpetaRep(ht.getId() + "-" + p.getId());
                    ht.setIdProceso(p.getId());
                    ht.setIdProcesoTemp(p.getId());
                    liq.setEstado(2);//tramite ingresado
                    liq.setHistoricTramite(ht);
                    reg.iniciarTramiteRegistro(liq, session.getName_user(), "Recepcion de Documentos.");
                    //JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
                    JsfUti.update("mainForm:dtLiquidaciones");
                    JsfUti.executeJS("PF('dlgIngreso').hide();");
                    disable = false;
                } else {
                    JsfUti.messageError(null, Messages.error, "");
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(LiquidacionesIngresadasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean reliquidacion() {
        Boolean flag = true;
        sonCertificados = true;
        cantidad = liq.getRegpLiquidacionDetallesCollection().size();
        for (RegpLiquidacionDetalles d : liq.getRegpLiquidacionDetallesCollection()) {
            if (d.getActo().getReliquidacion()) {
                flag = false;
            }
            if (d.getActo().getTipoActo().getId() == 1L || d.getActo().getTipoActo().getId() == 3L) {
                sonCertificados = false;
            }
        }
        return flag;
    }

    public void llenarVariablesActiviti() {
        parametros = new HashMap<>();
        parametros.put("carpeta", ht.getTipoTramite().getCarpeta());
        parametros.put("listaArchivos", new ArrayList<>());
        parametros.put("listaArchivosFinal", new ArrayList<>());
        parametros.put("prioridad", 50);
        parametros.put("tramite", ht.getId());
        parametros.put("entregado", Boolean.FALSE);
        parametros.put("director", reg.getUsuarioDirector().getUsuario());
        parametros.put("supervisor", reg.getUsuarioSupervisor().getUsuario());
        parametros.put("digitalizador", reg.getUsuarioDigitalizador().getUsuario());
        parametros.put("listaLiquidadores", reg.getLiquidadoresRegistro());
        parametros.put("cantidad", cantidad);
        parametros.put("devolutiva", Boolean.FALSE);
        if (ht.getTipoTramite().getAsignacionManual()) {
            parametros.put("tecnico", "");
            parametros.put("abogado", "");
            parametros.put("iniciarTramite", 1);
            if (sonCertificados) {
                parametros.put("asignado", 1);
            } else if (ht.getTipoTramite().getTieneDigitalizacion()) {
                parametros.put("asignado", 3);
            } else if (!ht.getTipoTramite().getTieneDigitalizacion()) {
                parametros.put("asignado", 2);
            }
        } else {
            parametros.put("tecnico", reg.getTecnicoRegistro(cantidad));
            if (sonCertificados) {
                parametros.put("iniciarTramite", 3);
            } else {
                parametros.put("abogado", reg.getAbogadoRegistro(cantidad));
                parametros.put("iniciarTramite", 2);
            }
        }
    }

    public Boolean getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(Boolean showEdit) {
        this.showEdit = showEdit;
    }

    public Boolean getShowIn() {
        return showIn;
    }

    public void setShowIn(Boolean showIn) {
        this.showIn = showIn;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RegpLiquidacionDerechosAranceles getLiq() {
        return liq;
    }

    public void setLiq(RegpLiquidacionDerechosAranceles liq) {
        this.liq = liq;
    }

    public RegpLiquidacionesRegistroLazy getLiquidacionesLazy() {
        return liquidacionesLazy;
    }

    public void setLiquidacionesLazy(RegpLiquidacionesRegistroLazy liquidacionesLazy) {
        this.liquidacionesLazy = liquidacionesLazy;
    }

    public List<HistoricTaskInstance> getTareas() {
        return tareas;
    }

    public void setTareas(List<HistoricTaskInstance> tareas) {
        this.tareas = tareas;
    }

    public List<Attachment> getListAttach() {
        return listAttach;
    }

    public void setListAttach(List<Attachment> listAttach) {
        this.listAttach = listAttach;
    }

    public Integer getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(Integer estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public HistoricTaskInstance getTareaActual() {
        return tareaActual;
    }

    public void setTareaActual(HistoricTaskInstance tareaActual) {
        this.tareaActual = tareaActual;
    }

    public List<AclUser> getUsers() {
        return users;
    }

    public void setUsers(List<AclUser> users) {
        this.users = users;
    }

    public Boolean getShowUsers() {
        return showUsers;
    }

    public void setShowUsers(Boolean showUsers) {
        this.showUsers = showUsers;
    }

    public List<RegpCertificadosInscripciones> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(List<RegpCertificadosInscripciones> trabajos) {
        this.trabajos = trabajos;
    }

}
