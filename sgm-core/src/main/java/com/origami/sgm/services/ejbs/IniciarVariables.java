/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.application.ApplEmpresa;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HiberUtil;

/**
 *
 * @author Angel Navarro
 */
@Singleton(name = "iniciarVariables")
@Startup
@ApplicationScoped
public class IniciarVariables {

    @Inject
    protected Entitymanager manager;
    private static final Logger LOG = Logger.getLogger(IniciarVariables.class.getName());

    public void appinit(@Observes @Initialized(ApplicationScoped.class) Object init) {
        LOG.info(" @@@ Inicializando variables CDI way...");
        init();
    }

//    @PostConstruct
    public void init() {
//        LOG.info(" @@@ Inicializando variables EJB way...");
        try {
            Session sess = HiberUtil.getSession();
            Query q1 = sess.createQuery("SELECT em1 FROM ApplEmpresa em1");
            q1.setMaxResults(1);
            ApplEmpresa emp1 = (ApplEmpresa) q1.uniqueResult();
            if (emp1 != null) {
                SisVars.PROVINCIA = emp1.getCodigoProvincia() != null ? emp1.getCodigoProvincia().shortValue() : null;
                SisVars.CANTON = emp1.getCodigoCanton() != null ? emp1.getCodigoCanton().shortValue() : null;
                SisVars.URLPLANOIMAGENPREDIO = emp1.getUrlPredio() != null ? emp1.getUrlPredio().toString() : null;
                SisVars.NOMBREMUNICIPIO = emp1.getRazonSocial() != null ? emp1.getRazonSocial() : null;
                CatCanton canton = (CatCanton) manager.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA});
                if (canton != null) {
                    SisVars.NOMBRECANTON = canton.getNombre();
                    SisVars.NOMBREPROVINCIA = canton.getIdProvincia().getDescripcion();
                }
                SisVars.sitioWebMunicipio = emp1.getSitioWeb()!= null ? emp1.getSitioWeb() : "";

            }

        } catch (HibernateException e) {
            Logger.getLogger(IniciarVariables.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
