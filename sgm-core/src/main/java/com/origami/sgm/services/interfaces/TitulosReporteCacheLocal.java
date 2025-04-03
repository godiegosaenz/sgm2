/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces;

import com.origami.sgm.entities.PubGuiMenubar;
import javax.ejb.Local;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Joao Sanga
 */
@Local
public interface TitulosReporteCacheLocal {
    
    public TreeNode getTree();

    public void clearCache();
}
