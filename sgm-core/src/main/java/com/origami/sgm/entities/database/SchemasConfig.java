/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.database;

/**
 *
 * @author Fernando
 */
public abstract class SchemasConfig {

    public static final DatabaseEngine DB_ENGINE = DatabaseEngine.POSTGRESQL;
    public static final String LONG_TEXT_TYPE = "text";
    
    public static final String PREFIJO = "sgm_";
    
    public static final String APP1 = PREFIJO + "app";
    public static final String FINANCIERO = PREFIJO + "financiero";
    public static final String FLOW = PREFIJO + "flow";
    public static final String SECUENCIAS = PREFIJO + "secuencias";
    public static final String BITACORA = PREFIJO + "bitacora";
    public static final String BANCOS = PREFIJO + "bancos";
    public static final String HISTORICO = PREFIJO + "historico";
    public static final String LEGACY = "legacy";
    public static final String MEJORAS = PREFIJO + "mejoras";
    public static final String TEMPORAL = PREFIJO + "temporal";
    public static final String AGENDA = PREFIJO + "agenda";
    public static final String APPLICATION = PREFIJO + "application";

    public static final String APPUNISEQ_DB = "app_uni_seq";
    public static final String APPUNISEQ_ORM = "app_uni_seq";
    
    public static final String FILTER_ESTADO = "'TRUE'";
    
    public static final Boolean SEARCH_ESTADO = Boolean.TRUE; 
    
    
}
