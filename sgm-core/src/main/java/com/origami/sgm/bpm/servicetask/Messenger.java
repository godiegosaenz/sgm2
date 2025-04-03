/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import util.EmailUtil;

/**
 *
 * @author CarlosLoorVargas
 */
public class Messenger implements JavaDelegate {

    protected EmailUtil mail;

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            mail = new EmailUtil();
            if (de.getVariable("to") != null && de.getVariable("subject") != null && de.getVariable("message") != null && !de.getVariable("to").toString().equals("no_ingreso_correo@hotmail.com")) {
                if (de.getVariable("bcc") != null && de.getVariable("cc") != null) {
                    try {
                        mail.sendEmail(de.getVariable("to").toString(), de.getVariable("subject").toString(), de.getVariable("message").toString(), de.getVariable("bcc").toString(), de.getVariable("cc").toString());
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        mail.sendEmail(de.getVariable("to").toString(), de.getVariable("subject").toString(), de.getVariable("message").toString(), null, null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
