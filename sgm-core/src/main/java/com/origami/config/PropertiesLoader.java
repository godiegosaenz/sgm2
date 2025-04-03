/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import jodd.props.Props;

/**
 *
 * @author fernando
 */
public class PropertiesLoader {

    protected ServletContext sc;

    public void load() {
        try {
            Props props1 = new Props();
            Boolean isDesarrollo = false;
            if (isDesarrollo) { // true desarrollo, false produccion
                props1.load(sc.getResourceAsStream("/WEB-INF/sistema.props"));
            } else {
                props1.load(sc.getResourceAsStream("/WEB-INF/sistema.props.produccion"));
            }
            SisVars.ejbRuta = props1.getValue("sistema.ejbRuta");
            SisVars.rutaRepotiorioArchivo = props1.getValue("sistema.rutaRepotiorioArchivo");

            SisVars.rutaRepotiorioFichas = props1.getValue("sistema.rutaRepotiorioFichas");

            SisVars.rutaRepositorioUsuarios = props1.getValue("sistema.rutaRepositorioUsuarios");
            SisVars.rutaRepositorioUsuariosInternos = props1.getValue("sistema.rutaRepositorioUsuariosInternos");
            SisVars.rutaReportesDinardap = props1.getValue("sistema.rutaReportesDinardap");
//            SisVars.geoserverUrl = props1.getValue("sistema.geoserverUrl");
//            SisVars.geoserverProxyUrl = props1.getValue("sistema.geoserverProxyUrl");
            SisVars.formatoArchivos = props1.getValue("sistema.formatoArchivos");
            SisVars.urlServidorAlfrescoPublica = props1.getValue("sistema.urlServidorAlfrescoPublica");
            SisVars.urlServidorAlfrescoInterna = props1.getValue("sistema.urlServidorAlfrescoInterna");
            SisVars.ipPublica = props1.getValue("sistema.ipPublica");
            SisVars.ipInterna = props1.getValue("sistema.ipInterna");
            SisVars.ipPublicaAlfresco = props1.getValue("sistema.ipPublicaAlfresco");
            SisVars.ipInternaAlfresco = props1.getValue("sistema.ipInternaAlfresco");
            SisVars.correo = props1.getValue("sistema.correo");
            SisVars.pass = props1.getValue("sistema.pass");
            SisVars.smtp_Host = props1.getValue("sistema.smtp_Host");
            SisVars.smtp_Port = props1.getValue("sistema.smtp_Port");
            SisVars.ssl = Boolean.parseBoolean(props1.getValue("sistema.ssl"));
            SisVars.carpetaAdministrativaAlfresco = props1.getValue("sistema.carpetaAdministrativaAlfresco");
            SisVars.logoReportes = props1.getValue("sistema.logoReportes");
            SisVars.sisLogo = props1.getValue("sistema.sisLogo");
            SisVars.sisLogo1 = props1.getValue("sistema.sisLogo1");
            SisVars.varbackground = props1.getValue("sistema.varbackground");
//variables nuevas de SGM
            SisVars.urlPublica = props1.getValue("sistema.urlPublica");
            SisVars.urlServidorPublica = props1.getValue("sistema.urlServidorPublica");
            SisVars.urlServidorCompleta = props1.getValue("sistema.urlServidorCompleta");
            SisVars.correoClienteGenerico = props1.getValue("sistema.correoClienteGenerico");
//variables catdb
            SisVars.driverClass = props1.getValue("catdb.driverClass");
            SisVars.url = props1.getValue("catdb.url");
            SisVars.userName = props1.getValue("catdb.username");
            SisVars.password = props1.getValue("catdb.password");
            SisVars.maxIdleTime = Integer.parseInt(props1.getValue("catdb.maxIdleTime"));
            SisVars.maxPoolSize = Integer.parseInt(props1.getValue("catdb.maxPoolConsize"));
            SisVars.minPoolSize = Integer.parseInt(props1.getValue("catdb.minPoolConSize"));
//variables sgmdocs
            SisVars.driverClassSgmDocs = props1.getValue("sgmdocsdb.driverClass");
            SisVars.urlSgmDocs = props1.getValue("sgmdocsdb.url");
            SisVars.userNameSgmDocs = props1.getValue("sgmdocsdb.username");
            SisVars.passwordSgmDocs = props1.getValue("sgmdocsdb.password");
            SisVars.maxIdleTimeSgmDocs = Integer.parseInt(props1.getValue("sgmdocsdb.maxIdleTime"));
            SisVars.maxPoolSizeSgmDocs = Integer.parseInt(props1.getValue("sgmdocsdb.maxPoolConsize"));
            SisVars.minPoolSizeSgmDocs = Integer.parseInt(props1.getValue("sgmdocsdb.minPoolConSize"));
//ejbs
            SisVars.entityManager = props1.getBaseValue("ejbs.entityManager");
            SisVars.bpmBaseEngine = props1.getBaseValue("ejbs.bpmBaseEngine");
            SisVars.bpmProcessEngine = props1.getBaseValue("ejbs.bpmProcessEngine");
            SisVars.solicitud = props1.getBaseValue("ejbs.solicitud");
            SisVars.datasource = props1.getBaseValue("ejbs.datasource");
//zoning
            SisVars.region = props1.getBaseValue("zoning.region");
//Samdoc integration
            SamdocVars.url = props1.getBaseValue("samdoc.url");
//Censocat Integation
            SisVars.ccdriverClass = props1.getBaseValue("censocat.driverClass");
            SisVars.ccUrl = props1.getBaseValue("censocat.url");
            SisVars.ccUserName = props1.getBaseValue("censocat.username");
            SisVars.ccPassword = props1.getBaseValue("censocat.password");
            SisVars.ccMinPoolSize = Integer.parseInt(props1.getValue("censocat.minPoolConSize"));
            SisVars.ccMaxPoolSize = Integer.parseInt(props1.getBaseValue("censocat.maxPoolConsize"));
            SisVars.ccMaxIdleTime = Integer.parseInt(props1.getBaseValue("censocat.maxIdleTime"));

            SisVars.fechaMaximaRemisionInteres = props1.getBaseValue("sistema.fechaMaximaRemisionInteres");
            SisVars.fechaPublicacionOrdenanza = props1.getBaseValue("sistema.fechaPublicacionOrdenanza");

        } catch (IOException | NumberFormatException e) {
            Logger.getLogger(PropertiesLoader.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public PropertiesLoader(ServletContext sc) {
        this.sc = sc;
    }
}
