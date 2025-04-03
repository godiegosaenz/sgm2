/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sql;

import com.origami.config.SisVars;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anyelo
 */

public class ConexionSql{
    
    public static Connection getConnection() {
        Connection conexion = null;
        try {
            Class.forName(SisVars.sqlServerDriverClass); 
            String url = "jdbc:sqlserver://" + SisVars.ipSqlServer + ";databaseName=" + SisVars.nameDBSqlServer +
                    ";user=" + SisVars.userSqlServer + ";password=" + SisVars.passwordSqlServer + ";";
            conexion = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(ConexionSql.class.getName()).log(Level.SEVERE, null, e);
        }
        return conexion;
    }
    
    public Connection getConnection(String usuario, String password, String dbUrl) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        props.setProperty("user", usuario);
        props.setProperty("password", password);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, props);
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
}
