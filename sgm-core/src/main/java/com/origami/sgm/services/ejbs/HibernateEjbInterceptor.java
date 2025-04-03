/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionSynchronizationRegistry;
import util.HiberUtil;
import util.HibernateUtil;

/**
 *
 * @author Fernando
 */
public class HibernateEjbInterceptor implements Serializable{

    public static String UserRegistryKey = "usuario";

    public static ThreadLocal<Boolean> inicioTomado = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Resource
    private SessionContext sessionContext;
    @Resource
    private TransactionSynchronizationRegistry registry;

    public HibernateEjbInterceptor() {
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        // registrar username
        registerUsername();

        Object result = null;
        Boolean esPuntoEntrada = true;
        Exception ex = null;
        SessionContext context = sessionContext;// FerEjb.getSessionContext();
        Map<String, Object> contextData = context.getContextData();
        if (HibernateUtil.yaIniciada.get()) {
            esPuntoEntrada = false;
//            System.out.println("   -> metodo encadenado");
        } else {
            HibernateUtil.yaIniciada.set(true);
//            System.out.println(" -> session activada");
        }

//        System.out.println("*** TracingInterceptor intercepting " + ctx.getMethod().getName());
//        long start = System.currentTimeMillis();
//        String param = (String)ctx.getParameters()[0];
//
//        if (param == null)
//            ctx.setParameters(new String[]{"default"});
        try {
            result = ctx.proceed();
//            System.out.println("Resultado: " + result); Obtiene la clase que se esta ejecutado
            if (esPuntoEntrada) {
                HiberUtil.flushAndCommit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            HiberUtil.rollback();
            ex = e;
            throw e;
        } finally {
//            long time = System.currentTimeMillis() - start;
//            String method = ctx.getClass().getName();
//            System.out.println("*** TracingInterceptor invocation of " + method + " took " + time + "ms");
            if (esPuntoEntrada) {
                //FerHiber.flushAndCommit();
                HiberUtil.rollbackOnlyCheck();
                HiberUtil.closeSession();
                HibernateUtil.yaIniciada.set(false);
//                System.out.println(" -> session cerrada");
            }

            // desregistrar user
            unregisterUsername();
        }

        if (ex != null) {
            throw ex;
        }

        return result;
    }

    /**
     * Register Username for transaction
     */
    private void registerUsername() {
        // get username from threadlocal
//        registry.putResource(UserRegistryKey, IdentificacionHilo.getUsernameGlobal());
    }

    private void unregisterUsername() {
        registry.putResource(UserRegistryKey, null);
    }

}
