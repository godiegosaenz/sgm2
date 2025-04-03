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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;
import util.Utils;

/**
 * Modificado de la clase #Documento de Joao Sanga.
 *
 * @author Angel Navarro
 */
@WebServlet(name = "DocumentoXls", urlPatterns = {"/DocumentoExcel"})
public class DocumentoXls extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2676948362924799696L;

	private Map<String, Object> parametros;

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
    @SuppressWarnings("unchecked")
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
        //response.setContentType("application/vnd.ms-excel");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (servletSession.tieneParametro("ciRuc")) {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + servletSession.retornarValor("ciRuc") + ".xlsx");
        } else {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + new SimpleDateFormat("dd-MMMM-YYYY").format(Calendar.getInstance().getTime()) + ".xlsx");
        }
        try {
            request.setCharacterEncoding("UTF-8");
            InputStream ruta;
            String nombreReporte = null;
            servletSession.setNombreSubCarpeta(servletSession.getNombreSubCarpeta().replace("//", "/"));
            if (servletSession.getNombreSubCarpeta() == null) {
//                ruta = getServletContext().getRealPath("//reportes//" + servletSession.getNombreReporte() + ".jasper");
                nombreReporte = "/reportes/" + servletSession.getNombreReporte() + ".jasper";
            } else {
//                ruta = getServletContext().getRealPath("//reportes//" + servletSession.getNombreSubCarpeta() + "//" + servletSession.getNombreReporte() + ".jasper");
                nombreReporte = "/reportes/" + servletSession.getNombreSubCarpeta() + "/" + servletSession.getNombreReporte() + ".jasper";
            }
            System.out.println("Ejecutando Reporte >> " + nombreReporte);
            ruta = DocumentoXls.class.getResourceAsStream(nombreReporte);
            Utils.reemplazarRutaSubReportes(parametros);
            if (servletSession.getTieneDatasource()) {
                Session sess = HiberUtil.getSession();
                SessionImplementor sessImpl = (SessionImplementor) sess;
                conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                jasperPrint = JasperFillManager.fillReport(ruta, parametros, conn);

            } else {
                JRDataSource dataSource = new JRBeanCollectionDataSource(new ArrayList<>());
                jasperPrint = JasperFillManager.fillReport(ruta, parametros, dataSource);
            }

            outStream = response.getOutputStream();

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));

            // Configuraciones del Reporte
            SimpleXlsxReportConfiguration xlsReportConfiguration = new SimpleXlsxReportConfiguration();
            
            xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
            xlsReportConfiguration.setDetectCellType(true);
            xlsReportConfiguration.setWhitePageBackground(servletSession.getFondoBlanco());
            xlsReportConfiguration.setMaxRowsPerSheet(500000);
            xlsReportConfiguration.setIgnoreAnchors(true);
            xlsReportConfiguration.setFontSizeFixEnabled(true);
            xlsReportConfiguration.setWrapText(true);
            xlsReportConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
            xlsReportConfiguration.setCollapseRowSpan(Boolean.TRUE);
            xlsReportConfiguration.setIgnoreAnchors(Boolean.TRUE);
            exporter.setConfiguration(xlsReportConfiguration);
            if (servletSession.getOnePagePerSheet()) {
                xlsReportConfiguration.setOnePagePerSheet(servletSession.getOnePagePerSheet());
                xlsReportConfiguration.setWhitePageBackground(servletSession.getFondoBlanco());
            }
            // Configuraciones de exportacion.
            //SimpleXlsExporterConfiguration xlsExporterConfiguration = new SimpleXlsExporterConfiguration();
            exporter.exportReport();
            ruta.close();
            servletSession.borrarDatos();

        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(DocumentoXls.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(DocumentoXls.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(DocumentoXls.class.getName()).log(Level.SEVERE, null, ex);
        }
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
