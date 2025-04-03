/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.registro;

import com.origami.session.ServletSession;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.util.EjbsCaller;
import java.io.IOException;
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
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;

/**
 *
 * @author Anyelo
 */
@WebServlet(name = "DocumentoWord", urlPatterns = {"/DocumentoWord"})
public class DocumentoWord extends HttpServlet {

    private Map parametros;
    
    @Inject
    ServletSession ss;

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
        request.setCharacterEncoding("UTF-8");
        //ServletSession ss = (ServletSession) request.getSession().getAttribute("servletSession");
        if (ss == null || ss.estaVacio()) {
            PrintWriter pw = response.getWriter();
            MsgFormatoNotificacion msg = EjbsCaller.getTransactionManager().find(MsgFormatoNotificacion.class, new Long(1));
            pw.println(msg.getHeader());
            pw.println("<center><p>No hay datos que mostrar.</p></center>");
            pw.println(msg.getFooter());
            return;
        }
        JasperPrint jp;
        OutputStream os;
        parametros = ss.getParametros();
        response.setContentType("application/msword");
        response.addHeader("Content-disposition", "filename=" + ss.getNombreDocumento() + ".docx");
        try {
            Connection conn;
            String ruta;
            os = response.getOutputStream();
            
            if (ss.getNombreSubCarpeta() == null) {
                ruta = getServletContext().getRealPath("//reportes//" + ss.getNombreReporte() + ".jasper");
            } else {
                ruta = getServletContext().getRealPath("//reportes//" + ss.getNombreSubCarpeta() + "//" + ss.getNombreReporte() + ".jasper");
            }
            if (ss.getTieneDatasource()) {
                Session sess = HiberUtil.getSession();
                SessionImplementor sessImpl = (SessionImplementor) sess;
                conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                jp = JasperFillManager.fillReport(ruta, parametros, conn);
                conn.close();
            } else {
                JRDataSource dataSource = new JRBeanCollectionDataSource(new ArrayList());
                jp = JasperFillManager.fillReport(ruta, parametros, dataSource);
            }
            JRDocxExporter ex = new JRDocxExporter();
            ex.setExporterInput(new SimpleExporterInput(jp));
            ex.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            SimpleDocxReportConfiguration re = new SimpleDocxReportConfiguration();
            re.setFlexibleRowHeight(Boolean.TRUE);
            re.setFramesAsNestedTables(Boolean.FALSE);
            ex.exportReport();
            ss.borrarDatos();
        } catch (IOException | SQLException | JRException e) {
            Logger.getLogger(DocumentoWord.class.getName()).log(Level.SEVERE, null, e);
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
