/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.utils;


/**
 *
 * @author Fernando
 */
public abstract class WmsImageUtils {
    
    public static Integer heightCalculate(Bbox bbox, Integer width){
        /**
         *  w     xdim 
         *  ?     ydim
         */
        Double xdim = bbox.getXmax() - bbox.getXmin();
        Double ydim = bbox.getYmax() - bbox.getYmin();
        Double w = width.doubleValue();
        
        Double h = ((w * ydim)/xdim);
        
        return h.intValue();
    }
    
}
