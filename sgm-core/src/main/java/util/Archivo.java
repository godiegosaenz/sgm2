/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;

/**
 *
 * @author User
 */
public class Archivo implements Serializable {
    public static final long serialVersionUID = -1891512391744456924L;
        
    private String nombre;
    private String ruta;
    private String tipo;
    private String url;
    private String descripcion;
    private Long idValidadorDocumento;
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdValidadorDocumento() {
        return idValidadorDocumento;
    }

    public void setIdValidadorDocumento(Long idValidadorDocumento) {
        this.idValidadorDocumento = idValidadorDocumento;
    }
    
}
