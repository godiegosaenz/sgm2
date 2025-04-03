/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

import com.origami.sgm.entities.database.DatabaseEngine;


/**
 *
 * @author Fernando
 */
public abstract class SchemasConfigOracle {
    
    public static final DatabaseEngine DB_ENGINE = DatabaseEngine.ORACLE;
    public static final String LONG_TEXT_TYPE = "CLOB";
    
    public static final String PREFIJO = "sgm_";
    
    public static final String APP1 = PREFIJO + "app";
    public static final String FINANCIERO = APP1;
    public static final String FLOW = APP1;
    public static final String SECUENCIAS = APP1;
    public static final String BITACORA = APP1;
    public static final String BANCOS = APP1;
    public static final String HISTORICO = APP1;
    public static final String LEGACY = APP1;
    public static final String MEJORAS = APP1;
    public static final String TEMPORAL = APP1;
    public static final String AGENDA = APP1;
    public static final String APPLICATION = APP1;

    public static final String APPUNISEQ_DB = "app_uni_seq";
    public static final String APPUNISEQ_ORM = "app_uni_seq";
    
    public static final String FILTER_ESTADO = "1";
    
    public static final Integer SEARCH_ESTADO = 1; 
}
