/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;

/**
 *
 * @author Angel Navarro
 */
public class CombinacionPH implements Serializable{
    protected short phv;
    protected short phh;

    public short getPhv() {
        return phv;
    }

    public void setPhv(short phv) {
        this.phv = phv;
    }

    public short getPhh() {
        return phh;
    }

    public void setPhh(short phh) {
        this.phh = phh;
    }
    
}
