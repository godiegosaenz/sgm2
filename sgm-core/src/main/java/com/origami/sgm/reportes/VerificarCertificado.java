/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.util.EjbsCaller;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
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
 * @author Origami13
 */
@WebServlet(name = "verificarCertificado", urlPatterns = {"/verificarCertificado"})
public class VerificarCertificado extends HttpServlet {

    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    protected CatastroServices catastro;
    @Inject
    protected OmegaUploader omegaUploader;
    @Inject
    ServletSession ss;

    private CatCertificadoAvaluo cert = null;
    protected List<CatPredio> predios;
    protected CatPredio predio;
    private Map params;
    private CatEnte funcionario;
    private JasperPrint jasperPrint;
    private Connection conn = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JRException {
        response.setContentType("text/html;charset=UTF-8");

        params = new HashMap();
        predio = new CatPredio();
        predios = new ArrayList<>();
        InputStream ruta;
        String footer = SisVars.sisLogo1.substring(0, SisVars.sisLogo1.length() - 6) + "-footer.png";
        if (request.getParameter("idCertificado") != null) {
            String id = request.getParameter("idCertificado");
            String petNombre = "";
            String petIdentificacion = "";
            cert = (CatCertificadoAvaluo) manager.find(Querys.getCertificadobyId, new String[]{"id"}, new Object[]{Long.valueOf(id)});
            if (cert == null) {
                devolverError(response, "Certificado Inválido");
                return;
            }
            funcionario = (CatEnte) manager.find(Querys.getEnteByUsername, new String[]{"user"}, new Object[]{cert.getUsuario()});

            if (funcionario == null) {
                devolverError(response, "Certificado Inválido");
                return;
            }

            if (cert != null) {
                try {
                    String claves[] = cert.getDetalle().split("/");
                    for (int i = 2; i < claves.length; i++) {
                        predio = (CatPredio) manager.find(Querys.getPredioByClaveCat, new String[]{"claveCat"}, new Object[]{claves[i]});
                        predios.add(predio);
                    }
                    String petidor[] = cert.getIdentificacion().split("/");
                    petIdentificacion = petidor[0];
                    if (petidor.length == 2) {
                        petNombre = petidor[1];
                    }
                    ss.instanciarParametros();
                    ss.setNombreSubCarpeta("/catastro/certificados");
                    ss.setNombreReporte(cert.getFormato().getReporte());
                    ss.agregarParametro("LOGO_1", SisVars.logoReportes);
                    ss.agregarParametro("LOGO", SisVars.sisLogo1);
                    ss.agregarParametro("LOGO_FOOTER", footer);
                    ss.agregarParametro("TITULO", SisVars.NOMBREMUNICIPIO);
                    ss.agregarParametro("NOMBRE_CERTIFICADO", cert.getFormato().getCodigo());
                    ss.agregarParametro("DETALLE", claves[0]);
                    ss.agregarParametro("USUARIO", cert.getUsuario());
                    ss.agregarParametro("FECHA", cert.getFecha());
                    ss.agregarParametro("PETIDOR", petNombre);
                    ss.agregarParametro("PERTIDOR_IDNT", petIdentificacion);
                    ss.agregarParametro("CODIGO", cert.getId());
                    ss.agregarParametro("SUBREPORT_DIR", SisVars.REPORTES + "/");
                    ss.agregarParametro("FUNCIONARIO", funcionario.getNombreCompleto());

                    response.setContentType("application/pdf");
                    response.addHeader("Content-disposition", "filename=" + cert.getFormato().getReporte() + ".pdf");
                    request.setCharacterEncoding("UTF-8");
                    Session sess = HiberUtil.getSession();
                    SessionImplementor sessImpl = (SessionImplementor) sess;

                    try (InputStream resourceAsStream = DescargarCertificados.class.getResourceAsStream("/reportes/catastro/certificados/" + cert.getFormato().getReporte() + ".jasper")) {
                        jasperPrint = JasperFillManager.fillReport(resourceAsStream, ss.getParametros(), new JRBeanCollectionDataSource(predios));
                        //outStream = response.getOutputStream();

                    }

                    if (cert.getFormato().getCodigo().equals("FICHA CATASTRAL")) {
                        Map<String, Object> reporteadd = new HashMap<>();
                        reporteadd.put("nombreSubCarpeta", "/catastro/Ibarra");
                        reporteadd.put("nombreReporte", "fichaMiduvi");

                        CatPredio predio = predios.get(0);
                        if (predio != null) {

                            int numFotos = 1;
                            List<FotoPredio> fotos = null;
                            if (predio.getPredioRaiz() == null) {
                                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()});
                            } else {
                                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getPredioRaiz().longValue()});
                            }
                            if (Utils.isNotEmpty(fotos)) {
                                for (FotoPredio foto : fotos) {
                                    switch (numFotos) {
                                        case 1:
                                            reporteadd.put("FachadaFrontal", omegaUploader.streamFile(foto.getFileOid()));

                                            break;
                                        case 2:
                                            reporteadd.put("FachadaIzquierda", omegaUploader.streamFile(foto.getFileOid()));
                                            break;
                                        case 3:
                                            reporteadd.put("FachadaDerecha", omegaUploader.streamFile(foto.getFileOid()));
                                            break;
                                        case 4:
                                            reporteadd.put("FachadaPosterior", omegaUploader.streamFile(foto.getFileOid()));
                                            break;
                                    }
                                    numFotos++;
                                }
                            }
                            reporteadd.put("predio", predio.getId());

                            if (predio.getPredioRaiz() != null) {
                                CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{predio.getPredioRaiz()});
                                reporteadd.put("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
                            } else {
                                reporteadd.put("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predio.getNumPredio());
                            }
                            reporteadd.put("LOGO", SisVars.sisLogo);
                            reporteadd.put("LOGO2", SisVars.sisLogo1);
                            reporteadd.put("SUBREPORT_DIR_TITLE", "reportes/");
                            reporteadd.put("SUBREPORT_DIR", "reportes/catastro/Ibarra/");
                            ss.addParametrosReportes(reporteadd);

                            if (predio.getCatPredioEdificacionCollection() != null && !Utils.isEmpty(predio.getCatPredioEdificacionCollection())) {
                                int count = 0;
                                System.out.println("Agregando construcciones");
                                String edificaciones = "";
                                for (CatPredioEdificacion edif : predio.getCatPredioEdificacionCollection()) {
                                    count++;
                                    edificaciones += edif.getId().toString() + ",";
                                    if ((count % 4) == 0 || (predio.getCatPredioEdificacionCollection().size() - count) == 0) {
                                        edificaciones = edificaciones.substring(0, edificaciones.length() - 1);
                                        reporteadd = new HashMap<>();
                                        reporteadd.put("nombreSubCarpeta", "/catastro/Ibarra");
                                        reporteadd.put("nombreReporte", "fichaMiduviBloques");
                                        reporteadd.put("predio", predio.getId());
                                        reporteadd.put("edificaciones", edificaciones);
                                        ss.addParametrosReportes(reporteadd);
                                        edificaciones = "";
                                    }

                                }

                            }

                        }
                        ss.setAgregarReporte(true);
                        ss.getReportes().size();
                    }

                    if (ss.getAgregarReporte() != null && ss.getAgregarReporte()) {
                        if (conn == null) {
                            conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                        }
                        for (Map reporte : ss.getReportes()) {
                            System.out.println(">>" + reporte);
                            System.out.println("reporte >>>>> " + reporte);
                            if (reporte.containsKey("nombreSubCarpeta")) {
                                ruta = VerificarCertificado.class.getResourceAsStream("/reportes/" + reporte.get("nombreSubCarpeta") + "/" + reporte.get("nombreReporte") + ".jasper");
                            } else {
                                ruta = VerificarCertificado.class.getResourceAsStream("/reportes/" + ss.getNombreSubCarpeta() + "/" + reporte.get("nombreReporte") + ".jasper");
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

                    JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

                } catch (SQLException ex) {
                    Logger.getLogger(VerificarCertificado.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(VerificarCertificado.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        } else {
            devolverError(response, "Certificado Inválido");
            return;
        }
    }

    protected void devolverError(HttpServletResponse response, String msg) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Verificar Certificado</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + msg + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        } catch (JRException ex) {
            Logger.getLogger(VerificarCertificado.class.getName()).log(Level.SEVERE, null, ex);
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
//        processRequest(request, response);
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
