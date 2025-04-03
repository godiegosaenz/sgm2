/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.util.EjbsCaller;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import util.MimeTypes;

/**
 *
 * @author CarlosLoorVargas
 */
@WebServlet(name = "DescargarDocsRepositorioReporte", urlPatterns = {"/DescargarDocumento"})
public class DescargarDocsRepositorioReporte extends HttpServlet {

    private byte[] bytes = null;
    private int read = 0;
    @Inject
    private OmegaUploader omegaUploader;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStream responseOutputStream = null;
                
        try {
            if (request.getParameter("id") != null && NumberUtils.isNumber(request.getParameter("id"))) {
                String id = request.getParameter("id");
                HistoricoReporteTramite r = EjbsCaller.getTransactionManager().find(HistoricoReporteTramite.class, Long.valueOf(id));
                String type = "application/pdf";
                Long fotoId = Long.parseLong(r.getUrl());
                response.addHeader("Content-Disposition", "attachment; filename=" + r.getNombreReporte() + "." + MimeTypes.getMimeType(type));
                response.setContentType(type);
                bytes = omegaUploader.getByte(fotoId);
                response.setContentLength(bytes.length);
                responseOutputStream = response.getOutputStream();
                responseOutputStream.write(bytes, 0, bytes.length);
            }
        } catch (IOException | NumberFormatException e) {
            Logger.getLogger(DescargarDocsRepositorioReporte.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            if(responseOutputStream!=null){
                responseOutputStream.flush();
                responseOutputStream.close();
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
    }

}
