/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.bpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton(name = "bpmBaseEngine")
@ApplicationScoped
@Lock(LockType.READ)
public class BpmBaseEngineEJb implements BpmBaseEngine {

    @javax.inject.Inject
    private BpmProcessEngine engine;

    @Override
    public ProcessEngine getProcessEngine() {
        return engine.getProcessEngine();
    }

    @Override
    public String getFormKey(String processId) {
        return getProcessEngine().getFormService().getStartFormData(processId).getFormKey();
    }

    @Override
    public String getProcessKey(String processId) {
        return getProcessEngine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processId).singleResult().getKey();
    }

    @Override
    public String getFromKey(Task task) {
        String formKey;
        formKey = getProcessEngine().getFormService().getTaskFormKey(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        System.out.println(formKey);
        return formKey;
    }

    @Override
    public List<Task> getUsertasksList(String asignee, String keyTarea) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        ArrayList<Task> acttask = null;
        try {
            if (keyTarea != null) {
                acttask = (ArrayList<Task>) taskService.createTaskQuery().taskAssignee(asignee).taskDefinitionKey(keyTarea).orderByTaskPriority().desc().list();
            } else {
                acttask = (ArrayList<Task>) taskService.createTaskQuery().taskAssignee(asignee).orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
            }
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }

        return acttask;
    }

    @Override
    public List<Task> getCandidateUsertasksList(String asignee) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        ArrayList<Task> acttask = (ArrayList<Task>) taskService.createTaskQuery().taskCandidateUser(asignee).orderByTaskPriority().desc().list();
        return acttask;
    }
    
    @Override
    public List<Task> getAllTasksUser(String asignee, int first, int pageSize) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        ArrayList<Task> taskAssigne = new ArrayList<>();
        try {
            taskAssigne = (ArrayList<Task>) taskService.createTaskQuery().taskCandidateOrAssigned(asignee).orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage(first, pageSize);
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngineEJb.class.getName()).log(Level.SEVERE, null, e);
        }
        return taskAssigne;
    }
    
    @Override
    public List<Task> getTasksUserProcessId(String asignee, String processID) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        ArrayList<Task> taskAssigne = new ArrayList<>();
        try {
            taskAssigne = (ArrayList<Task>) taskService.createTaskQuery().taskCandidateOrAssigned(asignee).processInstanceId(processID).list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngineEJb.class.getName()).log(Level.SEVERE, null, e);
        }
        return taskAssigne;
    }

    @Override
    public Integer getNumberTasksUser(String asignee) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        Long cantidad = 0L;
        try {
            cantidad = taskService.createTaskQuery().taskCandidateOrAssigned(asignee).count();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngineEJb.class.getName()).log(Level.SEVERE, null, e);
        }
        return cantidad.intValue();
    }

    @Override
    public List<ProcessDefinition> getProcessDesployedList() {
        List<ProcessDefinition> list;
        if (getProcessEngine() == null) {
            list = new ArrayList<>();
            return list;
        }
        list = getProcessEngine().getRepositoryService().createProcessDefinitionQuery()
                .latestVersion().list();
        return list;
    }

    @Override
    public long getTaskCounterByProcessKey(String proccessDefKey) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        return taskService.createTaskQuery().processDefinitionKey(proccessDefKey).count();
    }

    @Override
    public long getTaskCounterByUser(String asignee) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        return taskService.createTaskQuery().taskAssignee(asignee).count();
    }

    @Override
    public boolean completeTask(String taskId, HashMap<String, Object> parameters) throws Exception {
        boolean flag;
        try {
            TaskService taskService = this.getProcessEngine().getTaskService();
            taskService.complete(taskId, parameters);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public ProcessInstance startProcessByDefinitionKey(String processDefinitionKey, HashMap<String, Object> parameters) throws Exception {
        ProcessInstance p;
        try {
            RuntimeService runtimeService = this.getProcessEngine().getRuntimeService();
            p = runtimeService.startProcessInstanceByKey(processDefinitionKey, parameters);
//            TaskQuery taskQuery = this.getProcessEngine().getTaskService().createTaskQuery().processInstanceId(processInstances.getProcessInstanceId());
//            flag = taskQuery != null;
        } catch (Exception e) {
            p = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return p;
    }

    @Override
    public Object getvariable(String taskId, String varName) {
        Object o;
        try {
            Task t = this.getProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
            o = this.getProcessEngine().getRuntimeService().getVariable(t.getProcessInstanceId(), varName);
            if (o == null) {
                o = this.getProcessEngine().getRuntimeService().getVariable(t.getExecutionId(), varName);
            }
        } catch (Exception e) {
            o = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public Object getVariableByProcessInstance(String processInstanceId, String varName) {
        Object o;
        try {
            o = this.getProcessEngine().getRuntimeService().getVariable(processInstanceId, varName);
        } catch (Exception e) {
            o = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public HashMap getvariables(String instanceId) {
        HashMap<String, Object> o;
        try {
            o = (HashMap<String, Object>) this.getProcessEngine().getRuntimeService().getVariables(instanceId);
        } catch (Exception e) {
            o = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public boolean completeTask(String taskId) {
        boolean flag;
        try {
            TaskService taskService = this.getProcessEngine().getTaskService();
            taskService.complete(taskId);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public void setTaskPriority(String taskId, int priority) {
        try {
            TaskService taskService = this.getProcessEngine().getTaskService();
            taskService.setPriority(taskId, priority);
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public List<Attachment> getAttachmentsFiles(String taskId) {
        return this.getProcessEngine().getTaskService().getTaskAttachments(taskId);
    }

    @Override
    public Task getTaskDataByTaskID(String taskId) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public ProcessDefinition getProcessDataByDefID(String defId) {
        return this.getProcessEngine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(defId).singleResult();
    }

    @Override
    public List<Attachment> getProcessInstanceAttachmentsFiles(String instanceId) {
        return this.getProcessEngine().getTaskService().getProcessInstanceAttachments(instanceId);
    }

    @Override
    public List<Attachment> getAttachmentsFilesByProcessInstanceIdMain(String processInstanceId) {
        List<Attachment> attachments = new ArrayList<>();
        String idProcessInstance = processInstanceId;
        List<HistoricProcessInstance> instance1;
        do {
            attachments.addAll(this.getProcessEngine().getTaskService().getProcessInstanceAttachments(idProcessInstance));
            instance1 = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().superProcessInstanceId(idProcessInstance).list();
            if (instance1 != null && !instance1.isEmpty()) {
                for (int i = 0; i < instance1.size() - 1; i++) {
                    attachments.addAll(this.getProcessEngine().getTaskService().getProcessInstanceAttachments(instance1.get(i).getId()));
                }
                idProcessInstance = instance1.get(instance1.size() - 1).getId();
            }
        } while (instance1 != null && !instance1.isEmpty());
        return attachments;
    }

    @Override
    public List<HistoricTaskInstance> getEndedUsertasksList(String asignee) {
        return this.getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().taskAssignee(asignee).finished().orderByDeleteReason().desc().list();
    }

    @Override
    public boolean activateProcess(String processInstanceId) {
        boolean flag;
        try {
            RuntimeService runtimeService = this.getProcessEngine().getRuntimeService();
            runtimeService.activateProcessInstanceById(processInstanceId);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean suspendProcess(String processInstanceId) {
        boolean flag;
        try {
            RuntimeService runtimeService = this.getProcessEngine().getRuntimeService();
            runtimeService.suspendProcessInstanceById(processInstanceId);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public Object getvariableByExecutionId(String taskId, String varName) {
        Task t = this.getProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
        Object o = this.getProcessEngine().getRuntimeService().getVariable(t.getProcessInstanceId(), varName);
        return o;
    }

    @Override
    public boolean setAssigneeTask(String taskId, String assignee) {
        boolean flag;
        try {
            this.getProcessEngine().getTaskService().setAssignee(taskId, assignee);
            flag = true;
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public ArrayList<Task> getTaskGroup(String candidate) {
        ArrayList<Task> list = null;
        try {
            list = (ArrayList<Task>) this.getProcessEngine().getTaskService().createTaskQuery().taskCandidateGroup(candidate).list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    @Override
    public void loadSingleProcessByClassPath(String path) {
        RepositoryService repositoryService = this.getProcessEngine().getRepositoryService();
        repositoryService.createDeployment().addClasspathResource(path).deploy();
    }

    @Override
    public ProcessDefinition getProcessDefinitionByKey(String key) {
        ProcessDefinition pd;
        try {
            pd = getProcessEngine().getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
        } catch (Exception e) {
            pd = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return pd;
    }

    @Override
    public List<HistoricProcessInstance> getProcessInstanceHistoric() {
        List<HistoricProcessInstance> hpi = null;
        try {
            hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().orderByProcessInstanceStartTime().desc().list();

        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }

        return hpi;
    }

    @Override
    public List<HistoricProcessInstance> getProcessInstanceHistoric(int init, int max, boolean state) {
        List<HistoricProcessInstance> hpi = null;
        try {
            if (state) {
                hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().finished().orderByProcessInstanceStartTime().desc().listPage(init, max);
            } else {
                hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().unfinished().orderByProcessInstanceStartTime().desc().listPage(init, max);
            }
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }

        return hpi;
    }

    @Override
    public List<HistoricProcessInstance> getProcessInstanceHistoricByKey(String key) {
        List<HistoricProcessInstance> hpi = null;
        try {
            hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processDefinitionKey(key).orderByProcessInstanceStartTime().desc().list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return hpi;
    }

    @Override
    public ProcessInstance getProcessInstanceById(String processInstanceId) {
        try {
            return this.getProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public ProcessInstance getProcessInstanceByDefId(String processInstanceDefId) {
        try {
            return this.getProcessEngine().getRuntimeService().createProcessInstanceQuery().processDefinitionId(processInstanceDefId).singleResult();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<HistoricTaskInstance> getTaskByProcessInstanceId(String processInstanceId) {
        try {
            return this.getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().orderByTaskCreateTime().desc().list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<HistoricTaskInstance> getTaskByProcessInstanceIdMain(String processInstanceId) {
        List<String> listaIdsProcessInstace = new ArrayList<>();
        String idProcessInstance = processInstanceId;
        List<HistoricProcessInstance> instance1;
        do {
            listaIdsProcessInstace.add(idProcessInstance);
            instance1 = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().superProcessInstanceId(idProcessInstance).list();
            if (instance1 != null && !instance1.isEmpty()) {
                for (int i = 0; i < instance1.size() - 1; i++) {
                    listaIdsProcessInstace.add(instance1.get(i).getId());
                }
                idProcessInstance = instance1.get(instance1.size() - 1).getId();
            }
        } while (instance1 != null && !instance1.isEmpty());

        String ids = "";
        for (String listaIdsProcessInstace1 : listaIdsProcessInstace) {
            ids = ids + "'" + listaIdsProcessInstace1 + "',";
        }
        ids = ids.substring(0, ids.length() - 1);
        String sql = "SELECT TH.* FROM act_hi_taskinst as TH WHERE TH.proc_inst_id_ IN (" + ids + ") ORDER BY TH.start_time_ DESC";
        return this.getProcessEngine().getHistoryService().createNativeHistoricTaskInstanceQuery().sql(sql).list();
    }

    @Override
    public List<HistoricProcessInstance> getProcessInstanceHistoricById(String id) {
        List<HistoricProcessInstance> lhpi = null;
        try {
            lhpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processDefinitionId(id).orderByProcessInstanceStartTime().desc().list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return lhpi;
    }

    @Override
    public HistoricProcessInstance getHistoricProcessInstanceByInstanceID(String processInstanceId) {
        HistoricProcessInstance lhpi = null;
        try {
            lhpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return lhpi;
    }

    @Override
    public void setVariableProcessInstance(String porcessInstanceId, String varName, Object value) {
        try {
            this.getProcessEngine().getRuntimeService().setVariable(porcessInstanceId, varName, value);
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public List<Task> getListTaskActiveByProcessInstance(String porcessInstanceId) {
        try {
            return this.getProcessEngine().getTaskService().createTaskQuery().processInstanceId(porcessInstanceId).active().list();
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<IdentityLink> identityLinkPorTareaId(String idTarea) {
        try {
            return engine.getProcessEngine().getTaskService().getIdentityLinksForTask(idTarea);
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<String> getListProcessInstanceIdsByProcessInstanceIdMain(String processInstanceId) {
        List<String> listaIdsProcessInstace = new ArrayList<>();
        String idProcessInstance = processInstanceId;
        List<HistoricProcessInstance> instance1;
        do {
            listaIdsProcessInstace.add(idProcessInstance);
            instance1 = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().superProcessInstanceId(idProcessInstance).unfinished().list();
            if (instance1 != null && !instance1.isEmpty()) {
                for (int i = 0; i < instance1.size() - 1; i++) {
                    listaIdsProcessInstace.add(instance1.get(i).getId());
                }
                idProcessInstance = instance1.get(instance1.size() - 1).getId();
            }
        } while (instance1 != null && !instance1.isEmpty());
        return listaIdsProcessInstace;
    }

    @Override
    public Map getVar(String taskId) {
        Map<String, Object> o;
        try {
            o = this.getProcessEngine().getRuntimeService().getVariables(taskId);
//            this.getProcessEngine().close();
        } catch (Exception e) {
            o = null;
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
        return o;
    }

    @Override
    public void deleteProcessInstance(String processInstance, String reason) {
        try {
            this.getProcessEngine().getRuntimeService().deleteProcessInstance(processInstance, reason);
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public List<HistoricIdentityLink> HistoricidentityLinkPorTareaId(String idTarea) {
        return engine.getProcessEngine().getHistoryService().getHistoricIdentityLinksForTask(idTarea);
    }

    @Override
    public Task getTaskDataByProcessID(String processId) {
        TaskService taskService = this.getProcessEngine().getTaskService();
        return taskService.createTaskQuery().processInstanceId(processId).singleResult();
    }

    @Override
    public Object getvariableByExecutionId(String taskId, String executionId, String varName) {
        Task t = this.getProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
        Object o = this.getProcessEngine().getRuntimeService().getVariable(t.getExecutionId(), varName);
        return o;
    }

    @Override
    public List<HistoricProcessInstance> getProcessInstanceHistoric(String id, boolean state) {
        List<HistoricProcessInstance> hpi = null;
        try {
            if (state) {
                hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(id).finished().orderByProcessInstanceStartTime().desc().list();
            } else {
                hpi = this.getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(id).unfinished().orderByProcessInstanceStartTime().desc().list();
            }
        } catch (Exception e) {
            Logger.getLogger(BpmBaseEngine.class.getName()).log(Level.SEVERE, null, e);
        }

        return hpi;
    }

    @Override
    public ProcessDefinition getProcessDefinitionById(String id) {
        return this.getProcessEngine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(id).singleResult();
    }
}
