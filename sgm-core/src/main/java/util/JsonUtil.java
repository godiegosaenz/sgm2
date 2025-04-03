/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

/**
 *
 * @author CarlosLoorVargas
 */
public class JsonUtil implements Serializable {

    private ObjectMapper mapper;
    private static final long serialVersionUID = 1L;

    public String toJson(Object value) {
        try {
            mapper = new ObjectMapper();
            mapper.disable(Feature.FAIL_ON_EMPTY_BEANS);
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public Object fromJson(String val, Class className) throws IOException {
        mapper = new ObjectMapper();
        mapper.disable(Feature.FAIL_ON_EMPTY_BEANS);
        return mapper.readValue(val, className);
    }

}
