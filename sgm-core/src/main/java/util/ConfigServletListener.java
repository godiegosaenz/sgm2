/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.origami.config.PropertiesLoader;
import com.origami.config.SisVars;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import jodd.datetime.JDateTimeDefault;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.hibernate.HibernateException;

/**
 * Web application lifecycle listener.
 *
 * @author Fernando
 */
public class ConfigServletListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ConfigServletListener.class.getName());

    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        LOG.info("*** Inicializando ***");
        System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
        // timezone y locale default para el sistema
        TimeZone.setDefault(TimeZone.getTimeZone("America/Guayaquil"));
        JDateTimeDefault.timeZone = TimeZone.getTimeZone("America/Guayaquil");
        Locale.setDefault(new Locale("es", "EC"));

        SisVars.urlbase = sce.getServletContext().getContextPath() + "/";
        SisVars.urlbaseFaces = sce.getServletContext().getContextPath() + SisVars.facesUrl + "/";
        SisVars.urlbaseFacesSinBarra = sce.getServletContext().getContextPath() + SisVars.facesUrl;

        DateConverter dateConverter = new DateConverter();
        dateConverter.setPattern("dd MM yyyy HH mm ss");
        ConvertUtils.register(dateConverter, java.util.Date.class);
        

        // carga de propiedades
        PropertiesLoader props1 = new PropertiesLoader(sce.getServletContext());
        props1.load();

        //Notificador not = new Notificador();
        //not.notificar();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            HibernateUtil.getSessionFactory().close();
//            HiberUtil.closeSession();
        } catch (HibernateException e) {
            // again failure, not much you can do
        }
        Enumeration<Driver> drivers = DriverManager.getDrivers();     
        Driver driver = null;
        // clear drivers
        while(drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            } catch (SQLException ex) {
                // deregistration failed, might want to do something, log at the very least
            }
        }
        LOG.info("cerrando kiosko!!!");
        System.gc();
    }
}
