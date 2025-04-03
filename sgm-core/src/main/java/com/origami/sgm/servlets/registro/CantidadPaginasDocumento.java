/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.registro;

import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegMovimiento;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class CantidadPaginasDocumento extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    protected ServletSession servletSession;

    private Map parametros;
    private JasperPrint jasperPrint;

    public RegMovimiento updateAndCountPageInscripcion(RegMovimiento mov) {
        try {
            parametros = servletSession.getParametros();
            String ruta = JsfUti.getRealPath("//reportes//registroPropiedad//RegistroInscripcion.jasper");

            Session sess = HiberUtil.getSession();
            SessionImplementor sessImpl = (SessionImplementor) sess;
            Connection conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
            jasperPrint = JasperFillManager.fillReport(ruta, parametros, conn);
            // EN EL REGISTRO DE LA PROPIEDAD UNA PAGINA ES IGUAL A UNA HOJA (HOJA = FOJA = PAGINA)
            Integer numPage = Utils.getNumberOfPagesDocumento(JasperExportManager.exportReportToPdf(jasperPrint));
            if ((numPage % 2) == 0) {
                numPage = numPage / 2;
            } else {
                numPage = (numPage + 1) / 2;
            }
            mov.setNumPaginaInscripcion(numPage);
            //mov.setInscripcionImpresa(Boolean.TRUE);
            //acl.update(mov);
            acl.persist(mov);
            servletSession.borrarParametros();
            conn.close();
        } catch (SQLException | JRException e) {
            Logger.getLogger(CantidadPaginasDocumento.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return mov;
    }

    public RegMovimiento updateAndCountPageRazonInscripcion(RegMovimiento mov) {
        try {
            parametros = servletSession.getParametros();
            String ruta = JsfUti.getRealPath("//reportes//registroPropiedad//CabCertificadoPropiedadMercantil.jasper");

            Session sess = HiberUtil.getSession();
            SessionImplementor sessImpl = (SessionImplementor) sess;
            Connection conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
            jasperPrint = JasperFillManager.fillReport(ruta, parametros, conn);
            // EN EL REGISTRO DE LA PROPIEDAD UNA PAGINA ES IGUAL A UNA HOJA (HOJA = FOJA = PAGINA)
            Integer numPage = Utils.getNumberOfPagesDocumento(JasperExportManager.exportReportToPdf(jasperPrint));
            if ((numPage % 2) == 0) {
                numPage = numPage / 2;
            } else {
                numPage = (numPage + 1) / 2;
            }
            mov.setNumPaginaRazon(numPage);
            //mov.setRazonImpresa(Boolean.TRUE);
            mov.setFolioFin(mov.getFolioFin() + mov.getNumPaginaInscripcion() + numPage);
            //acl.update(mov);
            acl.persist(mov);
            servletSession.borrarParametros();
            conn.close();
        } catch (SQLException | JRException e) {
            Logger.getLogger(CantidadPaginasDocumento.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return mov;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public Map getParametros() {
        return parametros;
    }

    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

}
