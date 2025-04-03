/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.ServletSession;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author dfcalderio
 */
@Named(value = "fichaDefaultView")
@ApplicationScoped
public class FichaDefaultSeccion implements Serializable {

    protected String seccion1;
    protected String seccion1_1;
    protected String seccion1_2;
    protected String seccion1_3;
    protected String seccion1_4;
    protected String seccion1_5;
    protected String seccion2;
    protected String seccion3;
    protected String seccion4;
    protected String seccion5;
    protected String seccion6;
    protected String seccion7;
    protected String seccion8;
    protected String seccion9;
    protected String seccion10;

    protected String seccion1Title;
    protected String seccion1_1Title;
    protected String seccion1_2Title;
    protected String seccion1_3Title;
    protected String seccion1_4Title;
    protected String seccion1_5Title;
    protected String seccion2Title;
    protected String seccion3Title;
    protected String seccion4Title;
    protected String seccion5Title;
    protected String seccion6Title;
    protected String seccion7Title;
    protected String seccion8Title;
    protected String seccion9Title;
    protected String seccion10Title;

    protected Boolean seccion1Render;
    protected Boolean seccionFichaReg = Boolean.TRUE;
    protected Boolean seccion1_1Render;
    protected Boolean seccion1_2Render;
    protected Boolean seccion1_3Render;
    protected Boolean seccion1_4Render;
    protected Boolean seccion1_5Render;
    protected Boolean seccion2Render;
    protected Boolean seccion3Render;
    protected Boolean seccion4Render;
    protected Boolean seccion5Render;
    protected Boolean seccion6Render;
    protected Boolean seccion7Render;
    protected Boolean seccion8Render;
    protected Boolean seccion9Render;
    protected Boolean seccion10Render;
    
    protected String idPredio;
    protected String edit;
    @Inject
    protected ServletSession ss;

    @PostConstruct
    public void init() {
        defaultValues();
    }

    public void defaultValues() {

        seccion1 = "_seccion01.xhtml";
        seccion1Title = "1. Identificación y ubicación predial";
        seccion1Render = Boolean.TRUE;
        seccion1_1 = "_seccion01-1.xhtml";
        seccion1_1Title = "Código Catastral";
        seccion1_1Render = Boolean.TRUE;
        seccion1_2 = "_seccion01-2.xhtml";
        seccion1_2Title = "Localización del predio";
        seccion1_2Render = Boolean.TRUE;
        seccion1_3 = "_seccion01-3.xhtml";
        seccion1_3Title = "Áreas y Avalúos";
        seccion1_3Render = Boolean.TRUE;
        seccion1_4 = "_seccion01-4.xhtml";
        seccion1_4Title = "Código catastral predio Raíz";
        seccion1_4Render = Boolean.TRUE;
        seccion1_5 = "_seccion01-5.xhtml";
        seccion1_5Title = "Información Urbanística";
        seccion1_5Render = Boolean.FALSE;
        seccion2 = "_seccion02.xhtml";
        seccion2Title = "2. Identificación legal";
        seccion2Render = Boolean.TRUE;
        seccion3 = "_seccion03.xhtml";
        seccion3Title = "3. Caracterización del lote";
        seccion3Render = Boolean.TRUE;
        seccion4 = "_seccion04.xhtml";
        seccion4Title = "4. Infraestructura, Instalaciones y Servicios";
        seccion4Render = Boolean.TRUE;
        seccion5 = "_seccion05.xhtml";
        seccion5Title = "5. Escrituras";
        seccion5Render = Boolean.TRUE;
        seccion6 = "_seccion06.xhtml";
        seccion6Title = "6. Caracterización y elementos constructivos de la edificación";
        seccion6Render = Boolean.TRUE;
        seccion7 = "_seccion07.xhtml";
        seccion7Title = "7. Gráfico y linderos del predio";
        seccion7Render = Boolean.TRUE;
        seccion8 = "_seccion08.xhtml";
        seccion8Title = "8. Responsables";
        seccion8Render = Boolean.TRUE;
        seccion9 = "_seccion09.xhtml";
        seccion9Title = "9. Adicionales";
        seccion9Render = Boolean.TRUE;
        seccion10 = "_seccion10.xhtml";
        seccion10Title = "10. Vivienda Censal";
        seccion10Render = Boolean.TRUE;

    }

    //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
    public String getSeccion1() {
        return seccion1;
    }

    public void setSeccion1(String seccion1) {
        this.seccion1 = seccion1;
    }

    public String getSeccion1_1() {
        return seccion1_1;
    }

    public void setSeccion1_1(String seccion1_1) {
        this.seccion1_1 = seccion1_1;
    }

    public String getSeccion1_2() {
        return seccion1_2;
    }

    public void setSeccion1_2(String seccion1_2) {
        this.seccion1_2 = seccion1_2;
    }

    public String getSeccion1_3() {
        return seccion1_3;
    }

    public void setSeccion1_3(String seccion1_3) {
        this.seccion1_3 = seccion1_3;
    }

    public String getSeccion1_4() {
        return seccion1_4;
    }

    public void setSeccion1_4(String seccion1_4) {
        this.seccion1_4 = seccion1_4;
    }

    public String getSeccion1_5() {
        return seccion1_5;
    }

    public void setSeccion1_5(String seccion1_5) {
        this.seccion1_5 = seccion1_5;
    }

    public String getSeccion2() {
        return seccion2;
    }

    public void setSeccion2(String seccion2) {
        this.seccion2 = seccion2;
    }

    public String getSeccion3() {
        return seccion3;
    }

    public void setSeccion3(String seccion3) {
        this.seccion3 = seccion3;
    }

    public String getSeccion4() {
        return seccion4;
    }

    public void setSeccion4(String seccion4) {
        this.seccion4 = seccion4;
    }

    public String getSeccion5() {
        return seccion5;
    }

    public void setSeccion5(String seccion5) {
        this.seccion5 = seccion5;
    }

    public String getSeccion6() {
        return seccion6;
    }

    public void setSeccion6(String seccion6) {
        this.seccion6 = seccion6;
    }

    public String getSeccion7() {
        return seccion7;
    }

    public void setSeccion7(String seccion7) {
        this.seccion7 = seccion7;
    }

    public String getSeccion8() {
        return seccion8;
    }

    public void setSeccion8(String seccion8) {
        this.seccion8 = seccion8;
    }

    public String getSeccion9() {
        return seccion9;
    }

    public void setSeccion9(String seccion9) {
        this.seccion9 = seccion9;
    }

    public String getSeccion10() {
        return seccion10;
    }

    public void setSeccion10(String seccion10) {
        this.seccion10 = seccion10;
    }

    public String getSeccion1Title() {
        return seccion1Title;
    }

    public void setSeccion1Title(String seccion1Title) {
        this.seccion1Title = seccion1Title;
    }

    public String getSeccion1_1Title() {
        return seccion1_1Title;
    }

    public void setSeccion1_1Title(String seccion1_1Title) {
        this.seccion1_1Title = seccion1_1Title;
    }

    public String getSeccion1_2Title() {
        return seccion1_2Title;
    }

    public void setSeccion1_2Title(String seccion1_2Title) {
        this.seccion1_2Title = seccion1_2Title;
    }

    public String getSeccion1_3Title() {
        return seccion1_3Title;
    }

    public void setSeccion1_3Title(String seccion1_3Title) {
        this.seccion1_3Title = seccion1_3Title;
    }

    public String getSeccion1_4Title() {
        return seccion1_4Title;
    }

    public void setSeccion1_4Title(String seccion1_4Title) {
        this.seccion1_4Title = seccion1_4Title;
    }

    public String getSeccion1_5Title() {
        return seccion1_5Title;
    }

    public void setSeccion1_5Title(String seccion1_5Title) {
        this.seccion1_5Title = seccion1_5Title;
    }

    public String getSeccion2Title() {
        return seccion2Title;
    }

    public void setSeccion2Title(String seccion2Title) {
        this.seccion2Title = seccion2Title;
    }

    public String getSeccion3Title() {
        return seccion3Title;
    }

    public void setSeccion3Title(String seccion3Title) {
        this.seccion3Title = seccion3Title;
    }

    public String getSeccion4Title() {
        return seccion4Title;
    }

    public void setSeccion4Title(String seccion4Title) {
        this.seccion4Title = seccion4Title;
    }

    public String getSeccion5Title() {
        return seccion5Title;
    }

    public void setSeccion5Title(String seccion5Title) {
        this.seccion5Title = seccion5Title;
    }

    public String getSeccion6Title() {
        return seccion6Title;
    }

    public void setSeccion6Title(String seccion6Title) {
        this.seccion6Title = seccion6Title;
    }

    public String getSeccion7Title() {
        return seccion7Title;
    }

    public void setSeccion7Title(String seccion7Title) {
        this.seccion7Title = seccion7Title;
    }

    public String getSeccion8Title() {
        return seccion8Title;
    }

    public void setSeccion8Title(String seccion8Title) {
        this.seccion8Title = seccion8Title;
    }

    public String getSeccion9Title() {
        return seccion9Title;
    }

    public void setSeccion9Title(String seccion9Title) {
        this.seccion9Title = seccion9Title;
    }

    public String getSeccion10Title() {
        return seccion10Title;
    }

    public void setSeccion10Title(String seccion10Title) {
        this.seccion10Title = seccion10Title;
    }

    public Boolean getSeccion1Render() {
        return seccion1Render;
    }

    public void setSeccion1Render(Boolean seccion1Render) {
        this.seccion1Render = seccion1Render;
    }

    public Boolean getSeccion1_1Render() {
        return seccion1_1Render;
    }

    public void setSeccion1_1Render(Boolean seccion1_1Render) {
        this.seccion1_1Render = seccion1_1Render;
    }

    public Boolean getSeccion1_2Render() {
        return seccion1_2Render;
    }

    public void setSeccion1_2Render(Boolean seccion1_2Render) {
        this.seccion1_2Render = seccion1_2Render;
    }

    public Boolean getSeccion1_3Render() {
        return seccion1_3Render;
    }

    public void setSeccion1_3Render(Boolean seccion1_3Render) {
        this.seccion1_3Render = seccion1_3Render;
    }

    public Boolean getSeccion1_4Render() {
        return seccion1_4Render;
    }

    public void setSeccion1_4Render(Boolean seccion1_4Render) {
        this.seccion1_4Render = seccion1_4Render;
    }

    public Boolean getSeccion1_5Render() {
        return seccion1_5Render;
    }

    public void setSeccion1_5Render(Boolean seccion1_5Render) {
        this.seccion1_5Render = seccion1_5Render;
    }

    public Boolean getSeccion2Render() {
        return seccion2Render;
    }

    public void setSeccion2Render(Boolean seccion2Render) {
        this.seccion2Render = seccion2Render;
    }

    public Boolean getSeccion3Render() {
        return seccion3Render;
    }

    public void setSeccion3Render(Boolean seccion3Render) {
        this.seccion3Render = seccion3Render;
    }

    public Boolean getSeccion4Render() {
        return seccion4Render;
    }

    public void setSeccion4Render(Boolean seccion4Render) {
        this.seccion4Render = seccion4Render;
    }

    public Boolean getSeccion5Render() {
        return seccion5Render;
    }

    public void setSeccion5Render(Boolean seccion5Render) {
        this.seccion5Render = seccion5Render;
    }

    public Boolean getSeccion6Render() {
        return seccion6Render;
    }

    public void setSeccion6Render(Boolean seccion6Render) {
        this.seccion6Render = seccion6Render;
    }

    public Boolean getSeccion7Render() {
        return seccion7Render;
    }

    public void setSeccion7Render(Boolean seccion7Render) {
        this.seccion7Render = seccion7Render;
    }

    public Boolean getSeccion8Render() {
        return seccion8Render;
    }

    public void setSeccion8Render(Boolean seccion8Render) {
        this.seccion8Render = seccion8Render;
    }

    public Boolean getSeccion9Render() {
        return seccion9Render;
    }

    public void setSeccion9Render(Boolean seccion9Render) {
        this.seccion9Render = seccion9Render;
    }

    public Boolean getSeccion10Render() {
        return seccion10Render;
    }

    public void setSeccion10Render(Boolean seccion10Render) {
        this.seccion10Render = seccion10Render;
    }
//</editor-fold>

    public Boolean getSeccionFichaReg() {
        return seccionFichaReg;
    }

    public void setSeccionFichaReg(Boolean seccionFichaReg) {
        this.seccionFichaReg = seccionFichaReg;
    }

}
