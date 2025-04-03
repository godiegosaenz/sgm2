
package com.origami.app.wscliente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cedularuc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cedularuc"
})
@XmlRootElement(name = "Persona")
public class Persona {

    @XmlElement(name = "Cedularuc")
    protected String cedularuc;

    /**
     * Obtiene el valor de la propiedad cedularuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCedularuc() {
        return cedularuc;
    }

    /**
     * Define el valor de la propiedad cedularuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCedularuc(String value) {
        this.cedularuc = value;
    }

}
