/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.session.ServletSession;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.util.EjbsCaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author origami-idea
 */
@WebServlet(name = "Documento", urlPatterns = {"/Documento"})
public class Documento extends HttpServlet {

    @Inject
    ServletSession servletSession;

    private Map parametros;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        //ServletSession servletSession = (ServletSession) request.getSession().getAttribute("servletSession");
        //ServletSession servletSession = (ServletSession) BeanProvider.getContextualReference("servletSession", false);
        JasperPrint jasperPrint;
        OutputStream outStream;
        Connection conn = null;
        if (servletSession.estaVacio()) {
            PrintWriter salida = response.getWriter();
            MsgFormatoNotificacion msg = EjbsCaller.getTransactionManager().find(MsgFormatoNotificacion.class, new Long(1));
            salida.println(msg.getHeader());
            salida.println("<center><p>No hay datos que mostrar.</p></center>");
            salida.println(msg.getFooter());
            salida.close();
            return;
        }
        parametros = servletSession.getParametros();
        parametros.put("FINANCIERO", SchemasConfig.FINANCIERO);
        parametros.put("APP1", SchemasConfig.APP1);
        parametros.put("FLOW", SchemasConfig.FLOW);
        parametros.put("MEJORAS", SchemasConfig.MEJORAS);
        parametros.put("SECUENCIAS", SchemasConfig.SECUENCIAS);
        parametros.put("TEMPORAL", SchemasConfig.TEMPORAL);

        response.setContentType("application/pdf");

        if (servletSession.tieneParametro("ciRuc")) {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + servletSession.retornarValor("ciRuc") + ".pdf");
        } else {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + "(" + new Date().getTime() + ")" + ".pdf");
        }

        try {
            request.setCharacterEncoding("UTF-8");
            InputStream ruta;
            System.out.println("Nombre reporte: " + servletSession.getNombreReporte());
            if (servletSession.getNombreSubCarpeta() == null) {
//                ruta = getServletContext().getRealPath("//reportes//" + servletSession.getNombreReporte() + ".jasper");
                ruta = Documento.class.getResourceAsStream("/reportes/" + servletSession.getNombreReporte() + ".jasper");
                System.out.println("Entro sin sub carpeta");
                System.out.println("Ruta sin subcarpeta: " + ruta);
            } else {
                System.out.println("Entro a sub carpeta");
//                ruta = getServletContext().getRealPath("//reportes//" + servletSession.getNombreSubCarpeta() + "//" + servletSession.getNombreReporte() + ".jasper");
                ruta = Documento.class.getResourceAsStream("/reportes/" + servletSession.getNombreSubCarpeta() + "/" + servletSession.getNombreReporte() + ".jasper");
                System.out.println(" Ruta que debe mostrar: " + "/reportes/" + servletSession.getNombreSubCarpeta() + "/" + servletSession.getNombreReporte() + ".jasper");
                System.out.println("Ruta en subcarpeta: " + ruta.toString());
            }
            Logger.getLogger(Documento.class.getName()).log(Level.INFO, "Generando Reporte >> {0}", servletSession.getNombreReporte());
            if (servletSession.getTieneDatasource()) {
                Session sess = HiberUtil.getSession();
                SessionImplementor sessImpl = (SessionImplementor) sess;
                conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                Utils.reemplazarRutaSubReportes(parametros);
                System.out.println(parametros);
                System.out.println("Ruta del certificado:");
                System.out.println(ruta);
                jasperPrint = JasperFillManager.fillReport(ruta, parametros, conn);
                if (servletSession.getEncuadernacion() != null && servletSession.getEncuadernacion()) {
                    List pages = jasperPrint.getPages();
                    JRPrintPage page;
                    List<JRPrintElement> elements;
                    for (int i = 1; i < pages.size() + 1; i++) {
                        page = (JRPrintPage) pages.get(i - 1);
                        elements = page.getElements();
                        if (i % 2 != 0) {//IMPAR
                            for (JRPrintElement temp : elements) {
                                temp.setX(temp.getX() + 30);
                            }
                        } else {//PAR
                            for (JRPrintElement temp : elements) {
                                temp.setX(temp.getX() - 30);
                            }
                        }
                    }
                }
            } else {
                Utils.reemplazarRutaSubReportes(parametros);
                JRDataSource dataSource = new JRBeanCollectionDataSource(new ArrayList());
                if (servletSession.getDataSource() != null) {
                    dataSource = new JRBeanCollectionDataSource(servletSession.getDataSource());
                }

                jasperPrint = JasperFillManager.fillReport(ruta, parametros, dataSource);

            }
            if (servletSession.getAgregarReporte() != null && servletSession.getAgregarReporte()) {
                for (Map reporte : servletSession.getReportes()) {
                    System.out.println(">>" + reporte);
                    System.out.println("reporte >>>>> "+reporte);
                    if (conn == null) {
                        Session sess = HiberUtil.getSession();
                        SessionImplementor sessImpl = (SessionImplementor) sess;
                        conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                    }
                    if (reporte.containsKey("nombreSubCarpeta")) {
                        ruta = Documento.class.getResourceAsStream("/reportes/" + reporte.get("nombreSubCarpeta") + "/" + reporte.get("nombreReporte") + ".jasper");
                    } else {
                        ruta = Documento.class.getResourceAsStream("/reportes/" + servletSession.getNombreSubCarpeta() + "/" + reporte.get("nombreReporte") + ".jasper");
                    }
                    Utils.reemplazarRutaSubReportes(reporte);
                    JasperPrint jasperPrint2 = JasperFillManager.fillReport(ruta, reporte, conn);
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
            outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            ruta.close();
            outStream.flush();
            outStream.close();
            servletSession.borrarDatos();

        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        try {
//            processRequest(request, response);
//        } catch (SQLException ex) {
//            Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
