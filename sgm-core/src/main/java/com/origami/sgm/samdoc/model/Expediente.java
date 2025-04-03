/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.samdoc.model;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class Expediente implements Serializable{
    
    private String nombreLibro;
    private Integer anioInscripcion;
    private Integer numeroTomo;
    private Integer numeroInscripcion;
    private Integer folioInicial;
    private Integer folioFinal;

    public Expediente() {
    }

    public String getNombreLibro() {
        return nombreLibro;
    }

    public void setNombreLibro(String nombreLibro) {
        this.nombreLibro = nombreLibro;
    }

    public Integer getAnioInscripcion() {
        return anioInscripcion;
    }

    public void setAnioInscripcion(Integer anioInscripcion) {
        this.anioInscripcion = anioInscripcion;
    }

    public Integer getNumeroTomo() {
        return numeroTomo;
    }

    public void setNumeroTomo(Integer numeroTomo) {
        this.numeroTomo = numeroTomo;
    }

    public Integer getNumeroInscripcion() {
        return numeroInscripcion;
    }

    public void setNumeroInscripcion(Integer numeroInscripcion) {
        this.numeroInscripcion = numeroInscripcion;
    }

    public Integer getFolioInicial() {
        return folioInicial;
    }

    public void setFolioInicial(Integer folioInicial) {
        this.folioInicial = folioInicial;
    }

    public Integer getFolioFinal() {
        return folioFinal;
    }

    public void setFolioFinal(Integer folioFinal) {
        this.folioFinal = folioFinal;
    }
    
    
    
}
