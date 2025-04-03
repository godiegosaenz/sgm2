/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.app.AppMenu;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.PubGuiMenu;
import com.origami.sgm.entities.PubGuiMenuRol;
import com.origami.sgm.entities.PubGuiMenuTipoAcceso;
import com.origami.sgm.entities.PubGuiMenubar;
import com.origami.sgm.services.interfaces.MenuServiceLocal;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class MenuRol implements Serializable {

    private static final Logger LOG = Logger.getLogger(MenuRol.class.getName());

    @javax.inject.Inject
    private MenuServiceLocal menuServ;
    @Inject
    private AppMenu appMenu;

    private List<PubGuiMenu> padre;
    private PubGuiMenu menu;
    private PubGuiMenuRol permisoMenu;
    private Map<String, Object> entradas;

    @PostConstruct
    public void initView() {
        padre = menuServ.getMenusOrdenados(new PubGuiMenubar(1));
        entradas = new HashMap<>();
        permisoMenu = new PubGuiMenuRol();
    }

    /*Menus*/
    public void nuevoMenu(Boolean esPadre, PubGuiMenu menu) {

        entradas.put("esPadre", esPadre);
        entradas.put("editar", false);
        if (esPadre) {
            entradas.put("header", "Ingreso de Menu Padre");
            entradas.put("menu", new PubGuiMenu(null, null, padre.size() + 1));
        } else {
            entradas.put("header", "Ingreso de Menu Hijo para: ".concat(menu.getNombre()));
            this.menu = menu;
            entradas.put("menu", new PubGuiMenu(null, null, getHijos(menu).size() + 1));
        }
        JsfUti.executeJS("PF('dlgMenu').show()");
        JsfUti.update("agregarM:dlgMenu");
        JsfUti.update("agregarM:frmMenu");
    }

    public void editarMenu(PubGuiMenu menu) {
        Boolean esPadre = menu.getMenubar() != null;
        entradas.put("menu", menu);
        entradas.put("esPadre", esPadre);
        entradas.put("editar", true);
        if (esPadre) {
            entradas.put("header", "Edición de Menu Padre");
        } else {
            entradas.put("header", "Edición de Menu Hijo");
            this.menu = menu.getMenuPadre();
        }
        JsfUti.executeJS("PF('dlgMenu').show()");
        JsfUti.update("agregarM:dlgMenu");
        JsfUti.update("agregarM:frmMenu");
    }

    public void guardar() {
        try {
            PubGuiMenu m = (PubGuiMenu) entradas.get("menu");
            Boolean p = (Boolean) entradas.get("esPadre");
            if (m.getNombre().trim() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe Ingresar el Nombre del Menu");
                return;
            }
            if (m.getNumPosicion() == 0) {
                JsfUti.messageError(null, "Advertencia", "Debe la posicion mayor a uno");
                return;
            }
            if (m.getTipoAcceso() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe seleccionar el tipo de acceso");
                return;
            }
            if (p) {
                if (m.getMenubar() == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe debe selecionar el menu bar");
                    return;
                }
            }
            if (!p) {
                m.setMenuPadre(this.menu);
            }
            m = menuServ.guardar(m);
            if (m == null) {
                JsfUti.messageError(null, "Error", "Ocurrio un problema en la transacción");
            } else {
                JsfUti.executeJS("PF('dlgMenu').hide()");
                String men;

                if (p) {
                    men = "Menu ";
                } else {
                    men = "SubMenu ";
                }
                if (p) {
                    JsfUti.messageInfo(null, "Información", men + "actualizado Correctamente.");
                    entradas.put("editar", false);
                } else {
                    JsfUti.messageInfo(null, "Información", men + "ingresado Correctamente.");
                }
                entradas.put("menu", new PubGuiMenu());
                entradas.put("esPadre", false);
                this.menu = null;
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "Guardar Menu", e);
        }
    }

    /*permiso de menu*/
    public void verPermiso(PubGuiMenu menu) {
        try {
            entradas.put("menu", (PubGuiMenu) EntityBeanCopy.clone(menu));
            entradas.put("permisosMenu", (List<PubGuiMenuRol>) EntityBeanCopy.clone(menuServ.getAccesosMenuRol(menu)));

            JsfUti.executeJS("PF('dlgPermiso').show()");
            JsfUti.update("dlgPermiso");
            JsfUti.update("frmPermiso");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void eliminar(PubGuiMenu menu) {
        try {
            boolean borrados = false;
            boolean hijo = false;

            Collection<PubGuiMenu> pubGuiMenuCollection = menu.getPubGuiMenuCollection();
            menu.setPubGuiMenuCollection(null);
            if (Utils.isNotEmpty((List<?>) pubGuiMenuCollection)) {
                for (PubGuiMenu pubGuiMenu : pubGuiMenuCollection) {
                    borrados = this.menuServ.borrarMenu(pubGuiMenu);
                }
            } else {
                borrados = true;
                hijo = true;
            }
            if (borrados) {
                this.menuServ.borrarMenu(menu);
                this.appMenu.clearMenuWorkflow();
                this.appMenu.getMenuWorkflow();
                padre = menuServ.getMenusOrdenados(new PubGuiMenubar(1));
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public String getNombreRol(Long idRol) {
        if (idRol == null) {
            return "";
        }
        return menuServ.getRol(idRol).getNombre();
    }

    public List<GeDepartamento> getDepartamentos() {
        return menuServ.getDepts();
    }

    public List<AclRol> getRoles() {
        try {
            if (entradas.get("dep") == null) {
                return menuServ.getRolesDepartamento(null);
            } else {
                String dp = entradas.get("dep").toString();
                Long idDep = Long.valueOf(dp);
                return menuServ.getRolesDepartamento(idDep);
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
        return null;
    }

    public void nuevoPermiso() {
        entradas.put("dep", 0L);
        this.permisoMenu = new PubGuiMenuRol();
        JsfUti.executeJS("PF('dlgRol').show()");
        JsfUti.update("frmRol");
    }

    public void editarPermiso(PubGuiMenuRol permiso) {
        try {
            this.permisoMenu = permiso;
            AclRol rol = menuServ.getRol(permiso.getRol());
            if (rol.getDepartamento() != null) {
                entradas.put("dep", rol.getDepartamento().getId());
            } else {
                entradas.put("dep", 0L);
            }
            getRoles();
            JsfUti.executeJS("PF('dlgRol').show()");
            JsfUti.update("frmRol");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void guardarAccesoRol() {
        try {
            if (this.permisoMenu.getRol() == null) {
                JsfUti.messageError(null, "Error", "Debe seleccionar el rol");
                return;
            }

            List<PubGuiMenuRol> accesos = (List<PubGuiMenuRol>) entradas.get("permisosMenu");
            Boolean edicion = false;
            if (this.permisoMenu.getId() == null) {
                for (PubGuiMenuRol acceso : accesos) {
                    if (acceso.getRol().equals(this.permisoMenu.getRol())) {
                        JsfUti.messageError(null, "Error", "Rol ya esta agregado");
                        return;
                    }
                }
            } else {
                edicion = true;
            }
            this.permisoMenu.setMenu((PubGuiMenu) entradas.get("menu"));
            this.permisoMenu = menuServ.guardarPubGuiMenuRol(this.permisoMenu);
            if (!edicion) {
                accesos.add(this.permisoMenu);
            }
            JsfUti.executeJS("PF('dlgRol').hide()");
            JsfUti.update("frmPermiso");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void elimiarAccesoRol(PubGuiMenuRol acceso) {
        try {
            List<PubGuiMenuRol> accesos = (List<PubGuiMenuRol>) entradas.get("permisosMenu");
            accesos.remove(acceso);
            menuServ.borrarMenu(acceso);
            entradas.put("permisosMenu", accesos);
            JsfUti.update("frmPermiso");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public List<PubGuiMenuTipoAcceso> getAccesos() {
        return (List<PubGuiMenuTipoAcceso>) EntityBeanCopy.clone(menuServ.getMenuAccesoList());
    }

    public List<PubGuiMenubar> getMenuBars() {
        return (List<PubGuiMenubar>) EntityBeanCopy.clone(menuServ.getMenuBarList());
    }

    public List<PubGuiMenu> getHijos(PubGuiMenu padre) {
        return menuServ.getMenusOrdenados(padre);
    }

    public List<PubGuiMenu> getPadre() {
        return padre;
    }

    public void setPadre(List<PubGuiMenu> padre) {
        this.padre = padre;
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public PubGuiMenu getMenu() {
        return menu;
    }

    public void setMenu(PubGuiMenu menu) {
        this.menu = menu;
    }

    public void setPermisoMenu(PubGuiMenuRol permisoMenu) {
        this.permisoMenu = permisoMenu;
    }

    public PubGuiMenuRol getPermisoMenu() {
        return permisoMenu;
    }

}
