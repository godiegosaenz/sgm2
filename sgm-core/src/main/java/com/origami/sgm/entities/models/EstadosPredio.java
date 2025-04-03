/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

/**
 *
 * @author Angel Navarro
 */
public class EstadosPredio {

    /**
     * PREDIO ACTIVOS
     */
    public static String ACTIVO = "A";

    /**
     * EL PREDIO PASA A ESTE ESTADO CUANDO SE REALIZA UN PROCESO SOBRE EL PREDIO
     * BIEN SEA POR DIVISION, FUSION O PROPIEDAD HORIZONTAL
     */
    public static String HISTORICO = "H";

    /**
     * PREDIOS INACTIVOS POR ALGUN MOTIVOS EN ESPECIAL.
     */
    public static String INACTIVO = "I";

    /**
     * PREDIO EN PROCESO DE DIVISION, FUSION Y DIVISION.
     */
    public static String PENDIENTE = "P";

    /**
     * PREDIO EN PROCESO DE EDICION TEMPORAL.
     */
    public static String TEMPORAL = "X";
}
