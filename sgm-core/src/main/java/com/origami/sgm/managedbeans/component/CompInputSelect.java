/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.CtlgItem;
import java.util.logging.Logger;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

/**
 *
 * @author dfcalderio
 */
@FacesComponent(value = "compInputSelect")
public class CompInputSelect extends UINamingContainer {

    private static final Logger LOG = Logger.getLogger(CompInputSelect.class.getName());

    public Integer compareNull(CtlgItem item) {

        if (item == null) {
            return null;
        } else {
            return item.getOrden();
        }

    }
}
