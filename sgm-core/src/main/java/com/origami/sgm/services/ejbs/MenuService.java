/*
 *  Origami Solutions
 */
package com.origami.sgm.services.ejbs;

import com.origami.config.RolesEspeciales;
import com.origami.config.TipoAccesoIdent;
import com.origami.session.WfUserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.PubGuiMenu;
import com.origami.sgm.entities.PubGuiMenuRol;
import com.origami.sgm.entities.PubGuiMenuTipoAcceso;
import com.origami.sgm.entities.PubGuiMenubar;
import com.origami.sgm.services.interfaces.MenuCacheLocal;
import com.origami.sgm.services.interfaces.MenuServiceLocal;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import util.HiberUtil;

/**
 *
 * @author fernando
 */
@Stateless
public class MenuService implements MenuServiceLocal {

    @javax.inject.Inject
    private MenuCacheLocal menuCache;
    @javax.inject.Inject
    private Entitymanager manager;

    /**
     * genera estructura de menu correspondiente al nivel de acceso del usuario
     * web
     *
     * @param menubarIdent
     * @param userSession
     * @return
     */
    @Override
    public PubGuiMenubar genMenuStruct(String menubarIdent, WfUserSession userSession) {
        PubGuiMenubar menubar1;
        try {
            menubar1 = menuCache.getMenuBar(menubarIdent);
            genMenuStruct_process(menubar1.getMenuListSoyMenubar_byOrden(), userSession);
        } catch (Exception e) {
            Logger.getLogger(MenuService.class.getName()).log(Level.SEVERE, null, e);
            menubar1 = null;
        }
        return menubar1;
    }

    protected void genMenuStruct_process(Collection<PubGuiMenu> menuList, WfUserSession userSession) {
        LinkedList<PubGuiMenu> listaRemover = new LinkedList<>();
        for (PubGuiMenu cadaMenu : menuList) {
            // si no tiene acceso al menu, cortarlo:
            if (!genMenuStruct_checkAccess(cadaMenu, userSession)) {
                listaRemover.add(cadaMenu);
            } else {
                // recursive
                if (cadaMenu.getMenusHijos_byNumPosicion() != null) {
                    genMenuStruct_process(cadaMenu.getMenusHijos_byNumPosicion(), userSession);
                }
            }
        }
        menuList.removeAll(listaRemover);
    }

    protected Boolean genMenuStruct_checkAccess(PubGuiMenu menu, WfUserSession userSession) {
        for (Long r : userSession.getRoles()) {
            if (r.equals(RolesEspeciales.ADMINISTRADOR)) {
                return true;
            }
        }
        // chekear por tipo de acceso:
        if (menu.getTipoAcceso() == null) {
            return false;
        }

        if (menu.getTipoAcceso().getIdentificador().equals(TipoAccesoIdent.PUBLICO)) {
            return true;
        }
        if (menu.getTipoAcceso().getIdentificador().equals(TipoAccesoIdent.USUARIO)
                && userSession.getUserId() != null) {
            return true;
        }

        if (menu.getTipoAcceso().getIdentificador().equals(TipoAccesoIdent.DIRECTOR)
                && userSession.getEsDirector()) {
            return true;
        }

        // chekear por roles
        if (menu.getPubGuiMenuRolCollection() == null) {
            return false;
        }
        for (PubGuiMenuRol cadaRol : menu.getPubGuiMenuRolCollection()) {
            if (genMenuStruct_compare(cadaRol, userSession)) {
                return true;
            }
        }
        return false;
    }

    protected Boolean genMenuStruct_compare(PubGuiMenuRol rol, WfUserSession userSession) {
        for (Long r : userSession.getRoles()) {
            if ((rol.getRol().equals(r))
                    && (rol.getEsDirector() != null ? (rol.getEsDirector().equals(userSession.getEsDirector())) : true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PubGuiMenubar getMenuBarTree(String menubarIdent) {

        PubGuiMenubar menubar1 = this.getMenuBar(menubarIdent);
        if (menubar1 == null) {
            return null; // si no existe el menubar,, null
        }
        menubar1.setMenuListSoyMenubar_byOrden(this.getMenusOrdenados(menubar1));

        this.getMenuBarTree_initMenuList(menubar1.getMenuListSoyMenubar_byOrden());

        return menubar1;
    }

    private void getMenuBarTree_initMenuList(List<PubGuiMenu> menus) {
        for (PubGuiMenu cadaMenu : menus) {
            cadaMenu = this.menu_associate(cadaMenu);
            this.getMenuBarTree_initmenu(cadaMenu);
            this.menu_init(cadaMenu);
        }
    }

    @Override
    public PubGuiMenu menu_associate(PubGuiMenu menu) {
        Session sess = HiberUtil.getSession();
        if (!sess.contains(menu)) {
            menu = (PubGuiMenu) sess.get(PubGuiMenu.class, menu.getId());

        }

        return menu;
    }

    private void getMenuBarTree_initmenu(PubGuiMenu menu) {
        menu.setMenusHijos_byNumPosicion(this.getMenusOrdenados(menu));
        //menu.getRolesPermitidos().toArray();
        menu.getPubGuiMenuRolCollection().toArray();
        // generacion recursiva:
        this.getMenuBarTree_initMenuList(menu.getMenusHijos_byNumPosicion());
    }

    @Override
    public void menu_init(PubGuiMenu menu) {
        Hibernate.initialize(menu.getMenuPadre());
        Hibernate.initialize(menu.getTipoAcceso());
        Hibernate.initialize(menu.getPubGuiMenuRolCollection());
    }

    @Override
    public PubGuiMenu getMenu(Integer idMenu) {
        if (idMenu == null) {
            return null;
        }
        return manager.find(PubGuiMenu.class, idMenu);
//        Session sess = HiberUtil.getSession();
//
//        Query q1 = sess.createQuery("SELECT me1 FROM PubGuiMenu me1 WHERE me1.id=:id").setMaxResults(1);
//        q1.setInteger("id", idMenu);
//
//        PubGuiMenu menuResult = (PubGuiMenu) q1.uniqueResult();
//        Hibernate.initialize(menuResult);
//        return menuResult;
    }

    @Override
    public PubGuiMenubar getMenuBar(String menubarIdent) {
        return (PubGuiMenubar) manager.find(Querys.getMenuBar, new String[]{"ident"}, new Object[]{menubarIdent});
//        Session sess = HiberUtil.getSession();
//        Query q1 = sess.createQuery("SELECT mb1 FROM PubGuiMenubar mb1 WHERE mb1.identificador = :ident").setMaxResults(1);
//        q1.setString("ident", menubarIdent);
//
//        PubGuiMenubar result = (PubGuiMenubar) q1.uniqueResult();
//        Hibernate.initialize(result);
//        return result;
    }

    @Override
    public List<PubGuiMenu> getMenusOrdenados(PubGuiMenu menuPadre) {
        return manager.findAllByEntities(Querys.getMenusOrdenadosPadre, new String[]{"menuPadre"}, new Object[]{menuPadre});
//        Session sess = HiberUtil.getSession();
//        Query q1 = sess.createQuery("SELECT m1 FROM PubGuiMenu m1 WHERE m1.menuPadre = :menuPadre ORDER BY m1.numPosicion ASC ");
//        q1.setEntity("menuPadre", menuPadre);
//        List<PubGuiMenu> result = q1.list();
//        Hibernate.initialize(result);
//        return result;
    }

    @Override
    public List<PubGuiMenu> getMenusOrdenados(PubGuiMenubar menuBar) {
        return manager.findAllByEntities(Querys.getMenusOrdenadosBar, new String[]{"menuBar"}, new Object[]{menuBar});
//        List result = null;
//        Session sess = HiberUtil.getSession();
//        Query q1 = sess.createQuery("SELECT m1 FROM PubGuiMenu m1 WHERE m1.menubar = :menuBar ORDER BY m1.numPosicion ASC ");
//        q1.setEntity("menuBar", menuBar);
//        result = q1.list();
//        Hibernate.initialize(result);
//        return result;
    }

    //***************** Metodos para mantenimiento de menu *******************//
    @Override
    public PubGuiMenu guardar(PubGuiMenu menu) {
        try {
            if (menu.getId() == null) {
                menu = (PubGuiMenu) manager.persist(menu);
            } else {
                manager.persist(menu);
            }
        } catch (Exception e) {
            return null;
        }
//        menu_init(menu);
        return menu;
    }

    @Override
    public List<PubGuiMenubar> getMenuBarList() {
        return manager.findAllObjectOrder(PubGuiMenubar.class, new String[]{"identificador"}, Boolean.TRUE);
    }

    @Override
    public List<PubGuiMenuTipoAcceso> getMenuAccesoList() {
        return manager.findAllObjectOrder(PubGuiMenuTipoAcceso.class, new String[]{"identificador"}, Boolean.TRUE);
    }

    @Override
    public List<PubGuiMenuRol> getAccesosMenuRol(PubGuiMenu menu) {
        Map<String, Object> map = new HashMap<>();
        map.put("menu", menu);
        return manager.findObjectByParameterList(PubGuiMenuRol.class, map);
    }

    @Override
    public AclRol getRol(Long idRol) {
        return manager.find(AclRol.class, idRol);
    }

    @Override
    public List<GeDepartamento> getDepts() {
        return manager.findAllOrdEntCopy(GeDepartamento.class, new String[]{"nombre"}, new Boolean[]{true});
    }

    @Override
    public List<AclRol> getRolesDepartamento(Long id) {
        if (id == null) {
            return manager.findAll(Querys.getListAclRolByDepNull);
        } else {
            return manager.findAll(Querys.getListAclRolByDep, new String[]{"departamento"}, new Object[]{id});
        }
    }

    @Override
    public Boolean borrarMenu(PubGuiMenu menu) {
        HiberUtil.newTransaction();
        return manager.delete(menu);
    }

    @Override
    public Boolean borrarMenu(PubGuiMenuRol menuRol) {
        HiberUtil.newTransaction();
        return manager.delete(menuRol);
    }

    @Override
    public PubGuiMenuRol guardarPubGuiMenuRol(PubGuiMenuRol PermisoMenu) {
        if (PermisoMenu == null) {
            return null;
        }

        if (PermisoMenu.getId() == null) {
            return (PubGuiMenuRol) manager.persist(PermisoMenu);
        } else {
            manager.persist(PermisoMenu);
            return PermisoMenu;
        }
    }

    @Override
    public PubGuiMenubar buscMenuStruct(String menubarIdent, WfUserSession userSession, String busqueda) {
        PubGuiMenubar menubar1;
        List<PubGuiMenu> menusTemp = new ArrayList<>();
        try {
            menubar1 = menuCache.getMenuBar(menubarIdent);
            for (PubGuiMenu menu : menubar1.getMenuListSoyMenubar_byOrden()) {
                if (menu.getNombre().toUpperCase().contains(busqueda.toUpperCase())) {
                    menusTemp.add(menu);
                } else if (menu.getMenusHijos_byNumPosicion() != null && menu.getMenusHijos_byNumPosicion().size() > 0) {
                    for (PubGuiMenu hijos : menu.getMenusHijos_byNumPosicion()) {
                        if(hijos.getNombre().toUpperCase().contains(busqueda.toUpperCase())){
                            if(!menusTemp.contains(menu))
                                menusTemp.add(menu);
                        }
                    }
                }
            }
            menubar1.setMenuListSoyMenubar_byOrden(menusTemp);
            genMenuStruct_process(menubar1.getMenuListSoyMenubar_byOrden(), userSession);
        } catch (Exception e) {
            Logger.getLogger(MenuService.class.getName()).log(Level.SEVERE, null, e);
            menubar1 = null;
        }
        return menubar1;
    }
}
