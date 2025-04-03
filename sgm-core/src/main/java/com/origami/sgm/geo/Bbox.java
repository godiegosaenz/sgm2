/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.geo;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class Bbox implements Serializable{
    
    private Double xmin;
    private Double xmax;
    private Double ymin;
    private Double ymax;

    public Bbox() {
    }

    public Double getXmin() {
        return xmin;
    }

    public void setXmin(Double xmin) {
        this.xmin = xmin;
    }

    public Double getXmax() {
        return xmax;
    }

    public void setXmax(Double xmax) {
        this.xmax = xmax;
    }

    public Double getYmin() {
        return ymin;
    }

    public void setYmin(Double ymin) {
        this.ymin = ymin;
    }

    public Double getYmax() {
        return ymax;
    }

    public void setYmax(Double ymax) {
        this.ymax = ymax;
    }

    @Override
    public String toString() {
        return "Xmax " + xmax + ", Xmin: " + xmin + ", Ymax: " + ymax + ", Ymin: " + ymin;
    }

    
    
}
