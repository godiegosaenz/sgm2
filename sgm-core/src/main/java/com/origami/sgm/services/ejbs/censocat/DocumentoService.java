/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import com.origami.sgm.entities.GeDocumentos;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author Xndy Sanchez
 */
@Stateless
public class DocumentoService {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UploadDocumento uploadDocumentoBean;

    public GeDocumentos persistDocumento(Date fecha, String contentType, Long idEntity, String nombre, String identificacion, Long documentoId) {
        GeDocumentos doc = new GeDocumentos();//(fileId, nombre, contentType, null, predioId, idPredio);
        doc.setFechaCreacion(fecha);
        doc.setFechaHasta(fecha);
        doc.setReforma(Boolean.TRUE);
        doc.setEstado(Boolean.TRUE);
        doc.setVisible(Boolean.TRUE);
        doc.setDescripcion(contentType);
        doc.setRaiz(BigInteger.valueOf(idEntity));
        doc.setNombre(nombre);
        doc.setIdentificacion(identificacion);
        doc.setTipoLegal(BigInteger.valueOf(documentoId));
        doc = (GeDocumentos) manager.persist(doc);
        return doc;
    }

    public GeDocumentos loadDocumento(Long id) {
        return manager.find(GeDocumentos.class, id);
    }
    
    protected Boolean genFactory(HttpServletRequest r) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = r.getServletContext();
        File repository = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
        factory.setRepository(repository);
        uploadDocumentoBean.setFactory(factory);
        return true;
    }

}
