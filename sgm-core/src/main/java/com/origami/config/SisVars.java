/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

/**
 * Configuraciones del sistema
 * @author Origami
 */

public abstract class SisVars {

    public static String urlbase;// = "/asdf/geoPortalSni/";
    public static String facesUrl = "/faces";
    public static String urlbaseFaces;// = "/asdf/geoPortalSni/faces/";
    public static String urlbaseFacesSinBarra;// = "/asdf/geoPortalSni/faces";
    public static String ejbRuta;// = "java:global/JavaServerFaces/";
    public static String formatoArchivos;// ="/(\\.|\\/)(gif|jpe?g|png|pdf|xlsx|docx|xlsm|dwg|shp|doc|xls|ppt|pptx|tif|txt)$/";
    public static String geoserverUrl;
    public static String geoserverProxyUrl;
    public static String urlServidorWorkFlow;// ="http://localhost:8084/JavaServerFaces";
    public static String urlServidorCatastro;// ="http://localhost:8084/smbCatastro";
    public static String urlServidorSGM;// ="http://localhost:8084/sgm";
    public static String correoClienteGenerico;
    public static String rutaReportesDinardap;
    public static String rutaRepotiorioArchivo;
    public static String rutaRepotiorioFichas;//= "C:\\servers_files\\cmbFlow\\repo\\";
    public static String rutaRepositorioUsuarios;
    public static String rutaRepositorioUsuariosInternos;
    public static String urlServidorCatastroPublica;
    public static String urlServidorWorkFlowPublica;
    public static String urlServidorWorkFlowPublicaNotificacion;
    public static String urlServerWorkFlowPublic;
    public static String urlWorkFlowVentanilla;
    public static String urlServidorAlfrescoPublica;
    public static String urlServidorAlfrescoInterna;
    public static String ipPublica;
    public static String ipInterna;
    public static String ipPublicaAlfresco;
    public static String ipInternaAlfresco;
    public static String ipSqlServer;
    public static String nameDBSqlServer;
    public static String userSqlServer;
    public static String passwordSqlServer;
    public static String smtp_Host;
    public static String smtp_Port;
    public static String correo;
    public static String pass;
    public static boolean ssl;
    public static String carpetaAdministrativaAlfresco;
    public static String sqlServerDriverClass;
    public static String sqlServerUrl;
    public static String logoReportes;
    public static String sisLogo;
    public static String sisLogo1;
    public static String varbackground;

    //variables rutas archivos de propiedades
    public static String driverClass;
    public static String url;
    public static String userName;
    public static String password;
    public static int minPoolSize = 1;
    public static int maxPoolSize = 1;
    public static int maxIdleTime = 1;

    //variables  sgmdoc
    public static String driverClassSgmDocs;
    public static String urlSgmDocs;
    public static String userNameSgmDocs;
    public static String passwordSgmDocs;
    public static int minPoolSizeSgmDocs = 1;
    public static int maxPoolSizeSgmDocs = 1;
    public static int maxIdleTimeSgmDocs = 1;

    //variables nuevas de SGM
    public static String urlPublica;
    public static String urlServidorPublica;
    public static String urlServidorCompleta;

    //JNDI EJB
    public static String entityManager;
    public static String bpmBaseEngine;
    public static String bpmProcessEngine;
    public static String datasource;
    public static String solicitud;

    //zoning
    public static String region;

    //conexion a base postgresql anterior smbcatas
    public static String driverClassDbOld;
    public static String urlDbOld;
    public static String userNameDbOld;
    public static String passwordDbOld;
    public static int minPoolSizeDbOld = 1;
    public static int maxPoolSizeDbOld = 1;
    public static int maxIdleTimeDbOld = 1;

    //conexion a base postgresql anterior activiti
    public static String driverClassDbOldAct;
    public static String urlDbOldAct;
    public static String userNameDbOldAct;
    public static String passwordDbOldAct;
    public static int minPoolSizeDbOldAct = 1;
    public static int maxPoolSizeDbOldAct = 1;
    public static int maxIdleTimeDbOldAct = 1;

    //conexion a base postgresql censoCat
    public static String ccdriverClass;
    public static String ccUrl;
    public static String ccUserName;
    public static String ccPassword;
    public static int ccMinPoolSize = 1;
    public static int ccMaxPoolSize = 1;
    public static int ccMaxIdleTime = 1;
    
    //PPLICATION EMPRESA 
    public static Short PROVINCIA;
    public static Short CANTON;
    public static String NOMBREMUNICIPIO;
    public static String URLPLANOIMAGENPREDIO;
    public static String NOMBRECANTON;
    public static String NOMBREPROVINCIA;

    public static String REPORTES = "reportes";
    public static String URL_REPORTES;
    
    public static String sitioWebMunicipio;
    
     //VARIBLE REMISION DE INTERES
    public static String fechaMaximaRemisionInteres;
    public static String fechaPublicacionOrdenanza;
    
}
