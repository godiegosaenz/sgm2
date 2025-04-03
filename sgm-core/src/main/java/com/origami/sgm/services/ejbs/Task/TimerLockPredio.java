/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.Task;

import com.origami.app.AppConfig;
import com.origami.sgm.predio.models.ModelLockPredio;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 *
 * @author Angel Navarro
 */
@Named
@Stateless
@javax.enterprise.context.Dependent
public class TimerLockPredio {

    @Inject
    private AppConfig appconfig;

    @Schedule(dayOfWeek = "*", month = "*", hour = "7-22", dayOfMonth = "*", year = "*", minute = "*/5", second = "0", persistent = false)
    @AccessTimeout(value = -1)
    public void myTimer() {
        System.out.println("Timer event: " + new Date());
        scanLockedPredios();
    }

    public void scanLockedPredios() {
         Map<String,ModelLockPredio> temp = new HashMap<String,ModelLockPredio>();
        try {
            Set<Map.Entry<String, ModelLockPredio>> entrySet = appconfig.getPrediosedicion().entrySet();
            for (Map.Entry<String, ModelLockPredio> en : entrySet) {
                String key = en.getKey();
                ModelLockPredio value = en.getValue();
                if (value.getExecuteMinute() >= 20) {
                    System.out.println("Liberado: " + key);                                   
                }else{
                    temp.put(key,value);                
                }
            }
            appconfig.setPrediosedicion(temp);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "scanLockedPredios", e);
        }
    }
    private static final Logger LOG = Logger.getLogger(TimerLockPredio.class.getName());

}
