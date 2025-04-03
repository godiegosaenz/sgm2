/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.origami.config.SisVars;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author User
 */
public class FerEjb {

    /**
     * ctx.lookup("java:global/#{nombreProyecto}/#{component}"); ejemplo:
     * ctx.lookup("java:global/smbCatastro/Acl");
     *
     * @param enviroment
     * @param component
     * @return Object
     */
    public static Object getEjb(String component) {

        InitialContext ctx = null;
        Object o1 = null;
        try {

            ctx = new InitialContext();
            //o1 = ctx.lookup("java:global/smbCatastro/Acl");
            o1 = ctx.lookup(SisVars.ejbRuta + component);
            //System.out.println("//ruta "+SisVars.ejbRuta+" comp "+component);
            //System.out.println("//ooo1 "+ctx.toString());	
        } catch (NamingException ex) {
            // Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            // Logger.getLogger(component, component);
            //System.out.println("//ingreso al catch ::"+component);
        }
        //System.out.println("objeto "+o1);
        return o1;
    }

    public static EJBContext getEjbContext() {
        EJBContext context = null;
        try {
            context = (EJBContext) new InitialContext().lookup("java:comp/EJBContext");
        } catch (NamingException ex) {
            Logger.getLogger(FerEjb.class.getName()).log(Level.SEVERE, null, ex);

        }
        return context;
    }

    public static SessionContext getSessionContext() {
        SessionContext context = null;
        try {
            context = (SessionContext) new InitialContext().lookup("java:comp/env/sessionContext");
        } catch (NamingException ex) {
            Logger.getLogger(FerEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return context;
    }
}
