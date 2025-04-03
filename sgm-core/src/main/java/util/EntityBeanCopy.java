/*
 *  Origami Solutions
 */
package util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.cloner.ObjectCloner;
import org.jboss.marshalling.cloner.ObjectClonerFactory;
import org.jboss.marshalling.cloner.ObjectCloners;

/**
 *
 * @author fernando
 */
public class EntityBeanCopy {

    public static Object clone(Object source) {

        final ObjectClonerFactory clonerFactory = ObjectCloners.getSerializingObjectClonerFactory();
        final ClonerConfiguration configuration = new ClonerConfiguration();
        configuration.setObjectResolver(new EntityResolver());
        final ObjectCloner cloner = clonerFactory.createCloner(configuration);

        Object result = null;

        try {
            result = cloner.clone(source);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List cloneClass(Collection source) throws Exception {
        List object = new ArrayList<>();
        Object s1;
        for (Object s : source) {
            try {
                s1 = s.getClass().newInstance();
                cloneClass(s, s1);
                object.add(s1);
            } catch (Exception ex) {
                Logger.getLogger(EntityBeanCopy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return object;
    }

    public static void cloneClass(Object orig, Object dest) throws Exception {
        try {
            BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
            beanUtilsBean.getConvertUtils().register(false, true, 0);
            BeanUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException | InvocationTargetException ie) {
            Logger.getLogger(EntityBeanCopy.class.getName()).log(Level.SEVERE, null, ie);
        }
    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization is null");
        }
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

}
