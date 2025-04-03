/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.Cobros;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.util.EjbsCaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author Mariuly
 */
@WebServlet(name = "Comprobante", urlPatterns = {"/Comprobantes"})
public class Comprobantes extends HttpServlet {

    @Inject
    ServletSession servletSession;
    private Map parametros;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        OutputStream outputStream;
        Connection conn = null;
        if (servletSession == null || servletSession.estaVacio()) {
            try (PrintWriter salida = response.getWriter()) {
                MsgFormatoNotificacion msg = EjbsCaller.getTransactionManager().find(MsgFormatoNotificacion.class, new Long(1));
                salida.println(msg.getHeader());
                salida.println("<center><p> No hay datos que mostrar.</p></center>");
                salida.println(msg.getFooter());
            }
            return;
        }
        parametros = servletSession.getParametros();
        response.setContentType("application/pdf");
        if (servletSession.tieneParametro("ciRuc")) {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + servletSession.retornarValor("ciRuc") + ".pdf");
        } else {
            response.addHeader("Content-disposition", "filename=" + servletSession.getNombreReporte() + ".pdf");
        }

        try {
            request.setCharacterEncoding("UTF-8");
            String ruta = getServletContext().getRealPath("//reportes//Emision");
            //CONECCION PARA PASARLE AL SUBREPORTE
            Session sess = HiberUtil.getSession();
            SessionImplementor sessionImpl = (SessionImplementor) sess;
            conn = sessionImpl.getJdbcConnectionAccess().obtainConnection();

            parametros = servletSession.getParametros();
            JasperPrint jasperPrint = null;
            JasperPrint p;
            Map<String, Object> map = new HashMap<>();

            List<RenPago> pagos = (List<RenPago>) parametros.get("liquidaciones");
            // Agregamos la lista de cobros
            int i = 0;
            // Agregamos al reporte principal el numero de paguinas que tenga la lista de pagos
            for (RenPago cobros : pagos) {
                map.put("cobros", cobros(cobros));
                map.put("ID_LIQUIDACION", cobros.getLiquidacion().getId());
                map.put("LOGO", parametros.get("LOGO"));
                map.put("COPIA", parametros.get("COPIA"));
                map.put("SUBREPORT_DIR", parametros.get("SUBREPORT_DIR"));
                map.put("NOMBRE_REPORTE", servletSession.getNombreReporte().concat(".jasper"));

                if (cobros.getNumComprobante() != null) {
                    map.put("NUM_COMPROBANTE", cobros.getNumComprobante().longValue());
                    // se obtiene el Numero de Comprobante
                } else {
                    map.put("NUM_COMPROBANTE", null);
                }
                if (cobros.getLiquidacion().getTipoLiquidacion().getId() == 49L) {
                    map.put("ID_LIQUIDACION", cobros.getId());
                    map.put("NOMBRE_REPORTE", "comprobanteCoactiva.jasper");
                }
                Utils.reemplazarRutaSubReportes(map);
                System.out.println("Ruta procesada: " + map.get("SUBREPORT_DIR"));
                Long cantidadPagos = recaudacion.cantidadPagosByLiquidacion(cobros.getLiquidacion());
                ruta = "/reportes/Emision/" + map.get("NOMBRE_REPORTE");
                if (i == 0) {
                    System.out.println("Procesando Reporte " + ruta);
                    try (InputStream resourceAsStream = Comprobantes.class.getResourceAsStream(ruta)) {
                        jasperPrint = JasperFillManager.fillReport(resourceAsStream, map, conn);
                    }
                } else {
                    try (InputStream resourceAsStream = Comprobantes.class.getResourceAsStream(ruta)) {
                        p = JasperFillManager.fillReport(resourceAsStream, map, conn);
                    }
                    if (p.getPages() != null && p.getPages().size() > 0) {
                        jasperPrint.addPage(p.getPages().get(0));
                    }
                }
                i++;
            }

            // Mostrar Report
            outputStream = response.getOutputStream();
            //            removeBlankPage(jasperPrint, ids.size());
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            outputStream.flush();
            outputStream.close();
            servletSession.borrarDatos();
        } catch (SQLException | JRException | IOException e) {
            Logger.getLogger(Comprobantes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (conn != null) {
                conn.close();
            }

        }
    }

    private List<Cobros> cobros(RenPago pago) {
        try {
            return recaudacion.getCobros(pago);
        } catch (Exception e) {
            Logger.getLogger(Comprobantes.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    private JasperReport getSubReport(String urlReport) throws JRException, FileNotFoundException {
        InputStream is = new FileInputStream(urlReport);
        return (JasperReport) JRLoader.loadObject(is);
    }

    private void removeBlankPage(JasperPrint print, int size) {
        List<JRPrintPage> pages = print.getPages(); // Obtengo las paginas que tiene el reporte 
        for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) { // recorro las paguinas
            JRPrintPage page = i.next(); // obtengo la pagina

            if (page.getElements().isEmpty()) { // pregunto si los elementos de la pagina son vacios
                i.remove(); // elimino ese elemento vacio
            }
        }
        if (pages.size() > size) {
            print.removePage(size); // se remueve una pagina  
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
            Logger.getLogger(Comprobantes.class.getName()).log(Level.SEVERE, null, ex);
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
//            Logger.getLogger(Comprobantes.class.getName()).log(Level.SEVERE, null, ex);
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
