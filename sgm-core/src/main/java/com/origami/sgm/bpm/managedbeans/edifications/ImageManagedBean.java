/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.faces.bean.ApplicationScoped;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Envia a buscar la entity obtenida por el id, y busca el el campo que sea de
 * tipo byte[] obtiene el valor del campo y lo envia a convertir a
 * StreamedContent. <br/>
 *
 * Ejemplo de uso:
 * <p:graphicImage value="#{imageManagedBean.imageAllEntity}" height="250px" width="250px" rendered="#{solicitudNormasConstruccion.hayImagen}">
 * <f:param name="id" value="#{solicitudNormasConstruccion.normaConstruccion.id}"/>
 * <f:param name="nameEntity" value="CatNormasConstruccion"/>
 * </p:graphicImage>
 *
 * Recibe como parametros:<br/>
 *
 * @param id - id de la tabla a buscar.
 * @param nameEntity - Nombre de entity a buscar.<br/>
 *
 * @return @throws IOException cuando el campo que sea de tipo byte[] sea nullo.
 * @throws ClassNotFoundException Salta la excepcion cuando el valor el nombre
 * de la clase no es correcta o no encuenta la clase en el paquete.
 *
 *
 * @author Angel Navarro
 *
 *
 */
@Named(value = "imageManagedBean")
@ApplicationScoped
public class ImageManagedBean {

    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    /**
     * Obtiene el parametro "id" y envia a realizar la consulta a la tabla
     * CatNormasConstruccion y toma el valor del campo "imafoto" y lo convierter
     * a StreamedContent.
     *
     * @return StreamedContent
     * @throws IOException Salta la excepcion cuando el campo es null
     */
    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String imageId = context.getExternalContext().getRequestParameterMap().get("id");
            CatNormasConstruccion n = normasServices.getCatNormasConstruccion(Long.parseLong(imageId));
            byte[] image = n.getImafoto();
            return new DefaultStreamedContent(new ByteArrayInputStream(image));
        }
    }
    public StreamedContent image(byte[] buffer) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            try {
                if(buffer == null){
                    System.out.println("buffer null...");
                    return new DefaultStreamedContent();
                }
                byte[] image = buffer;
                return new DefaultStreamedContent(new ByteArrayInputStream(image), "image/jpg");
            } catch (Exception e) {
                Logger.getLogger(ImageManagedBean.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return new DefaultStreamedContent();
    }

    /**
     * Envia a buscar la entity obtenida por el id, y busca el el campo que sea
     * de tipo byte[] obtiene el valor del campo y lo envia a convertir a
     * StreamedContent. <br/>
     *
     * Ejemplo de uso:
     * 
     * <p:graphicImage value="#{imageManagedBean.imageAllEntity}" height="250px" width="250px" rendered="#{solicitudNormasConstruccion.hayImagen}">
     * <f:param name="id" value="#{solicitudNormasConstruccion.normaConstruccion.id}"/>
     * <f:param name="nameEntity" value="CatNormasConstruccion"/>
     * </p:graphicImage>
     * 
     * Recibe como parametros:<br/>
     *
     * @param id id de la tabla a buscar.
     * @param nameEntity Nombre de entity a buscar.<br/>
     *
     * @return @throws IOException cuando el campo que sea de tipo byte[] sea
     * nullo.
     * @throws ClassNotFoundException Salta la excepcion cuando el nombre de la
     * clase no es correcta o no encuenta la clase en el paquete
     * "com.origami.sgm.entities".
     */
    public StreamedContent getImageAllEntity() throws IOException, ClassNotFoundException {
        FacesContext context = FacesContext.getCurrentInstance();
        byte[] image = null;
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            String nameEntity = context.getExternalContext().getRequestParameterMap().get("nameEntity");

            Object entityObject = normasServices.getEntityById(nameEntity, Long.parseLong(id));
            Field[] fields = entityObject.getClass().getDeclaredFields(); // Se obtiene todos los campos que tiene la clase
            for (Field field : fields) {
                field.setAccessible(true); // Permite acceder a los campos private o protected
                if (field.getType().equals(byte[].class)) { // comparamos que el tipo de dato del campo sea byte[]
                    try {
                        image = (byte[]) field.get(entityObject); // Se obtiene el valor que tiene el campo
                        break;
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(ImageManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return new DefaultStreamedContent(new ByteArrayInputStream(image));
    }

    public ImageManagedBean() {
    }

}
