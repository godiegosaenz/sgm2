
package com.origami.app.wscliente;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10-b140803.1500
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Service1Soap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Service1Soap {



    /**
     * 
     * @param cedularuc
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "Persona", action = "http://tempuri.org/Persona")
    @WebResult(name = "PersonaResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "Persona", targetNamespace = "http://tempuri.org/", className = "com.origami.ws.catastro.Persona")
    @ResponseWrapper(localName = "PersonaResponse", targetNamespace = "http://tempuri.org/", className = "com.origami.ws.catastro.PersonaResponse")
    public String persona(
        @WebParam(name = "Cedularuc", targetNamespace = "http://tempuri.org/")
        String cedularuc);

}
