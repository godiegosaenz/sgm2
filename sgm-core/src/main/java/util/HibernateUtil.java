/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.lang.annotation.Annotation;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import com.origami.app.cdi.jpa.hibernate.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author User
 */
public class HibernateUtil {

//    private static SessionFactory sessionFactory;
    public static Configuration cfg;
    
    public static HibernateFactory getFactory(){
        UnitQualifier uq = new UnitQualifier() {
            @Override
            public String value() {
                return "sgm";
            }
            @Override
            public Class<? extends Annotation> annotationType() {
                return UnitQualifier.class;
            }
        };
        return BeanProvider.getContextualReference(HibernateFactory.class, uq);
    }
    
    public static ThreadLocal<Boolean> yaIniciada = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false; //To change body of generated methods, choose Tools | Templates.
        }
    };
    public static ThreadLocal<Boolean> rollbackOnly = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false; //To change body of generated methods, choose Tools | Templates.
        }
    };

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
//            cfg = new Configuration().configure();
            
//            sessionFactory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
//            System.err.println("Initial SessionFactory creation failed." + ex);
//            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return HibernateUtil.getFactory().getFactory();
    }
    
}
