/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful;

/**
 *
 * @author Angel Navarro
 */
public class EstadoMovil {

    /**
     * ESTADO PENDIENTE CUANDO SE ENVIA LA ORDEN A LA TABLET.
     */
    public static String PENDIENTE = "P";

    /**
     * ESTADO EN QUESE RECIBE LA ORDEN CUANDO ESTEN TODOS PROCESADOS EN LA TABLET
     */
    public static String CENSADA = "C";

    /**
     * ESTADO PARA INDICAR EL QUE LA ORDEN DE TRABAJO Y CADA REGISTRO DEL DETALLE
     * DE LA ORDEN ESTA FINALIZADA, SOLO CAMBIA A FINALIZADA LA ORDEN CUANDO 
     * TODOS REGISTROS DEL DETALLE ACTIVOS ESTEN EN 'P'
     */
    public static String FINALIZADO = "F";
    /**
     * ESTADO DEL PREDIO CUANDO ES RECHAZADA LA ORDEN POR ALGUN MOTIVO
     * SE CLONA EL REGISTRO EN ESTADO 'P'
     */
    public static String RECHAZADA = "R";
}
