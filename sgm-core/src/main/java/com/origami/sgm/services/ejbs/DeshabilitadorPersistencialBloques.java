/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.sgm.managedbeans.component.BloquesDialogConf;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Fernando
 */
@DisabledPersistenceBloques
@Interceptor
public class DeshabilitadorPersistencialBloques implements Serializable{

    private static final Logger LOG = Logger.getLogger(DeshabilitadorPersistencialBloques.class.getName());

    @Inject
    private PersistenceState state;
    @Inject
    private BloquesDialogConf bloqueDlg;
    @Inject
    private DPBStates runStates;

    @AroundInvoke
    public Object around(InvocationContext ic) throws Exception {
        if (Boolean.TRUE.equals(runStates.getAroundYaIniciado())) {
//            LOG.info(" ### bloque around bypass");
            return ic.proceed();
        } else {
//            LOG.info(" ### iniciando bloque around");
            runStates.setAroundYaIniciado(Boolean.TRUE);
            if (Boolean.TRUE.equals(bloqueDlg.getTransitorio())) {
//                LOG.info(" ### Deshabilitando transacciones bloques");
                state.setTransactionEnabled(false);
            }
            Object result = ic.proceed();
//            LOG.info(" ### finalizando bloque around");
            state.setTransactionEnabled(true);
            runStates.setAroundYaIniciado(Boolean.FALSE);
            return result;
        }
    }


}
