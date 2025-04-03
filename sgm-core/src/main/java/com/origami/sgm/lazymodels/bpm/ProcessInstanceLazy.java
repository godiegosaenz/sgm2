/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels.bpm;

import com.origami.sgm.bpm.models.DetalleProceso;
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
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author CarlosLoorVargas
 */
public class ProcessInstanceLazy extends LazyDataModel<DetalleProceso> {

    private static final long serialVersionUID = 1L;
    protected BpmBaseEngine engine;
    private DetalleProceso det;
    private List<DetalleProceso> ldet, ltemp;
    private List<HistoricProcessInstance> lhpi;
    private boolean state = false;
    private ProcessDefinition p;
    private ProcessInstance pi;
    private HistoricoTramites ht;
    private Entitymanager manager;
    private int maxRows = 0, x = 0;
    private HistoryService hs;
    private RuntimeService rs;
    private RepositoryService res;

    public ProcessInstanceLazy() {
        ldet = new ArrayList<>();
        ltemp = new ArrayList<>();
        engine = EjbsCaller.getEngine();
        manager = EjbsCaller.getTransactionManager();
        maxRows = engine.getProcessInstanceHistoric().size();
    }

    public ProcessInstanceLazy(boolean state) {
        ldet = new ArrayList<>();
        ltemp = new ArrayList<>();
        engine = EjbsCaller.getEngine();
        manager = EjbsCaller.getTransactionManager();
        this.state = state;
        maxRows = engine.getProcessInstanceHistoric().size();
    }

    @Override
    public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
            return getDefaultData(first, pageSize, filters);
        } catch (Exception e) {
            Logger.getLogger(ProcessInstanceLazy.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    private List getDefaultData(int first, int pageSize, Map<String, Object> filters) {
        try {
            ldet = new ArrayList<>();
            ltemp = new ArrayList<>();
            hs = engine.getProcessEngine().getHistoryService();
            rs = engine.getProcessEngine().getRuntimeService();
            res = engine.getProcessEngine().getRepositoryService();
            if (filters.isEmpty()) {
                x = 0;
                ltemp = new ArrayList<>();
                this.setRowCount(maxRows);
                lhpi = engine.getProcessInstanceHistoric(first, pageSize, state);
            } else {
                if (filters.containsKey("idProceso")) {
                    lhpi = hs.createHistoricProcessInstanceQuery().processInstanceId(getProceso(Long.parseLong(filters.get("idProceso").toString().trim())).getIdProceso()).unfinished().orderByProcessInstanceStartTime().desc().listPage(first, pageSize);
                }
                if (filters.containsKey("instancia")) {
                    lhpi = hs.createHistoricProcessInstanceQuery().processInstanceId(filters.get("instancia").toString().trim()).listPage(first, pageSize);
                }
                this.setRowCount(lhpi.size());
            }
            for (HistoricProcessInstance hpi : lhpi) {
                p = engine.getProcessDataByDefID(hpi.getProcessDefinitionId());
                pi = engine.getProcessInstanceById(hpi.getId());
                det = new DetalleProceso();
                ht = (HistoricoTramites) manager.find(Querys.getHistoricProceduresByProcId, new String[]{"idprocess"}, new Object[]{hpi.getId()});
                if (ht == null) {
                    ht = (HistoricoTramites) manager.find(Querys.getHistoricProceduresByProcIdTemp, new String[]{"idprocess"}, new Object[]{hpi.getId()});
                }
                if (ht != null) {
                    if (p.getName() != null) {
                        det.setNombreProceso(p.getName() + " (" + ht.getId() + ")");
                    } else {
                        det.setNombreProceso(p.getKey() + " (" + ht.getId() + ")");
                    }
                    if (pi != null) {
                        det.setInstancia(pi.getId());
                    }
                    if (ht.getId() != null) {
                        det.setIdProceso(ht.getId().toString());
                    }
                    det.setFechaInicio(hpi.getStartTime());
                    det.setFechaFin(hpi.getEndTime());
                    det.setTasks(engine.getTaskByProcessInstanceId(hpi.getId()));
                    ldet.add(det);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessInstanceLazy.class.getName()).log(Level.SEVERE, null, e);
        }
        return ldet;
    }

    public HistoricoTramites getProceso(Long id) {
        ht = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{id});
        return ht;
    }
}
