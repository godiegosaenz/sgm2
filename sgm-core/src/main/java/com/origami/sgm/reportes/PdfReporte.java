/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.session.ServletSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
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
 * @author supergold
 */
public class PdfReporte implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    private String ruta;
    private Boolean agregarReporte = false;
    ServletSession servletSession;
    JasperPrint jasperPrint;
    
    JasperPrint reporte_view;
    
    public byte[] generarPdf(String nombre, Map paramt) throws SQLException {
        byte[] pdfByte = null;
        Connection conn = null;
        InputStream in = null;
        InputStream stream = null;
        try {
            Session sess = HiberUtil.getSession();
            SessionImplementor implementor = (SessionImplementor) sess;
            conn = implementor.getJdbcConnectionAccess().obtainConnection();
            System.out.println("Ejecutando Reporte >> " + nombre);
//            in = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(nombre);
            in = PdfReporte.class.getResourceAsStream(nombre);
            Utils.reemplazarRutaSubReportes(paramt);
            jasperPrint = JasperFillManager.fillReport(in, paramt, conn);
            if (servletSession.getAgregarReporte() != null && servletSession.getAgregarReporte()) {
                System.out.println(">>" + servletSession.getReportes());
                for (Map reporte : servletSession.getReportes()) {
//                    System.out.println(">>" + reporte);
//                    System.out.println("reporte >>>>> " + reporte);
                 
                    if (conn == null) {
                        sess = HiberUtil.getSession();
                        SessionImplementor sessImpl = (SessionImplementor) sess;
                        conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                    }
                    if (reporte.containsKey("nombreSubCarpeta")) {
                        stream = PdfReporte.class.getResourceAsStream("/reportes/" + reporte.get("nombreSubCarpeta") + "/" + reporte.get("nombreReporte") + ".jasper");
                    } else {
                        stream = PdfReporte.class.getResourceAsStream("/reportes/" + servletSession.getNombreSubCarpeta() + "/" + reporte.get("nombreReporte") + ".jasper");
                    }
                    Utils.reemplazarRutaSubReportes(reporte);
                    JasperPrint jasperPrint2 = JasperFillManager.fillReport(stream, reporte, conn);
                    if (jasperPrint2.getPages() != null && jasperPrint2.getPages().size() > 0) {
                        if (jasperPrint2.getPages().size() > 1) {
                            for (JRPrintPage page : jasperPrint2.getPages()) {
                                jasperPrint.addPage(page);
                            }
                        } else {
                            jasperPrint.addPage(jasperPrint2.getPages().get(0));
                        }
                    }
                }
            }

//            reporte_view = JasperFillManager.fillReport(in, paramt, conn);
            pdfByte = JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (JRException | SQLException e) {
            Logger.getLogger(PdfReporte.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PdfReporte.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (conn != null) {
                conn.close();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(PdfReporte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return pdfByte;
    }
    
    public JasperPrint getReporte_view() {
        return reporte_view;
    }
    
    public void setReporte_view(JasperPrint reporte_view) {
        this.reporte_view = reporte_view;
    }
    
    public ServletSession getServletSession() {
        return servletSession;
    }
    
    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }
    
}
