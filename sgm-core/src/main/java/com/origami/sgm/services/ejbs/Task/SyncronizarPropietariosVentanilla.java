/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.Task;

import com.origami.config.SisVars;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.models.Usuario;
import com.origami.sql.SqlTransaction;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Singleton
@Lock(LockType.READ)
public class SyncronizarPropietariosVentanilla {

    private static final Logger LOG = Logger.getLogger(SyncronizarPropietariosVentanilla.class.getName());

    @javax.inject.Inject
    private SqlTransaction transaction;

    @Lock(LockType.READ)
    @AccessTimeout(value = -1)
//    @Schedule(dayOfWeek = "*", month = "*", hour = "10", dayOfMonth = "*", year = "*", minute = "35", second = "0", persistent = false)
    public void doWork() {
        System.out.println("Timer event syncronizar propietarios ventanilla: " + new java.util.Date());
        if (SisVars.PROVINCIA == 2 && SisVars.CANTON == 5) {
            syncronizarPropietariosConVentanilla();
            System.out.println("Verificar Los contactos que no tienen cuenta: " + new java.util.Date());
            verificarContactos();
        }

        System.out.println("Timer event finish: " + new java.util.Date());
    }

    protected Connection getConnection() {
        try {
            Class.forName(SisVars.ccdriverClass);
//            Connection cx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ventanilla_virtual", SisVars.ccUserName, SisVars.ccPassword);
            Connection cx = DriverManager.getConnection("jdbc:postgresql://192.168.101.239:5432/ventanilla_virtual", SisVars.ccUserName, SisVars.ccPassword);
            return cx;
        } catch (ClassNotFoundException | SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return null;
    }

    private void syncronizarPropietariosConVentanilla() {
        try {
            List<String> identificacionPropietarios = getIdentificacion();
            if (Utils.isEmpty(identificacionPropietarios)) {
                return;
            }
            List<CatPredioPropietario> prop = getPropietarios(identificacionPropietarios);
            if (Utils.isEmpty(prop)) {
                return;
            }
            System.out.println("Propietarios a syncronizar: " + prop.size());
            int count = 1;
            for (CatPredioPropietario cpp : prop) {
                Long temp = existeContacto(cpp.getEnte().getCiRuc());
                Long id = null;
                if (temp == null) {
                    id = persistContacto(cpp.getEnte());
                }
                if ((count % 100) == 0) {
                    System.out.println("Procesados: " + count);
                }
                count++;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    private List<String> getIdentificacion() {
        List<String> ls = null;
        try {
            Connection c = null;
            try {
                c = getConnection();
            } catch (Exception e) {
                System.out.println("Error Al iniciar conexion con la ventanilla...");
                return null;
            }
            if (c != null) {
                PreparedStatement ps = c.prepareCall("SELECT identificacion FROM app.contacto");
                ResultSet rs = ps.executeQuery();
                ls = new ArrayList<>();
                while (rs.next()) {
                    ls.add(rs.getString("identificacion"));
                }
                rs.close();
                ps.close();
                c.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
        return ls;
    }

    private List<CatPredioPropietario> getPropietarios(List props) {
        List<CatPredioPropietario> list = new ArrayList<>();
        try {
            Session sess;
            HiberUtil.requireTransaction();
            sess = HiberUtil.getSession();
            Criteria q = sess.createCriteria(CatPredioPropietario.class).createAlias("ente", "pente")
                    .add(Restrictions.not(Restrictions.in("pente.ciRuc", props)));
            list = q.list();
            list.size();
            Hibernate.initialize(list);
        } catch (HibernateException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return list;
    }

    private Long persistContacto(CatEnte ente) {
        Long x = null;
        try {
            if (ente.getCiRuc() == null) {
                return null;
            }
            Connection c = getConnection();
            c.setAutoCommit(false);
            if (c != null) {
                PreparedStatement ps = c.prepareStatement("INSERT INTO app.contacto(nacionalidad, tipo_persona, tipo_ident, identificacion, descripcion, "
                        + "            fecha, email_p, telf_p, usr_cre, "
                        + "            estado, fec_cre) "
                        + "    VALUES (?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, "
                        + "            ?, ?);");
                if (ente.getNacionalidad() != null && ente.getNacionalidad().getId() == 1L) {
                    ps.setLong(1, 1L);
                } else {
                    ps.setLong(1, 1L);
                }
                if (ente.getEsPersona()) {
                    ps.setLong(2, 1L);
                } else {
                    ps.setLong(2, 2L);
                }
                switch (ente.getCiRuc().length()) {
                    case 10:
                        ps.setLong(3, 1L);
                        break;
                    case 13:
                        ps.setLong(3, 2L);
                        break;
                    default:
                        ps.setLong(3, 1L);
                        break;
                }
                ps.setString(4, ente.getCiRuc());
                ps.setString(5, ente.getNombreCompleto());
                if (ente.getFechaNacimiento() != null) {
                    ps.setDate(6, new Date(ente.getFechaNacimiento().getTime()));
                } else {
                    ps.setDate(6, null);
                }
                ps.setString(7, ente.getEmails());
                ps.setString(8, ente.getTelefonos());
                ps.setString(9, "admin");
                ps.setBoolean(10, true);
                ps.setTimestamp(11, new Timestamp(new java.util.Date().getTime()));

                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    x = rs.getLong(2);
                }
                ps.getConnection().commit();
                ps.close();
                c.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return x;
    }

    private Long persistUser(Long id, String ciRuc) {
        Long x = null;
        String sql = "INSERT INTO seg.usuario("
                + "            contacto, app, usuario, clave, sk, usr_cre, estado, fec_cre)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            if (exiteCuenta(ciRuc)) {
                return null;
            }
            List<Object> paramt = new ArrayList<Object>();
            paramt.add(id);
            paramt.add(1L);
            paramt.add(ciRuc);
            paramt.add(Utils.encode(ciRuc));
            paramt.add(Utils.getRandomUUID());
            paramt.add("admin");
            paramt.add(true);
            paramt.add(new Timestamp(new java.util.Date().getTime()));
            Connection c = getConnection();
            x = transaction.insertInto(getConnection(), sql, paramt);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return x;
    }

    private Long existeContacto(String ciRuc) {
        Long id = null;
        try {
            List<Object> paramt = new ArrayList<Object>();
            paramt.add(ciRuc);
            String sql = "SELECT id FROM app.contacto WHERE identificacion=?";
            id = (Long) transaction.find(getConnection(), sql, paramt);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return id;
    }

    private Boolean exiteCuenta(String ciRuc) {
        Boolean id = false;
        try {
            List<Object> paramt = new ArrayList<Object>();
            paramt.add(ciRuc);
            String sql = "SELECT id FROM seg.usuario WHERE usuario=?";
            Long idu = (Long) transaction.find(getConnection(), sql, paramt);
            id = idu != null;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return id;
    }

    private void verificarContactos() {
        try {
            Long x = null;
            String sql = "INSERT INTO seg.usuario("
                    + "            contacto, app, usuario, clave, sk, usr_cre, estado, fec_cre)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            List<Usuario> usuarios = getUsuarios();
            System.out.println("Usuarios:  " + usuarios.size());
            List<List<Object>> listBac = new ArrayList<List<Object>>();
            List<Object> paramt = new ArrayList<Object>();
            int count = 1;
            for (Usuario usuario : usuarios) {
                try {
                    paramt = new ArrayList<Object>();
                    paramt.add(usuario.getId());
                    paramt.add(1L);
                    paramt.add(usuario.getUsuario());
                    paramt.add(Utils.encode(usuario.getUsuario()));
                    paramt.add(Utils.getRandomUUID());
                    paramt.add("admin");
                    paramt.add(true);
                    paramt.add(new Timestamp(new java.util.Date().getTime()));
                    listBac.add(paramt);
                    if ((count % 100) == 0) {
                        Boolean ok = transaction.insertIntoBacth(getConnection(), sql, listBac);
                        if (ok) {
                            listBac = new ArrayList<>();
                        }
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error Insertar en Bacth", e);
                }
            }
            Boolean ok = transaction.insertIntoBacth(getConnection(), sql, listBac);
            try {
                if (ok != null && ok) {
                    listBac = new ArrayList<>();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    private List<Usuario> getUsuarios() {
        List<Usuario> usuarios = null;
        try {
            Connection c = null;
            try {
                c = getConnection();
            } catch (Exception e) {
                System.out.println("Error Al iniciar conexion con la ventanilla...");
                return null;
            }
            if (c != null) {
                StringBuffer query = new StringBuffer("SELECT c.id, c.identificacion FROM app.contacto c ")
                        .append("WHERE c.identificacion NOT IN (")
                        .append("SELECT usuario FROM seg.usuario")
                        .append(")");
                PreparedStatement ps = c.prepareCall(query.toString());
                ResultSet rs = ps.executeQuery();
                usuarios = new ArrayList<>();
                if (rs.next()) {
                    while (rs.next()) {
                        Usuario s = new Usuario();
                        s.setId(rs.getLong(1));
                        s.setUsuario(rs.getString(2));
                        usuarios.add(s);
                    }
                }
                rs.close();
                ps.close();
                c.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
        return usuarios;
    }
}
