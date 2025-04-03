/*
 *  Origami Solutions
 */
package com.origami.app;

import com.origami.session.UserSession;
import com.origami.sgm.entities.PubGuiMenu;
import com.origami.sgm.entities.PubGuiMenubar;
import com.origami.sgm.services.interfaces.MenuCacheLocal;
import com.origami.sgm.services.interfaces.MenuServiceLocal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import util.Faces;
import util.JsfUti;

/**
 *
 * @author fernando
 */
@Named
@RequestScoped
public class AppMenu {

    @javax.inject.Inject
    protected MenuServiceLocal menuService;
    @javax.inject.Inject
    protected MenuCacheLocal menuCache;

    @Inject
    protected UserSession userSession;
    private String busqueda;

    private MenuModel model;

    public List<PubGuiMenu> getMenuWorkflow() {
        PubGuiMenubar menubar = menuService.genMenuStruct("workflow.main", userSession);
        return menubar.getMenuListSoyMenubar_byOrden();
    }

    @PostConstruct
    public void initView() {
        if (model == null) {
            model = new DefaultMenuModel();
        }
        llenarMenu(getMenuWorkflow());
    }

    private void llenarMenu(List<PubGuiMenu> menus) {
        int count = 0;
        model = new DefaultMenuModel();
        for (PubGuiMenu menu : menus) {
            DefaultSubMenu subMenu = new DefaultSubMenu(menu.getNombre());
            subMenu.setId("menu_" + count);
//            subMenu.setStyleClass("layout-menubar-container icon-building");
            subMenu.setStyleClass("layout-menubar-container");
            if (tieneSubmenus(menu)) {
                for (PubGuiMenu itemMenu : menu.getMenusHijos_byNumPosicion()) {
                    DefaultMenuItem item = new DefaultMenuItem(itemMenu.getNombre(), "ui-icon-search", menuUrl(itemMenu));
                    //item.setUrl(menuUrl(itemMenu)); // antes: setHref
                    subMenu.addElement(item);
                }
                model.addElement(subMenu);
            }
            count++;
        }
    }

    public void buscarMenu() {
        if (userSession.getVarTemp() == null || userSession.getVarTemp().trim().length() == 0) {
            llenarMenu(getMenuWorkflow());
            return;
        }
        model = null;
        PubGuiMenubar menubar = menuService.buscMenuStruct("workflow.main", userSession, userSession.getVarTemp());
        if (menubar.getMenuListSoyMenubar_byOrden() != null) {
            llenarMenu(menubar.getMenuListSoyMenubar_byOrden());
        } else {
            JsfUti.messageInfo(null, "Información", "No se encontrarón coincidencias");
        }
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

    public Boolean tieneSubmenus(PubGuiMenu menu) {
        if (menu.getMenusHijos_byNumPosicion() != null
                && menu.getMenusHijos_byNumPosicion().size() > 0) {
            return true;
        }
        return false;
    }

    public void clearMenuWorkflow() {
        menuCache.clearCache("workflow.main");
    }

    public String menuUrl(PubGuiMenu menu) {
        if (menu.getHrefUrl() != null && !menu.getHrefUrl().equals("") && !menu.getHrefUrl().equals("#")) {
        	if (menu.getHrefUrl().startsWith("http://")){
        		return menu.getHrefUrl();
        	}
            if (menu.getHrefUrl().startsWith("/")) {
//                return SisVars.urlServidorCompleta + menu.getHrefUrl();
                //return menu.getHrefUrl();
                return Faces.getHostContextUrl() + menu.getHrefUrl();
            } else {
//                return SisVars.urlServidorCompleta + "/" + menu.getHrefUrl();
                return Faces.getHostContextUrl() + "/" + menu.getHrefUrl();
            }
        }
        return "#";
    }

    /**
     * Creates a new instance of AppMenu
     */
    public AppMenu() {
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

}
