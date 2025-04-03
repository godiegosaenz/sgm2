/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.mail.service;

import com.origami.mail.ent.OmMail;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import java.util.Date;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HiberUtil;

/**
 *
 * @author Fernando
 */
@Stateless
@Interceptors(value = {HibernateEjbInterceptor.class})
public class OmMailerService {
    
    public void sendMail(OmMail mail){
        Session sess = HiberUtil.getSession();
        HiberUtil.requireTransaction();
        
        sess.persist(mail);
        
    }
    
    public OmMail takeNextMail(){
        Session sess = HiberUtil.getSession();
        HiberUtil.requireTransaction();
        
        Criteria crit = sess.createCriteria(OmMail.class);
        crit.add(Restrictions.gt("oportunidades", 0));
        crit.add(Restrictions.eq("enviado", false));
        crit.setMaxResults(1);
        
        OmMail mail1 = (OmMail) crit.uniqueResult();
        
        if(mail1!=null){
            mail1.setOportunidades(mail1.getOportunidades()-1);
        }
        sess.flush();
        
        return mail1;
    }
    
    public void mailEnviadoCorrectamente(Long id){
        Session sess = HiberUtil.getSession();
        HiberUtil.requireTransaction();
        
        Criteria crit = sess.createCriteria(OmMail.class);
        crit.add(Restrictions.eq("id", id));
        crit.setMaxResults(1);
        
        OmMail mail = (OmMail) crit.uniqueResult();
        
        mail.setEnviado(Boolean.TRUE);
        mail.setInstEnviado(new Date());
        
        sess.flush();
    }
    
}
