/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import com.origami.config.SisVars;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.services.interfaces.censocat.CensoServices;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless
public class CensoCatEjb implements CensoServices {

    private static final Logger log = Logger.getLogger(CensoCatEjb.class.getName());

    protected Connection getConnection() {
        try {
            Class.forName(SisVars.ccdriverClass);
            Connection cx = DriverManager.getConnection(SisVars.ccUrl, SisVars.ccUserName, SisVars.ccPassword);
            return cx;
        } catch (ClassNotFoundException | SQLException e) {
            log.log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public CatPredio procesarPredio(CatPredio predioCensado, CatPredio Act) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
