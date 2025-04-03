/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.origami.sgm.entities.database.DatabaseEngine;
import com.origami.sgm.entities.database.SchemasConfig;
import java.lang.reflect.Type;

/**
 *
 * @author dfcalderio
 */
public class BooleanSerializer implements JsonDeserializer<Boolean>, JsonSerializer<Boolean> {

    @Override
    public Boolean deserialize(JsonElement json, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        try {
            boolean number = json.getAsJsonPrimitive().isNumber();

            if (number) {
                int value = json.getAsJsonPrimitive().getAsInt();
                return value != 0;
            } else {
                String value = json.getAsJsonPrimitive().getAsString();
                return value.equals("TRUE") || value.equals("true");
            }

        } catch (ClassCastException e) {
            throw new JsonParseException("Cannot parse json boolean '" + json.toString() + "'", e);
        }
    }

    @Override
    public JsonElement serialize(Boolean bool, Type type, JsonSerializationContext jsc) {
        if (SchemasConfig.DB_ENGINE.equals(DatabaseEngine.ORACLE)) {
            return new JsonPrimitive(bool ? 1 : 0);
        } else {
            return new JsonPrimitive(bool);
        }
    }

}
