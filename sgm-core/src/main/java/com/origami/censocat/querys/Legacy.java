/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.querys;

/**
 *
 * @author CarlosLoorVargas
 */
public class Legacy {
    
    public static String getBloquesPredios = "select e from LegBloquesPredio e where e.bloquesPredioPK.preCodigocatastral = :codigo";
    
}
