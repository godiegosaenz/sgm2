/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.registro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;

/**
 *
 * @author Anyelo
 */
@WebServlet(name = "SolicitudIngreso", urlPatterns = {"/SolicitudIngreso"})
public class SolicitudIngreso extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/pdf");
        response.addHeader("Content-disposition", "filename=Acuerdo.pdf");
        ServletContext context = this.getServletConfig().getServletContext();
        String path = context.getRealPath("/");
        OutputStream out = response.getOutputStream();
        Map pars = new HashMap();
        Long id = Long.parseLong(request.getParameter("codigo"));
        JasperPrint jasperPrint;
        try {
            pars.put("p_idtramite", id);
            pars.put("SUBREPORT_DIR", path + "reportes/registroPropiedad/");
            pars.put("LOGO_URL", path + "image/logo.jpg");
            File reportFile = new File(getServletConfig().getServletContext()
                    .getRealPath("/reportes/registroPropiedad/CabRegistroMunicipal.jasper"));
            FileInputStream reportInStream = new FileInputStream(reportFile);
            Session sess = HiberUtil.getSession();
            SessionImplementor sessImpl = (SessionImplementor) sess;
            Connection conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
            jasperPrint = JasperFillManager.fillReport(reportInStream, pars, conn);
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();
            out.close();
            conn.close();
        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(SolicitudIngreso.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            out.close();
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
        processRequest(request, response);
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
        processRequest(request, response);
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
