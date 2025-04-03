/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.origami.sgm.bpm.util.BooleanSerializer;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.HibernateProxyTypeAdapter;

/**
 *
 * @author Angel Navarro
 */
public class JsonUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(JsonUtils.class.getName());

    private transient Gson gson2;
    private transient GsonBuilder builder;

    public String generarJson(Object obj) {
        try {
            builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                    .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .setPrettyPrinting();
            gson2 = builder.create();
            return gson2.toJson(obj);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generar Json.", e);
        }
        return null; 
    }

    public String getElementFromJson(String json, String field) {
        try {
            builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                    .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .setPrettyPrinting();
            gson2 = builder.create();
            JsonObject fromJson = gson2.fromJson(json, JsonObject.class);
            try {
                System.out.println("Get field data json >> " + field);
                return fromJson.get(field).toString();
            } catch (Exception e) {
                return null;
            }
        } catch (JsonSyntaxException e) {
            LOG.log(Level.SEVERE, "Json to Object", e);
        }
        return null;
    }

    public <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setLongSerializationPolicy(LongSerializationPolicy.DEFAULT)
                    .registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                    .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .setPrettyPrinting();
            gson2 = builder.create();
            return gson2.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            LOG.log(Level.SEVERE, "Json to Object", e);
        }
        return null;
    }

    public String generarJsonStaticModel(Object obj) {
        try {
            builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd")
                    .setPrettyPrinting();
            gson2 = builder.create();
            return gson2.toJson(obj);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generar Json.", e);
        }
        return null;
    }

    public <T> T jsonToObjectStaticModel(String json, Class<T> clazz) {
        try {
            builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd")
                    .setPrettyPrinting();
            gson2 = builder.create();
            return gson2.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            LOG.log(Level.SEVERE, "Json to Object", e);
        }
        return null;
    }

}
