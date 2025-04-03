/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.cmis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import util.ApplicationContextUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import util.CmisUtil;

/**
 *
 * @author Joao Sanga
 */
@WebServlet(name = "CmisServlet", urlPatterns = {"/CmisServlet"})
public class CmisServlet extends HttpServlet {
    
    private CmisUtil alfrescoUtils;
    
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
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
        
        String ciRuc;
        InputStream is = null;
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload;
        List<FileItem> items;
        alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");        
        
        try {
            
            ciRuc = (String)request.getParameter("dni");
            upload = new ServletFileUpload(factory);
            items = upload.parseRequest(request);
            
            if(ciRuc.length() == 10 || ciRuc.length() == 13){
                Folder carpeta = alfrescoUtils.getFolder("ventanilla");
                
                carpeta = alfrescoUtils.getFolder(ciRuc);
                if(carpeta == null){
                    carpeta = alfrescoUtils.createFolder(alfrescoUtils.getFolder("ventanilla"), ciRuc);
                }
                
                if(carpeta == null){
                    return;
                }
                
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if(!item.isFormField()){
                        is = item.getInputStream();
                        
                        alfrescoUtils.createDocument(carpeta, item.getFieldName(), item.getContentType(), IOUtils.toByteArray(is));
                    }
                }
                
                response.setStatus(SC_OK);
            }
        } catch (Exception e) {
            Logger.getLogger(CmisServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            alfrescoUtils = null;
            if(is != null)
                is.close();
        }

    } 
}
