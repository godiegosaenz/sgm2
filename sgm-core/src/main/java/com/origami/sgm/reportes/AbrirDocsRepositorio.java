/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import util.ApplicationContextUtils;
import util.CmisUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@WebServlet(name = "AbrirDocsRepositorio", urlPatterns = {"/AbrirDocsRepositorio"})
public class AbrirDocsRepositorio extends HttpServlet {

    private CmisUtil cmis;
    private byte[] bytes = null;
    private int read = 0;
    private OutputStream responseOutputStream;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            
            cmis = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            if (cmis != null) {
                ContentStream fileStream = cmis.getDocument(request.getParameter("id"));
                response.addHeader("Content-Disposition", "inline; filename=" + fileStream.getFileName());
                response.setContentType(fileStream.getMimeType());
                response.setContentLength((int) fileStream.getLength());
                responseOutputStream = response.getOutputStream();
                bytes = new byte[1024];
                while ((read = fileStream.getStream().read(bytes)) != -1) {
                    responseOutputStream.write(bytes, 0, read);
                }
                responseOutputStream.flush();
            }
        } catch (Exception e) {
            Logger.getLogger(AbrirDocsRepositorio.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            if(responseOutputStream != null)
                responseOutputStream.close();
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
