/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.enums;

/**
 *
 * @author dfcalderio
 */
public enum TipoProceso {

    CREAR_PREDIO(1, "CREAR PREDIO NUEVO"),
    FUSIONAR_PREDIOS(2, "FUSIONAR PREDIOS"),
    DIVIDIR_PREDIO(3, "DIVIDIR PREDIO"),
    ACTUALIZAR_DATOS(4, "ACTUALIZAR DATOS"),
    ACTUALIZAR_CONSTRUCCION(5, "ACTUALIZAR CONSTRUCCION"),
    ELIMINAR_PREDIO(4, "ELIMINAR PREDIO");

    private final int code;
    private final String descripcion;

    private TipoProceso(int codigo, String descripcion) {
        this.code = codigo;
        this.descripcion = descripcion;
    }

    public int getCode() {
        return code;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoProceso byCode(int code) {
        for (TipoProceso value : TipoProceso.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
