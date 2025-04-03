package com.origami.sgm.bpm.managedbeans;

import com.origami.sgm.bpm.models.DetalleProceso;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.lazymodels.bpm.ProcessInstanceLazy;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class ProcessAdministration extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVerisonUID = 1L;

    @javax.inject.Inject
    private PropiedadHorizontalServices servicio;

    private String process = null;
    private boolean showDetail = false;
//    private List<DetalleProceso> details;
    private ProcessInstanceLazy details;
    List<HistoricTaskInstance> listTareas;
    private int prioridad;
    private HistoricoTramites historicoTramites;
    private List<Attachment> documentos;
    private LazyDataModel<GeDepartamento> departamentosLazy;
    private HistoricTaskInstance tareaSeleccionada;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    private void iniView() {
        try {
            process = new String();
            departamentosLazy = new BaseLazyDataModel<>(GeDepartamento.class, "nombre");
            //details = this.getInstanciaProcesos();
            details = new ProcessInstanceLazy(false);
        } catch (Exception e) {
            Logger.getLogger(ProcessManagement.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void load() {
        try {
            if (process != null) {
                //details = this.getInstanciaProcesos();
                details = new ProcessInstanceLazy(false);
                showDetail = !details.isRowAvailable();
            } else {
                showDetail = false;
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessManagement.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("frmProcessAdmin:tdatos");
    }

    public void onRowToggle(ToggleEvent event) {
        try {
            if (event.getData() != null) {
                listTareas = ((DetalleProceso) event.getData()).getTasks();
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessManagement.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void listaTareas(DetalleProceso dp) {
        historicoTramites = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{new Long(dp.getIdProceso())});
        //listTareas = dp.getTasks();
        listTareas = this.getTaskByProcessInstanceIdMain(historicoTramites.getIdProcesoTemp());
    }

    public void listarVariables(DetalleProceso dp) {
        historicoTramites = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{new Long(dp.getIdProceso())});
    }

    public void selectPrioridad(DetalleProceso dp) {
        historicoTramites = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{new Long(dp.getIdProceso())});
        prioridad = (int) this.getVariableByPorcessIntance(historicoTramites.getIdProceso(), "prioridad");
    }

    public void actualizarPrioridad() {
        try {
            List<String> listadoIdsProcessInstace = this.obtenerProcessInstanceByProcessInstaceIdMain(historicoTramites.getIdProceso());
            for (String listadoIdsProcessInstace1 : listadoIdsProcessInstace) {
                this.setVariableByProcessInstance(listadoIdsProcessInstace1, "prioridad", prioridad);
                List<Task> tareasActivas = this.obtenerTareasActivasProcessInstance(listadoIdsProcessInstace1);
                this.asignarTareaPriority(tareasActivas, prioridad);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessAdministration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectObservaciones(DetalleProceso dp) {
        historicoTramites = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{new Long(dp.getIdProceso())});
    }

    public void selectDocumentos(DetalleProceso dp) {
        historicoTramites = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{new Long(dp.getIdProceso())});
        documentos = this.getProcessInstanceAllAttachmentsFiles(historicoTramites.getIdProceso());
    }

    public void seleccionarTareaReasignacion(HistoricTaskInstance ta) {
        tareaSeleccionada = ta;
    }

    public void reasignarTarea(AclUser usuario) {
        try {
            servicio.guardarObservaciones(historicoTramites, session.getName_user(), "TAREA: " + tareaSeleccionada.getName() + "USUARIO ANTERIOR: " + tareaSeleccionada.getAssignee() + ". USUARIO ACTUAL: " + usuario.getUsuario(), "REASIGNACION DE USUARIO");
            this.reasignarTarea(tareaSeleccionada.getId(), usuario.getUsuario());
            Map<String, Object> v = this.engine.getvariables(tareaSeleccionada.getProcessInstanceId());
            for (Map.Entry<String, Object> entrySet : v.entrySet()) {
                if (entrySet.getValue() != null && entrySet.getValue().equals(tareaSeleccionada.getAssignee())) {
                    this.setVariableByProcessInstance(tareaSeleccionada.getProcessInstanceId(), entrySet.getKey(), usuario.getUsuario());
                    break;
                }
            }
            listTareas = this.getTaskByProcessInstanceIdMain(historicoTramites.getIdProcesoTemp());
        } catch (Exception ex) {
            Logger.getLogger(ProcessAdministration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public ProcessInstanceLazy getDetails() {
        return details;
    }

    public void setDetails(ProcessInstanceLazy details) {
        this.details = details;
    }

    public List<HistoricTaskInstance> getListTareas() {
        return listTareas;
    }

    public void setListTareas(List<HistoricTaskInstance> listTareas) {
        this.listTareas = listTareas;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public HistoricoTramites getHistoricoTramites() {
        return historicoTramites;
    }

    public void setHistoricoTramites(HistoricoTramites historicoTramites) {
        this.historicoTramites = historicoTramites;
    }

    public List<Attachment> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Attachment> documentos) {
        this.documentos = documentos;
    }

    public LazyDataModel<GeDepartamento> getDepartamentosLazy() {
        return departamentosLazy;
    }

    public void setDepartamentosLazy(LazyDataModel<GeDepartamento> departamentosLazy) {
        this.departamentosLazy = departamentosLazy;
    }

    public HistoricTaskInstance getTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public void setTareaSeleccionada(HistoricTaskInstance tareaSeleccionada) {
        this.tareaSeleccionada = tareaSeleccionada;
    }

}
