/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.sgm.servlets.registro.PreformaIngreso;
import java.io.IOException;
import java.io.InputStream;
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
import util.Utils;

/**
 *
 * @author Anyelo
 */
@WebServlet(name = "Recibos", urlPatterns = {"/Recibos"})
public class Recibos extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/pdf");
        response.addHeader("Content-disposition", "filename=Recibo.pdf");
        ServletContext context = this.getServletConfig().getServletContext();
        String path = context.getRealPath("/");
        OutputStream out = response.getOutputStream();
        Map pars = new HashMap();
        Long id = Long.parseLong(request.getParameter("codigo"));
        JasperPrint jasperPrint;
        Connection conn = null;
        try {
            pars.put("ID_PAGO", id);
            pars.put("SUBREPORT_DIR", path + "reportes/Emision/");
            pars.put("RUTA_FIRMAS", path + "css/firmas/");
            Session sess = HiberUtil.getSession();
            SessionImplementor sessImpl = (SessionImplementor) sess;
            conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
            Utils.reemplazarRutaSubReportes(pars);
            try (InputStream resourceAsStream = Recibos.class.getResourceAsStream("/reportes/Emision/reciboCompleto.jasper")) {
                jasperPrint = JasperFillManager.fillReport(resourceAsStream, pars, conn);
            }
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();
            out.close();
            conn.close();
        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(PreformaIngreso.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
}
