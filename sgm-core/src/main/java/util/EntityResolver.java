/*
 *  Origami Solutions
 */

package util;

import java.util.ArrayList;
import java.util.Collection;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.marshalling.ObjectResolver;

/**
 *
 * @author fernando
 */
public class EntityResolver implements ObjectResolver{ 

    @Override
    public Object readResolve(Object source) {
        
        return unproxy(source);
    }

    @Override
    public Object writeReplace(Object source) {
        return source;
    }
    
    protected Object unproxy(Object field) {
        if (field == null) {
            return null;
        }
  
        if (field instanceof HibernateProxy) {
            if(Hibernate.isInitialized(field)){
                field =  ((HibernateProxy) field).getHibernateLazyInitializer().getImplementation();
            }
            else{
                return null;
            }
            
        }
        
        if(field instanceof PersistentCollection) {
            if(Hibernate.isInitialized(field)){
                return new ArrayList((Collection) field);
            }
            else{
                return null;
            }
            
        }
 
        return field;
    }
    
}
