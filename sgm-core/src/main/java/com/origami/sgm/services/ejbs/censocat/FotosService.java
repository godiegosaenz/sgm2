/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import com.origami.sgm.entities.FotoPredio;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author Fernando, CarlosLoorVargas
 */
@Stateless
public class FotosService {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UploadFotoBean uploadFotoBean;

//    public Long persistFoto(Long fileId,
//            String nombre,
//            String contentType,
//            Long predioId) {
//        OrdenDet p1 = (OrdenDet) em.find(OrdenDet.class, predioId);
//        FotoPredio fp1 = new FotoPredio(fileId, nombre, contentType, p1);
//        fp1.setSisEnabled(Boolean.TRUE);
//        em.persist(fp1);
//        em.flush();
//        return fp1.getId();
//    }
    public Long persistFoto(Long fileId, String nombre, String contentType, Long predioId) {

        FotoPredio fp1 = new FotoPredio(fileId, nombre, contentType, null, predioId);
        fp1.setSisEnabled(Boolean.TRUE);
        fp1 = (FotoPredio) manager.persist(fp1);
        return fp1.getId();
    }

    public Long persistFoto(Long fileId, String nombre, String contentType, Long predioId, Long idPredio) {
        FotoPredio fp1 = new FotoPredio(fileId, nombre, contentType, null, predioId, idPredio);
        fp1.setSisEnabled(Boolean.TRUE);
        fp1 = (FotoPredio) manager.persist(fp1);
        return fp1.getId();
    }

    public FotoPredio persistFoto(Long fileId, String nombre, String contentType, Long predioId, Long idPredio, Long bloque) {
        FotoPredio fp1 = new FotoPredio(fileId, nombre, contentType, null, predioId, idPredio, bloque);
        fp1.setSisEnabled(Boolean.TRUE);
        fp1 = (FotoPredio) manager.persist(fp1);
        return fp1;
    }

    public FotoPredio loadFoto(Long id) {
        return manager.find(FotoPredio.class, id);
    }

    protected Boolean genFactory(HttpServletRequest r) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = r.getServletContext();
        File repository = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
        factory.setRepository(repository);
        uploadFotoBean.setFactory(factory);
        return true;
    }

}
