/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.restful.models.DatosPersonales;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.origami.app.cdi.jpa.hibernate.HibernateAddClassesEvent;
import com.origami.app.cdi.jpa.hibernate.HibernateFactory;
import com.origami.app.cdi.jpa.hibernate.UnitQualifier;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import util.Utils;

/**
 *
 * @author supergold
 */
@Named
@RequestScoped
@Path(value = "consultas/entes/")
@Produces({"application/Json", "text/xml"})
public class ConsultasEntes implements Serializable {

    private static final Logger LOG = Logger.getLogger(ConsultasEntes.class.getName());

    private static final long serialVersionUID = 1L;
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    @Inject @UnitQualifier("sgm")
    private HibernateFactory hibernateFact;

    @GET
    @Path(value = "datosEnte/dni/{dni}")
    public DatosPersonales getDatosTramite(@PathParam(value = "dni") String ds) {
        if (!Utils.validateCCRuc(ds)) {
            System.out.println("Cedula invalida");
//            return null;
        }
        DatosPersonales datos = null;
        try {
            CatEnte prop = permisoServices.getCatEnteByCiRuc(ds);
            if (prop == null) {
                System.out.println("prop es null");
                return null;
            }
            if (prop.getEstado().equalsIgnoreCase("I") && prop.getEstado().equalsIgnoreCase("F")) {
                System.out.println("prop.getEstado() ");
                return null;
            }
            datos = getDatosPersonales(prop);
            if (prop.getRepresentanteLegal() != null) {
                CatEnte resp = permisoServices.getFichaServices().getCatEnteById(prop.getRepresentanteLegal().longValue());
                if (resp != null) {
                    datos.setRepresentante(getDatosPersonales(resp));
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Consulta de Ente WS", e);
            return null;
        }
        return datos;
    }

    private DatosPersonales getDatosPersonales(CatEnte prop) {
        DatosPersonales  datos = new DatosPersonales();
        datos.setIdentificacion(prop.getCiRuc());
        datos.setDescripcion(prop.getNombreCompleto().trim());
        datos.setDireccion(prop.getDireccion());
        Long tipoIdent = null;
        if (prop.getCiRuc().length() == 10) {
            tipoIdent = 1L;
        } else if (prop.getCiRuc().length() == 13) {
            tipoIdent = 2L;
        }
        datos.setTipoIdentificacion(tipoIdent);
        Long tipoPersona = null;
        if (prop.getEsPersona()) {
            tipoPersona = 1L;
        } else {
            tipoPersona = 2L;
        }
        datos.setTipoPersona(tipoPersona);
        datos.setNacionalidad(1L);
        if (prop.getEnteTelefonoCollection() != null && !prop.getEnteTelefonoCollection().isEmpty()) {
            if (prop.getEnteTelefonoCollection().size() > 1) {
                datos.setCelular(prop.getEnteTelefonoCollection().get(0).getTelefono());
                datos.setConvencional(prop.getEnteTelefonoCollection().get(1).getTelefono());
            } else {
                datos.setCelular(prop.getEnteTelefonoCollection().get(0).getTelefono());
            }
        }
        if (prop.getEnteCorreoCollection() != null && !prop.getEnteCorreoCollection().isEmpty()) {
            if (prop.getEnteTelefonoCollection().size() > 1) {
                datos.setEmailP(prop.getEnteCorreoCollection().get(0).getEmail());
                datos.setEmailS(prop.getEnteCorreoCollection().get(1).getEmail());
            } else {
                datos.setEmailP(prop.getEnteCorreoCollection().get(0).getEmail());
            }
        }
        return datos;
    }
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path(value = "createSchemas/oracle")
    public String getDDLSchemas(){
        StringBuilder sb1 = new StringBuilder();
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        for(String cdDdl : hibernateFact.getCfg().generateSchemaCreationScript(Oracle10gDialect.getDialect(props))){
            sb1.append(cdDdl).append("; \n");
        }
        return sb1.toString();
    }
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path(value = "createSchemas/pg")
    public String getDDLSchemasPg(){
        StringBuilder sb1 = new StringBuilder();
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL82Dialect");
        for(String cdDdl : hibernateFact.getCfg().generateSchemaCreationScript(PostgreSQL82Dialect.getDialect(props))){
            sb1.append(cdDdl).append("; \n");
        }
        return sb1.toString();
    }
    
}
