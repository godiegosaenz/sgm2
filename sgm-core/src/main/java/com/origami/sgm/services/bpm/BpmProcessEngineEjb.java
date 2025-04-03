/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.bpm;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.activiti.engine.ProcessEngine;
import util.ApplicationContextUtils;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton(name = "bpmProcessEngine")
@Lock(LockType.READ)
public class BpmProcessEngineEjb implements BpmProcessEngine {

    private ProcessEngine processEngine;
    
    
    @PostConstruct
    private void init() {
        processEngine = (ProcessEngine) ApplicationContextUtils.getBean("processEngine");
        //processEngine.getProcessEngineConfiguration()
    }

    @Override
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

}
