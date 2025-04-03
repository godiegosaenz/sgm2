/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

/**
 *
 * @author Fernando
 */
@Named
@ApplicationScoped
public class OmegaUploader {

    @javax.inject.Inject
    private OmegaConfigs configs;

    public Boolean streamFile(Long oid, OutputStream out) {
        Boolean flag = false;
        LargeObjectManager lobj = null;
        LargeObject obj = null;
        Connection conn = getDocumentalConnection();
        try {
            // All LargeObject API calls must be within a transaction block
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            try {
                obj = lobj.open(oid, LargeObjectManager.READ);
            } catch (SQLException e) {
                flag = false;
            }
            if (obj != null) {
                flag = true;
            } else {
                return false;
            }
            byte buf[] = new byte[2048];
            int s, tl = 0;
            while ((s = obj.read(buf, 0, 2048)) > 0) {
                out.write(buf, 0, s);
                tl += s;
                out.flush();
            }
            obj.close();
            conn.commit();

        } catch (SQLException | IOException ex) {
            flag = false;
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                flag = false;
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return flag;
    }

    public InputStream streamFile(Long oid) {
        LargeObjectManager lobj = null;
        LargeObject obj = null;
        InputStream is = null;
        Connection conn = getDocumentalConnection();
        try {
            // All LargeObject API calls must be within a transaction block
            if (conn == null) {
                return null;
            }
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            try {
                obj = lobj.open(oid, LargeObjectManager.READ);
            } catch (SQLException e) {
                conn.close();
                return null;
            }
            byte buf[] = obj.read(obj.size());
            InputStream stream = new ByteArrayInputStream(buf);
            is = stream;
//            is = obj.getInputStream();
            obj.close();
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return is;
    }

    public byte[] getByte(Long oid) {
        LargeObjectManager lobj = null;
        LargeObject obj = null;
        byte buf[] = null;
        Connection conn = getDocumentalConnection();
        try {
            // All LargeObject API calls must be within a transaction block
            if (conn == null) {
                return null;
            }
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            try {
                obj = lobj.open(oid, LargeObjectManager.READ);
            } catch (SQLException e) {
                conn.close();
                return null;
            }
            buf = obj.read(obj.size());
//            is = obj.getInputStream();
            obj.close();
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return buf;
    }

    public Long uploadFile(InputStream stream, String nombre, String contentType) {
        Connection conn = getDocumentalConnection();
        Long oid = null;

        try {
            // All LargeObject API calls must be within a transaction block
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            LargeObjectManager lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            // Create a new large object
            oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            // Open the large object for writing
            LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

            // Copy the data from the file to the large object
            byte buf[] = new byte[2048];
            int s, tl = 0;
            while ((s = stream.read(buf, 0, 2048)) > 0) {
                obj.write(buf, 0, s);
                tl += s;
            }

            // Close the large object
            obj.close();

            // Now insert the row into table
            PreparedStatement ps = conn.prepareStatement("INSERT INTO " + configs.getTableName() + " VALUES (?, ?, ?, ?, ?)");
            ps.setLong(1, oid);
            ps.setString(2, nombre);
            ps.setBoolean(3, false);
            ps.setTimestamp(4, new Timestamp((new Date()).getTime()));
            ps.setString(5, contentType);
            ps.executeUpdate();
            ps.close();
            stream.close();

            // Finally, commit the transaction.
            conn.commit();

        } catch (SQLException | IOException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return oid;
    }
    
    public void deleteFile(Long oid) {
        Connection conn = getDocumentalConnection();
        try {
            // All LargeObject API calls must be within a transaction block
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            LargeObjectManager lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            // Delete large object
            lobj.delete(oid);
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Connection getDocumentalConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        props.setProperty("user", configs.getDbuser());
        props.setProperty("password", configs.getDbpass());
        Connection conn = null;
        try {
            if (configs.getDburl() == null) {
                return null;
            }
            conn = DriverManager.getConnection(configs.getDburl(), props);
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public Connection getDocumentalConnectionTemp() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        props.setProperty("user", configs.getDbuserTemp());
        props.setProperty("password", configs.getDbpassTemp());
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(configs.getDburlTemp(), props);
        } catch (SQLException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public void streamFileTemp(Long oid, OutputStream out) {
        Connection conn = getDocumentalConnectionTemp();
        try {
            // All LargeObject API calls must be within a transaction block
            conn.setAutoCommit(false);
            // Get the Large Object Manager to perform operations with
            LargeObjectManager lobj = ((org.postgresql.PGConnection) conn).getLargeObjectAPI();
            LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
            byte buf[] = new byte[2048];
            int s, tl = 0;
            while ((s = obj.read(buf, 0, 2048)) > 0) {
                out.write(buf, 0, s);
                tl += s;
                out.flush();
            }
            obj.close();
            // Finally, commit the transaction.
            conn.commit();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(OmegaUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
