/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.restful.models.Acceso;
import com.origami.sgm.restful.models.Departamentos;
import com.origami.sgm.restful.models.Roles;
import com.origami.sgm.util.EjbsCaller;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author CarlosLoorVargas
 */
@Path("acceso")
@Produces({"application/Json", "text/xml"})
public class WSAcceso implements Serializable {

    private static final long serialVersionUID = 1L;

    @GET
    @Path("/auth/userName/{userName}/password/{password}/get")
    public Acceso getAutenticacion(@PathParam("userName") String user, @PathParam("password") String pass) {
        Acceso a = new Acceso();
        AclUser usuario = null;
        Collection<Roles> roles;
        Roles rol;
        Collection<Departamentos> departamentos;
        Departamentos d;
        if (user != null && pass != null) {
            usuario = (AclUser) EjbsCaller.getTransactionManager().find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{user.trim(), pass.trim()});
            if (usuario != null) {
                a.setId(usuario.getId());
                a.setUser(user);
                if (usuario.getEnte() != null) {
                    a.setDescripcion(usuario.getEnte().getApellidos() + " " + usuario.getEnte().getNombres());
                    a.setIdentificacion(usuario.getEnte().getCiRuc());
                }
                if (!usuario.getAclRolCollection().isEmpty()) {
                    roles = new ArrayList<>();
                    departamentos = new ArrayList<>();
                    for (AclRol r : usuario.getAclRolCollection()) {
                        rol = new Roles();
                        rol.setId(r.getId());
                        rol.setDescripcion(r.getNombre());
                        if (r.getDepartamento() != null) {
                            d = new Departamentos();
                            d.setId(r.getDepartamento().getId());
                            d.setDescripcion(r.getDepartamento().getNombre());
                            departamentos.add(d);
                        }
                        roles.add(rol);
                    }
                    a.setDepartamentos(departamentos);
                    a.setRoles(roles);
                    a.setEstado(true);
                    a.setMensaje("Ingreso exitoso");
                } else {
                    a.setDepartamentos(null);
                    a.setRoles(null);
                }
            } else {
                a.setMensaje("Usuario no existe, o las credenciales son incorrectas");
                a.setEstado(false);
            }
        } else {
            a.setMensaje("Debe ingresar las credenciales respectivas: Usuario y Clave");
            a.setEstado(false);
        }
        return a;
    }

}
