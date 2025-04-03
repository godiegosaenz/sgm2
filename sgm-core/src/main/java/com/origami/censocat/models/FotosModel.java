/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.models;

import com.google.gson.annotations.Expose;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Angel Navarro
 */
public class FotosModel implements Serializable {

    private static final Logger LOG = Logger.getLogger(FotosModel.class.getName());

    @Expose
    private String id;
    @Expose
    private String image;

    public FotosModel(String foto) {
        this.id = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getImageFromBase64() {
        try {
            if (image == null) {
                return null;
            }
            return javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al obtener image con id " + id, e);
            return null;
        }
    }

    public StreamedContent getImageStreamed() {
        if (image != null) {
            System.out.println("Image length: " + image.length());
            System.out.println("Byte image length: " + getImageFromBase64().length);
            return new DefaultStreamedContent(new ByteArrayInputStream(getImageFromBase64()), "image/jpg");
        } else {
            System.out.println("Image null");
            return null;
        }
    }

    @Override
    public String toString() {
        return "com.origami.censocat.models.FotosModel [" + id + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return this.id != null && this.id.equals(obj);
        }
        if (!(obj instanceof FotosModel)) {
            return false;
        }
        FotosModel other = (FotosModel) obj;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
