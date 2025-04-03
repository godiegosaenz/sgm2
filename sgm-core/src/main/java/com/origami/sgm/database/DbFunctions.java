/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.database.DatabaseEngine;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Funciones para compatibilidad multi-database en SQL queries
 * @author Fernando
 */
@Named
@ApplicationScoped
@Singleton
@Lock(LockType.READ)
public class DbFunctions {
    
    public String escape(String text){
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.POSTGRESQL){
            return "E'" + text + "'";
        }
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.ORACLE){
            return "'{" + text + "}'";
        }
        return text;
    }
    
    /**
     * Encierra como identificador ej-pgsql: "CatPredio"
     * @param indentifier
     * @return 
     */
    public String identifierEnclose(String indentifier){
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.POSTGRESQL){
            return "\"" + indentifier + "\"";
        }
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.ORACLE){
            return indentifier.toLowerCase();
        }
        return indentifier;
    }
    
    public String boolLiteral(Boolean data){
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.POSTGRESQL){
            return data == true ? "TRUE" : "FALSE";
        }
        if(SchemasConfig.DB_ENGINE == DatabaseEngine.ORACLE){
            return data == true ? "1" : "0";
        }
        return data == true ? "TRUE" : "FALSE";
    }
    
    public String schemaTableIdentifier(String schema, String table){
        if(schema != null && !schema.trim().isEmpty()){
            return identifierEnclose(schema) + "." + identifierEnclose(table);
        }
        else{
            return identifierEnclose(table);
        }
    }
    
}
