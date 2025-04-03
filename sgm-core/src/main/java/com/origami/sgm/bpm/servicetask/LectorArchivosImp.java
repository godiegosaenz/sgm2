/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Max
 */
public class LectorArchivosImp implements LectorArchivos {

    @Override
    public byte[] leerArchivo(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        byte[] data = Files.readAllBytes(path);
        return data;
    }
}
