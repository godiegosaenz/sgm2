/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author CarlosLoorVargas
 */
@SuppressWarnings("serial")
public class GroovyUtil implements Serializable {

    private String gScript;
    private GroovyClassLoader loader = null;
    private GroovyObject gobj;
    private Class groovy;
    private static final long serialVersionUID = 1L;

    public GroovyUtil() {
        this.init();
    }

    public GroovyUtil(String gScript) {
        this.gScript = gScript;
        this.init();
    }

    private void init() {
        try {
            loader = new GroovyClassLoader();
            groovy = loader.parseClass(gScript);
            if (groovy != null) {
                gobj = (GroovyObject) groovy.newInstance();
            }
        } catch (CompilationFailedException | InstantiationException | IllegalAccessException e) {
            Logger.getLogger(GroovyUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Object getExpression(String method, Object[] parameters) {
        try {
            if (gobj != null) {
                return gobj.invokeMethod(method, parameters);
            }
        } catch (CompilationFailedException e) {
            Logger.getLogger(GroovyUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public Object getProperty(String property) {
        try {
            if (gobj != null) {
                return gobj.getProperty(property);
            }
        } catch (CompilationFailedException e) {
            Logger.getLogger(GroovyUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public void setProperty(String property, Object value) {
        try {
            if (gobj != null) {
                gobj.setProperty(property, value);
            }
        } catch (CompilationFailedException e) {
            Logger.getLogger(GroovyUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
