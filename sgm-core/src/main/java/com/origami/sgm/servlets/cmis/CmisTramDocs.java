/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.servlets.cmis;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.services.interfaces.VentanillaPublica;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.activiti.engine.TaskService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpStatus;
import util.ApplicationContextUtils;
import util.CmisUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@WebServlet(name = "cmisTramDocs", urlPatterns = {"/cmisTramDocs"})
public class CmisTramDocs extends HttpServlet implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private VentanillaPublica ventanilla;
    
    private Logger LOG = Logger.getLogger(CmisTramDocs.class.getName());
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        System.out.println(request.getContentType());
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
    @SuppressWarnings("empty-statement")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String numTra = request.getParameter("tramite");
            if (numTra != null) {
                HistoricoTramites tram = (HistoricoTramites) ventanilla.getManager().find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(numTra)});
                if (tram != null) {
                    String carpetaTramite;
                    if (tram.getTipoTramite().getDisparador() != null) {
                        carpetaTramite = tram.getTipoTramite().getDisparador().getCarpeta();
                    } else {
                        carpetaTramite = tram.getTipoTramite().getCarpeta();
                    }
//                    Future<Integer> uploadFile = ventanilla.uploadFile(request, carpetaTramite, tram.getCarpetaRep(), tram.getId(), tram.getIdProcesoTemp());
//                    if (uploadFile.isDone()) {
                        try {
//                            response.setStatus(uploadFile.get());
                        response.setStatus(uploadFile(request, carpetaTramite, tram.getCarpetaRep(), tram.getId(), tram.getIdProcesoTemp()));
                        } catch (Exception ex) {
                            Logger.getLogger(CmisTramDocs.class.getName()).log(Level.SEVERE, null, ex);
                        }
//                    }
                }
            } else {
                LOG.warning("Parametro es null");
                response.setStatus(HttpStatus.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public Integer uploadFile(final HttpServletRequest request, String carpetaTramite, String carpetaRepo, Long id, String idProcess) {
        FileItemFactory factory = new DiskFileItemFactory(3145728, new File(SisVars.rutaRepotiorioArchivo));
        Folder carpeta;
        TaskService service;
        String url = SisVars.urlServidorAlfrescoPublica + "share/page/site/smbworkflow/document-details?nodeRef=";
        try {
            CmisUtil cmis = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            if (cmis != null) {
                if (id != null) {
                    if (carpetaTramite != null) {
                        carpeta = cmis.getFolder(carpetaTramite);
                        if (carpeta != null) {
                            Folder subCarpeta = cmis.createSubFolder(carpeta, carpetaRepo);
                            if (subCarpeta != null) {
                                LOG.log(Level.INFO, "Obteniendo archivos  {0}", carpetaRepo);
                                ServletFileUpload upload = new ServletFileUpload(factory);
                                List<FileItem> items = null;
                                try {
                                    //Create a progress listener
                                    ProgressListener progress = new ProgressListener() {
                                        private long megaBytes = -1;
                                        
                                        @Override
                                        public void update(long pBytesRead, long pContentLength, int pItems) {
                                            long mBytes = pBytesRead / 1500000;
                                            if (megaBytes == mBytes) {
                                                return;
                                            }
                                            megaBytes = mBytes;
                                            System.out.print("We are currently reading item " + pItems);
                                            if (pContentLength == -1) {
                                                System.out.println(", So far, " + pBytesRead + " bytes have been read.");
                                            } else {
                                                System.out.println(", So far, " + pBytesRead + " of " + pContentLength
                                                        + " bytes have been read.");
                                            }
                                        }
                                    };
                                    upload.setProgressListener(progress);
                                    items = upload.parseRequest(request);
                                } catch (FileUploadException e) {
                                    LOG.log(Level.SEVERE, "Obtener archivos", e);
                                    return HttpStatus.SC_NO_CONTENT;
                                }
                                int count = 1;
                                if (items != null) {
                                    Iterator<FileItem> iter = items.iterator();
                                    while (iter.hasNext()) {
                                        FileItem item = iter.next();
                                        LOG.log(Level.INFO, "File Upload: {0} of {1}, fiel name: {2}, Content Type: {3}, from field: {4}",
                                                new Object[]{count, items.size(), item.getFieldName(), item.getContentType(), item.isFormField()});
//                                    if (item.isFormField()) {
                                        Document doc = cmis.createDocument(subCarpeta, "(" + id + ")" + item.getFieldName(), item.getContentType(), item.get());
                                        if (doc != null) {
                                            service = ventanilla.getEngine().getProcessEngine().getTaskService();
                                            service.createAttachment(item.getContentType(),
                                                    null, idProcess,
                                                    (((new Date()).getTime()) + item.getFieldName()),
                                                    "Archivo Adjunto de tarea " + "(" + idProcess + ")" + item.getFieldName(),
                                                    (url + doc.getId()));
                                        }
//                                    }
                                        count++;
                                    }
                                    cmis = null;
                                    LOG.log(Level.INFO, "Files uploads", id);
                                    return HttpStatus.SC_OK;
                                } else {
                                    return HttpStatus.SC_NO_CONTENT;
                                }
                            } else {
                                LOG.log(Level.WARNING, "Hubo un error al Crear Carpeta del tramite {0}", new Object[]{id});
                                return HttpStatus.SC_NO_CONTENT;
                            }
                        } else {
                            LOG.warning("Buscar Carpeta retorno null");
                            return HttpStatus.SC_NO_CONTENT;
                        }
                    } else {
                        LOG.warning("Carpeta tramite es null");
                        return HttpStatus.SC_NO_CONTENT;
                    }
                } else {
                    LOG.warning("No se encontro Tramite");
                    return HttpStatus.SC_NO_CONTENT;
                }
            } else {
                LOG.warning("Cmis es null");
                return HttpStatus.SC_NO_CONTENT;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "uploadFile()", e);
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
    }
    
}
