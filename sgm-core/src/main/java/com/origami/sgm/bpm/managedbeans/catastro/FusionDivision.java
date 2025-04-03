/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.origami.sgm.entities.CatPredio;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import util.Faces;
import util.HibernateProxyTypeAdapter;

/**
 *
 * @author Xndy Sxnchez :v
 */
public abstract class FusionDivision {

    public void fusionar(CatPredio predio) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = new Gson();

        builder.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .serializeNulls().setPrettyPrinting();
        gson = builder.create();

        String[] ignoreProperties = {"procesoFusionPrediosCollection", "catSolicitudNormaConstruccionCollection", "pePermisoCollection", "procesoReporteCollection",
            "catEscrituraCollection", "catPredioPropietarioCollection", "catPredioEdificacionCollection"};

        CatPredio clon = new CatPredio();
        BeanUtils.copyProperties(predio, clon);

//        System.out.println("Inicio: " + gson1.toJson(predio));
//        System.out.println("Clonado: " + gson2.toJson(clon));
        String pClon = gson.toJson(clon);

        CatPredio clonTemp = gson.fromJson(pClon, CatPredio.class);
        System.out.println("Antes ClonadoTEMP: " + gson.toJson(clonTemp));

        //clonTemp.setSolar(nuevoCodigo);
        clonTemp.setNumPredio(new BigInteger("1391502612"));
        //manager.saveAll(clonTemp);

        Hibernate.initialize(predio);
        clon = new CatPredio();
        BeanUtils.copyProperties(predio, clon);
        String pClon2 = gson.toJson(clon);
        CatPredio clonTemp2 = gson.fromJson(pClon2, CatPredio.class);

        System.out.println("Despues ClonadoTEMP: " + gson.toJson(clonTemp2));

    }



}
