/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.service.ordenes;

import com.origami.censocat.service.ordenes.model.ClaveLegacy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Fernando
 */
@Stateless
public class LegacyDom {

    @Inject
    private Entitymanager em;


    public ClaveLegacy getClave(String codigoLegacy) {
        ClaveLegacy claveOut = new ClaveLegacy();
        /**
         * *
         * zona digitos 7-8
         */
        claveOut.setZona(Short.parseShort(codigoLegacy.substring(6, 8)));
        /**
         * sector digitoss 9-10
         */
        claveOut.setSector(Short.parseShort(codigoLegacy.substring(8, 10)));
        /**
         * mz digitos 11-12
         */
        claveOut.setMz(Short.parseShort(codigoLegacy.substring(10, 12)));
        /**
         * solar digitos 13-15
         */
        claveOut.setSolar(Short.parseShort(codigoLegacy.substring(12, 15)));
        /**
         * div digitos 16-18
         */
        claveOut.setDiv(Short.parseShort(codigoLegacy.substring(15, 18)));

        return claveOut;
    }

}
