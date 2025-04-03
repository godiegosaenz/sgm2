
package com.origami.sgm.database;

import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.database.DatabaseEngine;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Qualifier;
import com.origami.app.cdi.jpa.hibernate.HibernateAddClassesEvent;
import com.origami.app.cdi.jpa.hibernate.HibernateFactory;
import com.origami.app.cdi.jpa.hibernate.UnitQualifier;

/**
 *
 * @author Fernando
 */
@ApplicationScoped
@UnitQualifier("sgm")
public class SgmHibernateFactory extends HibernateFactory{
    
    @Inject @UnitQualifier("sgm")
    protected Event<HibernateAddClassesEvent> acEvent;
    
    @Override
    protected void fireAddClassesEvent() {
        acEvent.fire(new HibernateAddClassesEvent());
    }
    
    @Override
    protected String getHibernateCfgXml() {
        return super.getHibernateCfgXml();
    }

    @Override
    protected void configNamingStrategy() {
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.ORACLE){
            cfg.setNamingStrategy(OracleNamingStrategy.INSTANCE);
        }
    }
    
    
    
}
