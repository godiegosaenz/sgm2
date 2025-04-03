/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.CatPredioClasificRural;
import com.origami.sgm.entities.CatPredioCultivo;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioObraInterna;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import util.Faces;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @date 29/08/2016
 */
@FacesComponent(value = "seccionesFicha")
public class SeccionesFicha extends UINamingContainer {

    private static final Logger LOG = Logger.getLogger(SeccionesFicha.class.getName());

    @PostConstruct
    public void init() {

    }

    public String completarCeros(Number n, Integer numCaracteres) {
        try {
            if (n == null) {
                return null;
            }
            return Utils.completarCadenaConCeros(n.toString(), numCaracteres);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Completar con ceros", e);
        }
        return "";
    }

    public String comparar(Object ant, Object act) {
        try {
            if (Objects.equals(ant, act)) {
                return "blanco";
            } else {
                return "diferente";
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Completar con ceros", e);
        }
        return "diferente";
    }

    public String valueByCode(int n, String code) {
        String result = "";
        try {
            switch (n) {
                case 1: {
                    switch (code) {
                        case "U": {
                            result = "Urbano";
                            break;
                        }
                        case "R": {
                            result = "Rural";
                            break;
                        }
                        default: {
                            result = "";
                            break;
                        }
                    }
                }
                case 2: {
                    switch (code) {
                        case "A": {
                            result = "Activo";
                            break;
                        }
                        case "I": {
                            result = "Inactivo";
                            break;
                        }
                        case "H": {
                            result = "Histórico";
                            break;
                        }
                        default: {
                            result = "";
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Completar con ceros", e);
        }
        return result;
    }

    public String buscarObjeto(Object obj, List lista) {
        try {
            if (Utils.isEmpty(lista)) {
                return "blanco";
            }
            for (Object objlista : lista) {
                if (objlista.equals(obj)) {
                    return "blanco";
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Completar con ceros", e);
        }
        return "diferente";
    }

    public Boolean existeObjeto(Object obj, List lista) {
        try {
            for (Object objlista : lista) {
                if (objlista.equals(obj)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Completar con ceros", e);
        }
        return false;
    }

    public void verBloque(CatPredioEdificacion bloque) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        if (bloque.getId() == null) {
            p.add("0");
        } else {
            p.add(bloque.getId().toString());
        }
        params.put("idCatPredioBloq", p);
        p = new ArrayList<>();
        p.add(Boolean.TRUE.toString());
        params.put("ver", p);
        Faces.setSessionBean("bloque", bloque);
        Utils.openDialog("/resources/dialog/edificacionesPredio", params, "520");
    }

    public void verObraInterna(CatPredioObraInterna obraInterna) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        if (obraInterna.getId() == null) {
            p.add("0");
        } else {
            p.add(obraInterna.getId().toString());
        }
        params.put("idCatPredioObraInterna", p);
        p = new ArrayList<>();
        p.add(Boolean.TRUE.toString());
        params.put("ver", p);
        Faces.setSessionBean("obraInterna", obraInterna);
        Utils.openDialog("/resources/dialog/obraInterna", params);
    }

    public void verCultivo(CatPredioCultivo cultivo) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        if (cultivo.getId() == null) {
            p.add("0");
        } else {
            p.add(cultivo.getId().toString());
        }
        params.put("idCatPredioCult", p);
        p = new ArrayList<>();
        p.add(Boolean.TRUE.toString());
        params.put("ver", p);
        Faces.setSessionBean("cultivo", cultivo);
        Utils.openDialog("/resources/dialog/cutivos", params, "520");
    }

    public void verClasificacionRural(CatPredioClasificRural clasificacionRural) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        if (clasificacionRural.getId() == null) {
            p.add("0");
        } else {
            p.add(clasificacionRural.getId().toString());
        }
        params.put("idCatClasiSueloRural", p);
        p = new ArrayList<>();
        p.add(Boolean.TRUE.toString());
        params.put("ver", p);
        Faces.setSessionBean("clasificacionRural", clasificacionRural);
        Utils.openDialog("/resources/dialog/clasificacionSueloRural", params);
    }
}
