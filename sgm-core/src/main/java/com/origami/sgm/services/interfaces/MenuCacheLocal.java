/*
 *  Origami Solutions
 */

package com.origami.sgm.services.interfaces;
import com.origami.sgm.entities.PubGuiMenubar;
import javax.ejb.Local;

/**
 *
 * @author fernando
 */
@Local
public interface MenuCacheLocal {

    public PubGuiMenubar getMenuBar(String identificador);

    public void clearCache(String identificador);
    
}
