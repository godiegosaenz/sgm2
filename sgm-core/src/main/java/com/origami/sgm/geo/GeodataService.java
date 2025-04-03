/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.geo;

import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import util.HiberUtil;

/**
 *
 * @author Fernando
 */
@Singleton
@Lock(LockType.READ)
@Interceptors(value = {HibernateEjbInterceptor.class})
public class GeodataService {

    private static final Logger LOG = Logger.getLogger(GeodataService.class.getName());
    protected static final Integer ENVELOPE_EXTENDED = 100;
    protected static final Integer ENVELOPE_COLINDANTES = 50;

    protected Session getSession() {
        return HiberUtil.getSession();
    }

    @Resource
    private String geoserverUrl;

    @javax.inject.Inject
    protected GeodataIdentifiers geoIdent;
    @javax.inject.Inject
    protected GeoserverIdentifiers geoserverIdnt;

    protected Boolean existSchema() {
        Session sess = getSession();
        SQLQuery schemaExist = sess.createSQLQuery("SELECT count(*) FROM pg_namespace pn INNER JOIN pg_tables pt ON pn.nspname = pt.schemaname WHERE nspname = :schema AND pt.tablename=:tabla");
        schemaExist.setString("schema", geoIdent.getGeodataSchema());
        schemaExist.setString("tabla", geoIdent.getTblGeoPredio());
        Object result = schemaExist.uniqueResult();
        if (result != null) {
            Boolean name = Integer.valueOf(result.toString()) != 0;
            System.out.println("Existe Schema: " + name);
            return name;
        } else {
            System.out.println("resultado vacio Schema: ");
            return false;
        }
    }

    public Bbox getBboxColindantesByClaveCatastral(String clave) {
        Bbox bbox1 = null;
        Integer exist = null;
        try {
            Session sess = getSession();
            if (existSchema()) {
                SQLQuery sq1 = sess.createSQLQuery("SELECT ST_XMax(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_COLINDANTES + ")) AS xmax, "
                        + "ST_XMin(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_COLINDANTES + ")) AS xmin,"
                        + "ST_YMax(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_COLINDANTES + ")) AS ymax, "
                        + "ST_YMin(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_COLINDANTES + ")) AS ymin "
                        + "FROM " + geoIdent.getGeoPredio() + " gp1 "
                        + "WHERE gp1.clave_catastral = :clave");
//            SQLQuery sq1 = sess.createSQLQuery("SELECT ST_XMax(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS xmax, "
//                    + "ST_XMin(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS xmin, "
//                    + "ST_YMax(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS ymax, "
//                    + "ST_YMin(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS ymin "
//                    + "FROM geodata.geo_predio AS gp1 "
//                    + "WHERE gp1.clave_catastral = :clave");
                sq1.setResultTransformer(Transformers.aliasToBean(Bbox.class));
                sq1.setString("clave", clave);
                sq1.setMaxResults(1);
                bbox1 = (Bbox) sq1.uniqueResult();
            } else {
                bbox1 = null;
            }
        } catch (HibernateException hibernateException) {
            //System.out.println("No Existe: sgm_geodata.geo_predio");
            LOG.log(Level.SEVERE, hibernateException.getMessage(), hibernateException);
        }
        return bbox1;
    }

    public Bbox getBboxExtendedByClaveCatastral(String clave) {
        Bbox bbox1 = null;
        Integer exist = null;
        try {
            Session sess = getSession();
            if (existSchema()) {
                SQLQuery sq1 = sess.createSQLQuery("SELECT ST_XMax(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_EXTENDED + ")) AS xmax, "
                        + "ST_XMin(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_EXTENDED + ")) AS xmin,"
                        + "ST_YMax(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_EXTENDED + ")) AS ymax, "
                        + "ST_YMin(ST_Expand(ST_Envelope(gp1.geom)," + GeodataService.ENVELOPE_EXTENDED + ")) AS ymin "
                        + "FROM " + geoIdent.getGeoPredio() + " gp1 "
                        + "WHERE gp1.clave_catastral = :clave");
//            SQLQuery sq1 = sess.createSQLQuery("SELECT ST_XMax(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS xmax, "
//                    + "ST_XMin(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS xmin, "
//                    + "ST_YMax(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS ymax, "
//                    + "ST_YMin(ST_Intersection(ST_Envelope(gp1.geom), ST_Envelope(gp1.geom))) AS ymin "
//                    + "FROM geodata.geo_predio AS gp1 "
//                    + "WHERE gp1.codigo = :clave AND gp1.habilitado=true");
                sq1.setResultTransformer(Transformers.aliasToBean(Bbox.class));
                sq1.setString("clave", clave);
                sq1.setMaxResults(1);
                bbox1 = (Bbox) sq1.uniqueResult();
            } else {
                bbox1 = null;
            }
        } catch (HibernateException hibernateException) {
            //System.out.println("No Existe: sgm_geodata.geo_predio");
            LOG.log(Level.SEVERE, hibernateException.getMessage(), hibernateException);
        }
        return bbox1;
    }

    public String getUrlColindantesImage(String clave, Integer ancho, Integer alto) {
        Bbox bbox1 = null;
        try {
            bbox1 = getBboxExtendedByClaveCatastral(clave);
        } catch (Exception ex) {
            //Logger.getLogger(GeodataService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (bbox1 == null) {
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder(geoserverUrl)
                .append("sanmiguel/wms?service=WMS&version=1.1.0&request=GetMap&layers=")
                .append(geoserverIdnt.concatNs(geoserverIdnt.getColindanteLayer()))
                .append("&styles=&bbox=")
                .append(bbox1.getXmin()).append(",")
                .append(bbox1.getYmin()).append(",").append(bbox1.getXmax()).append(",")
                .append(bbox1.getYmax())
                .append("&width=768")
                .append("&height=450")
                .append("&srs=EPSG:32717&format=image%2Fpng&env=clave:")
                .append(clave);
        String result = urlBuilder.toString();
        LOG.log(Level.INFO, "Colindantes: {0}", result);
        return result;
    }

    public Bbox getBboxByClaveCatastral(String clave) {
        Bbox bbox1 = null;
        Integer exist = null;
        try {
            Session sess = getSession();
            if (existSchema()) {
                SQLQuery sq1 = sess.createSQLQuery("SELECT ST_XMax(ST_Expand(ST_Envelope(gp1.geom),30)) AS xmax, ST_XMin(ST_Expand(ST_Envelope(gp1.geom),30)) AS xmin,"
                        + "ST_YMax(ST_Expand(ST_Envelope(gp1.geom),30)) AS ymax, ST_YMin(ST_Expand(ST_Envelope(gp1.geom),30)) AS ymin "
                        + "FROM " + geoIdent.getGeoPredio() + " gp1 "
                        + "WHERE gp1.clave_catastral = :clave");
                sq1.setResultTransformer(Transformers.aliasToBean(Bbox.class));
                sq1.setString("clave", clave);
                sq1.setMaxResults(1);
                bbox1 = (Bbox) sq1.uniqueResult();
            } else {
                bbox1 = null;
            }

        } catch (HibernateException hibernateException) {
            LOG.log(Level.SEVERE, "Clave: " + clave, hibernateException);
            //System.out.println("No Existe: sgm_geodata.geo_predio");
        }
        return bbox1;
    }

    public String getUrlPredioImage(String clave, Integer ancho, Integer alto) {
        Bbox bbox1 = null;
        try {
            bbox1 = getBboxByClaveCatastral(clave);
        } catch (Exception ex) {
            Logger.getLogger(GeodataService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (bbox1 == null) {
            return null;
        }

        StringBuilder urlBuilder = new StringBuilder(geoserverUrl)
                .append("sanmiguel/wms?service=WMS&version=1.1.0&request=GetMap&layers=")
                .append(geoserverIdnt.concatNs(geoserverIdnt.getPredioSeleccionadoLayer()))
                .append("&styles=&bbox=")
                .append(bbox1.getXmin()).append(",")
                .append(bbox1.getYmin()).append(",").append(bbox1.getXmax()).append(",")
                .append(bbox1.getYmax())
                .append("&width=768")
                .append("&height=450")
                .append("&srs=EPSG:32717&format=image%2Fpng&env=clave:")
                .append(clave);
        String result = urlBuilder.toString();
        LOG.info(result);
        return result;
    }

    public String getGeoserverUrl() {
        return geoserverUrl;
    }

//    public Bbox getBboxByClaveCatastralSSH(String clave) {
//        Bbox bbox1 = null;
//        try {
//            com.jcraft.jsch.Session s = new JSch().getSession("root", "190.57.138.220");
//            s.setPassword("orIgamI98bsC");
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            s.setConfig(config);
//            s.connect();
//            int setPortForwardingL = s.setPortForwardingL(5555, "192.168.1.93", 5432);
//            PreparedStatement ps = null;
//            ResultSet rs = null;
//            Connection c = getConnection(5555);
//            ps = c.prepareCall("SELECT ST_XMax(ST_Expand(ST_Envelope(gp1.geom),30)) AS xmax, ST_XMin(ST_Expand(ST_Envelope(gp1.geom),30)) AS xmin,"
//                    + "ST_YMax(ST_Expand(ST_Envelope(gp1.geom),30)) AS ymax, ST_YMin(ST_Expand(ST_Envelope(gp1.geom),30)) AS ymin "
//                    + "FROM sgm_geodata.sgm_geo_predio AS gp1 "
//                    + "WHERE gp1.codigo = ?");
//            ps.setString(1, clave);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                bbox1 = new Bbox();
//                bbox1.setXmax(rs.getDouble("xmax"));
//                bbox1.setXmin(rs.getDouble("xmin"));
//                bbox1.setYmax(rs.getDouble("ymax"));
//                bbox1.setYmin(rs.getDouble("ymin"));
//            }
//            rs.close();
//            ps.close();
//            c.close();
//            s.disconnect();
//        } catch (JSchException | SQLException ex) {
//            Logger.getLogger(GeodataService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return bbox1;
//    }
    protected Connection getConnection(int port) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection cx = DriverManager.getConnection("jdbc:postgresql://localhost:" + port + "/sanmiguel", "sisapp", "sis98");
            return cx;
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(GeodataService.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

}
