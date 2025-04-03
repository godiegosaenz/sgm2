/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import com.origami.sgm.services.ejbs.censocat.FotosService;
import com.origami.sgm.entities.GeDocumentos;
import java.math.BigInteger;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
/**
 *
 * @author Xndy Sanchez
 */
@Named
@RequestScoped
public class UploadDocumento {
    
    
    private Date fechaCreacion;
    private String contentType;
    private Boolean estado = Boolean.TRUE;
    private Long raiz;
    private String nombre;
    private Long documentoId;
    private String identificacion; 
    
    private GeDocumentos documento;
    
    private DiskFileItemFactory factory;

    @javax.inject.Inject
    private DocumentoService documentoService;


    public GeDocumentos saveDocumento() {
        return documento = documentoService.persistDocumento(this.fechaCreacion, this.contentType, this.raiz, this.nombre, this.identificacion, this.documentoId);
    }


    public DiskFileItemFactory getFactory() {
        return factory;
    }

    public void setFactory(DiskFileItemFactory factory) {
        this.factory = factory;
    }


    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getRaiz() {
        return raiz;
    }

    public void setRaiz(Long raiz) {
        this.raiz = raiz;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Long documentoId) {
        this.documentoId = documentoId;
    }

    public DocumentoService getDocumentoService() {
        return documentoService;
    }

    public void setDocumentoService(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public GeDocumentos getDocumento() {
        return documento;
    }

    public void setDocumento(GeDocumentos documento) {
        this.documento = documento;
    }
    
    

    
}
