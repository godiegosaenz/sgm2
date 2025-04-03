/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels.bpm;

import com.origami.sgm.bpm.models.TareaWF;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.sgm.util.EjbsCaller;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Anyelo
 */
public class TareasWFLazy extends LazyDataModel<TareaWF> {

    private static final long serialVersionUID = 1L;

    private BpmBaseEngine engine;
    private Entitymanager manager;
    private HistoricProcessInstance hpi;
    private HistoricoTramites ht;
    private int maxRows = 0;
    private String usuario = "admin";
    private List<Task> tasks;
    private List<TareaWF> tareas;

    public TareasWFLazy() {
        engine = EjbsCaller.getEngine();
        manager = EjbsCaller.getTransactionManager();
        maxRows = engine.getNumberTasksUser(usuario);
        tasks = new ArrayList<>();
    }

    public TareasWFLazy(String user) {
        this.usuario = user;
        engine = EjbsCaller.getEngine();
        manager = EjbsCaller.getTransactionManager();
        maxRows = engine.getNumberTasksUser(user);
        tasks = new ArrayList<>();
    }

    @Override
    public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
            return getDefaultData(first, pageSize, filters);
        } catch (Exception e) {
            Logger.getLogger(TareasWFLazy.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    private List getDefaultData(int first, int pageSize, Map<String, Object> filters) {
        try {
            tasks = new ArrayList<>();
            tareas = new ArrayList<>();
            List<Task> temp;
            List<String> hijos;
            if (filters.isEmpty()) {
                this.setRowCount(maxRows);
                tasks = engine.getAllTasksUser(usuario, first, pageSize);
            } else {
                if (filters.containsKey("idTramite")) {
                    String idProcess = (String) manager.find("Select ht.idProceso from HistoricoTramites ht where ht.id = :id", new String[]{"id"}, new Object[]{new Long(filters.get("idTramite").toString())});
                    if (idProcess == null) {
                        idProcess = (String) manager.find("Select ht.idProcesoTemp from HistoricoTramites ht where ht.id = :id", new String[]{"id"}, new Object[]{new Long(filters.get("idTramite").toString())});
                    }
                    if (idProcess != null) {
                        tasks = engine.getTasksUserProcessId(usuario, idProcess);
                        if (tasks.isEmpty()) {
                            hijos = engine.getListProcessInstanceIdsByProcessInstanceIdMain(idProcess);
                            for (String s : hijos) {
                                tasks = engine.getTasksUserProcessId(usuario, s);
                                if (!tasks.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (filters.containsKey("numTramiteXdep")
                        || filters.containsKey("carpetaRep")) {
                    String idProcess = null;
                    if (filters.containsKey("carpetaRep")) {
                        if (filters.get("carpetaRep") != null || filters.get("carpetaRep").toString().trim().length() > 0) {
                            idProcess = (String) manager.find("Select ht.idProcesoTemp from HistoricoTramites ht where ht.carpetaRep like :carpetaRep ",
                                    new String[]{"carpetaRep"}, new Object[]{"%".concat(filters.get("carpetaRep").toString().toUpperCase()).concat("%")});
                        }
                    } else {
                        idProcess = (String) manager.find("Select ht.idProcesoTemp from HistoricoTramites ht where ht.numTramiteXDepartamento = :numTramite and ht.tipoTramite.id = 5", new String[]{"numTramite"}, new Object[]{new Long(filters.get("numTramiteXdep").toString())});
                    }
                    if (idProcess != null) {
                        tasks = engine.getTasksUserProcessId(usuario, idProcess);
                        if (tasks.isEmpty()) {
                            hijos = engine.getListProcessInstanceIdsByProcessInstanceIdMain(idProcess);
                            for (String s : hijos) {
                                tasks = engine.getTasksUserProcessId(usuario, s);
                                if (!tasks.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                }
                this.setRowCount(tasks.size());
            }
            for (Task task : tasks) {
                hpi = engine.getHistoricProcessInstanceByInstanceID(task.getProcessInstanceId());
                if (hpi != null && hpi.getSuperProcessInstanceId() != null) {
                    ht = this.getTramiteByIdProcess(hpi.getSuperProcessInstanceId());
                    if (ht == null) {
                        ht = this.getTramiteByIdProcessTemp(hpi.getSuperProcessInstanceId());
                        if (ht != null && ht.getIdProceso() == null) {
                            ht.setIdProceso(hpi.getId());
                            ht.setCarpetaRep(ht.getId() + "-" + hpi.getId());
                            manager.persist(ht);
                        }
                    }
                } else {
                    ht = this.getTramiteByIdProcess(task.getProcessInstanceId());
                    if (ht == null) {
                        ht = this.getTramiteByIdProcessTemp(task.getProcessInstanceId());
                    }
                }
                if (ht != null) {
                    TareaWF t = new TareaWF();
                    t.setIdTramite(ht.getId());
                    t.setNumTramiteXdep(ht.getNumTramiteXDepartamento());
                    t.setTarea(task);
                    if (task.getDescription() != null) {
                        if (task.getDescription().length() > 50) {
                            t.setDescripcionTareaMayor50char(true);
                        }
                    }
                    if (ht.getMz() == null) {
                        ht.setMz("0");
                    }
                    if (ht.getSolar() == null) {
                        ht.setSolar("0");
                    }
                    t.setTramite(ht);
                    tareas.add(t);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TareasWFLazy.class.getName()).log(Level.SEVERE, null, e);
        }
        return tareas;
    }

    public HistoricoTramites getTramiteByIdProcess(String processInstance) {
        return (HistoricoTramites) manager.find(Querys.getHistoricProceduresByProcId, new String[]{"idprocess"}, new Object[]{processInstance});
    }

    public HistoricoTramites getTramiteByIdProcessTemp(String processInstance) {
        return (HistoricoTramites) manager.find(Querys.getHistoricProceduresByProcIdTemp, new String[]{"idprocess"}, new Object[]{processInstance});
    }

}
