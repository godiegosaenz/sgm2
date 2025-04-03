/*
 *  Origami Solutions
 */

package com.origami.sgm.services.interfaces;
import com.origami.session.WfUserSession;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.PubGuiMenu;
import com.origami.sgm.entities.PubGuiMenuRol;
import com.origami.sgm.entities.PubGuiMenuTipoAcceso;
import com.origami.sgm.entities.PubGuiMenubar;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author fernando
 */
@Local
public interface MenuServiceLocal {


    

    public List<PubGuiMenu> getMenusOrdenados(PubGuiMenu menuPadre);

    public List<PubGuiMenu> getMenusOrdenados(PubGuiMenubar menuBar);

    public PubGuiMenubar getMenuBarTree(String menubarIdent);

    public PubGuiMenubar getMenuBar(String menubarIdent);

    public PubGuiMenu getMenu(Integer idMenu);

    public void menu_init(PubGuiMenu menu);

    public PubGuiMenu menu_associate(PubGuiMenu menu);

    public PubGuiMenubar genMenuStruct(String menubarIdent, WfUserSession userSession);

    //***************** Metodos para mantenimiento de menu *******************//
    public PubGuiMenu guardar(PubGuiMenu menu);

    public List<PubGuiMenubar> getMenuBarList();

    public List<PubGuiMenuTipoAcceso> getMenuAccesoList();

    public List<PubGuiMenuRol> getAccesosMenuRol(PubGuiMenu menu);

    public AclRol getRol(Long idRol);

    public List<GeDepartamento> getDepts();

    public List<AclRol> getRolesDepartamento(Long id);

    public Boolean borrarMenu(PubGuiMenu menu);

    public Boolean borrarMenu(PubGuiMenuRol menuRol);

    public PubGuiMenuRol guardarPubGuiMenuRol(PubGuiMenuRol PermisoMenu);

    public PubGuiMenubar buscMenuStruct(String menubarIdent, WfUserSession userSession, String busqueda);
    
}
