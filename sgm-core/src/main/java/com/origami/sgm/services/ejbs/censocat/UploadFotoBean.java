/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import com.origami.sgm.entities.FotoPredio;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author Fernando
 */
@Named
@RequestScoped
public class UploadFotoBean {

    private String predioUUID;
    private Long predioId;
    private Long ordenId;
    private Long fileId;
    private String nombre;
    private String contentType;
    private Long idPredio;
    private Long bloque;

    private Long fotoPredioId;

    private DiskFileItemFactory factory;

    @javax.inject.Inject
    private FotosService fotosService;

    public void saveFotoId() {
        fotoPredioId = fotosService.persistFoto(fileId, nombre, contentType, predioId);
    }
    
    public void saveFoto() {
        fotoPredioId = fotosService.persistFoto(fileId, nombre, contentType, predioId, idPredio);
    }
    
    public FotoPredio saveFotoBloque() {
        return fotosService.persistFoto(fileId, nombre, contentType, predioId, idPredio, bloque);
    }

    public UploadFotoBean() {
    }

    public Long getPredioId() {
        return predioId;
    }

    public void setPredioId(Long predioId) {
        this.predioId = predioId;
    }

    public DiskFileItemFactory getFactory() {
        return factory;
    }

    public void setFactory(DiskFileItemFactory factory) {
        this.factory = factory;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFotoPredioId() {
        return fotoPredioId;
    }

    public String getPredioUUID() {
        return predioUUID;
    }

    public void setPredioUUID(String predioUUID) {
        this.predioUUID = predioUUID;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public Long getBloque() {
        return bloque;
    }

    public void setBloque(Long bloque) {
        this.bloque = bloque;
    }

}
