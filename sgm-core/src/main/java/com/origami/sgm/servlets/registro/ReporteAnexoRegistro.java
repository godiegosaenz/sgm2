/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.registro;

import com.origami.config.SisVars;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Anyelo
 */
@WebServlet(name = "ReporteAnexoRegistro", urlPatterns = {"/ReporteAnexoRegistro"})
public class ReporteAnexoRegistro extends HttpServlet {

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

        String name = request.getParameter("name");
        String fecha = request.getParameter("fecha");
        BufferedInputStream input;
        BufferedOutputStream output;
        int length;
        if (name != null && fecha != null) {
            String nombreReporte = "";
            switch (name) {
                case "Anexo1":
                    nombreReporte = "RP_" + fecha + "_SAMBORONDON";
                    break;
                case "Anexo2":
                    nombreReporte = "RM_MC_" + fecha + "_SAMBORONDON";
                    break;
                case "Anexo3":
                    nombreReporte = "RM_MS_" + fecha + "_SAMBORONDON";
                    break;
            }
            String ruta = SisVars.rutaReportesDinardap + nombreReporte + ".txt";
            response.setContentType("text/plain");
            response.setCharacterEncoding("windows-1252");
            response.addHeader("Content-Disposition", "attachment; filename=" + nombreReporte + ".txt");
            try {
                File file = new File(ruta);
                input = new BufferedInputStream(new FileInputStream(file));
                output = new BufferedOutputStream(response.getOutputStream());
                byte[] buffer = new byte[8192];
                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
                input.close();
                output.close();
            } catch (Exception e) {
                Logger.getLogger(ReporteAnexoRegistro.class.getName()).log(Level.SEVERE, null, e);
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
