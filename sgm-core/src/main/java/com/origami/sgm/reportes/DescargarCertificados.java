/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.util.EjbsCaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author CarlosLoorVargas
 */
@WebServlet(name = "certificadoAvaluo", urlPatterns = {"/certificadoAvaluo"})
public class DescargarCertificados extends HttpServlet {

    private OutputStream responseOutputStream;
    private CatCertificadoAvaluo cert = null;
    private JasperPrint jasperPrint;
    private OutputStream outStream;
    private String path, reporte, ruta;
    private Map params;
    private Connection conn = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        params = new HashMap();
        path = request.getRealPath("/");
        try {
            //System.out.println("" + request.getRealPath("/"));
            if (request.getParameter("cert") != null) {
                cert = (CatCertificadoAvaluo) EjbsCaller.getTransactionManager().find(Querys.getCertificadoAval, new String[]{"codigo"}, new Object[]{request.getParameter("cert")});
                if (cert != null) {
                    params.put("id", cert.getId());
                    params.put("firma", cert.getFirmaDir());
                    params.put("logo", path + SisVars.logoReportes);
                    reporte = "/catastro/CertificadoAvaluoPropiedad";
                    response.setContentType("application/pdf");
                    response.addHeader("Content-disposition", "filename=" + reporte + ".pdf");
                    request.setCharacterEncoding("UTF-8");
                    Session sess = HiberUtil.getSession();
                    SessionImplementor sessImpl = (SessionImplementor) sess;
                    conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                    try (InputStream resourceAsStream = DescargarCertificados.class.getResourceAsStream("/reportes/" + reporte + ".jasper")) {
                        jasperPrint = JasperFillManager.fillReport(resourceAsStream, params, conn);
                        //outStream = response.getOutputStream();
                        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
                    }
                }
            } else {
                PrintWriter salida = response.getWriter();
                MsgFormatoNotificacion msg = EjbsCaller.getTransactionManager().find(MsgFormatoNotificacion.class, new Long(1));
                salida.println(msg.getHeader());
                salida.println("<center><p>No hay datos que mostrar.</p></center>");
                salida.println(msg.getFooter());
                salida.close();
            }

        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(DescargarCertificados.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(DescargarCertificados.class.getName()).log(Level.SEVERE, null, ex);
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
//            Logger.getLogger(DescargarCertificados.class.getName()).log(Level.SEVERE, null, ex);
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
    }

}
