/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.session.ServletSession;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.util.EjbsCaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import util.Utils;

/**
 *
 * @author origami-idea
 */
@WebServlet(name = "DocumentoDataSource", urlPatterns = {"/DocumentoDS"})
public class DocumentoDataSource extends HttpServlet {

    private Map parametros;
    
    @Inject
    ServletSession servletSession;

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
        JasperPrint jasperPrint;
        OutputStream outStream;
        Connection conn = null;
        if (servletSession == null || servletSession.estaVacio()) {
            PrintWriter salida = response.getWriter();
            MsgFormatoNotificacion msg = EjbsCaller.getTransactionManager().find(MsgFormatoNotificacion.class, new Long(1));
            salida.println(msg.getHeader());
            salida.println("<center><p>No hay datos que mostrar.</p></center>");
            salida.println(msg.getFooter());
            return;
        }
        parametros = servletSession.getParametros();
        Utils.reemplazarRutaSubReportes(parametros);
        response.setContentType("application/pdf");

        if (servletSession.tieneParametro("ciRuc")) {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + servletSession.retornarValor("ciRuc") + ".pdf");
        } else {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + ".pdf");
        }

        try {
            request.setCharacterEncoding("UTF-8");
            String ruta;
            if (servletSession.getNombreSubCarpeta() == null) {
                ruta = "//reportes//" + servletSession.getNombreReporte() + ".jasper";
            } else {
                ruta = "//reportes//" + servletSession.getNombreSubCarpeta() + "//" + servletSession.getNombreReporte() + ".jasper";
            }
            ruta = ruta.replace("//", "/");
            ArrayList<?> list = (ArrayList<?>) parametros.get("list");
            JRDataSource dataSource = new JRBeanCollectionDataSource(list);
            try (InputStream resourceAsStream = DocumentoDataSource.class.getResourceAsStream(ruta)) {
                jasperPrint = JasperFillManager.fillReport(resourceAsStream, parametros, dataSource);
            }
            outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            outStream.flush();
            outStream.close();

            servletSession.borrarDatos();

        } catch (JRException | IOException e) {
            Logger.getLogger(DocumentoDataSource.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(DocumentoDataSource.class.getName()).log(Level.SEVERE, null, ex);
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
//            Logger.getLogger(DocumentoDataSource.class.getName()).log(Level.SEVERE, null, ex);
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
