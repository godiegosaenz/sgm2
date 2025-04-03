/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sql;

import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author Angel Navarro
 */
@Stateless
@Interceptors(value = {HibernateEjbInterceptor.class})
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SqlTransaction {

    private static final Logger LOG = Logger.getLogger(SqlTransaction.class.getName());

    /**
     * Returna el primary key de la tabla
     *
     * @param c
     * @param conn Connection sql
     * @param sql
     * @param paramt Parametros
     * @return id de la tabla.
     * @throws java.sql.SQLException
     */
    public Long insertInto(Connection c, String sql, List<Object> paramt) throws SQLException {
        Long x = null;
        try {

            if (c != null) {
                c.setAutoCommit(false);
                PreparedStatement ps = c.prepareStatement(sql);
                int countParamt = 1;
                for (Object object : paramt) {
                    ps.setObject(countParamt, object);
                    countParamt++;
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    x = rs.getLong(1);
                }
//                ps.getConnection().commit();
                c.commit();
                ps.close();
                c.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            c.close();
        }
        return x;
    }

    public Object find(Connection c, String sql, List<Object> paramt) throws SQLException {
        Object ob = null;
        try {
            if (c != null) {
                PreparedStatement ps = c.prepareCall(sql);
                int countParamt = 1;
                for (Object object : paramt) {
                    ps.setObject(countParamt, object);
                    countParamt++;
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ob = rs.getObject(1);
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            c.close();
        }
        return ob;
    }

    public Boolean insertIntoBacth(Connection c, String sql, List<List<Object>> paramt) {
        Boolean x = null;
        try {
            if (c != null) {
                c.setAutoCommit(false);
                PreparedStatement ps = c.prepareStatement(sql);
                for (List<Object> lo : paramt) {
                    int countParamt = 1;
                    for (Object object : lo) {
                        ps.setObject(countParamt, object);
                        countParamt++;
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                c.commit();
                ps.close();
            }
        } catch (SQLException e) {
            try {
                LOG.log(Level.SEVERE, null, e);
                c.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(SqlTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(SqlTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return x;
    }
    
}
