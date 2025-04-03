/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets;

import com.origami.sgm.services.ejbs.censocat.*;
import java.io.IOException;
import java.io.OutputStream;
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
 * @author Angel Navarro
 */
@WebServlet(name = "ShowDocuments", urlPatterns = {"/showDocuments"})
public class ShowDocuments extends HttpServlet {

    @Inject
    private OmegaUploader omegaUploader;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // id de foto
        if (request.getParameter("idDoc") != null && NumberUtils.isNumber(request.getParameter("idDoc"))
                && request.getParameter("type") != null) {
            String type = null;
            if (request.getParameter("type").equalsIgnoreCase("pdf")) {
                type = "application/pdf";
            } else {
                type = request.getParameter("type");
            }

            Long fotoId = Long.parseLong(request.getParameter("idDoc"));
            // content-type
            response.setContentType(type);
            response.addHeader("Content-disposition", "filename=" + request.getParameter("idDoc") + "." + MimeTypes.getMimeType(type));
            // escribir el output-stream del response
            OutputStream os = response.getOutputStream();
            omegaUploader.streamFile(fotoId, os);
            //osAux = os;
            os.close();
        }
    }

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
    }// </editor-fold>

}
