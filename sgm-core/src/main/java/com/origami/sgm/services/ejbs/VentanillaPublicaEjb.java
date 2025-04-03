/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.sgm.services.interfaces.VentanillaPublica;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpStatus;
import util.ApplicationContextUtils;
import util.CmisUtil;
import org.activiti.engine.TaskService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Anyelo
 */
@Stateless(name = "ventanillaService")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class VentanillaPublicaEjb implements VentanillaPublica {

    private static final Logger LOG = Logger.getLogger(VentanillaPublicaEjb.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private BpmBaseEngine engine;

    @Override
    public Boolean activarCuenta(Long id, String codigo) {
//        RpubUsuario u;
        try {
            /*u = (RpubUsuario) manager.find(Querys.getRpubUsuarioById, new String[]{"id"}, new Object[]{id});

            if (u == null) {
                return false;
            }

            if (u.getCodigoActivacion().equals(codigo)) {
                u.setCuentaActivada(Boolean.TRUE);
                manager.persist(u);
                return true;
            }*/
        } catch (Exception e) {
            Logger.getLogger(VentanillaPublicaEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return false;
    }

    @Asynchronous
    @Override
    public Future<Integer> uploadFile(final HttpServletRequest request, String carpetaTramite, String carpetaRepo, Long id, String idProcess) {
        FileItemFactory factory = new DiskFileItemFactory();
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
                            Folder subCarpeta = null;
                            Folder folder = cmis.getFolder(carpetaTramite);
                            if (folder != null) {
                                subCarpeta = folder;
                            } else {
                                subCarpeta = cmis.createSubFolder(carpeta, carpetaRepo);
                            }
                            if (subCarpeta != null) {
                                LOG.log(Level.INFO, "Obteniendo archivos  {0}", carpetaRepo);
                                ServletFileUpload upload = new ServletFileUpload(factory);
                                List<FileItem> items = null;
                                try {
                                    items = upload.parseRequest(request);
                                    //Create a progress listener
                                    upload.setProgressListener(progressListener());
                                } catch (FileUploadException e) {
                                    LOG.log(Level.SEVERE, "Obtener archivos", e);
                                    return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
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
                                            service = engine.getProcessEngine().getTaskService();
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
                                    return new AsyncResult<>(HttpStatus.SC_OK);
                                } else {
                                    return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
                                }
                            } else {
                                LOG.log(Level.WARNING, "Hubo un error al Crear Carpeta del tramite {0}", new Object[]{id});
                                return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
                            }
                        } else {
                            LOG.warning("Buscar Carpeta retorno null");
                            return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
                        }
                    } else {
                        LOG.warning("Carpeta tramite es null");
                        return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
                    }
                } else {
                    LOG.warning("No se encontro Tramite");
                    return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
                }
            } else {
                LOG.warning("Cmis es null");
                return new AsyncResult<>(HttpStatus.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "uploadFile()", e);
            return new AsyncResult<>(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ProgressListener progressListener() {
        ProgressListener progressListener = new ProgressListener() {
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
        return progressListener;
    }

    @Override
    public BpmBaseEngine getEngine() {
        return engine;
    }

    @Override
    public Entitymanager getManager() {
        return manager;
    }

}
