/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

/**
 *
 * @author Max
 */
public interface LectorArchivos {

    byte[] leerArchivo(String ruta) throws Exception;
}
